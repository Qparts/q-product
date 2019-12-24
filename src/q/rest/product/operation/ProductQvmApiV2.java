package q.rest.product.operation;


import q.rest.product.filter.SecuredVendor;
import q.rest.product.model.qvm.QvmSearchRequest;
import q.rest.product.model.qvm.QvmSearchResult;
import q.rest.product.model.qvm.QvmVendorCredentials;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/qvm/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductQvmApiV2 {


    @SecuredVendor
    @POST
    @Path("search")
    public Response search(QvmSearchRequest sr) {
        try {
            List<QvmSearchResult> results = new ArrayList<>();
            for (QvmVendorCredentials cred : sr.getVendorCreds()) {
                String endpoint = cred.getEndpointAddress() + "search/" + sr.getQuery();
                String header = "Bearer " + cred.getSecret();
                Response r = getSecuredRequest(endpoint, header);
                if (r.getStatus() == 200) {
                    List<QvmSearchResult> rs = r.readEntity(new GenericType<List<QvmSearchResult>>() {
                    });
                    for (QvmSearchResult result : rs) {
                        result.setVendorId(cred.getVendorId());
                    }
                    results.addAll(rs);
                }
            }
            return Response.status(200).entity(results).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }



    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }
}
