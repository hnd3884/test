package com.me.ems.onpremise.useraccount.api.v1.controller;

import javax.ws.rs.GET;
import com.me.ems.framework.common.api.annotations.RestrictMatched;
import com.me.ems.onpremise.uac.api.v1.service.PasswordPolicyService;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.factory.UacFactoryProvider;
import com.me.ems.onpremise.useraccount.validators.TokenValidation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.onpremise.useraccount.api.v1.service.AccountActionService;
import javax.ws.rs.Path;

@Path("account")
public class AccountActionController
{
    AccountActionService accountActionService;
    
    public AccountActionController() {
        this.accountActionService = AccountActionService.getInstance();
    }
    
    @POST
    @Path("authenticate")
    @Produces({ "application/tokenValidation.v1+json" })
    @Consumes({ "application/tokenValidation.v1+json" })
    @PermitAll
    public Response validateUserToken(final Map tokenRequest, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final TokenValidation tokenValidation = new TokenValidation(tokenRequest);
        final Map tokenDetails = tokenValidation.validateToken();
        return Response.status(Response.Status.OK).entity((Object)UacFactoryProvider.getUserAccountServiceObject().getUserTokenDetails(tokenDetails, httpServletRequest)).build();
    }
    
    @POST
    @Path("password/policy")
    @Produces({ "application/passwordpolicy.v1+json" })
    @Consumes({ "application/passwordpolicy.v1+json" })
    @PermitAll
    @RestrictMatched("Probe")
    public Response getPasscodePolicy(final Map tokenRequest) throws APIException {
        final TokenValidation tokenValidation = new TokenValidation(tokenRequest);
        final Map tokenDetails = tokenValidation.validateToken();
        final PasswordPolicyService passwordPolicyService = new PasswordPolicyService();
        return Response.status(Response.Status.OK).entity((Object)passwordPolicyService.getPasswordPolicyDetails()).build();
    }
    
    @POST
    @Path("password")
    @Produces({ "application/addpassword.v1+json" })
    @Consumes({ "application/addpassword.v1+json" })
    @PermitAll
    public Response addPassword(final Map passwordDetails, @Context final HttpServletRequest httpServletRequest) throws APIException {
        final TokenValidation tokenValidation = new TokenValidation(passwordDetails);
        final Map tokenDetails = tokenValidation.validateToken();
        return Response.status(Response.Status.OK).entity((Object)UacFactoryProvider.getUserAccountServiceObject().addOrUpdatePassword(tokenDetails, httpServletRequest)).build();
    }
    
    @POST
    @Path("password/reset")
    @Produces({ "application/resetpassword.v1+json" })
    @Consumes({ "application/resetpassword.v1+json" })
    @PermitAll
    public Response resetpassword(final Map mailDetails, @Context final HttpServletRequest httpServletRequest) throws APIException {
        UacFactoryProvider.getUserAccountServiceObject().sendPasswordLink(mailDetails, httpServletRequest);
        return Response.status(Response.Status.ACCEPTED).build();
    }
    
    @GET
    @Path("metadata")
    @Produces({ "application/productmeta.v1+json" })
    @Consumes({ "application/productmeta.v1+json" })
    @PermitAll
    @RestrictMatched("Probe")
    public Response getproductMeta() throws APIException {
        return Response.status(Response.Status.OK).entity((Object)this.accountActionService.getProductMeta()).build();
    }
}
