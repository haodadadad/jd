package com.jit.jd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jit.jd.pojo.Goods;
import com.jit.jd.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
public interface GoodsMapper extends BaseMapper<Goods> {
//获取商品列表
    List<GoodsVo> findGoodsVo();
//获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
    //修改秒杀详情
}
