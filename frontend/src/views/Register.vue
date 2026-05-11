<template>
  <div class="auth-bg">
    <div class="panel">
      <h1 class="hero-title page-h1">注册校园二手账号</h1>
      <p class="sub">学号 + 手机号完成实名登记，一起安全交易</p>
      <el-form :model="form" :rules="rules" ref="f" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="登录名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="如 20219999" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="11 位手机号" />
        </el-form-item>
        <el-button type="primary" round class="btn" :loading="loading" @click="submit">注册</el-button>
        <div class="tips">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Register',
  data() {
    return {
      loading: false,
      form: { username: '', password: '', studentId: '', phone: '' },
      rules: {
        username: [{ required: true, message: '必填', trigger: 'blur' }],
        password: [{ required: true, message: '必填', trigger: 'blur' }],
        studentId: [{ required: true, message: '必填', trigger: 'blur' }],
        phone: [
          { required: true, message: '必填', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submit() {
      this.$refs.f.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await request.post('/auth/register', this.form)
          this.$message.success('注册成功')
          this.$router.push('/login')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.auth-bg {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 48px 24px 24px;
  background: radial-gradient(circle at 10% 20%, rgba(45, 212, 191, 0.22), transparent 35%),
    radial-gradient(circle at 90% 10%, rgba(14, 165, 233, 0.2), transparent 40%),
    linear-gradient(160deg, #ecfdf5, #f8fafc 40%, #e0f2fe);
}
.page-h1 {
  margin-bottom: 4px;
}
.panel {
  width: 100%;
  max-width: 420px;
  padding: 32px 28px 24px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.12);
}
.sub {
  color: #64748b;
  margin-bottom: 18px;
  font-size: 14px;
}
.btn {
  width: 100%;
  margin-top: 10px;
}
.tips {
  text-align: center;
  margin-top: 12px;
  a {
    color: #0f766e;
    font-weight: 600;
  }
}
</style>
