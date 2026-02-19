"use client";

import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import Header from "@/include/Header";
import ProductModal from "@/modal/ProductModal";
import {
  PageWrapper,
  MainContentWrapper,
  Content,
  ProductCard,
  ProductDetails,
  ButtonGroup,
  H1,
  H5,
  ProductImage,
  ContentInner,
  P,
  Pprice,
} from "@/styled/Admin.styles";
import SideBar from "./include/SideBar";

const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

type CategoryNode = {
  id: number;
  name: string;
  children?: CategoryNode[];
};

type Product = {
  id: number;
  title: string;
  desc: string;
  price: number;
  imageUrl?: string;

  // 프론트에서 매핑한 카테고리 구조
  primaryCategory?: {
    id: number;
    name: string;
  };

  secondaryCategory?: {
    id: number;
    name: string;
  };

  // 서버에서 내려오는 id 형태도 포함
  primaryCategoryId?: number;
  secondaryCategoryId?: number;
};

export default function Admin() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categoryList, setCategoryList] = useState<CategoryNode[]>([]);

  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] =
    useState<"create" | "edit" | "view">("create");
  const [currentProductId, setCurrentProductId] =
    useState<number | undefined>(undefined);

  const [isLogin, setIsLogin] = useState<boolean>(false);
  const onOpenModal = () => openModal("create");

  /* -----------------------------
     카테고리 리스트 조회
  ----------------------------- */
  const fetchCategories = async () => {
    try {
      const res = await fetch(`${API_BASE}/categories`, {
        cache: "no-store",
      });

      if (!res.ok) throw new Error("카테고리 로딩 실패");

      const data = await res.json();
      setCategoryList(data);
    } catch (err) {
      console.error("카테고리 로딩 실패", err);
    }
  };

  /* -----------------------------
     상품 리스트 조회 (카테고리 매핑 포함)
  ----------------------------- */
  const fetchProducts = async () => {
    try {
      const res = await fetch(`${API_BASE}/products`, {
        cache: "no-store",
      });

      if (!res.ok) throw new Error("상품 리스트 불러오기 실패");

      const data = await res.json();

      // 🔥 카테고리 매핑
      const mapped = data.map((p: any) => {
        const primaryId =
          p.primaryCategory?.id ??
          p.primaryCategoryId ??
          p.primaryCategory;

        const secondaryId =
          p.secondaryCategory?.id ??
          p.secondaryCategoryId ??
          p.secondaryCategory;

        const primary = categoryList.find((c) => c.id === primaryId);
        const secondary = primary?.children?.find(
          (c) => c.id === secondaryId
        );

        return {
          ...p,
          primaryCategory: primary
            ? { id: primary.id, name: primary.name }
            : undefined,
          secondaryCategory: secondary
            ? { id: secondary.id, name: secondary.name }
            : undefined,
        };
      });

      setProducts(mapped);
    } catch (err) {
      console.error("상품 로딩 실패", err);
    }
  };

  /* -----------------------------
     로그인 체크
  ----------------------------- */
  const checkLogin = async () => {
    try {
      const res = await fetch(`${API_BASE}/auth/me`, {
        credentials: "include",
      });

      setIsLogin(res.ok);
    } catch {
      setIsLogin(false);
    }
  };

  /* -----------------------------
     삭제
  ----------------------------- */
  const handleDelete = async (id: number) => {
    if (!confirm("삭제할까요?")) return;

    try {
      await fetch(`${API_BASE}/products/${id}`, {
        method: "DELETE",
      });

      fetchProducts();
    } catch (err) {
      console.error("삭제 실패", err);
    }
  };

  /* -----------------------------
     최초 로딩
  ----------------------------- */
  useEffect(() => {
    fetchCategories();
    checkLogin();
  }, []);

  /* -----------------------------
     카테고리 로딩 후 상품 재조회
  ----------------------------- */
  useEffect(() => {
    if (categoryList.length > 0) {
      fetchProducts();
    }
  }, [categoryList]);

  /* -----------------------------
     모달 열기
  ----------------------------- */
  const openModal = (
    mode: "create" | "edit" | "view",
    productId?: number
  ) => {
    setModalMode(mode);
    setCurrentProductId(productId);
    setShowModal(true);
  };

  return (
    <PageWrapper>
      <SideBar />

      <MainContentWrapper>
        <Header
          onOpenModal={() => openModal("create")}
          isLogin={isLogin}
          setIsLogin={setIsLogin}
        />

        <Content>
          <div className="d-flex justify-content-between my-4">
          <H1>쇼핑몰 관리</H1>    
          <Button 
          className="me-2" variant="outline-primary" onClick={onOpenModal} >
                상품 등록
              </Button>
            </div>    
 
     


          <ContentInner>
            {products.map((p) => (
              <ProductCard
                key={p.id}
                onClick={() => openModal("view", p.id)}
              >
                {p.imageUrl && (
                  <ProductImage
                    src={`${API_ROOT}${p.imageUrl}`}
                    alt={p.title}
                  />
                )}

                <ProductDetails>
                  <H5>{p.title}</H5>

                  {/* 카테고리 출력 */}
                  <P>
                    {p.primaryCategory && p.secondaryCategory
                      ? `${p.primaryCategory.name} / ${p.secondaryCategory.name}`
                      : "카테고리 정보 없음"}
                  </P>

                  <Pprice>
                    {p.price.toLocaleString()}원
                  </Pprice>
                </ProductDetails>

                <ButtonGroup>
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      openModal("edit", p.id);
                    }}
                  >
                    수정
                  </Button>

                  <Button
                    variant="danger"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(p.id);
                    }}
                  >
                    삭제
                  </Button>
                </ButtonGroup>
              </ProductCard>
            ))}
          </ContentInner>
        </Content>

        <ProductModal
          show={showModal}
          onClose={() => setShowModal(false)}
          onSaved={() => {
            setShowModal(false);
            fetchProducts();
          }}
          productId={currentProductId}
          mode={modalMode}
          isLogin={isLogin}
          categoryList={categoryList ?? []}
        />
      </MainContentWrapper>
    </PageWrapper>
  );
}
