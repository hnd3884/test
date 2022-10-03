package com.me.ems.framework.uac.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.Path;

@Path("uac")
public class UserController
{
    @GET
    @Path("userMeta")
    @Produces({ "application/userMeta.v1+json" })
    public User getLoggedInUserMeta(@Context final SecurityContext securityContext) {
        final User dcUser = (User)securityContext.getUserPrincipal();
        return dcUser;
    }
}
