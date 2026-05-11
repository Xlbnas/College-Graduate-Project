<template>
  <div class="home">
    <el-row :gutter="16">
      <el-col :md="17" :xs="24">
        <el-carousel height="260px" class="banner">
          <el-carousel-item>
            <div class="slide" :style="{ backgroundImage: 'url(/ui/banner-1.png)' }" />
          </el-carousel-item>
          <el-carousel-item>
            <div class="slide" :style="{ backgroundImage: 'url(/ui/banner-2.png)' }" />
          </el-carousel-item>
        </el-carousel>

        <div class="section-head">
          <i class="el-icon-star-on" />
          <span>热门商品</span>
        </div>
        <el-row :gutter="14" v-loading="loading">
          <el-col v-for="p in list" :key="p.id" :xs="24" :sm="12" :md="12" :lg="6">
            <el-card shadow="hover" class="pcard">
              <div class="thumb" @click="openDetail(p.id)">
                <el-image :src="img(p.imageUrl)" fit="cover" />
              </div>
              <div class="ptitle">{{ p.title }}</div>
              <div class="pcat">{{ p.categoryName || '—' }}</div>
              <div class="row-price">
                <span class="pprice">¥{{ p.price }}</span>
                <span class="seller">{{ p.sellerUsername || '校友' }}</span>
              </div>
              <div class="btn-row">
                <el-button size="small" plain type="primary" @click="openDetail(p.id)">查看详情</el-button>
                <el-button size="small" type="primary" :disabled="!token" @click="quickAdd(p)">加入购物车</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <div class="pager" v-if="total > pageSize">
          <el-pagination
            background
            layout="prev, pager, next"
            :page-size="pageSize"
            :current-page.sync="pageNum"
            :total="total"
            @current-change="load"
          />
        </div>
      </el-col>

      <el-col :md="7" :xs="24">
        <el-card class="side-card cats" :body-style="{ paddingTop: '0' }">
          <div class="blue-head">商品分类</div>
          <ul class="cat-list">
            <li @click="$router.push('/browse')"><i class="el-icon-menu" /> 全部商品</li>
            <li v-for="c in categories" :key="c.id" @click="$router.push({ path: '/browse', query: { categoryId: c.id } })">
              <i class="el-icon-folder" /> {{ c.name }}
            </li>
          </ul>
        </el-card>
        <el-card class="side-card latest" :body-style="{ paddingTop: '0' }">
          <div class="blue-head">最新发布</div>
          <div v-loading="latestLoad" class="latest-list">
            <div v-for="p in latest" :key="'l'+p.id" class="latest-item" @click="openDetail(p.id)">
              <el-image :src="img(p.imageUrl)" class="mini" fit="cover" />
              <div class="grow">
                <div class="t">{{ p.title }}</div>
                <div class="s">{{ p.sellerUsername }}</div>
                <div class="pr">¥{{ p.price }}</div>
              </div>
            </div>
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
  name: 'Home',
  components: { ProductDetailDialog },
  data() {
    return {
      categories: [],
      list: [],
      latest: [],
      latestLoad: false,
      pageNum: 1,
      pageSize: 8,
      total: 0,
      loading: false,
      detailVisible: false,
      detailId: null
    }
  },
  computed: {
    ...mapState('user', ['token'])
  },
  created() {
    this.fetchCategories()
    this.loadLatest()
    this.load()
  },
  methods: {
    img(u) {
      return productImageSrc(u)
    },
    async fetchCategories() {
      const res = await request.get('/categories')
      this.categories = res.data || []
    },
    async loadLatest() {
      this.latestLoad = true
      try {
        const res = await request.get('/products/latest', { params: { limit: 3 } })
        this.latest = res.data || []
      } finally {
        this.latestLoad = false
      }
    },
    async load() {
      this.loading = true
      try {
        const res = await request.get('/products/search', {
          params: { pageNum: this.pageNum, pageSize: this.pageSize }
        })
        this.list = res.data.list || []
        this.total = res.data.total || 0
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
.banner {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 18px;
}
.slide {
  height: 260px;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
}
.section-head {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin: 12px 0 14px;
  .el-icon-star-on {
    color: #e6a23c;
    margin-right: 6px;
  }
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
  color: #303133;
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
.side-card {
  margin-bottom: 16px;
}
.blue-head {
  background: #409eff;
  color: #fff;
  padding: 10px 12px;
  margin: 0 -20px 12px -20px;
  font-weight: 600;
}
.cat-list {
  list-style: none;
  padding: 0;
  margin: 0;
  li {
    padding: 10px 8px;
    cursor: pointer;
    border-radius: 4px;
    font-size: 14px;
    color: #606266;
  }
  li:hover {
    background: #ecf5ff;
    color: #409eff;
  }
  .el-icon-menu,
  .el-icon-folder {
    margin-right: 8px;
    color: #909399;
  }
}
.latest-list {
  min-height: 80px;
}
.latest-item {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  cursor: pointer;
}
.mini {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  flex-shrink: 0;
}
.grow {
  flex: 1;
  min-width: 0;
}
.latest-item .t {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.latest-item .s {
  font-size: 12px;
  color: #909399;
}
.latest-item .pr {
  font-size: 13px;
  color: #f56c6c;
  font-weight: 700;
}
.pager {
  margin-top: 12px;
  text-align: center;
}
</style>
