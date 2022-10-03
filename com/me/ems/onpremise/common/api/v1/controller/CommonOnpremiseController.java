package com.me.ems.onpremise.common.api.v1.controller;

import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.onpremise.common.api.v1.service.CommonOnpremiseService;
import javax.ws.rs.Path;

@Path("")
public class CommonOnpremiseController
{
    private CommonOnpremiseService service;
    
    public CommonOnpremiseController() {
        this.service = new CommonOnpremiseService();
    }
    
    @GET
    @Path("avTest/status")
    @Produces({ "application/avTestStatus.v1+json" })
    public Map<String, Integer> getAvTestStatus() throws APIException {
        return this.service.getAvTestStatus();
    }
    
    @PUT
    @Path("dbLock/notification/close")
    public Response closeNotification() throws APIException {
        this.service.closeDbLockNotification();
        return Response.ok().build();
    }
}
