package com.platform.dao;

import com.platform.entity.CartVo;
import org.apache.ibatis.annotations.Param;

public interface ApiCartMapper extends BaseDao<CartVo> {
    void updateCheck(@Param("productIds") String[] productIds,
                     @Param("isChecked") Integer isChecked, @Param("userId") Long userId);

    void deleteByProductIds(@Param("productIds") String[] productIds);

    void deleteByUserAndProductIds(@Param("userId") Long userId,@Param("productIds") String[] productIds);

    void deleteByCart(@Param("userId") Long userId, @Param("sessionId") Integer sessionId, @Param("checked") Integer checked);
}
