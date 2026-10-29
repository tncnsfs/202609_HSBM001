// Keycloak 연결 설정 한 곳에 모아 둔 파일
import Keycloak from 'keycloak-js'

// 어느 Keycloak, 어느 realm, 어느 클라이언트로 로그인할지
const keycloak = new Keycloak({
  url: 'http://localhost:8081', // Keycloak 서버 주소
  realm: 'xiaolin', // 우리 회원들이 있는 realm
  clientId: 'xiaolin-front', // 방금 만든 클라이언트 이름
})

// 앱 시작할 때 한 번 호출: 이미 로그인돼 있는지 조용히 확인
export async function initKeycloak () {
  await keycloak.init({
    onLoad: 'check-sso', // 로그인 강제 X. 로그인돼 있으면 정보만 가져옴 (목록은 누구나 봄)
    pkceMethod: 'S256', // 클라이언트 설정과 같은 일회용 암호 방식
    checkLoginIframe: false, // 로컬 개발에서 자주 나는 iframe 경고 끄기
  })
}

// 다른 파일(화면, API)에서 keycloak.login() 등을 쓰려고 내보냄
export default keycloak