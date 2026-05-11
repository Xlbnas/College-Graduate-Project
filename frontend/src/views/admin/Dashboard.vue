<template>
  <div>
    <h2>工作台</h2>
    <p class="muted">5 分钟答辩建议：登录 → 前台发布商品 → 后台审核通过 → 前台搜索加购下单 → 模拟支付 → 卖家发货 → 买家确认收货。</p>
    <el-row :gutter="18">
      <el-col :sm="12" :xs="24">
        <el-card class="card" @click.native="$router.push('/admin/audit')">
          <h3>商品审核</h3>
          <p>处理待审核（PENDING）</p>
        </el-card>
      </el-col>
      <el-col :sm="12" :xs="24">
        <el-card class="card" @click.native="$router.push('/admin/products')">
          <h3>商品管理</h3>
          <p>上下架、删除</p>
        </el-card>
      </el-col>
      <el-col :sm="12" :xs="24">
        <el-card class="card" @click.native="$router.push('/admin/users')">
          <h3>用户管理</h3>
          <p>禁用违规账号</p>
        </el-card>
      </el-col>
      <el-col :sm="12" :xs="24">
        <el-card class="card" @click.native="$router.push('/admin/orders')">
          <h3>订单管理</h3>
          <p>全站流水</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.muted {
  color: #94a3b8;
  margin-bottom: 18px;
}
.card {
  cursor: pointer;
  margin-bottom: 16px;
  background: rgba(15, 23, 42, 0.75) !important;
  border: 1px solid rgba(94, 234, 212, 0.22) !important;
  color: #e2e8f0 !important;
  transition: transform 0.15s ease, border-color 0.15s ease;
}
.card:hover {
  transform: translateY(-3px);
  border-color: rgba(94, 234, 212, 0.45) !important;
}
h3 {
  margin: 0 0 6px;
}
p {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
}
</style>
