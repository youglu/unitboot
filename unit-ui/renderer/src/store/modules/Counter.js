const state = {
  main: 0
}

const mutations = {
  DECREMENT_MAIN_COUNTER (state) {
    state.main--
  },
  INCREMENT_MAIN_COUNTER (state) {
    state.main++
  }
}

const actions = {
  someAsyncTask ({ commit }) {
    // do something async
    commit('INCREMENT_MAIN_COUNTER')
  },
  myAction(context, data) {
    return new Promise((resolve, reject) => {
      resolve(123); 
  })
  }
}

export default { 
  state,
  mutations,
  actions
}
