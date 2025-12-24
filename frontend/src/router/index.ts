import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/auth/Login.vue') },
    { path: '/register', component: () => import('@/views/auth/Register.vue') },
    {
      path: '/',
      component: MainLayout,
      children: [
        // ========= 普通用户路由 =========
        { 
          path: 'my-needs', 
          component: () => import('@/views/needs/MyNeeds.vue'),
          meta: { roles: ['user'] } 
        },
        { 
          path: 'my-services', 
          component: () => import('@/views/services/MyServices.vue'),
          meta: { roles: ['user'] } 
        },
        { 
          path: 'good-services', 
          component: () => import('@/views/needs/GoodServices.vue'),
          meta: { roles: ['user'] } 
        },
        { 
          path: 'stats', 
          component: () => import('@/views/stats/Statistics.vue'),
          meta: { roles: ['user'] } 
        },
        
        // ========= 管理员路由 =========
        { 
          path: 'admin/stats', 
          component: () => import('@/views/admin/AdminStats.vue'),
          meta: { roles: ['admin'] } 
        },
        {
          path: 'admin/users', // (选作) 用户管理
          component: () => import('@/views/admin/UserList.vue'), // 需自行创建占位文件
          meta: { roles: ['admin'] }
        },
        { 
          path: 'user-detail', 
          component: () => import('@/views/user/UserDetail.vue'),
        },
        // ========= 通用路由 =========
        { path: 'profile', component: () => import('@/views/user/UserProfile.vue') },
        { path: '/403', component: () => import('@/views/error/403.vue') } // 需创建无权限页面
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('token')

  // 1. 未登录
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    return next('/login')
  }

  // 2. 已登录，检查权限
  if (token && to.meta.roles) {
    const roles = to.meta.roles as string[]
    // 如果当前用户角色不在路由允许的角色列表中
    if (userStore.user && !roles.includes(userStore.user.role)) {
      ElMessage.error('无权访问该页面')
      return next('/403') // 或者回到首页
    }
  }

  // 3. 根路径重定向逻辑
  if (to.path === '/' && userStore.user) {
    if (userStore.user.role === 'admin') return next('/admin/stats')
    else return next('/my-needs')
  }

  next()
})

export default router