package com.changgou.search.listener;

import com.changgou.search.config.RabbitMQConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodDelListener {
    @Autowired
    private ESManagerService esManagerService;

    @RabbitListener(queues = RabbitMQConfig.SEARCH_DEL_QUEUE)
    public void receiveMessage(String message) {
        System.out.println("商品下架队列监听到的消息：" + message);

        // 删除es索引库数据
        esManagerService.delDataBySpuId(message);
    }
}
