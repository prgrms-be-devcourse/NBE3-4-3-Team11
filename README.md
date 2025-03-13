## 📑 **프로젝트 개요**

### 🚀 **기존 프로젝트**
- **POFO : 모든 개발자를 위한 아카이빙 사이트**
  > 기존 프로젝트는 개발자들의 프로젝트, 이력서, 게시글 등을 아카이빙하는 플랫폼을 구축하는 것이었습니다. 아래의 주요 기능들이 포함되었습니다:
  - 메인 페이지, 개인 페이지, 게시판 페이지
  - 소셜 로그인, 2차 인증, 계정 관리, 게시글 CRUD 등

#### 💻 **기술 스택**
- Frontend: Next.js, React, TypeScript, Axios
- Backend: Spring Boot, Java, Spring Security, JWT
- Database: MySQL
- Security: OAuth 2.0, JWT

---

### 🚀 **업데이트된 프로젝트**
- **기존 프로젝트를 기반으로 기능 확장 및 개선**
  - 도메인별 `DTO`, `Service`를 Kotlin으로 마이그레이션
  - 각 도메인 별 추가 마이그레이션 작업 진행

#### 추가된 기능:
- **관리자 대시보드**:
  - 사용자 이용 현황, 월별 가입자 현황, 연령별 분포 등 통계 확인 가능
  - 사용자 휴면 계정 처리 및 스케줄링
- **프로젝트 기능**:
  - 썸네일 이미지 등록 기능 추가
  - 검색 기능 및 휴지통 기능 추가
- **유저 기능**:
  - OAuth를 이용한 회원가입 후 계정 통합 처리, 이메일 인증
- **이력서 기능**:
  - 다음 주소 API를 활용한 주소 자동 입력 기능 추가

---

### 🛠 **마이그레이션을 통한 이점**
- **Kotlin 마이그레이션**:
  - **코드 가독성 및 유지보수성 향상**: Kotlin의 간결한 문법을 통해 코드가 더 직관적이고 깔끔해졌습니다.
  - **안정성 및 확장성 증가**: Kotlin의 null 안전성 및 강력한 타입 시스템을 활용해 런타임 에러를 최소화하고 코드의 안정성을 높였습니다.

---

### 📚 **참고 링크**
- **기존 프로젝트 링크**: [기존 프로젝트 주소](https://github.com/prgrms-be-devcourse/NBE3-4-2-Team11)
- **업데이트된 프로젝트 업데이트**: 위에 설명한 내용이 포함된 최신 README
