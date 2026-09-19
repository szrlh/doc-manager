<template>
  <el-container class="main-container">
    <el-aside
      width="220px"
      class="aside"
    >
      <div class="logo">
        文档管理器
      </div>
      <el-menu
        :default-active="route.path"
        router
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/search">
          <el-icon>
            <Search />
          </el-icon>
          <span>全局搜索</span>
        </el-menu-item>
        <el-menu-item index="/sections">
          <el-icon>
            <Collection />
          </el-icon>
          <span>分片管理</span>
        </el-menu-item>
        <el-menu-item index="/documents">
          <el-icon>
            <Document />
          </el-icon>
          <span>文档管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon>
            <FolderOpened />
          </el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/tags">
          <el-icon>
            <CollectionTag />
          </el-icon>
          <span>标签管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">文档管理工具</span>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useCategoryStore } from '@/stores/category'
import { useTagStore } from '@/stores/tag'
import {Collection, CollectionTag, Document, FolderOpened, Search} from "@element-plus/icons-vue";

const categoryStore = useCategoryStore()
const tagStore = useTagStore()
const route = useRoute()

onMounted(() => {
  // 初始化全局分类和标签数据
  categoryStore.refreshAll()
  tagStore.fetchTags()
})
</script>

<style scoped>
.main-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 500;
}
</style>