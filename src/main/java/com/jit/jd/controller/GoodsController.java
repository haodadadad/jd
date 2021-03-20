package com.jit.jd.controller;

import com.jit.jd.pojo.User;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.service.IUserService;
import com.jit.jd.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    //跳转到商品页面
    @RequestMapping("/toList")
    public String toList( Model model,User user) {
//        if (StringUtils.isEmpty(ticket)) {
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//       User user= userService.getUserByCookie(ticket,request,response);
//
//        if (null == user) {
//            return "login";
//        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        return "goodsList";
    }
            //跳转商品详情页
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId){
        model.addAttribute("user",user);
        GoodsVo goodsVo=goodsService.findGoodsVoByGoodsId(goodsId);
        //抢购时间判断
        Date startDate=goodsVo.getStartDate();
        Date endDate=goodsVo.getEndDate();
        Date nowDate=new Date();
        //秒杀状态
        int secKillStatus=0;
        //秒杀倒计时
        int remainSeconds=0;
        //秒杀还未开始
        if (nowDate.before(startDate)){
            remainSeconds=((int)((startDate.getTime()-nowDate.getTime())/1000));

        }else if (nowDate.after(endDate)){
            //秒杀已结束
            secKillStatus=2;
            remainSeconds=-1;
        }else {
            secKillStatus=1;
            remainSeconds=0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);
        return "goodsDetail";
    }
}
