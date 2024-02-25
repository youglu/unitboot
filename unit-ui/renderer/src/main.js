import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import permission from './permission'
import Cookies from 'js-cookie' 

import Element from 'element-ui'
import './assets/icons'
import 'normalize.css/normalize.css'
import './assets/styles/element-variables.scss'
import '@/assets/styles/index.scss' // global css
import '@/assets/styles/union.scss' // union css
import Pagination from "@/components/Pagination";
 
// 全局组件挂载
Vue.component('Pagination', Pagination)

// 消息方法配置
Vue.prototype.msgSuccess = function (msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
}

Vue.prototype.msgError = function (msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
}

Vue.prototype.msgInfo = function (msg) {
  this.$message.info(msg);
}
// 消息方法配置 end

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})
Vue.config.productionTip = false
/* eslint-disable no-new */
new Vue({
  components: { App },
  permission,
  router,
  store,
  render: h=>h(App)
}).$mount('#app')

//关闭vue devtool提示
Vue.config.devtools = false
 