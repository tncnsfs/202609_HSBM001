#!/usr/bin/env bash
# 로컬 개발 서버 한 번에 실행: DB → 백엔드(백그라운드) → 프런트(화면)
# 사용법: 프로젝트 루트에서 ./shscripts/dev.sh   (끌 때는 Ctrl+C 한 번)

# 명령이 하나라도 실패하면 바로 멈춤
set -e

# 스크립트가 shscripts/ 안에 있으니 한 칸 위(..) = 프로젝트 루트로 이동
cd "$(dirname "$0")/.."

# 1. DB + Keycloak 실행은 docker.sh 에 맡김 (이미 떠 있으면 그냥 넘어감)
./shscripts/docker.sh up

# 2. 백엔드를 백그라운드(&)로 실행. 로그는 화면 대신 파일로
#    backend 폴더 안에서 실행해야 ../dockers/.env 를 찾음
#    로그 위치 target/ 은 git에 안 올라가는 폴더
(cd backend && ./mvnw spring-boot:run > target/backend.log 2>&1) &
# 방금 띄운 백엔드의 프로세스 번호 기억
BACK_PID=$!
echo "백엔드 시작 (로그: backend/target/backend.log)"

# 3. Ctrl+C 로 스크립트가 끝날 때 백엔드도 같이 끄기
#    pkill -P: 백엔드가 띄운 자식 프로세스(Java)까지 정리
trap 'echo "종료 중..."; pkill -P $BACK_PID 2>/dev/null; kill $BACK_PID 2>/dev/null' EXIT

# 4. 프런트는 앞(화면)에서 실행 → 이 터미널에 프런트 로그가 보임
cd frontend && npm run dev