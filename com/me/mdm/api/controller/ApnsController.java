package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import com.me.mdm.server.ios.apns.api.ApnsService;
import com.me.mdm.api.model.BaseAPIModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Path;

@Path("apns")
public class ApnsController
{
    @POST
    @Path("/test_connection")
    public Response testConnection(@Context final ContainerRequestContext requestContext) throws Exception {
        final BaseAPIModel model = new BaseAPIModel();
        model.setCustomerUserDetails(requestContext);
        final ApnsService service = new ApnsService();
        service.testConnection(model);
        return Response.status(200).entity((Object)true).build();
    }
}
