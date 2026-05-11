<template>
  <div class="login-page">
    <div class="card">
      <h1 class="title">校园二手交易平台</h1>
      <p class="subtitle">登录</p>
      <el-form :model="form" @submit.native.prevent="submit">
        <el-form-item>
          <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-button type="primary" class="btn-login" native-type="submit" :loading="loading">登录</el-button>
        <p class="reg-hint">
          还没有账号？<router-link to="/register" class="link">立即注册</router-link>
        </p>
        <div class="demo-tip">
          <div class="demo-title">测试账号</div>
          <div>学生：<strong>zhangsan</strong> / <strong>123456</strong>，或 <strong>user1</strong> / <strong>user123</strong></div>
          <div class="mt-line">管理员：<strong>admin</strong> / <strong>admin123</strong>（同一登录页，自动进入后台）</div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: 'zhangsan', password: '123456' },
      loading: false
    }
  },
  methods: {
    async submit() {
      this.loading = true
      const silent = { silent: true }
      try {
        try {
          const res = await request.post('/auth/login', this.form, silent)
          this.$store.commit('admin/CLEAR')
          this.$store.commit('user/SET_AUTH', { token: res.data.token, userInfo: res.data.userInfo })
          this.$message.success('登录成功')
          this.$root.$emit('cart-refresh')
          const redirect = this.$route.query.redirect || '/'
          this.$router.replace(redirect)
          return
        } catch (e) {
          /* 学生账号未命中时再尝试管理员 */
        }
        const adminRes = await request.post('/admin/login', this.form, silent)
        this.$store.commit('user/CLEAR')
        this.$store.commit('admin/SET_AUTH', { token: adminRes.data.token, adminInfo: adminRes.data.adminInfo })
        this.$message.success('管理员登录成功')
        const adminRedirect = this.$route.query.redirect
        if (adminRedirect && String(adminRedirect).startsWith('/admin')) {
          this.$router.replace(adminRedirect)
        } else {
          this.$router.replace('/admin/dashboard')
        }
      } catch (e) {
        this.$message.error((e && e.message) || '登录失败，请检查账号密码')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(145deg, #6a5acd 0%, #7b68ee 35%, #5b6cf0 70%, #483d8b 100%);
}
.card {
  width: 100%;
  max-width: 440px;
  background: #fff;
  border-radius: 12px;
  padding: 36px 32px 28px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.2);
}
.title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  text-align: center;
}
.subtitle {
  text-align: center;
  color: #909399;
  margin: 8px 0 24px;
  font-size: 15px;
}
.btn-login {
  width: 100%;
}
.reg-hint {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
  .link {
    color: #409eff;
    font-weight: 600;
    text-decoration: none;
  }
}
.demo-tip {
  margin-top: 20px;
  padding: 12px 14px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
.demo-title {
  font-weight: 700;
  margin-bottom: 6px;
  color: #303133;
}
.mt-line {
  margin-top: 8px;
}
</style>
