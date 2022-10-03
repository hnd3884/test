package com.me.ems.onpremise.common.queuecount.api.v1.controller;

import javax.ws.rs.Consumes;
import java.util.Map;
import javax.ws.rs.PUT;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.common.queuecount.api.v1.service.QueueCountService;
import javax.ws.rs.Path;

@Path("queueCount")
public class QueueCountController
{
    private QueueCountService queueCountService;
    
    public QueueCountController() {
        this.queueCountService = new QueueCountService();
    }
    
    @PUT
    @Path("refreshAllCount")
    public Response refreshAllCount() throws APIException {
        this.queueCountService.refreshAllQueue();
        return Response.ok().build();
    }
    
    @PUT
    @Path("refreshCount")
    @Consumes({ "application/queueDetails.v1+json" })
    public Response refreshCount(final Map queueDetails) throws APIException {
        this.queueCountService.refreshQueue(queueDetails);
        return Response.ok().build();
    }
    
    @PUT
    @Path("suspendQueue")
    @Consumes({ "application/queueName.v1+json" })
    public Response suspendQueue(final Map queueDetails) throws APIException {
        this.queueCountService.suspendQueue(queueDetails);
        return Response.ok().build();
    }
    
    @PUT
    @Path("resumeQueue")
    @Consumes({ "application/queueName.v1+json" })
    public Response resumeQueue(final Map queueDetails) throws APIException {
        this.queueCountService.resumeQueue(queueDetails);
        return Response.ok().build();
    }
}
