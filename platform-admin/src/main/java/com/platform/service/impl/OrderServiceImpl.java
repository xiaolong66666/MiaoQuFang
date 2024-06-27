package com.platform.service.impl;

import com.platform.dao.OrderDao;
import com.platform.dao.ShippingDao;
import com.platform.entity.OrderEntity;
import com.platform.entity.ShippingEntity;
import com.platform.entity.UserVo;
import com.platform.service.ApiUserService;
import com.platform.service.OrderService;
import com.platform.utils.RRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ShippingDao shippingDao;
    @Autowired
    private ApiUserService userService;

    @Override
    public OrderEntity queryObject(Integer id) {
        return orderDao.queryObject(id);
    }

    @Override
    public List<OrderEntity> queryList(Map<String, Object> map) {
        return orderDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return orderDao.queryTotal(map);
    }

    @Override
    public int save(OrderEntity order) {
        return orderDao.save(order);
    }

    @Override
    public int update(OrderEntity order) {
        return orderDao.update(order);
    }

    @Override
    public int delete(Integer id) {
        return orderDao.delete(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return orderDao.deleteBatch(ids);
    }

    @Override
    public int confirm(Integer id) {
        OrderEntity orderEntity = queryObject(id);
        Integer shippingStatus = orderEntity.getShippingStatus();//发货状态
        Integer payStatus = orderEntity.getPayStatus();//付款状态
        if (2 != payStatus) {
            throw new RRException("此订单未付款，不能确认收货！");
        }
        if (4 == shippingStatus) {
            throw new RRException("此订单处于退货状态，不能确认收货！");
        }
        if (0 == shippingStatus) {
            throw new RRException("此订单未发货，不能确认收货！");
        }
        orderEntity.setShippingStatus(2);
        orderEntity.setOrderStatus(301);
        return orderDao.update(orderEntity);
    }

    @Override
    public int sendGoods(OrderEntity order) {
        Integer payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
            throw new RRException("此订单未付款！");
        }

        ShippingEntity shippingEntity = shippingDao.queryObject(order.getShippingId());
        if (null != shippingEntity) {
            order.setShippingName(shippingEntity.getName());
        }
        order.setOrderStatus(300);//订单已发货
        order.setShippingStatus(1);//已发货
        return orderDao.update(order);
    }

    @Override
    @Transactional
    public int confirmPay(Integer id, Integer status) {
        OrderEntity orderEntity = queryObject(id);
        Integer payStatus = orderEntity.getPayStatus();//付款状态
        Integer orderStatus = orderEntity.getOrderStatus();
        //无法修改付款状态,支付状态不为0或201
        if (0 != orderStatus && 201 != orderStatus) {
            throw new RRException("该订单状态不能修改付款状态！");
        }
        UserVo userVo = userService.queryObject(Long.valueOf(orderEntity.getUserId()));
        if (2 == status && 0 == payStatus) {
            orderEntity.setPayStatus(2);
            orderEntity.setOrderStatus(201);
            //扣减用户积分
            //判断用户积分是否足够
            if (userVo.getPoints().compareTo(orderEntity.getPointsPay()) < 0) {
                throw new RRException("用户余额不足！");
            }
            userVo.setPoints(userVo.getPoints().subtract(orderEntity.getPointsPay()));
            return orderDao.update(orderEntity);
        }
        //取消付款
        if (0 == status && 2 == payStatus) {
            orderEntity.setPayStatus(0);
            orderEntity.setOrderStatus(0);
            //退还用户积分
            userVo.setPoints(userVo.getPoints().add(orderEntity.getPointsPay()));
            return orderDao.update(orderEntity);
        }
        return 0;
    }
}
