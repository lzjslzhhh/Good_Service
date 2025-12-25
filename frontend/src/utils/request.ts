import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: 'http://localhost:8080', // 后端接口的基础路径
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
    // 假设后端返回格式为 { code: 200, data: ..., message: '' }
    const res = response.data;
    
    // 如果响应状态码不是200，说明有错误
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '操作失败');
      return Promise.reject(new Error(res.message));
    }
    
    // 正常返回数据
    return res;
  },
  (error) => {
    // 处理HTTP错误（如404, 500等）
    if (error.response && error.response.data) {
      const backendError = error.response.data;
      
      // 显示后端返回的错误消息
      const errorMessage = backendError.message || 
                          backendError.error || 
                          error.message || 
                          '请求失败';
      ElMessage.error(errorMessage);
    } else {
      // 网络错误或其他错误
      ElMessage.error(error.message || '网络错误，请检查连接');
    }
    return Promise.reject(error);
  }
);

export default service