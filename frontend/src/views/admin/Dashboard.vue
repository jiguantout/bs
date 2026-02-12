<template>
  <div class="dashboard">
    <!-- Stats Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66b1ff);">
            <el-icon :size="28" color="#fff"><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #85ce61);">
            <el-icon :size="28" color="#fff"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalTools }}</div>
            <div class="stat-label">工具总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #ebb563);">
            <el-icon :size="28" color="#fff"><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.pendingAuditCount }}</div>
            <div class="stat-label">待审核工具</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #f89898);">
            <el-icon :size="28" color="#fff"><Connection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.activeBorrows }} / {{ stats.totalBorrows }}</div>
            <div class="stat-label">活跃/总借用</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- Recent Tools -->
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">
              <span class="section-title">最新工具</span>
              <el-button type="primary" link @click="$router.push('/admin/tools')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentTools" size="small" v-loading="toolsLoading">
            <el-table-column prop="name" label="名称" min-width="120" />
            <el-table-column prop="category" label="分类" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.category || '其他' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">
                  {{ statusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- Rankings -->
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <template #header>
            <span class="section-title">积分排行 TOP 5</span>
          </template>
          <el-table :data="topUsers" size="small" v-loading="rankLoading">
            <el-table-column label="排名" width="60" align="center">
              <template #default="{ $index }">
                <span v-if="$index < 3" class="rank-medal">{{ ['🥇', '🥈', '🥉'][$index] }}</span>
                <span v-else>{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="用户" min-width="120">
              <template #default="{ row }">
                {{ row.nickname || row.username || '匿名' }}
              </template>
            </el-table-column>
            <el-table-column label="积分" width="100" align="center">
              <template #default="{ row }">
                <span style="font-weight: 600; color: #E6A23C;">{{ row.points || 0 }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { api } from '@/api'

const stats = reactive({
  totalUsers: 0,
  totalTools: 0,
  pendingAuditCount: 0,
  activeBorrows: 0,
  totalBorrows: 0
})

const recentTools = ref([])
const topUsers = ref([])
const toolsLoading = ref(false)
const rankLoading = ref(false)

const statusType = (status) => {
  const map = {
    'PENDING_REVIEW': 'warning',
    'APPROVED': 'success',
    'AVAILABLE': 'success',
    'REJECTED': 'danger',
    'BORROWED': 'info',
    'OFFLINE': 'info'
  }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = {
    'PENDING_REVIEW': '待审核',
    'APPROVED': '已通过',
    'AVAILABLE': '可借用',
    'REJECTED': '已拒绝',
    'BORROWED': '已借出',
    'OFFLINE': '已下架'
  }
  return map[status] || status || '未知'
}

const fetchDashboard = async () => {
  try {
    const res = await api.admin.getDashboard()
    if (res.code === 200 && res.data) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error('获取仪表盘数据失败', e)
  }
}

const fetchRecentTools = async () => {
  toolsLoading.value = true
  try {
    const res = await api.admin.getTools()
    if (res.code === 200) {
      recentTools.value = (res.data || []).slice(0, 5)
    }
  } catch (e) { /* silent */ }
  finally { toolsLoading.value = false }
}

const fetchTopUsers = async () => {
  rankLoading.value = true
  try {
    const res = await api.points.getRanking()
    if (res.code === 200) {
      topUsers.value = (res.data || []).slice(0, 5)
    }
  } catch (e) { /* silent */ }
  finally { rankLoading.value = false }
}

onMounted(() => {
  fetchDashboard()
  fetchRecentTools()
  fetchTopUsers()
})
</script>

<style scoped>
.dashboard {
  padding-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
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
  border-radius: 14px;
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
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.rank-medal {
  font-size: 18px;
}
</style>
