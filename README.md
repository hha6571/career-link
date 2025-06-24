# career-link

채용·인재 매칭을 위한 풀스택 웹 애플리케이션.
구직자(지원자) · 기업 · 관리자 3역할을 중심으로 **회원/인증**, **기업 등록·심사**, **공고 관리·지원·스크랩**, **대시보드 통계**, **정책/동의 관리**, **보안(JWT/쿠키/Redis)** 을 제공합니다.

---

## 목차

* [핵심 기능](#핵심-기능)
* [도메인 역할](#도메인-역할)
* [백엔드 패키지 구조](#백엔드-패키지-구조)
* [주요 데이터 모델](#주요-데이터-모델)
* [API 개요](#api-개요)
* [보안/인증](#보안인증)
* [프론트엔드](#프론트엔드)
* [로컬 실행](#로컬-실행)
* [환경 변수/설정](#환경-변수설정)
* [Swagger 문서](#swagger-문서)
* [브랜치/릴리스](#브랜치릴리스)
* [권한/정책](#권한정책)
* [문의](#문의)

---

## 핵심 기능

### 공통(비로그인/로그인)
* 공지사항, HOT100 공고 목록/상세, 전체 채용공고 목록/상세
* 정책/동의 문서 **정적 HTML** 관리(`frontend/public/legal/*.html`) → MUI Dialog **iframe**으로 표시`

### 지원자(Applicant)

* **회원가입/로그인**

  * 소셜 온보딩: `/api/auth/social/me`, `/api/auth/social/complete`
  * 휴대폰 자동 포맷/검증, 생년월일(연/월/일 셀렉트)
  * 필수 동의(약관/개인정보), 선택 동의(마케팅)
* **회원정보 수정**: 이름/연락처/생일, 마케팅 수신 동의 토글(+ 상세 보기)
* **이력서 & 자소서 관리**: 학력/경력/자격증/스킬, 자소서 등록/수정
* **공고 탐색 & 스크랩**: 스크랩 추가/해제(내 스크랩 보관함)
* **지원**: 공고별 지원 및 지원 이력 조회

### 기업(Employers)

* **기업 소속 회원가입**: 초대/도메인/승인 기반
* **기업 등록 및 심사**

  * 기업등록요청(기업명/사업자번호/대표자/설립일/이메일/사업자등록증 업로드)
  * **국세청 사업자 진위 확인**(ODcloud NTS API)
  * 중복 등록 확인(`/emp/check-bizRegNo`)
  * 승인 시 안내 메일 발송
* **기업 정보 수정**: 프로필/담당자/연락처/소개/문서 재업로드
* **기업 회원 관리**: 소속 사용자 초대/권한(관리자/에디터/뷰어)
* **기업 공고 관리**

  * 공고 등록/수정/비활성화, 노출 기간 관리
  * **기업 대시보드**: 등록 공고 수, 지원자 수, 스크랩 수, 전환율

### 관리자(Admin)

* **사용자 관리**: 검색/상태 변경/잠금 해제
* **메뉴 관리**: 어드민/포털 메뉴 트리/권한
* **공통 코드 관리**: 직무/지역/경력/학력 등 코드 그룹·아이템
* **기업 등록 심사**: 신청 목록/상세/승인·반려
* **공고 관리**: 목록/상세, 비노출·정책 위반 처리
* **대시보드**: 일/주/월 등록 공고 수, 신규 지원자 수, 활성 기업 수, 트래픽 요약
* **세션/토큰 모니터링**: Redis 키/TTL, 강제 만료/로그아웃

---

## 도메인 역할

* **Applicant**: 구직자 회원/프로필/지원/스크랩
* **Employer**: 기업 정보/회원/공고/대시보드
* **Admin**: 사용자·기업 심사·공고·메뉴·공통코드·모니터링

---

## 백엔드 패키지 구조

```
src/main/java/...
├─ applicant/              # 지원자(계정/프로필/지원/스크랩)
├─ common/                 # 공통 유틸/응답/예외
├─ dashboard/              # 기업/관리자 대시보드 API
├─ employers/              # 기업/기업회원/기업정보/등록심사
├─ faq/                    # FAQ/공지
├─ global/
│  ├─ config/              # RedisConfig, S3Config, SecurityConfig
│  ├─ exception/           # 글로벌 예외 처리
│  ├─ redis/               # Redis 헬퍼/레포
│  ├─ response/            # 통일 응답 모델
│  ├─ s3/                  # 업로드/서명 URL
│  └─ security/            # 인증/인가
│     ├─ oauth/            # OAuth
│     ├─ CustomUserDetailsService
│     ├─ JwtAuthenticationFilter
│     └─ JwtTokenProvider
├─ job/                    # 채용공고 CRUD/검색
├─ jobScrap/               # 스크랩 기능/집계
├─ main/                   # 헬스체크/루트
├─ notice/                 # 공지/약관 버전
└─ users/                  # 사용자 계정/권한/프로필
```

```
src/main/resources/
├─ mapper/                 # MyBatis XML
├─ static/                 # 정적 리소스
├─ templates.email/        # 이메일 템플릿
├─ application.yml
└─ application-local.yml
```

---

## 주요 데이터 모델

* `users`, `user_roles`(APPLICANT/EMPLOYER/ADMIN), `user_profiles`
* `employers`, `employer_members`(기업↔사용자, 역할)
* `employer_registration_requests`(등록요청/서류URL/상태/사유/로그)
* `jobs`(공고/상태/노출기간/형태/지역 등)
* `job_applications`(지원, 상태/이력서·포폴 URL)
* `job_scraps`(사용자별 스크랩)
* `menus`, `common_codes`(그룹/아이템)
* **Redis 세션/토큰**

  * `onboarding:{code}` (TTL 1800s)
  * `auth:rt:{userId}` (Refresh Token 저장/TTL)
  * `session:blacklist:{jti}` (AccessToken 블랙리스트)

---

## API 개요

### 인증/온보딩

* `GET  /api/auth/social/me?code=...` — 소셜 프리필
* `POST /api/auth/social/complete` — 온보딩(agreeTerms/Privacy/Marketing)
* `POST /api/auth/link/resend` — 인증 메일 재전송

### 사용자/프로필

* `GET/PUT /applicant/account/getProfile` — 내 정보 조회/수정(이름/연락처/마케팅 동의)

### 기업

* `GET  /emp/check-bizRegNo?bizRegNo=...` — 사업자 중복 확인
* `POST /emp/registration-requests` — 등록 요청(FormData: `dto` + `file`)
* `PATCH /emp/registration-requests/{id}/approve|reject` — (관리자) 승인/반려
* `GET/PUT /emp/info/save` — 기업 정보 조회/수정
* `GET /emp/job-postings/manage` — 공고 필터 검색
* `POST /emp/job-postings/delete-bulk`—  기업공고일괄삭제
* `GET /emp/job-postings` —  기업공고 목록 조회
* `GET /emp/applications` — 지원자목록 조회
* `PUT /emp/applications/status` — 지원 상태 업데이트
* `GET /emp/applications/{applicationId}/preview` — 지원서 미리보기

### 공고/지원/스크랩

* `GET /job/filters/` - 공고 필터 검색
* `GET  /job/jobList` — 공고 목록/검색
* `POST /job/job-posting/new` — (기업) 공고 등록
* `PUT  /job/job-posting/update?id={id}` — (기업) 공고 수정
* `POST /applicant/application/job-postings/apply` — (지원자) 지원
* `GET  /emp/dashboard/stats/postings` — (기업) 내 공고 통계
* `GET  /emp/dashboard/stats/applicants` — (기업) 내 지원 통계

### 관리자

* `GET /admin/commonCode/getParentCodes` / `POST/PUT/DELETE`  —  공통코드 관리
* `GET /admin/applicant/getUsers` — 사용자 관리
* `GET /admin/menus` / `POST/PUT/DELETE` — 메뉴 관리
* `GET /admin/notice/getNotices` — 공지사항 관리
* `PUT /admin/notice/saveNotice/{id}` — 공지사항 저장
* `PUT /admin/notice/deleteNotice/{id}` — 공지사항 삭제
* `GET /admin/faq/getFaqs` / `POST/PUT/DELETE` — 자주 묻는 질문 관리
* `GET /admin/dashboard/stats/postings` — 총 등록공고수
* `GET /admin/dashboard/stats/applicants` — 총 지원자수
* `GET /admin/job-postings/manage` — 공고 관리

---

## 보안/인증

### JWT + 쿠키 + Redis

* **Access Token**: 짧은 TTL, API 보호(헤더/쿠키)
* **Refresh Token**: Redis 저장(`refresh:{userId}`) 및 재발급/강제 로그아웃
* **쿠키 정책(환경 분기)**

  * **prod**: `secure(true)` + `SameSite(None)` + HTTPS 필수
  * **dev/local**: `secure(false)` + `SameSite(Lax|Strict)`

Spring 예시:

```java
@Value("${app.cookie.secure:false}") boolean cookieSecure;
@Value("${app.cookie.same-site:Strict}") String cookieSameSite;

public ResponseCookie buildCookie(String name, String value, long maxAge) {
  return ResponseCookie.from(name, value)
    .httpOnly(true).path("/").maxAge(maxAge)
    .secure(cookieSecure).sameSite(cookieSameSite)
    .build();
}
```

---

## 프론트엔드

* **Next.js(React 18)**, **MUI**, **Tailwind**
* 정책/동의: Dialog + **iframe** (정적 파일)
* 파일 업로드: `useS3Upload` → S3 (서명 URL/멀티파트)
* 세션: `authContext.tsx` (+ 필요한 경우 `SessionWatcher.tsx`)
* 폼 유효성: 입력 즉시 포맷/정규식 검증(휴대폰/사업자번호 등)

---

## 로컬 실행

### Backend

```bash
# JDK 21, Redis, DB 준비
./gradlew bootRun   # http://localhost:8080
```

### Frontend

```bash
# Node 18+
npm install
npm run dev         # http://localhost:3000
```

---

## 환경 변수/설정

### `frontend/.env.local`

```env
NEXT_PUBLIC_API_BASE=http://localhost:8080
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

### `backend/src/main/resources/application.yml` (발췌)

```yaml
mybatis:
  mapper-locations: classpath:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

app:
  frontend:
    base-url: "http://localhost:3000/"
  redis:
    onboardingKeyPrefix: "onboarding:"
    onboardingTtlSeconds: 1800
  cookie:
    secure: true        # prod
    same-site: None     # prod

spring:
  profiles:
    active: local       # local/dev/prod 분리 운영
```

S3/DB/Redis 등 상세 설정은 각 환경 `application-*.yml`에 분리하세요.

---

## Swagger 문서

---

## 브랜치/릴리스

* 기본 흐름: `develop` → (PR) → `main`
* **Squash & merge** 권장(히스토리 간결)
* PR 체크리스트

  * [ ] Swagger 반영 확인
  * [ ] 쿠키/HTTPS/JWT TTL 점검
  * [ ] 마이그레이션/시드 스크립트 공지
  * [ ] 법정 고지 문서 버전 업데이트

---

## 권한/정책

* **Roles**

  * `USER`(= APPLICANT)
  * `EMPLOYER` / `ADMIN` / `EMPLOYER_PENDING`
  * `ADMIN`
* **기업 권한**

  * `ADMIN`: 기업 정보/회원/공고 전체 관리
  * `EMPLOYER`: 공고 등록/수정, 기업 정보 읽기
  * `EMPLOYER_PENDING` : 기업회원가입은 하였으나 아직 기업 승인 전 권한 없음

---

## 문의

* **[hayun.dev00@gmail.com](mailto:hayun.dev00@gmail.com)**, **[jhkimm96@gmail.com](mailto:jhkimm96@gmail.com)**

> 배포 전, 운영 환경에서는 HTTPS 강제, `secure(true)+SameSite(None)` 설정, Redis 토큰 TTL, 업로드 파일 검증(확장자·용량)을 반드시 점검하세요.
