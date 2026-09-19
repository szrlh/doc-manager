<template>
  <el-tree-select
    v-model="selectedCategoryId"
    :data="categoryStore.categoryTree"
    :props="{ label: 'name', children: 'children', value: 'id' }"
    node-key="id"
    placeholder="请选择分类"
    clearable
    check-strictly
    :render-after-expand="false"
    style="width: 100%"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useCategoryStore } from '@/stores/category'

const props = defineProps<{
  modelValue: number | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: number | null): void
}>()

const categoryStore = useCategoryStore()

const selectedCategoryId = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})
</script>