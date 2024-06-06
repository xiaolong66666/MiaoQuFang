<template>
  <view class="con">
    <image src="/static/images/logo.png" />
    <!-- 登录 -->
    <view class="login-form">
      <view :class="['item',errorTips==1? 'error':'']">
        <view class="account">
          <text class="input-item">
            账号
          </text>
          <input
              type="text"
              data-type="account"
              placeholder-class="inp-palcehoder"
              placeholder="请输入用户名"
              @input="getInputVal"
          >
        </view>
        <view
            v-if="errorTips==1"
            class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          请输入账号！
        </view>
      </view>
      <view :class="['item',errorTips==2? 'error':'']">
        <view class="account">
          <text class="input-item">
            密码
          </text>
          <input
              type="password"
              data-type="password"
              placeholder-class="inp-palcehoder"
              placeholder="请输入密码"
              @input="getInputVal"
          >
        </view>
        <view
            v-if="errorTips==2"
            class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          请输入密码！
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

<!--<script setup>-->
<!--import { encrypt } from './crypto'-->

<!--/**-->
<!-- * 生命周期函数&#45;&#45;监听页面显示-->
<!-- */-->
<!--onShow(() => {-->
<!--  // 头部导航标题-->
<!--  uni.setNavigationBarTitle({-->
<!--    title: '用户登录'-->
<!--  })-->
<!--})-->

<!--const principal = ref('') // 账号-->
<!--const errorTips = ref(0) // 错误提示-->
<!--watch(-->
<!--    () => principal.value,-->
<!--    () => {-->
<!--      errorTips.value = 0-->
<!--    }-->
<!--)-->

<!--const credentials = ref('') // 密码-->
<!--/**-->
<!-- * 输入框的值-->
<!-- */-->
<!--const getInputVal = (e) => {-->
<!--  const type = e.currentTarget.dataset.type-->
<!--  if (type == 'account') {-->
<!--    principal.value = e.detail.value-->
<!--  } else if (type == 'password') {-->
<!--    credentials.value = e.detail.value-->
<!--  }-->
<!--}-->

<!--/**-->
<!-- * 登录-->
<!-- */-->
<!--const login = () => {-->
<!--  if (principal.value.length == 0) {-->
<!--    errorTips.value = 1-->
<!--  } else if (credentials.value.length == 0) {-->
<!--    errorTips.value = 2-->
<!--  } else {-->
<!--    errorTips.value = 0-->
<!--    http.request({-->
<!--      url: '/login',-->
<!--      method: 'post',-->
<!--      data: {-->
<!--        userName: principal.value,-->
<!--        passWord: encrypt(credentials.value)-->
<!--      }-->
<!--    })-->
<!--        .then(({ data }) => {-->
<!--          http.loginSuccess(data, () => {-->
<!--            uni.showToast({-->
<!--              title: '登录成功',-->
<!--              icon: 'none',-->
<!--              complete: () => {-->
<!--                setTimeout(() => {-->
<!--                  wx.switchTab({-->
<!--                    url: '/pages/index/index'-->
<!--                  })-->
<!--                }, 1000)-->
<!--              }-->
<!--            })-->
<!--          })-->
<!--        })-->
<!--  }-->
<!--}-->

<!--/**-->
<!-- * 去注册-->
<!-- */-->
<!--const toRegitser = () => {-->
<!--  uni.navigateTo({-->
<!--    url: '/pages/register/register'-->
<!--  })-->
<!--}-->

<!--/**-->
<!-- * 回到首页-->
<!-- */-->
<!--const toIndex = () => {-->
<!--  wx.switchTab({-->
<!--    url: '/pages/index/index'-->
<!--  })-->
<!--}-->
<!--</script>-->

<style scoped lang="scss">
@import "./accountLogin.scss";
</style>











<!--&lt;!&ndash; 昵称（邮箱或者个人主页） &ndash;&gt;-->
<!--<template>-->
<!--	<view class="page">-->
<!--		<view class="hd">-->
<!--			<image class="logo" src="/static/images/logo.png"></image>-->
<!--			<view class="title">妙趣坊欢迎您!</view>-->
<!--		</view>-->
<!--		<view class="btn spacing">-->
<!--			&lt;!&ndash; 需要使用 button 来授权登录 &ndash;&gt;-->
<!--			<button class="weui_btn weui_btn_primary" > 微信登录 </button>-->
<!--		</view>-->
<!--	</view>-->
<!--</template>-->

<!--<script>-->
<!--	const util = require("@/utils/util.js")-->
<!--	const api = require('@/utils/api.js');-->
<!--	export default {-->
<!--		data() {-->
<!--			return {-->
<!--				// canIUseGetUserProfile: false,-->
<!--				// navUrl: '',-->
<!--				// code: ''-->
<!--			}-->
<!--		},-->
<!--		methods: {-->
<!--			getUserProfile() {-->
<!--				let that = this;-->
<!--				// 推荐使用uni.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认-->
<!--				// 开发者妥善保管用户快速填写的头像昵称，避免重复弹窗-->
<!--				uni.getUserProfile({-->
<!--					desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写-->
<!--					success: (resp) => {-->
<!--						//登录远程服务器-->
<!--						if (that.code) {-->
<!--							//登录远程服务器-->
<!--							that.loginByWeixin(resp)-->
<!--						} else {-->
<!--							uni.login({-->
<!--								success: function(resp) {-->
<!--									if (resp.code) {-->
<!--										that.code = resp.code-->
<!--										that.loginByWeixin(resp)-->
<!--									}-->
<!--								}-->
<!--							});-->
<!--						}-->
<!--					}-->
<!--				})-->
<!--			},-->
<!--			bindGetUserInfo: function(e) {-->
<!--				//登录远程服务器-->
<!--				this.loginByWeixin(e.detail)-->
<!--			},-->
<!--			loginByWeixin: function(userInfo) {-->
<!--				let that = this;-->
<!--				//登录远程服务器-->
<!--				if (that.code) {-->
<!--					util.request(api.AuthLoginByWeixin, {-->
<!--						code: that.code,-->
<!--						userInfo: userInfo-->
<!--					}, 'POST', 'application/json').then(res => {-->
<!--						if (res.errno === 0) {-->
<!--							//存储用户信息-->
<!--							uni.setStorageSync('userInfo', res.data.userInfo);-->
<!--							uni.setStorageSync('token', res.data.token);-->
<!--							uni.setStorageSync('userId', res.data.userId);-->

<!--						} else {-->
<!--							// util.showErrorToast(res.errmsg)-->
<!--							uni.showModal({-->
<!--								title: '提示',-->
<!--								content: res.errmsg,-->
<!--								showCancel: false-->
<!--							});-->
<!--						}-->
<!--					});-->
<!--				}-->
<!--				if (that.navUrl && that.navUrl == '/pages/index/index') {-->
<!--					uni.switchTab({-->
<!--						url: that.navUrl,-->
<!--					})-->
<!--				} else if (that.navUrl) {-->
<!--					uni.redirectTo({-->
<!--						url: that.navUrl,-->
<!--					})-->
<!--				}-->
<!--			}-->
<!--		},-->
<!--		onLoad: function(options) {-->
<!--			let that = this;-->
<!--			if (uni.getStorageSync("navUrl")) {-->
<!--				that.navUrl = uni.getStorageSync("navUrl")-->
<!--			} else {-->
<!--				that.navUrl = '/pages/index/index'-->
<!--			}-->
<!--			if (uni.getUserProfile) {-->
<!--				that.canIUseGetUserProfile = true-->
<!--			}-->
<!--			uni.login({-->
<!--				success: function(res) {-->
<!--					if (res.code) {-->
<!--						that.code = res.code-->
<!--					}-->
<!--				}-->
<!--			});-->
<!--		}-->
<!--	}-->
<!--</script>-->

<!--<style lang="scss">-->
<!--	view,-->
<!--	text {-->
<!--		font-family: PingFangSC-Light, helvetica, 'Heiti SC';-->
<!--		font-size: 29rpx;-->
<!--		color: #333;-->
<!--	}-->

<!--	.hd {-->
<!--		display: flex;-->
<!--		width: 100%;-->
<!--		flex-direction: column;-->
<!--		align-items: center;-->
<!--		justify-content: center;-->
<!--	}-->

<!--	.hd .logo {-->
<!--		width: 260rpx;-->
<!--		height: 260rpx;-->
<!--		margin-top: 40rpx;-->
<!--	}-->

<!--	.hd .title {-->
<!--		text-align: center;-->
<!--		font-size: 36rpx;-->
<!--		color: #000;-->
<!--	}-->

<!--	.bd {-->
<!--		width: 100%;-->
<!--		padding: 50rpx;-->
<!--	}-->

<!--	.bd .top_line {-->
<!--		width: 100%;-->
<!--		height: 1rpx;-->
<!--		background: #ccc;-->
<!--		margin-top: 20rpx;-->
<!--		margin-bottom: 50rpx;-->
<!--	}-->

<!--	.bd .m_name {-->
<!--		display: block;-->
<!--		font-size: 36rpx;-->
<!--		color: #000;-->
<!--	}-->

<!--	.bd .s_name {-->
<!--		margin-top: 25rpx;-->
<!--		display: block;-->
<!--		font-size: 34rpx;-->
<!--		color: #8a8a8a;-->
<!--	}-->

<!--	.btn {-->
<!--		padding: 120rpx 50rpx 0;-->
<!--	}-->

<!--	.weui_btn_primary {-->
<!--		background-color: #04be02;-->
<!--	}-->

<!--	.weui_btn {-->
<!--		position: relative;-->
<!--		display: block;-->
<!--		margin-left: auto;-->
<!--		margin-right: auto;-->
<!--		padding-left: 14px;-->
<!--		padding-right: 14px;-->
<!--		box-sizing: border-box;-->
<!--		font-size: 18px;-->
<!--		text-align: center;-->
<!--		text-decoration: none;-->
<!--		color: #fff;-->
<!--		line-height: 2.33333333;-->
<!--		border-radius: 5px;-->
<!--		-webkit-tap-highlight-color: rgba(0, 0, 0, 0);-->
<!--		overflow: hidden;-->
<!--	}-->

<!--	.weui_btn:after {-->
<!--		content: " ";-->
<!--		width: 200%;-->
<!--		height: 200%;-->
<!--		position: absolute;-->
<!--		top: 0;-->
<!--		left: 0;-->
<!--		border: 1px solid rgba(0, 0, 0, 0.2);-->
<!--		-webkit-transform: scale(0.5);-->
<!--		-ms-transform: scale(0.5);-->
<!--		transform: scale(0.5);-->
<!--		-webkit-transform-origin: 0 0;-->
<!--		-ms-transform-origin: 0 0;-->
<!--		transform-origin: 0 0;-->
<!--		box-sizing: border-box;-->
<!--		border-radius: 10px;-->
<!--	}-->
<!--</style>-->
<script setup>
</script>