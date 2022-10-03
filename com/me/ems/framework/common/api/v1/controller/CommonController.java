package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import java.util.HashMap;
import javax.ws.rs.PathParam;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ResourceBundle;
import javax.ws.rs.WebApplicationException;
import com.me.ems.framework.common.api.response.APIResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Locale;
import com.adventnet.i18n.MultiplePropertiesResourceBundleControl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Map;
import com.me.ems.framework.common.api.v1.service.CommonService;
import javax.ws.rs.Path;

@Path("")
public class CommonController
{
    CommonService commonService;
    
    public CommonController() {
        this.commonService = new CommonService();
    }
    
    @GET
    @Path("productMeta")
    @Produces({ "application/productMeta.v1+json" })
    public Map fetchProductMeta() {
        return this.commonService.fetchProductMeta();
    }
    
    @GET
    @Path("i18N")
    @Produces({ "application/i18N.v1+json" })
    public Map fetchI18NKeys(@Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        try {
            final Locale userLocale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            final ResourceBundle resourceBundle = new MultiplePropertiesResourceBundleControl().newCombinedBundle(userLocale, ClassLoader.getSystemClassLoader(), false);
            final Field lookup = resourceBundle.getClass().getDeclaredField("lookup");
            lookup.setAccessible(true);
            final Map<String, String> i18NMap = (Map<String, String>)lookup.get(resourceBundle);
            if (!userLocale.equals(Locale.US)) {
                final ResourceBundle resourceBundleEn = new MultiplePropertiesResourceBundleControl().newCombinedBundle(Locale.US, ClassLoader.getSystemClassLoader(), false);
                final Field lookupEn = resourceBundleEn.getClass().getDeclaredField("lookup");
                lookupEn.setAccessible(true);
                final Map<String, String> i18NMapEn = (Map<String, String>)lookupEn.get(resourceBundleEn);
                final Map resultMap = Stream.concat(i18NMapEn.entrySet().stream(), i18NMap.entrySet().stream()).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (i18NKeyEn, i18NKey) -> i18NKey));
                return resultMap;
            }
            return i18NMap;
        }
        catch (final Exception ex) {
            throw new WebApplicationException(APIResponse.errorResponse("GENERIC0002", ex.getMessage(), new String[0]));
        }
    }
    
    @GET
    @Path("i18NForModules")
    @Produces({ "application/i18NForModules.v1+json" })
    public Map fetchI18NForModules(@QueryParam("modules[]") final List<String> modules, @QueryParam("isServer") final Boolean isServer, @QueryParam("isClient") final Boolean isClient, @Context final SecurityContext securityContext) throws APIException {
        return this.commonService.getI18NForModules(modules, isServer, isClient);
    }
    
    @GET
    @Path("gettingStarted/status/{gettingStartedParam}")
    @Produces({ "application/gettingStartedStatus.v1+json" })
    @CustomerSegmented
    public Map isGettingStartedClosed(@PathParam("gettingStartedParam") final String gettingStartedParam, @Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        final Map gettingStartedMap = new HashMap();
        gettingStartedMap.put("isGettingStartedClosed", this.commonService.isGettingStartedClosed(user.getUserID(), gettingStartedParam));
        return gettingStartedMap;
    }
    
    @PUT
    @Path("gettingStarted/status")
    @Consumes({ "application/gettingStartedInfo.v1+json" })
    public Response closeGettingStarted(final Map gettingStartedInfo, @Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        final String gettingStartedParam = gettingStartedInfo.get("gettingStartedParam");
        final String isClosed = gettingStartedInfo.get("isClosed");
        if (gettingStartedParam == null || isClosed == null) {
            final String missingParam = (gettingStartedParam == null && isClosed == null) ? "gettingStartedParam,isClosed" : ((isClosed == null) ? "isClosed" : "gettingStartedParam");
            return APIResponse.missingParamErrorResponse(missingParam);
        }
        this.commonService.closeGettingStarted(user.getUserID(), gettingStartedParam, isClosed);
        return Response.noContent().build();
    }
    
    @GET
    @Path("leftTree/status")
    @Produces({ "application/leftTreeStatus.v1+json" })
    public Map isLeftTreeEnabled(@Context final SecurityContext securityContext) {
        final User user = (User)securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        final Map leftTreeStatusMap = new HashMap();
        leftTreeStatusMap.put("showLeftTree", this.commonService.getLeftTreeStatus(userID));
        return leftTreeStatusMap;
    }
    
    @PUT
    @Path("leftTree/status")
    @Consumes({ "application/leftTreeStatus.v1+json" })
    public Response updateLeftTreeStatus(final Map leftTreeInfo, @Context final SecurityContext securityContext) throws APIException {
        final User user = (User)securityContext.getUserPrincipal();
        final Long userID = user.getUserID();
        final Boolean showLeftTree = leftTreeInfo.get("showLeftTree");
        if (showLeftTree == null) {
            return APIResponse.missingParamErrorResponse("showLeftTree");
        }
        this.commonService.updateLeftTreeStatus(userID, showLeftTree);
        return Response.noContent().build();
    }
    
    @GET
    @Path("buildVersion")
    @Produces({ "application/latestBuildDetails.v1+json" })
    public Map<String, Object> getBuildVersionDetails() throws APIException {
        return this.commonService.getBuildVersionDetails();
    }
    
    @POST
    @Path("validateScheduledTime")
    @Produces({ "application/scheduledTimeValidationStatus.v1+json" })
    public Map isscheduledTimeValid(final Map timeData) {
        try {
            return this.commonService.isScheduledTimeValid(timeData);
        }
        catch (final Exception ex) {
            throw new WebApplicationException(ex.getMessage());
        }
    }
    
    @GET
    @Path("liveChat")
    @Produces({ "application/liveChatDetails.v1+json" })
    public Map<String, Object> getLiveChatWidgetCode() throws APIException {
        return this.commonService.getLiveChatWidgetCode();
    }
    
    @GET
    @Path("locales")
    @Produces({ "application/locales.v1+json" })
    public List<Map<String, String>> getLocales() throws APIException {
        return this.commonService.getLocales();
    }
    
    @GET
    @Path("timeFormat")
    @Produces({ "application/timeFormat.v1+json" })
    public List<Map<String, String>> getTimeFormat() throws APIException {
        return this.commonService.getTimeFormat();
    }
    
    @GET
    @Path("mobileAppUser")
    @Produces({ "application/mobileAppUserStatus.v1+json" })
    public Map<String, Boolean> getMobileAppUsedStatus() {
        return this.commonService.mobileAppUsedDetails();
    }
    
    @GET
    @Path("productProperties")
    @Produces({ "application/productProperties.v1+json" })
    public Map<String, Object> getProductProperties() throws APIException {
        return this.commonService.getProductLoaderProperties();
    }
}
