package com.tech.kj.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.kj.config.cache.service.GatewayCache;
import com.tech.kj.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

@Component
public class GatewayKafkaListener {
    private static  final  Logger log = LoggerFactory.getLogger(GatewayKafkaListener.class);
    private final JwtTokenProvider tokenProvider;
    private final GatewayCache gatewayCache;
    public GatewayKafkaListener(JwtTokenProvider tokenProvider,GatewayCache gatewayCache){
        this.tokenProvider=tokenProvider;
        this.gatewayCache=gatewayCache;
    }
    @KafkaListener(topics = "${kafka.topicName.gateway}", containerGroup = "gatewayKafkaGroup")
    public void listenGatewayTopic(String message) throws JsonProcessingException {

        log.info("message received {}",message);
        ObjectMapper jsonMapper=new ObjectMapper();
        Map<String, Object> map = jsonMapper.readValue(message, Map.class);
        String action=(String) map.get("action");
        switch (action){
            case "markExpireJWT":
                String token=(String)   map.get("jwtToken");
                Claims claims = tokenProvider.getAllClaimsFromToken(token);
                //long expireTime =  (long) claims.get("exp") * 1000; // Convert seconds to milliseconds
                Integer expValue = (Integer) claims.get("exp");
                long expireTime = expValue.longValue() * 1000L;

                //long issuedAt = (long) claims.get("iat") * 1000; // Convert seconds to milliseconds
                Integer issuedAtValue = (Integer) claims.get("iat");
                long issuedTime = issuedAtValue.longValue() * 1000L;
                // Calculate remaining time duration
                Instant now = Instant.now();
                Instant expirationInstant = Instant.ofEpochMilli(expireTime);
                Duration remainingDuration = Duration.between(now, expirationInstant);
                gatewayCache.put(token,null,remainingDuration.getSeconds());

                break;
            default:
                log.info("default case executed");
                break;
        }
    }
}
