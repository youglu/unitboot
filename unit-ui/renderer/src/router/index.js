import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  // electron需要改成hash模式
  mode: 'hash', 
  routes: [
    {
      path: '/',
      component: require('@/views/index').default
    },
    {
      path: '',
      component: require('@/views/index'),
      redirect: 'index',
      children: [
        {
          path: 'index',
          component: (resolve) => require(['@/views/index'], resolve),
          name: '首页',
          meta: { title: '首页', icon: 'dashboard', noCache: true, affix: true }
        }
      ]
    },
    {
      path: '/login',
      component: (resolve) => require(['@/views/login'], resolve),
      hidden: true
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})
