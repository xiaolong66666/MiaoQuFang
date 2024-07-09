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

import java.math.BigDecimal;
import java.util.UUID;

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

    /**
     * 获取填写邀请码
     */
    @ApiOperation(value = "获取填写邀请码")
    @PostMapping("getUserCode")
    public Object getUserCode(@LoginUser UserVo loginUser) {
        UserVo userVo = userService.getUserCode(loginUser.getUserId());
        return this.toResponseSuccess(userVo);
    }

    /**
     * 填写邀请码
     */
    @ApiOperation(value = "填写邀请码")
    @PostMapping("setUserCode")
    public Object setUserCode(@LoginUser UserVo loginUser) {
        JSONObject jsonParams = getJsonRequest();
        String userCode = jsonParams.getString("code");
        if (StringUtils.isNullOrEmpty(userCode)) {
            return this.toResponseFail("邀请码不能为空");
        }
        //校验邀请码是否存在
        UserVo userCodeVo = userService.queryByUserCode(userCode);
        if (StringUtils.isNullOrEmpty(userCodeVo)) {
            return this.toResponseFail("邀请码不存在");
        }
        //邀请码存在，进行下一步操作
        UserVo userVo = userService.queryObject(loginUser.getUserId());
        userVo.setUsedCode(userCode);
        //填写邀请码后，用户积分增加5
        userVo.setPoints(userVo.getPoints().add(new BigDecimal(5)));
        userService.update(userVo);
        return this.toResponseSuccess("填写邀请码成功");
    }

    /**
     * 生成邀请码
     */
    @ApiOperation(value = "生成邀请码")
    @PostMapping("createCode")
    public Object createCode(@LoginUser UserVo loginUser) {
        UserVo userVo = userService.queryObject(loginUser.getUserId());
        if (!StringUtils.isNullOrEmpty(userVo.getCode())) {
            return this.toResponseFail("邀请码已生成");
        }
        String userCode = UUID.randomUUID().toString().replace("-", "");
        userVo.setCode(userCode);
        userService.update(userVo);
        return this.toResponseSuccess(userCode);
    }

}
