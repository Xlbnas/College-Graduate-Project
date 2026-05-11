/**
 * 将后端存储的相对路径拼成可直接访问的图片地址。
 */
export function productImageSrc(path) {
  if (!path) {
    return defaultPlaceholder()
  }
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  const origin = window.location.origin
  // 前端 public/demo 静态资源，不经 /api 代理
  if (path.startsWith('/demo/')) {
    return origin + (path.startsWith('/') ? path : '/' + path)
  }
  const base = process.env.NODE_ENV === 'production' ? origin : ''
  return base + '/api' + (path.startsWith('/') ? path : '/' + path)
}

function defaultPlaceholder() {
  return 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="640" height="420" viewBox="0 0 640 420">
      <defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#5eead4"/><stop offset="100%" stop-color="#0ea5e9"/></linearGradient></defs>
      <rect width="100%" height="100%" fill="#f8fafc"/>
      <rect x="48" y="48" width="544" height="324" rx="24" fill="url(#g)" opacity=".35"/>
      <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#0f766e"
        font-family="PingFang SC,Microsoft YaHei,sans-serif" font-size="28" opacity=".75">校园二手 · 好物循环</text>
    </svg>`
  )
}
