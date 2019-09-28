package com.pinyougou.search.service.impl;

import com.pinyougou.search.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;


public class itemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {


        try {

            System.out.println("获得消息");
            //获得消息
            ObjectMessage objectMessage = (ObjectMessage)message;

            //获得消息中的数据
            Long[] goodsIds =(Long[]) objectMessage.getObject();

            //实现删除
            itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));

            //删除成功
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
