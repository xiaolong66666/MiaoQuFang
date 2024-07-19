package com.platform.dao;

import com.platform.entity.FootprintVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApiFootprintMapper extends BaseDao<FootprintVo> {
    int deleteByParam(Map<String, Object> map);

    List<FootprintVo> shareList(Map<String, Object> map);

	List<FootprintVo> queryListFootprint(@Param("userId") String userId,@Param("offset") Integer offset, @Param("limit") Integer limit);
}
