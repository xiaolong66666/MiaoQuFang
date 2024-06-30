package com.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.platform.dao.GoodsSpecificationDao;
import com.platform.entity.GoodsSpecificationEntity;
import com.platform.service.GoodsSpecificationService;

/**
 * 商品对应规格表值表Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-31 11:15:55
 */
@Service("goodsSpecificationService")
public class GoodsSpecificationServiceImpl implements GoodsSpecificationService {
    @Autowired
    private GoodsSpecificationDao goodsSpecificationDao;

    @Override
    public GoodsSpecificationEntity queryObject(Integer id) {
        return goodsSpecificationDao.queryObject(id);
    }

    @Override
    public List<GoodsSpecificationEntity> queryList(Map<String, Object> map) {
        List<Integer> ids = goodsSpecificationDao.querySpecificationIdByGoods(Integer.parseInt(map.get("goodsId").toString()));
        //获取当前产品规格id(更新产品时)
        Integer cur_id = null;
        try {
            cur_id = Integer.parseInt(map.get("curSpecificationId").toString());
        }catch (Exception e) {
        }
        Integer finalCur_id = cur_id;
        return goodsSpecificationDao
                .queryList(map)
                .stream()
                .filter(goodsSpecificationEntity -> {
                    if (ids.contains(goodsSpecificationEntity.getId())) {
                        if (finalCur_id != null && finalCur_id == goodsSpecificationEntity.getId()) {
                            return true;
                        }
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return goodsSpecificationDao.queryTotal(map);
    }

    @Override
    public int save(GoodsSpecificationEntity goodsSpecification) {
        goodsSpecification.setSpecificationId(1);
        return goodsSpecificationDao.save(goodsSpecification);
    }

    @Override
    public int update(GoodsSpecificationEntity goodsSpecification) {
        return goodsSpecificationDao.update(goodsSpecification);
    }

    @Override
    public int delete(Integer id) {
        return goodsSpecificationDao.delete(id);
    }

    @Override
    public int deleteBatch(Integer[]ids) {
        return goodsSpecificationDao.deleteBatch(ids);
    }
}
