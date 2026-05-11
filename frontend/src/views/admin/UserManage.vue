<template>
  <div>
    <h2>用户管理</h2>
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="studentId" label="学号" />
      <el-table-column prop="phone" label="手机" />
      <el-table-column prop="status" label="状态" width="90">
        <template slot-scope="{ row }">{{ row.status === 1 ? '禁用' : '正常' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template slot-scope="{ row }">
          <el-button v-if="row.status !== 1" type="danger" plain size="mini" @click="dis(row)">禁用</el-button>
          <el-button v-else type="success" plain size="mini" @click="enb(row)">解禁</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'UserManage',
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
        const res = await request.get('/admin/users', { params: { pageNum: 1, pageSize: 100 } })
        this.rows = res.data.records || []
      } finally {
        this.loading = false
      }
    },
    async dis(row) {
      await request.post('/admin/users/' + row.id + '/disable')
      this.$message.success('已禁用')
      this.load()
    },
    async enb(row) {
      await request.post('/admin/users/' + row.id + '/enable')
      this.$message.success('已解禁')
      this.load()
    }
  }
}
</script>
