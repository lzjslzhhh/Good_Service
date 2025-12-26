<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      <el-tab-pane label="好服务大厅 (我要接单)" name="market">
        <div class="filter-bar">
          <el-select v-model="marketQuery.serviceType" placeholder="按服务类型筛选" clearable style="width: 180px; margin-right: 10px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-input v-model="marketQuery.keyword" placeholder="关键词搜索" style="width: 200px; margin-right: 10px" clearable />
          <el-button type="primary" @click="loadMarket">查询</el-button>
        </div>
        <el-table :data="marketList" border stripe v-loading="loading" style="margin-top: 15px">
          <el-table-column prop="title" label="需求标题" min-width="150" show-overflow-tooltip />
          <el-table-column prop="serviceType" label="类型" width="120">
             <template #default="{ row }">
               <el-tag effect="plain">{{ row.serviceType }}</el-tag>
             </template>
          </el-table-column>
          <el-table-column prop="regionId" label="区域" width="150" />
          <el-table-column prop="description" label="需求描述" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-tag v-if="row.userId === currentUserId" type="info" size="small">
                  我的需求
                </el-tag>
                <el-tag v-else-if="row.hasMyResponse" type="success" size="small">
                  我已参与
                </el-tag>
                <el-button 
                  v-else
                  type="primary" 
                  size="small" 
                  @click="openRespondDialog(row)"
                >
                  立即响应
                </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px; display: flex; justify-content: center;">
          <el-pagination 
            background layout="prev, pager, next" 
            :total="marketTotal" 
            :page-size="marketQuery.pageSize"
            @current-change="handlePageChange"
          />
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的响应记录" name="my-responses">
        <div style="margin-bottom: 10px;">
           <span style="margin-left: 10px; font-size: 12px; color: #999;">仅“已提交”且未被接受的响应可修改或撤销</span>
        </div>
        
        <el-table :data="myResponses" border v-loading="loading">
          <el-table-column prop="needTitle" label="关联的需求" min-width="150" />
          <el-table-column prop="serviceContent" label="我的服务方案" show-overflow-tooltip />
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
                  {{ mapStatus(row.status) }}
                </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div v-if="row.status === 0">
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
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '提交服务响应' : '修改响应信息'" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="关联需求" v-if="dialogType === 'add'">
           <el-input v-model="selectedNeedTitle" disabled />
        </el-form-item>

        <el-form-item label="服务描述" prop="content" :rules="[{ required: true, message: '请输入描述' }]">
          <el-input type="textarea" v-model="form.content" rows="4" placeholder="请描述您的服务内容、优势、上门时间等..." />
        </el-form-item>
        
        <el-form-item label="附件上传">
          <el-upload
            action="#"
            :http-request="customUpload"
            :file-list="fileList"
            :on-remove="handleRemoveFile"
            list-type="text"
          >
            <el-button type="primary" size="small">点击上传图片/视频</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 jpg/png/mp4 文件</div>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { getPublicNeedsList, submitResponse, getMyResponses, updateResponse, deleteResponse, uploadFile } from '@/api/service'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue' // 需要图标

import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.user?.id)
const categories = ['管道维修', '助老服务', '保洁服务', '就诊服务', '营养餐服务', '定期接送服务', '其他']


const activeTab = ref('market')
const loading = ref(false)
const marketList = ref<any[]>([])
const marketTotal = ref(0)
const myResponses = ref<any[]>([])


const marketQuery = reactive({ page: 1, pageSize: 10, serviceType: '', keyword: '' })


const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const selectedNeedTitle = ref('')
const form = reactive({ id: 0, needId: 0, content: '', price: 0, mediaFiles: [] as string[], userId: 0 })
const fileList = ref<any[]>([]) // 用于 el-upload 显示

const mapStatus = (statusCode: number) => {
  switch(statusCode) {
    case 0: return '待接受';
    case 1: return '同意';
    case 2: return '拒绝';
    default: return '取消';
  }
}

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


const loadMyResponses = async () => {
  loading.value = true
  try {
    myResponses.value = await getMyResponses({ responderId: currentUserId.value })
  } finally {
    loading.value = false
  }
}


const handleTabChange = (name: string) => {
  if (name === 'market') loadMarket()
  else loadMyResponses()
}


const openRespondDialog = (row: any) => {
  if (row.userId === currentUserId.value) {
    ElMessage.warning('不能响应自己发布的需求')
    return
  }
  dialogType.value = 'add'
  selectedNeedTitle.value = row.title
  form.id = 0
  form.needId = row.needId
  form.content = ''
  form.mediaFiles = []
  fileList.value = []
  form.userId = currentUserId.value 
  dialogVisible.value = true
}


const openEditDialog = (row: any) => {
  dialogType.value = 'edit'
  form.id = row.id
  form.needId = row.needId
  form.content = row.serviceContent
  form.price = row.price

  dialogVisible.value = true
}


const customUpload = (options: any) => {
  const { file } = options

  uploadFile(file).then((res: any) => {
    form.mediaFiles.push(res.name) // 存文件名
    ElMessage.success(`文件 ${file.name} 上传成功`)

  })
}
const handleRemoveFile = (uploadFile: any) => {
  const name = uploadFile.name
  form.mediaFiles = form.mediaFiles.filter(f => f !== name)
}


const handleSubmit = async () => {
  if (!form.content ) {
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

  if (activeTab.value === 'market') loadMarket() 
  loadMyResponses()
}


const handleDeleteResp = (row: any) => {
  ElMessageBox.confirm('确定要撤销这条服务响应吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteResponse(row.id)
      ElMessage.success('已撤销')
      loadMyResponses()
    })
    .catch(() => {})
}


const handlePageChange = (page: number) => {
  marketQuery.page = page
  loadMarket()
}


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