<template>
  <div class="app-container">
    <el-card header="用户管理">
      <!-- 搜索栏 -->
      <div class="filter-bar">
        <el-input
          v-model="queryParams.keyword"
          placeholder="输入用户名或手机号搜索"
          style="width: 250px; margin-right: 10px;"
          clearable
          @clear="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top: 20px">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">
              {{ row.role === 'admin' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="intro" label="个人简介" show-overflow-tooltip />
        <!-- 选作功能：点击查看详情 -->
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" style="margin-top: 20px; display: flex; justify-content: center;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="queryParams.pageSize"
          @current-change="handlePageChange"
        />
      </div>

      <!-- 用户详情弹窗 -->
      <el-dialog v-model="dialogVisible" title="用户详细信息" width="500px">
        <el-descriptions border :column="1">
          <el-descriptions-item label="头像">
            <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          </el-descriptions-item>
          <el-descriptions-item label="ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ currentUser.role }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="简介">{{ currentUser.intro }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserList } from '@/api/admin'
import type { User } from '@/types'

const loading = ref(false)
const tableData = ref<User[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const currentUser = ref<Partial<User>>({})

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchData()
}

const handlePageChange = (page: number) => {
  queryParams.page = page
  fetchData()
}

const handleViewDetail = (row: User) => {
  currentUser.value = row
  dialogVisible.value = true
}

onMounted(fetchData)
</script>