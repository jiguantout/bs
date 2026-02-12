<template>
  <div class="user-management">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户管理</span>
          <el-tag type="info">共 {{ users.length }} 位用户</el-tag>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="用户" min-width="180">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" :src="row.avatar">
                {{ (row.nickname || row.username || 'U').charAt(0) }}
              </el-avatar>
              <div>
                <div class="user-nickname">{{ row.nickname || '未设置' }}</div>
                <div class="user-username">@{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" align="center">
          <template #default="{ row }">
            <span style="font-weight: 600; color: #E6A23C;">{{ row.points || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role !== 'ADMIN'"
              size="small"
              :type="row.status === 1 ? 'danger' : 'success'"
              text
              @click="toggleUserStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-tag v-else size="small" type="info">系统管理员</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && users.length === 0" description="暂无用户数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api'

const users = ref([])
const loading = ref(false)

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await api.admin.getUsers()
    if (res.code === 200) {
      users.value = res.data || []
    }
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const toggleUserStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户「${user.nickname || user.username}」吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await api.admin.updateUserStatus(user.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`已${action}该用户`)
      fetchUsers()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-nickname {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.user-username {
  font-size: 12px;
  color: #909399;
}
</style>
