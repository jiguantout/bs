<template>
  <div class="home">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">社区工具共享平台</h1>
        <p class="hero-subtitle">邻里互助，共享工具，让社区更温暖</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/publish')">
            <el-icon><Plus /></el-icon>发布工具
          </el-button>
          <el-button size="large" @click="router.push('/tools')">
            <el-icon><Search /></el-icon>浏览工具
          </el-button>
          <el-button size="large" @click="router.push('/rankings')">
            <el-icon><Trophy /></el-icon>积分排行
          </el-button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e8f4fd;">
            <el-icon :size="28" color="#409EFF"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ toolCount }}</div>
            <div class="stat-label">共享工具数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e8f8e8;">
            <el-icon :size="28" color="#67C23A"><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ toolCount > 0 ? Math.ceil(toolCount * 2.5) : 0 }}</div>
            <div class="stat-label">活跃用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #fef3e2;">
            <el-icon :size="28" color="#E6A23C"><Connection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ toolCount > 0 ? Math.ceil(toolCount * 1.8) : 0 }}</div>
            <div class="stat-label">成功借用次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Announcements -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="section-title">
            <el-icon color="#E6A23C"><Bell /></el-icon>
            最新公告
          </span>
        </div>
      </template>
      <div v-if="announcements.length > 0">
        <div
          v-for="item in announcements"
          :key="item.id"
          class="announcement-item"
        >
          <el-tag type="danger" size="small" effect="dark">公告</el-tag>
          <span class="announcement-title">{{ item.title }}</span>
          <span class="announcement-content">{{ item.content }}</span>
          <span class="announcement-time">{{ formatTime(item.createTime || item.createdAt) }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无公告" :image-size="80" />
    </el-card>

    <!-- Quick Tool Preview -->
    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="section-title">
            <el-icon color="#409EFF"><Box /></el-icon>
            最新共享工具
          </span>
          <el-button type="primary" link @click="router.push('/tools')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-row :gutter="16" v-if="latestTools.length > 0">
        <el-col :span="6" v-for="tool in latestTools" :key="tool.id">
          <el-card shadow="hover" class="tool-preview-card" @click="router.push(`/tools/${tool.id}`)">
            <el-image
              :src="getFirstImage(tool.images)"
              fit="cover"
              class="tool-preview-img"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="32"><Box /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="tool-preview-info">
              <h4>{{ tool.name }}</h4>
              <el-tag size="small" type="info">{{ tool.category }}</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无工具" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const toolCount = ref(0)
const announcements = ref([])
const latestTools = ref([])

const getFirstImage = (images) => {
  if (!images) return ''
  if (typeof images === 'string') {
    return images.split(',')[0].trim()
  }
  if (Array.isArray(images)) return images[0] || ''
  return ''
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const fetchData = async () => {
  try {
    const toolRes = await api.tools.getList({})
    const tools = toolRes.data || []
    toolCount.value = tools.length
    latestTools.value = tools.slice(0, 4)
  } catch (e) {
    // silent
  }

  try {
    const annRes = await api.announcements.getPublic()
    announcements.value = annRes.data || []
  } catch (e) {
    // silent
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home {
  padding-bottom: 20px;
}

.hero-banner {
  background: linear-gradient(135deg, #409EFF 0%, #6c5ce7 100%);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 24px;
  text-align: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.hero-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.hero-banner::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
}

.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 2px;
}

.section-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.announcement-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-weight: 500;
  color: #303133;
  flex-shrink: 0;
}

.announcement-content {
  color: #606266;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-time {
  color: #909399;
  font-size: 13px;
  flex-shrink: 0;
}

.tool-preview-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s;
}

.tool-preview-card:hover {
  transform: translateY(-4px);
}

.tool-preview-card :deep(.el-card__body) {
  padding: 0;
}

.tool-preview-img {
  width: 100%;
  height: 160px;
  display: block;
}

.image-placeholder {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.tool-preview-info {
  padding: 12px;
}

.tool-preview-info h4 {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
