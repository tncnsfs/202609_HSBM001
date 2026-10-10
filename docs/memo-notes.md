# 개발 메모 (HSBM001)

## 1-1. 프론트 ↔ 백엔드 연결 (2026-10-10)

### 개념
- 서버 2개: 3000 = Vite(화면 파일), 8080 = Spring Boot(데이터 JSON)
- CORS: 브라우저는 다른 포트로 직접 요청하면 막는다
- 프록시: Vite가 /api 요청을 8080에 대신 전달 → 브라우저는 3000하고만 대화
- ref: 값이 바뀌면 화면이 자동으로 다시 그려지는 변수
- onMounted: 화면이 처음 뜰 때 한 번 실행
- async/await: 서버 응답을 기다리는 코드. await는 async 함수 안에서만 사용
- 백엔드 데이터는 아직 메모리(store Map)에 있음 → 재시작하면 초기 3개로 돌아감
- AtomicLong: 동시에 요청이 와도 id가 겹치지 않게 번호를 매기는 Java 기본 기능

### 바꾼 파일
- frontend/vite.config.mts: proxy: { '/api': 'http://localhost:8080' }
- frontend/src/pages/index.vue: load(GET), confirmDelete(DELETE), save(POST/PUT)

### fetch 패턴
- 조회: await fetch('/api/items') → await res.json()
- 삭제: fetch(`/api/items/${id}`, { method: 'DELETE' })
- 등록/수정: method 'POST' 또는 'PUT' + headers Content-Type JSON + body JSON.stringify(...)
- 끝나면 load()로 목록을 다시 받아 화면 갱신

### VS Code 단축키
- Ctrl+P: 파일 이름으로 열기
- Ctrl+F: 파일 안에서 찾기
- Ctrl+Shift+F: 프로젝트 전체에서 찾기
- Ctrl+G: 줄 번호로 이동

### git 명령어
- git status: 바뀐 파일 확인
- git add 파일1 파일2: 이번 저장에 담기
- git commit -m "메시지": 저장(내 PC에만)
- git config --global user.name / user.email: 작성자 등록(최초 1번)



## 1-2. API 호출 분리 (2026-10-10)

### 개념
- 화면(index.vue)과 서버 호출(api/items.ts)을 나눔 → 주소/토큰이 바뀌면 items.ts만 고침
- export / import: 다른 파일에서 쓰게 내보내고 / 가져옴. 확장자 .ts는 생략
- .vue 파일 = template(화면) + script(동작, lang="ts") + style(꾸미기)
- TypeScript = JavaScript + 타입 표시. 실행 전에 실수를 빨간 줄로 알려 줌
- Omit<타입, '필드'>: 그 필드만 뺀 새 타입 (등록할 땐 id가 없으니까)
- Promise<T>: 기다리면 T를 돌려준다는 표시
- res.ok: 응답 코드가 200번대인지. 아니면 throw로 에러를 알림
- HTTP method: GET 조회 / POST 등록 / PUT 수정 / DELETE 삭제 (생략하면 GET)
- 세미콜론: TS/Vue는 선택(이 프로젝트는 안 씀), Java는 필수

### 바꾼 파일
- frontend/src/api/items.ts (새 파일): fetchItems, createItem, updateItem, deleteItem
- frontend/src/pages/index.vue: fetch 직접 호출 → items.ts 함수 호출, interface 삭제


## 1-3. Docker + PostgreSQL (2026-10-11)

### 개념
- Docker: 프로그램이 설치된 "상자(컨테이너)"를 받아서 켜고 끄기만 함. WSL 리눅스 안에 설치(/var/lib/docker)
- docker compose: 상자를 어떻게 띄울지 적은 설계도(yml). yml은 들여쓰기(스페이스)가 문법, 
`이름: 값`
- 포트 연결 "5432:5432": 내 PC 포트 → 상자 포트. WSL2가 Windows localhost로 넘겨줘서 DBeaver 접속 가능
- 바인드 마운트: DB 데이터를 내가 정한 폴더(/engn002/data002/postgres)에 저장 → 상자를 지워도 데이터 유지
- networks: 아래쪽 = 네트워크 만들기, 서비스 안 = 그 네트워크에 연결. 상자끼리는 서비스 이름으로 찾음
- .env: 비밀번호 보관. .gitignore로 git 제외. compose와 Spring Boot가 같은 파일을 읽음
- IDENTITY 컬럼: DB가 id를 자동으로 매김 (AtomicLong 대신)

### 파일
- dockers/docker-compose-dev.yml, dockers/.env(git 제외), .gitignore
- backend/pom.xml: postgresql 드라이버, spring-boot-starter-jdbc
- backend/src/main/resources/application.properties: spring.config.import로 .env 읽고 datasource 설정

### 명령어
- docker compose -f dockers/docker-compose-dev.yml up -d : 켜기 (down: 끄기)
- docker ps / docker logs 이름 --tail 5 / docker network ls
- git check-ignore -v 파일 : git이 무시하는지 확인
- ss -ltnp | grep 8080 : 포트를 누가 쓰는지 확인


## 1-4. MyBatis로 DB 연동 (XML 매퍼)
- pom.xml: mybatis-spring-boot-starter 추가
- application.properties
  - mybatis.mapper-locations=classpath:mapper/*.xml  → XML 위치 지정
  - mybatis.configuration.map-underscore-to-camel-case=true → created_at ↔ createdAt 자동 변환
- ServiceItemMapper.java: @Mapper 인터페이스 (메서드 이름 = XML의 id)
- resources/mapper/ServiceItemMapper.xml: 실제 SQL 작성
  - INSERT ... RETURNING id 로 새 id 받기
- ItemController: 메모리 저장소 제거 → mapper 사용 (생성자 주입)
- 확인: 로그에 HikariPool 시작 + curl 결과가 DB 데이터와 같음
- 이제 백엔드 재시작해도 데이터가 남음 (DB에 저장되니까)

