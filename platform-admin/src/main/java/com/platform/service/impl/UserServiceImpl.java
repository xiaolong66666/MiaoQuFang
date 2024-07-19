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
        SeedMailServiceImpl seedMailService = new SeedMailServiceImpl();
        String title = "【妙趣坊】系统发放积分通知!";
        List<String> ids = (List<String>) params.get("userIds");
        for (String id : ids) {
            UserEntity userEntity = userDao.queryObject(id);
            //发送邮件
            Double s = Double.valueOf(params.get("points").toString());
            BigDecimal points = BigDecimal.valueOf(Double.valueOf(s));
            BigDecimal totalPoints = userEntity.getPoints().add(points);
            String content = "您的积分发生变动，已获得积分：" + points + "，剩余积分：" + totalPoints + "，请注意查收！(http://miaoqufang.cn)";
            seedMailService.seedMessage(title, userEntity.getUsername(), content);
            //保存积分记录
            pointsRecordService.addPintsRecord(Integer.valueOf(id), 1, 1, BigDecimal.valueOf(Double.valueOf(s)));
        }
        userDao.setUserPoints(params);
    }
}
