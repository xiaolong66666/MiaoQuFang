package com.platform.service;

import com.platform.cache.J2CacheUtils;
import com.platform.dao.ApiUserLevelMapper;
import com.platform.dao.ApiUserMapper;
import com.platform.entity.SmsLogVo;
import com.platform.entity.UserInfo;
import com.platform.entity.UserLevelVo;
import com.platform.entity.UserVo;
import com.platform.util.ApiBaseAction;
import com.platform.utils.CharUtil;
import com.platform.utils.RRException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class ApiUserService extends ApiBaseAction {
    @Autowired
    private ApiUserMapper userDao;
    @Autowired
    private ApiUserLevelMapper userLevelDao;

    public UserVo queryObject(Long userId) {
        return userDao.queryObject(userId);
    }

    public UserVo queryByOpenId(String openId) {
        return userDao.queryByOpenId(openId);
    }

    public List<UserVo> queryList(Map<String, Object> map) {
        return userDao.queryList(map);
    }

    public int queryTotal(Map<String, Object> map) {
        return userDao.queryTotal(map);
    }

    public void save(String mobile, String password) {
        UserVo user = new UserVo();
        user.setMobile(mobile);
        user.setUsername(mobile);
        user.setPassword(DigestUtils.sha256Hex(password));
        user.setRegisterTime(new Date());
        userDao.save(user);
    }

    public void save(UserVo userVo) {
        userDao.save(userVo);
    }

    public void update(UserVo user) {
        userDao.update(user);
    }

    public void delete(Long userId) {
        userDao.delete(userId);
    }

    public void deleteBatch(Long[] userIds) {
        userDao.deleteBatch(userIds);
    }

    public UserVo queryByMobile(String mobile) {
        return userDao.queryByMobile(mobile);
    }

    public UserVo queryByUsername(String username) {
        return userDao.queryByUsername(username);
    }

    public UserVo login(String mail, String checkCode) {
        UserVo userVo = queryByUsername(mail);
        //校验验证码
        String code = (String) J2CacheUtils.getCode(mail);
        if (!checkCode.equals(code)) {
            throw new RRException("验证码错误");
        }

        if (null == userVo) {
            //账号不存在,创建账号
            userVo = new UserVo();
            String name = "妙友" + CharUtil.getRandomString(12);
            userVo.setUsername(name);
            userVo.setPassword(DigestUtils.sha256Hex("123456"));
            userVo.setRegisterTime(new Date());
            userVo.setRegisterIp(this.getClientIp());
            userVo.setLastLoginIp(this.getClientIp());
            userVo.setLastLoginTime(new Date());
            userVo.setAvatar("http://112.74.53.128:8080/h5/static/images/logo.png");
            userVo.setNickname(name);
            save(userVo);
        }else {
            //更新登录信息
            userVo.setLastLoginIp(this.getClientIp());
            userVo.setLastLoginTime(new Date());
            update(userVo);
        }

        return userVo;
    }

    public SmsLogVo querySmsCodeByUserId(Long userId) {
        return userDao.querySmsCodeByUserId(userId);
    }


    public int saveSmsCodeLog(SmsLogVo smsLogVo) {
        return userDao.saveSmsCodeLog(smsLogVo);
    }

    public String getUserLevel(UserVo loginUser) {
        String result = "普通用户";
        UserLevelVo userLevelVo = userLevelDao.queryObject(loginUser.getUserLevelId());
        if (null != userLevelVo) {
            result = userLevelVo.getName();
        }
        return result;
    }

    public UserInfo getUserByUserName(String username) {
        return userDao.queryByUserName(username);
    }
}
