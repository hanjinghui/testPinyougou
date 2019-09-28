package com.pinyougou.solrutil;


import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    public void importItemData(){

        TbItemExample example =new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();//创建查询条件的对象
        criteria.andStatusEqualTo("1");//查询已经审核的数据
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        System.out.println("商品列表");
        for (TbItem tbItem : tbItems) {

            Map specMap= JSON.parseObject(tbItem.getSpec());//将 spec 字段中的 json 字符串转换为 map
            tbItem.setSpecMap(specMap);//给带注解的字段赋值

            System.out.println(tbItem.getTitle());
        }
        System.out.println("商品列表结束");

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    //创建测试类
    public static void main(String[] args) {

        ApplicationContext context=new
                ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
