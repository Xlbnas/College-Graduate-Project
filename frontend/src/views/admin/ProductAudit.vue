<template>
  <div>
    <h2>商品审核</h2>
    <el-table :data="rows" v-loading="loading" class="dark-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="price" label="价格" width="90" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{ row }">
          <el-button size="mini" type="success" plain @click="audit(row, 'PASS')">通过</el-button>
          <el-button size="mini" type="danger" plain @click="openReject(row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="填写拒绝原因" :visible.sync="dlg" width="420px">
      <el-input type="textarea" v-model="reason" rows="3" />
      <span slot="footer">
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ProductAudit',
  data() {
    return { rows: [], loading: false, dlg: false, reason: '', current: null }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await request.get('/admin/products', {
          params: { status: 'PENDING', pageNum: 1, pageSize: 50 }
        })
        this.rows = res.data.records || []
      } finally {
        this.loading = false
      }
    },
    async audit(row, auditStatus, reason = '') {
      const params = {
        productId: row.id,
        auditStatus,
        ...(reason ? { reason } : {})
      }
      await request.post('/admin/products/audit', {}, { params })
      this.$message.success('审核已提交')
      this.load()
    },
    openReject(row) {
      this.current = row
      this.reason = ''
      this.dlg = true
    },
    confirmReject() {
      if (!this.reason) return this.$message.warning('请填写原因')
      this.audit(this.current, 'REJECT', this.reason)
      this.dlg = false
    }
  }
}
</script>

<style scoped>
.dark-table {
  background: transparent;
}
</style>
