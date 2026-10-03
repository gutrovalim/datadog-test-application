package com.example.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class OrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }

    @Bean
    RestClient inventoryClient(RestClient.Builder builder,
            @org.springframework.beans.factory.annotation.Value("${inventory.url}") String url) {
        return builder.baseUrl(url).build();
    }
}
