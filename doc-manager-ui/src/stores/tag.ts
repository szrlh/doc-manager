import { defineStore } from 'pinia'
import { getAllTags } from '@/api/tag'
import type { Tag } from '@/types'

export const useTagStore = defineStore('tag', {
  state: () => ({
    tags: [] as Tag[]
  }),
  actions: {
    async fetchTags() {
      this.tags = await getAllTags()
    },
    async refresh() {
      await this.fetchTags()
    }
  }
})