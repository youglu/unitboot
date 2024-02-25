import localRequest from '@/utils/request'

/**
 * 获得当前设备信息
 * @returns 当前设备信息
 */
export function getCurrentDeviceInfo() {
  return localRequest({
    url: '/currentDeviceInfo',
    method: 'get',
    params: {}
  })
}