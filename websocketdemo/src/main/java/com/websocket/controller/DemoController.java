package com.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * 2020/1/9
 * Create By 肖章明
 */
@RestController
public class DemoController {

    @GetMapping("/index")
    public ResponseEntity<String> index(){
        return ResponseEntity.ok("success");
    }

    @GetMapping("/page")
    public ModelAndView page(){
        System.out.println("page");
        return new ModelAndView("websocket");
    }

    @RequestMapping("/push/{toUserId}")
    public ResponseEntity<String> pushToWeb(String message, @PathVariable String toUserId) throws IOException {
        WebSocketServer.sendInfo(message,toUserId);
        return ResponseEntity.ok("send success");
    }
}
