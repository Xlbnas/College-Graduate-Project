<template>
  <el-dialog
    :visible.sync="innerVisible"
    width="720px"
    custom-class="pd-dialog"
    @close="onClose"
  >
    <div slot="title" class="dlg-title">
      <span>商品详情</span>
    </div>
    <div v-loading="loading" v-if="p" class="body">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-image :src="img(p.imageUrl)" fit="cover" class="cover" />
        </el-col>
        <el-col :span="12">
          <h2 class="name">{{ p.title }}</h2>
          <p class="cat">{{ p.categoryName || '分类' }}</p>
          <p class="price">¥{{ p.price }}</p>
          <div class="block">
            <strong>商品描述</strong>
            <p>{{ p.description || '暂无描述' }}</p>
          </div>
          <div class="block">
            <strong>卖家信息</strong>
            <p>{{ p.sellerUsername || '—' }}</p>
          </div>
          <div class="block">
            <strong>发布时间</strong>
            <p>{{ fmt(p.createTime) }}</p>
          </div>
        </el-col>
      </el-row>
    </div>
    <span slot="footer" class="footer-btns">
      <el-button @click="innerVisible = false">关闭</el-button>
      <el-button type="primary" icon="el-icon-shopping-cart-2" :disabled="!token" @click="addCart">加入购物车</el-button>
    </span>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'
import { productImageSrc } from '@/utils/image'
import { mapState } from 'vuex'

export default {
  name: 'ProductDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    productId: { type: [Number, String], default: null }
  },
  data() {
    return {
      innerVisible: false,
      loading: false,
      p: null
    }
  },
  computed: {
    ...mapState('user', ['token'])
  },
  watch: {
    visible: {
      immediate: true,
      handler(v) {
        this.innerVisible = v
        if (v && this.productId) this.load()
      }
    },
    innerVisible(v) {
      this.$emit('update:visible', v)
    },
    productId() {
      if (this.innerVisible) this.load()
    }
  },
  methods: {
    img(u) {
      return productImageSrc(u)
    },
    fmt(t) {
      if (!t) return '—'
      return String(t).replace('T', ' ').slice(0, 19)
    },
    async load() {
      if (!this.productId) return
      this.loading = true
      try {
        const res = await request.get('/products/' + this.productId)
        this.p = res.data
      } catch (e) {
        this.p = null
      } finally {
        this.loading = false
      }
    },
    onClose() {
      this.p = null
    },
    async addCart() {
      if (!this.p) return
      await request.post('/cart', { productId: this.p.id, quantity: 1 })
      this.$message.success('已加入购物车')
      this.$emit('cart-changed')
    }
  }
}
</script>

<style lang="scss">
.pd-dialog .el-dialog__header {
  background: #409eff;
  padding: 12px 16px;
  margin: 0;
}
.pd-dialog .el-dialog__title {
  color: #fff;
  font-weight: 600;
}
.pd-dialog .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}
.pd-dialog .el-dialog__body {
  padding: 20px;
}
</style>
<style scoped lang="scss">
.dlg-title {
  color: #fff;
}
.cover {
  width: 100%;
  height: 260px;
  border-radius: 8px;
  background: #eee;
}
.name {
  margin: 0 0 6px;
  font-size: 20px;
}
.cat {
  color: #909399;
  font-size: 13px;
  margin: 0 0 8px;
}
.price {
  color: #f56c6c;
  font-size: 26px;
  font-weight: 800;
  margin: 0 0 16px;
}
.block {
  margin-bottom: 12px;
  strong {
    display: block;
    margin-bottom: 4px;
  }
  p {
    margin: 0;
    color: #606266;
    line-height: 1.5;
    white-space: pre-wrap;
  }
}
.footer-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
