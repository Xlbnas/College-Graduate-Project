<template>
  <div>
    <h2>订单管理</h2>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="userId" label="买家 ID" width="90" />
      <el-table-column prop="productId" label="商品 ID" width="90" />
      <el-table-column prop="totalPrice" label="金额" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'OrderManage',
  data() {
    return { rows: [], loading: false }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await request.get('/admin/orders', { params: { pageNum: 1, pageSize: 200 } })
        this.rows = res.data.records || []
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
