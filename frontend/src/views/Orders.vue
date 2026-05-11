<template>
  <div>
    <div class="page-head">
      <i class="el-icon-s-order" />
      <span>我的订单</span>
    </div>
    <div v-loading="loading" class="order-list">
      <el-card v-for="row in rows" :key="row.id" shadow="never" class="order-card">
        <div class="hdr">
          <span>订单号: {{ row.orderNo || row.id }}</span>
          <el-tag v-if="row.status === 'WAIT_PAY'" type="warning" size="small">待支付</el-tag>
          <el-tag v-else-if="row.status === 'PAID'" type="info" size="small">待发货</el-tag>
          <el-tag v-else-if="row.status === 'SHIPPED'" type="" size="small">待收货</el-tag>
          <el-tag v-else-if="row.status === 'COMPLETED'" type="success" size="small">已完成</el-tag>
          <el-tag v-else type="info" size="small">{{ row.status }}</el-tag>
        </div>
        <div class="body">
          <div class="line">
            <span>{{ row.productTitle || '商品' }}</span>
            <span class="muted">¥{{ row.totalPrice }} x {{ row.quantity }}</span>
          </div>
          <el-divider />
          <div class="line strong">
            <span>总计</span>
            <span class="red">¥{{ row.totalPrice }}</span>
          </div>
          <div class="meta" v-if="row.sellerDisplayName">卖家：{{ row.sellerDisplayName }}</div>
          <div class="meta" v-if="row.address">收货地址：{{ row.address }}</div>
          <div class="meta" v-if="row.contactPhone">联系电话：{{ row.contactPhone }}</div>
          <div class="meta">下单时间：{{ fmt(row.createTime) }}</div>
          <div v-if="row.reviewContent" class="review-box">
            <div class="review-label">我的评价</div>
            <div class="review-text">{{ row.reviewContent }}</div>
            <div class="review-time">评价时间：{{ fmt(row.reviewTime) }}</div>
          </div>
        </div>
        <div class="foot">
          <template v-if="row.status === 'WAIT_PAY'">
            <el-button size="small" @click="fakeCancel">取消订单</el-button>
            <el-button type="primary" size="small" @click="pay(row)">支付</el-button>
          </template>
          <template v-else-if="row.status === 'COMPLETED'">
            <el-button size="small" @click="$message.info('演示：可跳转商品再次购买')">再次购买</el-button>
            <el-button
              v-if="!row.reviewContent"
              type="primary"
              size="small"
              plain
              @click="openReview(row)"
            >评价</el-button>
          </template>
          <template v-else-if="row.status === 'SHIPPED'">
            <el-button type="success" size="small" @click="recv(row)">确认收货</el-button>
          </template>
        </div>
      </el-card>
    </div>
    <el-empty v-if="!loading && !rows.length" description="暂无订单" />

    <el-dialog title="订单评价" :visible.sync="reviewVisible" width="480px" @closed="resetReview">
      <el-input
        v-model="reviewForm.content"
        type="textarea"
        :rows="4"
        maxlength="500"
        show-word-limit
        placeholder="说说本次交易体验（提交后买卖双方可见）"
      />
      <span slot="footer">
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">提交评价</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Orders',
  data() {
    return {
      rows: [],
      loading: false,
      reviewVisible: false,
      reviewSubmitting: false,
      reviewForm: { orderId: null, content: '' }
    }
  },
  created() {
    this.load()
  },
  methods: {
    fmt(t) {
      if (!t) return ''
      const s = String(t).replace('T', ' ')
      return s.slice(0, 19).replace(/-/g, '/')
    },
    async load() {
      this.loading = true
      try {
        const res = await request.get('/orders', { params: { pageNum: 1, pageSize: 50 } })
        this.rows = res.data.records || []
      } finally {
        this.loading = false
      }
    },
    fakeCancel() {
      this.$message.info('演示环境：取消订单请联系管理员或自行忽略待支付单')
    },
    async pay(row) {
      await request.post('/orders/' + row.id + '/pay')
      this.$message.success('支付成功（模拟）')
      this.load()
    },
    async recv(row) {
      await request.post('/orders/' + row.id + '/receive')
      this.$message.success('已确认收货')
      this.load()
    },
    openReview(row) {
      this.reviewForm.orderId = row.id
      this.reviewForm.content = ''
      this.reviewVisible = true
    },
    resetReview() {
      this.reviewForm.orderId = null
      this.reviewForm.content = ''
      this.reviewSubmitting = false
    },
    async submitReview() {
      const text = (this.reviewForm.content || '').trim()
      if (!text) {
        this.$message.warning('请填写评价内容')
        return
      }
      this.reviewSubmitting = true
      try {
        await request.post('/orders/' + this.reviewForm.orderId + '/review', { content: text })
        this.$message.success('评价已提交')
        this.reviewVisible = false
        this.load()
      } finally {
        this.reviewSubmitting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.page-head {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
  .el-icon-s-order {
    margin-right: 8px;
  }
}
.order-card {
  margin-bottom: 16px;
  border-radius: 8px;
}
.hdr {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
}
.body {
  margin-top: 12px;
  font-size: 14px;
}
.line {
  display: flex;
  justify-content: space-between;
  &.strong {
    font-weight: 700;
  }
}
.red {
  color: #f56c6c;
}
.muted {
  color: #909399;
}
.meta {
  font-size: 13px;
  color: #606266;
  margin-top: 6px;
}
.review-box {
  margin-top: 12px;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}
.review-label {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}
.review-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}
.review-time {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
.foot {
  margin-top: 14px;
  text-align: right;
  .el-button + .el-button {
    margin-left: 8px;
  }
}
</style>
