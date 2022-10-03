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
import com.me.mdm.server.profiles.api.service.ProfileService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("profiles")
public class ProfilesController extends BaseController
{
    private Logger logger;
    private ProfileService profileService;
    
    public ProfilesController() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileService = new ProfileService();
    }
    
    @POST
    @Path("/{profile_id}/move")
    public Response moveProfileToAllCustomers(@Context final ContainerRequestContext requestContext, @PathParam("profile_id") final Long profileId) throws APIHTTPException {
        try {
            final BaseAPIModel apiModel = new BaseAPIModel();
            apiModel.setCustomerUserDetails(requestContext);
            final Long loginId = apiModel.getLogInId();
            if (!DMUserHandler.isUserInAdminRole(loginId)) {
                this.logger.log(Level.INFO, "User not in admin role to move profiles to all customers");
                throw new APIHTTPException("COM0013", new Object[0]);
            }
            this.profileService.moveProfileToAllCustomers(apiModel, profileId);
            return Response.status(202).build();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Profiles controller post /profile_id/move", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
