package com.platform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PointsRecordEntity {
    //    id           int auto_increment
    //        primary key,
    //    user_id      int            not null comment '用户id',
    //    points       decimal(10, 2) not null comment '积分',
    //    source       int            not null comment '来源 1:系统 2:邀请 3:购物',
    //    type         int            not null comment '类型 1:增加 2:减少',
    //    `desc`       varchar(255)   null comment '描述',
    //    total_points decimal(10, 2) not null comment '总积分',
    //    create_time  datetime       null comment '创建时间'
    private Integer id;
    private Integer userId;
    private BigDecimal points;
    private Integer source;
    private Integer type;
    private String desc;
    private BigDecimal totalPoints;
    private Date createTime;

    //其他
    private String userName;
}
