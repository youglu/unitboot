<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>

export default {
  name: 'IndexApp',
  data() {
    return {
      loading: false,
      redirect: undefined
    }
  },
  created(){
        document.title = "康美风中间件"
    }
}
</script>
<style>
/* CSS */
</style>
