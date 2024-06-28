<!-- 昵称（邮箱或者个人主页） -->
<template>
	<view class="container">
		<view class="pay-result">
			<view class="success">
        <view style="display: flex; justify-content: center; align-items: center;">
          <image src="/static/images/pay.png" mode="aspectFit" @tap = 'previewImg()' style="width: 400rpx;height: 400rpx;"/>
        </view>
				<view class="msg">
          您的订单
          <span style="text-decoration: underline;color: red">
          {{orderSn?orderSn:0}}
          </span>
          <p>
            需支付<span style="text-decoration: underline;color: red">{{pay?pay:0}}</span>元
          </p>
          <p>请将付款截屏、订单号发送给客服......</p>
        </view>
				<view class="btns">
					<navigator class="btn" url="/pages/ucenter/order/order" open-type="redirect">查看订单</navigator>
          <a href="https://work.weixin.qq.com/kfid/kfce9524b2cf9333141" class="btn">联系客服</a>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				status: false,
				orderId: 0,
        pay: 0,
			}
		},
		methods: {
      previewImg() {
        uni.previewImage({
          current: '/static/images/pay.png',
          urls: ['/static/images/pay.png']
        });
      },
      updateSuccess: function() {
				let that = this
				util.request(api.OrderQuery, {
					orderId: that.orderId
				}).then(function(res) {})
			},
			payOrder() {
        //校验订单id
        if (!this.orderId) {
          util.toast('订单id不能为空');
          return;
        }
				util.payOrder(parseInt(this.orderId)).then(res => {
					that.status = true
				}).catch(res => {
					util.toast('支付失败');
				});
			}
		},
		onLoad: function(options) {
			// 页面初始化 options为页面跳转所带来的参数
			this.orderSn = options.orderSn
			this.pay = options.pay
		},
    onPullDownRefresh() {
      // 增加下拉刷新数据的功能
      uni.stopPullDownRefresh();
    },
	}
</script>

<style lang="scss">
	page {
		min-height: 100%;
		width: 100%;
		background: #fff;
	}

	.container {
		height: 100%;
		background: #fff;
	}

	.pay-result {
		background: #fff;
	}

	.pay-result .msg {
		text-align: center;
		margin: 100rpx auto;
		color: #2bab25;
		font-size: 36rpx;
	}

	.pay-result .btns {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pay-result .btn {
		text-align: center;
		height: 80rpx;
		margin: 0 20rpx;
		width: 200rpx;
		line-height: 78rpx;
		border: 1px solid #868686;
		color: #000000;
		border-radius: 5rpx;
	}

	.pay-result .error .msg {
		color: #b4282d;
		margin-bottom: 60rpx;
	}

	.pay-result .error .tips {
		color: #7f7f7f;
		margin-bottom: 70rpx;
	}

	.pay-result .error .tips .p {
		font-size: 24rpx;
		line-height: 42rpx;
		text-align: center;
	}

	.pay-result .error .tips .p {
		line-height: 42rpx;
		text-align: center;
	}
</style>
