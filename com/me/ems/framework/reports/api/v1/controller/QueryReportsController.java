package com.me.ems.framework.reports.api.v1.controller;

import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.Node;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.framework.reports.api.v1.service.QueryReportsService;
import javax.ws.rs.Path;

@Path("reports/queryReports")
public class QueryReportsController
{
    QueryReportsService service;
    @Context
    ContainerRequestContext requestContext;
    
    public QueryReportsController() {
        this.service = new QueryReportsService();
    }
    
    private Long getCustomerIDFromContext() {
        return Long.parseLong((String)this.requestContext.getProperty("X-Customer"));
    }
    
    @GET
    @CustomerSegmented
    @Produces({ "application/allQueryReports.v1+json" })
    public Node getAvailableQueryReports() throws APIException {
        final User dcUser = (User)this.requestContext.getSecurityContext().getUserPrincipal();
        this.service.showQueryReport(dcUser);
        final Node query = this.service.getAvailableQueryReports(dcUser, this.getCustomerIDFromContext());
        if (query != null) {
            return query;
        }
        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_erro");
    }
}
