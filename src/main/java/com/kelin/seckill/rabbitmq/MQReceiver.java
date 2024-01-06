package com.kelin.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg) {
        log.info("queue_direct01 接收消息: " + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receiver04(Object msg) {
        log.info("queue_direct02 接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void receiver05(Object msg) {
        log.info("queue_topic01 接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receiver06(Object msg) {
        log.info("queue_topic02 接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_header01")
    public void receiver07(Message message) {
        log.info("queue01接收Message 对象: " + message);
        log.info("queueu01接收消息：" + new String(message.getBody()));
    }

    @RabbitListener(queues = "queue_header02")
    public void receiver08(Message message) {
        log.info("queue02接收Message 对象: " + message);
        log.info("queueu02接收消息：" + new String(message.getBody()));
    }
}
