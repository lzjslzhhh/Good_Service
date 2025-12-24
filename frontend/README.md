# GoodService 项目安装配置文档

## 项目概述

GoodService 是一个基于 Vue 3 + TypeScript + Vite 的服务管理平台项目。

## 系统要求

- **Node.js**: ^20.19.0 或 >=22.12.0
- **npm**: 最新版本
- **推荐操作系统**: macOS/Windows/Linux

## 环境检查

在开始安装前，请确保您的系统已安装正确版本的 Node.js：

```bash
node --version
# 应该显示 v20.19.0 或 v22.12.0 及以上版本

npm --version
# 确保是最新版本
```

## 项目安装步骤

### 1. 克隆项目

如果您还没有项目代码，请先克隆项目：

```bash
git clone https://github.com/lzjslzhhh/GoodService.git
```

### 2. 安装依赖

使用 npm 安装项目所需的所有依赖：

```bash
npm install
```

### 3. 开发环境启动

启动开发服务器（支持热重载）：

```bash
npm run dev
```

启动后，浏览器会自动打开 http://localhost:5173（或其他可用端口）

### 4. 构建生产版本

构建用于生产环境的优化版本：

```bash
npm run build
```

### 5. 预览生产构建

预览构建后的生产版本：

```bash
npm run preview
```

### 6. 类型检查

单独运行 TypeScript 类型检查：

```bash
npm run type-check
```

### 7. 仅构建（跳过类型检查）

跳过类型检查进行构建：

```bash
npm run build-only
```

## 开发工具配置

### 项目技术栈

- **前端框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **UI 组件库**: Element Plus
- **HTTP 客户端**: Axios
- **图表库**: ECharts
- **类型检查**: vue-tsc

## 项目结构

```
src/
├── api/           # API 接口
├── assets/        # 静态资源
├── components/    # 组件
├── layout/        # 布局组件
├── router/        # 路由配置
├── stores/        # 状态管理
├── types/         # 类型定义
├── utils/         # 工具函数
└── views/         # 页面视图
```

## 常见问题

### Q: 端口被占用怎么办？
A: Vite 会自动选择可用端口，或手动指定端口：`npm run dev -- --port 3000`

### Q: 类型检查失败？
A: 确保安装了 TypeScript 支持，可以使用 `npm run type-check` 检查具体错误

### Q: 依赖安装失败？
A: 清除缓存重新安装：
```bash
npm cache clean --force
npm install
```

## 开发规范

- 使用 TypeScript 进行开发
- 遵循 Vue 3 组合式 API 风格
- 使用 ESLint + Prettier 进行代码格式化（如果配置了）
- 组件文件名使用 PascalCase 命名
- TypeScript 严格模式和类型检查

## 部署说明

构建后的文件位于 `dist/` 目录，可直接部署到静态文件服务器。

---