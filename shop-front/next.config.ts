import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  reactStrictMode: true, // 권장 옵션
  compiler: {
    styledComponents: true, // ✅ styled-components SSR 활성화
  },
};

export default nextConfig;
