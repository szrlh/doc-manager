import http from './http'
import type { Tag } from '@/types'

// 获取所有标签
export const getAllTags = () =>
  http.get<Tag[], Tag[]>('/tags')

// 创建标签
export const createTag = (name: string) =>
  http.post<Tag, Tag>('/tags', null, { params: { name } })

// 删除标签
export const deleteTag = (id: number) =>
  http.delete(`/tags/${id}`)

// 更新标签名称
export const updateTag = (id: number, name: string) =>
  http.put<Tag, Tag>(`/tags/${id}`, null, { params: { name } })