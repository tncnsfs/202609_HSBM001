<template>
<v-app-bar title="서비스 항목" />
<v-container class="pa-4">
  <v-text-field v-model="keyword" label="검색" prepend-inner-icon="mdi-magnify" variant="outlined" />
  <v-card v-for="item in filtered" :key="item.id" class="mb-2 pa-4" variant="outlined">
    <div class="text-h6">{{ item.name }}</div>
    <div class="text-medium-emphasis">{{ item.category }}</div>
    <div class="font-weight-bold">{{ item.price.toLocaleString() }}원</div>
  </v-card>
</v-container>
<v-btn icon="mdi-plus" color="black" position="fixed" location="bottom right" class="ma-4" />
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
const keyword = ref('')

const items = [
  { id: 1, name: '에어컨 청소', category: '가전 청소', price: 50000 },
  { id: 2, name: '욕실 청소', category: '청소', price: 80000 },
  { id: 3, name: '이사 청소', category: '청소', price: 90000 },
]
const filtered = computed(() =>
  items.filter(i => i.name.includes(keyword.value))
)

</script>
