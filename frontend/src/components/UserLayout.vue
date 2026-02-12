<template>
  <div class="layout">
    <!-- Header -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/home')">
          <el-icon size="24"><Tools /></el-icon>
          <span class="logo-text">社区工具共享</span>
        </div>

        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="nav-menu"
          :ellipsis="false"
          router
        >
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>首页
          </el-menu-item>
          <el-menu-item index="/tools">
            <el-icon><Search /></el-icon>工具列表
          </el-menu-item>
          <el-menu-item index="/rankings">
            <el-icon><Trophy /></el-icon>积分排行
          </el-menu-item>
        </el-menu>

        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <!-- Notification bell -->
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
              <el-button :icon="Bell" circle @click="router.push('/notifications')" />
            </el-badge>

            <!-- Points display -->
            <el-tag type="warning" effect="plain" round class="points-tag">
              <el-icon><Medal /></el-icon>
              {{ userStore.userPoints }} 积分
            </el-tag>

            <!-- User dropdown -->
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userStore.user?.avatar">
                  {{ userStore.user?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="nickname">{{ userStore.user?.nickname || userStore.user?.username }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="/profile">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="/publish">
                    <el-icon><Plus /></el-icon>发布工具
                  </el-dropdown-item>
                  <el-dropdown-item command="/my-tools">
                    <el-icon><Box /></el-icon>我的工具
                  </el-dropdown-item>
                  <el-dropdown-item command="/borrows">
                    <el-icon><Document /></el-icon>借用记录
                  </el-dropdown-item>
                  <el-dropdown-item command="/borrow-requests">
                    <el-icon><ChatDotRound /></el-icon>借出管理
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="/admin/dashboard" divided>
                    <el-icon><Setting /></el-icon>管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
            <el-button @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- Main content -->
    <el-main class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </el-main>

    <!-- Footer -->
    <el-footer class="footer">
      <p>&copy; 2024 社区工具共享平台 - 邻里互助，共享工具</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Bell } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const unreadCount = ref(0)

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/tools/')) return '/tools'
  return path
})

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await api.notifications.getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    // silent fail
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else {
    router.push(command)
  }
}

onMounted(() => {
  fetchUnreadCount()
})

// Re-fetch unread count on route changes
watch(() => route.path, () => {
  fetchUnreadCount()
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* ========== Header ========== */
.header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 0;
  height: 64px !important;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 24px;
}

/* ========== Logo ========== */
.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 48px;
  flex-shrink: 0;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.85;
}

.logo .el-icon {
  background: linear-gradient(135deg, #409eff, #6366f1);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-size: 24px;
  /* fallback for icon rendering */
  color: #409eff;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  margin-left: 10px;
  background: linear-gradient(135deg, #409eff, #6366f1);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
  letter-spacing: 1px;
}

/* ========== Navigation Menu ========== */
.nav-menu {
  flex: 1;
  border-bottom: none !important;
  background: transparent !important;
}

.nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  font-weight: 500;
  height: 64px;
  line-height: 64px;
  border-bottom-width: 3px;
  transition: color 0.2s, border-color 0.2s;
}

.nav-menu :deep(.el-menu-item:hover) {
  background-color: transparent !important;
  color: #409eff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  border-bottom-color: #409eff;
  color: #409eff;
  font-weight: 600;
}

/* ========== Header Right ========== */
.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.notification-badge :deep(.el-badge__content) {
  font-size: 11px;
}

.points-tag {
  font-size: 13px;
  border-radius: 20px;
  padding: 0 12px;
  height: 28px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ========== User Info Dropdown ========== */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background: #f0f2f5;
}

.nickname {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.2s;
}

/* ========== Main Content ========== */
.main-content {
  flex: 1;
  padding: 24px;
  margin-top: 64px;
  min-height: calc(100vh - 64px - 64px);
  background: #f5f7fa;
}

.content-wrapper {
  max-width: 1280px;
  margin: 0 auto;
}

/* ========== Footer ========== */
.footer {
  background: #fff;
  text-align: center;
  color: #909399;
  font-size: 14px;
  height: 64px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid #ebeef5;
}

.footer p {
  margin: 0;
  letter-spacing: 0.5px;
}

/* ========== Responsive ========== */
@media (max-width: 768px) {
  .header-content {
    padding: 0 12px;
  }

  .logo {
    margin-right: 16px;
  }

  .logo-text {
    display: none;
  }

  .nickname {
    display: none;
  }

  .points-tag {
    display: none;
  }

  .nav-menu :deep(.el-menu-item) {
    font-size: 14px;
    padding: 0 10px;
  }
}
</style>
