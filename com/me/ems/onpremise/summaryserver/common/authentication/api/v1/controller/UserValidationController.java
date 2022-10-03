package com.me.ems.onpremise.summaryserver.common.authentication.api.v1.controller;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.security.auth.login.LoginException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import javax.ws.rs.core.Response;
import com.me.ems.framework.uac.api.v1.model.AuthUser;
import com.me.ems.onpremise.uac.api.v1.service.OPUserService;
import javax.ws.rs.Path;

@Path("validateUser")
public class UserValidationController
{
    OPUserService opUserService;
    
    public UserValidationController() {
        this.opUserService = OPUserService.getInstance();
    }
    
    @POST
    @Consumes({ "application/authentication.v1+json" })
    @Produces({ "application/authenticationresponse.v1+json" })
    @PermitAll
    public Response authenticateUser(final AuthUser authUser) {
        final String userName = authUser.getUserName();
        String password = authUser.getEncodedPassword();
        final String domainName = authUser.getDomainName();
        final String authType = authUser.getAuthType();
        password = ((password != null && !password.trim().isEmpty()) ? SyMUtil.decodeAsUTF16LE(password) : null);
        try {
            final User userDetails = this.opUserService.validateAndAuthenticateUser(userName, password, domainName, authType);
            if (userDetails != null) {
                return Response.status(Response.Status.OK).entity((Object)userDetails).build();
            }
            throw new LoginException(I18N.getMsg("ems.rest.userdetails.not.found", new Object[0]));
        }
        catch (final Exception ex) {
            return APIResponse.errorResponse("USER0003");
        }
    }
}
