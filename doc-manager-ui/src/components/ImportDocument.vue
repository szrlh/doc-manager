<template>
  <el-dialog
    v-model="dialogVisible"
    title="导入文档"
    width="80%"
    @close="reset"
  >
    <!-- 上传区域（未解析时显示） -->
    <div
      v-if="!previewResult"
      class="upload-area"
    >
      <el-upload
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".md,.txt"
      >
        <el-icon>
          <UploadFilled />
        </el-icon>
        <div class="el-upload__text">
          拖拽文件到此处，或点击选择
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 .md 和 .txt 文件
          </div>
        </template>
      </el-upload>
    </div>

    <!-- 预览区域（解析成功后显示） -->
    <div v-else>
      <el-alert
        :title="`文件解析成功，共 ${previewResult.sections.length} 个片段`"
        type="success"
        :closable="false"
        style="margin-bottom: 20px"
      />

      <el-form label-width="80px">
        <el-form-item label="文档标题">
          <el-input v-model="previewResult.documentTitle" />
        </el-form-item>
      </el-form>

      <el-table
        :data="sections"
        border
        row-key="orderIndex"
        highlight-current-row
        @row-click="handleRowClick"
      >
        <el-table-column
          prop="orderIndex"
          label="序号"
          width="60"
        />
        <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip/>
        <el-table-column label="推荐标签" min-width="180">
          <template #default="{ row }">
            <el-checkbox-group v-model="row.selectedRecommendedTags">
              <el-checkbox v-for="tag in row.suggestedTags" :key="tag" :label="tag">
                {{ tag }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
        </el-table-column>
        <el-table-column label="手动标签" min-width="200">
          <template #default="{ row }">
            <TagSelect v-model="row.selectedTagIds"/>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="200">
          <template #default="{ row }">
            <CategorySelect v-model="row.selectedCategoryId"/>
          </template>
        </el-table-column>
      </el-table>

      <!-- 内容预览 -->
      <el-divider content-position="left">内容预览</el-divider>
      <div class="preview-box">
        <template v-if="previewSection">
          <MarkdownRenderer
            v-if="previewResult?.fileType === 'md'"
            :content="previewSection.originalContent"
          />
          <pre v-else class="plain-text">{{ previewSection.originalContent }}</pre>
        </template>
        <el-empty v-else description="点击表格行查看片段预览"/>
      </div>

      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmImport">确认导入</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref, watch} from 'vue'
import {ElMessage} from 'element-plus'
import {importDocument as importDoc, confirmImport as confirmImportApi} from '@/api/import'
import {createTag} from '@/api/tag'
import {useTagStore} from '@/stores/tag'
import type {ImportResult, ImportedSection, ImportConfirm} from '@/types'
import CategorySelect from '@/components/CategorySelect.vue'
import TagSelect from '@/components/TagSelect.vue'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import {UploadFilled} from "@element-plus/icons-vue";

// 扩展导入片段类型，增加前端本地状态
interface PreviewSection extends ImportedSection {
  selectedCategoryId: number | null
  selectedTagIds: number[]
  selectedRecommendedTags: string[] // 勾选的推荐标签名称
}

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(props.visible)
const previewResult = ref<ImportResult | null>(null)
const sections = ref<PreviewSection[]>([])
const previewSection = ref<PreviewSection | null>(null)
const tagStore = useTagStore()

// 同步父组件 visible 到本地 dialogVisible
watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

// 本地 dialogVisible 变化时通知父组件
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

function handleFileChange(file: any) {
  importDoc(file.raw)
    .then((result) => {
      const firstResult = result[0]
      if (firstResult) {
        previewResult.value = firstResult
        sections.value = firstResult.sections.map(sec => ({
          ...sec,
          selectedCategoryId: sec.categorySuggestions.length > 0
            ? (sec.categorySuggestions[0]?.categoryId ?? null)
            : null,
          selectedTagIds: [],
          selectedRecommendedTags: []
        }))
        previewSection.value = null
      }
    })
    .catch(() => {
      ElMessage.error('文件解析失败')
    })
}

function handleRowClick(row: PreviewSection) {
  previewSection.value = row
}

async function confirmImport() {
  if (!previewResult.value) return

  // 处理推荐标签：确保选中的推荐标签已存在，并获取其 ID
  for (const sec of sections.value) {
    for (const tagName of sec.selectedRecommendedTags) {
      let tag = tagStore.tags.find(t => t.name === tagName)
      if (!tag) {
        try {
          tag = await createTag(tagName)
          await tagStore.fetchTags() // 刷新标签列表
        } catch (e) {
          ElMessage.error(`创建标签失败: ${tagName}`)
          return
        }
      }
      if (!sec.selectedTagIds.includes(tag.id)) {
        sec.selectedTagIds.push(tag.id)
      }
    }
  }

  const importData: ImportConfirm = {
    documentTitle: previewResult.value.documentTitle,
    fileType: previewResult.value.fileType,
    sections: sections.value.map(sec => ({
      title: sec.title,
      originalContent: sec.originalContent,
      content: sec.content,
      orderIndex: sec.orderIndex,
      categoryId: sec.selectedCategoryId,
      tagIds: sec.selectedTagIds
    }))
  }

  await confirmImportApi(importData)
  ElMessage.success('导入成功')
  emit('success')
  dialogVisible.value = false
}

function reset() {
  previewResult.value = null
  sections.value = []
  previewSection.value = null
}
</script>

<style scoped>
.upload-area {
  text-align: center;
}

.dialog-footer {
  margin-top: 20px;
  text-align: right;
}

.preview-box {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 20px;
}

.plain-text {
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  margin: 0;
}
</style>