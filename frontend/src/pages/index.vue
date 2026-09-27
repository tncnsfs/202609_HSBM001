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
import { ref, computed } from 'vue'
import type { VForm } from 'vuetify/components'

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

function confirmDelete() {
  if (deleteTarget.value) {
    const id = deleteTarget.value.id
    items.value = items.value.filter(i => i.id !== id)
  }
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

async function save() {
  const result = await formRef.value?.validate()
  if (!result?.valid) return

  if (editingId.value === null) {
    items.value.push({ id: nextId++, ...form.value })
  } else {
    const idx = items.value.findIndex(i => i.id === editingId.value)
    items.value[idx] = { id: editingId.value, ...form.value }
  }
  dialog.value = false
}
</script>