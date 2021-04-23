package com.jit.jd.service;

import com.jit.jd.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.vo.GoodsVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
public interface IGoodsService extends IService<Goods> {
//获取商品列表
    List<GoodsVo> findGoodsVo();
//获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);

    int update(GoodsVo goods);

    String addGoods(String goodsName, String goodsTitle, String goodsDetail, Integer goodsStock, BigDecimal goodsPrice);

    Goods adminModifyGoods(Long goodsId);

    String modifyGoods(Long id, String goodsName, String goodsTitle, String goodsDetail, Integer goodsStock, BigDecimal goodsPrice);

    String delGoods(Long goodsId);

    int updateImgGoods(Long goodsId,String path,String fileName);
}
