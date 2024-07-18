package com.platform.service;

import com.platform.dao.ApiPointsRecordMapper;
import com.platform.entity.PointsRecordVo;
import com.platform.entity.UserVo;
import com.platform.service.impl.SeedMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ApiPointsRecordService {
    @Autowired
    private ApiPointsRecordMapper pointsRecordDao;
    @Autowired
    private ApiUserService userService;

    public int addPintsRecord(Long userId, Integer source, Integer type, BigDecimal points) {
        PointsRecordVo pointsRecordEntity = new PointsRecordVo();
        //封装参数
        pointsRecordEntity.setUserId(userId);
        pointsRecordEntity.setSource(source);
        pointsRecordEntity.setType(type);
        pointsRecordEntity.setPoints(points);
        pointsRecordEntity.setCreateTime(new Date());
        //查询用户总积分
        UserVo userVo = userService.queryObject(userId);
        BigDecimal totalPoints = userVo.getPoints();
        //判断是增加还是减少
        if (type == 1) {
            totalPoints = totalPoints.add(points);
        } else {
            totalPoints = totalPoints.subtract(points);
        }
        pointsRecordEntity.setTotalPoints(totalPoints);
        //发送邮件通知用户
        //发送邮件
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        String title = "【妙趣坊】积分变动通知!";
        String change_from = (source == 1 ? "系统" : (source == 2 ? "邀请" : "购物"));
        String point_change_str = (type == 1 ? "增加" : "减少")+points+"积分";
        //指定日期格式 yyyy-MM-dd HH:mm:ss
        String date_Str = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //变化后的总积分（固定信息时间格式）
        String content = "尊敬的用户"+userVo.getUsername()+"您好，您的积分账户于"+date_Str+"发生了一笔"+change_from+point_change_str+"，当前总积分为"+totalPoints+"。";
        seedMailService.seedMessage(title, userVo.getUsername(), content);
        return pointsRecordDao.save(pointsRecordEntity);
    }

    public List<PointsRecordVo> queryList(Map<String, Object> params) {
        return pointsRecordDao.queryList(params);
    }

}
