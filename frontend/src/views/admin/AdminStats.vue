<template>
  <div class="app-container">
    <el-card header="平台运营数据分析">
      <!-- 筛选条件 -->
      <div class="filter-bar" style="margin-bottom: 20px;">
        <el-date-picker
          v-model="dateRange"
          type="monthrange"
          range-separator="至"
          start-placeholder="起始年月"
          end-placeholder="终止年月"
          value-format="YYYY-MM"
          style="margin-right: 10px;"
        />
        <el-input 
          v-model="region" 
          placeholder="地域 (如: 北京市)" 
          style="width: 200px; margin-right: 10px;" 
          clearable 
        />
        <el-button type="primary" @click="loadStats">开始分析</el-button>
      </div>

      <!-- 核心修改：使用 Tabs 标签页切换视图 -->
      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
        
        <!-- 视图 1: ECharts 图表 -->
        <el-tab-pane label="趋势分析 (图表)" name="chart">
          <div class="chart-wrapper" style="padding: 10px;">
            <div ref="chartRef" style="width: 100%; height: 450px;"></div>
          </div>
        </el-tab-pane>

        <!-- 视图 2: 统计明细列表 -->
        <el-tab-pane label="统计明细 (列表)" name="table">
          <el-table :data="tableData" border stripe show-summary style="width: 100%">
            <el-table-column prop="month" label="月份" sortable />
            <el-table-column prop="region" label="地域" />
            <el-table-column prop="publishCount" label="月累计发布需求数" sortable align="center">
               <template #default="{ row }">
                 <el-tag>{{ row.publishCount }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column prop="successCount" label="月累计响应成功数" sortable align="center">
               <template #default="{ row }">
                 <el-tag type="success">{{ row.successCount }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column label="成交转化率" align="center">
              <template #default="{ row }">
                <span :style="{ color: getRateColor(row) }">
                  {{ row.publishCount ? ((row.successCount / row.publishCount) * 100).toFixed(1) : 0 }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { getMonthlyStats } from '@/api/admin'
import dayjs from 'dayjs'

// 默认最近半年
const end = dayjs().format('YYYY-MM')
const start = dayjs().subtract(5, 'month').format('YYYY-MM')

const dateRange = ref([start, end])
const region = ref('')
const tableData = ref<any[]>([])

// Tabs 状态管理
const activeTab = ref('chart') // 默认显示图表
const chartRef = ref<HTMLElement>()
let myChart: echarts.ECharts | null = null

const loadStats = async () => {
  const params = {
    start: dateRange.value?.[0] || start,
    end: dateRange.value?.[1] || end,
    region: region.value
  }
  
  // 调用 API 获取数据
  const res = await getMonthlyStats(params)
  tableData.value = res
  
  // 如果当前在图表页，立即渲染；如果不是，切换Tab时会自动渲染
  if (activeTab.value === 'chart') {
    renderChart()
  }
}

const renderChart = () => {
  // 必须使用 nextTick，因为切换 Tab 时 DOM 可能还没完全显示（width为0），导致 ECharts 渲染失败
  nextTick(() => {
    if (!chartRef.value) return
    
    // 销毁旧实例，防止数据残留
    if (myChart) {
      myChart.dispose()
    }
    
    myChart = echarts.init(chartRef.value)

    const months = tableData.value.map(item => item.month)
    const publishData = tableData.value.map(item => item.publishCount)
    const successData = tableData.value.map(item => item.successCount)

    const option = {
      title: { text: '服务供需趋势分析', left: 'center' ,bottom: '93%'},
      tooltip: { trigger: 'axis' },
      legend: { data: ['发布需求数', '响应成功数'], top: '30px' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true, top: '80px' },
      xAxis: { 
        type: 'category', 
        boundaryGap: false, 
        data: months 
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '发布需求数',
          type: 'line',
          data: publishData,
          smooth: true,
          areaStyle: { opacity: 0.3 },
          itemStyle: { color: '#409EFF' },
          markPoint: {
            data: [
              { type: 'max', name: '最大值' },
              { type: 'min', name: '最小值' }
            ]
          }
        },
        {
          name: '响应成功数',
          type: 'line',
          data: successData,
          smooth: true,
          areaStyle: { opacity: 0.3 },
          itemStyle: { color: '#67C23A' }
        }
      ]
    }

    myChart.setOption(option)
  })
}

// 监听 Tab 切换事件
const handleTabChange = (name: string | number) => {
  // 只有切换到 'chart' 且有数据时才渲染图表
  // 这样可以解决从表格切回图表时，图表空白或宽度不正确的问题
  if (name === 'chart' && tableData.value.length > 0) {
    renderChart()
  }
}

// 监听窗口缩放，自适应图表
window.addEventListener('resize', () => {
  if (activeTab.value === 'chart') {
    myChart?.resize()
  }
})

const getRateColor = (row: any) => {
  if (!row.publishCount) return '#606266'
  const rate = row.successCount / row.publishCount
  if (rate > 0.8) return 'green'
  if (rate < 0.3) return 'red'
  return '#606266'
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.chart-wrapper {
  background: #fff;
}
</style>