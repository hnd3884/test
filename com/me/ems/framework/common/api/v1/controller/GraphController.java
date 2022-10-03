package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.common.api.v1.service.GraphService;
import com.me.ems.framework.common.api.v1.model.Graph;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

@Path("graphs")
public class GraphController
{
    @GET
    @Path("{graphName}/")
    @Produces({ "application/graph.v1+json" })
    public Graph fetchGraphDetails(@PathParam("graphName") final String graphName, @Context final UriInfo uriInfo, @Context final ContainerRequestContext requestContext) {
        final MultivaluedMap<String, String> queryParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
        String graphType = null;
        final StringBuilder params = new StringBuilder();
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        for (final String key : queryParams.keySet()) {
            final String value = (String)queryParams.getFirst((Object)key);
            if (!params.toString().equals("")) {
                params.append(";");
            }
            graphType = (key.equalsIgnoreCase("graphType") ? value : graphType);
            params.append(key).append("=").append(value);
        }
        if (!params.toString().equals("")) {
            params.append(";");
        }
        params.append("customerID").append("=").append(customerIDStr);
        final HashMap<String, Object> graphDetails = GraphService.getGraphDetails(graphName);
        if (graphType != null) {
            graphDetails.put("GRAPH_TYPE", graphType);
        }
        return GraphService.getGraphBean(graphDetails, params.toString());
    }
}
