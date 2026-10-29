# 202609_HSBM001
Home Service Business Model

git add . && git commit -m "20260927-서비스 항목 CRUD (프론트 메모리)" && git push

## 프로젝트 받기 : backend : spring-boot curl 로 받기 
curl https://start.spring.io/starter.zip -d type=maven-project -d language=java -d javaVersion=21 -d groupId=com.hsbm -d artifactId=backend -d packageName=com.hsbm.backend -d dependencies=web,validation,devtools -o backend.zip

## 실행 :  backend : spring-boot 
cd backend
./mvnw spring-boot:run



## API 설계 (서비스 항목)

기능	메서드	주소	Vue 함수
목록	GET	/api/items	화면 열 때
상세	GET	/api/items/{id}	(나중에 상세 화면)
등록	POST	/api/items	save() 신규
수정	PUT	/api/items/{id}	save() 수정
삭제	DELETE	/api/items/{id}	confirmDelete()





## 로컬 개발 환경 (집 노트북)

<!-- Windows 터미널(PowerShell)에서 프로젝트용 WSL로 들어가기 -->
    wsl -d Ubuntu-Target

<!-- 프로젝트 폴더로 이동 -->
    cd /engn002/app/01_dev_homservice

<!-- DB(PostgreSQL) 실행: 프로젝트 루트에서 -->
    docker compose -f dockers/docker-compose-dev.yml up -d

<!-- 백엔드 실행: 반드시 backend 폴더에서 (../dockers/.env 를 읽기 때문) -->
    cd backend && ./mvnw spring-boot:run

<!-- 프런트 실행: 다른 터미널 탭에서 → http://localhost:3000 -->
    cd frontend && npm run dev

- dockers/.env 는 git에 안 올라감 (DB 비밀번호 보관용)
- PC방에서는 GitHub Codespaces 사용 (DB/.env 없음)