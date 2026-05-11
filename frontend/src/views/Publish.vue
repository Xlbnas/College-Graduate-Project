<template>
  <div>
    <el-card class="publish-card">
      <div slot="header" class="card-head">
        <i class="el-icon-circle-plus-outline" /> 发布商品
      </div>
      <el-form :model="form" label-width="100px" class="form-inner">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类" required>
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%;max-width:400px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" required>
          <el-input v-model.number="form.price" type="number" placeholder="请输入价格" style="max-width:400px" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input type="textarea" :rows="5" v-model="form.description" placeholder="成色、交易方式、自取地点等" />
        </el-form-item>
        <el-form-item label="商品图片" required>
          <el-upload drag action="" :auto-upload="false" :limit="1" accept="image/*" :on-change="onFile">
            <i class="el-icon-upload" />
            <div class="el-upload__text">选择文件</div>
            <div slot="tip" class="upload-tip">{{ file ? file.name : '未选择文件' }}</div>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-upload2" @click="submit" :loading="loading">发布商品</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Publish',
  data() {
    return {
      categories: [],
      file: null,
      loading: false,
      form: { title: '', categoryId: null, price: null, description: '' }
    }
  },
  created() {
    request.get('/categories').then(r => (this.categories = r.data || []))
  },
  methods: {
    onFile(file) {
      this.file = file.raw
    },
    async submit() {
      if (!this.form.title) return this.$message.warning('请填写名称')
      if (!this.form.categoryId) return this.$message.warning('请选择分类')
      if (this.form.price == null || this.form.price <= 0) return this.$message.warning('请输入有效价格')
      if (!this.file) return this.$message.warning('请上传图片')
      const fd = new FormData()
      fd.append('title', this.form.title)
      fd.append('description', this.form.description || '')
      fd.append('price', this.form.price)
      fd.append('categoryId', this.form.categoryId)
      fd.append('image', this.file)
      this.loading = true
      try {
        await request.post('/products', fd, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        this.$message.success('发布成功，等待管理员审核')
        this.$router.push('/profile')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.publish-card ::v-deep .el-card__header {
  padding: 0;
  border: none;
}
.card-head {
  background: #409eff;
  color: #fff;
  padding: 12px 18px;
  font-weight: 600;
  font-size: 16px;
  .el-icon-circle-plus-outline {
    margin-right: 8px;
  }
}
.form-inner {
  max-width: 720px;
  padding-top: 8px;
}
.upload-tip {
  font-size: 13px;
  color: #909399;
  margin-top: 6px;
}
</style>
