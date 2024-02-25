<template>
  <div class="login">
    <div class="main">
      <img src="@/assets/image/img_bg.png" alt="" class="img2">
      <h3><span style="display:inline-block;margin-left:130px;">{{ constInfo.appName }}</span></h3>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h2 class="title">欢迎登录</h2>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" type="text" auto-complete="off" @focus="isUsername = true"
            @blur="isUsername = false" placeholder="用户名">
            <svg-icon slot="prefix" icon-class="user" v-bind:class="{ 'color-class': isUsername }" class=" input-icon " />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码"
            @focus="isPassword = true" @blur="isPassword = false" @keyup.enter.native="handleLogin">
            <svg-icon slot="prefix" icon-class="password" v-bind:class="{ 'color-class': isPassword }"
              class="el-input__icon input-icon " />
          </el-input>
        </el-form-item>

        <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 8px;">记住密码</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button :loading="loading" size="medium" type="primary" style="width:100%;"
            @click.native.prevent="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--  底部  -->
    <div class="el-login-footer">
      <span>技术支持：<a href="email:329900041@qq.com">329900041@qq.com</a></span>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import constInfo from '@/utils/constInfo'

export default {
  name: "Login", 
  data() {
    return {
      constInfo: constInfo,
      isUsername: false,
      isPassword: false,
      isValidCode: false,
      codeUrl: "",
      cookiePassword: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "用户名不能为空" }
        ],
        password: [
          { required: true, trigger: "blur", message: "密码不能为空" }
        ],
        code: [{ required: true, trigger: "change", message: "验证码不能为空" }]
      },
      loading: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      /*getCodeImg().then(res => {
        this.codeUrl = "data:image/gif;base64," + res.img;
        this.loginForm.uuid = res.uuid;
      });*/
    },
    getCookie() {
      console.log(localStorage)
      const username = localStorage.getItem("username");
      const password = localStorage.getItem("password"); 
      const rememberMe = localStorage.getItem('rememberMe')
      this.loginForm = {
        username: username === undefined || !username? this.loginForm.username : username,
        password: password === undefined || !password ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {  
      this.$refs.loginForm.validate(valid => { 
        if (valid) { 
          this.loading = true;
          try{
          if (this.loginForm.rememberMe) {
            localStorage.setItem("username", this.loginForm.username, { expires: 30 });
            localStorage.setItem("password", encrypt(this.loginForm.password), { expires: 30 });
            localStorage.setItem('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            localStorage.removeItem("username");
            localStorage.removeItem("password");
            localStorage.removeItem('rememberMe');
          }
        }catch(error){
            console.log(error)
          } 
          this.$store
            .dispatch("Login", this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || "/" });
            })
            .catch((er) => {
              this.loading = false;
              this.getCode();
            });
        } 
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #fff;
  position: relative;
}

.main {
  position: relative;
  width: 940px;
  height: 495px;
  background: rgba(255, 255, 255, 1);
  border-radius: 12px;
  box-shadow: 0px 2px 12px 0px rgb(185, 185, 185);
  color: rgb(70, 67, 67); 
  font-family: 'Arial Normal', 'Arial', sans-serif;

  .img1 {
    position: absolute;
    top: 46px;
    left: 53px;
    width: 110px;
  }

  .img2 {
    position: absolute;
    top: 63px;
    left: 95px;
    width: 348px;
    height: 299px;
  }

  h3 {
    margin-left: 52px;
    margin-top: 383px;
    margin-bottom: 0;
    font-size: 32px;

  }

  h4 {
    margin-left: 52px;
    font-size: 24px;
    margin-top: 16px;
  }

  .login-form {
    position: absolute;
    right: 0;
    top: 0;
    border-radius: 6px;
    background: #ffffff;
    width: 299px;
    height: 495px;
    padding: 58px 33px 33px 33px;

    .title {
      width: 85px;
      font-size: 20px;
      margin: 0px auto 62px auto;
      border-bottom: 3px rgba(27, 74, 127, 1) ridge;
      text-align: center;
      color: #333333;
      padding-bottom: 8px;
    }

    .el-input {
      height: 32px;

      input:focus {
        color: rgba(6, 104, 185, 1);
      }

      input {
        outline: none;
        height: 38px;
        border-top: 0;
        border-left: 0;
        border-right: 0;
        border-radius: 0;
      }

    }

    .color-class {
      color: rgba(6, 104, 185, 1);
    }

    .input-icon {
      height: 39px;
      width: 19px;
      margin-left: 2px;
    }
  }
}

.login-code {
  position: absolute;
  width: 88px;
  height: 32px;
  right: 0;
  bottom: 8px;

  img {
    width: 88px;
    height: 32px;
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: absolute;
  transform: translateY(273px);
  width: 100%;
  text-align: center;
  color: #c0c0c0;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;

  span {
    img {
      height: 12px;
    }
  }
}
</style>
