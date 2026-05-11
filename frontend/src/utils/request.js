import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 20000
})

service.interceptors.request.use(config => {
  const isAdminRoute = router.currentRoute.path.startsWith('/admin')
  const token = isAdminRoute ? localStorage.getItem('adminToken') : localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  res => {
    const body = res.data
    if (body && body.code === 500) {
      Message.error(body.msg || '请求失败')
      return Promise.reject(body)
    }
    return body
  },
  err => {
    const msg = (err.response && err.response.data && err.response.data.msg) || err.message || '网络错误'
    Message.error(msg)
    return Promise.reject(err)
  }
)

export default service
