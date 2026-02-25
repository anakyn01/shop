"use client"

import {useEffect, useMemo, useState} from "react";
import {useRouter} from "next/navigation";

import { TextBanner } from "@/styled/Component.styles"
import { BtnWrap, BlackBtn } from "@/styled/Button.styles"

type BannerItem = {
  id:number; title:string; desc:string; imageUrl?:string;
  linkUrl?:string | null; sortOrder? : number | null;
  visibleYn? : "Y" | "N";
}

const API_ROOT = "http://localhost:9999";
const API_BASE = `${API_ROOT}/api`;

async function apiBannerList(): Promise<BannerItem[]> {
  /*
비동기 함수입니다. 내부에서 await을 사용할 수 있고, 항상 Promise를 반환합니다.  
이 함수는 BannerItem[] (배너 객체 배열)을 감싸는 Promise를 반환합니다.
즉, 최종적으로는 BannerItem[]을 resolve 하는 Promise입니다.
  */
  const res = await fetch(`${API_BASE}/banners`, {
    //→ 브라우저에서 제공하는 HTTP 요청 함수입니다.
    method: "GET",//HTTP 요청 방식은 GET입니다.
    credentials: "include",//쿠키를 포함해서 요청하겠다는 의미입니다.
    cache: "no-store",//브라우저 캐시를 사용하지 않겠다는 의미입니다.
  });
  if (!res.ok) {//실패했으면 아래 코드 실행.
    const text = await res.text().catch(() => "");
    //응답 바디를 문자열로 읽습니다.
    throw new Error(`banners failed: ${res.status} ${text}`);
    //HTTP 요청이 실패했으면 에러를 강제로 발생시킵니다.
  }
  return await res.json();//응답 데이터를 JSON 형태로 변환합니다.
}

export default function TEXTBanner() {

const router = useRouter(); 
/*
Next.js에서 제공하는 useRouter() 훅
페이지 이동 (router.push()), 뒤로 가기, 새로고침 등을 할 때 사용합니다.
*/
const [bannerList, setBannerList] = useState<BannerItem[]>([]);
/*
bannerList → 배너 목록을 저장하는 state
setBannerList → 그 값을 변경하는 함수
useState<BannerItem[]>([])
타입은 BannerItem[] (배너 객체 배열)
초기값은 빈 배열 []

👉 처음에는 배너가 없고 API 호출 후 배열이 채워집니다.
*/
const [loading, setLoading] = useState(false);
/*
loading → 현재 로딩 중인지 여부 (boolean)
setLoading → 로딩 상태 변경 함수
초기값은 false (처음엔 로딩 중 아님)
*/

const fetchBanners = async () => {//배너 데이터를 가져오는 비동기 함수 정의
//내부에서 API 호출 수행
setLoading(true);
  try{ //API 호출 중 에러 발생 가능하므로 예외 처리 시작
    const data = await apiBannerList();//앞에서 만든 apiBannerList() 호출
    const list = Array.isArray(data) ? data : [];
    //data가 배열이면 그대로 사용 아니면 빈 배열로 대체 👉 방어 코드 (defensive coding)
  } catch (e:any) {//API 호출 중 에러 발생 시 실행
    console.error(e);//콘솔에 에러 출력 (디버깅용)
    setBannerList([]);//에러가 나면 빈 배열로 초기화
  } finally {//성공하든 실패하든 항상 실행되는 블록
setLoading(false);//로딩 상태 종료  로딩 스피너 제거
  }
}

useEffect(() => {//컴포넌트가 처음 마운트될 때 한 번만 실행
  fetchBanners();
}, []);//두 번째 인자 []는 dependency 배열 빈 배열이므로 최초 1회 실행

// ✅ 노출(Y)만 + sortOrder 정렬
  const visibleSorted = useMemo(() => {
    return bannerList
      .filter((b) => (b.visibleYn ?? "Y") === "Y")
      .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0));
  }, [bannerList]);

  // ✅ 일단 "첫 번째 배너 1개만" 보여주기 (원하면 슬라이드로 확장 가능)
  const banner = visibleSorted[0];

  const goLink = (url?: string | null) => {
    if (!url) return alert("이 배너는 이동 링크가 설정되어 있지 않습니다.");

    // 외부링크 / 내부링크 분기
    if (/^https?:\/\//i.test(url)) {
      window.open(url, "_blank", "noopener,noreferrer");
      return;
    }
    router.push(url);
  };

  if (loading) return null;
  if (!banner) return null;

return(
    <>
    <TextBanner>
    <h1>나이키스킴스 2026 </h1>
    <p>출시알림을 설정하고</p>
    <BtnWrap>
      <BlackBtn>알림설정하기</BlackBtn>
      <div className="mx-2"></div>
      <BlackBtn>자세히 보기</BlackBtn>
    </BtnWrap>
    </TextBanner>
    </>
)
}