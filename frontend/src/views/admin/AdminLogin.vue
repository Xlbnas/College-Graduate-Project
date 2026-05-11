<template>
  <div class="wrap">
    <div class="panel">
      <div class="badge">管理后台</div>
      <h2>管理员登录</h2>
      <p class="muted">商品审核、强制下架、订单总览</p>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-button type="primary" round class="full" :loading="loading" @click="go">进入后台</el-button>
        <el-alert class="mt" title="演示：admin / admin123" type="success" :closable="false" show-icon />
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AdminLogin',
  data() {
    return { form: { username: 'admin', password: 'admin123' }, loading: false }
  },
  methods: {
    async go() {
      this.loading = true
      try {
        const res = await request.post('/admin/login', this.form)
        this.$store.commit('admin/SET_AUTH', { token: res.data.token, adminInfo: res.data.adminInfo })
        this.$message.success('登录成功')
        this.$router.replace('/admin/dashboard')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 20% 20%, rgba(94, 234, 212, 0.14), transparent 40%),
    #0b1220;
  color: #e2e8f0;
  padding: 24px;
}
.panel {
  width: 100%;
  max-width: 400px;
  padding: 28px 24px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 28px 90px rgba(0, 0, 0, 0.45);
}
.badge {
  display: inline-block;
  font-size: 11px;
  letter-spacing: 0.12em;
  color: #5eead4;
  margin-bottom: 8px;
}
.muted {
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 16px;
}
.full {
  width: 100%;
  margin-top: 10px;
}
.mt {
  margin-top: 14px;
}
</style>
