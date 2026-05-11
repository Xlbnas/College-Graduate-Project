import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: [
    {
      path: '/login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '学生登录', guest: true }
    },
    {
      path: '/register',
      component: () => import('@/views/Register.vue'),
      meta: { title: '注册', guest: true }
    },
    {
      path: '/admin/login',
      component: () => import('@/views/admin/AdminLogin.vue'),
      meta: { title: '管理登录', guest: true }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页', public: true } },
        {
          path: 'browse',
          name: 'Browse',
          component: () => import('@/views/Browse.vue'),
          meta: { title: '浏览商品', public: true }
        },
        {
          path: 'product/:id(\\d+)',
          name: 'ProductDetail',
          component: () => import('@/views/ProductDetail.vue'),
          meta: { title: '商品详情', public: true }
        },
        { path: 'publish', name: 'Publish', component: () => import('@/views/Publish.vue'), meta: { title: '发布商品', needUser: true } },
        { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { title: '购物车', needUser: true } },
        { path: 'orders', name: 'Orders', component: () => import('@/views/Orders.vue'), meta: { title: '我的订单', needUser: true } },
        { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心', needUser: true } }
      ]
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { needAdmin: true },
      children: [
        { path: '', redirect: 'dashboard' },
        {
          path: 'dashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '工作台' }
        },
        {
          path: 'audit',
          component: () => import('@/views/admin/ProductAudit.vue'),
          meta: { title: '商品审核' }
        },
        {
          path: 'products',
          component: () => import('@/views/admin/ProductManage.vue'),
          meta: { title: '商品管理' }
        },
        {
          path: 'users',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'orders',
          component: () => import('@/views/admin/OrderManage.vue'),
          meta: { title: '订单管理' }
        }
      ]
    },
    { path: '*', redirect: '/' }
  ]
})

router.beforeEach((to, from, next) => {
  const title = [...to.matched].reverse().find(r => r.meta.title)?.meta.title
  document.title = title ? `${title} · 校园二手交易平台` : '校园二手交易平台'

  const studentToken = store.state.user.token
  const adminToken = store.state.admin.token

  if (to.path === '/admin/login') {
    if (adminToken) return next('/admin/dashboard')
    return next()
  }
  if (['/login', '/register'].includes(to.path)) {
    if (studentToken) return next('/')
    return next()
  }

  const needAdmin = to.matched.some(r => r.meta.needAdmin)
  if (needAdmin) {
    if (!adminToken) return next('/admin/login')
    return next()
  }

  const needUser = to.matched.some(r => r.meta.needUser)
  if (needUser && !studentToken) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
