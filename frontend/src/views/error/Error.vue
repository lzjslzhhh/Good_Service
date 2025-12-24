<template>
  <div class="error-page">
    <el-empty description="哎呀！页面走丢了 (404)">
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.error-page {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
}
</style>