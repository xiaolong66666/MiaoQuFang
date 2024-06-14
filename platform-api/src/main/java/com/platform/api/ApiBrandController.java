package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.entity.BrandVo;
import com.platform.service.ApiBrandService;
import com.platform.util.ApiBaseAction;
import com.platform.util.ApiPageUtils;
import com.platform.utils.Query;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: ApiIndexController
 */
@Api(tags = "品牌制造商-ApiBrandController")
@RestController
@RequestMapping("/api/brand")
public class ApiBrandController extends ApiBaseAction {
    @Autowired
    private ApiBrandService brandService;

    /**
     * 分页获取品牌
     */
    @ApiOperation(value = "分页获取品牌")
    @IgnoreAuth
    @PostMapping("list")
    public Object list(@RequestBody JSONObject jsonParam) {
        Integer page = jsonParam.getInteger("page");
        Integer size = jsonParam.getInteger("size");
        //初始化分页参数
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        //查询列表数据
        Map<String, Object> params = new HashMap<>();
        params.put("fields", "id, name, floor_price, app_list_pic_url");
        params.put("page", page);
        params.put("limit", size);
        params.put("sidx", "id");
        params.put("order", "asc");

        Query query = new Query(params);
        List<BrandVo> brandEntityList = brandService.queryList(query);
        int total = brandService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(brandEntityList, total, query.getLimit(), query.getPage());
        //
        return toResponseSuccess(pageUtil);
    }

    /**
     * 品牌详情
     */
    @ApiOperation(value = "品牌详情")
    @IgnoreAuth
    @PostMapping("detail")
    public Object detail(@RequestBody JSONObject jsonParam) {
        //判断id是否为空
        if (jsonParam.getInteger("id") == null) {
            return toResponseFail("参数错误");
        }
        Map<String, Object> resultObj = new HashMap<>();
        //查询列表数据
        BrandVo entity = brandService.queryObject(jsonParam.getInteger("id"));
        resultObj.put("brand", entity);
        return toResponseSuccess(resultObj);
    }
}
