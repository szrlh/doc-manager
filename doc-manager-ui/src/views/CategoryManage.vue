<template>
  <div class="category-manage">
    <div class="toolbar">
      <el-input
        v-model="filterKeyword"
        placeholder="搜索分类名称"
        clearable
        style="width: 250px; margin-right: 10px"
      />
      <el-button type="primary" @click="openCreateDialog">新增分类</el-button>
      <el-button @click="loadCategories">刷新</el-button>
    </div>

    <!-- 表格绑定过滤后的树 -->
    <el-table
      :data="filteredCategoryTree"
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children' }"
      border
      default-expand-all
    >
      <el-table-column prop="name" label="分类名称" min-width="200" />
      <el-table-column label="规则关键词" min-width="250">
        <template #default="{ row }">
          <el-tag v-for="kw in parseKeywords(row.ruleKeywords)" :key="kw" size="small" style="margin-right: 4px">
            {{ kw }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingCategory ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryTree"
            :props="{ label: 'name', children: 'children', value: 'id' }"
            node-key="id"
            check-strictly
            clearable
            placeholder="不选择则为顶级分类"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="分类描述（可选）" />
        </el-form-item>
        <el-form-item label="规则关键词">
          <el-input v-model="form.ruleKeywordsStr" type="textarea" placeholder="逗号分隔关键词，例如：java, spring boot, 数据库" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCategoryStore } from '@/stores/category'
import { createCategory, updateCategory, deleteCategory } from '@/api/category'
import type { Category, CategoryTreeVO } from '@/types'

const categoryStore = useCategoryStore()
const loading = ref(false)
const categoryTree = ref<CategoryTreeVO[]>([])
const dialogVisible = ref(false)
const editingCategory = ref<CategoryTreeVO | null>(null)

const form = ref({
  id: undefined as number | undefined,
  name: '',
  parentId: null as number | null,
  description: '',
  ruleKeywordsStr: ''
})

async function loadCategories() {
  loading.value = true
  try {
    await categoryStore.fetchCategoryTree()
    categoryTree.value = categoryStore.categoryTree
  } catch (e) {
    // 错误已处理
  } finally {
    loading.value = false
  }
}

function parseKeywords(ruleKeywords?: string | null): string[] {
  if (!ruleKeywords) return []
  try {
    return JSON.parse(ruleKeywords)
  } catch {
    return []
  }
}

function openCreateDialog() {
  editingCategory.value = null
  form.value = {
    id: undefined,
    name: '',
    parentId: null,
    description: '',
    ruleKeywordsStr: ''
  }
  dialogVisible.value = true
}

function openEditDialog(row: CategoryTreeVO) {
  editingCategory.value = row
  // 需要从扁平分类中获取完整信息（包含 ruleKeywords）
  const fullCategory = categoryStore.categories.find(c => c.id === row.id)
  form.value = {
    id: row.id,
    name: row.name,
    parentId: fullCategory?.parentId ?? null,
    description: fullCategory?.description ?? '',
    ruleKeywordsStr: parseKeywords(fullCategory?.ruleKeywords).join(', ')
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.value.name.trim()) {
    ElMessage.warning('分类名称不能为空')
    return
  }

  // 将逗号分隔字符串转换为 JSON 数组
  const keywords = form.value.ruleKeywordsStr
    .split(/[,，]/)
    .map(k => k.trim())
    .filter(k => k.length > 0)
  const ruleKeywordsJson = keywords.length > 0 ? JSON.stringify(keywords) : null

  const payload: Partial<Category> = {
    name: form.value.name.trim(),
    parentId: form.value.parentId,
    description: form.value.description || null,
    ruleKeywords: ruleKeywordsJson
  }

  try {
    if (form.value.id) {
      await updateCategory(form.value.id, payload)
      ElMessage.success('分类更新成功')
    } else {
      await createCategory(payload)
      ElMessage.success('分类创建成功')
    }
    dialogVisible.value = false
    await categoryStore.refreshAll()
    categoryTree.value = categoryStore.categoryTree
  } catch (e) {
    // 错误已处理
  }
}

async function handleDelete(row: CategoryTreeVO) {
  try {
    await ElMessageBox.confirm(`确认删除分类“${row.name}”吗？`, '警告', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    await categoryStore.refreshAll()
    categoryTree.value = categoryStore.categoryTree
  } catch (e) {
    // 取消或错误
  }
}

onMounted(() => {
  loadCategories()
})

const filterKeyword = ref('')

const filteredCategoryTree = computed(() => {
  if (!filterKeyword.value) return categoryStore.categoryTree
  const keyword = filterKeyword.value.toLowerCase()
  const filterNode = (nodes: CategoryTreeVO[]): CategoryTreeVO[] => {
    const result: CategoryTreeVO[] = []
    for (const node of nodes) {
      const children = node.children ? filterNode(node.children) : []
      if (node.name.toLowerCase().includes(keyword) || children.length > 0) {
        result.push({ ...node, children })
      }
    }
    return result
  }
  return filterNode(categoryStore.categoryTree)
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
</style>