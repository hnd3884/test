package com.me.mdm.api.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.inv.actions.InvActionUtil;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.api.command.schedule.ScheduledActionsService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("actions/scheduled")
public class ScheduledActionsAPIRequestHandler extends BaseController
{
    protected static Logger logger;
    private ScheduledActionsService scheduledActionsService;
    
    public ScheduledActionsAPIRequestHandler() {
        this.scheduledActionsService = new ScheduledActionsService();
    }
    
    @POST
    @Path("/{action_type}/validate")
    public Map validateScheduledActionsForGroups(@Context final ContainerRequestContext requestContext, @PathParam("action_type") final String actionName, final Map reqParams) throws DataAccessException {
        final List groupIds = reqParams.get("group_ids");
        return this.scheduledActionsService.getScheduledGroupDetails(groupIds, InvActionUtil.getEquivalentActionType(actionName));
    }
    
    @POST
    @Path("/validate")
    public Map validateScheduledActionsForGroupsWithCommands(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        final List groupIds = reqParams.get("group_ids");
        final List actionList = reqParams.get("actions");
        return this.scheduledActionsService.getScheduledGroupWithActionList(groupIds, actionList);
    }
    
    @POST
    @Path("/{action_name}")
    public Response scheduledAction(@Context final ContainerRequestContext requestContext, @PathParam("action_name") final String actionName, final Map reqParams) throws Exception {
        this.scheduledActionsService.setCustomerDetails(requestContext);
        return this.scheduledActionsService.createScheduedAction(actionName, reqParams);
    }
    
    @PUT
    @Path("/{action_type}")
    public Response modifyAction(@Context final ContainerRequestContext requestContext, @PathParam("action_type") final String actionName, final Map reqParams) throws Exception {
        this.scheduledActionsService.setCustomerDetails(requestContext);
        return this.scheduledActionsService.modifyScheduledAction(actionName, reqParams);
    }
    
    @DELETE
    @Path("/{action_type}")
    public Response suspendActions(@Context final ContainerRequestContext requestContext, @PathParam("action_type") final String action_type, final Map reqParams) throws Exception {
        this.scheduledActionsService.setCustomerDetails(requestContext);
        return this.scheduledActionsService.suspendScheduledAction(reqParams, action_type);
    }
    
    static {
        ScheduledActionsAPIRequestHandler.logger = Logger.getLogger("MDMLogger");
    }
}
