package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import com.me.mdm.server.apps.api.model.BlockListAppsModel;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.apps.api.service.BlocklistAppService;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("/blocklist")
public class BlocklistAppController extends BaseController
{
    private BlocklistAppService blocklistAppService;
    
    public BlocklistAppController() {
        this.blocklistAppService = new BlocklistAppService();
    }
    
    @POST
    @Path("/criticalapps")
    public Response getCriticalApps(@Context final ContainerRequestContext requestContext, final BlockListAppsModel blockListAppsModel) {
        blockListAppsModel.setCustomerUserDetails(requestContext);
        return Response.status(Response.Status.OK).entity((Object)this.blocklistAppService.getCriticalApps(blockListAppsModel)).build();
    }
}
