package com.me.ems.framework.home.api.v1.controller;

import javax.ws.rs.POST;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import com.me.ems.framework.home.core.CardPositionBean;
import java.util.List;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.home.api.v1.service.HomePageService;
import javax.ws.rs.Path;

@Path("home")
public class HomePageController
{
    private static HomePageService service;
    
    @GET
    @Path("details")
    @Produces({ "application/homePageDetails.v1+json" })
    public Map<String, Object> getHomePageDetails(@Context final SecurityContext securityContext, @Context final HttpServletRequest request) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final Map<String, Boolean> requestData = new HashMap<String, Boolean>(6);
        requestData.put("expiryMail", Boolean.parseBoolean(request.getParameter("expiryMail")));
        requestData.put("warningMail", Boolean.parseBoolean(request.getParameter("warningMail")));
        requestData.put("extendMail", Boolean.parseBoolean(request.getParameter("extendMail")));
        requestData.put("techInvite", Boolean.parseBoolean(request.getParameter("techInvite")));
        return HomePageController.service.getHomePageDetails(user, requestData);
    }
    
    @GET
    @Path("usefulLinks")
    @Produces({ "application/homePageUsefulLinks.v1+json" })
    public Map<String, Object> getHomePageQuickLinks(@Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        return HomePageController.service.getHomePageHelpLinks(user);
    }
    
    @GET
    @Path("dashCards")
    @Produces({ "application/homeDashboardCards.v1+json" })
    @CustomerSegmented(requireCustomerID = true)
    public Map<String, Object> getHomePageDashCards(@Context final ContainerRequestContext requestContext, @Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = Long.parseLong(customerIDStr);
        final Long loginID = user.getLoginID();
        return HomePageController.service.getHomePageDashboardCards(loginID, customerID);
    }
    
    @PUT
    @Path("dashCards")
    @Consumes({ "application/cardIDvsPosition.v1+json" })
    @Produces({ "application/cardsPositionUpdateStatus.v1+json" })
    @CustomerSegmented(requireCustomerID = true)
    public Map<String, Boolean> updateDashCardPosition(@Context final ContainerRequestContext requestContext, @Context final SecurityContext securityContext, final Map<String, List<CardPositionBean>> idVsCardPosition) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = Long.parseLong(customerIDStr);
        final Long loginID = user.getLoginID();
        final List<CardPositionBean> idVsCardPositionList = idVsCardPosition.get("cardOrder");
        return HomePageController.service.updateCardPosition(idVsCardPositionList, loginID, customerID);
    }
    
    @PUT
    @Path("freeLicense/notification/close")
    public Response closeFreeLicenseNotification(@Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        final Long loginID = user.getLoginID();
        return HomePageController.service.closeNotificationForLicense(loginID);
    }
    
    @POST
    @Path("licensePromo/time")
    public Response updateLicensePromoClickedTime() {
        if (SyMUtil.getSyMParameter("LicensePromoMessageClickedTime") == null) {
            SyMUtil.updateSyMParameter("LicensePromoMessageClickedTime", String.valueOf(System.currentTimeMillis()));
        }
        return Response.ok().build();
    }
    
    static {
        HomePageController.service = new HomePageService();
    }
}
