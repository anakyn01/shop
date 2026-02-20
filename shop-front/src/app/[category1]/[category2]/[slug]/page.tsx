"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button, Container, Form } from "react-bootstrap";

const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

type MenuNode = {
  id: number;
  name: string;
  children?: MenuNode[];
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
  const [role, setRole] = useState<"consumer" | "developer">("consumer");

  // 메뉴 불러오기
  const fetchMenus = async () => {
    try {
      const res = await fetch(`${API_BASE}/nav-menus/tree`);
      if (!res.ok) throw new Error("메뉴 조회 실패");
      setMenuTree(await res.json());
    } catch (err) {
      console.error(err);
    }
  };

  // 상품 정보 불러오기
  const fetchProductDetails = async () => {
    try {
      const res = await fetch(`${API_BASE}/products/slug/${slug}`);
      if (!res.ok) throw new Error("상품 정보 불러오기 실패");
      const data = await res.json();
      setProduct(data);
      if (data.sizes && data.sizes.length > 0) setSelectedSize(data.sizes[0].size);
    } catch (err) {
      console.error(err);
    }
  };

  // 사용자 로그인 체크
  const fetchUserInfo = async () => {
    try {
      const res = await fetch(`${API_BASE}/auth/me`, { credentials: "include" });
      if (!res.ok) throw new Error("로그인 체크 실패");
      const data = await res.json();
      localStorage.setItem("isLogin", "true");
      localStorage.setItem("userRole", data.role);
      setRole(data.role);
    } catch {
      localStorage.setItem("isLogin", "false");
      localStorage.setItem("userRole", "consumer");
      setRole("consumer");
    }
  };

  useEffect(() => {
    if (!slug) return;
    setLoading(true);
    Promise.all([fetchProductDetails(), fetchMenus(), fetchUserInfo()]).finally(() =>
      setLoading(false)
    );
  }, [slug]);

  // 로그인 체크
  const isLoggedIn = (): boolean => {
    return JSON.parse(localStorage.getItem("isLogin") || "false");
  };

  // 장바구니 추가
  const handleAddToCart = () => {
    if (!product) return;

    if (!isLoggedIn()) {
      alert("로그인이 필요합니다");
      router.push("/login");
      return;
    }

    if (role === "developer") {
      alert("개발자는 장바구니를 이용할 수 없습니다.");
      return;
    }

    if (!selectedSize) {
      alert("장바구니에 담으려면 사이즈를 선택하세요.");
      return;
    }

    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    const exists = cart.find(
      (item: any) => item.id === product.id && item.selectedSize === selectedSize
    );
    if (exists) {
      alert("이미 장바구니에 추가된 상품입니다.");
      return;
    }

    const sizeStock = product.sizes.find((s: any) => s.size === selectedSize);
    if (sizeStock?.stock === 0) {
      alert("선택한 사이즈는 재고가 없습니다.");
      return;
    }

    cart.push({ ...product, selectedSize, quantity: 1 });
    localStorage.setItem("cart", JSON.stringify(cart));
    alert(`장바구니에 추가되었습니다. (사이즈: ${selectedSize})`);
  };

  // 결제
  const handleCheckout = () => {
    if (!isLoggedIn()) {
      alert("로그인이 필요합니다");
      router.push("/login");
      return;
    }
    router.push("/checkout");
  };

  // 바로 주문
  const handleDirectOrder = () => {
    if (!isLoggedIn()) {
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

  return (
    <Container>
      <h1>상품 상세</h1>

      {role === "developer" && (
        <div className="mb-3 mt-3">
          <Form.Label>카테고리 선택 (3차 메뉴)</Form.Label>
          <Form.Select
            value={selectedCategoryId}
            onChange={(e) =>
              setSelectedCategoryId(e.target.value === "" ? "" : Number(e.target.value))
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

        {/* 사이즈 선택 */}
        {product.sizes?.length > 0 && (
          <div className="d-flex gap-2 mb-3">
            {product.sizes.map((s: any) => (
              <Button
                key={s.size}
                variant={selectedSize === s.size ? "primary" : s.stock === 0 ? "secondary" : "outline-primary"}
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
            <Button variant="primary" onClick={handleAddToCart}>
              장바구니에 담기
            </Button>
            <Button variant="success" onClick={handleCheckout}>
              결제하기
            </Button>
            <Button variant="warning" onClick={handleDirectOrder}>
              바로 주문
            </Button>
          </div>
        )}

        {/* 개발자 버튼 */}
        {role === "developer" && (
          <div className="d-flex gap-2 mt-3">
            <Button variant="warning" onClick={() => router.push(`/products/edit/${product.id}`)}>
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
    </Container>
  );
}