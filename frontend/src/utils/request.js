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

function rejectAsError(msg) {
  const e = new Error(msg || '请求失败')
  return Promise.reject(e)
}

service.interceptors.response.use(
  res => {
    const body = res.data
    if (!body || typeof body !== 'object') {
      return rejectAsError('无效的响应')
    }
    const code = Number(body.code)
    const silent = res.config && res.config.silent
    if (code === 500) {
      if (!silent) Message.error(body.msg || '请求失败')
      return rejectAsError(body.msg || '请求失败')
    }
    if (body.code != null && code !== 200) {
      if (!silent) Message.error(body.msg || '请求失败')
      return rejectAsError(body.msg || '请求失败')
    }
    return body
  },
  err => {
    const d = err.response && err.response.data
    let msg = '网络异常，请检查网络或后端是否已启动'
    if (d && typeof d === 'object' && d.msg) msg = d.msg
    else if (typeof d === 'string' && d) msg = d
    else if (err.code === 'ECONNABORTED' || (err.message && err.message.includes('timeout'))) {
      msg = '请求超时，请稍后重试'
    } else if (err.message === 'Network Error') {
      msg = '无法连接服务器，请确认后端已启动且地址正确'
    } else if (err.message) msg = err.message
    const silent = err.config && err.config.silent
    if (!silent) Message.error(msg)
    return rejectAsError(msg)
  }
)

export default service
