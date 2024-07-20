package com.platform.api;
import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.cache.J2CacheUtils;
import com.platform.service.impl.SeedMailServiceImpl;
import com.platform.util.CodeUtils;
import com.platform.util.ThreadPoolUtils;
import com.platform.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
@RestController
@RequestMapping("/api/mail")
public class SendMailController {
    @Autowired
    private CodeUtils codeUtils;
    //生成验证码
    @IgnoreAuth
    @PostMapping("code")
    public R sendLoginCode(@RequestBody JSONObject jsonObject){
        String mail = jsonObject.getString("mail");
        //通过mail查找缓存中的验证码
        Object code = J2CacheUtils.getCode(mail);
        if (code != null) {
            return R.error("验证码已发送，请查看邮箱!");
        }
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        String generator = codeUtils.generator(mail);
        if (generator == null || "".equals(generator)) {
            return R.error("验证码生成失败!请重试!");
        }
        String title = "妙趣坊商城验证码:"+generator;
        seedMailService.seedMessage(title,mail,"欢迎"+mail+"来到妙趣坊商城！验证码将在1分钟后失效! 请尽快使用!");
        return R.ok("验证码发送成功!");
    }

    //发送自定义邮件
    @PostMapping("message")
    public R sendMessage(@RequestBody JSONObject jsonObject){
        String mail = jsonObject.getString("mail");
        String context = jsonObject.getString("context");
        String title = jsonObject.getString("title");
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        seedMailService.seedMessage(title,mail,context);
        return R.ok("邮件发送成功!");
    }
}
