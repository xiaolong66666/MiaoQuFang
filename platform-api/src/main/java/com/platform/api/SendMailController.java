package com.platform.api;
import com.platform.service.impl.SeedMailServiceImpl;
import com.platform.util.CodeUtils;
import com.platform.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.MessagingException;
@RestController
@RequestMapping("/mail")
public class SendMailController {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private CodeUtils codeUtils;
    //生成验证码
    @GetMapping
    public R sendMessage(String mail) throws MessagingException {
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        seedMailService.setJavaMailSender(javaMailSender);
        seedMailService.setCode(codeUtils.generator(mail));
        seedMailService.setTo(mail);
        seedMailService.seedMessage();
        return R.ok("验证码发送成功!");
    }
}
