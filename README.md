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
