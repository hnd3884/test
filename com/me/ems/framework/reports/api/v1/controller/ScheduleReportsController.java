package com.me.ems.framework.reports.api.v1.controller;

import javax.ws.rs.DELETE;
import java.util.List;
import java.util.Arrays;
import com.me.ems.framework.reports.core.DCScheduleReportUtil;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import com.me.ems.framework.reports.core.ScheduleReportsCoreUtil;
import java.util.Map;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.Node;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.framework.reports.api.v1.service.ScheduleReportsService;
import javax.ws.rs.Path;

@Path("reports/scheduleReports")
public class ScheduleReportsController
{
    ScheduleReportsService service;
    @Context
    ContainerRequestContext requestContext;
    
    public ScheduleReportsController() {
        this.service = new ScheduleReportsService();
    }
    
    private Long getCustomerIDFromContext() {
        return Long.parseLong((String)this.requestContext.getProperty("X-Customer"));
    }
    
    @GET
    @CustomerSegmented
    @Path("predefinedReports")
    @Produces({ "application/predefinedReports.v1+json" })
    public Node getPredefinedreports() throws APIException {
        final User user = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final Node<String> reports = this.service.getAvailableReports(user);
        if (reports != null) {
            return reports;
        }
        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.rc.viewer.error_internalServerError");
    }
    
    @GET
    @CustomerSegmented
    @Path("retentionPeriod")
    @Produces({ "application/retentionPeriod.v1+json" })
    public Map getRetentionPeriod() throws APIException {
        return this.service.getRetentionPeriod();
    }
    
    @PUT
    @CustomerSegmented
    @Path("retentionPeriod")
    @Consumes({ "application/retentionPeriod.v1+json" })
    public Response setRetentionPeriod(final Map retentionPeriod) throws APIException {
        Response response = null;
        final User user = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final Long userID = user.getUserID();
        if (ScheduleReportsCoreUtil.setRetentionPeriod(userID, this.getCustomerIDFromContext(), retentionPeriod)) {
            response = Response.status(Response.Status.NO_CONTENT).build();
            return response;
        }
        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
    }
    
    @POST
    @CustomerSegmented
    @Consumes({ "application/scheduleReport.v1+json" })
    @Produces({ "application/savedScheduleReport.v1+json" })
    public Response saveScheduleReport(final Map scheduleReport) throws APIException {
        Response response = null;
        final User User = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final Map responseMap = this.service.saveScheduleReportWithEventLog(scheduleReport, User, this.getCustomerIDFromContext());
        if (responseMap.get("status")) {
            response = Response.status(Response.Status.CREATED).entity((Object)responseMap).type("application/json").build();
            return response;
        }
        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
    }
    
    @PUT
    @Path("{taskID}")
    @CustomerSegmented
    @Consumes({ "application/scheduleReport.v1+json" })
    @Produces({ "application/updatedScheduleReport.v1+json" })
    public Response updateScheduleReport(final Map scheduleReport, @PathParam("taskID") final Long taskID) throws APIException {
        final User User = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        if (!User.isAdminUser() && !DCScheduleReportUtil.taskBelongsToUser(taskID, User.getUserID())) {
            throw new APIException(Response.Status.FORBIDDEN, "REP0008", "ems.admin.servicenow.authfailed");
        }
        final Map responseMap = this.service.updateScheduleReport(taskID, scheduleReport, User, this.getCustomerIDFromContext());
        if (responseMap.get("status")) {
            final Response response = Response.status(Response.Status.CREATED).entity((Object)responseMap).type("application/json").build();
            return response;
        }
        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
    }
    
    @GET
    @Path("{taskID}")
    @CustomerSegmented
    @Produces({ "application/scheduleReport.v1+json" })
    public Map getScheduleReport(@PathParam("taskID") final Long taskID) throws APIException {
        final User user = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        return this.service.getScheduleReport(taskID, user, this.getCustomerIDFromContext());
    }
    
    @DELETE
    @Path("{taskID}")
    @CustomerSegmented
    @Produces({ "application/deletedReports.v1+json" })
    public Map deleteScheduleReports(@PathParam("taskID") final Long taskID) throws APIException {
        final User dcUser = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final Long userID = dcUser.getUserID();
        final String owner = dcUser.getName();
        if (!dcUser.isAdminUser() && !DCScheduleReportUtil.taskBelongsToUser(taskID, userID)) {
            throw new APIException(Response.Status.FORBIDDEN, "REP0008", "ems.admin.servicenow.authfailed");
        }
        return this.service.deleteScheduleReports(owner, this.getCustomerIDFromContext(), Arrays.asList(taskID));
    }
    
    @POST
    @Path("{taskID}")
    @CustomerSegmented
    @Produces({ "application/taskExecutionStatus.v1+json" })
    public Response executeScheduleReport(@PathParam("taskID") final Long taskID) throws APIException {
        final User user = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final Long userID = user.getUserID();
        final String owner = user.getName();
        if (!user.isAdminUser() && !DCScheduleReportUtil.taskBelongsToUser(taskID, userID)) {
            throw new APIException(Response.Status.FORBIDDEN, "REP0008", "ems.admin.servicenow.authfailed");
        }
        if (!this.service.executeScheduleReports(owner, this.getCustomerIDFromContext(), taskID)) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return Response.noContent().build();
    }
}
