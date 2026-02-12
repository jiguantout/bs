<template>
  <div class="register-container">
    <!-- Animated background -->
    <div class="register-bg">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
      <div class="bg-shape bg-shape-3"></div>
    </div>

    <el-card class="register-card" shadow="always">
      <div class="register-header">
        <div class="register-icon-wrapper">
          <el-icon :size="36" color="#fff"><UserFilled /></el-icon>
        </div>
        <h2>用户注册</h2>
        <p class="subtitle">加入社区工具共享平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称"
            :prefix-icon="UserFilled"
            clearable
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            <span v-if="!loading">注 册</span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <el-link type="primary" :underline="false" @click="router.push('/login')">立即登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Phone } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      phone: form.phone
    })
    ElMessage.success('注册成功，请登录您的账号')
    router.push('/login')
  } catch (error) {
    // Error already handled by axios interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ========== Container ========== */
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* ========== Animated Background Shapes ========== */
.register-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  background: #fff;
}

.bg-shape-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.bg-shape-2 {
  width: 350px;
  height: 350px;
  bottom: -80px;
  right: -80px;
  animation: float 6s ease-in-out infinite reverse;
}

.bg-shape-3 {
  width: 180px;
  height: 180px;
  top: 40%;
  right: 20%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

/* ========== Card ========== */
.register-card {
  width: 440px;
  border-radius: 20px;
  border: none;
  z-index: 1;
  backdrop-filter: blur(10px);
}

.register-card :deep(.el-card__body) {
  padding: 44px 40px 32px;
}

/* ========== Header ========== */
.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
}

.register-header h2 {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 700;
  color: #1d2129;
  letter-spacing: 2px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

/* ========== Form ========== */
.register-card :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: box-shadow 0.2s;
}

.register-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.register-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

/* ========== Register Button ========== */
.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 6px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: opacity 0.2s, transform 0.2s;
}

.register-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.register-btn:active {
  transform: translateY(0);
}

/* ========== Footer ========== */
.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}

.register-footer .el-link {
  font-size: 14px;
  font-weight: 500;
  margin-left: 4px;
}

/* ========== Responsive ========== */
@media (max-width: 480px) {
  .register-card {
    width: 92%;
    border-radius: 16px;
  }

  .register-card :deep(.el-card__body) {
    padding: 32px 20px 24px;
  }
}
</style>
