package com.platform.service.impl;
import com.platform.service.SendMailService;
import com.platform.util.CodeUtils;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class SeedMailServiceImpl implements SendMailService {
    //发送人
    private String from="2636822826@qq.com";
    //标题
    private String title="妙趣坊商城";
    //验证码
    private String code;

    public void setCode(String code) {
        this.code = code;
    }
    @Autowired
    private CodeUtils codeUtils;
    @Override
    public void seedMessage(String to,String context) throws MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.host", "smtp.qq.com");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.port", "587");
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.trust", "*");
            prop.put("mail.smtp.ssl.socketFactory", sf);
            // 创建session，并验证用户名和密码
            Session session = Session.getDefaultInstance(prop, new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, "iiasvfniiovzecfa");
                }});
            // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);
            Message message = createSimpleMail(session,to,context);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public MimeMessage createSimpleMail(Session session,String to,String context) throws Exception {
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress(from));
        // 指明邮件的收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        // 邮件的标题
        message.setSubject(title);
        // 邮件的文本内容
        message.setContent(context, "text/html;charset=UTF-8");
        // 设置邮件消息发送的时间
        message.setSentDate(new Date());
        // 返回创建好的邮件对象
        return message;
    }

    @Override
    public Boolean checkCode(String mail,String code) {
        String code1 = codeUtils.getCode(mail);
        return code.equals(code1);
    }
}
