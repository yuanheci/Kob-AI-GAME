import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'

export default createStore({
  state: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {   //定义state的子模块
      user: ModuleUser,
      pk: ModulePk,
  }
})
