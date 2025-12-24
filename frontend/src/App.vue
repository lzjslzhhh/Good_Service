<template>
  <router-view />
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden; /* 关键代码 1：禁止浏览器的默认滚动条，消灭多余空白 */
  /* 强制使用 border-box，防止 padding 把宽度撑爆 */
  box-sizing: border-box; 
}

#app {
  width: 100%;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 这是一个保险措施，让所有元素都继承 box-sizing */
*, *:before, *:after {
  box-sizing: inherit;
}
</style>