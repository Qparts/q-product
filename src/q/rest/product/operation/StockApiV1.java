package q.rest.product.operation;

import q.rest.product.dao.DAO;
import q.rest.product.dao.DaoApi;
import q.rest.product.filter.annotation.SubscriberJwt;
import q.rest.product.helper.AppConstants;
import q.rest.product.helper.Helper;
import q.rest.product.model.qstock.*;
import q.rest.product.model.qstock.views.StockPurchaseSummary;
import q.rest.product.model.qstock.views.StockSalesSummary;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/api/v4/stock/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockApiV1 {

    @EJB
    private DAO dao;

    @EJB
    private AsyncProductApi async;
    @EJB
    private DaoApi daoApi;

    private Helper helper = new Helper();

}
