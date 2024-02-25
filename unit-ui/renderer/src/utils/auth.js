import Cookies from 'js-cookie'
import store from '@/store'
const TokenKey = 'Admin-Token'

// electron环境中使用localStorage
export function getToken() {
  return sessionStorage.getItem(TokenKey)//Cookies.get(TokenKey)
}

export function setToken(token) {
  return sessionStorage.setItem(TokenKey, token)//Cookies.set(TokenKey, token)
}

export function removeToken() {
  return sessionStorage.removeItem(TokenKey)//Cookies.remove(TokenKey)
}
