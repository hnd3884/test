package com.me.ems.framework.server.tabcomponents.api.v1.controller;

import javax.ws.rs.DELETE;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import com.me.ems.framework.server.tabcomponents.core.TabBean;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.AllowEntityFilter;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.server.tabcomponents.api.v1.model.TabComponent;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.server.tabcomponents.api.v1.service.TabComponentService;
import javax.ws.rs.Path;

@Path("tabComponents")
public class TabComponentController
{
    private TabComponentService tabComponentService;
    @Context
    private SecurityContext securityContext;
    
    public TabComponentController() {
        this.tabComponentService = new TabComponentService();
    }
    
    @GET
    @AllowEntityFilter
    @Produces({ "application/tabComponentResponse.v1+json" })
    public TabComponent getEnabledTabComponents() throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        return this.tabComponentService.getApplicableTabs(user);
    }
    
    @PUT
    @Path("order")
    @Consumes({ "application/tabIDvsPosition.v1+json" })
    @Produces({ "application/tabPositionUpdateStatus.v1+json" })
    public Map<String, Boolean> updateTabPosition(final Map<String, List<String>> updatedTabOrder) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final List<String> orderedTabIDs = updatedTabOrder.get("tabOrder");
        final JSONObject logData = new JSONObject();
        logData.put((Object)"DOMAIN_NAME", (Object)user.getDomainName());
        logData.put((Object)"AUTH_TYPE", (Object)user.getAuthType());
        logData.put((Object)"TAB_ORDERS", (Object)orderedTabIDs);
        try {
            final Map<String, Boolean> map = this.tabComponentService.updateTabPosition(orderedTabIDs, user);
            logData.put((Object)"REMARK", (Object)"SUCCESS");
            return map;
        }
        catch (final APIException ex) {
            logData.put((Object)"REMARK", (Object)"FAILURE");
            throw ex;
        }
        finally {
            SecurityOneLineLogger.log("Server", "Modify_custom_tab", logData, Level.INFO);
        }
    }
    
    @POST
    @Consumes({ "application/tabData.v1+json" })
    @Produces({ "application/tabAddedStatus.v1+json" })
    public Map<String, String> addNewCustomTab(final TabBean tabBean) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        return this.tabComponentService.addNewCustomTab(tabBean, userID);
    }
    
    @PUT
    @Consumes({ "application/tabData.v1+json" })
    public Response updateCustomTab(@QueryParam("tabID") final String tabID, final TabBean tabBean) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        return this.tabComponentService.updateCustomTab(tabBean, tabID, userID);
    }
    
    @PUT
    @Path("newTabCounter")
    @Produces({ "application/newTabNotification.v1+json" })
    public Map<ServerAPIConstants.TabAttribute, Boolean> deleteNewTabNotification(@QueryParam("tabID") final String tabID) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        return this.tabComponentService.updateNewTabCounter(tabID, userID);
    }
    
    @DELETE
    public Response deleteCustomTab(@QueryParam("tabID") final String tabID) throws APIException {
        final User user = (User)this.securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        return this.tabComponentService.deleteCustomTab(tabID, userID);
    }
}
