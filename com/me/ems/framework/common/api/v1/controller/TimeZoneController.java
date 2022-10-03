package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.framework.server.model.DCTimezone;
import java.util.List;
import com.me.ems.framework.common.api.v1.service.TimeZoneService;
import javax.ws.rs.Path;

@Path("/timezones")
public class TimeZoneController
{
    TimeZoneService timeZoneService;
    
    public TimeZoneController() {
        this.timeZoneService = new TimeZoneService();
    }
    
    @GET
    @Produces({ "application/timezones.v1+json" })
    public List<DCTimezone> getTimezones() throws APIException {
        return this.timeZoneService.getTimezones();
    }
}
