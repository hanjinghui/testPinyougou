package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name.do")
    @ResponseBody
    public Map loginName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap<>();
        map.put("loginName", name);
        return map;
    }

}
