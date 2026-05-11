<template>
  <div class="site-wrap">
    <site-header />
    <main class="main-area">
      <router-view />
    </main>
    <site-footer />
  </div>
</template>

<script>
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import request from '@/utils/request'

export default {
  name: 'MainLayout',
  components: { SiteHeader, SiteFooter },
  mounted() {
    this.refreshBadge()
    this.$root.$on('cart-refresh', this.refreshBadge)
  },
  beforeDestroy() {
    this.$root.$off('cart-refresh', this.refreshBadge)
  },
  methods: {
    async refreshBadge() {
      const token = this.$store.state.user.token
      if (!token) {
        this.$store.commit('cart/SET_ITEM_COUNT', 0)
        return
      }
      try {
        const res = await request.get('/cart')
        this.$store.commit('cart/SET_ITEM_COUNT', (res.data || []).length)
      } catch (e) {
        this.$store.commit('cart/SET_ITEM_COUNT', 0)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.site-wrap {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
.main-area {
  flex: 1;
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
  padding: 20px 16px 32px;
  box-sizing: border-box;
}
</style>
