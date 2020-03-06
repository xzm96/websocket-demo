package com.websocket.websocketdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.websocket")
public class WebsocketdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketdemoApplication.class, args);
    }

}
