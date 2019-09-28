package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.HttpClient;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WerXinPayServiceImpl implements WeiXinPayService {
    /**
     * 生成微信支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额
     * @return
     */

    @Value("${appid}")
    private String appid;//公共账号ID

    @Value("${partner}")
    private String partner;//商户号

    @Value("${partnerkey}")
    private String partnerkey;//财付通商户密钥
    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        //1，添加参数
        Map<String, String> map = new HashMap<>();

        map.put("appid",appid);//公共账号ID
        map.put("mch_id",partner);//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//获取随机数
        map.put("body","品优购");//商品描述
        map.put("out_trade_no",out_trade_no);//商品的订单号
        map.put("total_fee",total_fee);//商品的金额
        map.put("spbill_create_ip","127.0.0.1");//ip
        map.put("notify_url ","http://www.itcast.cn");
        map.put("trade_type","NATIVE");//交易类型


        try {
            //2，生成发送的xml

            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println("请求的参数："+xmlParam);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.接收消息
            String content = httpClient.getContent();

            Map<String, String> resoutMap = WXPayUtil.xmlToMap(content);//将xml字符串转换成map

            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("code_url",resoutMap.get("code_url"));//支付地址

            hashMap.put("total_fee",total_fee);//总金额

            hashMap.put("out_trade_no",out_trade_no);//订单号

            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }
}
