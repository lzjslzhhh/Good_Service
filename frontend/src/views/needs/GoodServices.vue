<template>
  <div class="app-container">
    <el-card header="好服务 - 需求大厅">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="服务类型">
          <el-select v-model="queryParams.serviceType" placeholder="请选择类型" clearable style="width: 180px">
            <el-option v-for="item in categories" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="输入类型名称或标题" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="title" label="需求标题" min-width="150" />
        <el-table-column prop="serviceType" label="服务类型" width="120">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.serviceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="regionId" label="区域" width="150" />
        <el-table-column label="发布时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'warning' : 'success'">
              {{ row.status === '1' ? '已取消' : '已发布' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="queryParams.pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPublicNeedsList } from '@/api/service'
import type { NeedItem, ServiceCategory } from '@/types'

const categories: ServiceCategory[] = ['管道维修', '助老服务', '保洁服务', '就诊服务', '营养餐服务', '定期接送服务', '其他']

const loading = ref(false)
const tableData = ref<NeedItem[]>([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  serviceType: '',
  keyword: ''
})
// 添加日期格式化函数
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};
const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getPublicNeedsList(queryParams)
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

const resetQuery = () => {
  queryParams.serviceType = ''
  queryParams.keyword = ''
  handleSearch()
}

const handlePageChange = (page: number) => {
  queryParams.page = page
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.filter-form { margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: center; }
</style>