package com.platform.dao;

import com.platform.entity.OrderVo;
import org.apache.ibatis.annotations.Param;

public interface ApiOrderMapper extends BaseDao<OrderVo> {

    /**
     * 根据订单编号查询订单
     *
     * @param orderSn
     * @return
     */
    OrderVo queryObjectByOrderSn(@Param("orderSn") String orderSn);
}
