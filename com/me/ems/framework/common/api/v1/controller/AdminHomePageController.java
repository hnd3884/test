package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.common.api.v1.service.AdminHomePageService;
import javax.ws.rs.Path;

@Path("adminhomepage")
public class AdminHomePageController
{
    private AdminHomePageService adminHomePageService;
    @Context
    private SecurityContext securityContext;
    
    public AdminHomePageController() {
        this.adminHomePageService = new AdminHomePageService();
    }
    
    @GET
    @Produces({ "application/adminHomePageResponse.v1+json" })
    public List getAdminHomePageComponents() throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        return this.adminHomePageService.getAdminTabComponents(user);
    }
}
