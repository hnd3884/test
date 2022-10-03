package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.model.BaseAPIModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.api.service.AppService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("apps")
public class AppsController extends BaseController
{
    private Logger logger;
    private AppService appService;
    
    public AppsController() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.appService = new AppService();
    }
    
    @POST
    @Path("/{app_id}/move")
    public Response moveProfileToAllCustomers(@Context final ContainerRequestContext requestContext, @PathParam("app_id") final Long appId) throws APIHTTPException {
        try {
            final BaseAPIModel apiModel = new BaseAPIModel();
            apiModel.setCustomerUserDetails(requestContext);
            final Long loginId = apiModel.getLogInId();
            if (!DMUserHandler.isUserInAdminRole(loginId)) {
                this.logger.log(Level.INFO, "User not in admin role to move profiles to all customers");
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            this.appService.moveAppToAllCustomers(apiModel, appId);
            return Response.status(202).build();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Apps controller post /app_id/move", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
