package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.LoginUser;
import com.platform.entity.CollectVo;
import com.platform.entity.UserVo;
import com.platform.service.ApiCollectService;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: ApiIndexController
 */
@Api(tags = "会员收藏-ApiCollectController")
@RestController
@RequestMapping("/api/collect")
public class ApiCollectController extends ApiBaseAction {
    @Autowired
    private ApiCollectService collectService;

    /**
     * 获取用户收藏
     */
    @ApiOperation(value = "获取用户收藏")
    @PostMapping("list")
    public Object list(@LoginUser UserVo loginUser, @RequestBody JSONObject jsonParam) {
        //获取分页参数
        Integer page = jsonParam.getInteger("page");
        Integer size = jsonParam.getInteger("size");
        //校验参数，并初始化
        if (null == page || page <= 0) {
            page = 1;
        }
        if (null == size || size <= 0) {
            size = 8;
        }
        Integer typeId = jsonParam.getInteger("typeId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginUser.getUserId());
        param.put("typeId", typeId);
        //添加分页参数
        param.put("limit", size);
        param.put("offset", (page - 1) * size);
        List<CollectVo> collectEntities = collectService.queryList(param);
        return toResponseSuccess(collectEntities);
    }

    /**
     * 获取用户收藏
     */
    @ApiOperation(value = "添加取消收藏")
    @PostMapping("addordelete")
    public Object addordelete(@LoginUser UserVo loginUser) {
        JSONObject jsonParam = getJsonRequest();
        Integer typeId = jsonParam.getInteger("typeId");
        Integer valueId = jsonParam.getInteger("valueId");

        Map<String, Object> param = new HashMap<>();
        param.put("userId", loginUser.getUserId());
        param.put("typeId", typeId);
        param.put("valueId", valueId);
        List<CollectVo> collectEntities = collectService.queryList(param);
        //
        Integer collectRes = null;
        String handleType = "add";
        if (null == collectEntities || collectEntities.size() < 1) {
            CollectVo collectEntity = new CollectVo();
            collectEntity.setAddTime(System.currentTimeMillis() / 1000);
            collectEntity.setTypeId(typeId);
            collectEntity.setValueId(valueId);
            collectEntity.setIsAttention(0);
            collectEntity.setUserId(loginUser.getUserId());
            //添加收藏
            collectRes = collectService.save(collectEntity);
        } else {
            //取消收藏
            collectRes = collectService.delete(collectEntities.get(0).getId());
            handleType = "delete";
        }

        if (collectRes > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("type", handleType);
            return toResponseSuccess(data);
        }
        return toResponseFail("操作失败");
    }
}
