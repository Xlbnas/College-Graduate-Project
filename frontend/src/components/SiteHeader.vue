<template>
  <header class="site-header">
    <div class="inner">
      <router-link class="brand" to="/">
        <span class="swap">⇄</span>
        <span>校园二手交易平台</span>
      </router-link>
      <nav class="nav">
        <router-link to="/" exact>首页</router-link>
        <router-link to="/browse">浏览商品</router-link>
        <router-link to="/publish" v-if="token">发布商品</router-link>
        <router-link to="/orders" v-if="token">我的订单</router-link>
      </nav>
      <div class="right">
        <div class="search">
          <el-input v-model="kw" placeholder="搜索商品..." clearable size="small" @keyup.enter.native="goSearch" />
          <el-button type="primary" icon="el-icon-search" size="small" @click="goSearch" />
        </div>
        <router-link to="/cart" class="cart-link" v-if="token">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <i class="el-icon-shopping-cart-2" />
          </el-badge>
        </router-link>
        <template v-if="!token">
          <el-button class="ghost" size="small" round @click="$router.push('/login')">登录</el-button>
          <el-button class="ghost" size="small" round @click="$router.push('/register')">注册</el-button>
        </template>
        <el-dropdown v-else trigger="click" @command="onUserCmd">
          <span class="user-dd">
            <i class="el-icon-user-solid" />
            {{ user && user.username }}
            <i class="el-icon-arrow-down el-icon--right" />
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="cart">购物车</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button type="text" class="admin-link" @click="$router.push('/admin/login')">管理</el-button>
      </div>
    </div>
  </header>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'SiteHeader',
  data() {
    return { kw: '' }
  },
  computed: {
    ...mapState('user', ['token', 'userInfo']),
    ...mapState('cart', ['itemCount']),
    user() {
      return this.userInfo
    },
    cartCount() {
      return this.itemCount
    }
  },
  watch: {
    '$route.query.keyword'(v) {
      this.kw = v || ''
    }
  },
  mounted() {
    this.kw = this.$route.query.keyword || ''
  },
  methods: {
    goSearch() {
      const q = (this.kw || '').trim()
      this.$router.push({ path: '/browse', query: q ? { keyword: q } : {} })
    },
    onUserCmd(cmd) {
      if (cmd === 'profile') this.$router.push('/profile')
      else if (cmd === 'cart') this.$router.push('/cart')
      else if (cmd === 'logout') {
        this.$store.commit('user/CLEAR')
        this.$store.commit('cart/SET_ITEM_COUNT', 0)
        this.$message.success('已退出')
        if (this.$route.meta.needUser) this.$router.push('/')
      }
    }
  }
}
</script>

<style scoped lang="scss">
$blue: #409eff;

.site-header {
  background: $blue;
  color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
.inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 16px;
  height: 56px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-weight: 700;
  text-decoration: none;
  white-space: nowrap;
  .swap {
    font-size: 20px;
    opacity: 0.95;
  }
}
.nav {
  flex: 1;
  display: flex;
  gap: 4px;
  margin-left: 12px;
  a {
    color: rgba(255, 255, 255, 0.92);
    text-decoration: none;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
  }
  a.router-link-active {
    background: rgba(255, 255, 255, 0.18);
    font-weight: 600;
  }
}
.right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}
.search {
  display: flex;
  align-items: center;
  gap: 0;
  ::v-deep .el-input__inner {
    border-radius: 4px 0 0 4px;
    width: 200px;
  }
  .el-button {
    border-radius: 0 4px 4px 0;
  }
}
.ghost {
  background: transparent !important;
  border-color: #fff !important;
  color: #fff !important;
}
.cart-link {
  color: #fff;
  font-size: 22px;
  line-height: 1;
  margin: 0 4px;
}
.cart-badge ::v-deep .el-badge__content {
  border: none;
}
.user-dd {
  cursor: pointer;
  color: #fff;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.admin-link {
  color: rgba(255, 255, 255, 0.85) !important;
  margin-left: 4px;
  font-size: 13px;
}
</style>
