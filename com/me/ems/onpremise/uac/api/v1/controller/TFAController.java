package com.me.ems.onpremise.uac.api.v1.controller;

import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.Map;
import javax.ws.rs.Path;

@Path("two-factor-authentication")
public class TFAController
{
    @GET
    @Path("enforcement")
    @Produces({ "application/getEnforcementDetails.v1+json" })
    public Map getEnforcementDetails() throws APIException {
        return ApiFactoryProvider.getTFAService().getTFAEnforcementDetails();
    }
    
    @GET
    @RolesAllowed({ "Settings_Write" })
    @Produces({ "application/getTwoFactorDetails.v1+json" })
    public Map getTwoFactorDetails() throws APIException {
        return ApiFactoryProvider.getTFAService().getTwoFactorDetails();
    }
    
    @POST
    @RolesAllowed({ "Settings_Write" })
    @Consumes({ "application/saveTwoFactorDetails.v1+json" })
    public Response saveTwoFactorDetails(final Map twoFactorDetails, @Context final SecurityContext securityContext, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final User dmUser = (User)securityContext.getUserPrincipal();
        return ApiFactoryProvider.getTFAService().saveTwoFactorDetails(twoFactorDetails, dmUser, httpServletRequest);
    }
    
    @POST
    @RolesAllowed({ "Settings_Write" })
    @Path("auth-app/qr-code/{loginID}")
    @Consumes({ "application/QRCode.v1+json" })
    @Produces({ "application/QRCodeResponse.v1+json" })
    public Map<String, String> getQRCode(@PathParam("loginID") final Long loginID, final Map<String, String> responseTypeDetails) throws APIException {
        return ApiFactoryProvider.getTFAService().getQRCode(loginID, responseTypeDetails);
    }
    
    @PUT
    @Path("enforcement")
    @Produces({ "application/modifyTFAEnforcement.v1+json" })
    @Consumes({ "application/modifyTFAEnforcement.v1+json" })
    public Response extendOrDisableTFA(final Map tfaExtensionDetails, @Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        ApiFactoryProvider.getTFAService().extendOrDisableTFA(tfaExtensionDetails, user.getName());
        return Response.ok().build();
    }
}
