package com.platform.api;
import com.platform.entity.PointsRecordVo;
import com.platform.service.ApiPointsRecordService;
import com.platform.util.ApiBaseAction;
import com.platform.utils.PageUtils;
import com.platform.utils.Query;
import com.platform.utils.R;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "用户积分记录-ApiPointsRecordController")
@RestController
@RequestMapping("/api/pointsRecord")
public class ApiPointsRecordController extends ApiBaseAction {
    @Autowired
    private ApiPointsRecordService pointsRecordService;

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //校验page，size参数
        try {
            Integer.parseInt(params.get("page").toString());
            Integer.parseInt(params.get("size").toString());
        } catch (Exception e) {
            return R.error("参数错误");
        }
        //添加用户参数
        params.put("userId", getUserId());
        //查询列表数据
        Query query = new Query(params);

        List<PointsRecordVo> pointsRecordEntityList = pointsRecordService.queryList(query);
        int total = pointsRecordService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(pointsRecordEntityList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

}
