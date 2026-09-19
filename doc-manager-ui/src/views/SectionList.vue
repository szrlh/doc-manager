<template>
  <div class="section-list">
    <el-page-header @back="router.back()" :content="`文档片段 - ${documentTitle}`" style="margin-bottom: 20px" />

    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索片段标题或内容" clearable style="width: 250px; margin-right: 10px" />
      <el-select v-model="filterCategoryId" placeholder="选择分类" clearable style="width: 180px; margin-right: 10px">
        <el-option
          v-for="cat in flattenCategories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
      <el-select v-model="filterTagId" placeholder="选择标签" clearable style="width: 180px; margin-right: 10px">
        <el-option
          v-for="tag in tagStore.tags"
          :key="tag.id"
          :label="tag.name"
          :value="tag.id"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">筛选</el-button>
    </div>

    <el-table :data="sectionList" v-loading="loading" border>
      <el-table-column prop="orderIndex" label="序号" width="60" />
      <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="content" label="摘要" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column label="标签" min-width="150">
        <template #default="{ row }">
          <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin-right: 4px">{{ tag }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">查看</el-button>
          <el-button link type="warning" @click="editSection(row)">编辑</el-button>
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
      @size-change="loadSections"
      @current-change="loadSections"
    />

    <!-- 片段详情抽屉 -->
    <el-drawer v-model="detailVisible" title="片段详情" size="50%">
      <div class="detail-content">
        <MarkdownRenderer v-if="detailSection?.fileType === 'md' && detailSection.originalContent" :content="detailSection.originalContent" />
        <pre v-else class="plain-text">{{ detailSection?.content }}</pre>
      </div>
    </el-drawer>

    <!-- 编辑片段对话框 -->
    <el-dialog v-model="editVisible" title="编辑片段" width="60%">
      <el-form v-if="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="片段标题" />
        </el-form-item>
        <el-form-item label="分类">
          <CategorySelect v-model="editForm.categoryId" />
        </el-form-item>
        <el-form-item label="标签">
          <TagSelect v-model="editForm.tagIds" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.originalContent" type="textarea" :rows="12" placeholder="Markdown 或纯文本内容" />
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSectionPage, updateSection, deleteSection } from '@/api/section'
import type { SectionItem } from '@/types'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import CategorySelect from '@/components/CategorySelect.vue'
import TagSelect from '@/components/TagSelect.vue'
import { useCategoryStore } from '@/stores/category'
import { useTagStore } from '@/stores/tag'

const route = useRoute()
const router = useRouter()
const categoryStore = useCategoryStore()
const tagStore = useTagStore()

const documentId = Number(route.params.id)
const documentTitle = ref('')
const loading = ref(false)
const sectionList = ref<SectionItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterCategoryId = ref<number | null>(null)
const filterTagId = ref<number | null>(null)

const detailVisible = ref(false)
const detailSection = ref<SectionItem | null>(null)
const editVisible = ref(false)
const editForm = ref<{ id: number; title: string | null; originalContent: string; categoryId: number | null; tagIds: number[]; } | null>(null)

const flattenCategories = computed(() => {
  const result: { id: number; name: string }[] = []
  const flatten = (nodes: any[]) => {
    nodes.forEach(node => {
      result.push({ id: node.id, name: node.name })
      if (node.children && node.children.length) flatten(node.children)
    })
  }
  flatten(categoryStore.categoryTree)
  return result
})

async function loadSections() {
  loading.value = true
  try {
    const result = await getSectionPage({
      page: currentPage.value - 1,
      size: pageSize.value,
      documentId,
      categoryId: filterCategoryId.value || undefined,
      tagId: filterTagId.value || undefined,
      keyword: keyword.value || undefined
    })
    sectionList.value = result.records
    total.value = result.total
    // 设置文档标题
    if (result.records.length > 0) {
      documentTitle.value = result.records[0]?.documentTitle ?? ''
    } else {
      documentTitle.value = ''
    }
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadSections()
}

function viewDetail(row: SectionItem) {
  detailSection.value = row
  detailVisible.value = true
}

function editSection(row: SectionItem) {
  editForm.value = {
    id: row.id,
    title: row.title,
    originalContent: row.originalContent,
    categoryId: row.categoryId,
    tagIds: [...row.tagIds]  // 复制数组
  }
  editVisible.value = true
}

async function submitEdit() {
  if (!editForm.value) return
  try {
    await updateSection(editForm.value.id, {
      originalContent: editForm.value.originalContent,
      title: editForm.value.title || undefined,
      categoryId: editForm.value.categoryId,
      tagIds: editForm.value.tagIds
    })
    ElMessage.success('更新成功')
    editVisible.value = false
    loadSections()
  } catch (e) {
    // 错误已处理
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确认删除该片段吗？', '警告', { type: 'warning' })
    await deleteSection(id)
    ElMessage.success('删除成功')
    loadSections()
  } catch (e) {
    // 取消或错误
  }
}

onMounted(() => {
  loadSections()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
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