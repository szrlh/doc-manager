<template>
  <div class="tag-manage">
    <div class="toolbar">
      <el-input v-model="newTagName" placeholder="输入新标签名称" style="width: 250px" />
      <el-button type="primary" @click="handleCreate" style="margin-left: 10px">添加标签</el-button>
      <el-button @click="loadTags">刷新</el-button>
    </div>

    <el-table :data="tagStore.tags" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="标签名称" min-width="200" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑标签对话框 -->
    <el-dialog v-model="editVisible" title="编辑标签" width="400px">
      <el-form label-width="80px">
        <el-form-item label="标签名称">
          <el-input v-model="editTagName" placeholder="请输入新标签名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTagStore } from '@/stores/tag'
import { createTag, deleteTag, updateTag } from '@/api/tag'
import type { Tag } from '@/types'

const tagStore = useTagStore()
const loading = ref(false)
const newTagName = ref('')
const editVisible = ref(false)
const editTagId = ref<number | null>(null)
const editTagName = ref('')

async function loadTags() {
  loading.value = true
  try {
    await tagStore.fetchTags()
  } catch (e) {
    // 错误已处理
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  const name = newTagName.value.trim()
  if (!name) {
    ElMessage.warning('标签名称不能为空')
    return
  }
  try {
    await createTag(name)
    ElMessage.success('标签创建成功')
    newTagName.value = ''
    await tagStore.fetchTags()
  } catch (e) {
    // 错误已处理
  }
}

function openEditDialog(tag: Tag) {
  editTagId.value = tag.id
  editTagName.value = tag.name
  editVisible.value = true
}

async function submitEdit() {
  const name = editTagName.value.trim()
  if (!name) {
    ElMessage.warning('标签名称不能为空')
    return
  }
  try {
    await updateTag(editTagId.value!, name)
    ElMessage.success('标签更新成功')
    editVisible.value = false
    await tagStore.fetchTags()
  } catch (e) {
    // 错误已显示
  }
}

async function handleDelete(row: Tag) {
  try {
    await ElMessageBox.confirm(`确认删除标签“${row.name}”吗？`, '警告', { type: 'warning' })
    await deleteTag(row.id)
    ElMessage.success('删除成功')
    await tagStore.fetchTags()
  } catch (e) {
    // 取消或错误
  }
}

onMounted(() => {
  loadTags()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>