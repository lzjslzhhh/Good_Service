<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <h2>用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="简介" prop="intro">
          <el-input v-model="form.intro" placeholder="基本信息" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading"
            >注册</el-button
          >
          <el-button link @click="$router.push('/login')"
            >已有账号？登录</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { validatePassword } from "@/utils/validate";
import { ElMessage, type FormInstance } from "element-plus";
import { register as registerApi } from "@/api/user";

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: "",
  intro: "",
  password: "",
});

const rules = {
  username: [{ required: true, message: "必填", trigger: "blur" }],
  password: [{ validator: validatePassword, trigger: "blur" }],
};

const handleRegister = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await registerApi(form);
        ElMessage.success("注册成功");
        router.push("/login");
      } catch (e: any) {
        ElMessage.error(e?.message || "注册失败");
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.auth-card {
  width: 400px;
}
</style>
