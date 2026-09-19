<template>
  <el-select
    v-model="selectedTagIds"
    multiple
    filterable
    placeholder="选择标签"
    style="width: 100%"
  >
    <el-option
      v-for="tag in tagStore.tags"
      :key="tag.id"
      :label="tag.name"
      :value="tag.id"
    />
  </el-select>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useTagStore } from '@/stores/tag'

const props = defineProps<{
  modelValue: number[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: number[]): void
}>()

const tagStore = useTagStore()

const selectedTagIds = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})
</script>