import { defineStore } from 'pinia'
import { api } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    userPoints: (state) => state.user?.points || 0
  },
  actions: {
    async login(credentials) {
      const res = await api.auth.login(credentials)
      // res 现在是 { code, message, data } 格式
      if (res.code === 200) {
        this.token = res.data
        localStorage.setItem('token', this.token)
        await this.fetchProfile()
      }
      return res
    },
    async register(data) {
      const res = await api.auth.register(data)
      return res
    },
    async fetchProfile() {
      if (!this.token) return
      try {
        const res = await api.auth.getProfile()
        if (res.code === 200) {
          this.user = res.data
        }
      } catch (e) {
        this.logout()
      }
    },
    async updateProfile(data) {
      const res = await api.auth.updateProfile(data)
      if (res.code === 200) {
        await this.fetchProfile()
      }
      return res
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    }
  }
})
