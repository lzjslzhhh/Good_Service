import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api', // 后端接口的基础路径
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 假设后端返回格式为 { code: 200, data: ..., msg: '' }
    const res = response.data
    // 这里模拟直接返回数据，实际根据后端结构调整
    return res
  },
  (error) => {
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service