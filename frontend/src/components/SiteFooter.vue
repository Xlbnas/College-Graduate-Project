<template>
  <footer class="site-footer">
    <div class="inner">
      <div class="col">
        <strong>校园二手交易平台</strong>
        <p>{{ desc }}</p>
      </div>
      <div class="col">
        <strong>快速链接</strong>
        <router-link to="/">首页</router-link>
        <router-link to="/browse">浏览商品</router-link>
        <router-link to="/publish">发布商品</router-link>
        <router-link to="/orders">我的订单</router-link>
      </div>
      <div class="col">
        <strong>联系我们</strong>
        <p><i class="el-icon-phone" /> 123-4567-8910</p>
        <p><i class="el-icon-message" /> contact@campus-market.com</p>
        <p><i class="el-icon-location-outline" /> 某某大学学生服务中心</p>
      </div>
    </div>
    <div class="copy">© 2026 校园二手交易平台. 保留所有权利。</div>
  </footer>
</template>

<script>
export default {
  name: 'SiteFooter',
  data() {
    return {
      desc: '为校园师生提供便捷的二手交易服务。'
    }
  }
}
</script>

<style scoped lang="scss">
.site-footer {
  background: #2c3e50;
  color: #e2e8f0;
  padding: 32px 20px 12px;
  margin-top: auto;
}
.inner {
  max-width: 1180px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  gap: 28px;
  justify-content: space-between;
}
.col {
  flex: 1;
  min-width: 200px;
  font-size: 13px;
  line-height: 1.6;
  strong {
    display: block;
    margin-bottom: 10px;
    color: #fff;
  }
  a {
    display: block;
    color: #cbd5e1;
    text-decoration: none;
    margin-bottom: 6px;
  }
  a:hover {
    color: #93c5fd;
  }
  p {
    margin: 4px 0;
    color: #94a3b8;
  }
}
.copy {
  text-align: center;
  margin-top: 24px;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: #94a3b8;
}
</style>
