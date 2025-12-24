<template>
  <div class="app-container">
    <el-card header="我发布的需求">
      <div style="margin-bottom: 10px; text-align: right">
        <el-button type="primary" @click="openPublishDialog"
          >发布新需求</el-button
        >
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="类型" width="100" />
        <el-table-column label="响应情况" width="120" height="150">
          <template #default="{ row }">
            <el-badge
              :value="row.responseCount"
              class="response-badge"
              :hidden="row.responseCount === 0"
              type="danger"
            >
              <el-button size="small" @click="handleCheckResponses(row)"
                >查看响应</el-button
              >
            </el-badge>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <!-- 需求 1.4: 没有响应时才可修改删除 -->
            <div v-if="row.responseCount === 0">
              <el-button size="small" @click="handleEdit(row)">修改</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)"
                >删除</el-button
              >
            </div>
            <div v-else>
              <el-tag type="info">已有响应锁定</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 响应审核弹窗 -->
      <el-dialog
        v-model="responseDialogVisible"
        title="响应列表处理"
        width="70%"
      >
        <el-table :data="responseList" border>
          <el-table-column prop="responderName" label="服务人员" width="120" />
          <el-table-column prop="responderIntro" label="人员简介" />
          <el-table-column prop="serviceContent" label="服务描述" />
          <el-table-column prop="price" label="报价" width="100" />
          <el-table-column label="附件" width="100">
            <template #default>
              <el-button link type="primary">查看图片</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <div v-if="row.status === 'Submitted'">
                <el-button
                  type="success"
                  size="small"
                  @click="audit(row, 'accept')"
                  >接受</el-button
                >
                <el-button
                  type="danger"
                  size="small"
                  @click="audit(row, 'reject')"
                  >拒绝</el-button
                >
              </div>
              <div v-else>
                <el-tag>{{ row.status }}</el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>

      <!-- 发布/修改弹窗 (含文件上传) -->
      <el-dialog v-model="publishDialogVisible" title="需求信息">
        <el-form :model="form" label-width="100px">
          <el-form-item label="标题"
            ><el-input v-model="form.title"
          /></el-form-item>
          <el-form-item label="文字介绍"
            ><el-input type="textarea" v-model="form.description"
          /></el-form-item>
          <!-- 需求 1.1: 上传图片视频 -->
          <el-form-item label="图片/视频">
            <el-upload action="#" :auto-upload="false" multiple>
              <el-button type="primary">点击上传文件</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">发布</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import type { ResponseItem } from "@/types";
import {
  getMyPublishedNeeds,
  getResponsesByNeedId,
  auditResponse,
} from "@/api/service";
import { ElMessage } from "element-plus";

const tableData = ref([]);
const loading = ref(false);
// 响应管理相关
const responseDialogVisible = ref(false);
const responseList = ref<ResponseItem[]>([]);
// 发布相关
const publishDialogVisible = ref(false);
const form = reactive({ title: "", description: "", category: "" });

const pager = reactive({ page: 1, pageSize: 10 });

const loadData = async () => {
  loading.value = true;
  try {
    const res: any = await getMyPublishedNeeds({
      page: pager.page,
      pageSize: pager.pageSize,
    });
    tableData.value = res.list || res;
  } finally {
    loading.value = false;
  }
};

const handleCheckResponses = async (row: any) => {
  responseList.value = await getResponsesByNeedId(row.id);
  responseDialogVisible.value = true;
};

const audit = async (row: any, action: "accept" | "reject") => {
  await auditResponse(row.id, action);
  ElMessage.success(action === "accept" ? "已接受服务" : "已拒绝服务");
  // 刷新响应列表
  row.status = action === "accept" ? "Accepted" : "Rejected";
};

const openPublishDialog = () => {
  publishDialogVisible.value = true;
};
const handleSave = () => {
  ElMessage.success("发布成功（含文件信息）");
  publishDialogVisible.value = false;
};
const handleEdit = (row: any) => {
  console.log(row);
};
const handleDelete = (row: any) => {
  console.log(row);
};

onMounted(loadData);
</script>

<style scoped>
/* ... 原有的样式 ... */

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 新增的修复样式 */
.response-badge {
  margin-right: 15px; /* 关键：给右边留出15px空隙，防止红点被截断 */
  margin-top: 10px; /* 关键：给上边留点空隙，防止顶住表格线 */
}

/* 强行微调 Element Plus 徽标的位置，让它往里收一点 (可选) */
:deep(.el-badge__content) {
  right: 2px;
  transform: translateY(-50%) translateX(50%); /* 保持默认居中逻辑 */
}
</style>
