<template>
  <div class="app-container">
    <el-card>
      <div class="user-header">
        <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <h2 style="margin-left: 20px;">{{ userInfo.username }}</h2>
      </div>
      
      <el-divider />

      <el-descriptions title="详细资料" border column="2">
        <el-descriptions-item label="用户ID">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">2023-01-15</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ userInfo.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="信用评分">
          <el-rate v-model="userInfo.rating" disabled show-score text-color="#ff9900" />
        </el-descriptions-item>
        <el-descriptions-item label="个人简介" span="2">
          {{ userInfo.intro || '这个人很懒，什么都没有留下。' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
           <el-statistic title="累计发布需求" :value="12" />
        </el-col>
        <el-col :span="12">
           <el-statistic title="累计服务次数" :value="5" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
// 实际场景可能是获取 URL 这里的 ID，这里简单展示当前登录用户
const userInfo = ref({
  ...userStore.user,
  rating: 4.5
})
</script>

<style scoped>
.user-header { display: flex; align-items: center; padding: 20px; }
</style>