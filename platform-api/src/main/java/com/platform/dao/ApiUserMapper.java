package com.platform.dao;

import com.platform.entity.SmsLogVo;
import com.platform.entity.UserInfo;
import com.platform.entity.UserVo;
import org.apache.ibatis.annotations.Param;

/**
 * 用户
 */
public interface ApiUserMapper extends BaseDao<UserVo> {

    UserVo queryByMobile(String mobile);
    UserVo queryByUsername(String username);

    UserVo queryByOpenId(@Param("openId") String openId);

    /**
     * 获取用户最后一条短信
     *
     * @param userId
     * @return
     */
    SmsLogVo querySmsCodeByUserId(@Param("userId") Long userId);

    /**
     * 保存短信
     *
     * @param smsLogVo
     * @return
     */
    int saveSmsCodeLog(SmsLogVo smsLogVo);

    UserInfo queryByUserName(String username);
}
