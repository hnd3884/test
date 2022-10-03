package com.me.mdm.api.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import java.util.Map;
import javax.ws.rs.GET;
import java.util.logging.Level;
import com.me.mdm.api.command.schedule.ScheduleActionsResponseModel;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.mdm.api.command.schedule.ScheduleCommandService;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("scheduled_commands")
public class ScheduledCommandsController extends BaseController
{
    private static Logger logger;
    private final ScheduleCommandService scheduleCommandService;
    
    public ScheduledCommandsController() {
        this.scheduleCommandService = new ScheduleCommandService();
    }
    
    @GET
    @Path("/{schedule_command_id}")
    public ScheduleActionsResponseModel getDetailsForCollection(@Context final ContainerRequestContext requestContext, @PathParam("schedule_command_id") final String collectionID) throws Exception {
        ScheduledCommandsController.logger.log(Level.INFO, "GET Method has been initiated for /api/mdm/scheduled_commands params:{0}", collectionID);
        return this.scheduleCommandService.getScheduledCommandDetails(collectionID);
    }
    
    @POST
    public ScheduleActionsResponseModel scheduleCommandsForDevices(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduledCommandsController.logger.log(Level.INFO, "POST Request to ScheduledActions ContainerRequestContext:{0} requestContext{1}:", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.scheduleCommandsToResourcesHandler(requestContext, reqParams);
    }
    
    @PUT
    public ScheduleActionsResponseModel updateScheduleCommandsForDevice(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduledCommandsController.logger.log(Level.INFO, "PUT Request to ScheduledActions ContainerRequestContext:{0} requestContext{1}:", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.modifyScheduledCommandToResourceHandler(requestContext, reqParams);
    }
    
    @DELETE
    public Response deleteScheduledCommandsToResource(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduledCommandsController.logger.log(Level.INFO, "DELTE Request to ScheduledActions ContainerRequestContext:{0} requestContext{1}:", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.removeScheduledCommandToResource(requestContext, reqParams);
    }
    
    static {
        ScheduledCommandsController.logger = Logger.getLogger("MDMLogger");
    }
}
