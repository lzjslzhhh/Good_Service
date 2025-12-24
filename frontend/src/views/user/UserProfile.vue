<template>
  <div class="app-container">
    <el-card header="个人信息修改">
      <!-- 只读信息 -->
      <el-descriptions border column="1" style="margin-bottom: 20px">
        <el-descriptions-item label="用户名">{{ userStore.user?.username }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ userStore.user?.id }}</el-descriptions-item>
      </el-descriptions>

      <!-- 可修改表单 -->
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="用户简介" prop="intro">
          <el-input type="textarea" v-model="form.intro" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="不修改请留空" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user.ts'
import { validatePassword } from '@/utils/validate'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'

const userStore = useUserStore()
const formRef = ref<FormInstance>()

const form = reactive({
  phone: userStore.user?.phone || '1234567890',
  intro: userStore.user?.intro || '哈哈哈哈',
  password: ''
})

// 自定义校验：如果密码为空则不校验，否则走严格校验
const checkPass = (rule: any, value: string, callback: any) => {
  if (!value) return callback()
  validatePassword(rule, value, callback)
}

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ validator: checkPass, trigger: 'blur' }]
}

const handleSave = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      // 需求8: CUD操作需要有确认提示框
      ElMessageBox.confirm('确定要修改个人信息吗？', '提示', { type: 'warning' })
        .then(async () => {
           // await updateUserApi(form)
           ElMessage.success('修改成功')
        })
    }
  })
}
</script>