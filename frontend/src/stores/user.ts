import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, Role } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 从本地存储恢复时，注意恢复 role
  const user = ref<User | null>(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  function login(userData: User) {
    user.value = userData
    localStorage.setItem('userInfo', JSON.stringify(userData))
    localStorage.setItem('token', userData.token || 'mock-token')
  }

  function logout() {
    user.value = null
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  // 辅助函数：判断是否是管理员
  const isAdmin = () => user.value?.role === 'admin'

  return { user, login, logout, isAdmin }
})