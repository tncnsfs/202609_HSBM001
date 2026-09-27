<template>
<v-app-bar title="서비스 항목" />
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
      <v-btn icon="mdi-delete" variant="text" color="error" @click.stop="remove(item.id)" />
    </div>
  </v-card>
</v-container>

<v-btn icon="mdi-plus" color="black" position="fixed" location="bottom right" class="ma-4" @click="openCreate" />

<!-- 등록 / 수정 팝업 -->
<v-dialog v-model="dialog" max-width="500">
  <v-card :title="editingId === null ? '서비스 등록' : '서비스 수정'">
    <v-card-text>
      <v-text-field v-model="form.name" label="서비스명" />
      <v-select v-model="form.category" :items="categories" label="카테고리" />
      <v-text-field v-model.number="form.price" label="가격" type="number" suffix="원" />
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn @click="dialog = false">취소</v-btn>
      <v-btn color="primary" variant="flat" @click="save">저장</v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'

interface ServiceItem {
  id: number
  name: string
  category: string
  price: number
}

const keyword = ref('')
const categories = ['가전 청소', '청소', '이사', '수리']

const items = ref<ServiceItem[]>([
  { id: 1, name: '에어컨 청소', category: '가전 청소', price: 50000 },
  { id: 2, name: '욕실 청소', category: '청소', price: 80000 },
  { id: 3, name: '이사 청소', category: '청소', price: 90000 },
])
let nextId = 4

const filtered = computed(() =>
  items.value.filter(i => i.name.includes(keyword.value))
)

// 삭제
function remove(id: number) {
  items.value = items.value.filter(i => i.id !== id)
}

// 등록 / 수정 공용
const dialog = ref(false)
const editingId = ref<number | null>(null)   // null = 등록, 숫자 = 수정 중인 id
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

function save() {
  if (editingId.value === null) {
    // 등록
    items.value.push({ id: nextId++, ...form.value })
  } else {
    // 수정
    const idx = items.value.findIndex(i => i.id === editingId.value)
    items.value[idx] = { id: editingId.value, ...form.value }
  }
  dialog.value = false
}
</script>