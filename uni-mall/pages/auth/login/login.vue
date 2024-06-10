<template>
  <view class="con">
    <image src="/static/images/logo.png" />
    <!-- 登录 -->
    <view class="login-form">
      <view :class="['item','']">
        <view class="account">
          <text class="input-item">
            账号
          </text>
          <input
              type="text"
              data-type="account"
              placeholder-class="inp-palcehoder"
              placeholder="请输入手机号"
              v-model="mobile"
          >
        </view>
      </view>
      <view :class="['item','']">
        <view class="account">
          <text class="input-item">
            密码
          </text>
          <input
              type="password"
              data-type="password"
              placeholder-class="inp-palcehoder"
              placeholder="请输入密码"
              v-model="password"
          >
        </view>
      </view>
      <view class="operate">
        <view
            class="to-register"
            @tap="toRegitser"
        >
          还没有账号？
          <text>去注册></text>
        </view>
      </view>
    </view>

    <view>
      <button
          class="authorized-btn"
          @tap="login"
      >
        登录
      </button>
      <button
          class="to-idx-btn"
          @tap="toIndex"
      >
        回到首页
      </button>
    </view>
  </view>
</template>

<style scoped lang="scss">
@import "./accountLogin.scss";
</style>

<script>
	const util = require("@/utils/util.js")
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
        mobile: '19076194847',
        password: '123456',
			}
		},
		methods: {
			login: function() {
				//登录远程服务器
        let that = this;
        util.request(api.Login,
            {
              mobile: that.mobile,
              password: that.password
            }
            , 'POST', 'application/json').then(res => {
          if (res.code === 0) {
            console.log("登录成功")
            //存储用户信息
            uni.setStorageSync('userInfo', res.userInfo);
            uni.setStorageSync('token', res.token);
            uni.setStorageSync('userId', res.userId);
          } else {
            // util.showErrorToast(res.errmsg)
            uni.showModal({
              title: '提示',
              content: res.data.msg,
              showCancel: false
            });
          }

        });
        if (that.navUrl && that.navUrl == '/pages/index/index') {
        uni.switchTab({
            url: that.navUrl,
        })
        } else if (that.navUrl) {
        uni.redirectTo({
            url: that.navUrl,
        })
        }
			}
		},
		onLoad: function(options) {
			let that = this;
			if (uni.getStorageSync("navUrl")) {
				that.navUrl = uni.getStorageSync("navUrl")
			} else {
				that.navUrl = '/pages/index/index'
			}
			if (uni.getUserProfile) {
				that.canIUseGetUserProfile = true
			}
		}
	}
</script>