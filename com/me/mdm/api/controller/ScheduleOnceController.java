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

@Path("schedule_once")
public class ScheduleOnceController extends BaseController
{
    private static Logger logger;
    private final ScheduleCommandService scheduleCommandService;
    
    public ScheduleOnceController() {
        this.scheduleCommandService = new ScheduleCommandService();
    }
    
    @GET
    @Path("/{schedule_command_id}")
    public ScheduleActionsResponseModel getdetailsForCollection(@Context final ContainerRequestContext requestContext, @PathParam("schedule_command_id") final String collectionID) throws Exception {
        ScheduleOnceController.logger.log(Level.INFO, "GET Method has been initiated for /api/mdm/scheduled_commands params:{0}", collectionID);
        return this.scheduleCommandService.getScheduleOnceCommandToResourceDetails(requestContext, collectionID);
    }
    
    @POST
    public ScheduleActionsResponseModel scheduleCommand(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleOnceController.logger.log(Level.INFO, "POST ScheduleOnceAction ContainerRequestContext:{0} requestContext{1}", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.scheduleOnceCommandWrapper(requestContext, reqParams);
    }
    
    @PUT
    public ScheduleActionsResponseModel updateScheduledCommand(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleOnceController.logger.log(Level.INFO, "PUT ScheduleOnceAction ContainerRequestContext:{0} requestContext{1}:", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.modifyScheduleOnceCommand(requestContext, reqParams);
    }
    
    @DELETE
    public Response deleteScheduledCommand(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleOnceController.logger.log(Level.INFO, "DELTE Request to ScheduleOnceAction ContainerRequestContext:{0} requestContext{1}", new Object[] { requestContext, reqParams });
        return this.scheduleCommandService.removeScheduleOnceCommand(requestContext, reqParams);
    }
    
    static {
        ScheduleOnceController.logger = Logger.getLogger("MDMLogger");
    }
}
