import { getCurrentDeviceInfo } from '@/api/DeviceInfo'

const state = {
  // 设备编号
  deviceNo: '',
  // 设备能生产的 工序号
  processNoList: [],
  // 能生产的叶片料宽
  material_width: ''
}

const mutations = {
  SET_DEVICENO(state, deviceNo) {
    state.deviceNo = deviceNo
  },
  SET_PROCESSNO_LIST(state, processNoList) {
    state.processNoList = processNoList
  },
  SET_MATERIAL_WIDTH(state, material_width) {
    state.material_width = material_width
  }
}

const actions = {
  getCurrentDeviceInfo({ commit }) { 
    return new Promise((resolve, reject) => {
      getCurrentDeviceInfo().then(res => {  
        if(!res.data || res.data.length <= 0){
          return 
        }
        let deviceNo = Object.keys(res.data)[0]
        if(!deviceNo || typeof deviceNo == undefined){
          return 
        }
        let deviceInfo = res.data[deviceNo]  
        commit('SET_DEVICENO',deviceNo)
        commit('SET_PROCESSNO_LIST',deviceInfo.processNoList)
        commit('SET_MATERIAL_WIDTH',deviceInfo.material_width)
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })


  }
}

const deviceInfo =  {
  state,
  mutations,
  actions
}

export default deviceInfo
