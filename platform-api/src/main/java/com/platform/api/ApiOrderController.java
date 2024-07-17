package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.dao.ApiCouponMapper;
import com.platform.entity.CouponVo;
import com.platform.entity.OrderGoodsVo;
import com.platform.entity.OrderVo;
import com.platform.entity.UserVo;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.util.ApiPageUtils;
import com.platform.utils.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: ApiIndexController
 */
@Api(tags = "订单-ApiOrderController")
@RestController
@RequestMapping("/api/order")
public class ApiOrderController extends ApiBaseAction {
    @Autowired
    private ApiOrderService orderService;
    @Autowired
    private ApiOrderGoodsService orderGoodsService;
    @Autowired
    private ApiKdniaoService apiKdniaoService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiCouponMapper apiCouponMapper;
    @Autowired
    private ApiPointsRecordService pointsRecordService;
    /**
     *
     */
    @ApiOperation(value = "订单首页")
    @IgnoreAuth
    @PostMapping("index")
    public Object index() {
        //
        return this.toResponseSuccess("");
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "获取订单列表")
    @PostMapping("list")
    public Object list(@LoginUser UserVo loginUser,@RequestBody JSONObject jsonParams) {
        Integer page = jsonParams.getInteger("page");
        Integer size = jsonParams.getInteger("size");
        if (null == page) {
            page = 1;
        }
        if (null == size) {
            size = 10;
        }
        //
        Map<String, Object> params = new HashMap<>();
        params.put("userId", loginUser.getUserId());
        params.put("page", page);
        params.put("limit", size);
        params.put("sidx", "add_time");
        params.put("order", "desc");
        //查询列表数据
        Query query = new Query(params);
        List<OrderVo> orderEntityList = orderService.queryList(query);
        int total = orderService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(orderEntityList, total, query.getLimit(), query.getPage());
        //
        for (OrderVo item : orderEntityList) {
            Map<String, Object> orderGoodsParam = new HashMap<>();
            orderGoodsParam.put("orderId", item.getId());
            //订单的商品
            List<OrderGoodsVo> goodsList = orderGoodsService.queryList(orderGoodsParam);
            Integer goodsCount = 0;
            for (OrderGoodsVo orderGoodsEntity : goodsList) {
                goodsCount += orderGoodsEntity.getNumber();
                item.setGoodsCount(goodsCount);
            }
        }
        return toResponseSuccess(pageUtil);
    }

    /**
     * 获取订单详情
     */
    @ApiOperation(value = "获取订单详情")
    @PostMapping("detail")
    public Object detail(@RequestBody JSONObject params, @LoginUser UserVo loginUser) {
        Integer orderId = params.getInteger("orderId");
        Map<String, Object> resultObj = new HashMap<>();
        //
        OrderVo orderInfo = orderService.queryObject(orderId);
        if (null == orderInfo) {
            return toResponsObject(400, "订单不存在", "");
        }

        if (!loginUser.getUserId().equals(orderInfo.getUserId())) {
            return toResponseFail("越权操作！");
        }
        Map<String, Object> orderGoodsParam = new HashMap<>();
        orderGoodsParam.put("orderId", orderId);
        //订单的商品
        List<OrderGoodsVo> orderGoods = orderGoodsService.queryList(orderGoodsParam);
        //订单最后支付时间
        if (orderInfo.getOrderStatus() == 0) {
            // if (moment().subtract(60, 'minutes') < moment(orderInfo.addTime)) {
//            orderInfo.final_pay_time = moment("001234", "Hmmss").format("mm:ss")
            // } else {
            //     //超过时间不支付，更新订单状态为取消
            // }
        }

        //订单可操作的选择,删除，支付，收货，评论，退换货
        Map<String, Object> handleOption = orderInfo.getHandleOption();
        //
        resultObj.put("orderInfo", orderInfo);
        resultObj.put("orderGoods", orderGoods);
        resultObj.put("handleOption", handleOption);
        if (!StringUtils.isEmpty(orderInfo.getShippingCode()) && !StringUtils.isEmpty(orderInfo.getShippingNo())) {
            // 快递
            List traces = apiKdniaoService.getOrderTracesByJson(orderInfo.getShippingCode(), orderInfo.getShippingNo());
            resultObj.put("shippingList", traces);
        }
        return toResponseSuccess(resultObj);
    }

    @ApiOperation(value = "修改订单")
    @PostMapping("updateSuccess")
    public Object updateSuccess(@RequestBody JSONObject jsonParams, @LoginUser UserVo loginUser) {
        Integer orderId = jsonParams.getInteger("orderId");
        OrderVo orderInfo = orderService.queryObject(orderId);
        if (orderInfo == null) {
            return toResponseFail("订单不存在");
        } else if (orderInfo.getOrderStatus() != 0) {
            return toResponseFail("订单状态不正确orderStatus" + orderInfo.getOrderStatus() + "payStatus" + orderInfo.getPayStatus());
        }

        if (!loginUser.getUserId().equals(orderInfo.getUserId())) {
            return toResponseFail("越权操作！");
        }
        orderInfo.setId(orderId);
        orderInfo.setPayStatus(2);
        orderInfo.setOrderStatus(201);
        orderInfo.setShippingStatus(0);
        orderInfo.setPayTime(new Date());
        int num = orderService.update(orderInfo);
        if (num > 0) {
            return toResponseSuccess("修改订单成功");
        } else {
            return toResponseFail("修改订单失败");
        }
    }

    /**
     * 获取订单列表
     */
    @ApiOperation(value = "订单提交")
    @PostMapping("submit")
    public Object submit(@LoginUser UserVo loginUser) {
        Map<String, Object> resultObj = null;
        try {
            resultObj = orderService.submit(getJsonRequest(), loginUser);
            if (null != resultObj) {
                return toResponsObject(MapUtils.getInteger(resultObj, "errno"), MapUtils.getString(resultObj, "errmsg"), resultObj.get("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toResponseFail("提交失败");
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @PostMapping("cancelOrder")
    @Transactional
    public Object cancelOrder(@RequestBody JSONObject jsonPrams , @LoginUser UserVo loginUser) {
        Integer orderId = jsonPrams.getInteger("orderId");
        OrderVo orderVo = orderService.queryObject(orderId);
        if (null == orderVo) {
            return toResponseFail("订单不存在！");
        }
        if (!loginUser.getUserId().equals(orderVo.getUserId())) {
            return toResponseFail("越权操作！");
        }
        if (orderVo.getOrderStatus() == 300) {
            return toResponseFail("已发货，不能取消");
        } else if (orderVo.getOrderStatus() == 301) {
            return toResponseFail("已收货，不能取消");
        }
        // 需要退款
        // 退积分
        if (orderVo.getPointsPay().compareTo(new BigDecimal(0)) > 0) {
            UserVo userVo = userService.queryObject(getUserId());
            userVo.setPoints(userVo.getPoints().add(orderVo.getPointsPay()));
            //积分记录
            pointsRecordService.addPintsRecord(loginUser.getUserId(),3,1,orderVo.getPointsPay());
            userService.update(userVo);
        }

        orderVo.setOrderStatus(101);
        orderService.update(orderVo);
        //退回优惠券
        if (orderVo.getCouponId() != null) {
            //TODO 退回优惠券
            apiCouponMapper.returnCoupon(orderVo.getCouponId());
        }
        return this.toResponseSuccess("取消成功");
    }

    /**
     * 确认收货
     */
    @ApiOperation(value = "确认收货")
    @PostMapping("confirmOrder")
    @Transactional
    public Object confirmOrder(@RequestBody JSONObject jsonParams, @LoginUser UserVo loginUser) {
        try {
            Integer orderId = jsonParams.getInteger("orderId");
            OrderVo orderVo = orderService.queryObject(orderId);
            if (null == orderVo) {
                return toResponseFail("订单不存在！");
            }
            if (!loginUser.getUserId().equals(orderVo.getUserId())) {
                return toResponseFail("越权操作！");
            }
            orderVo.setOrderStatus(301);
            orderVo.setShippingStatus(2);
            orderVo.setConfirmTime(new Date());
            orderService.update(orderVo);
            return this.toResponseSuccess("确认收货成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toResponseFail("提交失败");
    }
}
