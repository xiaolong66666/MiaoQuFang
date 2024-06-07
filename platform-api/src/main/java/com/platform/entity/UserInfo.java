package com.platform.entity;

import lombok.Data;

import java.io.Serializable;


/**
 * @author lipengjun
 * @date 2017-08-15 08:03:41
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    //用户id
    private Long userId;
    //
    private String avatar;
    //
    private String city;
    //
    private Integer gender;
    //
    private String nickname;
    //
    private String province;
}
