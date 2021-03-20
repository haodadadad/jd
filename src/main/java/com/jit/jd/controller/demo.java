package com.jit.jd.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class demo {

    @RequestMapping("hello")
    public String hello(Model model){
        model.addAttribute("name","jit");
        return "hello";
    }
}
