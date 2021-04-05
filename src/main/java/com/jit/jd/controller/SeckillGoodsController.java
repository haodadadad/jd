package com.jit.jd.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jit.jd.pojo.Goods;
import com.jit.jd.pojo.SeckillGoods;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.service.ISeckillGoodsService;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
@Controller
@RequestMapping("/seckillGoods")
public class SeckillGoodsController  {

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("/update")
    @ResponseBody
    public RespBean update(@RequestBody GoodsVo goods){
        try {
            goodsService.update(goods);
        }catch(Exception e){
            e.printStackTrace();
        }
        return RespBean.success();
    }

}
