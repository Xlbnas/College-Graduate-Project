<template>
  <div>
    <h2>商品管理</h2>
    <div class="bar">
      <el-select v-model="status" clearable placeholder="状态筛选" @change="load" style="width:160px">
        <el-option label="在售" value="ON_SALE" />
        <el-option label="待审核" value="PENDING" />
        <el-option label="已拒绝" value="REJECTED" />
        <el-option label="已下架" value="OFF_SALE" />
        <el-option label="已售罄" value="SOLD_OUT" />
      </el-select>
    </div>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{ row }">
          <el-button v-if="row.status === 'ON_SALE'" type="text" @click="off(row)">下架</el-button>
          <el-button type="text" style="color:#f97316" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ProductManage',
  data() {
    return { status: '', rows: [], loading: false }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await request.get('/admin/products', {
          params: { status: this.status || undefined, pageNum: 1, pageSize: 100 }
        })
        this.rows = res.data.records || []
      } finally {
        this.loading = false
      }
    },
    async off(row) {
      await request.post('/admin/products/' + row.id + '/off-sale')
      this.$message.success('操作成功')
      this.load()
    },
    async del(row) {
      await this.$confirm('确认删除商品？不可恢复演示数据请谨慎操作。', '提示')
      await request.delete('/admin/products/' + row.id)
      this.$message.success('已删除')
      this.load()
    }
  }
}
</script>

<style scoped>
.bar {
  margin-bottom: 12px;
}
</style>
