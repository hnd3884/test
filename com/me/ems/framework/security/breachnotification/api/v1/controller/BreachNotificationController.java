package com.me.ems.framework.security.breachnotification.api.v1.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.security.breachnotification.api.v1.service.BreachNotificationService;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Map;
import javax.ws.rs.Path;

@Path("security")
public class BreachNotificationController
{
    @POST
    @Path("breachNotification")
    @Consumes({ "application/breachNotificationData.v1+json" })
    @Produces({ "application/breachNotificationDetails.v1+json" })
    public Map setNotificationDetails(final Map notificationData, @Context final ContainerRequestContext requestContext) throws APIException {
        return BreachNotificationService.getInstance().setNotificationDetails(notificationData);
    }
    
    @GET
    @Path("breachNotification")
    @Produces({ "application/breachNotificationDetails.v1+json" })
    public Map getNotificationDetails() throws APIException {
        return BreachNotificationService.getInstance().getNotificationDetails();
    }
}
