package q.rest.product.operation;

import com.google.gson.Gson;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.qvm.QvmObject;
import q.rest.product.model.qvm.QvmVendorCredentials;
import q.rest.product.operation.socket.AvailabilitySearchUserServer;
import q.rest.product.operation.socket.AvailabilitySearchVendorServer;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AsyncProductApi {


    @Asynchronous
    public void callVendorAPI(QvmVendorCredentials vendorCredentials, String query, int requesterId, char requesterType, String authHeader){
        String endpoint = vendorCredentials.getEndpointAddress() + query;
        String header = "Bearer " + vendorCredentials.getSecret();
        Response r = getSecuredRequest(endpoint, header);
        int code = r.getStatus();
        Map<String,Object> map = new HashMap<>();
        map.put("vendorId", vendorCredentials.getVendorId());
        if(code == 200){
            List<QvmObject> rs = r.readEntity(new GenericType<List<QvmObject>>() {
            });
            for (QvmObject result : rs) {
                result.setSource('L');
                result.setVendorId(vendorCredentials.getVendorId());
                result.setStatus('C');
            }

            Response r2 = this.putSecuredRequest(AppConstants.PUT_UPDATE_SEARCH_AVAILABILITY_WITH_BRANCHES, rs, authHeader);
            if(r2.getStatus() == 200){
                rs = r2.readEntity(new GenericType<List<QvmObject>>(){});
            }
            map.put("results", rs);
        }
        Gson gson = new Gson();
        String mapString = gson.toJson(map);
        if(requesterType == 'V') {
            AvailabilitySearchVendorServer.sendToVendorUser(mapString, requesterId);
        }else if(requesterType == 'U'){
            AvailabilitySearchUserServer.sendToUser(mapString, requesterId);
        }
    }



    public <T> Response putSecuredRequest(String link, T t, String authHeader) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, authHeader);
        Response r = b.put(Entity.entity(t, "application/json"));
        return r;
    }



    public Response getSecuredRequest(String link, String header) {
        Invocation.Builder b = ClientBuilder.newClient().target(link).request();
        b.header(HttpHeaders.AUTHORIZATION, header);
        Response r = b.get();
        return r;
    }
}
