package com.platform.api;
import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.cache.J2CacheUtils;
import com.platform.service.impl.SeedMailServiceImpl;
import com.platform.util.CodeUtils;
import com.platform.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
@RestController
@RequestMapping("/api/mail")
public class SendMailController {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private CodeUtils codeUtils;
    //生成验证码
    @IgnoreAuth
    @PostMapping
    public R sendMessage(@RequestBody JSONObject jsonObject) throws MessagingException {
        String mail = jsonObject.getString("mail");
        //通过mail查找缓存中的验证码
        Object code = J2CacheUtils.getCode(mail);
        if (code != null) {
            return R.error("验证码已发送，请查看邮箱!");
        }
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        seedMailService.setJavaMailSender(javaMailSender);
        seedMailService.setCode(codeUtils.generator(mail));
        seedMailService.setTo(mail);
        seedMailService.seedMessage();
        return R.ok("验证码发送成功!");
    }
}
