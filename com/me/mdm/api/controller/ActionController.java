package com.me.mdm.api.controller;

import javax.ws.rs.QueryParam;
import com.adventnet.persistence.DataAccessException;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import com.me.mdm.api.model.BaseAPIModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import com.me.mdm.api.command.schedule.ScheduledGroupActionResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import com.me.mdm.api.command.schedule.ScheduledActionsService;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.server.inv.api.service.ActionService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("actions")
public class ActionController extends BaseController
{
    protected static Logger logger;
    ActionService actionService;
    
    public ActionController() {
        this.actionService = new ActionService();
    }
    
    @GET
    @Path("/{group_action_id}/groups")
    public Map getGroupActionSummary(@Context final ContainerRequestContext requestContext, @PathParam("group_action_id") final String group_action_id) throws Exception {
        final ScheduledActionsService scheduledActionsService = new ScheduledActionsService();
        if (GroupActionScheduleUtils.isGroupActionScheduled(Long.parseLong(group_action_id))) {
            final ObjectMapper mapper = new ObjectMapper();
            scheduledActionsService.setCustomerDetails(requestContext);
            final ScheduledGroupActionResponseModel responseModel = scheduledActionsService.getScheduledActionInfo(group_action_id);
            final Map result = (Map)mapper.convertValue((Object)responseModel, (Class)Map.class);
            result.put("summary_count", scheduledActionsService.getScheduledGroupActionStatusDetails(Long.parseLong(group_action_id)));
            return result;
        }
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        return this.actionService.getGroupActionDetails(Long.valueOf(group_action_id), customerID);
    }
    
    @DELETE
    @Path("/{action_type}")
    public Response suspendActions(@Context final ContainerRequestContext requestContext, @PathParam("action_type") final String action_type, final Map reqParams) {
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        reqParams.put("user_name", baseAPIModel.getUserName());
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        this.actionService.suspendActions(reqParams, action_type, customerID);
        return Response.status(202).build();
    }
    
    @POST
    @Path("/validate")
    public Map validateActionsForDevices(@Context final ContainerRequestContext requestContext, final Map reqParams) {
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        return this.actionService.validateActionsForDevices(reqParams, customerID);
    }
    
    @POST
    @Path("/{action_type}/validate")
    public Map validateActionsForDevices(@Context final ContainerRequestContext requestContext, @PathParam("action_type") final String action_type, final Map reqParams) throws DataAccessException {
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        return this.actionService.validateActionsForDevices(reqParams, customerID, action_type);
    }
    
    @GET
    @Path("/clearAppDataSuggestions")
    public Map getClearAppDataSuggestions(@Context final ContainerRequestContext requestContext, @QueryParam("device_id") final String device_id, @QueryParam("group_id") final String group_id) {
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        return this.actionService.fetchClearAppDataSuggestions(device_id, group_id, customerID);
    }
    
    static {
        ActionController.logger = Logger.getLogger("MDMApiLogger");
    }
}
