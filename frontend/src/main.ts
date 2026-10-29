/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Composables
import { createApp } from 'vue'

// Plugins
import { registerPlugins } from '@/plugins'

// Components
import App from './App.vue'

// Keycloak: 로그인 상태 확인 함수 (방금 만든 파일)
import { initKeycloak } from '@/auth/keycloak'

// Styles
import 'unfonts.css'

const app = createApp(App)

registerPlugins(app)

// 로그인 상태를 먼저 확인한 다음 화면을 띄움
// 왜: 화면이 뜰 때 "로그인했는지"를 이미 알고 있어야 버튼을 맞게 보여줄 수 있어요
initKeycloak()
  .catch(err => {
    // Keycloak 이 꺼져 있어도 목록 화면은 보이게 (로그인만 안 됨)
    console.error('Keycloak 연결 실패', err)
  })
  .finally(() => {
    // 성공이든 실패든 화면은 띄움
    app.mount('#app')
  })