import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/components/UserLayout.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/user/Home.vue')
      },
      {
        path: 'tools',
        name: 'Tools',
        component: () => import('@/views/user/ToolList.vue')
      },
      {
        path: 'tools/:id',
        name: 'ToolDetail',
        component: () => import('@/views/user/ToolDetail.vue')
      },
      {
        path: 'publish',
        name: 'Publish',
        component: () => import('@/views/user/PublishTool.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'my-tools',
        name: 'MyTools',
        component: () => import('@/views/user/MyTools.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'borrows',
        name: 'Borrows',
        component: () => import('@/views/user/BorrowRecords.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'borrow-requests',
        name: 'BorrowRequests',
        component: () => import('@/views/user/BorrowRequests.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'rankings',
        name: 'Rankings',
        component: () => import('@/views/user/Rankings.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/user/Notifications.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'tools',
        name: 'AdminTools',
        component: () => import('@/views/admin/ToolAudit.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/Announcements.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth) {
    if (!userStore.token) {
      return next({ path: '/login', query: { redirect: to.fullPath } })
    }
    if (!userStore.user) {
      await userStore.fetchProfile()
    }
    if (to.meta.requiresAdmin && !userStore.isAdmin) {
      return next('/home')
    }
  }

  next()
})

export default router
