import http from './http'
import type { SearchResultItem, PageResult } from '@/types'

// 全局搜索片段
export const searchSections = (params: { keyword: string; page: number; size: number }) =>
  http.get<PageResult<SearchResultItem>, PageResult<SearchResultItem>>('/search', { params })