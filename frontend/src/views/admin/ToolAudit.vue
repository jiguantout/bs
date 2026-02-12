<template>
  <div class="tool-audit">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">工具审核与管理</span>
          <el-radio-group v-model="statusFilter" size="small" @change="filterTools">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="PENDING_REVIEW">待审核</el-radio-button>
            <el-radio-button label="AVAILABLE">可借用</el-radio-button>
            <el-radio-button label="BORROWED">借用中</el-radio-button>
            <el-radio-button label="REJECTED">已拒绝</el-radio-button>
            <el-radio-button label="OFFLINE">已下架</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="filteredTools" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="工具名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category || '其他' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="toolCondition" label="状况" width="80" />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column label="发布者" width="100">
          <template #default="{ row }">
            {{ row.ownerNickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="viewDetail(row)">
              查看
            </el-button>
            <template v-if="row.status === 'PENDING_REVIEW'">
              <el-button size="small" type="success" text @click="handleAudit(row, 'approve')">
                通过
              </el-button>
              <el-button size="small" type="danger" text @click="handleAudit(row, 'reject')">
                拒绝
              </el-button>
            </template>
            <el-button
              v-if="row.status === 'AVAILABLE' || row.status === 'APPROVED'"
              size="small"
              type="warning"
              text
              @click="handleOffline(row)"
            >
              下架
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && filteredTools.length === 0" description="暂无工具数据" />
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog v-model="showDetail" title="工具详情" width="600px">
      <template v-if="currentTool">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="名称">{{ currentTool.name }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentTool.category }}</el-descriptions-item>
          <el-descriptions-item label="状况">{{ currentTool.toolCondition }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ currentTool.location }}</el-descriptions-item>
          <el-descriptions-item label="发布者">{{ currentTool.ownerNickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(currentTool.status)">{{ statusText(currentTool.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ currentTool.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="图片" :span="2" v-if="currentTool.images">
            <div class="image-list">
              <el-image
                v-for="(img, idx) in getImages(currentTool.images)"
                :key="idx"
                :src="img"
                :preview-src-list="getImages(currentTool.images)"
                fit="cover"
                class="detail-image"
              />
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <template v-if="currentTool?.status === 'PENDING_REVIEW'">
          <el-button type="success" @click="handleAudit(currentTool, 'approve'); showDetail = false">
            通过审核
          </el-button>
          <el-button type="danger" @click="handleAudit(currentTool, 'reject'); showDetail = false">
            拒绝
          </el-button>
        </template>
        <el-button @click="showDetail = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api'

const tools = ref([])
const loading = ref(false)
const statusFilter = ref('')
const showDetail = ref(false)
const currentTool = ref(null)

const filteredTools = computed(() => {
  if (!statusFilter.value) return tools.value
  return tools.value.filter(t => t.status === statusFilter.value)
})

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

const getImages = (images) => {
  if (!images) return []
  if (typeof images === 'string') return images.split(',').map(s => s.trim()).filter(Boolean)
  if (Array.isArray(images)) return images.filter(Boolean)
  return []
}

const filterTools = () => {
  // Computed property handles filtering
}

const fetchTools = async () => {
  loading.value = true
  try {
    const res = await api.admin.getTools()
    if (res.code === 200) {
      tools.value = res.data || []
    }
  } catch (e) {
    ElMessage.error('获取工具列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (tool) => {
  currentTool.value = tool
  showDetail.value = true
}

const handleAudit = async (tool, action) => {
  const actionText = action === 'approve' ? '通过' : '拒绝'
  try {
    let reason = ''
    if (action === 'reject') {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入原因...'
      })
      reason = value
    } else {
      await ElMessageBox.confirm(`确定${actionText}工具「${tool.name}」吗？`, '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      })
    }
    const res = await api.admin.auditTool(tool.id, { action, reason })
    if (res.code === 200) {
      ElMessage.success(`已${actionText}`)
      fetchTools()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`操作失败`)
    }
  }
}

const handleOffline = async (tool) => {
  try {
    await ElMessageBox.confirm(`确定要下架工具「${tool.name}」吗？`, '下架确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await api.admin.offlineTool(tool.id)
    if (res.code === 200) {
      ElMessage.success('已下架')
      fetchTools()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

onMounted(() => {
  fetchTools()
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
  flex-wrap: wrap;
  gap: 12px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.image-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
}
</style>
