package com.platform.service.impl;

import com.platform.dao.PointsRecordDao;
import com.platform.dao.UserDao;
import com.platform.entity.PointsRecordEntity;
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
        BigDecimal totalPoints = userDao.queryObject(userId).getPoints();
        //判断是增加还是减少
        if (type == 1) {
            totalPoints = totalPoints.add(points);
        } else {
            totalPoints = totalPoints.subtract(points);
        }
        pointsRecordEntity.setTotalPoints(totalPoints);
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
