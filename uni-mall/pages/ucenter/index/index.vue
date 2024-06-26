<template>
	<view class="container">
		<button v-if="canIUseGetUserProfile" class="userinfo" @tap="getUserProfile">
			<image class="userinfo-avatar" :src="userInfo.avatar" background-size="cover"></image>
			<text class="userinfo-nickname">
        {{ userInfo.nickname}}(余额:{{userPoints}})
      </text>
		</button>
		<button v-else class="userinfo" open-type="getUserInfo" @getuserinfo="bindGetUserInfo">
			<image class="userinfo-avatar" :src="userInfo.avatar" background-size="cover"></image>
			<text class="userinfo-nickname">
        {{ userInfo.nickname}}(余额:{{userPoints}})
      </text>
		</button>
		<view style="height:20rpx;background: #eee;width:100%;"></view>
		<view class="my-item" style='background:none;display:flex;flex-direction:column;height:auto;'></view>

		<view class="user-menu">
			<view class="item">
				<navigator url="/pages/ucenter/order/order" class="a">
					<text class="icon order"></text>
					<text class="txt">我的订单</text>
				</navigator>
			</view>
			<view class="item">
				<navigator url="/pages/ucenter/coupon/coupon" class="a">
					<text class="icon coupon"></text>
					<text class="txt">优惠券</text>
				</navigator>
			</view>
			<view class="item">
				<navigator url="/pages/ucenter/collect/collect" class="a">
					<text class="icon address"></text>
					<text class="txt">我的收藏</text>
				</navigator>
			</view>
			<view class="item">
				<navigator url="/pages/ucenter/footprint/footprint" class="a">
					<text class="icon security"></text>
					<text class="txt">我的足迹</text>
				</navigator>
			</view>
			<view class="item">
				<navigator url="../address/address" class="a">
					<text class="icon address"></text>
					<text class="txt">地址管理</text>
				</navigator>
			</view>
			<button class='service' open-type="contact">
				<view class="item no-border">
          <a href="https://work.weixin.qq.com/kfid/kfce9524b2cf9333141" class="a no-underline">
            <text class="icon kefu"></text>
            <text class="txt">联系客服</text>
          </a>
				</view>
			</button>
			<view class="item item-bottom">
				<navigator url="/pages/ucenter/help/help" class="a">
					<text class="icon help"></text>
					<text class="txt">帮助中心</text>
				</navigator>
			</view>
			<view class="item item-bottom">
				<navigator url="/pages/ucenter/feedback/feedback" class="a">
					<text class="icon feedback"></text>
					<text class="txt">意见反馈</text>
				</navigator>
			</view>
<!--			<view class="item item-bottom" v-if="!hasMobile">-->
<!--				<navigator url="/pages/auth/mobile/mobile" class="a">-->
<!--					<text class="icon phone"></text>-->
<!--					<text class="txt">修改邮箱</text>-->
<!--				</navigator>-->
<!--			</view>-->
		</view>
		<view class="logout" v-if="userInfo.userName!='点击去登录'" @tap="exitLogin">退出登录</view>
		<view class="logout" v-else @tap="goLogin">点击去登录</view>
    <view class='company'>
          深圳市妙趣坊贸易有限公司
    </view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js")
	const api = require('@/utils/api.js');
	const app = getApp();
	export default {
		data() {
			return {
				canIUseGetUserProfile: false,
				userInfo: {},
				hasMobile: '',
        userPoints: 0
			}
		},
		methods: {
			loginByWeixin: function(userInfo) {
				let code = null;
				return new Promise(function(resolve, reject) {
					return util.login().then((res) => {
						code = res.code;
						return userInfo;
					}).then((userInfo) => {
						//登录远程服务器
						util.request(api.AuthLoginByWeixin, {
							code: code,
							userInfo: userInfo
						}, 'POST', 'application/json').then(res => {
							if (res.errno === 0) {
								//存储用户信息
								uni.setStorageSync('userInfo', res.data.userInfo);
								uni.setStorageSync('token', res.data.token);

								resolve(res);
							} else {
								util.toast(res.errmsg)
								reject(res);
							}
						}).catch((err) => {
							reject(err);
						});
					}).catch((err) => {
						reject(err);
					})
				});
			},
			getUserProfile() {
				let that = this;
				// 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认
				// 开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
				wx.getUserProfile({
					desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
					success: (resp) => {
						//登录远程服务器
						that.loginByWeixin(resp).then(res => {
							that.userInfo = res.data.userInfo
							app.globalData.userInfo = res.data.userInfo;
							app.globalData.token = res.data.token;
						}).catch((err) => {
							console.log(err)
						});
					}
				})
			},
			bindGetUserInfo(e) {
				let that = this;
				that.loginByWeixin(e.detail).then(res => {
					that.userInfo = res.data.userInfo
					app.globalData.userInfo = res.data.userInfo;
					app.globalData.token = res.data.token;
				}).catch((err) => {
					console.log(err)
				});
			},
      goLogin: function() {
        uni.navigateTo({
          url: '/pages/auth/login/login'
        });
      },
			exitLogin: function() {
				uni.showModal({
					title: '',
					confirmColor: '#b4282d',
					content: '退出登录？',
					success: function(res) {
						if (res.confirm) {
							uni.removeStorageSync('token');
							uni.removeStorageSync('userInfo');
							app.globalData.userInfo = {
                nickname: 'Hi,游客',
								userName: '点击去登录',
								avatar: '/static/images/unlogin_avatar.png'
							}
							util.toast('退出成功');
							uni.switchTab({
								url: '/pages/index/index'
							});
						}
					}
				})
			},
      getPoints(){
        //如果用户已登录，不发送请求
        if (!app.globalData.token) {
          return;
        }
        let that = this;
        util.request(api.Points).then(res => {
          if (res.errno === 0) {
            console.log(res.data);
            that.userPoints = res.data;
          }
        });

      }
		},
		onShow: function() {
			let that = this;
			let userInfo = uni.getStorageSync('userInfo');
			let token = uni.getStorageSync('token');

			// 页面显示
			if (userInfo && token) {
				app.globalData.userInfo = userInfo;
				app.globalData.token = token;
			} else {
				uni.login({
					success: function(res) {
						if (res.code) {
							that.code = res.code
						}
					}
				});
			}
			that.userInfo = app.globalData.userInfo
		},
		onLoad: function() {
      //获取积分
      this.getPoints();
			// 页面初始化 options为页面跳转所带来的参数
			if (wx.getUserProfile) {
				this.canIUseGetUserProfile = true
			}
		},
    onPullDownRefresh: function() {
      // 下拉刷新
      this.getPoints();
      uni.stopPullDownRefresh();
    }
	}
</script>

<style lang="scss">
	page {
		height: 100%;
		width: 100%;
		background: #f4f4f4;
	}

	.container {
		background: #f4f4f4;
		height: auto;
		overflow: hidden;
		width: 100%;
	}

	.userinfo {
		display: flex;
		flex-direction: column;
		padding: 50rpx 0;
		align-items: center;
		background: #333;
		width: 750rpx;
	}

	.userinfo-avatar {
		width: 160rpx;
		height: 160rpx;
		margin: 20rpx;
		border-radius: 50%;
	}

	.userinfo-nickname {
		margin-top: 20rpx;
		color: #FFF;
	}

	.profile-info {
		width: 100%;
		height: 280rpx;
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		justify-content: flex-start;
		padding: 0 30.25rpx;
		background: #333;
	}

	.profile-info .avatar {
		height: 148rpx;
		width: 148rpx;
		border-radius: 50%;
	}

	.profile-info .info {
		flex: 1;
		height: 85rpx;
		padding-left: 31.25rpx;
	}

	.profile-info .name {
		display: block;
		height: 45rpx;
		line-height: 45rpx;
		color: #fff;
		font-size: 37.5rpx;
		margin-bottom: 10rpx;
	}

	.profile-info .level {
		display: block;
		height: 30rpx;
		line-height: 30rpx;
		margin-bottom: 10rpx;
		color: #7f7f7f;
		font-size: 30rpx;
	}

	.user-menu {
		width: 100%;
		height: auto;
		overflow: hidden;
		background: #fff;
	}

	.user-menu .item {
		float: left;
		width: 33.33333%;
		height: 187.5rpx;
		border-right: 1px solid rgba(0, 0, 0, .15);
		border-bottom: 1px solid rgba(0, 0, 0, .15);
		text-align: center;
	}

	.user-menu .item .a {
		display: flex;
		width: 100%;
		height: 100%;
		flex-direction: column;
		align-items: center;
		justify-content: center;
    text-decoration: none;
	}

	.user-menu .item.no-border {
		border-right: 0;
	}

	.user-menu .item.item-bottom {
		border-bottom: none;
	}

	.user-menu .icon {
		margin: 0 auto;
		display: block;
		height: 52.803rpx;
		width: 52.803rpx;
		margin-bottom: 16rpx;
	}

	.user-menu .icon.order {
		background: url(/h5/static/images/ucenter.png) 0 -437.5rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.coupon {
		background: url(/h5/static/images/ucenter.png) 0 -62.4997rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.gift {
		background: url(/h5/static/images/ucenter.png) 0 -187.5rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.address {
		background: url(/h5/static/images/ucenter.png) 0 0 no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.security {
		background: url(/h5/static/images/ucenter.png) 0 -500rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.kefu {
		background: url(/h5/static/images/ucenter.png) 0 -312.5rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.help {
		background: url(/h5/static/images/ucenter.png) 0 -250rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.feedback {
		background: url(/h5/static/images/ucenter.png) 0 -125rpx no-repeat;
		background-size: 52.803rpx;
	}

	.user-menu .icon.phone {
		background: url(/h5/static/images/phone.png) no-repeat;
		background-size: 52.803rpx;
	}


	.user-menu .txt {
		display: block;
		height: 24rpx;
		width: 100%;
		font-size: 24rpx;
		color: #333;
	}

	.logout {
		margin-top: 50rpx;
		height: 101rpx;
		width: 100%;
		line-height: 101rpx;
		text-align: center;
		background: #fff;
		color: #333;
		font-size: 30rpx;
	}

	.service {
		position: static;
		background-color: transparent;
		color: transparent;
		margin: 0;
		padding: 0;
		border: none;
		text-align: left;
		line-height: normal;
		display: inline;
	}

	.company {
		font-size: 20rpx;
		text-align: center;
		margin-top: 40px;
	}
</style>
