package com.jit.jd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jit.jd.mapper.GoodsMapper;
import com.jit.jd.mapper.SeckillGoodsMapper;
import com.jit.jd.pojo.Goods;
import com.jit.jd.pojo.SeckillGoods;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    //获取商品列表
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    //获取商品详情
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(GoodsVo goods) {
        return goodsMapper.update(goods);
    }

    @Override
    public String addGoods(String goodsName, String goodsTitle, String goodsDetail, Integer goodsStock, BigDecimal goodsPrice) {
        Goods goods = new Goods();


        goods.setGoodsName(goodsName);
        goods.setGoodsTitle(goodsTitle);
        goods.setGoodsDetail(goodsDetail);
        goods.setGoodsStock(goodsStock);
        goods.setGoodsPrice(goodsPrice);
        goodsMapper.insert(goods);


        SeckillGoods seckillGoods=new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
        seckillGoods.setSeckillPrice(BigDecimal.valueOf(0.01));
        seckillGoods.setStockCount(1);
        seckillGoods.setStartDate(new Date());
        seckillGoods.setEndDate(new Date());

        seckillGoodsMapper.insert(seckillGoods);

        return "redirect:/goods/toAdminGoodsList";
    }

    @Override
    public Goods adminModifyGoods(Long goodsId) {


        return null;
    }

    @Override
    public String modifyGoods(Long id, String goodsName, String goodsTitle, String goodsDetail, Integer goodsStock, BigDecimal goodsPrice) {
        Goods goods = goodsMapper.selectById(id);
        goods.setGoodsName(goodsName);
        goods.setGoodsTitle(goodsTitle);
        goods.setGoodsDetail(goodsDetail);
        goods.setGoodsStock(goodsStock);
        goods.setGoodsPrice(goodsPrice);

        goodsMapper.updateById(goods);
        return "redirect:/goods/toAdminGoodsList";

    }

    @Override
    public String delGoods(Long goodsId) {
        int res = goodsMapper.deleteById(goodsId);
        if (res == 1) {
            return "redirect:/goods/toAdminGoodsList";
        } else {

        }
        return null;
    }

    @Override
    public int updateImgGoods(Long goodsId, String path, String fileName) {

        System.out.print("开始插入=id==" + goodsId + "\n");
        System.out.print("开始插入=path==" + path + "\n");
        System.out.print("开始插入=fileName==" + fileName + "\n");
        Goods goods = new Goods();
        goods = goodsMapper.selectById(goodsId);
        goods.setGoodsImg("/img/"+fileName);
        int jieguo = goodsMapper.updateById(goods);
        System.out.print("插入结果===" + jieguo + "\n");
        return jieguo;


    }


}
