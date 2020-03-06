package com.websocket.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2020/1/9
 * Create By 肖章明
 */
@Component
@ServerEndpoint("/imserver/{userId}")
@Slf4j
public class WebSocketServer {
    private static int onlineCount = 0;

    private static ConcurrentHashMap<String,WebSocketServer> websocketMap = new ConcurrentHashMap<>();

    private Session session;

    private String userId="";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
        this.session = session;
        this.userId = userId;
        if(websocketMap.containsKey(userId)){
            websocketMap.remove(userId);
            websocketMap.put(userId,this);
        }else{
            websocketMap.put(userId,this);
            addOnlineCount();
        }
        log.info("用户连接："+userId+",当前连接人数为："+getOnlineCount());

        try{
            sendMessage("连接成功");
        }catch (IOException e){
            log.error("用户:"+userId+",网络异常!!!!!!");
        }
    }

    @OnClose
    public void onClose(){
        if(websocketMap.containsKey(userId)){
            websocketMap.remove(userId);
            subOnlineCount();
        }
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message,Session session){
        log.info("用户消息:"+userId+",报文:"+message);

        if(StringUtils.isNotBlank(message)){
            try {
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");

                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && websocketMap.containsKey(toUserId)) {
                    websocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                }else{
                    log.error("请求的userId:"+toUserId+"不在该服务器上");
                }
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    @OnError
    public void onError(Session session,Throwable error){
        log.error("用户错误："+this.userId+",原因"+error.getMessage());
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException{
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(StringUtils.isNotBlank(userId)&&websocketMap.containsKey(userId)){
            websocketMap.get(userId).sendMessage(message);
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }





    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
