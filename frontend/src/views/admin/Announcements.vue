<template>
  <div class="announcements">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">公告管理</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon> 发布公告
          </el-button>
        </div>
      </template>

      <el-table :data="announcements" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300">
          <template #default="{ row }">
            <span class="content-text">{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" text @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '发布公告' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api'

const announcements = ref([])
const loading = ref(false)
const showDialog = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const form = reactive({
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const res = await api.admin.getAnnouncements()
    if (res.code === 200) {
      announcements.value = res.data || []
    }
  } catch (e) {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  isEdit.value = false
  editId.value = null
  form.title = ''
  form.content = ''
  showDialog.value = true
}

const openEditDialog = (item) => {
  isEdit.value = true
  editId.value = item.id
  form.title = item.title
  form.content = item.content
  showDialog.value = true
}

const resetForm = () => {
  form.title = ''
  form.content = ''
  isEdit.value = false
  editId.value = null
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      const res = await api.admin.updateAnnouncement(editId.value, {
        title: form.title,
        content: form.content
      })
      if (res.code === 200) {
        ElMessage.success('修改成功')
      }
    } else {
      const res = await api.admin.createAnnouncement({
        title: form.title,
        content: form.content
      })
      if (res.code === 200) {
        ElMessage.success('发布成功')
      }
    }
    showDialog.value = false
    fetchAnnouncements()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(`确定要删除公告「${item.title}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await api.admin.deleteAnnouncement(item.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchAnnouncements()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchAnnouncements()
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

.content-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #606266;
}
</style>
