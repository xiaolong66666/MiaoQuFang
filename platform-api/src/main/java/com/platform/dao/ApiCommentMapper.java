package com.platform.dao;

import com.platform.entity.CommentVo;

import java.util.Map;

public interface ApiCommentMapper extends BaseDao<CommentVo> {
    int queryhasPicTotal(Map<String, Object> map);
}
