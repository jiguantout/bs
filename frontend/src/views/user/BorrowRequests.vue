<template>
  <div class="borrow-requests">
    <h2 class="page-title">借出管理</h2>

    <el-card shadow="never" class="table-card">
      <el-table :data="requests" v-loading="loading" stripe style="width: 100%">
        <el-table-column label="工具名称" min-width="150">
          <template #default="{ row }">
            {{ row.toolName || row.tool?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="借用人" width="120">
          <template #default="{ row }">
            {{ row.borrowerNickname || row.borrower?.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="120">
          <template #default="{ row }">
            {{ formatTime(row.createTime || row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180">
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'APPLIED'">
              <el-button size="small" type="success" @click="handleApprove(row)">
                同意
              </el-button>
              <el-button size="small" type="danger" @click="handleReject(row)">
                拒绝
              </el-button>
            </template>
            <template v-else>
              <el-tag :type="statusType(row.status)" size="small">
                {{ statusText(row.status) }}
              </el-tag>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && requests.length === 0" description="暂无借用请求" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const requests = ref([])
const loading = ref(false)

const statusType = (status) => {
  const map = {
    'APPLIED': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'PICKED_UP': '',
    'RETURNED': 'info'
  }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = {
    'APPLIED': '待处理',
    'APPROVED': '已同意',
    'REJECTED': '已拒绝',
    'PICKED_UP': '使用中',
    'RETURNED': '已归还'
  }
  return map[status] || status || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const fetchRequests = async () => {
  loading.value = true
  try {
    const res = await api.borrows.getReceived()
    requests.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handleApprove = async (record) => {
  try {
    await ElMessageBox.confirm('确定同意该借用申请吗？', '同意借用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await api.borrows.approve(record.id)
    ElMessage.success('已同意借用')
    fetchRequests()
  } catch (e) {
    if (e !== 'cancel') {
      // Error handled by interceptor
    }
  }
}

const handleReject = async (record) => {
  try {
    await ElMessageBox.confirm('确定拒绝该借用申请吗？', '拒绝借用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.borrows.reject(record.id)
    ElMessage.success('已拒绝借用')
    fetchRequests()
  } catch (e) {
    if (e !== 'cancel') {
      // Error handled by interceptor
    }
  }
}

onMounted(() => {
  fetchRequests()
})
</script>

<style scoped>
.borrow-requests {
  padding-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.table-card {
  border-radius: 12px;
}

.remark-text {
  color: #606266;
  font-size: 13px;
}
</style>
