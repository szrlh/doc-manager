import http from './http'
import type {SectionItem, PageResult} from '@/types'

// 片段分页参数
export interface SectionPageParams {
  page: number
  size: number
  documentId?: number
  categoryId?: number
  tagId?: number
  keyword?: string
}

// 获取片段分页列表
export const getSectionPage = (params: SectionPageParams) =>
  http.get<PageResult<SectionItem>, PageResult<SectionItem>>('/sections', {params})

// 获取片段详情
export const getSectionDetail = (id: number) =>
  http.get<SectionItem, SectionItem>(`/sections/${id}`)

// 更新片段（传递原始内容、标题、分类ID、标签ID数组）
type UpdateSectionData = {
  originalContent: string
  title?: string | null
  categoryId?: number | null
  tagIds?: number[]
}

type UpdateSectionParams = Omit<UpdateSectionData, 'tagIds'> & {
  tagIds?: string
}

export const updateSection = (
  id: number,
  data: UpdateSectionData
) => {
  const params: UpdateSectionParams = {...data, tagIds: data.tagIds?.join(',')}
  return http.put(`/sections/${id}`, null, {params})
}

// 删除片段
export const deleteSection = (id: number) =>
  http.delete(`/sections/${id}`)

// 添加片段标签
export const addSectionTags = (id: number, tagIds: number[]) =>
  http.post(`/sections/${id}/tags`, tagIds)

// 移除片段标签
export const removeSectionTag = (id: number, tagId: number) =>
  http.delete(`/sections/${id}/tags/${tagId}`)