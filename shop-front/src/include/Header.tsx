"use client";
import { Button, Container, Nav, Navbar } from "react-bootstrap";

type Props = {
  onOpenModal: () => void;
  isLogin: boolean | null; // 로그인 상태
  setIsLogin: (v: boolean) => void; // 상태 변경 함수
};

export default function Header({ onOpenModal, isLogin, setIsLogin }: Props) {
  const logout = async () => {
    try {
      await fetch("http://localhost:9999/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
      setIsLogin(false); // 로그아웃 후 상태 갱신
    } catch (err) {
      console.error("logout error:", err);
    }
  };

  if (isLogin === null) return null; // 로그인 상태 확인 중일 때는 아무것도 렌더링하지 않음

  return (
    <Navbar bg="secondary" expand="lg">
      <Container>
        <Navbar.Brand href="/">My shop</Navbar.Brand>

        <Nav className="me-auto">
          <Nav.Link href="/consumer">상품</Nav.Link>
          <Nav.Link href="/cart">장바구니</Nav.Link>
          <Nav.Link href="/orders">주문</Nav.Link>
        </Nav>

        <div className="ms-auto d-flex align-items-center">
          {isLogin ? (
            <>
<Button className="me-2" 
variant="outline-light" 
onClick={() => window.location.href="/admin"}>
  관리자
</Button>

              <Button variant="outline-light" onClick={logout}>
                로그아웃
              </Button>
            </>
          ) : (
            <>
              <a href="/member" className="btn btn-outline-light me-2">
                회원가입
              </a>
              <a href="/login" className="btn btn-outline-light">
                로그인
              </a>
            </>
          )}
        </div>
      </Container>
    </Navbar>
  );
}
