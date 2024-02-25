import request from '@/utils/request'
import remoteRequest from '@/utils/remoteRequest'
// 登录方法
export function login(userName, userPwd, code, uuid) { 
  const data = {
    userName,
    userPwd,
    code,
    uuid
  }

  return remoteRequest({
    url: '/erp/login',
    method: 'get',
    params: data
  })
}
// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return remoteRequest({
    url: '/logout',
    method: 'get'
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/captchaImage',
    method: 'get'
  })
}