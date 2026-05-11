<template>
  <div>
    <div class="page-title">
      <i class="el-icon-s-operation" />
      <span>浏览商品</span>
    </div>
    <div class="toolbar">
      <el-select v-model="sort" size="small" style="width:140px" @change="noop">
        <el-option label="最新发布" value="time" />
        <el-option label="价格升序" value="priceAsc" />
        <el-option label="价格降序" value="priceDesc" />
      </el-select>
      <el-select v-model="categoryId" size="small" clearable placeholder="全部分类" style="width:160px" @change="reload">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="String(c.id)" />
      </el-select>
    </div>
    <el-row :gutter="14" v-loading="loading">
      <el-col v-for="p in sortedList" :key="p.id" :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="pcard">
          <div class="thumb" @click="openDetail(p.id)">
            <el-image :src="img(p.imageUrl)" fit="cover" />
          </div>
          <div class="ptitle">{{ p.title }}</div>
          <div class="pcat">{{ p.categoryName || '—' }}</div>
          <div class="row-price">
            <span class="pprice">¥{{ p.price }}</span>
            <span class="seller">{{ p.sellerUsername }}</span>
          </div>
          <div class="btn-row">
            <el-button size="small" plain type="primary" @click="openDetail(p.id)">查看详情</el-button>
            <el-button size="small" type="primary" :disabled="!token" @click="quickAdd(p)">加入购物车</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <product-detail-dialog :visible.sync="detailVisible" :product-id="detailId" @cart-changed="$root.$emit('cart-refresh')" />
  </div>
</template>

<script>
import request from '@/utils/request'
import { productImageSrc } from '@/utils/image'
import ProductDetailDialog from '@/components/ProductDetailDialog.vue'
import { mapState } from 'vuex'

export default {
  name: 'Browse',
  components: { ProductDetailDialog },
  data() {
    return {
      sort: 'time',
      categoryId: '',
      keyword: '',
      categories: [],
      list: [],
      loading: false,
      detailVisible: false,
      detailId: null
    }
  },
  computed: {
    ...mapState('user', ['token']),
    sortedList() {
      const arr = [...this.list]
      if (this.sort === 'priceAsc') arr.sort((a, b) => Number(a.price) - Number(b.price))
      else if (this.sort === 'priceDesc') arr.sort((a, b) => Number(b.price) - Number(a.price))
      return arr
    }
  },
  watch: {
    '$route.query'() {
      this.syncQuery()
      this.reload()
    }
  },
  created() {
    this.fetchCategories()
    this.syncQuery()
    this.reload()
  },
  methods: {
    noop() {},
    img(u) {
      return productImageSrc(u)
    },
    syncQuery() {
      const q = this.$route.query
      this.keyword = (q.keyword && String(q.keyword)) || ''
      this.categoryId = q.categoryId != null && q.categoryId !== '' ? String(q.categoryId) : ''
    },
    async fetchCategories() {
      const res = await request.get('/categories')
      this.categories = res.data || []
    },
    async reload() {
      this.loading = true
      try {
        const cat = this.categoryId ? Number(this.categoryId) : undefined
        const res = await request.get('/products/search', {
          params: {
            keyword: this.keyword || undefined,
            categoryId: cat,
            pageNum: 1,
            pageSize: 40
          }
        })
        this.list = res.data.list || []
      } finally {
        this.loading = false
      }
    },
    openDetail(id) {
      this.detailId = id
      this.detailVisible = true
    },
    async quickAdd(p) {
      await request.post('/cart', { productId: p.id, quantity: 1 })
      this.$message.success('已加入购物车')
      this.$root.$emit('cart-refresh')
    }
  }
}
</script>

<style scoped lang="scss">
.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 14px;
  color: #303133;
  .el-icon-s-operation {
    margin-right: 8px;
  }
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pcard {
  margin-bottom: 14px;
}
.thumb {
  cursor: pointer;
  height: 140px;
  border-radius: 6px;
  overflow: hidden;
  background: #eee;
  .el-image {
    width: 100%;
    height: 100%;
  }
}
.ptitle {
  font-weight: 700;
  margin-top: 10px;
  font-size: 15px;
}
.pcat {
  font-size: 12px;
  color: #909399;
  margin: 4px 0;
}
.row-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 10px;
}
.pprice {
  color: #f56c6c;
  font-weight: 800;
  font-size: 16px;
}
.seller {
  font-size: 12px;
  color: #909399;
}
.btn-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
