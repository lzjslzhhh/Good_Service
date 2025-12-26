<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>系统登录</h2>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="loading"
            style="width: 100%"
            size="large"
            >登录</el-button
          >
        </el-form-item>
        <div style="text-align: right">
          <el-link type="primary" @click="$router.push('/register')"
            >没有账号？去注册</el-link
          >
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import type { User as UserType } from "@/types";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage, type FormInstance } from "element-plus";
import { User as UserIcon, Lock } from "@element-plus/icons-vue";
import { login as loginApi } from "@/api/user";

const router = useRouter();
const userStore = useUserStore();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({ username: "lz", password: "Lz1234" }); // 默认填好方便测试

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleLogin = async () => {

  loading.value = true;
  try {
    const res = await loginApi({
      username: form.username,
      password: form.password,
    });

    userStore.login(res);
    ElMessage.success("登录成功");
    if (res.role === "admin") router.push("/admin/stats");
    else router.push("/my-needs");
  } catch (e: any) {
    ElMessage.error(e?.message || "登录失败");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2d3a4b;
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
</style>
