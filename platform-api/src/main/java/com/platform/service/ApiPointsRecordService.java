package com.platform.service;

import com.platform.dao.ApiPointsRecordMapper;
import com.platform.entity.PointsRecordVo;
import com.platform.utils.Query;
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

    public int addPintsRecord(Integer userId, Integer source, Integer type, BigDecimal points) {
        PointsRecordVo pointsRecordEntity = new PointsRecordVo();
        //封装参数
        pointsRecordEntity.setUserId(userId);
        pointsRecordEntity.setSource(source);
        pointsRecordEntity.setType(type);
        pointsRecordEntity.setPoints(points);
        pointsRecordEntity.setCreateTime(new Date());
        //查询用户总积分
        BigDecimal totalPoints = userService.queryObject(Long.valueOf(userId)).getPoints();
        //判断是增加还是减少
        if (type == 1) {
            totalPoints = totalPoints.add(points);
        } else {
            totalPoints = totalPoints.subtract(points);
        }
        pointsRecordEntity.setTotalPoints(totalPoints);
        return pointsRecordDao.save(pointsRecordEntity);
    }

    public List<PointsRecordVo> queryList(Map<String, Object> params) {
        return pointsRecordDao.queryList(params);
    }

    public int queryTotal(Query query) {
        return pointsRecordDao.queryTotal(query);
    }
}
