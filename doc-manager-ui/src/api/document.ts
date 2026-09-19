import http from './http'
import type { DocumentItem, PageResult } from '@/types'

// 分页参数接口
export interface PageParams {
  page: number
  size: number
  keyword?: string
}

// 获取文档分页列表
// http.get<T = any, R = AxiosResponse<T>, D = any>(url: string, config?: AxiosRequestConfig<D>): Promise<R>;
// T = 响应体类型，服务器返回的 data 字段的类型；R = 整体响应类型，整个 HTTP 响应对象的类型，默认是 AxiosResponse<T>（可以拦截器处理）
// D = 请求体类型，发送请求时 body 的类型（GET 请求一般不用），url是请求接口地址，config是请求配置对象，可包含 params、headers、timeout等
// params是axios的语法糖，会将对象自动拼接成URL查询字符串
export const getDocumentPage = (params: PageParams) =>
  http.get<PageResult<DocumentItem>, PageResult<DocumentItem>>('/documents', { params })

// 删除文档
export const deleteDocument = (id: number) =>
  http.delete(`/documents/${id}`)