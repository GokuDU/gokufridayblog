package com.guo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public final static String ES_QUEUE = "es_queue";
    public final static String ES_EXCHANGE = "es_exchange";
    public final static String ES_ROUTING_KEY = "es_routingKey";

    @Bean
    public Queue esQuene() {
        return new Queue(ES_QUEUE);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(ES_EXCHANGE);
    }

    // 通过 routingKey 绑定 交换机( DirectExchange ) 和 队列( Queue )
    // 当生产者发送消息到 交换机，并且指定和 这个队列（esQueue）绑定的 routingKey 时，消息就会存到队列
    // 当消费者 监听 到有消息在这个队列（esQueue）中，就能够消费到对应的消息
    @Bean
    public Binding binding(Queue esQueue,DirectExchange exchange) {
        return BindingBuilder.bind(esQueue).to(exchange).with(ES_ROUTING_KEY);
    }

}
