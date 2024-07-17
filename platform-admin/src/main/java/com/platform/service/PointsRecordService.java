package com.platform.service;

import com.platform.entity.PointsRecordEntity;
import com.platform.utils.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PointsRecordService {
    int addPintsRecord(Integer userId, Integer source, Integer type,BigDecimal points);

    List<PointsRecordEntity> queryList(Query query);

    int queryTotal(Query query);

    int deleteBatch(Integer[] ids);
}
