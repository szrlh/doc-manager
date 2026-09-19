/// <reference types="vite/client" />
// 全局组件类型声明文件 IDE能够自动识别
/// <reference types="element-plus/global" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}