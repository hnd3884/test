package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.common.api.v1.service.LiteFilterService;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.LiteFilter;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;

@Path("liteFilters")
public class LiteFilterController
{
    @GET
    @Produces({ "application/liteFilters.v1+json" })
    public Map<String, LiteFilter> fetchFilterComponents(@Context final UriInfo uriInfo, @Context final SecurityContext securityContext, @Context final ContainerRequestContext containerRequestContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final MultivaluedMap paramMap = (MultivaluedMap)new MultivaluedHashMap(uriInfo.getQueryParameters());
        final List pageIDList = (List)paramMap.get((Object)"pageID");
        final List viewIDList = (List)paramMap.get((Object)"viewID");
        final Long pageID = (pageIDList == null) ? null : Long.valueOf(pageIDList.get(0));
        final Long viewID = (viewIDList == null) ? null : Long.valueOf(viewIDList.get(0));
        final Long userID = user.getUserID();
        if (pageID == null || viewID == null) {
            final String missingParam = (pageID == null && viewID == null) ? "pageID,viewID" : ((pageID == null) ? "pageID" : "viewID");
            throw new APIException("GENERIC0003", null, new String[] { missingParam });
        }
        final String customerIDStr = (String)containerRequestContext.getProperty("X-Customer");
        if (customerIDStr == null && CustomerInfoUtil.getInstance().isMSP()) {
            throw new APIException("CUSTOMER0002");
        }
        if (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) {
            final Long customerID = Long.parseLong(customerIDStr);
            paramMap.add((Object)"customerID", (Object)customerID);
        }
        else {
            if (customerIDStr != null) {
                try {
                    final ArrayList customersInfoList = CustomerInfoUtil.getInstance().getCustomersForUser(userID);
                    customersInfoList.stream().forEach(item -> {
                        if (item instanceof HashMap) {
                            final HashMap customerMap = (HashMap)item;
                            final Long customerID2 = customerMap.get("CUSTOMER_ID");
                            multivaluedMap.add((Object)"customerID", (Object)customerID2);
                        }
                        return;
                    });
                    return LiteFilterService.getInstance().getApplicableFiltersForPage(pageID, viewID, user, paramMap);
                }
                catch (final Exception e) {
                    throw new APIException("CUSTOMER0001");
                }
            }
            paramMap.add((Object)"customerID", (Object)CustomerInfoUtil.getInstance().getDefaultCustomer());
        }
        final Map<String, LiteFilter> applicableFilterMap = LiteFilterService.getInstance().getApplicableFiltersForPage(pageID, viewID, user, paramMap);
        return applicableFilterMap;
    }
}
