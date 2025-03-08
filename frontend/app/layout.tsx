// app/layout.tsx
import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css"; // 경로 확인 필수!
import Header from "../components/Header";
import TokenRefreshProvider from "../components/TokenRefreshProvider"; // 클라이언트 전용 컴포넌트

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "POFO",
  description: "개발자의 지식과 경험을 공유하는 공간",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {

  return (
    <html lang="ko">
      <body className={`${geistSans.variable} ${geistMono.variable} antialiased bg-gray-100 text-gray-900`}>
        <Header />
        <TokenRefreshProvider />
        <main className="container mx-auto min-h-screen px-4">{children}</main>
      </body>
    </html>
  );
}
