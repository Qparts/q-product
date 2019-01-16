package q.rest.product.filter;

import q.rest.product.dao.DAO;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.entity.WebApp;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SecuredCustomer
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecuredCustomerAgent implements ContainerRequestFilter {

    @EJB
    private DAO dao;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try{
            String header = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if(header == null || !header.startsWith("Bearer") || header.startsWith("Bearer null")){
                throw new Exception();
            }
            String[] values = header.split("&&");
            String tokenPart = values[0];
            String token = tokenPart.substring("Bearer".length()).trim();
            String username = values[1].trim();
            String appSecret = values[2].trim();
            String type = values[3].trim();

            matchToken(token, username, appSecret, type, header);
        }
        catch (Exception ex){
            requestContext.abortWith(Response.status(401).entity("Unauthorized Access").build());
        }
    }

    private void matchToken(String token, String username, String appSecret, String type, String header) throws NotAuthorizedException{
        validateSecret(appSecret);
        if(!type.equals("C")){
            throw new NotAuthorizedException("Request authorization failed");
        }
        String link = AppConstants.CUSTOMER_MATCH_TOKEN;
        Map<String,String> map = new HashMap<>();
        map.put("username", username);
        map.put("appSecret", appSecret);
        map.put("token", token);
        Response r = this.postSecuredRequest(link, map, header);
        if(r.getStatus() != 200){
            throw new NotAuthorizedException("Request authorization failed");
        }
    }



    // retrieves app object from app secret
    private void validateSecret(String secret) throws NotAuthorizedException {
        // verify web app secret
        String sql = "select b from WebApp b where b.appSecret = :value0 and b.active = :value1";
        WebApp webApp = dao.findJPQLParams(WebApp.class, sql, secret, true);
        if (webApp == null) {
            throw new NotAuthorizedException("Request authorization failed");
        }
    }

    public <T> Response postSecuredRequest(String link, T t, String authHeader) {
        Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.post(Entity.entity(t, "application/json"));// not secured
        return r;
    }
}
