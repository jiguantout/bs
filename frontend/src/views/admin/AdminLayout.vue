<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="admin-sidebar">
      <div class="sidebar-header">
        <el-icon :size="24" color="#409EFF"><Setting /></el-icon>
        <span v-show="!isCollapsed" class="sidebar-title">管理后台</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        :collapse="isCollapsed"
        background-color="#001529"
        text-color="rgba(255, 255, 255, 0.7)"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/tools">
          <el-icon><Tools /></el-icon>
          <template #title>工具审核</template>
        </el-menu-item>
        <el-menu-item index="/admin/announcements">
          <el-icon><Notification /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>
      </el-menu>

      <!-- Collapse toggle at sidebar bottom -->
      <div class="sidebar-footer">
        <el-button
          text
          class="collapse-btn"
          @click="isCollapsed = !isCollapsed"
        >
          <el-icon :size="18" color="rgba(255,255,255,0.6)">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </el-button>
      </div>
    </el-aside>

    <!-- Main area -->
    <el-container class="admin-main-container">
      <!-- Top bar -->
      <el-header class="admin-header">
        <div class="admin-header-left">
          <h3 class="page-title">{{ currentTitle }}</h3>
        </div>
        <div class="admin-header-right">
          <el-button type="primary" plain size="small" @click="router.push('/home')">
            <el-icon><HomeFilled /></el-icon>
            返回前台
          </el-button>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-user">
              <el-avatar :size="32" :src="userStore.user?.avatar">
                <el-icon :size="16"><User /></el-icon>
              </el-avatar>
              <span class="admin-nickname">{{ userStore.user?.nickname || '管理员' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="home">
                  <el-icon><HomeFilled /></el-icon>返回前台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main content -->
      <el-main class="admin-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const titleMap = {
    '/admin/dashboard': '数据概览',
    '/admin/users': '用户管理',
    '/admin/tools': '工具审核',
    '/admin/announcements': '公告管理'
  }
  return titleMap[route.path] || '管理后台'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'home') {
    router.push('/home')
  }
}
</script>

<style scoped>
/* ========== Layout Root ========== */
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* ========== Sidebar ========== */
.admin-sidebar {
  background: #001529;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s ease;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none !important;
  flex: 1;
  overflow-y: auto;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  margin: 2px 8px;
  border-radius: 6px;
  transition: all 0.2s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(64, 158, 255, 0.15) !important;
  color: #409eff !important;
  font-weight: 600;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  text-align: center;
  flex-shrink: 0;
}

.collapse-btn {
  width: 100%;
  height: 36px;
  border-radius: 6px;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

/* ========== Main Container ========== */
.admin-main-container {
  flex: 1;
  flex-direction: column;
  margin-left: 220px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
}

/* Adjust margin when sidebar collapsed */
.admin-layout .admin-sidebar[style*="64px"] ~ .admin-main-container {
  margin-left: 64px;
}

/* ========== Top Header ========== */
.admin-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px !important;
  position: sticky;
  top: 0;
  z-index: 50;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
}

.admin-header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* ========== Admin User Dropdown ========== */
.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.admin-user:hover {
  background: #f0f2f5;
}

.admin-nickname {
  font-size: 14px;
  color: #4e5969;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: #909399;
}

/* ========== Content Area ========== */
.admin-content {
  padding: 24px;
  background: #f0f2f5;
  flex: 1;
}
</style>
