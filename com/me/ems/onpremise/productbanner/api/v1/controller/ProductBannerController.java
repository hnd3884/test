package com.me.ems.onpremise.productbanner.api.v1.controller;

import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.onpremise.productbanner.api.v1.service.ProductBannerService;
import javax.ws.rs.Path;

@Path("banner")
public class ProductBannerController
{
    private final ProductBannerService service;
    
    public ProductBannerController() {
        this.service = new ProductBannerService();
    }
    
    @GET
    @Produces({ "application/bannerContent.v1+json" })
    public Map<String, Object> getBannerContent(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return this.service.getProductBannerData(user);
    }
    
    @PUT
    public Response updateBannerStatus(@Context final SecurityContext securityContext, @QueryParam("templateID") final String bannerID, @QueryParam("action") final String action) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        if (bannerID == null) {
            throw new APIException("IAM0003");
        }
        this.service.updateBannerStatus(bannerID, action, user.getLoginID());
        return Response.ok().build();
    }
    
    @PUT
    @Path("count")
    public Response addOrIncrementClickCountForReviewPage(@QueryParam("templateID") final String bannerID) throws APIException {
        if (bannerID == null) {
            throw new APIException("IAM0003");
        }
        this.service.addOrIncrementClickCountForReviewPage(bannerID);
        return Response.ok().build();
    }
}
