package q.rest.product.operation;

import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.model.tecdoc.article.ArticleResponse;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v3/replacements/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TechDocApi {

    @EJB
    private AsyncProductApi async;

    @GET
    @Path("search/{query}")
    @SubscriberJwt
    public Response searchReplacement(@HeaderParam(HttpHeaders.AUTHORIZATION) String header, @PathParam(value = "query") String query){
        Map<String, Object> map = new HashMap<>();
        map.put("articleCountry", "SA");
        map.put("lang", "en");
        map.put("provider", "22423");
        map.put("searchType", 10);
        map.put("searchQuery", query);
        map.put("perPage", 100);
        map.put("page", 1);
        map.put("includeAll", true);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("getArticles", map);
        Response r = this.postSecuredRequest(requestMap);
        if(r.getStatus() == 200){
            ArticleResponse ar = r.readEntity(ArticleResponse.class);
            boolean found = !ar.getArticles().isEmpty();
            async.saveReplacementSearch(header, query, found);
            for(var art : ar.getArticles()){
                for(var img : art.getImages()){
                    img.replaceImages();
                }
            }
            return Response.ok().entity(ar).build();
        }
        return Response.status(404).build();
    }


    public <T> Response postSecuredRequest(T t) {
        Invocation.Builder b = ClientBuilder.newClient().target(AppConstants.TECH_DOC_API_LINK).request();
        b.header("X-Api-Key", AppConstants.TECH_DOC_API_KEY);
        Response r = b.post(Entity.entity(t, "application/json"));
        return r;
    }


}
