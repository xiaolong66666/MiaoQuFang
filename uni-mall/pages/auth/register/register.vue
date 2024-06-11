<template>
    <view>
    </view>
</template>

<script>
    const util = require("@/utils/util.js")
    export default {
        data() {
            return {
            }
        },
        methods: {
        },
        onLoad: function() {
        },
      onPullDownRefresh() {
        // 增加下拉刷新数据的功能
        uni.stopPullDownRefresh();
      },

    }
</script>

<style lang="scss">
</style>
