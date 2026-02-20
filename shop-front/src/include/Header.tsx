"use client";
//add
import { useEffect, useState } from "react";

import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import Link from "next/link";//
import "./header.css";

type Props = {
  onOpenModal: () => void;
  isLogin: boolean | null;
  setIsLogin: (v: boolean) => void;
};

//add
type MenuNode = {
  id:number; name:string; path?:string | null;
  children?:MenuNode[];
}

//add
const API_BASE = "http://localhost:9999/api";

export default function Header({ onOpenModal, isLogin, setIsLogin }: Props) {

//add 메뉴상태 추가
const [menus, setMenus] = useState<MenuNode[]>([]);

//add
useEffect(() => { //컴포넌트가 렌더링된 이후 실행되는 코드 블록입니다.
  const fetchMenus = async () => {//fetchMenus라는 비동기 함수 선언
    try{//예외 처리 시작
const res = await fetch(`${API_BASE}/nav-menus/tree`);
if(!res.ok) return;//👉 응답이 실패하면 종료
const data = await res.json();//👉 응답 데이터를 JSON으로 변환
setMenus(data);//useState로 만든 상태 변경 함수
    }catch(e){
console.error("menu load error", e);//👉 에러가 발생하면 여기로 이동
    }
  };
  fetchMenus();//👉 위에서 만든 함수 실행
},[]);//👉 useEffect의 의존성 배열 [] → 처음 1번만 실행
//값이 들어가면 그 값이 바뀔 때마다 다시 실행

  const logout = async () => {
    try {
      await fetch("http://localhost:9999/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
      setIsLogin(false);
    } catch (err) {
      console.error("logout error:", err);
    }
  };

  if (isLogin === null) return null;

  // 재귀적으로 메뉴를 렌더링하는 함수
  const renderMenu = (menu: MenuNode) => (
    <NavDropdown.Item as={Link} href={menu.path ?? "#"}>{menu.name}</NavDropdown.Item>
  );

 const renderDropdown = (node: MenuNode) => (
  <NavDropdown
    key={node.id}
    title={node.name}
    id={`nav-${node.id}`}
    className="mega-dropdown"
  >
    <div className="mega-menu">
      {(node.children ?? []).map((child) => (
        <div key={child.id} className="mega-column">
          <div className="mega-title">
            {child.path ? (
              <Link href={child.path} className="mega-title-link">
                {child.name}
              </Link>
            ) : (
              child.name
            )}
          </div>

          {(child.children ?? []).map((sub) => (
            <NavDropdown.Item
              key={sub.id}
              as={Link}
              href={sub.path ?? "#"}
              className="mega-item"
            >
              {sub.name}
            </NavDropdown.Item>
          ))}
        </div>
      ))}
    </div>
  </NavDropdown>
);

  return (
    <Navbar
      bg="white"
      expand="lg"
      className="border-bottom"
      style={{ backgroundColor: "#ffffff" }}
    >
      <Container>
        {/* 왼쪽 브랜드 */}
        <Navbar.Brand
          href="/"
          style={{ color: "#000", fontWeight: "600" }}
        >
          My shop
        </Navbar.Brand>


        {/* 가운데 메뉴 */}
        <Nav className="mx-auto">
          {menus.map(m1 =>
            (m1.children ?? []).length > 0 ? 
              renderDropdown(m1) : 
              <Nav.Link key={m1.id} as={Link} href={m1.path ?? "#"}>
                {m1.name}
              </Nav.Link>
          )}
        </Nav>

       

        {/* 오른쪽 버튼 */}
        <div className="ms-auto d-flex align-items-center">
          {isLogin ? (
            <>
    <Button
    className="me-2"
    variant="outline-dark"
    onClick={() => (window.location.href = "/cart")}
  >
    장바구니
  </Button>

  <Button
    className="me-2"
    variant="outline-dark"
    onClick={() => (window.location.href = "/orders")}
  >
    주문
  </Button>

  <Button
    className="me-2"
    variant="outline-dark"
    onClick={() => (window.location.href = "/admin")}
  >
    관리자
  </Button>

  <Button variant="outline-dark" onClick={logout}>
    로그아웃
  </Button>
            </>
          ) : (
            <>
              <a
                href="/member"
                className="btn btn-outline-dark me-2"
              >
                회원가입
              </a>
              <a
                href="/login"
                className="btn btn-outline-dark"
              >
                로그인
              </a>
            </>
          )}
        </div>
      </Container>
    </Navbar>
  );
}


/*
 {/* 가운데 메뉴 
        <Nav className="mx-auto">
          {/*<Nav.Link href="/consumer" style={{ color: "#000" }}>
            상품
          </Nav.Link>
{menus.map((m1) => (
<NavDropdown
key={m1.id}
title={
(m1.children ?? []).length === 0 ? (
<Link href={m1.path ?? "#"} style={{textDecoration:"none", color:"black"}}>
{m1.name}
</Link>
):(  
  m1.name) 
}
id={`nav-${m1.id}`}
>
  {(m1.children ?? []).map((m2) => (
    <NavDropdown.Item
    as={Link}
    href={m1.path ?? "#"}
    >
      {m2.name}
    </NavDropdown.Item>
  ))}

</NavDropdown>
))}
        </Nav>

         const renderDropdown = (node: MenuNode) => (
    <NavDropdown key={node.id} title={node.name} id={`nav-${node.id}`}>
      {(node.children ?? []).map(child => 
        child.children && child.children.length > 0 ? 
          renderDropdown(child) : 
          renderMenu(child)
      )}
    </NavDropdown>
  );
*/