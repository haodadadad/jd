package com.jit.jd.service;

import com.jit.jd.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.vo.GoodsVo;

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
}
