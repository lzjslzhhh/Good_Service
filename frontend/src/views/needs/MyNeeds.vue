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
        <el-table-column prop="serviceType" label="类型" width="100" />
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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{
                row.status === 0
                  ? "已发布"
                  : row.status === -1
                  ? "已取消"
                  : row.status
              }}
            </el-tag>
          </template>
        </el-table-column>
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
              <div v-if="row.status === 0">
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
                <el-tag>{{ "已锁定" }}</el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
      <el-dialog v-model="publishDialogVisible" title="需求信息">
        <el-form :model="form" label-width="100px" ref="formRef">
          <el-form-item label="标题"
            ><el-input v-model="form.title"
          /></el-form-item>
          <el-form-item label="服务类型" prop="category">
            <el-select
              v-model="form.category"
              placeholder="请选择服务类型"
              style="width: 100%"
            >
              <el-option label="管道维修" value="管道维修" />
              <el-option label="助老服务" value="助老服务" />
              <el-option label="保洁服务" value="保洁服务" />
              <el-option label="就诊服务" value="就诊服务" />
              <el-option label="营养餐服务" value="营养餐服务" />
              <el-option label="定期接送服务" value="定期接送服务" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="所在地区" prop="region">
            <el-select v-model="form.region" placeholder="请选择地区">
              <el-option
                v-for="region in regions"
                :key="region.value"
                :label="region.label"
                :value="region.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="文字介绍"
            ><el-input type="textarea" v-model="form.description"
          /></el-form-item>
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
      <el-dialog v-model="editDialogVisible" title="修改需求" width="600px">
        <el-form :model="editForm" ref="editFormRef" label-width="100px">
          <el-form-item label="标题"
            ><el-input v-model="editForm.title"
          /></el-form-item>
          <el-form-item label="服务类型" prop="category">
            <el-select
              v-model="editForm.serviceType"
              placeholder="请选择服务类型"
              style="width: 100%"
            >
              <el-option label="管道维修" value="管道维修" />
              <el-option label="助老服务" value="助老服务" />
              <el-option label="保洁服务" value="保洁服务" />
              <el-option label="就诊服务" value="就诊服务" />
              <el-option label="营养餐服务" value="营养餐服务" />
              <el-option label="定期接送服务" value="定期接送服务" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="所在地区" prop="region">
            <el-select v-model="editForm.region" placeholder="请选择地区">
              <el-option
                v-for="region in regions"
                :key="region.value"
                :label="region.label"
                :value="region.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="文字介绍"
            ><el-input type="textarea" v-model="form.description"
          /></el-form-item>
          <el-form-item label="图片/视频">
            <el-upload action="#" :auto-upload="false" multiple>
              <el-button type="primary">点击上传文件</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">发布</el-button>
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
  publishNeed,
  updateNeed,
  deleteNeed,
} from "@/api/service";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const tableData = ref([]);
const loading = ref(false);
const responseDialogVisible = ref(false);
const responseList = ref<ResponseItem[]>([]);
const publishDialogVisible = ref(false);
const formRef = ref();
const form = reactive({
  category: "", 
  title: "",
  description: "",
  region: "", // 新增地区选择
  mediaFiles: [] as string[],
});
const editDialogVisible = ref(false);
const editForm = reactive({
  id: "",
  serviceType: "",
  title: "",
  description: "",
  region: "",
});

const regions = ref([
  { label: "朝阳区", value: "朝阳区" },
  { label: "海淀区", value: "海淀区" },
  { label: "浦东新区", value: "浦东新区" },
  { label: "黄埔区", value: "黄浦区" },
  { label: "天河区", value: "天河区" },
  { label: "南山区", value: "南山区" },
]);

const formRules = {
  category: [{ required: true, message: "请选择服务类型", trigger: "change" }],
  title: [{ required: true, message: "请输入需求标题", trigger: "blur" }],
  description: [{ required: true, message: "请输入需求描述", trigger: "blur" }],
  region: [{ required: true, message: "请选择地区", trigger: "change" }],
};
const pager = reactive({ page: 1, pageSize: 10 });

const handleCheckResponses = async (row: any) => {
  responseList.value = await getResponsesByNeedId(row.needId);
  responseDialogVisible.value = true;
};

const audit = async (row: any, action: "accept" | "reject") => {
  await auditResponse(row.id, action);
  ElMessage.success(action === "accept" ? "已接受服务" : "已拒绝服务");

  row.status = action === "accept" ? 1 : 2;
};

const openPublishDialog = () => {
  publishDialogVisible.value = true;
};
const fetchData = async (resetPage = false) => {
  try {
    loading.value = true;
    if (resetPage) pager.page = 1; 

    const res = await getMyPublishedNeeds({
      page: pager.page,
      pageSize: pager.pageSize,
      userId: userStore.user?.id, // 添加用户ID
    });

    tableData.value = res.list;
  } catch (error) {
    ElMessage.error("获取数据失败");
  } finally {
    loading.value = false;
  }
};
// 初始化加载数据
onMounted(() => {
  fetchData();
});

// 修改发布方法
const handleSave = () => {
  formRef.value.validate(async (valid) => {
    try {
      await publishNeed({
        title: form.title,
        description: form.description,
        serviceType: form.category,
        region: form.region, // 传递地区ID
        userId: userStore.user?.id, // 传递当前用户ID
      });

      ElMessage.success("发布成功");
      publishDialogVisible.value = false;
      fetchData(); // 刷新列表
    } catch (error) {
      ElMessage.error(error.message || "发布失败");
    }
  });
};
const handleEdit = (row: any) => {
  editForm.id = row.needId;
  editForm.serviceType = row.serviceType;
  editForm.title = row.title;
  editForm.description = row.description;
  editForm.region = row.region;
  editDialogVisible.value = true;
};
const handleEditSubmit = async () => {
  try {
    await updateNeed(editForm);
    ElMessage.success("修改成功");
    editDialogVisible.value = false;
    fetchData();
  } catch (error) {
    ElMessage.error("修改失败");
  }
};
const handleDelete = async (row: any) => {
  // 确认对话框
  ElMessageBox.confirm("确定要删除这个需求吗？删除后无法恢复。", "删除确认", {
    confirmButtonText: "确定删除",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteNeed(row.needId, userStore.user?.id);
        ElMessage.success("删除成功");
        fetchData(); // 刷新列表
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {

    });
};
</script>

<style scoped>


.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.response-badge {
  margin-right: 15px; 
  margin-top: 10px; 
}


:deep(.el-badge__content) {
  right: 2px;
  transform: translateY(-50%) translateX(50%); 
}
</style>
