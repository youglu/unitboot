import axios from 'axios'
import { initHttpService } from '@/utils/requestConfig' 
import Vue from 'vue'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
console.log(process.env)
// 创建axios中间件远程后台服务实例
// 'http://14.18.154.234:9999/',
// 'http://localhost:9999/'
const remoteService = Vue.remoteService = Vue.prototype.$serverSttp = axios.create({
  baseURL:  process.env.VUE_APP_SERVER_URL,
   // 超时
   timeout: 10000
})
initHttpService(remoteService)
export default remoteService