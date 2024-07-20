package com.platform.service.impl;

import com.platform.dao.PointsRecordDao;
import com.platform.dao.UserDao;
import com.platform.entity.PointsRecordEntity;
import com.platform.entity.UserEntity;
import com.platform.service.PointsRecordService;
import com.platform.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("PointsRecordService")
public class PointsRecordServiceImpl implements PointsRecordService {
    @Autowired
    private PointsRecordDao  pointsRecordDao;
    @Autowired
    private UserDao userDao;

    @Override
    public int addPintsRecord(Integer userId, Integer source, Integer type, BigDecimal points) {
        PointsRecordEntity pointsRecordEntity = new PointsRecordEntity();
        //封装参数
        pointsRecordEntity.setUserId(userId);
        pointsRecordEntity.setSource(source);
        pointsRecordEntity.setType(type);
        pointsRecordEntity.setPoints(points);
        pointsRecordEntity.setCreateTime(new Date());
        //查询用户总积分
        UserEntity userEntity = userDao.queryObject(userId);
        BigDecimal totalPoints = userEntity.getPoints();
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
        //来源 1:系统 2:邀请 3:购物 4:提现
        String change_from = "";
        switch (source) {
            case 1:
                change_from = "系统";
                break;
            case 2:
                change_from = "邀请";
                break;
            case 3:
                change_from = "购物";
                break;
            case 4:
                change_from = "提现";
                break;
            default:
                change_from = "未知";
                break;
        }
        String point_change_str = (type == 1 ? "增加" : "减少")+points+"积分";
        //指定日期格式 yyyy-MM-dd HH:mm:ss
        String date_Str = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //变化后的总积分（固定信息时间格式）
        String content = "尊敬的用户"+userEntity.getUsername()+"您好，您的积分账户于"+date_Str+"发生了一笔"+change_from+point_change_str+"，当前总积分为"+totalPoints+"。"+ "，请注意查收！(妙趣坊:http://miaoqufang.cn)";
        seedMailService.seedMessage(title, userEntity.getUsername(), content);
        return pointsRecordDao.save(pointsRecordEntity);
    }

    @Override
    public List<PointsRecordEntity> queryList(Query query) {
        return pointsRecordDao.queryList(query);
    }

    @Override
    public int queryTotal(Query query) {
        return pointsRecordDao.queryTotal(query);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return pointsRecordDao.deleteBatch(ids);
    }
}
