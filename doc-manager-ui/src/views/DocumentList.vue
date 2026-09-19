<template>
  <div class="document-list">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索文档标题"
        clearable
        style="width: 300px; margin-right: 10px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="importVisible = true">导入文档</el-button>
    </div>

    <el-table :data="documentList" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="fileType" label="类型" width="80" />
      <el-table-column prop="sectionCount" label="片段数" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToSections(row.id)">片段</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; justify-content: flex-end"
      @size-change="loadDocuments"
      @current-change="loadDocuments"
    />

    <!-- 导入文档对话框 -->
    <ImportDocument v-model:visible="importVisible" @success="loadDocuments" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDocumentPage, deleteDocument } from '@/api/document'
import type { DocumentItem } from '@/types'
import ImportDocument from '@/components/ImportDocument.vue'

const router = useRouter()
const loading = ref(false)
const documentList = ref<DocumentItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const importVisible = ref(false)

async function loadDocuments() {
  loading.value = true
  try {
    const result = await getDocumentPage({
      page: currentPage.value - 1, // 后端页码从0开始
      size: pageSize.value,
      keyword: keyword.value || undefined
    })
    documentList.value = result.records
    total.value = result.total
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadDocuments()
}

function goToSections(documentId: number) {
  router.push(`/documents/${documentId}/sections`)
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该文档及其所有片段吗？', '警告', {
      type: 'warning'
    })
    await deleteDocument(id)
    ElMessage.success('删除成功')
    loadDocuments()
  } catch (e) {
    // 取消或错误
  }
}

onMounted(() => {
  loadDocuments()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>