package com.me.ems.framework.server.quicklaunch.api.v1.controller;

import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.server.quicklaunch.api.v1.service.QuickLaunchService;
import javax.ws.rs.Path;

@Path("quickLaunch")
public class QuickLaunchController
{
    private QuickLaunchService service;
    @Context
    SecurityContext securityContext;
    
    public QuickLaunchController() {
        this.service = new QuickLaunchService();
    }
    
    @GET
    @AllowEntityFilter
    @Produces({ "application/quickLaunchResponse.v1+json" })
    public List<Map<String, Object>> getQuickLaunchLinks() throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        return this.service.getQuickLaunchLinks(user);
    }
}
