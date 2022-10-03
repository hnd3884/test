package com.me.ems.framework.reports.api.v1.controller;

import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.Node;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.framework.reports.api.v1.service.CustomReportsService;
import javax.ws.rs.Path;

@Path("reports/customReports")
public class CustomReportsController
{
    CustomReportsService service;
    @Context
    ContainerRequestContext requestContext;
    
    public CustomReportsController() {
        this.service = new CustomReportsService();
    }
    
    private Long getCustomerIDFromContext() {
        return Long.parseLong((String)this.requestContext.getProperty("X-Customer"));
    }
    
    @GET
    @CustomerSegmented
    @Produces({ "application/allCustomReports.v1+json" })
    public Node getAvailableCustomReports() throws APIException {
        final User dcUser = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        final boolean isPMP = CustomerInfoUtil.isPMP();
        if (isPMP) {
            throw new APIException(Response.Status.EXPECTATION_FAILED, "REP0008", "ems.rest.authentication.unauthorized");
        }
        return this.service.getAvailableCustomReports(dcUser, this.getCustomerIDFromContext());
    }
}
