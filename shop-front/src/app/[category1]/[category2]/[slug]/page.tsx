"use client";

import { useState, useEffect, useCallback, useMemo } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button, Container, Form, Modal } from "react-bootstrap";
import api from "@/lib/axios";

const API_ROOT = "http://localhost:9999";

type MenuNode = {
  id: number;
  name: string;
  children?: MenuNode[];
};

type UserRole = "consumer" | "developer";

type ProductSize = {
  size: number;
  stock: number;
};

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();
  const { slug } = params as { slug: string };

  const [product, setProduct] = useState<any>(null);
  const [menuTree, setMenuTree] = useState<MenuNode[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | "">("");

  const [selectedSize, setSelectedSize] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  const [isLogin, setIsLogin] = useState<boolean | null>(null);
  const [role, setRole] = useState<UserRole>("consumer");

  // ✅ 장바구니 담기 모달 상태
  const [showCartModal, setShowCartModal] = useState(false);
  const [cartSize, setCartSize] = useState<number | null>(null);
  const [cartQty, setCartQty] = useState<number>(1);

  const productSizes: ProductSize[] = useMemo(
    () => product?.sizes ?? [],
    [product]
  );

  const getStockBySize = useCallback(
    (size: number | null) => {
      if (!size) return 0;
      return productSizes.find((s) => s.size === size)?.stock ?? 0;
    },
    [productSizes]
  );

  const fetchMenus = useCallback(async () => {
    try {
      const res = await api.get("/api/nav-menus/tree");
      setMenuTree(res.data);
    } catch (err) {
      console.error(err);
    }
  }, []);

  const fetchProductDetails = useCallback(async () => {
    try {
      // ✅ slug 가드 (초기 렌더/오타로 undefined면 호출 자체를 막음)
      if (!slug || slug === "undefined") return;

      const safeSlug = encodeURIComponent(String(slug));
      const res = await api.get(`/api/products/slug/${safeSlug}`);

      const data = res.data;

      setProduct(data);

      // 기본 선택 사이즈
      if (data?.sizes && data.sizes.length > 0) {
        setSelectedSize(data.sizes[0].size);
      } else {
        setSelectedSize(null);
      }
    } catch (err) {
      console.error(
        "상품 상세 조회 실패 URL:",
        `${API_ROOT}/api/products/slug/${slug}`,
        err
      );
    }
  }, [slug]);

  const fetchUserInfo = useCallback(async (): Promise<boolean> => {
    try {
      const res = await api.get("/api/auth/me", {
        headers: { Accept: "application/json" },
      });
      const data = res.data;

      setIsLogin(true);
      setRole((data?.role as UserRole) ?? "consumer");

      localStorage.setItem("isLogin", JSON.stringify(true));
      localStorage.setItem(
        "userRole",
        JSON.stringify((data?.role as UserRole) ?? "consumer")
      );
      return true;
    } catch (err) {
      console.warn(err);

      setIsLogin(false);
      setRole("consumer");

      localStorage.setItem("isLogin", JSON.stringify(false));
      localStorage.setItem("userRole", JSON.stringify("consumer"));
      return false;
    }
  }, []);

  useEffect(() => {
    if (!slug || slug === "undefined") return;

    setLoading(true);
    Promise.all([fetchProductDetails(), fetchMenus(), fetchUserInfo()]).finally(
      () => setLoading(false)
    );
  }, [slug, fetchProductDetails, fetchMenus, fetchUserInfo]);

  const ensureLogin = useCallback(async (): Promise<boolean> => {
    if (isLogin === true) return true;
    if (isLogin === false) return false;
    return await fetchUserInfo();
  }, [isLogin, fetchUserInfo]);

  // ✅ "장바구니에 담기" 클릭 시: 바로 저장 X, 모달 열기
  const openCartModal = async () => {
    if (!product) return;

    const ok = await ensureLogin();
    if (!ok) {
      alert("로그인이 필요합니다");
      router.push("/login");
      return;
    }

    if (role === "developer") {
      alert("개발자는 장바구니를 이용할 수 없습니다.");
      return;
    }

    // 상세에서 선택한 사이즈를 모달 기본값으로
    const baseSize = selectedSize ?? (productSizes[0]?.size ?? null);
    if (!baseSize) {
      alert("사이즈 정보가 없습니다.");
      return;
    }

    // 품절이면 첫 재고 있는 사이즈로 자동 선택
    const baseStock = getStockBySize(baseSize);
    const fallbackSize =
      baseStock > 0
        ? baseSize
        : productSizes.find((s) => s.stock > 0)?.size ?? null;

    if (!fallbackSize) {
      alert("모든 사이즈가 품절입니다.");
      return;
    }

    setCartSize(fallbackSize);
    setCartQty(1);
    setShowCartModal(true);
  };

  // ✅ 모달에서 최종 장바구니 저장 + ✅ 저장 후 /cart 이동
  const confirmAddToCart = () => {
    if (!product) return;
    if (!cartSize) return alert("사이즈를 선택하세요.");

    const stock = getStockBySize(cartSize);
    if (stock <= 0) return alert("선택한 사이즈는 품절입니다.");
    if (cartQty < 1) return alert("수량은 1 이상이어야 합니다.");
    if (cartQty > stock) return alert(`재고가 부족합니다. (최대 ${stock}개)`);

    const cart = JSON.parse(localStorage.getItem("cart") || "[]");

    const exists = cart.find(
      (item: any) => item.id === product.id && item.selectedSize === cartSize
    );
    if (exists) {
      alert("이미 장바구니에 추가된 상품입니다. (같은 사이즈)");
      return;
    }

    cart.push({ ...product, selectedSize: cartSize, quantity: cartQty });
    localStorage.setItem("cart", JSON.stringify(cart));

    setShowCartModal(false);

    // ✅ 담고 나서 장바구니로 이동
    router.push("/cart");
  };

  const handleCheckout = async () => {
    const ok = await ensureLogin();
    if (!ok) {
      alert("로그인이 필요합니다");
      router.push("/login");
      return;
    }
    router.push("/checkout");
  };

  const handleDirectOrder = async () => {
    const ok = await ensureLogin();
    if (!ok) {
      alert("로그인이 필요합니다");
      router.push("/login");
      return;
    }
    if (!selectedSize) {
      alert("주문하려면 사이즈를 선택하세요.");
      return;
    }
    router.push(`/order?productId=${product.id}&size=${selectedSize}`);
  };

  if (!slug) return <div>잘못된 접근입니다.</div>;
  if (loading) return <div>로딩 중...</div>;
  if (!product) return <div>상품 정보를 불러오는 중입니다...</div>;

  const disableActions = isLogin === null;

  const modalStock = getStockBySize(cartSize);
  const maxQty = modalStock > 0 ? modalStock : 1;

  return (
    <Container>
      <h1>상품 상세</h1>

      {role === "developer" && (
        <div className="mb-3 mt-3">
          <Form.Label>카테고리 선택 (3차 메뉴)</Form.Label>
          <Form.Select
            value={selectedCategoryId}
            onChange={(e) =>
              setSelectedCategoryId(
                e.target.value === "" ? "" : Number(e.target.value)
              )
            }
          >
            <option value="">카테고리 선택</option>
            {menuTree.map((m1) =>
              (m1.children ?? []).map((m2) =>
                (m2.children ?? []).map((m3) => (
                  <option key={m3.id} value={m3.id}>
                    {m1.name} &gt; {m2.name} &gt; {m3.name}
                  </option>
                ))
              )
            )}
          </Form.Select>
        </div>
      )}

      <div className="d-flex flex-column align-items-center mt-3">
        <img
          src={`${API_ROOT}${product.imageUrl}`}
          alt={product.title}
          style={{ width: "100%", height: 300, objectFit: "cover" }}
        />

        <h3 className="mt-3">{product.title}</h3>
        <p>{product.desc}</p>
        <p>
          <strong>{product.price.toLocaleString()}원</strong>
        </p>

        {/* 사이즈 선택 (상세에서) */}
        {productSizes?.length > 0 && (
          <div className="d-flex gap-2 mb-3">
            {productSizes.map((s) => (
              <Button
                key={s.size}
                variant={
                  selectedSize === s.size
                    ? "primary"
                    : s.stock === 0
                    ? "secondary"
                    : "outline-primary"
                }
                disabled={s.stock === 0}
                onClick={() => setSelectedSize(s.size)}
              >
                {s.size} {s.stock === 0 && "(품절)"}
              </Button>
            ))}
          </div>
        )}

        {/* 버튼 */}
        {role !== "developer" && (
          <div className="d-flex gap-2 mt-3">
            <Button
              variant="primary"
              onClick={openCartModal}
              disabled={disableActions}
            >
              장바구니에 담기
            </Button>

            <Button
              variant="success"
              onClick={handleCheckout}
              disabled={disableActions}
            >
              결제하기
            </Button>

            <Button
              variant="warning"
              onClick={handleDirectOrder}
              disabled={disableActions}
            >
              바로 주문
            </Button>
          </div>
        )}

        {/* 개발자 버튼 */}
        {role === "developer" && (
          <div className="d-flex gap-2 mt-3">
            <Button
              variant="warning"
              onClick={() => router.push(`/products/edit/${product.id}`)}
            >
              상품 수정
            </Button>
            <Button
              variant="danger"
              onClick={() => {
                if (confirm("정말 삭제하시겠습니까?")) {
                  // 삭제 API 연결
                }
              }}
            >
              상품 삭제
            </Button>
          </div>
        )}
      </div>

      {/* ✅ 장바구니 담기 모달 */}
      <Modal show={showCartModal} onHide={() => setShowCartModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>장바구니 담기</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <div style={{ fontWeight: 600, marginBottom: 8 }}>{product.title}</div>
          <div style={{ marginBottom: 12 }}>
            가격: <b>{product.price.toLocaleString()}원</b>
          </div>

          <Form.Group className="mb-3">
            <Form.Label>사이즈</Form.Label>
            <Form.Select
              value={cartSize ?? ""}
              onChange={(e) => {
                const v = e.target.value === "" ? null : Number(e.target.value);
                setCartSize(v);
                setCartQty(1);
              }}
            >
              {productSizes
                .slice()
                .sort((a, b) => a.size - b.size)
                .map((s) => (
                  <option key={s.size} value={s.size} disabled={s.stock === 0}>
                    {s.size} {s.stock === 0 ? "(품절)" : `(재고 ${s.stock})`}
                  </option>
                ))}
            </Form.Select>
          </Form.Group>

          <Form.Group className="mb-2">
            <Form.Label>수량</Form.Label>
            <div className="d-flex align-items-center gap-2">
              <Button
                variant="outline-secondary"
                onClick={() => setCartQty((q) => Math.max(1, q - 1))}
                disabled={modalStock <= 0}
              >
                -
              </Button>

              <Form.Control
                style={{ width: 90, textAlign: "center" }}
                value={cartQty}
                onChange={(e) => {
                  const n = Number(e.target.value);
                  if (!Number.isFinite(n)) return;
                  setCartQty(Math.min(Math.max(1, n), maxQty));
                }}
                inputMode="numeric"
                disabled={modalStock <= 0}
              />

              <Button
                variant="outline-secondary"
                onClick={() => setCartQty((q) => Math.min(maxQty, q + 1))}
                disabled={modalStock <= 0}
              >
                +
              </Button>

              <div style={{ marginLeft: 8, color: "#666" }}>최대 {maxQty}개</div>
            </div>
          </Form.Group>

          <div style={{ marginTop: 12 }}>
            선택 재고: <b>{modalStock}</b>
          </div>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCartModal(false)}>
            취소
          </Button>
          <Button
            variant="primary"
            onClick={confirmAddToCart}
            disabled={!cartSize || modalStock <= 0}
          >
            담기
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}