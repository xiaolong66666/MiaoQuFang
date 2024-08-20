package com.platform.service.impl;

import com.platform.dao.GoodsSpecificationDao;
import com.platform.entity.GoodsSpecificationEntity;
import com.platform.utils.BeanUtils;
import com.platform.utils.Query;
import com.platform.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.platform.dao.ProductDao;
import com.platform.entity.ProductEntity;
import com.platform.service.ProductService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service实现类
 *
 * @author lipengjun
 * @email 939961241@qq.com
 * @date 2017-08-30 14:31:21
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private GoodsSpecificationDao goodsSpecificationDao;

    @Override
    public ProductEntity queryObject(Integer id) {
        return productDao.queryObject(id);
    }

    @Override
    public List<ProductEntity> queryList(Map<String, Object> map) {
        List<ProductEntity> list = productDao.queryList(map);

        List<ProductEntity> result = new ArrayList<>();
        //翻译产品规格
        if (null != list && list.size() > 0) {
            for (ProductEntity item : list) {
                Integer specificationIds = item.getGoodsSpecificationIds();
                GoodsSpecificationEntity entity = goodsSpecificationDao.queryObject(specificationIds);
                if (null != entity){
                    item.setSpecificationValue(entity.getValue());
                }
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return productDao.queryTotal(map);
    }

    @Override
    @Transactional
    public int save(ProductEntity product) {
        Integer goodsId = product.getGoodsId();
        //校验商品id是否为空
        if (null == goodsId) {
            return 0;
        }
        BigDecimal marketPrice = product.getMarketPrice();
        if (null == marketPrice) {
            product.setMarketPrice(new BigDecimal(99999));
        }
        ProductEntity entity = new ProductEntity();
        BeanUtils.copyProperties(product, entity);
        return productDao.save(entity);
    }

    @Override
    public int update(ProductEntity product) {
        return productDao.update(product);
    }

    @Override
    public int delete(Integer id) {
        return productDao.delete(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return productDao.deleteBatch(ids);
    }

    @Override
    public List<ProductEntity> queryProfitsList(Query query) {
        return productDao.queryProfitsList(query);
    }

    @Override
    public int queryProfitsTotal(Query query) {
        return productDao.queryProfitsTotal(query);
    }
}
