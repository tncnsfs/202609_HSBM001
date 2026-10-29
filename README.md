# 202609_HSBM001
Home Service Business Model: 청소 서비스 항목 관리 모바일 웹앱 (테스트용)

## 기술 스택
- 프런트: Vue 3 + Vuetify 4 + TypeScript (Vite, 포트 3000)
- 백엔드: Spring Boot + MyBatis (Java 21, 포트 8080)
- DB: PostgreSQL 17 (Docker, 포트 5432)
- 로그인: Keycloak 26 (Docker, 포트 8081) + Google 로그인

## 폴더 구조
```text
frontend/    Vue 화면
backend/     Spring Boot API
dockers/     docker compose 파일, .env(비밀번호, git 제외), sql/
shscripts/   실행 스크립트 (dev.sh, docker.sh)
docs/        메모 (memo-notes.md)
```

## 로컬 개발 환경 (집 노트북)

```bash
# Windows 터미널(PowerShell)에서 프로젝트용 WSL로 들어가기
wsl -d Ubuntu-Target

# 프로젝트 폴더로 이동
cd /engn002/app/01_dev_homservice

# 한 번에 실행: DB + Keycloak → 백엔드(백그라운드) → 프런트(화면)
# 끌 때는 Ctrl+C 한 번 (백엔드도 같이 꺼짐)
./shscripts/dev.sh
```

- 백엔드 로그: `backend/target/backend.log`
- 처음 켤 때 백엔드가 뜨기 전(10~20초)엔 프런트에 `proxy error` 가 날 수 있음 → 잠시 후 새로고침

### Docker만 따로
```bash
./shscripts/docker.sh up      # 켜기 (postgres 먼저, 그다음 keycloak)
./shscripts/docker.sh down    # 끄기 (데이터는 안 지워짐)
./shscripts/docker.sh status  # 상태 보기
```

### 따로 실행하고 싶을 때
```bash
# 백엔드: 반드시 backend 폴더에서 (../dockers/.env 를 읽기 때문)
cd backend && ./mvnw spring-boot:run

# 프런트: 다른 터미널 탭에서
cd frontend && npm run dev
```

### 주소
| 이름 | 주소 |
|---|---|
| 화면 (Vue) | http://localhost:3000 |
| 백엔드 API | http://localhost:8080/api/items |
| Keycloak 관리자 | http://localhost:8081/admin (realm: xiaolin) |
| PostgreSQL | localhost:5432 (DB: xiaolin) |

### 참고
- `dockers/.env` 는 git에 안 올라감 (DB, Keycloak 비밀번호 보관용)
- PC방에서는 GitHub Codespaces 사용 (DB/.env 없음)

## 로그인 (Keycloak)
- 이메일 회원가입 + 인증 메일 (Gmail SMTP)
- Google 로그인 (Google Cloud 테스트 모드: 등록한 테스트 사용자만 가능)
- 화면: 목록은 누구나, 오른쪽 위 로그인/로그아웃 버튼

## API 설계 (서비스 항목)

| 기능 | 메서드 | 주소 | Vue 함수 |
|---|---|---|---|
| 목록 | GET | /api/items | 화면 열 때 |
| 상세 | GET | /api/items/{id} | (나중에 상세 화면) |
| 등록 | POST | /api/items | save() 신규 |
| 수정 | PUT | /api/items/{id} | save() 수정 |
| 삭제 | DELETE | /api/items/{id} | confirmDelete() |