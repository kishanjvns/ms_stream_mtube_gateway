package com.tech.kj.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
        return routeLocatorBuilder.routes()
                .route(route->
                        route.path("/users/api/v1/**")
                        .uri("lb://USER-SERVICE"))
                .route(route->
                        route.path("/stream/api/v1/**")
                                .uri("lb://video-service"))
                .route(route->
                        route.path("/review/api/v1/**")
                                .uri("lb://review-service"))
                .build();
    }
}
