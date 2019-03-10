package q.rest.product.operation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v2/beacon")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BeaconAPI {



    @Path("test")
    @POST
    public void getBestSellers(Object object){
        try{
            System.out.println(object.toString());
        }catch (Exception ex){

        }
    }

}
