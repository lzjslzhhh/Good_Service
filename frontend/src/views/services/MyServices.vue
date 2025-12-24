<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      
      <!-- Tab 1: 服务大厅 (浏览需求并响应) -->
      <el-tab-pane label="好服务大厅 (我要接单)" name="market">
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-select v-model="marketQuery.category" placeholder="按服务类型筛选" clearable style="width: 180px; margin-right: 10px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-input v-model="marketQuery.keyword" placeholder="关键词搜索" style="width: 200px; margin-right: 10px" clearable />
          <el-button type="primary" @click="loadMarket">查询</el-button>
        </div>

        <!-- 市场列表 -->
        <el-table :data="marketList" border stripe v-loading="loading" style="margin-top: 15px">
          <el-table-column prop="title" label="需求标题" min-width="150" show-overflow-tooltip />
          <el-table-column prop="category" label="类型" width="120">
             <template #default="{ row }">
               <el-tag effect="plain">{{ row.category }}</el-tag>
             </template>
          </el-table-column>
          <el-table-column prop="region" label="区域" width="150" />
          <el-table-column prop="description" label="需求描述" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <!-- 如果API返回 hasMyResponse=true，则禁用按钮 -->
              <el-button 
                v-if="!row.hasMyResponse" 
                type="primary" 
                size="small" 
                @click="openRespondDialog(row)"
              >
                立即响应
              </el-button>
              <el-tag v-else type="success">我已参与</el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div style="margin-top: 20px; display: flex; justify-content: center;">
          <el-pagination 
            background layout="prev, pager, next" 
            :total="marketTotal" 
            :page-size="marketQuery.pageSize"
            @current-change="handlePageChange"
          />
        </div>
      </el-tab-pane>

      <!-- Tab 2: 我的响应管理 -->
      <el-tab-pane label="我的响应记录" name="my-responses">
        <div style="margin-bottom: 10px;">
           <el-button icon="Refresh" circle @click="loadMyResponses" />
           <span style="margin-left: 10px; font-size: 12px; color: #999;">仅“已提交”且未被接受的响应可修改或撤销</span>
        </div>
        
        <el-table :data="myResponses" border v-loading="loading">
          <el-table-column prop="needTitle" label="关联的需求" min-width="150" />
          <el-table-column prop="serviceContent" label="我的服务方案" show-overflow-tooltip />
          <el-table-column prop="price" label="报价(元)" width="100" />
          <el-table-column label="附件" width="100">
            <template #default="{ row }">
              <el-popover placement="top" trigger="hover" v-if="row.mediaFiles && row.mediaFiles.length">
                <template #reference>
                   <el-tag size="small">查看附件</el-tag>
                </template>
                <div v-for="file in row.mediaFiles" :key="file">{{ file }}</div>
              </el-popover>
              <span v-else style="color:#ccc">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="当前状态" width="120">
            <template #default="{ row }">
               <el-tag :type="getStatusType(row.status)">
                 {{ row.status === 'Submitted' ? '待审核' : (row.status === 'Accepted' ? '已接受' : '已拒绝') }}
               </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <!-- 只有 Submitted 状态（未被发布者处理）才能修改删除 -->
              <div v-if="row.status === 'Submitted'">
                <el-button size="small" type="primary" link @click="openEditDialog(row)">修改</el-button>
                <el-button size="small" type="danger" link @click="handleDeleteResp(row)">撤销</el-button>
              </div>
              <div v-else>
                <el-text type="info" size="small">已锁定</el-text>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 通用响应表单弹窗 (新增/修改) -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '提交服务响应' : '修改响应信息'" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="关联需求" v-if="dialogType === 'add'">
           <el-input v-model="selectedNeedTitle" disabled />
        </el-form-item>

        <el-form-item label="服务描述" prop="content" :rules="[{ required: true, message: '请输入描述' }]">
          <el-input type="textarea" v-model="form.content" rows="4" placeholder="请描述您的服务内容、优势、上门时间等..." />
        </el-form-item>
        
        <el-form-item label="预估报价" prop="price" :rules="[{ required: true, message: '请输入报价' }]">
          <el-input-number v-model="form.price" :min="0" :step="10" />
          <span style="margin-left: 10px">元</span>
        </el-form-item>

        <el-form-item label="附件上传">
          <!-- 模拟上传 -->
          <el-upload
            action="#"
            :http-request="customUpload"
            :file-list="fileList"
            :on-remove="handleRemoveFile"
            list-type="text"
          >
            <el-button type="primary" size="small">点击上传图片/视频</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 jpg/png/mp4 文件，模拟上传</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getPublicNeedsList, submitResponse, getMyResponses, updateResponse, deleteResponse, uploadFile } from '@/api/service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue' // 需要图标

const categories = ['管道维修', '助老服务', '保洁服务', '就诊服务', '营养餐服务', '定期接送服务', '其他']

// 状态
const activeTab = ref('market')
const loading = ref(false)
const marketList = ref<any[]>([])
const marketTotal = ref(0)
const myResponses = ref<any[]>([])

// 查询参数
const marketQuery = reactive({ page: 1, pageSize: 10, category: '', keyword: '' })

// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const selectedNeedTitle = ref('')
const form = reactive({ id: 0, needId: 0, content: '', price: 0, mediaFiles: [] as string[] })
const fileList = ref<any[]>([]) // 用于 el-upload 显示

// ================= 逻辑方法 =================

// 1. 加载市场需求
const loadMarket = async () => {
  loading.value = true
  try {
    const res: any = await getPublicNeedsList(marketQuery)
    marketList.value = res.list
    marketTotal.value = res.total
  } finally {
    loading.value = false
  }
}

// 2. 加载我的响应
const loadMyResponses = async () => {
  loading.value = true
  try {
    myResponses.value = await getMyResponses()
  } finally {
    loading.value = false
  }
}

// 3. Tab 切换
const handleTabChange = (name: string) => {
  if (name === 'market') loadMarket()
  else loadMyResponses()
}

// 4. 打开响应弹窗 (新增)
const openRespondDialog = (row: any) => {
  dialogType.value = 'add'
  selectedNeedTitle.value = row.title
  // 重置表单
  form.id = 0
  form.needId = row.id
  form.content = ''
  form.price = 0
  form.mediaFiles = []
  fileList.value = []
  
  dialogVisible.value = true
}

// 5. 打开修改弹窗
const openEditDialog = (row: any) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.needId = row.needId
  form.content = row.serviceContent
  form.price = row.price
  // 恢复文件列表
  form.mediaFiles = row.mediaFiles || []
  fileList.value = form.mediaFiles.map((f: string) => ({ name: f, url: f }))
  
  dialogVisible.value = true
}

// 6. 文件上传逻辑 (Mock)
const customUpload = (options: any) => {
  const { file } = options
  // 调用 API 模拟上传
  uploadFile(file).then((res: any) => {
    form.mediaFiles.push(res.name) // 存文件名
    ElMessage.success(`文件 ${file.name} 上传成功`)
    // 这里不调用 options.onSuccess 因为我们手动管理了 fileList
  })
}
const handleRemoveFile = (uploadFile: any) => {
  const name = uploadFile.name
  form.mediaFiles = form.mediaFiles.filter(f => f !== name)
}

// 7. 提交表单
const handleSubmit = async () => {
  if (!form.content || !form.price) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (dialogType.value === 'add') {
    await submitResponse(form)
    ElMessage.success('响应已提交，请等待发布者审核')
  } else {
    await updateResponse(form)
    ElMessage.success('修改成功')
  }
  
  dialogVisible.value = false
  // 刷新数据
  if (activeTab.value === 'market') loadMarket() // 刷新为了更新 hasMyResponse 状态
  loadMyResponses()
}

// 8. 撤销响应
const handleDeleteResp = (row: any) => {
  ElMessageBox.confirm('确定要撤销这条服务响应吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteResponse(row.id)
      ElMessage.success('已撤销')
      loadMyResponses()
    })
    .catch(() => {})
}

// 分页
const handlePageChange = (page: number) => {
  marketQuery.page = page
  loadMarket()
}

// 状态颜色辅助
const getStatusType = (status: string) => {
  if (status === 'Accepted') return 'success'
  if (status === 'Rejected') return 'danger'
  return 'warning'
}

onMounted(() => {
  loadMarket()
})
</script>

<style scoped>
.filter-bar { display: flex; align-items: center; margin-bottom: 10px; }
</style>