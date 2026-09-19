// 集中定义后端DTO对应的TypeScript接口，供全局使用

// 文档列表项
export interface DocumentItem {
  id: number
  title: string
  fileType: 'md' | 'txt'
  sectionCount: number
  createTime: string
  updateTime: string
}

// 片段列表项
export interface SectionItem {
  id: number
  documentId: number
  documentTitle: string
  fileType: 'md' | 'txt'
  title: string | null
  content: string
  originalContent: string
  orderIndex: number
  categoryId: number | null
  categoryName: string | null
  tags: string[]
  tagIds: number[]
  updateTime: string
}

export interface ImportedSection {
  title: string | null
  originalContent: string
  content: string
  orderIndex: number
  categorySuggestions: CategorySuggestion[]
  suggestedTags: string[]
}

// 分类实体（扁平）
export interface Category {
  id: number
  name: string
  description?: string | null
  parentId?: number | null
  ruleKeywords?: string | null
  createTime?: string
  updateTime?: string
}

// 分类树节点
export interface CategoryTreeVO {
  id: number
  name: string
  children: CategoryTreeVO[]
}

// 标签
export interface Tag {
  id: number
  name: string
}

// 搜索结果项
export interface SearchResultItem {
  sectionId: number
  sectionTitle: string | null
  contentSnippet: string
  documentTitle: string
  categoryName: string | null
  relevance: number
}

// 导入预览相关
export interface CategorySuggestion {
  categoryId: number
  categoryName: string
  confidence: number
}

export interface ImportedSection {
  title: string | null
  originalContent: string
  content: string
  orderIndex: number
  categorySuggestions: CategorySuggestion[]
}

export interface ImportResult {
  documentTitle: string
  fileType: 'md' | 'txt'
  sections: ImportedSection[]
}

export interface ImportedSectionConfirm {
  title: string | null
  originalContent: string
  content: string
  orderIndex: number
  categoryId: number | null
  tagIds: number[]
}

export interface ImportConfirm {
  documentTitle: string
  fileType: 'md' | 'txt'
  sections: ImportedSectionConfirm[]
}

// 通用分页返回结构（与后端 MyBatis-Plus IPage 对应）
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}