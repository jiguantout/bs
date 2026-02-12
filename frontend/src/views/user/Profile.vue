<template>
  <div class="profile">
    <h2 class="page-title">个人中心</h2>

    <el-row :gutter="24">
      <!-- User Info Card -->
      <el-col :span="8">
        <el-card shadow="never" class="profile-card">
          <div class="profile-header">
            <el-avatar :size="80" :src="userStore.user?.avatar">
              <el-icon :size="36"><User /></el-icon>
            </el-avatar>
            <h3 class="profile-name">{{ userStore.user?.nickname || '未设置昵称' }}</h3>
            <p class="profile-username">@{{ userStore.user?.username }}</p>
            <el-tag v-if="userStore.isAdmin" type="danger" size="small">管理员</el-tag>
          </div>

          <el-descriptions :column="1" class="profile-info" border>
            <el-descriptions-item label="手机号">
              {{ userStore.user?.phone || '未绑定' }}
            </el-descriptions-item>
            <el-descriptions-item label="积分">
              <span class="points-value">{{ userStore.user?.points || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="角色">
              {{ userStore.user?.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-descriptions-item>
          </el-descriptions>

          <el-button
            type="primary"
            class="edit-btn"
            @click="openEditDialog"
          >
            <el-icon><Edit /></el-icon> 编辑资料
          </el-button>
        </el-card>
      </el-col>

      <!-- Stats -->
      <el-col :span="16">
        <el-row :gutter="16" class="stats-row">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: #e8f4fd;">
                <el-icon :size="24" color="#409EFF"><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ myToolsCount }}</div>
                <div class="stat-label">发布工具</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: #e8f8e8;">
                <el-icon :size="24" color="#67C23A"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ myBorrowsCount }}</div>
                <div class="stat-label">借用次数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon" style="background: #fef3e2;">
                <el-icon :size="24" color="#E6A23C"><Tickets /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ myLentCount }}</div>
                <div class="stat-label">借出次数</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- Point Records -->
        <el-card shadow="never" class="point-card">
          <template #header>
            <span class="section-title">
              <el-icon color="#E6A23C"><Coin /></el-icon> 积分记录
            </span>
          </template>
          <el-table :data="pointRecords" v-loading="pointsLoading" stripe>
            <el-table-column prop="description" label="描述" min-width="200">
              <template #default="{ row }">
                {{ row.description || row.reason || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分变动" width="120" align="center">
              <template #default="{ row }">
                <span :class="row.points > 0 ? 'points-plus' : 'points-minus'">
                  {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.createTime || row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!pointsLoading && pointRecords.length === 0" description="暂无积分记录" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- Edit Dialog -->
    <el-dialog v-model="showEditDialog" title="编辑个人资料" width="480px">
      <el-form :model="editForm" label-width="80px" size="large">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="editForm.avatar" placeholder="请输入头像图片链接" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleUpdateProfile">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const userStore = useUserStore()

const myToolsCount = ref(0)
const myBorrowsCount = ref(0)
const myLentCount = ref(0)
const pointRecords = ref([])
const pointsLoading = ref(false)

const showEditDialog = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  nickname: '',
  avatar: '',
  phone: ''
})

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const openEditDialog = () => {
  editForm.nickname = userStore.user?.nickname || ''
  editForm.avatar = userStore.user?.avatar || ''
  editForm.phone = userStore.user?.phone || ''
  showEditDialog.value = true
}

const handleUpdateProfile = async () => {
  editLoading.value = true
  try {
    await userStore.updateProfile(editForm)
    ElMessage.success('资料更新成功')
    showEditDialog.value = false
  } catch (e) {
    // Error handled by interceptor
  } finally {
    editLoading.value = false
  }
}

const fetchStats = async () => {
  try {
    const res = await api.tools.getMy()
    myToolsCount.value = (res.data || []).length
  } catch (e) { /* silent */ }

  try {
    const res = await api.borrows.getMy()
    myBorrowsCount.value = (res.data || []).length
  } catch (e) { /* silent */ }

  try {
    const res = await api.borrows.getReceived()
    myLentCount.value = (res.data || []).length
  } catch (e) { /* silent */ }
}

const fetchPointRecords = async () => {
  pointsLoading.value = true
  try {
    const res = await api.points.getMy()
    pointRecords.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    pointsLoading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchPointRecords()
})
</script>

<style scoped>
.profile {
  padding-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.profile-card {
  border-radius: 12px;
  text-align: center;
}

.profile-header {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.profile-username {
  font-size: 14px;
  color: #909399;
}

.profile-info {
  margin: 16px 0;
}

.points-value {
  font-size: 18px;
  font-weight: 700;
  color: #E6A23C;
}

.edit-btn {
  width: 100%;
  margin-top: 8px;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 12px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.point-card {
  border-radius: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.points-plus {
  color: #67C23A;
  font-weight: 600;
}

.points-minus {
  color: #F56C6C;
  font-weight: 600;
}
</style>
