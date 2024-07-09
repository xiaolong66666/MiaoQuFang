<template>
	<view>
		<view class="userinfo">
			<image class="userinfo-avatar" :src="userInfo.avatar" background-size="cover"></image>
			<text class="userinfo-nickname">{{userInfo.nickname}}</text>
		</view>
		<view class="separate"></view>

    <view>
      <view v-if="usedCode === ''" class="login-title">请填写邀请码</view>
      <view v-if="usedCode !== ''" class="login-title">我的邀请码</view>
      <view class="login">
        <view v-if="usedCode === ''" class="second-line">
          <input type="text" name="code" v-model="codeInfo.usedCode" placeholder="填写可获得5积分" />
          <button @tap="submitCode">提交邀请码</button>
        </view>
        <view v-if="usedCode !== ''" class="second-line">
          <button v-if="code !== null && code !== ''">我的邀请码</button>
          <input type="text" name="code"  v-model="codeInfo.code" placeholder="我的邀请码" disabled/>
          <button v-if="code === null || code === ''" @tap="getCode">获取邀请码</button>
        </view>
      </view>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	const app = getApp();
	export default {
		data() {
			return {
        userInfo: app.globalData.userInfo,
        code: "",
        usedCode: "",
        codeInfo: {
          code: "",
          usedCode: ""
        }
			}
		},
		methods: {
      //获取填写邀请码
      getCodeMsg() {
        let that = this;
        util.request(api.GetCodeMsg,null,"POST", "application/json").then(function(res) {
          if (res.code === 0) {
            that.code = res.userCode.code;
            that.usedCode = res.userCode.usedCode;
            console.log(that.code);
            console.log(that.usedCode);
          }else {
            //提示错误信息
            uni.showModal({
              title: '提示',
              content: res.data.msg,
              showCancel: false
            });
          }
        });
      },
      //填写用户邀请码
      submitCode() {
        let that = this;
        util.request(api.SubmitCode,that.codeInfo,"POST", "application/json")
            .then(function(res) {
          if (res.code === 0) {
            that.getCodeMsg();
          }
        });
      },
      //获取用户邀请码
      getCode() {
        let that = this;
        util.request(api.GetCode, {
          userId: that.userInfo.id
        },"POST", "application/json").then(function(res) {
          if (res.errno === 0) {
            that.code = res.errmsg;
            that.codeInfo.code = res.errmsg;
          }
        });
      }
		},
    onLoad: function() {
      //验证码
      this.getCodeMsg();
    },
    onPullDownRefresh: function() {
      //验证码
      this.getCodeMsg();
      uni.stopPullDownRefresh();
    }
	}
</script>

<style lang="scss">
	.userinfo {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-bottom: 25rpx;
		background: linear-gradient(to bottom, #FCD33D 0%, #FFF6D7 100%);
	}

	.userinfo-avatar {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		margin-top: 20rpx;
		margin-bottom: 25rpx;
	}

	.userinfo-nickname {
		color: #272727;
		font-size: 28rpx;
		line-height: 40rpx;
	}

	.separate {
		height: 18rpx;
		background-color: #f2f2f2;
	}

	.zichan {
		display: flex;
		flex-direction: column;
	}

	.zichan .first-line {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		font-size: 27rpx;
		margin-left: 20rpx;
		margin-top: 20rpx;
		margin-bottom: 20rpx;
	}

	.zichan .second-line {
		padding-top: 15rpx;
		padding-bottom: 15rpx;
		border-top: 1rpx solid #F6F6F6;
		border-bottom: 1rpx solid #F6F6F6;
		height: max-content;
	}

	.long-view {
		display: flex;
		flex-direction: row;
		width: 1850rpx;
	}

	.zichan .second-line .item {
		width: 560rpx;
		height: 264rpx;
		margin-left: 20rpx;
	}

	.zichan .second-line .item .image {
		height: 264rpx;
	}

	.zichan .second-line .desc {
		position: relative;
		top: -262rpx;
		left: 0;
		width: 560rpx;
		height: 264rpx;
		background-color: rgba(0, 0, 0, 0.3);
		border-radius: 5px;
		text-align: center;
		color: #fff;
		display: flex;
		flex-direction: column;
		justify-content: center;
		font-size: 25rpx;
	}

	.desc-line {
		margin-top: 5rpx;
		margin-bottom: 5rpx;
	}

	.desc-line.asset-count {
		font-size: 32rpx;
		color: #FFC800;
	}

	.slide-img {
		border-radius: 5px;
		width: 560rpx;
		height: 264rpx;
	}


	/* 绑定手机号的两个form */
	.login-title {
		margin: 20rpx 0 35rpx;
		text-align: center;
		font-size: 30rpx;
	}

	.login {
		font-size: 32rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.login .first-line {
		height: 80rpx;
		border: 1rpx solid rgb(217, 217, 217);
		border-radius: 5px;
		width: 600rpx;
		margin-bottom: 20rpx;
	}

	.login .first-line input {
		padding-left: 20rpx;
		height: 80rpx;
	}

	.login .second-line {
		height: 80rpx;
		display: flex;
		width: 600rpx;
		margin-bottom: 50rpx;
	}

	.login .second-line input {
		height: 80rpx;
		width: 350rpx;
		margin-right: 20rpx;
		border: 1rpx solid rgb(217, 217, 217);
		padding-left: 20rpx;
		border-radius: 5px;
	}

	.login .second-line button {
		text-align: center;
		height: 84rpx;
		line-height: 84rpx;
		width: 250rpx;
		font-size: 30rpx;
		background-color: #FFC800;
	}

	.login .password-second-line {
		height: 80rpx;
		display: flex;
		width: 600rpx;
		margin-bottom: 50rpx;
	}

	.login .password-second-line input {
		height: 80rpx;
		width: 600rpx;
		border: 1rpx solid rgb(217, 217, 217);
		padding-left: 20rpx;
		border-radius: 5px;
	}

	.third-line {
		margin-left: auto;
		margin-right: auto;
		width: 600rpx;
	}

	.third-line button {
		height: 80rpx;
		font-size: 32rpx;
		background-color: #FFC800;
	}

	.login-type {
		width: 600rpx;
		margin: 60rpx auto 0 auto;
		padding: 20rpx 0 20rpx;
		font-size: 32rpx;
		text-align: right;
		text-decoration: underline;
	}


	.profile-button-container {
		display: flex;
		flex-wrap: wrap;
		margin-top: 10rpx;
		margin-bottom: 10rpx;
	}

	.profile-button {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 188rpx;
		height: 130rpx;
	}

	.profile-button image {
		width: 36rpx;
		height: 36rpx;
	}

	.profile-button text {
		font-size: 25rpx;
	}
</style>
