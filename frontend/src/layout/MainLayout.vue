<template>
  <el-container class="layout-container">
    <!-- 左侧边栏 -->
    <el-aside width="220px" class="aside">
      <!-- Logo 区域 -->
      <div class="logo">
        <el-icon class="logo-icon"><Platform /></el-icon>
        <span class="logo-text">{{ userStore.isAdmin() ? '"好服务"后台管理' : '"好服务"用户平台' }}</span>
      </div>

      <el-menu
        active-text-color="#409EFF"
        background-color="#304156"
        text-color="#bfcbd9"
        :default-active="$route.path"
        router
        class="el-menu-vertical"
      >
        <!-- ============ 管理员菜单 ============ -->
        <template v-if="userStore.user?.role === 'admin'">
          <el-menu-item index="/admin/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>统计分析</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </template>

        <!-- ============ 普通用户菜单 ============ -->
        <template v-else>
          <el-menu-item index="/good-services">
            <!-- 优化：这里我把图标换成了 Search，更符合“查询”的语义 -->
            <el-icon><Search /></el-icon>
            <span>好服务 (需求查询)</span>
          </el-menu-item>

          <el-menu-item index="/my-needs">
            <el-icon><List /></el-icon>
            <span>我需要 (发布管理)</span>
          </el-menu-item>
          
          <el-menu-item index="/my-services">
            <el-icon><Service /></el-icon>
            <span>我服务 (响应管理)</span>
          </el-menu-item>

          <el-menu-item index="/stats">
            <el-icon><TrendCharts /></el-icon>
            <span>统计分析</span>
          </el-menu-item>
        </template>

        <!-- ============ 公共菜单 ============ -->
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息修改</span>
        </el-menu-item>
        
        <el-menu-item index="/user-detail">
          <el-icon><InfoFilled /></el-icon>
          <span>用户详情</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container class="main-wrapper">
      <!-- Header 精装修 -->
      <el-header class="header">
        <!-- 左侧：面包屑导航 -->
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/good-services' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ routeName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 右侧：用户信息 + 头像下拉 -->
        <div class="header-right">
          <el-avatar 
            :size="32" 
            class="user-avatar"
            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" 
          />
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userStore.user?.username || '用户' }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user-detail">个人中心</el-dropdown-item>
                <el-dropdown-item command="role" disabled>
                  当前角色：{{ userStore.user?.role === 'admin' ? '管理员' : '普通用户' }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" style="color: #f56c6c;">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main 内容区：灰色背景 + 过渡动画 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
      
      <!-- 底部版权 -->
      <el-footer class="footer">
        © 2025  "好服务"平台系统
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { 
  List, Service, User, TrendCharts, Search, Platform, UserFilled, ArrowDown, InfoFilled 
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 动态计算面包屑名称
const routeName = computed(() => {
  const map: Record<string, string> = {
    'good-services': '好服务 (需求查询)',
    'my-needs': '我需要 (发布管理)',
    'my-services': '我服务 (响应管理)',
    'stats': '统计分析', // 普通用户的统计
    'profile': '个人信息修改',
    'user-detail': '用户详情',
    'admin-stats': '统计分析', // 管理员的统计
    'admin-users': '用户管理'
  }
  // 取路径最后一段作为 key
  const pathKey = route.path.split('/').pop() || ''
  // 处理 admin/stats 这种情况
  if (route.path.includes('admin/stats')) return '运营统计'
  if (route.path.includes('admin/users')) return '用户管理'
  
  return map[pathKey] || '当前页面'
})

// 下拉菜单处理
const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'user-detail') {
    router.push('/user-detail')
  }
}
</script>

<style scoped>
/* 1. 整体布局 */
.layout-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  overflow: hidden;
}

/* 2. 侧边栏美化 */
.aside {
  background-color: #304156;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2b3649;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  overflow: hidden;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #409EFF;
}

.el-menu-vertical {
  border-right: none;
}

/* 3. 右侧主体区域 */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background-color: #f0f2f5; /* 灰色背景 */
}

/* 4. 顶栏 Header 精装修 */
.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 9;
}

.header-right {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  margin-right: 8px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #333;
  font-weight: 500;
}

/* 5. 内容区域 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 6. 底部版权 */
.footer {
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: #999;
  font-size: 12px;
  background: #f0f2f5;
}

/* 7. 页面切换动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>