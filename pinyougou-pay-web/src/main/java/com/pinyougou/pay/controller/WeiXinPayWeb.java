package com.pinyougou.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.IdWorker;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 生成二维码
 */
@RestController
@RequestMapping("/pay")
public class WeiXinPayWeb {

    @Reference(timeout = 5000)
    private WeiXinPayService weiXinPayService;

    @RequestMapping("/createNative.do")
    public Map createNative(){

        IdWorker idWorker = new IdWorker();

        //使用雪花算法创建的一个id，订单的价格写成1
        Map map = weiXinPayService.createNative(idWorker.nextId()+"", "1");
        return map;
    }

}
