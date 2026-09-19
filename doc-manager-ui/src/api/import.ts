import http from './http'
import type { ImportResult, ImportConfirm } from '@/types'

// 导入预览
export const importDocument = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<ImportResult[], ImportResult[]>('/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 确认导入
export const confirmImport = (data: ImportConfirm) =>
  http.post<number, number>('/import/confirm', data)