"use client";

import { useState, useEffect, useMemo } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import styled from "styled-components";

const API_BASE = "http://localhost:9999/api";

type CategoryNode = {
  id: number;
  name: string;
  children?: CategoryNode[];
};

type SizeStock = {
  size: number;
  stock: number;
};

type SpecItem = {
  label: string;
  value: string;
};

type ProductForm = {
  title: string;
  desc: string;
  price: string;
  sizes: SizeStock[];
  specs: SpecItem[];
};

type Props = {
  show: boolean;
  onClose: () => void;
  onSaved: () => void;
  productId?: number;
  mode?: "create" | "edit" | "view";
  isLogin: boolean;
  categoryList?: CategoryNode[];
};

export default function ProductModal({
  show,
  onClose,
  onSaved,
  productId,
  mode = "create",
  isLogin,
  categoryList = [],
}: Props) {
  const [form, setForm] = useState<ProductForm>({
    title: "",
    desc: "",
    price: "",
    sizes: [],
    specs: [],
  });

  const [sizeInput, setSizeInput] = useState("");
  const [stockInput, setStockInput] = useState("");
  const [selectedSize, setSelectedSize] = useState<number | null>(null);

  const [specLabel, setSpecLabel] = useState("");
  const [specValue, setSpecValue] = useState("");

  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  const [category1, setCategory1] = useState<number | null>(null);
  const [category2, setCategory2] = useState<number | null>(null);

  const [saving, setSaving] = useState(false);

  const isViewMode = mode === "view";

  const unitPrice = useMemo(() => {
    const n = Number(form.price);
    return Number.isFinite(n) ? n : 0;
  }, [form.price]);

  // -----------------------------
  // 상품 불러오기
  // -----------------------------
  useEffect(() => {
    if (!show) return;

    if (mode === "create") {
      setForm({ title: "", desc: "", price: "", sizes: [], specs: [] });
      setCategory1(null);
      setCategory2(null);
      setImageFile(null);
      setImageUrl(null);
      setSelectedSize(null);
      return;
    }

    if ((mode === "edit" || mode === "view") && productId) {
      (async () => {
        try {
          const res = await fetch(`${API_BASE}/products/${productId}`, {
            credentials: "include",
          });

          if (!res.ok) throw new Error("상품 조회 실패");

          const data = await res.json();

          setForm({
            title: data.title ?? "",
            desc: data.desc ?? "",
            price: data.price?.toString() ?? "",
            sizes: data.sizes ?? [],
            specs: data.specs ?? [],
          });

          const primaryId =
            typeof data.primaryCategory === "object"
              ? data.primaryCategory?.id
              : data.primaryCategory;

          const secondaryId =
            typeof data.secondaryCategory === "object"
              ? data.secondaryCategory?.id
              : data.secondaryCategory;

          setCategory1(primaryId ? Number(primaryId) : null);
          setCategory2(secondaryId ? Number(secondaryId) : null);

          setImageUrl(
            data.imageUrl ? `http://localhost:9999${data.imageUrl}` : null
          );
          setImageFile(null);
        } catch (err) {
          alert("상품 정보를 불러오지 못했습니다.");
          onClose();
        }
      })();
    }
  }, [show, mode, productId]);

  // -----------------------------
  // 입력 변경
  // -----------------------------
  const onChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    if (isViewMode) return;

    const { name, value } = e.target;

    if (name === "category1") {
      setCategory1(value === "" ? null : Number(value));
      setCategory2(null);
    } else if (name === "category2") {
      setCategory2(value === "" ? null : Number(value));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  // -----------------------------
  // 사이즈 추가
  // -----------------------------
  const addSize = () => {
    const size = Number(sizeInput);
    const stock = Number(stockInput);

    if (!size || size <= 0) return alert("사이즈를 입력하세요.");
    if (stock < 0) return alert("재고는 0 이상이어야 합니다.");

    if (form.sizes.some((s) => s.size === size)) {
      return alert("이미 등록된 사이즈입니다.");
    }

    setForm((prev) => ({
      ...prev,
      sizes: [...prev.sizes, { size, stock }],
    }));

    setSizeInput("");
    setStockInput("");
  };

  // -----------------------------
  // 상품정보고시 추가
  // -----------------------------
  const addSpec = () => {
    if (!specLabel.trim()) return alert("항목명을 입력하세요.");
    if (!specValue.trim()) return alert("내용을 입력하세요.");

    setForm((prev) => ({
      ...prev,
      specs: [...prev.specs, { label: specLabel, value: specValue }],
    }));

    setSpecLabel("");
    setSpecValue("");
  };

  const removeSpec = (idx: number) => {
    setForm((prev) => ({
      ...prev,
      specs: prev.specs.filter((_, i) => i !== idx),
    }));
  };

  // -----------------------------
  // 저장
  // -----------------------------
  const handleSave = async () => {
    if (isViewMode) return;

    if (!form.title.trim()) return alert("상품명을 입력하세요.");
    if (!form.price.trim()) return alert("가격을 입력하세요.");

    if (category1 == null || category2 == null) {
      return alert("카테고리를 선택하세요.");
    }

    if (form.sizes.length === 0) {
      return alert("사이즈를 하나 이상 추가하세요.");
    }

    if (form.specs.length === 0) {
      return alert("상품정보고시를 하나 이상 추가하세요.");
    }

    if (mode === "create" && !imageFile) {
      return alert("이미지를 선택하세요.");
    }

    setSaving(true);

    const fd = new FormData();
    fd.append("title", form.title);
    fd.append("desc", form.desc);
    fd.append("price", Number(form.price).toString());
    fd.append("primaryCategoryId", String(category1));
    fd.append("secondaryCategoryId", String(category2));

    // ⭐ 사이즈/재고 JSON
    fd.append("sizes", JSON.stringify(form.sizes));

    // ⭐ 상품정보고시 JSON
    fd.append("specs", JSON.stringify(form.specs));

    if (imageFile) {
      fd.append("image", imageFile);
    }

    try {
      const res = await fetch(
        mode === "create"
          ? `${API_BASE}/products`
          : `${API_BASE}/products/${productId}`,
        {
          method: mode === "create" ? "POST" : "PUT",
          body: fd,
          credentials: "include",
        }
      );

      if (!res.ok) {
        throw new Error(
          mode === "create" ? "상품 등록 실패" : "상품 수정 실패"
        );
      }

      alert(mode === "create" ? "등록 완료!" : "수정 완료!");
      onSaved();
      onClose();
    } catch (err: any) {
      alert(err?.message || "저장 중 오류 발생");
    } finally {
      setSaving(false);
    }
  };

  // -----------------------------
  // 카테고리 표시용
  // -----------------------------
  const primaryObj = categoryList.find((c) => c.id === category1);
  const secondaryObj = primaryObj?.children?.find((c) => c.id === category2);

  return (
    <Modal show={show} onHide={onClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>
          {mode === "create" && "상품 등록"}
          {mode === "edit" && "상품 수정"}
          {mode === "view" && "상품 상세"}
        </Modal.Title>
      </Modal.Header>

      <Modal.Body>
        <Form.Group className="mb-3">
          <Form.Label>상품명</Form.Label>
          <Form.Control
            name="title"
            value={form.title}
            onChange={onChange}
            disabled={saving || isViewMode}
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>설명</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            name="desc"
            value={form.desc}
            onChange={onChange}
            disabled={saving || isViewMode}
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>가격</Form.Label>
          <Form.Control
            name="price"
            value={form.price}
            onChange={onChange}
            inputMode="numeric"
            disabled={saving || isViewMode}
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>카테고리</Form.Label>

          {isViewMode ? (
            <div>
              {primaryObj?.name ?? "없음"} / {secondaryObj?.name ?? "없음"}
            </div>
          ) : (
            <>
              <Form.Select
                name="category1"
                value={category1 ?? ""}
                onChange={onChange}
                disabled={saving}
              >
                <option value="">1차 카테고리 선택</option>
                {categoryList.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>

              <Form.Select
                name="category2"
                className="mt-2"
                value={category2 ?? ""}
                onChange={onChange}
                disabled={!category1 || saving}
              >
                <option value="">2차 카테고리 선택</option>
                {primaryObj?.children?.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>
            </>
          )}
        </Form.Group>

        {/* ----------------------------- */}
        {/* 사이즈/재고 UI */}
        {/* ----------------------------- */}
        <Form.Group className="mb-3">
          <Form.Label>사이즈/재고</Form.Label>

          {!isViewMode && (
            <InputRow>
              <Form.Control
                placeholder="사이즈 (예: 250)"
                value={sizeInput}
                onChange={(e) => setSizeInput(e.target.value)}
                disabled={saving}
              />
              <Form.Control
                placeholder="재고 (예: 10)"
                value={stockInput}
                onChange={(e) => setStockInput(e.target.value)}
                disabled={saving}
              />
              <Button onClick={addSize} disabled={saving}>
                추가
              </Button>
            </InputRow>
          )}

          <SizeWrap>
            {form.sizes.map((s) => (
              <SizeBtn
                key={s.size}
                soldOut={s.stock === 0}
                selected={selectedSize === s.size}
                onClick={() => setSelectedSize(s.size)}
                disabled={s.stock === 0 || saving || isViewMode}
              >
                {s.size}
              </SizeBtn>
            ))}
          </SizeWrap>

          {selectedSize && (
            <div className="mt-2">
              선택 사이즈 재고:{" "}
              {form.sizes.find((s) => s.size === selectedSize)?.stock ?? 0}
            </div>
          )}
        </Form.Group>

        {/* ----------------------------- */}
        {/* 상품정보고시 UI */}
        {/* ----------------------------- */}
        <Form.Group className="mb-3">
          <Form.Label>상품정보고시</Form.Label>

          {!isViewMode && (
            <InputRow>
              <Form.Control
                placeholder="항목명 (예: 무게)"
                value={specLabel}
                onChange={(e) => setSpecLabel(e.target.value)}
                disabled={saving}
              />
              <Form.Control
                placeholder="내용 (예: 약 292g)"
                value={specValue}
                onChange={(e) => setSpecValue(e.target.value)}
                disabled={saving}
              />
              <Button onClick={addSpec} disabled={saving}>
                추가
              </Button>
            </InputRow>
          )}

          <SpecList>
            {form.specs.map((s, idx) => (
              <SpecItem key={idx}>
                <SpecLabel>{s.label}</SpecLabel>
                <SpecValue>{s.value}</SpecValue>
                {!isViewMode && (
                  <RemoveBtn onClick={() => removeSpec(idx)} disabled={saving}>
                    삭제
                  </RemoveBtn>
                )}
              </SpecItem>
            ))}
          </SpecList>
        </Form.Group>

        <Form.Group>
          <Form.Label>이미지</Form.Label>

          {imageUrl && (
            <img
              src={imageUrl}
              alt="상품 이미지"
              style={{
                width: "100%",
                height: 150,
                objectFit: "cover",
                marginBottom: 10,
              }}
            />
          )}

          {!isViewMode && (
            <Form.Control
              type="file"
              accept="image/*"
              onChange={(e) => {
                const target = e.target as HTMLInputElement;
                setImageFile(
                  target.files && target.files.length > 0
                    ? target.files[0]
                    : null
                );
              }}
              disabled={saving}
            />
          )}
        </Form.Group>
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={onClose} disabled={saving}>
          닫기
        </Button>

        {!isViewMode && (
          <Button variant="primary" onClick={handleSave} disabled={saving}>
            {saving
              ? mode === "create"
                ? "등록 중..."
                : "수정 중..."
              : mode === "create"
              ? "등록"
              : "수정"}
          </Button>
        )}
      </Modal.Footer>
    </Modal>
  );
}

/* ==========================
   styled-components
========================== */

const InputRow = styled.div`
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
`;

const SizeWrap = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
`;

const SizeBtn = styled.button<{ soldOut: boolean; selected: boolean }>`
  border: 1px solid #ddd;
  padding: 10px 14px;
  border-radius: 6px;
  background: ${({ selected }) => (selected ? "#e6f0ff" : "white")};
  cursor: ${({ soldOut }) => (soldOut ? "not-allowed" : "pointer")};
  color: ${({ soldOut }) => (soldOut ? "#aaa" : "inherit")};
  text-decoration: ${({ soldOut }) => (soldOut ? "line-through" : "none")};
  border-color: ${({ selected }) => (selected ? "#007bff" : "#ddd")};

  &:disabled {
    opacity: 0.8;
  }
`;

const SpecList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const SpecItem = styled.div`
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 8px 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafafa;
`;

const SpecLabel = styled.div`
  width: 25%;
  font-weight: 600;
  color: #333;
`;

const SpecValue = styled.div`
  flex: 1;
  color: #555;
`;

const RemoveBtn = styled.button`
  border: 1px solid #ff4d4f;
  background: #fff;
  color: #ff4d4f;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
  }
`;
