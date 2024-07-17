package com.platform.api;
import com.alibaba.fastjson.JSONObject;
import com.platform.entity.PointsRecordVo;
import com.platform.service.ApiPointsRecordService;
import com.platform.util.ApiBaseAction;
import com.platform.util.ApiPageUtils;
import com.platform.utils.Query;
import com.platform.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "用户积分记录-ApiPointsRecordController")
@RestController
@RequestMapping("/api/pointsRecord")
public class ApiPointsRecordController extends ApiBaseAction {
    @Autowired
    private ApiPointsRecordService pointsRecordService;

    @PostMapping("/list")
    public Object list(@RequestBody JSONObject jsonParam) {
        Map params = jsonParam.getInnerMap();
        //校验page，size参数
        try {
            Integer.parseInt(params.get("page").toString());
            Integer.parseInt(params.get("size").toString());
        } catch (Exception e) {
            return R.error("参数错误");
        }
        //添加用户参数
        params.put("userId", getUserId());
        params.put("limit", Integer.parseInt(params.get("size").toString()));
        params.put("offset", (Integer.parseInt(params.get("page").toString()) - 1) * Integer.parseInt(params.get("size").toString()));
        //查询列表数据
        List<PointsRecordVo> pointsRecordEntityList = pointsRecordService.queryList(params);
        ApiPageUtils pageUtil = new ApiPageUtils(pointsRecordEntityList, 0, Integer.parseInt(params.get("size").toString()), Integer.parseInt(params.get("page").toString()));
        return toResponseSuccess(pageUtil);
    }
}
