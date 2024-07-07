package com.platform.service;
public interface SendMailService {
    void seedMessage(String title,String to,String context);
    //检查验证码
    Boolean checkCode(String mail,String code);
}
