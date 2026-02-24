"use client";

import { useEffect, useState } from "react";
import { Container, Button, Carousel } from "react-bootstrap";
import { useRouter } from "next/navigation";

import Header from "@/include/Header";
import ProductModal from "@/modal/ProductModal";
import SmallBanner from "./common/SmallBanner";
import { BackgroundVideo, TextBanner,TextBanner2, VideoWrap } from "@/styled/Component.styles";
import { WhiteBtn, BlackBtn, BtnWrap } from "@/styled/Button.styles";

const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

type Product = {
  id: number;
  title: string;
  desc: string;
  price: number;
  primaryCategory?: number;
  secondaryCategory?: number;
  imageUrl?: string;
};

export default function Home() {
  const router = useRouter();

  const [products, setProducts] = useState<Product[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState<"create" | "edit" | "view">("create");
  const [currentProductId, setCurrentProductId] = useState<number | undefined>(undefined);
  const [isLogin, setIsLogin] = useState<boolean | null>(null);

  // 상품 리스트 조회
  const fetchProducts = async () => {
    try {
      const res = await fetch(`${API_BASE}/products`, { cache: "no-store" });
      if (!res.ok) throw new Error("상품 리스트 불러오기 실패");
      const data = await res.json();
      console.log(data); // 상품 데이터 로깅
      setProducts(data);
    } catch (err) {
      console.error("상품 로딩 실패", err);
    }
  };

  // 삭제 처리
  const handleDelete = async (id: number) => {
    if (!confirm("삭제할까요?")) return;
    try {
      await fetch(`${API_BASE}/products/${id}`, { method: "DELETE" });
      fetchProducts();
    } catch (err) {
      console.error("삭제 실패", err);
    }
  };

  // 로그인 상태 체크
  const checkLogin = async () => {
    try {
      const res = await fetch(`${API_BASE}/auth/me`, { credentials: "include" });
      setIsLogin(res.ok);
    } catch (err) {
      console.error("로그인 체크 실패", err);
      setIsLogin(false);
    }
  };

  // 카테고리 이름 표시
  const getCategoryName = (primaryId?: number, secondaryId?: number) => {
    console.log("primaryId:", primaryId, "secondaryId:", secondaryId); // 디버깅용

    const primary = categories.find(c => c.id === primaryId);
    const secondary = primary?.children?.find(c => c.id === secondaryId);

    console.log("Primary:", primary, "Secondary:", secondary); // 디버깅용

    // primary 또는 secondary가 없으면 "카테고리 없음" 출력
    if (!primary || !secondary) return "카테고리 없음";
    return `${primary.name} / ${secondary.name}`;
  };

  useEffect(() => {
    fetchProducts();
    checkLogin();
  }, []);

  // 모달 열기
  const openModal = (mode: "create" | "edit" | "view", productId?: number) => {
    setModalMode(mode);
    setCurrentProductId(productId);
    setShowModal(true);
  };

  return (
    <>
      <Header
        onOpenModal={() => openModal("create")}
        isLogin={isLogin}
        setIsLogin={setIsLogin}
      />

{/*슬라이드 배너광고 */}
<SmallBanner/>

<img src ="/img/banner.png" className="w-100"/>

<TextBanner>
<h1>나이키스킴스 2026 </h1>
<p>출시알림을 설정하고</p>
<BtnWrap>
  <BlackBtn>알림설정하기</BlackBtn>
  <div className="mx-2"></div>
  <BlackBtn>자세히 보기</BlackBtn>
</BtnWrap>
</TextBanner>

<VideoWrap>
<video
autoPlay muted loop playsInline
>
<source src="/videos/motion.mp4" type="video/mp4"/>
</video>
<TextBanner2>
<h1>나이키스킴스 2026 </h1>
<p>출시알림을 설정하고</p>
<BtnWrap>
  <WhiteBtn>알림설정하기</WhiteBtn>
  <div className="mx-2"></div>
  <WhiteBtn>자세히 보기</WhiteBtn>
</BtnWrap>
</TextBanner2>


</VideoWrap>

      <Container className="py-4">
 
      </Container>

      {/* 상품 모달 */}
      <ProductModal
        show={showModal}
        onClose={() => setShowModal(false)}
        onSaved={() => {
          setShowModal(false);
          fetchProducts();
        }}
        productId={currentProductId}
        mode={modalMode}
      />
    </>
  );
}
