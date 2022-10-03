package com.me.ems.framework.common.api.v1.controller;

import java.util.Hashtable;
import com.me.ems.framework.common.api.v1.service.DashboardPreferenceService;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashboardPreferenceBean;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import org.json.JSONArray;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashCardBean;
import com.adventnet.persistence.DataObject;
import com.me.ems.framework.common.api.v1.service.DashCardListService;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.v1.model.DashCardListBean;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.GET;
import javax.ws.rs.core.MultivaluedMap;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.common.api.v1.service.DashCardService;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.me.ems.framework.common.api.constants.DashboardConstants;
import java.util.List;
import java.util.Properties;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.DashCardAPIBean;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("summary")
public class DashboardController
{
    private static String sourceClass;
    private static Logger logger;
    
    @GET
    @CustomerSegmented
    @Path("/cards")
    @Produces({ "application/cards.v1+json", "text/plain" })
    public DashCardAPIBean getDashboardCard(@Context final UriInfo ui, @Context final SecurityContext securityContext, @Context final ContainerRequestContext requestContext) throws APIException {
        final String sourceMethod = "getDashboardCard";
        final User user = (User)securityContext.getUserPrincipal();
        try {
            final MultivaluedMap<String, String> queryMap = (MultivaluedMap<String, String>)ui.getQueryParameters();
            String viewName = null;
            String parentViewName = null;
            Long filterID = null;
            Long filterValueID = null;
            Long resourceId = null;
            String filterName = "";
            String filterValue = "";
            String dashboardName = "";
            Boolean isFilterCall = false;
            final Boolean renderCardData = true;
            final Properties cardProps = new Properties();
            if (queryMap.containsKey((Object)"meta[viewName]")) {
                final List<String> list = (List<String>)queryMap.get((Object)"meta[viewName]");
                viewName = list.get(0);
                ((Hashtable<String, String>)cardProps).put("selected", viewName);
                ((Hashtable<String, String>)cardProps).put("viewName", viewName);
            }
            if (queryMap.containsKey((Object)"meta[parentViewName]")) {
                final List<String> list = (List<String>)queryMap.get((Object)"meta[parentViewName]");
                parentViewName = list.get(0);
                ((Hashtable<String, String>)cardProps).put("parentViewName", parentViewName);
            }
            if (queryMap.containsKey((Object)"meta[filterCall]")) {
                final List<String> list = (List<String>)queryMap.get((Object)"meta[filterCall]");
                isFilterCall = Boolean.parseBoolean(list.get(0));
            }
            if (queryMap.containsKey((Object)"dashboardName")) {
                dashboardName = (String)queryMap.getFirst((Object)"dashboardName");
                ((Hashtable<String, String>)cardProps).put("dashboardName", dashboardName);
            }
            if (queryMap.containsKey((Object)"filterId")) {
                filterID = Long.parseLong((String)queryMap.getFirst((Object)"filterId"));
                ((Hashtable<String, Long>)cardProps).put("filterId", filterID);
            }
            if (queryMap.containsKey((Object)"filterValueId")) {
                filterValueID = Long.parseLong((String)queryMap.getFirst((Object)"filterValueId"));
                ((Hashtable<String, Long>)cardProps).put("filterValueId", filterValueID);
                ((Hashtable<String, Integer>)cardProps).put("showFilter", DashboardConstants.SHOW_FILTER);
                ((Hashtable<String, Boolean>)cardProps).put("isCardFilterApplied", true);
                if (filterID != null) {
                    filterName = DashboardUtil.getInstance().getFilterName(filterID);
                }
                if (filterValueID != null) {
                    filterValue = DashboardUtil.getInstance().getValueName(filterID, filterValueID);
                }
            }
            if (queryMap.containsKey((Object)"resourceId")) {
                resourceId = Long.parseLong((String)queryMap.getFirst((Object)"resourceId"));
                ((Hashtable<String, Boolean>)cardProps).put("isResourceDashboard", true);
                ((Hashtable<String, Long>)cardProps).put("resourceId", resourceId);
            }
            ((Hashtable<String, Boolean>)cardProps).put("renderCardData", renderCardData);
            final DashCardAPIBean resultBean = DashCardService.getInstance().formCardBean(cardProps);
            final String customerIDStr = (String)requestContext.getProperty("X-Customer");
            final Long custId = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
            final Long techId = user.getLoginID();
            if (isFilterCall) {
                DashboardUtil.getInstance().updateCardFilterUser(custId, techId, cardProps);
                DashboardController.logger.info("Updated the DB with the latest filterID and filterValueID");
            }
            DashboardController.logger.info("Formed DashCardAPIBean result bean");
            return resultBean;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            DashboardController.logger.severe("Exception while forming response for getDashboardCards" + ex2);
            throw new APIException("GENERIC0002", "Exception while forming response getDashboardCards" + ex2.getStackTrace(), new String[0]);
        }
    }
    
    @GET
    @CustomerSegmented
    @Path("/dashboards")
    @Produces({ "application/dashboards.v1+json", "text/plain" })
    public DashCardListBean getDashboardCards(@QueryParam("dashboardName") final String dashboardName, @QueryParam("resourceId") final Long resourceId, @Context final ContainerRequestContext requestContext, @Context final SecurityContext securityContext) throws APIException {
        final String sourceMethod = "getDashboardCards";
        final User user = (User)securityContext.getUserPrincipal();
        try {
            final Long loginId = user.getLoginID();
            final String customerIDStr = (String)requestContext.getProperty("X-Customer");
            final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
            final Long dashboardID = DashboardUtil.getInstance().getDashBoardId(dashboardName);
            DashboardController.logger.info("Fetch the dashboard cards for dashboardName: " + dashboardName + " resourceId: " + resourceId);
            if (dashboardID == null || dashboardID == -1L) {
                DashboardController.logger.warning("DashboardID not found in DB");
                throw new APIException(Response.Status.NO_CONTENT, "GENERIC0001", "DashboardID not found", (String[])null);
            }
            final DataObject resultDO = DashboardUtil.getInstance().getDashBoardDO(dashboardID);
            if (resultDO != null && !resultDO.isEmpty()) {
                final Boolean renderCardData = false;
                List<DashCardBean> cardBeans;
                if (resourceId != null) {
                    cardBeans = DashCardListService.getInstance().formDashCardBeanFromDO(dashboardName, resultDO, resourceId, renderCardData, customerID, loginId);
                }
                else {
                    cardBeans = DashCardListService.getInstance().formDashCardBeanFromDO(dashboardName, resultDO, renderCardData, customerID, loginId);
                }
                final DashCardListBean resultBean = new DashCardListBean();
                resultBean.setDashboardName(dashboardName);
                resultBean.setCardBeans(cardBeans);
                DashboardController.logger.info("DashCardListBean Result Bean formed for Dashboard: " + dashboardName);
                return resultBean;
            }
            DashboardController.logger.warning("No content for dashboard was found dashboard id-" + dashboardID);
            throw new APIException(Response.Status.FORBIDDEN, "GENERIC0010", "Dashboard ID valid, but it's content not accessbile", (String[])null);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            DashboardController.logger.severe("Exception while forming response for getDashboardCards" + ex2);
            throw new APIException("GENERIC0002", "Exception while forming response getDashboardCards" + ex2.getStackTrace(), new String[0]);
        }
    }
    
    @PUT
    @CustomerSegmented
    @Path("/dashboards")
    @Consumes({ "application/dashboardsOrder.v1+json" })
    public void updateDashboardOrder(final DashCardListBean dashboardBean) throws APIException {
        final String sourceMethod = "updateDashboardOrder";
        try {
            final String dashboardName = dashboardBean.getDashboardName();
            DashboardController.logger.info("Updating the dashboard properties for dashboard: " + dashboardName);
            final Long dashboardID = DashboardUtil.getInstance().getDashBoardId(dashboardName);
            if (dashboardID == null || dashboardID == -1L) {
                DashboardController.logger.warning("DashboardID not found in DB");
                throw new APIException(Response.Status.NO_CONTENT, "GENERIC0001", "DashboardID not found", (String[])null);
            }
            final JSONArray dashCardJSONArr = DashCardListService.getInstance().formDashCardJSONFromBean(dashboardBean);
            DashboardUtil.getInstance().updateDashboardDO(dashboardID, dashCardJSONArr);
            DashboardController.logger.info("Successfully updated the dashboard: " + dashboardName);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            DashboardController.logger.info("Exception while forming response for getDashboardCards" + ex2);
            throw new APIException("GENERIC0002", "Exception while forming response getDashboardCards" + ex2.getStackTrace(), new String[0]);
        }
    }
    
    @PUT
    @CustomerSegmented
    @Path("/defaultdashboard/{dashboardName}")
    @Consumes({ "application/dashboardsDefault.v1+json" })
    public void updateDefaultDashboard(@PathParam("dashboardName") final String dashboardName, @Context final SecurityContext securityContext, @Context final ContainerRequestContext requestContext) throws APIException {
        final String sourceMethod = "updateFavouriteDashboard";
        final User user = (User)securityContext.getUserPrincipal();
        try {
            DashboardController.logger.info("Updating the default dashboard to " + dashboardName);
            final Long dashboardID = DashboardUtil.getInstance().getDashBoardId(dashboardName);
            if (dashboardID == null || dashboardID == -1L) {
                DashboardController.logger.warning("DashboardID not found in DB");
                throw APIException.noDataAvailable();
            }
            final String customerIDStr = (String)requestContext.getProperty("X-Customer");
            final Long custId = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
            final Long loginID = user.getLoginID();
            DashboardUtil.getInstance().updateFavouriteDashboard(loginID, custId, dashboardID);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            DashboardController.logger.severe("Exception while updating the default dashboard" + ex2);
            throw new APIException("GENERIC0002", "Exception while updating the default dashboard" + ex2.getStackTrace(), new String[0]);
        }
    }
    
    @GET
    @CustomerSegmented
    @Path("/dashboardPreferences")
    @Produces({ "application/dashboardPreferences.v1+json", "text/plain" })
    public DashboardPreferenceBean getCustomerPreferences(@Context final SecurityContext securityContext, @Context final ContainerRequestContext requestContext) throws APIException {
        final String sourceMethod = "getCustomerPreferences";
        final User dcUser = (User)securityContext.getUserPrincipal();
        try {
            final Long loginID = dcUser.getLoginID();
            final String customerIDStr = (String)requestContext.getProperty("X-Customer");
            final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
            final DashboardPreferenceBean dashboardPreferenceBean = DashboardPreferenceService.getInstance().formCustomerPreferenceBean(customerID, loginID);
            return dashboardPreferenceBean;
        }
        catch (final Exception ex) {
            throw new APIException("GENERIC0002", "Exception while forming response" + ex.getStackTrace(), new String[0]);
        }
    }
    
    static {
        DashboardController.sourceClass = DashboardController.class.getName();
        DashboardController.logger = Logger.getLogger(DashboardController.class.getName());
    }
}
