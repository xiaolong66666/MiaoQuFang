package com.platform.entity;

import lombok.Data;

@Data
public class CommonVo {
    private Integer parentId;
    private String keyword;
    private Integer categoryId;
    private Integer brandId;
    private Integer isNew;
    private Integer isHot;
    private Integer page;
    private Integer size;
    private String sort;
    private String order;
    private Integer orderId;


}
