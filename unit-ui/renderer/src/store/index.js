import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
 

import modules from './modules'

Vue.use(Vuex)

const store =  new Vuex.Store({
  namespaced: true,   // ADD THIS LINE
  modules,
  getters,
  plugins: [ 
    //createSharedMutations()
  ],
  strict: process.env.NODE_ENV !== 'production'
})
export default store