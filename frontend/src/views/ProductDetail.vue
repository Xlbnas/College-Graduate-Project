<template>
  <div class="wrap">
    <product-detail-dialog :visible.sync="show" :product-id="pid" @cart-changed="$root.$emit('cart-refresh')" />
  </div>
</template>

<script>
import ProductDetailDialog from '@/components/ProductDetailDialog.vue'

export default {
  name: 'ProductDetail',
  components: { ProductDetailDialog },
  data() {
    return {
      show: true,
      pid: null
    }
  },
  watch: {
    '$route.params.id': {
      immediate: true,
      handler(v) {
        this.pid = v ? Number(v) : null
        this.show = true
      }
    },
    show(val) {
      if (!val) this.$router.replace('/browse')
    }
  }
}
</script>
