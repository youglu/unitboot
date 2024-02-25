<template>
  <div id="wrapper">
    <div style="text-align: right;padding-right:80px;position:relative;line-height: 35px; height:30px;">
      <span style="color: #757575;font-size: 20px;position:absolute;left:0px;top:-5px; transform-origin: left center; transform: perspective(300px) rotateY(25deg);">
        设备号：<span style="color:green;font-weight:bold;">{{ this.$store.getters.deviceNo }} </span>
        工序号：<span style="color:green;font-weight:bold;">{{ this.$store.getters.device.processNoList[0] }} </span>
      </span>
      <span style="position:absolute;right:0px;
      width:300px;transform-origin: right center; transform: perspective(300px) rotateY(-25deg);">
      <span style="color: #757575;font-size: 20px; position:relative;right:75px;">{{ this.$store.getters.name }} </span>
      <el-button :loading="loading" size="medium" type="primary" style="position: absolute;right:0px;top:0px;"
        @click="handleLogOut">
       <span >退出</span>
      </el-button>
    </span>
    </div>
  </div>
</template>

<script>  
/* the below code should be used where this.$http is created (as a global axios instance) */
 
/* end */

export default {
  name: 'TopInfo',
  data() {
    return {
      loading: false,
      redirect: undefined, 
      processNo: ''
    };
  },
  created() { 
  },
  methods: {
    handleLogOut() {
      this.$store
        .dispatch("LogOut")
        .then(() => {
          this.$router.push({ path: this.redirect || "/login" });
        })
        .catch((er) => {
          this.loading = false
        });
    }
  }
}
</script>
 