package q.rest.product.filter.jwt;

import q.rest.product.filter.annotation.SubscriberJwt;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@SubscriberJwt
@Priority(Priorities.AUTHENTICATION)
public class JwtFilterSubscriber extends JwtFilter {

    @Override
    public void validateType(Object type) throws Exception{
        if(!type.toString().equals("S")){
            throw new Exception();
        }
    }

}
