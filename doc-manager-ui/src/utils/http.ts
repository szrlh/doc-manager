// import axios from 'axios'
// import type { AxiosRequestConfig } from 'axios'
//
// interface HttpInstance {
//     get<T>(url: string, config?: AxiosRequestConfig): Promise<T>
//     post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
//     put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
//     delete<T>(url: string, config?: AxiosRequestConfig): Promise<T>
//     patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
// }
//
// const instance = axios.create({
//     baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
//     timeout: 10000,
// })
//
// // 请求拦截器（如添加 token）
// instance.interceptors.request.use((config) => {
//     const token = localStorage.getItem('token')
//     if (token) {
//         config.headers.Authorization = `Bearer ${token}`
//     }
//     return config
// })
//
// // 响应拦截器：解包 data
// instance.interceptors.response.use(
//     (response) => response.data,
//     (error) => Promise.reject(error)
// )
//
// export const http = instance as unknown as HttpInstance