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
        if (!checkCode(mail,substring)) {
            return null;
        }
        return substring;
    }
    //检查是否存入缓存，重试三次发送
    public boolean checkCode(String mail,String str){
        int i = 0;
        while (i < 3) {
            Object code = J2CacheUtils.getCode(mail);
            if (code != null) {
                return true;
            }
            J2CacheUtils.putCode(mail,str);
            i++;
        }
        return false;
    }
    public String getCode(String mail){
        return (String) J2CacheUtils.getCode(mail);
    }
}
