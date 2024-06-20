<template>
  <view class="con">
    <image src="/static/images/logo.png" />
    <!-- 登录 -->
    <view class="login-form">
      <view class="list">
        <view class="label"><span>*</span>邮箱账号 </view>
        <view class="right">
          <input type="email" placeholder="请输入" @blur="mailBlur" v-model="formData.mail">
        </view>
      </view>
      <view class="list">
        <view class="label"><span>*</span>验证码</view>
        <view class="right">
          <input type="number" placeholder="请输入" v-model="formData.checkCode">
          <button class="send-code" :disabled="disabled" @click="sendCode">{{sendTxt}}</button>
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
        formData: {
          mail: '2636822826@qq.com',
          checkCode: '',
        },
        timer: 60,
        sendTxt: '获取验证码',
        disabled: false,
			}
		},
		methods: {
      mailBlur: function(){
        //邮箱正则
        let pattern = /[a-zA-Z0-9_.+-]+@[a-zA-Z0-9_-]+\.[a-zA-Z0-9-.]+/;
        if (pattern.test(this.formData.mail)) {
          return true;
        } else {
          uni.showToast({
            icon: 'none',
            title: '请输入正确的邮箱格式'
          })
          // this.formData.mail = '';
          return false;
        }
      },
			login: function() {
        if (!this.formData.mail) {
          uni.showToast({
            title: '请输入邮箱',
            icon: 'none'
          });
          return;
        }
        if (!this.formData.checkCode) {
          uni.showToast({
            title: '请输入验证码',
            icon: 'none'
          });
          return;
        }
        //校验邮箱格式
        let pattern = /[a-zA-Z0-9_.+-]+@[a-zA-Z0-9_-]+\.[a-zA-Z0-9-.]+/;
        if (!pattern.test(this.formData.mail)) {
          uni.showToast({
            title: '请输入正确的邮箱',
            icon: 'none'
          });
          return;
        }
				//登录远程服务器
        let that = this;
        util.request(api.Login,
          {
            mail: that.formData.mail,
            checkCode: that.formData.checkCode
          }, 'POST', 'application/json').then(res => {
          if (res.code === 0) {
            //存储用户信息
            uni.setStorageSync('userInfo', res.userInfo);
            uni.setStorageSync('token', res.token);
            uni.setStorageSync('userId', res.userId);
            //跳转至首页
            uni.switchTab({
              url: '/pages/index/index'
            });
          } else {
            // util.showErrorToast(res.errmsg)
            uni.showModal({
              title: '提示',
              content: res.data.msg,
              showCancel: false
            });
          }
        });
			},
      // 获取验证码
      sendCode: function() {
        let that = this;
        if (!that.formData.mail) {
          uni.showToast({
            title: '请输入邮箱',
            icon: 'none'
          });
          return;
        }
        //校验邮箱格式
        let pattern = /[a-zA-Z0-9_.+-]+@[a-zA-Z0-9_-]+\.[a-zA-Z0-9-.]+/;
        if (!pattern.test(this.formData.mail)) {
          uni.showToast({
            title: '请输入正确的邮箱',
            icon: 'none'
          });
          return;
        }
        if (that.disabled) {
          return;
        }
        that.disabled = true;
        let timer = setInterval(() => {
          that.timer--;
          that.sendTxt = that.timer + 's';
          if (that.timer <= 0) {
            clearInterval(timer);
            that.timer = 60;
            that.sendTxt = '获取验证码';
            that.disabled = false;
          }
        }, 1000);
        util.request(api.SendCode, {
          mail: that.formData.mail
        }, 'POST').then(res => {
          if (res.code === 0) {
            uni.showToast({
              title: '发送成功',
              icon: 'none'
            });
          } else {
            uni.showToast({
              title: res.msg,
              icon: 'none'
            });
          }
        });
      },
      toIndex: function() {
        uni.switchTab({
          url: '/pages/index/index'
        });
      },
		},
    onPullDownRefresh() {
      // 增加下拉刷新数据的功能
      uni.stopPullDownRefresh();
    },
	}
</script>