package com.xyxy.core.service;

import java.util.Map;

public interface PayService {
    public Map createNative(String outTradeNo, String totalFee);
    public Map queryPayStatus(String out_trade_no);
}
