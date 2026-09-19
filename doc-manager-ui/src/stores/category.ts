import { defineStore } from 'pinia'
import { getAllCategories, getCategoryTree } from '@/api/category'
import type { Category, CategoryTreeVO } from '@/types'

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: [] as Category[],
    categoryTree: [] as CategoryTreeVO[]
  }),
  actions: {
    async fetchCategories() {
      this.categories = await getAllCategories()
    },
    async fetchCategoryTree() {
      this.categoryTree = await getCategoryTree()
    },
    async refreshAll() {
      await Promise.all([this.fetchCategories(), this.fetchCategoryTree()])
    }
  }
})