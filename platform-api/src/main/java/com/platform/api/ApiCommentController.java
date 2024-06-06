package com.platform.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.IgnoreAuth;
import com.platform.annotation.LoginUser;
import com.platform.entity.*;
import com.platform.service.*;
import com.platform.util.ApiBaseAction;
import com.platform.util.ApiPageUtils;
import com.platform.utils.Base64Util;
import com.platform.utils.CharUtil;
import com.platform.utils.Query;
import com.platform.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 08:32<br>
 * @gitee https://gitee.com/fuyang_lipengjun/platform
 * 描述: ApiIndexController <br>
 */
@Api(tags = "会员评论-ApiCommentController")
@RestController
@RequestMapping("/api/comment")
public class ApiCommentController extends ApiBaseAction {
    @Autowired
    private ApiCommentService commentService;
    @Autowired
    private ApiUserService userService;
    @Autowired
    private ApiCommentPictureService commentPictureService;
    @Autowired
    private ApiCouponService apiCouponService;
    @Autowired
    private ApiUserCouponService apiUserCouponService;

    /**
     * 发表评论
     */
    @ApiOperation(value = "发表评论")
    @PostMapping("post")
    public Object post(@LoginUser UserVo loginUser) {
        Map<String, Object> resultObj = new HashMap<>();
        //
        JSONObject jsonParam = getJsonRequest();
        Integer typeId = jsonParam.getInteger("typeId");
        Integer valueId = jsonParam.getInteger("valueId");
        String content = jsonParam.getString("content");
        JSONArray imagesList = jsonParam.getJSONArray("imagesList");
        CommentVo commentEntity = new CommentVo();
        commentEntity.setTypeId(typeId);
        commentEntity.setValueId(valueId);
        commentEntity.setContent(content);
        commentEntity.setStatus(0);
        //
        commentEntity.setAddTime(System.currentTimeMillis() / 1000);
        commentEntity.setUserId(loginUser.getUserId());
        commentEntity.setContent(Base64Util.encode(commentEntity.getContent()));
        Integer insertId = commentService.save(commentEntity);
        //
        if (insertId > 0 && null != imagesList && imagesList.size() > 0) {
            int i = 0;
            for (Object imgLink : imagesList) {
                i++;
                CommentPictureVo pictureVo = new CommentPictureVo();
                pictureVo.setCommentId(insertId);
                pictureVo.setPicUrl(imgLink.toString());
                pictureVo.setSortOrder(i);
                commentPictureService.save(pictureVo);
            }
        }
        // 是否领取优惠券
        if (insertId > 0 && typeId == 0) {
            // 当前评价的次数
            Map<String, Object> param = new HashMap<>();
            param.put("valueId", valueId);
            List<CommentVo> commentVos = commentService.queryList(param);
            boolean hasComment = false;
            for (CommentVo commentVo : commentVos) {
                if (commentVo.getUserId().equals(loginUser.getUserId())
                        && !commentVo.getId().equals(insertId)) {
                    hasComment = true;
                }
            }
            if (!hasComment) {
                Map<String, Object> couponParam = new HashMap<>();
                couponParam.put("sendType", 6);
                CouponVo newCouponConfig = apiCouponService.queryMaxUserEnableCoupon(couponParam);
                if (null != newCouponConfig
                        && newCouponConfig.getMinTransmitNum() >= commentVos.size()) {
                    UserCouponVo userCouponVo = new UserCouponVo();
                    userCouponVo.setAddTime(new Date());
                    userCouponVo.setCouponId(newCouponConfig.getId());
                    userCouponVo.setCouponNumber(CharUtil.getRandomString(12));
                    userCouponVo.setUserId(loginUser.getUserId());
                    apiUserCouponService.save(userCouponVo);
                    resultObj.put("coupon", newCouponConfig);
                }
            }
        }
        if (insertId > 0) {
            return toResponsObject(0, "评论添加成功", resultObj);
        } else {
            return toResponseFail("评论保存失败");
        }
    }

    /**
     */
    @ApiOperation(value = "评论数量")
    @PostMapping("count")
    public Object count(Integer typeId, Integer valueId) {
        Map<String, Object> resultObj = new HashMap<>();
        //
        Map<String, Object> param = new HashMap<>();
        param.put("typeId", typeId);
        param.put("valueId", valueId);
        Integer allCount = commentService.queryTotal(param);
        Integer hasPicCount = commentService.queryhasPicTotal(param);
        //
        resultObj.put("allCount", allCount);
        resultObj.put("hasPicCount", hasPicCount);
        return toResponseSuccess(resultObj);
    }

    /**
     * @param typeId
     * @param valueId
     * @param showType 选择评论的类型 0 全部， 1 只显示图片
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "选择评论类型")
    @IgnoreAuth
    @PostMapping("list")
    public Object list(Integer typeId, Integer valueId, Integer showType,
                       @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                       String sort, String order) {
        Map<String, Object> param = new HashMap<>();
        param.put("typeId", typeId);
        param.put("valueId", valueId);
        param.put("page", page);
        param.put("limit", size);
        if (StringUtils.isNullOrEmpty(sort)) {
            param.put("order", "desc");
        } else {
            param.put("order", sort);
        }
        if (StringUtils.isNullOrEmpty(order)) {
            param.put("sidx", "id");
        } else {
            param.put("sidx", order);
        }
        if (null != showType && showType == 1) {
            param.put("hasPic", 1);
        }
        //查询列表数据
        Query query = new Query(param);
        List<CommentVo> commentList = commentService.queryList(query);
        int total = commentService.queryTotal(query);
        ApiPageUtils pageUtil = new ApiPageUtils(commentList, total, query.getLimit(), query.getPage());
        //
        for (CommentVo commentItem : commentList) {
            commentItem.setContent(Base64Util.decode(commentItem.getContent()));
            commentItem.setUserInfo(userService.queryObject(commentItem.getUserId()));

            Map<String, Object> paramPicture = new HashMap<>();
            paramPicture.put("commentId", commentItem.getId());
            List<CommentPictureVo> commentPictureEntities = commentPictureService.queryList(paramPicture);
            commentItem.setPicList(commentPictureEntities);
        }
        return toResponseSuccess(pageUtil);
    }
}
