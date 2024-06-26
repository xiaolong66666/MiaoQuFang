package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.cache.J2CacheUtils;
import com.platform.entity.SmsConfig;
import com.platform.entity.SmsLogVo;
import com.platform.entity.UserVo;
import com.platform.service.ApiUserService;
import com.platform.service.SysConfigService;
import com.platform.util.ApiBaseAction;
import com.platform.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述: ApiIndexController
 */
@Api(tags = "会员操作-ApiUserController")
@RestController
@RequestMapping("/api/user")
public class ApiUserController extends ApiBaseAction {
    @Autowired
    private ApiUserService userService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 发送短信
     */
    @ApiOperation(value = "发送短信")
    @PostMapping("smscode")
    public Object smscode(@LoginUser UserVo loginUser) {
        JSONObject jsonParams = getJsonRequest();
        String phone = jsonParams.getString("phone");
        // 一分钟之内不能重复发送短信
        SmsLogVo smsLogVo = userService.querySmsCodeByUserId(loginUser.getUserId());
        if (null != smsLogVo && (System.currentTimeMillis() / 1000 - smsLogVo.getLogDate()) < 1 * 60) {
            return toResponseFail("短信已发送");
        }
        //生成验证码
        String smsCode = CharUtil.getRandomNum(4);
        //获取云存储配置信息
        SmsConfig config = sysConfigService.getConfigObject(Constant.SMS_CONFIG_KEY, SmsConfig.class);
        if (StringUtils.isNullOrEmpty(config)) {
            return toResponseFail("请先配置短信平台信息");
        }
        if (StringUtils.isNullOrEmpty(config.getAppid())) {
            return toResponseFail("请先配置短信平台APPID");
        }
        if (StringUtils.isNullOrEmpty(config.getAppkey())) {
            return toResponseFail("请先配置短信平台KEY");
        }
        if (StringUtils.isNullOrEmpty(config.getSign())) {
            return toResponseFail("请先配置短信平台签名");
        }
        // 发送短信
        SmsSingleSenderResult result;
        int templateId = 23;
        try {
            result = SmsUtil.crSendSms(config.getAppid(), config.getAppkey(), phone, templateId, new String[]{smsCode}, "");
        } catch (Exception e) {
            return toResponseFail("短信发送失败");
        }

        if (result.result == 0) {
            smsLogVo = new SmsLogVo();
            smsLogVo.setLogDate(System.currentTimeMillis() / 1000);
            smsLogVo.setUserId(loginUser.getUserId());
            smsLogVo.setPhone(phone);
            smsLogVo.setSmsCode(templateId);
            smsLogVo.setSmsText(smsCode);
            userService.saveSmsCodeLog(smsLogVo);
            return this.toResponseSuccess("短信发送成功");
        } else {
            return toResponseFail("短信发送失败");
        }
    }

    /**
     * 获取当前会员等级
     *
     * @param loginUser
     * @return
     */
    @ApiOperation(value = "获取当前会员等级")
    @PostMapping("getUserLevel")
    public Object getUserLevel(@LoginUser UserVo loginUser) {
        String userLevel = userService.getUserLevel(loginUser);
        return this.toResponseSuccess(userLevel);
    }

    /**
     * 绑定手机
     */
    @ApiOperation(value = "绑定手机")
    @PostMapping("bindMobile")
    public Object bindMobile(@LoginUser UserVo loginUser) {
        JSONObject jsonParams = getJsonRequest();
        SmsLogVo smsLogVo = userService.querySmsCodeByUserId(loginUser.getUserId());

        String mobileCode = jsonParams.getString("mobileCode");
        String mobile = jsonParams.getString("mobile");

        if (!mobileCode.equals(smsLogVo.getSmsText())) {
            return toResponseFail("验证码错误");
        }
        UserVo userVo = userService.queryObject(loginUser.getUserId());
        userVo.setMobile(mobile);
        userService.update(userVo);
        return this.toResponseSuccess("手机绑定成功");
    }

    /**
     *获取用户积分
     */
    @IgnoreAuth
    @ApiOperation(value = "获取用户积分")
    @PostMapping("points")
    public Object getUserPoints(@LoginUser UserVo loginUser) {
        //如果用户不存在，返回数据0
        Long userId = getUserId();
        if (null == userId) {
            return this.toResponseSuccess(0);
        }
        UserVo userVo = userService.queryObject(userId);
        return this.toResponseSuccess(userVo.getPoints());
    }

    //更换邮箱账号
//    @ApiOperation(value = "更换邮箱账号")
//    @PostMapping("bindEmail")
//    public Object bindEmail(@LoginUser UserVo loginUser, @RequestBody JSONObject jsonObject) {
//        Long userId = loginUser.getUserId();
//        if (null == userId) {
//            return toResponseFail("请先登录");
//        }
//        //
//        String mail = jsonObject.getString("username");
//        String checkCode = jsonObject.getString("checkCode");
//        //校验验证码
//        String code = (String) J2CacheUtils.getCode(mail);
//        if (!checkCode.equals(code)) {
//            throw new RRException("验证码错误");
//        }
//        //更新用户信息
//        UserVo userVo = new UserVo();
//        userVo.setUserId(userId);
//        userVo.setUsername(mail);
//        userService.update(userVo);
//        return this.toResponseSuccess("更新成功！");
//    }
}
