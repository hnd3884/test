package com.me.ems.onpremise.uac.api.v1.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.uac.api.v1.service.PasswordPolicyService;
import javax.ws.rs.Path;

@Path("password-policy")
public class PasswordPolicyController
{
    PasswordPolicyService passwordPolicyService;
    
    public PasswordPolicyController() {
        this.passwordPolicyService = new PasswordPolicyService();
    }
    
    @GET
    @Produces({ "application/passwordPolicyResponse.v1+json" })
    public Response getPasswordPolicyDetails() {
        return Response.status(Response.Status.OK).entity((Object)this.passwordPolicyService.getPasswordPolicyDetails()).build();
    }
    
    @POST
    @Consumes({ "application/savePasswordPolicy.v1+json" })
    public Response savePasswordPolicyDetails(final Map passwordPolicy) {
        return this.passwordPolicyService.savePasswordPolicyDetails(passwordPolicy);
    }
}
