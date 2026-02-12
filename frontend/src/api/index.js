import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：添加 JWT Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：自动解包 ApiResponse
request.interceptors.response.use(
  (response) => {
    // 直接返回后端 ApiResponse 对象 { code, message, data }
    return response.data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    // 尝试返回后端的错误信息
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

export const api = {
  auth: {
    login(data) {
      return request.post('/auth/login', data)
    },
    register(data) {
      return request.post('/auth/register', data)
    },
    getProfile() {
      return request.get('/auth/profile')
    },
    updateProfile(data) {
      return request.put('/auth/profile', data)
    }
  },
  tools: {
    getList(params) {
      return request.get('/tools', { params })
    },
    getById(id) {
      return request.get(`/tools/${id}`)
    },
    publish(data) {
      return request.post('/tools', data)
    },
    update(id, data) {
      return request.put(`/tools/${id}`, data)
    },
    delete(id) {
      return request.delete(`/tools/${id}`)
    },
    getMy() {
      return request.get('/tools/my')
    }
  },
  borrows: {
    apply(data) {
      return request.post('/borrows', data)
    },
    getMy() {
      return request.get('/borrows/my')
    },
    getReceived() {
      return request.get('/borrows/received')
    },
    approve(id) {
      return request.put(`/borrows/${id}/approve`)
    },
    reject(id) {
      return request.put(`/borrows/${id}/reject`)
    },
    pickup(id) {
      return request.put(`/borrows/${id}/pickup`)
    },
    return_(id) {
      return request.put(`/borrows/${id}/return`)
    }
  },
  reviews: {
    create(data) {
      return request.post('/reviews', data)
    },
    getByTool(toolId) {
      return request.get(`/reviews/tool/${toolId}`)
    },
    getMy() {
      return request.get('/reviews/my')
    }
  },
  points: {
    getRanking() {
      return request.get('/points/ranking')
    },
    getMy() {
      return request.get('/points/my')
    }
  },
  notifications: {
    getList() {
      return request.get('/notifications')
    },
    getUnreadCount() {
      return request.get('/notifications/unread-count')
    },
    markRead(id) {
      return request.put(`/notifications/${id}/read`)
    },
    markAllRead() {
      return request.put('/notifications/read-all')
    }
  },
  announcements: {
    getPublic() {
      return request.get('/announcements/public')
    }
  },
  // 管理员 API
  admin: {
    getDashboard() {
      return request.get('/admin/dashboard')
    },
    getUsers() {
      return request.get('/admin/users')
    },
    updateUserStatus(id, status) {
      return request.put(`/admin/users/${id}/status`, { status })
    },
    getTools() {
      return request.get('/admin/tools')
    },
    auditTool(id, data) {
      return request.put(`/admin/tools/${id}/audit`, data)
    },
    offlineTool(id) {
      return request.put(`/admin/tools/${id}/offline`)
    },
    getAnnouncements() {
      return request.get('/admin/announcements')
    },
    createAnnouncement(data) {
      return request.post('/admin/announcements', data)
    },
    updateAnnouncement(id, data) {
      return request.put(`/admin/announcements/${id}`, data)
    },
    deleteAnnouncement(id) {
      return request.delete(`/admin/announcements/${id}`)
    }
  }
}

export default api
