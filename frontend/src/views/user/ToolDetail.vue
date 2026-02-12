<template>
  <div class="tool-detail" v-loading="loading">
    <el-button @click="router.back()" class="back-btn" text>
      <el-icon><ArrowLeft /></el-icon> 返回列表
    </el-button>

    <template v-if="tool">
      <el-row :gutter="24">
        <!-- Left: Images -->
        <el-col :span="12">
          <el-card shadow="never" class="image-card">
            <el-image
              v-if="imageList.length > 0"
              :src="imageList[currentImageIndex]"
              fit="contain"
              class="main-image"
              :preview-src-list="imageList"
              :initial-index="currentImageIndex"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="60"><Box /></el-icon>
                  <p>暂无图片</p>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon :size="60"><Box /></el-icon>
              <p>暂无图片</p>
            </div>
            <div v-if="imageList.length > 1" class="image-thumbs">
              <el-image
                v-for="(img, index) in imageList"
                :key="index"
                :src="img"
                fit="cover"
                class="thumb-img"
                :class="{ active: currentImageIndex === index }"
                @click="currentImageIndex = index"
              />
            </div>
          </el-card>
        </el-col>

        <!-- Right: Info -->
        <el-col :span="12">
          <el-card shadow="never" class="info-card">
            <h1 class="tool-title">{{ tool.name }}</h1>
            <el-descriptions :column="1" border class="tool-descriptions">
              <el-descriptions-item label="分类">
                <el-tag>{{ tool.category || '其他' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="工具状况">
                <el-tag type="success">{{ tool.toolCondition || '未知' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="所在位置">
                {{ tool.location || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="工具状态">
                <el-tag :type="statusType(tool.status)">{{ statusText(tool.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="发布者">
                {{ tool.ownerNickname || tool.owner?.nickname || '匿名' }}
              </el-descriptions-item>
            </el-descriptions>

            <div class="tool-desc">
              <h3>工具描述</h3>
              <p>{{ tool.description || '暂无描述' }}</p>
            </div>

            <el-button
              v-if="tool.status === 'AVAILABLE' || tool.status === 'APPROVED'"
              type="primary"
              size="large"
              class="borrow-btn"
              @click="showBorrowDialog = true"
            >
              <el-icon><Position /></el-icon> 申请借用
            </el-button>
            <el-tag v-else-if="tool.status === 'BORROWED'" type="warning" size="large">
              当前已被借出
            </el-tag>
          </el-card>
        </el-col>
      </el-row>

      <!-- Reviews Section -->
      <el-card shadow="never" class="reviews-card">
        <template #header>
          <span class="section-title">
            <el-icon><ChatDotRound /></el-icon> 用户评价 ({{ reviews.length }})
          </span>
        </template>
        <div v-if="reviews.length > 0">
          <div v-for="review in reviews" :key="review.id" class="review-item">
            <div class="review-header">
              <el-avatar :size="36" :src="review.reviewerAvatar || review.reviewer?.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="review-meta">
                <span class="reviewer-name">{{ review.reviewerNickname || review.reviewer?.nickname || '匿名' }}</span>
                <span class="review-time">{{ formatTime(review.createTime || review.createdAt) }}</span>
              </div>
              <el-rate v-model="review.rating" disabled class="review-rating" />
            </div>
            <div class="review-content">{{ review.content }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无评价" :image-size="80" />
      </el-card>
    </template>

    <!-- Borrow Dialog -->
    <el-dialog v-model="showBorrowDialog" title="申请借用" width="480px">
      <el-form :model="borrowForm" label-width="80px">
        <el-form-item label="工具名称">
          <el-input :model-value="tool?.name" disabled />
        </el-form-item>
        <el-form-item label="借用备注">
          <el-input
            v-model="borrowForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请说明借用用途和预计归还时间..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBorrowDialog = false">取消</el-button>
        <el-button type="primary" :loading="borrowLoading" @click="submitBorrow">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const tool = ref(null)
const reviews = ref([])
const showBorrowDialog = ref(false)
const borrowLoading = ref(false)
const currentImageIndex = ref(0)

const borrowForm = reactive({
  remark: ''
})

const imageList = computed(() => {
  if (!tool.value?.images) return []
  if (typeof tool.value.images === 'string') {
    return tool.value.images.split(',').map(s => s.trim()).filter(Boolean)
  }
  if (Array.isArray(tool.value.images)) return tool.value.images.filter(Boolean)
  return []
})

const statusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'APPROVED': 'success',
    'BORROWED': 'warning',
    'PENDING_REVIEW': 'info',
    'REJECTED': 'danger',
    'OFFLINE': 'info'
  }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = {
    'AVAILABLE': '可借用',
    'APPROVED': '可借用',
    'BORROWED': '已借出',
    'PENDING_REVIEW': '审核中',
    'REJECTED': '已拒绝',
    'OFFLINE': '已下架'
  }
  return map[status] || status || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const fetchTool = async () => {
  loading.value = true
  try {
    const res = await api.tools.getById(route.params.id)
    tool.value = res.data
  } catch (e) {
    ElMessage.error('获取工具详情失败')
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
  try {
    const res = await api.reviews.getByTool(route.params.id)
    reviews.value = res.data || []
  } catch (e) {
    // silent
  }
}

const submitBorrow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  borrowLoading.value = true
  try {
    await api.borrows.apply({
      toolId: parseInt(route.params.id),
      remark: borrowForm.remark
    })
    ElMessage.success('借用申请已提交')
    showBorrowDialog.value = false
    borrowForm.remark = ''
    fetchTool()
  } catch (e) {
    // Error handled by interceptor
  } finally {
    borrowLoading.value = false
  }
}

onMounted(() => {
  fetchTool()
  fetchReviews()
})
</script>

<style scoped>
.tool-detail {
  padding-bottom: 20px;
}

.back-btn {
  margin-bottom: 16px;
  font-size: 15px;
}

.image-card {
  border-radius: 12px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 400px;
  display: block;
  border-radius: 8px;
}

.image-placeholder {
  width: 100%;
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  gap: 8px;
}

.image-thumbs {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  overflow-x: auto;
}

.thumb-img {
  width: 72px;
  height: 72px;
  border-radius: 6px;
  cursor: pointer;
  border: 2px solid transparent;
  flex-shrink: 0;
  transition: border-color 0.2s;
}

.thumb-img.active {
  border-color: #409EFF;
}

.info-card {
  border-radius: 12px;
  height: 100%;
}

.tool-title {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 20px;
}

.tool-descriptions {
  margin-bottom: 20px;
}

.tool-desc {
  margin-bottom: 24px;
}

.tool-desc h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.tool-desc p {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}

.borrow-btn {
  width: 100%;
  font-size: 16px;
}

.reviews-card {
  border-radius: 12px;
  margin-top: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-meta {
  flex: 1;
}

.reviewer-name {
  font-weight: 500;
  color: #303133;
  display: block;
}

.review-time {
  font-size: 12px;
  color: #909399;
}

.review-rating {
  flex-shrink: 0;
}

.review-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  padding-left: 48px;
}
</style>
