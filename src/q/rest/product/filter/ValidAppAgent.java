package q.rest.product.filter;

import q.rest.product.dao.DAO;
import q.rest.product.model.entity.WebApp;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@ValidApp
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ValidAppAgent implements ContainerRequestFilter {

    @EJB
    private DAO dao;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try{
            String header = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if(header == null || !header.startsWith("Bearer")){
                throw new Exception();
            }
            String[] values = header.split("&&");
            String appSecret = values[2].trim();
            validateSecret(appSecret);
        }catch (Exception ex){
            requestContext.abortWith(Response.status(401).entity("Unauthorized Access").build());
        }
    }


    // retrieves app object from app secret
    private void validateSecret(String secret) throws Exception {
        // verify web app secret
        String sql = "select b from WebApp b where b.appSecret = :value0 and b.active = :value1";
        WebApp webApp = dao.findJPQLParams(WebApp.class, sql, secret, true);
        if (webApp == null) {
            throw new Exception();
        }
    }
}
