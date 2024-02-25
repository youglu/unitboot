import axios from 'axios'
import { initHttpService } from '@/utils/requestConfig' 
import Vue from 'vue' 

let PORT = 8889
if(typeof window['mainEnv'] != "undefined"){
  PORT = window.mainEnv.PORT
}

const baseUrl = `${process.env.VUE_APP_LOCAL_SERVER_URL}/api` //`http://localhost:${PORT}/api`
console.log(`本地后台运行端口:${PORT},服务URL：${baseUrl}`)

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios本地服务实例
const localService = Vue.localService = Vue.prototype.$http = axios.create({
  baseURL: baseUrl,
  // 超时
  timeout: 10000
}) 
initHttpService(localService)
export default localService
