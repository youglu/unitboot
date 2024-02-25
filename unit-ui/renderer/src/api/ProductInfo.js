import localRequest from '@/utils/request'
import remoteRequest from '@/utils/remoteRequest' 
import store from '@/store'

export const product_state = {
  // 将产
  WILL_PRODUCTION: 0,
  // 在产
  IN_PRODUCTION: 1,
  // 已产
  PRODUCTED: 2,
  // 断中
  BREAKED: 10,
  // 取消
  CANCEL: 20,
  // 已报送
  SENDED: 30,
  // 已中断报送
  BREAK_SENDED: 31,
  // 删除
  DEL: -1
}
/* ------------------本地后台请求------------------- */
 /**
 * 将从服务端选择的生产数据保存到本地后台
 * @param newProductInfos 要保存到本地后台的生产数据集合(也可能只有一个)
 * @returns 
 */
  export function createProductInfo(newProductInfos) {
    // 
    return localRequest({
      url: '/product/createProductInfo',
      method: 'post',
      data: newProductInfos
    })
  }
/**
 * 获得当前设备在产的数据
 * @returns 
 */
 export function fetchCurrentProductionInfo(params) { 
  return localRequest({
    url: '/product/findCurrentProductionInfo',
    method: 'get',
    params: params
  })
}
/**
 * 获得当前设备在产与将产数据
 * @returns 
 */
 export function findCurrentProductInfo(params) { 
  return localRequest({
    url: '/product/findCurrentProductInfo',
    method: 'get',
    params: params
  })
}
/**
 * 获得当前设备的在产与将产的数据
 * @returns 
 */
 export function findAll() { 
  return localRequest({
    url: '/product/findAll',
    method: 'get'
  })
}
 /**
 * 更新生产数据状态
 * @returns 
 */
  export function updateProductInfoState(params) { 
    return localRequest({
      url: '/product/updateProductInfoState',
      method: 'get',
      params: params
    })
  }
  /**
   * 中断在产数据
   * @param {中断的生产数据ID} id 
   */
  export function saveBreakProductionInfo(id){
    return updateProductInfoState({id: id, production_state: product_state.BREAKED})
  }
    /**
   * 取消将产数据
   * @param {取消的生产数据ID} breakId 
   */
     export function saveCancelProductionInfo(id){
      return updateProductInfoState({id: id, production_state: product_state.CANCEL})
    }
/* ------------------服务端请求------------------- */
/**
 * 获得当前设备能够生产的数据
 * @param {*} processNo 工序单号
 * @returns 
 */
 export function fetchProductInfo(processNo) {
  const data = {
    processNo
  }
  return remoteRequest({
    url: '/erp/fetchProductInfo',
    method: 'get',
    params: data
  })
}
/**
 * 从服务端查询需要生产的数据
 * @returns 
 */
 export function findNeedToProductInfo(searchParam) { 
  // 获得当前设备对应的工序号
  let processNoList = store.getters.device.processNoList
  let material_width = store.getters.device.material_width
  console.log("能生产的叶片料宽:"+material_width)  
  
  searchParam['process_nos'] = processNoList
  searchParam['material_width'] = material_width
  console.log(searchParam)
  return remoteRequest({
    url: '/erp/findNeedToProductInfo',
    method: 'get',
    params: searchParam
  })
}
/**
 * 从服务端获得制造中心数据
 * @returns 
 */
 export function findManufactureCenter(searchParam) {
  return remoteRequest({
    url: '/erp/findManufactureCenter',
    method: 'get',
    params: searchParam
  })
}
/**
 * 上报确认接收量，创建工序作业单
 * @returns 
 */
 export function confirmReceiveQuantity(confirmData) {
  return remoteRequest({
    url: '/erp/confirmReceiveQuantity',
    method: 'post',
    data: confirmData
  })
}
