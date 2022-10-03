package com.me.ems.framework.common.api.v1.controller;

import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.HeaderParam;
import com.me.ems.framework.common.api.v1.service.ResourceBrowserService;
import javax.ws.rs.Path;

@Path("/resource-tree")
public class ResourceBrowserController
{
    private ResourceBrowserService resourceBrowserService;
    @HeaderParam("X-Customer")
    private String customerIdStr;
    @Context
    private SecurityContext securityContext;
    
    public ResourceBrowserController() {
        this.resourceBrowserService = new ResourceBrowserService();
    }
    
    @GET
    @Produces({ "application/resourceTypes.v1+json" })
    public Map getResourceBrowserInfo() throws APIException {
        return this.resourceBrowserService.getResourceTrees();
    }
    
    @GET
    @CustomerSegmented
    @Path("/{treeID : \\d+}/identities")
    @Produces({ "application/resourceTypeDetails.v1+json" })
    public Response getResourceBrowserInfo(@PathParam("treeID") final Long treeID, @Context final UriInfo uriInfo) throws APIException {
        final MultivaluedMap<String, String> requestParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
        final User user = (User)this.securityContext.getUserPrincipal();
        final Map userParams = new HashMap();
        userParams.put("userID", user.getUserID().toString());
        userParams.put("loginID", user.getLoginID().toString());
        userParams.put("customerID", (this.customerIdStr == null || this.customerIdStr.equalsIgnoreCase("all")) ? null : this.customerIdStr);
        userParams.put("treeID", treeID.toString());
        final Map responseMap = this.resourceBrowserService.getResourceInfoDetails(treeID, userParams, requestParams);
        final int count = responseMap.get("count");
        responseMap.remove("count");
        Response.ResponseBuilder responseBuilder = Response.noContent().header("X-Total-Count", (Object)0);
        if (count != 0) {
            responseBuilder = Response.ok().header("X-Total-Count", (Object)count);
        }
        return responseBuilder.entity((Object)responseMap).build();
    }
}
