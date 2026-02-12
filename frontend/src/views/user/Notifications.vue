<template>
  <div class="notifications">
    <div class="page-header">
      <h2 class="page-title">消息通知</h2>
      <el-button type="primary" @click="markAllRead" :disabled="unreadCount === 0">
        <el-icon><Check /></el-icon> 全部已读
      </el-button>
    </div>

    <el-card shadow="never" class="notification-card" v-loading="loading">
      <div v-if="notifications.length > 0" class="notification-list">
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: !item.read && !item.isRead }"
          @click="handleRead(item)"
        >
          <div class="notification-icon">
            <el-icon :size="20" :color="item.read || item.isRead ? '#c0c4cc' : '#409EFF'">
              <Bell />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">
              {{ item.title || '系统通知' }}
              <el-tag
                v-if="!item.read && !item.isRead"
                size="small"
                type="danger"
                effect="dark"
                class="unread-tag"
              >
                未读
              </el-tag>
            </div>
            <div class="notification-body">{{ item.content || item.message || '' }}</div>
            <div class="notification-time">
              {{ formatTime(item.createTime || item.createdAt) }}
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息通知" :image-size="100" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const notifications = ref([])
const loading = ref(false)

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read && !n.isRead).length
})

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await api.notifications.getList()
    notifications.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handleRead = async (item) => {
  if (item.read || item.isRead) return
  try {
    await api.notifications.markRead(item.id)
    item.read = true
    item.isRead = true
  } catch (e) {
    // silent
  }
}

const markAllRead = async () => {
  try {
    await api.notifications.markAllRead()
    notifications.value.forEach(n => {
      n.read = true
      n.isRead = true
    })
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    // Error handled by interceptor
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notifications {
  padding-bottom: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.notification-card {
  border-radius: 12px;
}

.notification-list {
  display: flex;
  flex-direction: column;
}

.notification-item {
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 8px;
  margin-bottom: 4px;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #ecf5ff;
  border-left: 3px solid #409EFF;
}

.notification-item.unread:hover {
  background: #d9ecff;
}

.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-item.unread .notification-icon {
  background: #d9ecff;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.unread-tag {
  flex-shrink: 0;
}

.notification-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}
</style>
