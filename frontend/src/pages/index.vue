<template>
<!-- 위쪽 바: 제목 + 오른쪽에 로그인/로그아웃 -->
<v-app-bar title="서비스 항목">
  <!-- 로그인한 상태: 이름 + 로그아웃 버튼 -->
  <template v-if="loggedIn">
    <span class="mr-2">{{ userName }}님</span>
    <v-btn variant="outlined" class="mr-2" @click="logout">로그아웃</v-btn>
  </template>
  <!-- 로그인 안 한 상태: 로그인 버튼 -->
  <v-btn v-else variant="flat" color="primary" class="mr-2" @click="login">로그인</v-btn>
</v-app-bar>
<v-container class="pa-4">
  <v-text-field v-model="keyword" label="검색" prepend-inner-icon="mdi-magnify" variant="outlined" />
  <v-card
    v-for="item in filtered" :key="item.id"
    class="mb-2 pa-4" variant="outlined"
    @click="openEdit(item)"
  >
    <div class="d-flex align-center">
      <div class="flex-grow-1">
        <div class="text-h6">{{ item.name }}</div>
        <div class="text-medium-emphasis">{{ item.category }}</div>
        <div class="font-weight-bold">{{ item.price.toLocaleString() }}원</div>
      </div>
      <v-btn icon="mdi-delete" variant="text" color="error" @click.stop="askDelete(item)" />
    </div>
  </v-card>
</v-container>

<v-btn icon="mdi-plus" color="black" position="fixed" location="bottom right" class="ma-4" @click="openCreate" />

<!-- 등록 / 수정 팝업 -->
<v-dialog v-model="dialog" max-width="500">
  <v-card :title="editingId === null ? '서비스 등록' : '서비스 수정'">
    <v-card-text>
      <v-form ref="formRef">
        <v-text-field v-model="form.name" label="서비스명" :rules="[required]" />
        <v-select v-model="form.category" :items="categories" label="카테고리" :rules="[required]" />
        <v-text-field
          v-model.number="form.price" label="가격" type="number" suffix="원"
          :rules="[required, positive]"
        />
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn @click="dialog = false">취소</v-btn>
      <v-btn color="primary" variant="flat" @click="save">저장</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

<!-- 삭제 확인 팝업 -->
<v-dialog v-model="deleteDialog" max-width="400">
  <v-card title="삭제 확인">
    <v-card-text>'{{ deleteTarget?.name }}' 항목을 삭제할까요?</v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn @click="deleteDialog = false">취소</v-btn>
      <v-btn color="error" variant="flat" @click="confirmDelete">삭제</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import type { VForm } from 'vuetify/components'
// api/items.ts에서 만든 함수 4개와 ServiceItem 타입을 가져옴
// ../ = 한 폴더 위로 (pages → src), 그다음 api/items
import { fetchItems, createItem, updateItem, deleteItem, type ServiceItem } from '../api/items'

// Keycloak 연결 (src/auth/keycloak.ts 에서 만든 것)
import keycloak from '../auth/keycloak'

// 지금 로그인했는지 (true/false)
// 로그인/로그아웃 하면 페이지가 새로 열려서 그때마다 다시 계산돼요
const loggedIn = keycloak.authenticated === true

// 토큰 안의 사용자 정보에서 이름 꺼내기. 이름이 없으면 아이디(이메일)
const userName = keycloak.tokenParsed?.name ?? keycloak.tokenParsed?.preferred_username

// 로그인 버튼: Keycloak 로그인 화면으로 이동 (구글 버튼도 거기 있음)
function login () {
  keycloak.login()
}

// 로그아웃 버튼: Keycloak 세션 끝내고 우리 화면으로 돌아오기
function logout () {
  keycloak.logout({ redirectUri: 'http://localhost:3000' })
}

const keyword = ref('')
const categories = ['가전 청소', '청소', '이사', '수리']

// 서비스 목록을 담는 상자. 처음엔 빈 배열이고, 백엔드에서 받아와 채운다.
// ref = 값이 바뀌면 화면이 자동으로 다시 그려지는 "반응형" 변수
const items = ref<ServiceItem[]>([])

// 백엔드에서 목록을 가져오는 함수
// async = 안에서 서버 응답을 기다리는(await) 코드가 있다는 표시
async function load () {
  // items.ts의 fetchItems()가 GET 요청 + JSON 변환까지 해 줌
  items.value = await fetchItems()
}

// 화면이 처음 뜰 때 load()를 한 번 실행
onMounted(load)

const filtered = computed(() =>
  items.value.filter(i => i.name.includes(keyword.value))
)

// 검증 규칙
const required = (v: unknown) =>
  (v !== null && v !== undefined && String(v).trim() !== '') || '필수 입력입니다'
const positive = (v: number) => v > 0 || '0보다 커야 합니다'

// 삭제 (확인 팝업)
const deleteDialog = ref(false)
const deleteTarget = ref<ServiceItem | null>(null)

function askDelete(item: ServiceItem) {
  deleteTarget.value = item
  deleteDialog.value = true
}

// 삭제 팝업에서 [삭제] 버튼을 눌렀을 때 실행
// async = 서버 응답을 기다려야 해서 붙임
async function confirmDelete () {
  if (deleteTarget.value) {
    // items.ts의 deleteItem()이 DELETE 요청을 보냄
    await deleteItem(deleteTarget.value.id)
    // 백엔드에서 최신 목록을 다시 받아와 화면 갱신
    await load()
  }
  // 팝업 닫기
  deleteDialog.value = false
}

// 등록 / 수정 공용
const dialog = ref(false)
const formRef = ref<InstanceType<typeof VForm> | null>(null)
const editingId = ref<number | null>(null)
const form = ref({ name: '', category: '', price: 0 })

function openCreate() {
  editingId.value = null
  form.value = { name: '', category: '', price: 0 }
  dialog.value = true
}

function openEdit(item: ServiceItem) {
  editingId.value = item.id
  form.value = { name: item.name, category: item.category, price: item.price }
  dialog.value = true
}

// 팝업에서 [저장] 버튼을 눌렀을 때 실행 (등록과 수정을 함께 처리)
async function save () {
  // ?. = formRef가 비어 있으면(null) 멈추지 않고 그냥 undefined를 돌려줌
  const result = await formRef.value?.validate()
  // 검사 결과가 없거나 통과 못 하면 저장하지 않고 멈춤
  if (!result?.valid) return

   // editingId가 null이면 새 항목 → 등록, 아니면 그 id로 수정
  if (editingId.value === null) {
    await createItem(form.value)
  } else {
    await updateItem(editingId.value, form.value)
  }
    
  // 백엔드에서 최신 목록을 다시 받아와 화면 갱신
  await load()
  // 팝업 닫기
  dialog.value = false
}
</script>