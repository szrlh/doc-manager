import http from './http'
import type { Category, CategoryTreeVO } from '@/types'

// 获取分类树
export const getCategoryTree = () =>
  http.get<CategoryTreeVO[], CategoryTreeVO[]>('/categories/tree')

// 获取所有分类（扁平）
export const getAllCategories = () =>
  http.get<Category[], Category[]>('/categories')

// 创建分类
export const createCategory = (data: Partial<Category>) =>
  http.post<Category, Category>('/categories', data)

// 更新分类
export const updateCategory = (id: number, data: Partial<Category>) =>
  http.put<Category, Category>(`/categories/${id}`, data)

// 删除分类
export const deleteCategory = (id: number) =>
  http.delete(`/categories/${id}`)