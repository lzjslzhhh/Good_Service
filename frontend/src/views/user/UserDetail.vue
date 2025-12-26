<template>
  <div class="app-container">
    <el-card>
      <div class="user-header">
        <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <h2 style="margin-left: 20px;">{{ userInfo.username }}</h2>
      </div>
      
      <el-divider />

      <el-descriptions title="详细资料" border :column="2">
        <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ userInfo.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" span="2">
          {{ userInfo.profile || '这个人很懒，什么都没有留下。' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const userInfo = ref({
  ...userStore.user
})
</script>

<style scoped>
.user-header { display: flex; align-items: center; padding: 20px; }
</style>