package q.rest.product.operation.socket;

import q.rest.product.dao.DAO;
import q.rest.product.helper.AppConstants;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/search/user/{userId}/token/{token}")
@Stateless
public class AvailabilitySearchUserServer {

    @Inject
    private DAO dao;

    private Session session;
    private long userId;
    private String token;

    private static Set<AvailabilitySearchUserServer> endpoints = new CopyOnWriteArraySet<>();

    @OnMessage
    public String onMessage(String message) {
        return message;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        endpoints.remove(this);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId, @PathParam("token") String token) throws IOException {
        this.session = session;
        this.userId = userId;
        this.token = token;
        if(this.tokenMatched()) {
            endpoints.add(this);
        }
        else {
            session.close();
        }
    }

    public static void sendToUser(Object message, long userId) {
        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                if (endpoint.session.isOpen()) {
                    if(endpoint.userId == userId) {
                        endpoint.session.getAsyncRemote().sendText(message.toString());
                    }
                }
            }
        });
    }


    private boolean tokenMatched(){
        String link = AppConstants.USER_MATCH_TOKEN_WS;
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("token", token);
        Response r = this.postSecuredRequest(link, map);
        if(r.getStatus() != 200){
            return false;
        }
        return true;
    }


    public <T> Response postSecuredRequest(String link, T t) {
        Response r = ClientBuilder.newClient().target(link).request().post(Entity.entity(t, "application/json"));// not secured
        return r;
    }
}
