package com.platform.dao;

import com.platform.entity.ProductEntity;
import com.platform.utils.Query;

import java.util.List;

/**
 * Dao
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-30 14:31:21
 */
public interface ProductDao extends BaseDao<ProductEntity> {

    List<ProductEntity> queryProfitsList(Query query);

    int queryProfitsTotal(Query query);
}
