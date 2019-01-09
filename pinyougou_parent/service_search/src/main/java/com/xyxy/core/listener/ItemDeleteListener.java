package com.xyxy.core.listener;

import com.xyxy.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemDeleteListener implements MessageListener {

    @Autowired
    private SolrManagerService solrManagerService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;

        try {
            String goodsId = atm.getText();
            solrManagerService.deleteItemFromSolr(Long.parseLong(goodsId));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
