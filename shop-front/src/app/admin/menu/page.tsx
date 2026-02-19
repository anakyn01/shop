"use client";//👉 Next.js에서 이 파일이 클라이언트 컴포넌트임을 선언
//브라우저에서 실행됨 — useState, useEffect 사용 가능
import {useEffect, useMemo, useState} from "react";
/*
👉 React 훅 가져오기
useState → 상태 관리 useEffect → 마운트 시 실행
useMemo → 계산값 메모이제이션
메모이제이션 : 👉 한 번 계산한 결과를 저장해 두었다가, 
같은 계산을 다시 요청하면 저장된 값을 그대로 사용하는 기법

복잡한 계산을 반복하면 속도가 느려지기 때문
이미 계산한 결과를 저장해 두면 성능이 크게 좋아 진다
📌 예시 (피보나치 수열)
f(n) = f(n-1) + f(n-2)
❌ 메모이제이션 없이
f(5)를 구할 때
f(4), f(3)을 구하고
또 f(3), f(2)를 또 구하고…
→ 같은 계산을 여러 번 함 😭
*/
import {Button, Form} from "react-bootstrap";

import Header from "@/include/Header";
import SideBar from "../include/SideBar";

import { PageWrapper, MainContentWrapper, Content,
H1, H5, ContentInner, P,   
 } from "@/styled/Admin.styles";

 //3️⃣ 상수 & 타입 정의
 const MENU_LS_KEY = "nav_menus"; //👉 localStorage에 저장할 key 이름
 //localStorage 👉 브라우저 안에 데이터를 저장해 두는 공간 브라우저를 꺼도 안 사라지고
 //다시 접속해도 그대로 남아있어요. 

 type MenuNode = {
id:number; //고유값
name:string;  //메뉴 이름
path?:string; //3차 메뉴에서 사용하는 URL
children?:MenuNode[];//하위 메뉴 배열
 };

//4️⃣ localStorage 헬퍼 함수
const loadMenusLS = ():MenuNode[] => {
 if(typeof window === "undefined") return[];
 try{//
 //👉 저장된 JSON 가져오기
 const raw = localStorage.getItem(MENU_LS_KEY);
 if (!raw) return [];//👉 저장된 값이 없다면 (null 이거나 빈 값이면) 빈 배열 반환
 //👉 문자열 → 객체 변환
 const parsed = JSON.parse(raw);
 //👉 혹시 파싱은 됐는데 배열이 아닐 경우 방어 코드 배열이 아니면 잘못된 데이터 그래서 빈 배열 반환
 if(!Array.isArray(parsed)) return[];
 return parsed; //👉 정상적인 배열이면 그 데이터를 그대로 반환
 } catch {
    return []; //👉 JSON.parse 중 에러가 나면 👉 그냥 빈 배열 반환
 }
}
//👉 SSR 환경 보호 (브라우저 아닐 경우 실행 방지)
//server-Side Rendering(서버 사이드 렌더링)의 약자로, 
// 웹 페이지를 브라우저가 아닌 서버에서 미리 렌더링하여 
// 완전한 HTML 형태로 클라이언트에 전달하는 방식입니다

const saveMenusLS = (menus: MenuNode[]) => {
//(menus: MenuNode[]) → MenuNode 타입 배열을 매개변수로 받는다
//즉, 저장할 메뉴 목록을 전달받는 함수 
 if(typeof window === "undefined") return;
 /*
 window가 없으면 (SSR / 서버 환경) localStorage를 사용할 수 없음 
 그래서 그냥 함수 종료 (return)
 */
localStorage.setItem(MENU_LS_KEY, JSON.stringify(menus));
//자바스크립트 객체(배열)를 문자열(JSON 형태)로 변환
}

const nextMenuIdFrom = (menus: MenuNode[]) => {
    let max = 0; //처음엔 0부터 시작
//재귀함수 nodes 배열(현재 단계의 노드들)을 돌면서 
// max를 갱신하고, 자식도 계속 탐색
const walk = (nodes:MenuNode[]) => {
    for(const n of nodes) {
        //nodes 배열의 각 요소를 하나씩 n에 담아 반복
        max = Math.max(max, n.id);
        //현재 max 값과 n.id 중 더 큰 값을 max에 저장
if(n.children?.length) walk(n.children);//자식 배열이 존재하고 비어있지 않으면 walk(n.children) 실행
//n.children가 있으면(=자식 메뉴가 있으면) 그 자식들도 탐색해야 하니까 재귀 호출
//?.는 옵셔널 체이닝:
//n.children가 undefined/null이면 에러 안 나고 그냥 넘어감
    }
};
walk(menus); //실제로 실행 탐색을 시작하는 실행코드
return max + 1;//탐색이 끝나면, 가장 큰 id 값이 max에 들어있음
//그 다음 번호를 새로 발급하려고 max + 1 반환
}

export default function NavMenuPage() {
const [isLogin, setIsLogin] = useState<boolean>(false);

const [menuList, setMenuList] = useState<MenuNode[]>([]);

// ✅ 입력값 (1/2/3차)
const [menu1Name, setMenu1Name] =useState("")
const [menu2Name, setMenu2Name] =useState("")
const [menu3Name, setMenu3Name] =useState("")
const [menu3Path, setMenu3Path] =useState("")
/*
✅ 1차 → 그룹(폴더 개념)
✅ 2차 → 중간 분류
✅ 3차 → 실제 페이지 (라우팅 대상)
*/

// ✅ 선택값
const [selectedMenu1Id, setSelectedMenu1Id] = useState<number | "">("")
const [selectedMenu2Id, setSelectedMenu2Id] = useState<number | "">("")

//로그인 상태 체크 (기존 패턴 유지)
const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

const checkLogin = async () => {
    try{
const res = await fetch(`${API_BASE}/auth/me`, {credentials:"include"});
/*
fetch() → 서버에 HTTP 요청 보내는 함수
${API_BASE}/auth/me → 로그인한 사용자 정보를 확인하는 API
*/
setIsLogin(res.ok);
//
    }catch(err){
console.error("로그인 체크 실패", err);
setIsLogin(false);      
    }
}


//메뉴로드 리프레시
const fetchMenus = () => {
    const ls = loadMenusLS();
    setMenuList(ls);
}

}
