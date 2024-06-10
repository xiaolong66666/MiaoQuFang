package com.platform.util;
import com.platform.cache.J2CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class CodeUtils {
    public String generator(String mail){
        int i = mail.hashCode();
        int encryption= 20010905;
        //进行异或运算
        long result=i^encryption;
        long nowTime=System.currentTimeMillis();
        result =result^nowTime;
        result=result > 0 ?result:-result;
        String last=Long.toString(result);
        String substring = last.substring(last.length() - 6);
        //将验证码存入缓存
        J2CacheUtils.putCode(mail,substring);
        log.info("向客户{}发送验证码：{}",mail,substring);
        return substring;
    }
    public String getCode(String mail){
        return (String) J2CacheUtils.getCode(mail);
    }
}
