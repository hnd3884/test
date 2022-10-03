package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.PUT;
import com.me.devicemanagement.framework.webclient.quicklink.QuickLinkControllerUtil;
import org.apache.commons.lang.StringUtils;
import com.me.ems.framework.common.api.response.APIResponse;
import javax.ws.rs.core.Response;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.v1.model.QuickLinkGroup;
import java.util.List;
import com.me.ems.framework.common.api.v1.service.QuickLinkService;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.QuickLink;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

@Path("quickLinks")
public class QuickLinkController
{
    @GET
    @Path("{pageNumber}/")
    @Produces({ "application/fetchQuickLinks.v1+json" })
    public QuickLink fetchQuickLinks(@PathParam("pageNumber") final String pageNumberString, @Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        final Long pageNumber = Long.valueOf(pageNumberString);
        final List<QuickLinkGroup> quickLinkGroups = QuickLinkService.getInstance().getDCQuickLinkList(pageNumber);
        return QuickLinkService.getInstance().getDCQuickLink(user, pageNumber, quickLinkGroups);
    }
    
    @PUT
    @Path("{pageNumber}/")
    @Produces({ "application/setVisibleStatus.v1+json" })
    public Response setVisibleStatus(@PathParam("pageNumber") final String pageNumberString, @Context final SecurityContext securityContext, final Map postData) {
        final User user = (User)securityContext.getUserPrincipal();
        final Long pageNumber = Long.valueOf(pageNumberString);
        final Long userID = user.getUserID();
        if (postData == null) {
            return APIResponse.errorResponse("QUICKLINK0002");
        }
        final String visibleStatus = postData.containsKey("showHideStatus") ? postData.get("showHideStatus") : "";
        final Integer showHideStatus = StringUtils.equalsIgnoreCase(visibleStatus, "show") ? 1 : (StringUtils.equalsIgnoreCase(visibleStatus, "hide") ? 0 : -1);
        if (showHideStatus != -1) {
            QuickLinkControllerUtil.getInstance().setShowHideStatus(showHideStatus, pageNumber, userID);
            return Response.status(Response.Status.OK).entity((Object)"success").build();
        }
        return APIResponse.errorResponse("QUICKLINK0001");
    }
}
