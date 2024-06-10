package com.platform.service;

import javax.mail.MessagingException;

public interface SendMailService {
    void seedMessage() throws MessagingException;
    //检查验证码
    Boolean checkCode(String mail,String code);
}
