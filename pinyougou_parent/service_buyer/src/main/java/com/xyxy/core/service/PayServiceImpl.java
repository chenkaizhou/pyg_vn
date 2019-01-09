package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import com.xyxy.core.util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    //这些属性都是支付要使用的，配置文件在common中
    //微信公众账号或开放平台APP的唯一标识
    @Value("${appid}")
    private String appid;

    //财付通平台的商户账号
    @Value("${partner}")
    private String partner;

    //财付通平台的商户密钥
    @Value("${partnerkey}")
    private String partnerkey;

    //回调地址
    //@Value("${notifyurl}")
    //private String notifyurl;


    @Override
    //创建支付连接  返回到前端生成支付二维码
    public Map createNative(String outTradeNo, String totalFee) {
        //1.创建参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", outTradeNo);//商户订单号
        param.put("total_fee",totalFee);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://www.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型

        try {
            //2.生成要发送的xml，，调用微信sdk的api接口将封装的map数据自动转换成xml格式字符串
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3.获取结果
            String result = client.getContent();
            //调用微信sdk的api接口将xml格式字符串自动转换成Java对象
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", totalFee);//总金额
            map.put("out_trade_no",outTradeNo);//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    //查询支付状  是否支付成功
    public Map queryPayStatus(String out_trade_no) {
        Map param = new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
