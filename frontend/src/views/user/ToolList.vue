<template>
  <div class="tool-list">
    <h2 class="page-title">工具列表</h2>

    <!-- Search bar -->
    <el-card class="search-card" shadow="never">
      <el-row :gutter="16" align="middle">
        <el-col :span="10">
          <el-input
            v-model="keyword"
            placeholder="搜索工具名称..."
            :prefix-icon="Search"
            clearable
            size="large"
            @keyup.enter="fetchTools"
          />
        </el-col>
        <el-col :span="8">
          <el-select
            v-model="category"
            placeholder="选择分类"
            clearable
            size="large"
            style="width: 100%"
          >
            <el-option
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :value="cat"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" size="large" @click="fetchTools" :icon="Search">
            搜索
          </el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Tool grid -->
    <el-row :gutter="20" v-if="tools.length > 0" class="tool-grid">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="tool in tools" :key="tool.id">
        <el-card shadow="hover" class="tool-card" @click="router.push(`/tools/${tool.id}`)">
          <el-image
            :src="getFirstImage(tool.images)"
            fit="cover"
            class="tool-img"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="40"><Box /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="tool-info">
            <h3 class="tool-name">{{ tool.name }}</h3>
            <div class="tool-meta">
              <el-tag size="small">{{ tool.category || '其他' }}</el-tag>
              <el-tag size="small" type="success" v-if="tool.toolCondition">{{ tool.toolCondition }}</el-tag>
            </div>
            <div class="tool-details">
              <span class="tool-location" v-if="tool.location">
                <el-icon><Location /></el-icon> {{ tool.location }}
              </span>
            </div>
            <div class="tool-footer">
              <span class="tool-owner" v-if="tool.ownerNickname || tool.owner?.nickname">
                <el-icon><User /></el-icon> {{ tool.ownerNickname || tool.owner?.nickname || '匿名' }}
              </span>
              <el-button type="primary" size="small" text>
                查看详情 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Empty state -->
    <el-empty v-else description="暂无工具，换个关键词试试" :image-size="120" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()

const keyword = ref('')
const category = ref('')
const tools = ref([])

const categories = ['电动工具', '手动工具', '测量工具', '园艺工具', '清洁工具', '其他']

const getFirstImage = (images) => {
  if (!images) return ''
  if (typeof images === 'string') return images.split(',')[0].trim()
  if (Array.isArray(images)) return images[0] || ''
  return ''
}

const fetchTools = async () => {
  try {
    const params = {}
    if (keyword.value) params.keyword = keyword.value
    if (category.value) params.category = category.value
    const res = await api.tools.getList(params)
    tools.value = res.data || []
  } catch (e) {
    // silent
  }
}

const resetSearch = () => {
  keyword.value = ''
  category.value = ''
  fetchTools()
}

onMounted(() => {
  fetchTools()
})
</script>

<style scoped>
.tool-list {
  padding-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.search-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.tool-grid {
  margin-top: 4px;
}

.tool-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.tool-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.tool-card :deep(.el-card__body) {
  padding: 0;
}

.tool-img {
  width: 100%;
  height: 180px;
  display: block;
}

.image-placeholder {
  width: 100%;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}

.tool-info {
  padding: 16px;
}

.tool-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tool-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.tool-details {
  margin-bottom: 8px;
}

.tool-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.tool-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tool-owner {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
</style>
