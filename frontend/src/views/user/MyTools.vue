<template>
  <div class="my-tools">
    <div class="page-header">
      <h2 class="page-title">我的工具</h2>
      <el-button type="primary" @click="router.push('/publish')">
        <el-icon><Plus /></el-icon> 发布新工具
      </el-button>
    </div>

    <el-card shadow="never" class="table-card">
      <el-table :data="tools" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="name" label="工具名称" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="router.push(`/tools/${row.id}`)">
              {{ row.name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category || '其他' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="toolCondition" label="状况" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="120">
          <template #default="{ row }">
            {{ formatTime(row.createTime || row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && tools.length === 0" description="还没有发布过工具">
        <el-button type="primary" @click="router.push('/publish')">去发布</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const router = useRouter()
const tools = ref([])
const loading = ref(false)

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

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const fetchTools = async () => {
  loading.value = true
  try {
    const res = await api.tools.getMy()
    tools.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handleEdit = (tool) => {
  router.push({ path: '/publish', query: { id: tool.id } })
}

const handleDelete = async (tool) => {
  try {
    await ElMessageBox.confirm(`确定要删除工具「${tool.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.tools.delete(tool.id)
    ElMessage.success('删除成功')
    fetchTools()
  } catch (e) {
    if (e !== 'cancel') {
      // Error handled by interceptor
    }
  }
}

onMounted(() => {
  fetchTools()
})
</script>

<style scoped>
.my-tools {
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

.table-card {
  border-radius: 12px;
}
</style>
