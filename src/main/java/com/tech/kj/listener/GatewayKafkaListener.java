package com.tech.kj.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GatewayKafkaListener {
    Logger log = LoggerFactory.getLogger(GatewayKafkaListener.class);
    @KafkaListener(topics = "${kafka.topicName.gateway}", containerGroup = "gatewayKafkaGroup")
    public void listenGatewayTopic(String message){
        log.info("message received {}",message);
    }
}
