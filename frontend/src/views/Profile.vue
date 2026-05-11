<template>
  <div>
    <h2 class="hero-title">个人中心</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="我的发布" name="pub">
        <el-table :data="published" size="small">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="price" label="价格" width="90" />
          <el-table-column prop="status" label="状态" width="110" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的收藏" name="fav">
        <el-table :data="favs" size="small" v-loading="favLoad">
          <el-table-column label="商品">
            <template slot-scope="{ row }">
              <el-button type="text" @click="$router.push('/product/' + row.productId)">{{ row.title }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的售出" name="sold">
        <el-table :data="soldRows" size="small" v-loading="soldLoad">
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column label="状态" width="100">
            <template slot-scope="{ row }">{{ orderLabel(row.status) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="{ row }">
              <el-button v-if="row.status === 'PAID'" type="text" @click="ship(row)">模拟发货</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Profile',
  data() {
    return {
      tab: 'pub',
      published: [],
      favs: [],
      favLoad: false,
      soldRows: [],
      soldLoad: false
    }
  },
  watch: {
    tab(v) {
      if (v === 'fav') this.loadFav()
      if (v === 'sold') this.loadSold()
    }
  },
  created() {
    this.loadPub()
  },
  methods: {
    orderLabel(s) {
      const M = { WAIT_PAY: '待支付', PAID: '待发货', SHIPPED: '待收货', COMPLETED: '已完成' }
      return M[s] || s
    },
    async loadPub() {
      const res = await request.get('/my/products')
      this.published = res.data || []
    },
    async loadFav() {
      this.favLoad = true
      try {
        const res = await request.get('/favorites')
        const list = res.data || []
        const rows = []
        for (const f of list) {
          let title = '#' + f.productId
          try {
            const d = await request.get('/products/' + f.productId)
            title = d.data.title
          } catch (e) { /* ignore */ }
          rows.push({ ...f, title })
        }
        this.favs = rows
      } finally {
        this.favLoad = false
      }
    },
    async loadSold() {
      this.soldLoad = true
      try {
        const res = await request.get('/my/sales-orders', { params: { pageNum: 1, pageSize: 50 } })
        this.soldRows = res.data.records || []
      } finally {
        this.soldLoad = false
      }
    },
    async ship(row) {
      await request.post('/orders/' + row.id + '/ship')
      this.$message.success('已标记发货')
      this.loadSold()
    }
  }
}
</script>
