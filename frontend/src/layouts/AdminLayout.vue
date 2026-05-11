<template>
  <div class="admin-root">
    <header class="admin-top">
      <div class="left">
        <span class="badge">Admin</span>
        <strong>校园二手 · 管理后台</strong>
        <span class="who" v-if="adminInfo">（{{ adminInfo.realName || adminInfo.username }}）</span>
      </div>
      <div class="right">
        <el-button type="text" @click="$router.push('/')">去看前台</el-button>
        <el-button type="danger" size="mini" plain round @click="logout">退出</el-button>
      </div>
    </header>
    <div class="admin-body">
      <aside class="side">
        <el-menu :default-active="active" router background-color="#0f172a" text-color="#cbd5e1" active-text-color="#5eead4">
          <el-menu-item index="/admin/dashboard">工作台</el-menu-item>
          <el-menu-item index="/admin/audit">商品审核</el-menu-item>
          <el-menu-item index="/admin/products">商品管理</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        </el-menu>
      </aside>
      <section class="content">
        <router-view />
      </section>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'AdminLayout',
  computed: {
    ...mapState('admin', ['adminInfo']),
    active() {
      return this.$route.path
    }
  },
  methods: {
    logout() {
      this.$store.commit('admin/CLEAR')
      this.$router.replace('/admin/login')
    }
  }
}
</script>

<style scoped lang="scss">
.admin-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0b1220;
}
.admin-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 22px;
  background: linear-gradient(90deg, #0f172a, #134e4a 55%, #0f172a);
  color: #e2e8f0;
  border-bottom: 1px solid rgba(94, 234, 212, 0.15);
}
.badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(94, 234, 212, 0.15);
  color: #5eead4;
  font-size: 11px;
  margin-right: 10px;
}
.who {
  margin-left: 8px;
  font-size: 13px;
  color: #94a3b8;
}
.admin-body {
  flex: 1;
  display: flex;
  min-height: 0;
}
.side {
  width: 220px;
  background: #0f172a;
  border-right: 1px solid rgba(148, 163, 184, 0.12);
}
.content {
  flex: 1;
  padding: 22px;
  overflow: auto;
  background: radial-gradient(circle at 20% 20%, rgba(45, 212, 191, 0.08), transparent 40%),
    radial-gradient(circle at 80% 0%, rgba(14, 165, 233, 0.1), transparent 35%),
    #0b1220;
  color: #e2e8f0;
}
</style>
