#!/usr/bin/env bash
# Docker 컨테이너(postgres + keycloak) 한 번에 관리
# 사용법 (프로젝트 루트에서):
#   ./shscripts/docker.sh up      → 켜기 (postgres 먼저, 그다음 keycloak)
#   ./shscripts/docker.sh down    → 끄기 (keycloak 먼저, 그다음 postgres. 데이터는 안 지워짐)
#   ./shscripts/docker.sh status  → 상태 보기

# 명령이 하나라도 실패하면 바로 멈춤
set -e

# 스크립트가 shscripts/ 안에 있으니 한 칸 위(..) = 프로젝트 루트로 이동
cd "$(dirname "$0")/.."

# 파일 경로를 변수로 (이름 바뀌면 여기만 고치면 됨)
DB_FILE=dockers/docker-compose-servicedb_postgresql.yml
KC_FILE=dockers/docker-compose-keycloak.yml

# 첫 번째 인자(up/down/status)에 따라 다르게 동작. 안 주면 up
case "${1:-up}" in
  up)
    # postgres 가 xiaolin-net 네트워크를 만들기 때문에 반드시 먼저
    docker compose -f "$DB_FILE" up -d
    docker compose -f "$KC_FILE" up -d
    echo "켜짐: postgres(5432), keycloak(http://localhost:8081)"
    ;;
  down)
    # keycloak 이 postgres 를 쓰고 있으니 keycloak 먼저 끄기
    docker compose -f "$KC_FILE" down
    docker compose -f "$DB_FILE" down
    echo "꺼짐 (데이터는 그대로)"
    ;;
  status)
    # 우리 컨테이너만 골라서 보기
    docker ps -a --filter "name=xiaolin-"
    ;;
  *)
    # 잘못 입력했을 때 사용법 안내
    echo "사용법: ./shscripts/docker.sh [up|down|status]"
    exit 1
    ;;
esac