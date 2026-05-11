export default {
  namespaced: true,
  state: {
    itemCount: 0
  },
  mutations: {
    SET_ITEM_COUNT(state, n) {
      state.itemCount = Math.max(0, Number(n) || 0)
    }
  }
}
