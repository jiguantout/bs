<template>
  <div class="borrow-records">
    <h2 class="page-title">借用记录</h2>

    <el-card shadow="never" class="table-card">
      <el-table :data="records" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="toolName" label="工具名称" min-width="150">
          <template #default="{ row }">
            {{ row.toolName || row.tool?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="工具主人" width="120">
          <template #default="{ row }">
            {{ row.ownerNickname || row.owner?.nickname || '-' }}
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
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'APPROVED'">
              <el-button size="small" type="success" @click="handlePickup(row)">
                确认取货
              </el-button>
            </template>
            <template v-else-if="row.status === 'PICKED_UP'">
              <el-button size="small" type="warning" @click="handleReturn(row)">
                确认归还
              </el-button>
            </template>
            <template v-else-if="row.status === 'RETURNED'">
              <el-button
                v-if="!row.reviewed"
                size="small"
                type="primary"
                @click="openReview(row)"
              >
                评价
              </el-button>
              <el-tag v-else size="small" type="success">已评价</el-tag>
            </template>
            <template v-else-if="row.status === 'APPLIED'">
              <el-tag size="small" type="warning">等待审核</el-tag>
            </template>
            <template v-else-if="row.status === 'REJECTED'">
              <el-tag size="small" type="danger">已被拒绝</el-tag>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && records.length === 0" description="暂无借用记录" />
    </el-card>

    <!-- Review Dialog -->
    <el-dialog v-model="showReviewDialog" title="评价工具" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="工具名称">
          <el-input :model-value="currentRecord?.toolName || currentRecord?.tool?.name" disabled />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请写下您的使用体验..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="submitReview">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const records = ref([])
const loading = ref(false)
const showReviewDialog = ref(false)
const reviewLoading = ref(false)
const currentRecord = ref(null)

const reviewForm = reactive({
  rating: 5,
  content: ''
})

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
    'APPLIED': '待审核',
    'APPROVED': '已通过',
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

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await api.borrows.getMy()
    records.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

const handlePickup = async (record) => {
  try {
    await ElMessageBox.confirm('确认已取到工具？', '确认取货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await api.borrows.pickup(record.id)
    ElMessage.success('已确认取货')
    fetchRecords()
  } catch (e) {
    if (e !== 'cancel') {
      // Error handled by interceptor
    }
  }
}

const handleReturn = async (record) => {
  try {
    await ElMessageBox.confirm('确认已归还工具？', '确认归还', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await api.borrows.return_(record.id)
    ElMessage.success('已确认归还')
    fetchRecords()
  } catch (e) {
    if (e !== 'cancel') {
      // Error handled by interceptor
    }
  }
}

const openReview = (record) => {
  currentRecord.value = record
  reviewForm.rating = 5
  reviewForm.content = ''
  showReviewDialog.value = true
}

const submitReview = async () => {
  if (!reviewForm.content) {
    ElMessage.warning('请输入评价内容')
    return
  }

  reviewLoading.value = true
  try {
    await api.reviews.create({
      borrowRecordId: currentRecord.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    // Mark as reviewed locally
    const record = records.value.find(r => r.id === currentRecord.value.id)
    if (record) record.reviewed = true
  } catch (e) {
    // Error handled by interceptor
  } finally {
    reviewLoading.value = false
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.borrow-records {
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
</style>
