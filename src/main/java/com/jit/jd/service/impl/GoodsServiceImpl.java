package com.jit.jd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jit.jd.mapper.GoodsMapper;
import com.jit.jd.pojo.Goods;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
//获取商品列表
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    //获取商品详情
    public GoodsVo findGoodsVoByGoodsId(Long goodsId){
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(GoodsVo goods) {
        return goodsMapper.update(goods);
    }
}
