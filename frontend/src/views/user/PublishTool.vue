<template>
  <div class="publish-tool">
    <h2 class="page-title">{{ isEdit ? '编辑工具' : '发布工具' }}</h2>

    <el-row :gutter="24">
      <el-col :span="14">
        <el-card shadow="never" class="form-card">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            size="large"
          >
            <el-form-item label="工具名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入工具名称" clearable />
            </el-form-item>

            <el-form-item label="工具分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="cat in categories"
                  :key="cat"
                  :label="cat"
                  :value="cat"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="工具状况" prop="toolCondition">
              <el-select v-model="form.toolCondition" placeholder="请选择工具状况" style="width: 100%">
                <el-option label="全新" value="全新" />
                <el-option label="良好" value="良好" />
                <el-option label="一般" value="一般" />
              </el-select>
            </el-form-item>

            <el-form-item label="详细描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="5"
                placeholder="请详细描述工具的品牌、型号、使用注意事项等信息"
              />
            </el-form-item>

            <el-form-item label="所在位置" prop="location">
              <el-input v-model="form.location" placeholder="例如：XX小区X栋" clearable />
            </el-form-item>

            <el-form-item label="图片链接" prop="images">
              <el-input
                v-model="form.images"
                type="textarea"
                :rows="3"
                placeholder="请输入图片URL，多个链接用逗号分隔"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleSubmit">
                {{ isEdit ? '保存修改' : '发布工具' }}
              </el-button>
              <el-button @click="router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- Preview -->
      <el-col :span="10">
        <el-card shadow="never" class="preview-card">
          <template #header>
            <span class="preview-title">预览效果</span>
          </template>
          <div class="preview-content">
            <el-image
              v-if="previewImages.length > 0"
              :src="previewImages[0]"
              fit="cover"
              class="preview-img"
            >
              <template #error>
                <div class="preview-img-placeholder">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="preview-img-placeholder">
              <el-icon :size="40"><Picture /></el-icon>
              <p>暂无图片</p>
            </div>
            <h3 class="preview-name">{{ form.name || '工具名称' }}</h3>
            <div class="preview-tags">
              <el-tag v-if="form.category" size="small">{{ form.category }}</el-tag>
              <el-tag v-if="form.toolCondition" size="small" type="success">{{ form.toolCondition }}</el-tag>
            </div>
            <p class="preview-location" v-if="form.location">
              <el-icon><Location /></el-icon> {{ form.location }}
            </p>
            <p class="preview-desc">{{ form.description || '工具描述将显示在这里...' }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)
const formRef = ref(null)
const loading = ref(false)

const categories = ['电动工具', '手动工具', '测量工具', '园艺工具', '清洁工具', '其他']

const form = reactive({
  name: '',
  category: '',
  toolCondition: '',
  description: '',
  location: '',
  images: ''
})

const rules = {
  name: [{ required: true, message: '请输入工具名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  toolCondition: [{ required: true, message: '请选择工具状况', trigger: 'change' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入所在位置', trigger: 'blur' }]
}

const previewImages = computed(() => {
  if (!form.images) return []
  return form.images.split(',').map(s => s.trim()).filter(Boolean)
})

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = { ...form }
    if (isEdit.value) {
      await api.tools.update(route.query.id, data)
      ElMessage.success('修改成功')
    } else {
      await api.tools.publish(data)
      ElMessage.success('发布成功，等待审核')
    }
    router.push('/my-tools')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

const fetchToolForEdit = async () => {
  if (!isEdit.value) return
  try {
    const res = await api.tools.getById(route.query.id)
    const tool = res.data
    form.name = tool.name || ''
    form.category = tool.category || ''
    form.toolCondition = tool.toolCondition || ''
    form.description = tool.description || ''
    form.location = tool.location || ''
    form.images = Array.isArray(tool.images) ? tool.images.join(', ') : (tool.images || '')
  } catch (e) {
    ElMessage.error('获取工具信息失败')
  }
}

onMounted(() => {
  fetchToolForEdit()
})
</script>

<style scoped>
.publish-tool {
  padding-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.form-card {
  border-radius: 12px;
}

.preview-card {
  border-radius: 12px;
  position: sticky;
  top: 84px;
}

.preview-title {
  font-size: 16px;
  font-weight: 600;
}

.preview-img {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  display: block;
  margin-bottom: 12px;
}

.preview-img-placeholder {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
  color: #c0c4cc;
  margin-bottom: 12px;
  gap: 4px;
}

.preview-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.preview-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.preview-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.preview-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>
