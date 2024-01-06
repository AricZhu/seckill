package com.kelin.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收消息: " + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg) {
        log.info("Queue01 接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg) {
        log.info("Queue02 接收消息：" + msg);
    }
}
