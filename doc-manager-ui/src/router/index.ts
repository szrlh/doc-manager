import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      redirect: '/search',
      children: [
        {
          path: 'search',
          name: 'SearchResult',
          component: () => import('@/views/SearchResult.vue')
        },
        {
          path: 'sections',
          name: 'SectionManage',
          component: () => import('@/views/SectionManage.vue')
        },
        {
          path: 'documents',
          name: 'DocumentList',
          component: () => import('@/views/DocumentList.vue')
        },
        {
          path: 'documents/:id/sections',
          name: 'SectionList',
          component: () => import('@/views/SectionList.vue'),
          props: true
        },
        {
          path: 'categories',
          name: 'CategoryManage',
          component: () => import('@/views/CategoryManage.vue')
        },
        {
          path: 'tags',
          name: 'TagManage',
          component: () => import('@/views/TagManage.vue')
        }
      ]
    }
  ]
})

export default router
