"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button, Container, Form } from "react-bootstrap";

const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

type MenuNode = {
  id: number;
  name: string;
  path?: string | null;
  children?: MenuNode[];
};

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();

  // category1, category2, slug 모두 받음
  const { category1, category2, slug } = params as {
    category1: string;
    category2: string;
    slug: string;
  };

  const [product, setProduct] = useState<any>(null);
  const [isLogin, setIsLogin] = useState<boolean | null>(null);
  const [userRole, setUserRole] = useState<"consumer" | "developer" | null>(null);

  const [menuTree, setMenuTree] = useState<MenuNode[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | "">("");

  // 메뉴 불러오기
  const fetchMenus = async () => {
    try {
      const res = await fetch(`${API_BASE}/nav-menus/tree`);
      if (!res.ok) throw new Error("메뉴 조회 실패");
      const data = await res.json();
      setMenuTree(data);
    } catch (err) {
      console.error(err);
    }
  };

  // 로그인 체크
  const checkUserRole = async () => {
    try {
      const res = await fetch(`${API_BASE}/auth/me`, {
        credentials: "include",
      });
      if (!res.ok) throw new Error("로그인 체크 실패");
      const data = await res.json();
      setIsLogin(true);
      setUserRole(data.role);
      localStorage.setItem("isLogin", JSON.stringify(true));
      localStorage.setItem("userRole", JSON.stringify(data.role));
    } catch {
      setIsLogin(false);
      setUserRole("consumer");
    }
  };

  // slug 기반 상품 조회
  const fetchProductDetails = async () => {
    try {
      const res = await fetch(`${API_BASE}/products/slug/${slug}`);
      if (!res.ok) throw new Error("상품 정보 불러오기 실패");
      const data = await res.json();
      setProduct(data);
    } catch (err) {
      console.error(err);
    }
  };

  // 장바구니 추가 함수
  const handleAddToCart = () => {
    if (!product) return;
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    if (cart.find((item: any) => item.id === product.id)) {
      alert("이미 장바구니에 추가된 상품입니다.");
      return;
    }
    cart.push(product);
    localStorage.setItem("cart", JSON.stringify(cart));
    alert("장바구니에 상품을 추가했습니다.");
  };

  // 결제 함수
  const handleCheckout = () => {
    if (isLogin) router.push("/checkout");
    else {
      alert("로그인이 필요합니다");
      router.push("/login");
    }
  };

  useEffect(() => {
    if (!slug) return;
    fetchProductDetails();
    fetchMenus();

    const storedIsLogin = localStorage.getItem("isLogin");
    const storedUserRole = localStorage.getItem("userRole");
    if (storedIsLogin && storedUserRole) {
      setIsLogin(JSON.parse(storedIsLogin));
      setUserRole(JSON.parse(storedUserRole));
    } else checkUserRole();
  }, [slug]);

  if (!slug) return <div>잘못된 접근입니다.</div>;
  if (isLogin === null || userRole === null) return <div>로딩 중...</div>;
  if (!product) return <div>상품 정보를 불러오는 중입니다...</div>;

  return (
    <Container>
      <h1>상품 상세</h1>

      {/* 개발자 카테고리 선택 */}
      {userRole === "developer" && (
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

        {isLogin && (
          <div className="d-flex gap-2 mt-3">
            <Button variant="primary" onClick={handleAddToCart}>
              장바구니에 담기
            </Button>
            <Button variant="success" onClick={handleCheckout}>
              결제하기
            </Button>
          </div>
        )}

        {userRole === "developer" && (
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
    </Container>
  );
}