<template>
  <div class="error-container">
    <el-result
      icon="warning"
      title="403 无权访问"
      sub-title="抱歉，您当前的身份无法访问此页面。"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goLogin">重新登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const goHome = () => {
  // 根据角色判断回哪里
  if (userStore.isAdmin()) {
    router.push('/admin/stats')
  } else {
    router.push('/my-needs')
  }
}

const goLogin = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.error-container {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
}
</style>