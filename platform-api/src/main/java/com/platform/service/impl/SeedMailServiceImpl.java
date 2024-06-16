package com.platform.service.impl;
import com.platform.service.SendMailService;
import com.platform.util.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
@Service
public class SeedMailServiceImpl implements SendMailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    //发送人
    private String from="2636822826@qq.com";
    //接收人
    private String to;

    public void setTo(String to) {
        this.to = to;
    }
    //标题
    private String title="妙趣坊商城";
    //验证码
    private String code;

    public void setCode(String code) {
        this.code = code;
    }
    @Autowired
    private CodeUtils codeUtils;
    //正文
    private String context;

    public void setContext(String context) {
        this.context = context;
    }
    @Override
    public void seedMessage() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(title);
        helper.setText(context);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public Boolean checkCode(String mail,String code) {
        String code1 = codeUtils.getCode(mail);
        return code.equals(code1);
    }
}
