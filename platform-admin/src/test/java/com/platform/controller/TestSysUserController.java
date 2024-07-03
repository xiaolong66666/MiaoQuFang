package com.platform.controller;

import com.platform.BaseSpringTestCase;
import com.platform.dao.GoodsGalleryDao;
import com.platform.entity.GoodsEntity;
import com.platform.entity.GoodsSpecificationEntity;
import com.platform.entity.ProductEntity;
import com.platform.entity.SysUserEntity;
import com.platform.service.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 会员测试
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2018-07-09 10:13:43
 */
public class TestSysUserController extends BaseSpringTestCase {
    @Autowired
    TestSysUserService testSysUserService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ProductService productService;
    @Autowired
    private GoodsSpecificationService goodsSpecificationService;
    @Autowired
    private GoodsGalleryDao goodsGalleryDao;

    /**
     * 使用测试类
     */
    @Test
    public void queryTestSysUserList() {
        Map<String, Object> params = new HashMap<>();
        List<SysUserEntity> list = testSysUserService.queryList(params);
        if (list != null && list.size() != 0) {
            for (SysUserEntity userEntity : list) {
                logger.info("username：" + userEntity.getUsername() + "；mobile：" + userEntity.getMobile());
            }
        }
    }

    /**
     * 使用项目中的service
     */
    @Test
    public void querySysUserList() {
        Map<String, Object> params = new HashMap<>();
        List<SysUserEntity> list = sysUserService.queryList(params);
        if (list != null && list.size() != 0) {
            for (SysUserEntity userEntity : list) {
                logger.info("username：" + userEntity.getUsername() + "；mobile：" + userEntity.getMobile());
            }
        }
    }
    @Test
    public void uploadPic() throws Exception {
        String prefix = "<p><img class=\"fr-fin\" data-fr-image-preview=\"false\" alt=\"Image title\" src=\"";
        String endfix = "\"></p>";
        while (true) {
            System.out.println("开始上传图片***********************************");
            //请输入商品ID
            System.out.print("请输入商品ID:");
            int goodId = new Scanner(System.in).nextInt();
            //请输入图片展示图
            System.out.print("请输入图片展示图:");
            String list_pic_url = new Scanner(System.in).nextLine();
            //输入商品轮番图
            String goods_gallery = "";
            while (true) {
                System.out.print("请输入商品轮番图:");
                String goods_gallery_image = new Scanner(System.in).nextLine();
                if (goods_gallery_image.equals("exit")) {
                    break;
                }
                //保存图片
                Map<String,Object> map = new HashMap<>();
                map.put("goods_id",goodId);
                map.put("img_url",goods_gallery_image);
                goodsGalleryDao.save(map);
            }
            //请输入商品详情图,输入完成退出循环
            String goods_desc = "";
            while (true) {
                System.out.print("请输入商品详情图:");
                String goods_detail_image = new Scanner(System.in).nextLine();
                if (goods_desc.equals("exit")) {
                    break;
                }
                //拼接图片
                goods_desc += prefix + goods_detail_image + endfix;
            }
            //更新商品信息
            GoodsEntity goodsEntity = new GoodsEntity();
            goodsEntity.setId(goodId);
            goodsEntity.setListPicUrl(list_pic_url);
            goodsEntity.setGoodsDesc(goods_desc);
            goodsService.update(goodsEntity);
            System.out.println("更新商品信息成功!");

            //创建规格信息
            System.out.println("开始创建产品信息***********************************");
            while (true){
                System.out.print("请输入规格名称:");
                String specification_name = new Scanner(System.in).nextLine();
                System.out.println("请输入规格图片:");
                String specification_image = new Scanner(System.in).nextLine();
                if (specification_name.equals("exit")) {
                    break;
                }
                GoodsSpecificationEntity goodsSpecificationEntity = new GoodsSpecificationEntity();
                goodsSpecificationEntity.setGoodsId(goodId);
                goodsSpecificationEntity.setValue(specification_name);
                goodsSpecificationEntity.setPicUrl(specification_image);
                goodsSpecificationService.save(goodsSpecificationEntity);
                System.out.println("创建规格信息成功!");
                //获取规格ID
                int specification_id = goodsSpecificationEntity.getId();
                //创建产品信息
                System.out.print("请输入产品价格:");
                ProductEntity productEntity = new ProductEntity();
                productEntity.setGoodsId(goodId);
                productEntity.setGoodsSpecificationIds(specification_id);
                productEntity.setGoodsSn("N-"+goodId);
                productEntity.setGoodsNumber(99999999);
                System.out.println("请输入零售价:");
                productEntity.setRetailPrice(new Scanner(System.in).nextBigDecimal());
                productEntity.setMarketPrice(BigDecimal.valueOf(9999));
                System.out.println("请输入成本价:");
                productEntity.setCostPrice(new Scanner(System.in).nextBigDecimal());
                productService.save(productEntity);
                System.out.println("创建产品信息成功!");
            }
        }

    }
}
