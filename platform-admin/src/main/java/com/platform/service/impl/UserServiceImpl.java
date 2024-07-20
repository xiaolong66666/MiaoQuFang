package com.platform.service.impl;

import com.platform.dao.PointsRecordDao;
import com.platform.dao.UserDao;
import com.platform.entity.UserEntity;
import com.platform.service.PointsRecordService;
import com.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-16 15:02:28
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PointsRecordService pointsRecordService;

    @Override
    public UserEntity queryObject(Integer id) {
        return userDao.queryObject(id);
    }

    @Override
    public List<UserEntity> queryList(Map<String, Object> map) {
        return userDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return userDao.queryTotal(map);
    }

    @Override
    public int save(UserEntity user) {
        user.setRegisterTime(new Date());
        user.setPoints(BigDecimal.ZERO);
        return userDao.save(user);
    }

    @Override
    public int update(UserEntity user) {
        return userDao.update(user);
    }

    @Override
    public int delete(Integer id) {
        return userDao.delete(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return userDao.deleteBatch(ids);
    }

    @Override
    @Transactional
    public void setUserPoints(Map<String,Object> params) {
        // todo 发送邮箱
        List<String> ids = (List<String>) params.get("userIds");
        Double s = Double.valueOf(params.get("points").toString());
        for (String id : ids) {
            //保存积分记录
            pointsRecordService.addPintsRecord(Integer.valueOf(id), 1, 1, BigDecimal.valueOf(Double.valueOf(s)));
        }
        userDao.setUserPoints(params);
    }

    @Override
    public int setUserPayouts(Integer[] ids) {
        //记录积分
        for (Integer id : ids) {
            UserEntity userEntity = userDao.queryObject(id);
            BigDecimal points = userEntity.getPoints();
            if (points.compareTo(BigDecimal.ZERO) > 0) {
                pointsRecordService.addPintsRecord(id, 4, 2, points);
            }
        }
        return userDao.setUserPayouts(ids);
    }
}
