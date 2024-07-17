package com.platform.controller;

import com.platform.entity.PointsRecordEntity;
import com.platform.service.PointsRecordService;
import com.platform.utils.PageUtils;
import com.platform.utils.Query;
import com.platform.utils.R;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pointsRecord")
public class PointsRecordController {
    @Autowired
    private PointsRecordService pointsRecordService;

    @RequestMapping("/list")
    @RequiresPermissions("pointsRecord:list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);

        List<PointsRecordEntity> pointsRecordEntityList = pointsRecordService.queryList(query);
        int total = pointsRecordService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(pointsRecordEntityList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("pointsRecord:delete")
    public R delete(@RequestBody Integer[] ids) {
        pointsRecordService.deleteBatch(ids);

        return R.ok();
    }
}
