<template>
  <div class="search-page">
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="输入关键词搜索片段（标题优先）"
        clearable
        style="width: 400px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch" style="margin-left: 10px">搜索</el-button>
    </div>

    <el-table :data="searchResults" v-loading="loading" border>
      <el-table-column label="片段标题" min-width="150">
        <template #default="{ row }">
          {{ row.sectionTitle || '（无标题）' }}
        </template>
      </el-table-column>
      <el-table-column prop="contentSnippet" label="内容摘要" min-width="250" show-overflow-tooltip />
      <el-table-column prop="documentTitle" label="所属文档" min-width="150" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="相关度" width="90">
        <template #default="{ row }">
          <el-tag :type="row.relevance === 2 ? 'danger' : 'info'" size="small">
            {{ row.relevance === 2 ? '标题' : '正文' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row.sectionId)">查看详情</el-button>
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
      @size-change="handleSearch"
      @current-change="handleSearch"
    />

    <el-empty v-if="!loading && searchResults.length === 0" description="暂无搜索结果" />

    <!-- 片段详情抽屉 -->
    <el-drawer v-model="detailVisible" title="片段详情" size="50%">
      <div class="detail-content">
        <MarkdownRenderer v-if="detailSection?.fileType === 'md'" :content="detailSection.originalContent" />
        <pre v-else class="plain-text">{{ detailSection?.content }}</pre>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchSections } from '@/api/search'
import { getSectionDetail } from '@/api/section'
import type { SectionItem, SearchResultItem } from '@/types'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'

const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const searchResults = ref<SearchResultItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const detailVisible = ref(false)
const detailSection = ref<SectionItem | null>(null)

async function handleSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const result = await searchSections({
      keyword: keyword.value,
      page: currentPage.value - 1, // 后端页码从0开始
      size: pageSize.value
    })
    searchResults.value = result.records
    total.value = result.total
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

async function viewDetail(sectionId: number) {
  try {
    detailSection.value = await getSectionDetail(sectionId)
    detailVisible.value = true
  } catch (e) {
    // 错误已处理
  }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.detail-content {
  padding: 0 20px;
}
.plain-text {
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  margin: 0;
}
</style>