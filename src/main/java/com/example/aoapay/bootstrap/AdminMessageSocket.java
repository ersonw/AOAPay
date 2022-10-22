package com.example.aoapay.bootstrap;

import com.example.aoapay.dao.AuthDao;
import com.example.aoapay.table.User;
import com.example.aoapay.util.AESUtils;
import com.example.aoapay.util.TimeUtil;
import com.example.aoapay.util.Utils;
import com.example.aoapay.util.WebSocketUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/admin/message/{token}")
@Component
@Getter
@Slf4j
public class AdminMessageSocket {
    @Autowired
    private AuthDao authDao;
    private static AdminMessageSocket self;
    public static List<AdminMessageSocket> webSockets = new CopyOnWriteArrayList<AdminMessageSocket>();

    private InetSocketAddress remoteAddress;
    private final Timer timer = new Timer();
    private Session session;
    private User user;

    @PostConstruct
    public void init() {
        log.info("Message WebSocket 加载");
        self = this;
    }
    public void deleteUser(User user){
        for (AdminMessageSocket socket: webSockets) {
            if (socket.user != null && socket.user.getId() == user.getId()){
                try {
                    socket.session.close(new CloseReason( CloseReason.CloseCodes.CANNOT_ACCEPT, "other login"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.user = user;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token")String token) {
        User user = self.authDao.findUserByToken(token);
        if (user == null){
            try {
                session.close(new CloseReason( CloseReason.CloseCodes.NO_EXTENSION, "user not login"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        log.error("[{}] {} 登录消息系统", TimeUtil.getNowDate(),user.getUsername());
        deleteUser(user);
        remoteAddress = WebSocketUtil.getRemoteAddress(session);
        this.session = session;
        webSockets.add(this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage("H");
            }
        }, 1000, 1000 * 15);
    }
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        timer.cancel();
    }
    @OnMessage
    public void onMessage(String m, Session session) {
        System.out.printf(m);
        System.out.printf(AESUtils.Decrypt(m));
    }
    @OnError
    public void onError(Session session, Throwable error) {
//        error.printStackTrace();
    }
    public void sendMessage(String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(AESUtils.Encrypt(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
