-- Keycloak 전용 사용자 + DB (같은 postgres 서버 안, 앱 DB xiaolin 과 분리)
-- 비밀번호는 저장소가 공개라 여기 적지 않음. 실행할 때 dockers/.env 의 KC_DB_PASSWORD 값으로 바꿔서 실행
CREATE USER keycloak WITH PASSWORD 'admin123';
CREATE DATABASE keycloak OWNER keycloak;