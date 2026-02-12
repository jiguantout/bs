<template>
  <div class="login-container">
    <!-- Animated background -->
    <div class="login-bg">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
      <div class="bg-shape bg-shape-3"></div>
    </div>

    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <div class="login-icon-wrapper">
          <el-icon :size="36" color="#fff"><Tools /></el-icon>
        </div>
        <h2>用户登录</h2>
        <p class="subtitle">欢迎回到社区工具共享平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @keyup.enter="handleLogin"
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
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>还没有账号？</span>
        <el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(form)
    if (res && res.code === 200) {
      ElMessage.success('登录成功，欢迎回来！')
      const redirect = route.query.redirect || '/home'
      router.push(redirect)
    } else {
      ElMessage.error(res?.message || '登录失败，请检查用户名和密码')
    }
  } catch (error) {
    ElMessage.error(error?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ========== Container ========== */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* ========== Animated Background Shapes ========== */
.login-bg {
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
  width: 600px;
  height: 600px;
  top: -200px;
  right: -150px;
  animation: float 8s ease-in-out infinite;
}

.bg-shape-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  animation: float 6s ease-in-out infinite reverse;
}

.bg-shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

/* ========== Card ========== */
.login-card {
  width: 420px;
  border-radius: 20px;
  border: none;
  z-index: 1;
  backdrop-filter: blur(10px);
}

.login-card :deep(.el-card__body) {
  padding: 48px 40px 36px;
}

/* ========== Header ========== */
.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-icon-wrapper {
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

.login-header h2 {
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
.login-card :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: box-shadow 0.2s;
}

.login-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

/* ========== Login Button ========== */
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 6px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  transition: opacity 0.2s, transform 0.2s;
}

.login-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.login-btn:active {
  transform: translateY(0);
}

/* ========== Footer ========== */
.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}

.login-footer .el-link {
  font-size: 14px;
  font-weight: 500;
  margin-left: 4px;
}

/* ========== Responsive ========== */
@media (max-width: 480px) {
  .login-card {
    width: 92%;
    border-radius: 16px;
  }

  .login-card :deep(.el-card__body) {
    padding: 36px 24px 28px;
  }
}
</style>
