package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听收到的消息。。");
        try {
            //将监听获取到的数据转换成textMessage类型
            TextMessage testMessage = (TextMessage)message;

            //获取消息中的数据
            String text = testMessage.getText();

            //将数据转换成需要的对象类型
            List<TbItem> list = JSON.parseArray(text, TbItem.class);
            
            //遍历集合
            for (TbItem tbItem : list) {
                System.out.println(tbItem.getId()+" "+tbItem.getTitle());

                Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);

                tbItem.setSpecMap(specMap);
            }

            itemSearchService.importList(list);
            System.out.println("导入成功。。。。");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
