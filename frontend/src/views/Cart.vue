<template>
  <div>
    <div class="page-head">
      <i class="el-icon-shopping-cart-2" />
      <span>购物车</span>
    </div>
    <template v-if="rows.length">
      <el-card v-for="row in rows" :key="row.id" class="line-card" shadow="never">
        <div class="row-wrap">
          <el-image :src="img(row.product && row.product.imageUrl)" class="thumb" fit="cover" @click.native="preview(row.productId)" />
          <div class="info">
            <div class="name" @click="preview(row.productId)">{{ row.product && row.product.title }}</div>
            <div class="price">¥{{ row.product && row.product.price }}</div>
          </div>
          <el-input-number
            v-model="row.quantity"
            :min="1"
            size="small"
            @change="v => qtyChange(row, v)"
          />
          <el-button type="danger" icon="el-icon-delete" circle size="small" @click="remove(row)" />
        </div>
      </el-card>
      <div class="summary-bar">
        <span class="total">总计: <b>¥{{ totalAmt.toFixed(2) }}</b></span>
        <el-button type="success" icon="el-icon-bank-card" :disabled="!rows.length" @click="settOpen = true">去结算</el-button>
      </div>
    </template>
    <el-empty v-else description="购物车还是空的，去逛逛吧~" />

    <el-dialog title="结算" :visible.sync="settOpen" width="520px" custom-class="sett-dlg">
      <div slot="title" class="sett-title"><i class="el-icon-bank-card" /> 结算</div>
      <el-form label-position="top">
        <el-form-item label="收货地址">
          <el-input type="textarea" v-model="sett.address" rows="2" placeholder="如：学生宿舍1号楼301室" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="sett.phone" placeholder="11位手机号" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="sett.pay" style="width:100%">
            <el-option label="微信支付" value="微信支付" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="模拟支付（演示）" value="模拟支付" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="sett.remark" rows="2" />
        </el-form-item>
        <div class="sum-box">
          <div><span>商品总价</span><span>¥{{ totalAmt.toFixed(2) }}</span></div>
          <div><span>运费</span><span>¥0.00</span></div>
          <el-divider />
          <div class="final"><span>总计</span><span>¥{{ totalAmt.toFixed(2) }}</span></div>
        </div>
      </el-form>
      <span slot="footer">
        <el-button @click="settOpen = false">取消</el-button>
        <el-button type="success" icon="el-icon-check" :loading="paying" @click="submitSett">提交订单</el-button>
      </span>
    </el-dialog>

    <product-detail-dialog :visible.sync="dlgVis" :product-id="dlgId" @cart-changed="$root.$emit('cart-refresh')" />
  </div>
</template>

<script>
import request from '@/utils/request'
import { productImageSrc } from '@/utils/image'
import ProductDetailDialog from '@/components/ProductDetailDialog.vue'

export default {
  name: 'Cart',
  components: { ProductDetailDialog },
  data() {
    return {
      rows: [],
      settOpen: false,
      paying: false,
      sett: {
        address: '',
        phone: '',
        remark: '',
        pay: '微信支付'
      },
      dlgVis: false,
      dlgId: null
    }
  },
  computed: {
    totalAmt() {
      return this.rows.reduce((s, r) => {
        const p = Number((r.product && r.product.price) || 0)
        return s + p * (r.quantity || 1)
      }, 0)
    }
  },
  created() {
    this.load()
  },
  methods: {
    img(u) {
      return productImageSrc(u)
    },
    preview(id) {
      this.dlgId = id
      this.dlgVis = true
    },
    async load() {
      const res = await request.get('/cart')
      const list = res.data || []
      const rows = await Promise.all(
        list.map(async c => {
          let product = null
          try {
            const d = await request.get('/products/' + c.productId)
            product = d.data
          } catch (e) {
            product = { title: '商品不可见', imageUrl: '', price: 0 }
          }
          return { ...c, product, quantity: c.quantity || 1 }
        })
      )
      this.rows = rows
    },
    async qtyChange(row, v) {
      try {
        await request.put('/cart/' + row.id, { quantity: v })
      } catch (e) {
        this.load()
      }
    },
    async remove(row) {
      await request.delete('/cart/' + row.id)
      this.$message.success('已删除')
      this.load()
      this.$root.$emit('cart-refresh')
    },
    async submitSett() {
      if (!this.sett.address) return this.$message.warning('请填写收货地址')
      if (!this.sett.phone) return this.$message.warning('请填写联系电话')
      this.paying = true
      try {
        const cartIds = this.rows.map(r => r.id)
        await request.post('/orders/checkout', {
          cartIds,
          address: this.sett.address,
          contactPhone: this.sett.phone,
          remark: this.sett.remark,
          payMethod: this.sett.pay
        })
        this.$message.success('下单成功')
        this.settOpen = false
        this.$root.$emit('cart-refresh')
        this.$router.push('/orders')
      } finally {
        this.paying = false
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
  .el-icon-shopping-cart-2 {
    margin-right: 8px;
  }
}
.line-card {
  margin-bottom: 14px;
  border-radius: 8px;
}
.row-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.thumb {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  cursor: pointer;
}
.info {
  flex: 1;
  min-width: 140px;
}
.name {
  font-weight: 600;
  cursor: pointer;
}
.price {
  color: #f56c6c;
  font-weight: 700;
  margin-top: 6px;
}
.summary-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
.total b {
  color: #f56c6c;
  font-size: 18px;
}
.sum-box {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px 14px;
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}
.sum-box div {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.sum-box .final {
  font-weight: 700;
  color: #f56c6c;
}
</style>

<style lang="scss">
.sett-dlg .el-dialog__header {
  padding: 0;
}
.sett-title {
  background: #409eff;
  color: #fff;
  padding: 12px 16px;
  font-weight: 600;
}
.sett-title .el-icon-bank-card {
  margin-right: 8px;
}
.sett-dlg .el-dialog__headerbtn .el-dialog__close {
  color: #fff;
}
</style>
