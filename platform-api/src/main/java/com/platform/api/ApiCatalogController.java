package com.platform.api;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.entity.CategoryVo;
import com.platform.service.ApiCategoryService;
import com.platform.util.ApiBaseAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: ApiIndexController
 */
@Api(tags = "商品分类-ApiCatalogController")
@RestController
@RequestMapping("/api/catalog")
public class ApiCatalogController extends ApiBaseAction {
    @Autowired
    private ApiCategoryService categoryService;

    /**
     * 获取分类栏目数据
     */
    @ApiOperation(value = "获取分类栏目数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", paramType = "query", required = false),
            @ApiImplicitParam(name = "page", value = "page", paramType = "query", required = false),
            @ApiImplicitParam(name = "size", value = "size", paramType = "query", required = false)})
    @IgnoreAuth
    @PostMapping(value = "index")
    public Object index(@RequestBody JSONObject jsonParam) {
        Integer id = jsonParam.getInteger("id");
        Integer page = jsonParam.getInteger("page");
        Integer size = jsonParam.getInteger("size");
        if (null == id) {
            id = 0;
        }
        if (null == page) {
            page = 1;
        }
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("limit", size);
        params.put("sidx", "sort_order");
        params.put("order", "asc");
        params.put("parentId", 0);
        //查询列表数据
        List<CategoryVo> data = categoryService.queryList(params);
        //
        CategoryVo currentCategory = null;
        if (null != id) {
            currentCategory = categoryService.queryObject(id);
        }
        if (null == currentCategory && null != data && data.size() != 0) {
            currentCategory = data.get(0);
        } else {
            currentCategory = new CategoryVo();
        }

        //获取子分类数据
        if (null != currentCategory && null != currentCategory.getId()) {
            params.put("parentId", currentCategory.getId());
            currentCategory.setSubCategoryList(categoryService.queryList(params));
        }

        resultObj.put("categoryList", data);
        resultObj.put("currentCategory", currentCategory);
        return toResponseSuccess(resultObj);
    }

    /**
     *
     */
    @ApiOperation(value = "分类目录当前分类数据接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", paramType = "query", required = false)})
    @IgnoreAuth
    @PostMapping(value = "current")
    public Object current(@RequestBody JSONObject jsonParam) {
        Integer id = jsonParam.getInteger("id");
        Map<String, Object> resultObj = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", 0);
        CategoryVo currentCategory = null;
        if (null != id) {
            currentCategory = categoryService.queryObject(id);
        }
        //获取子分类数据
        if (null != currentCategory && null != currentCategory.getId()) {
            params.put("parentId", currentCategory.getId());
            currentCategory.setSubCategoryList(categoryService.queryList(params));
        }
        resultObj.put("currentCategory", currentCategory);
        return toResponseSuccess(resultObj);
    }
}
