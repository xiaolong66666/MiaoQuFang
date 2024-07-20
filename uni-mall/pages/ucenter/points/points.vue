<template>
	<view class="container">
		<view class="orders" v-if="pointsRecordList.length>0">
			<navigator class="order" v-for="(item, index) in pointsRecordList" :key="item.id">
				<view class="h">
					<view class="l">积分来源：{{sourceMap[item.source]}}</view>
					<view class="r">{{typeMap[item.type]}}{{item.points}}</view>
				</view>
				<view class="b">
					<view class="l">积分余额：{{item.totalPoints}}</view>
				</view>
			</navigator>
		</view>
		<tui-show-empty v-else text="暂无记录"></tui-show-empty>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
        pointsRecordList: [],
				page: 1,
        size: 10,
        sourceMap: {
          1: '系统',
          2: '邀请',
          3: '购物',
          4: '提现',
          5: '未知'
        },
        typeMap:{
          1: '+',
          2: '-'
        }
			}
		},
		methods: {
			getPointsRecordList() {
				let that = this;
				util.request(api.PointsRecordList, {
					page: that.page,
          size: that.size
				}).then(function(res) {
					if (res.errno === 0) {
            if (res.data.data.length > 0) {
              that.pointsRecordList = that.pointsRecordList.concat(res.data.data)
              that.page += 1
            }
					}
				});
			}
		},
		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {
			this.getPointsRecordList()
		},
		onShow: function(options) {
			this.getPointsRecordList();
		},
    onPullDownRefresh: function() {
      this.page = 1;
      this.pointsRecordList = [];
      this.getPointsRecordList();
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

	.orders {
		height: auto;
		width: 100%;
		overflow: hidden;
	}

	.order {
		margin-top: 20rpx;
		background: #fff;
	}

	.order .h {
		height: 83.3rpx;
		line-height: 83.3rpx;
		margin-left: 31.25rpx;
		padding-right: 31.25rpx;
		border-bottom: 1px solid #f4f4f4;
		font-size: 30rpx;
		color: #333;
	}

	.order .h .l {
		float: left;
	}

	.order .h .r {
		float: right;
		color: #b4282d;
		font-size: 24rpx;
	}

	.order .goods {
		display: flex;
		align-items: center;
		height: 199rpx;
		margin-left: 31.25rpx;
	}

	.order .goods .img {
		height: 145.83rpx;
		width: 145.83rpx;
		background: #f4f4f4;
	}

	.order .goods .img image {
		height: 145.83rpx;
		width: 145.83rpx;
	}

	.order .goods .info {
		height: 145.83rpx;
		flex: 1;
		padding-left: 20rpx;
	}

	.order .goods .name {
		margin-top: 30rpx;
		display: block;
		height: 44rpx;
		line-height: 44rpx;
		color: #333;
		font-size: 30rpx;
	}

	.order .goods .number {
		display: block;
		height: 37rpx;
		line-height: 37rpx;
		color: #666;
		font-size: 25rpx;
	}

	.order .goods .status {
		width: 105rpx;
		color: #b4282d;
		font-size: 25rpx;
	}

	.order .b {
		height: 103rpx;
		line-height: 103rpx;
		margin-left: 31.25rpx;
		padding-right: 31.25rpx;
		border-top: 1px solid #f4f4f4;
		font-size: 30rpx;
		color: #333;
	}

	.order .b .l {
		float: left;
	}

	.order .b .r {
		float: right;
	}

	.order .b .btn {
		margin-top: 19rpx;
		height: 64.5rpx;
		line-height: 64.5rpx;
		text-align: center;
		padding: 0 20rpx;
		border-radius: 5rpx;
		font-size: 26rpx;
		color: #fff;
		background: #b4282d;
	}
</style>
