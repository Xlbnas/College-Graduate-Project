const TOKEN_KEY = 'token'
const USER_KEY = 'userInfo'

export default {
  namespaced: true,
  state: {
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  },
  mutations: {
    SET_AUTH(state, { token, userInfo }) {
      state.token = token
      state.userInfo = userInfo
      localStorage.setItem(TOKEN_KEY, token)
      localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
    },
    CLEAR(state) {
      state.token = ''
      state.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
}
