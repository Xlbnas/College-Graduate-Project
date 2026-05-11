const TOKEN_KEY = 'adminToken'
const ADMIN_KEY = 'adminInfo'

export default {
  namespaced: true,
  state: {
    token: localStorage.getItem(TOKEN_KEY) || '',
    adminInfo: JSON.parse(localStorage.getItem(ADMIN_KEY) || 'null')
  },
  mutations: {
    SET_AUTH(state, { token, adminInfo }) {
      state.token = token
      state.adminInfo = adminInfo
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(ADMIN_KEY, JSON.stringify(adminInfo))
    },
    CLEAR(state) {
      state.token = ''
      state.adminInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(ADMIN_KEY)
    }
  }
}
