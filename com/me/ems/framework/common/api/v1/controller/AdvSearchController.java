package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.HashMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.ems.framework.common.api.v1.model.SearchHistory;
import javax.ws.rs.PathParam;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.common.api.v1.model.ChatQuery;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import com.me.ems.framework.common.api.v1.service.AdvSearchService;
import java.util.Map;
import com.me.ems.framework.common.api.v1.model.SearchTabTrack;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("search")
public class AdvSearchController
{
    private static Logger advSearchErrorLogger;
    private static Logger advSearchLogger;
    
    @POST
    @Path("trackData")
    @Consumes({ "application/searchTabTrackData.v1+json" })
    @Produces({ "application/searchTabTrackDataResponse.v1+json" })
    public Map searchTabTrackData(final SearchTabTrack searchTabTrack) {
        AdvSearchController.advSearchLogger.fine("Advanced Search : Entering updateSearchTabTrackData action");
        return new AdvSearchService().getTrackData(searchTabTrack);
    }
    
    @POST
    @Path("chatQuery")
    @Consumes({ "application/printChatQuery.v1+json" })
    @Produces({ "application/printChatQueryResponse.v1+json" })
    public Map printChatQuery(final ChatQuery chatQuery, @Context final SecurityContext securityContext) {
        AdvSearchController.advSearchLogger.fine("Advanced Search : Entering printChatQuery action");
        final User user = (User)securityContext.getUserPrincipal();
        final Long loginId = user.getLoginID();
        return new AdvSearchService().printChatQuery(chatQuery, loginId);
    }
    
    @GET
    @Path("history/{searchParamId}")
    @Produces({ "application/searchHistoryResponse.v1+json" })
    public Map searchHistoryForParamID(@PathParam("searchParamId") final Long searchParamId, final SearchHistory searchHistory, @Context final SecurityContext securityContext) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering getSearchHistoryForParamID action");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            return new AdvSearchService().getSearchHistoryForParamID(searchParamId, loginId);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting searchhistory", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("searchparams")
    @Produces({ "application/searchParamsResponse.v1+json" })
    public Map searchParams(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering getsearchparams ");
            final Map map = new HashMap();
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            final Map querymap = (Map)uriInfo.getQueryParameters();
            final String searchLevel = String.valueOf(querymap.get("searchLevel").get(0));
            final String searchParamId = String.valueOf(querymap.get("searchParamId").get(0));
            final Boolean isHistory = Boolean.parseBoolean(String.valueOf(querymap.get("isHistory").get(0)));
            map.put(searchParamId, new AdvSearchService().getSearchParams(searchLevel, searchParamId, isHistory, loginId));
            return (Map)new ObjectMapper().readValue(new JSONObject(map).toString(), (TypeReference)new TypeReference<Map<String, Object>>() {});
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting SearchParams", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("basesearchparams")
    @Produces({ "application/basesearchParamsResponse.v1+json" })
    public ArrayList basesearchParams(@PathParam("searchLevel") final String searchLevel, @PathParam("searchParamId") final String searchParamId, @PathParam("isHistory") final String isHistory, @Context final SecurityContext securityContext, @Context final HttpServletRequest request) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering getBaseSearchParams ");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            final List roles = user.getAllRoles();
            Locale locale = user.getUserLocale();
            if (locale == null) {
                locale = request.getLocale();
            }
            return new AdvSearchService().getBaseSearchParams(roles, locale);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting baseSearchParams", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("defaultSearchParam")
    @Produces({ "application/defaultSearchParamResponse.v1+json" })
    public Map defaultSearchParam(@Context final SecurityContext securityContext) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering getDefaultSearchParam action");
            final User user = (User)securityContext.getUserPrincipal();
            final List roles = user.getAllRoles();
            return new AdvSearchService().getDefaultSearchParams(roles);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting defaultSearchParams", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("searchPage")
    @Produces({ "application/searchPageInfoResponse.v1+json" })
    public Map searchPageInfo(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo, @Context final HttpServletRequest request) throws APIException {
        AdvSearchController.advSearchLogger.log(Level.FINE, "Advanced Search : Entering showSearchPage ");
        try {
            final User user = (User)securityContext.getUserPrincipal();
            final Map userparams = (Map)uriInfo.getQueryParameters();
            final String selectedTab = String.valueOf(userparams.get("selectedTab").get(0));
            String lastDateUsed;
            if (userparams.containsKey("lastDateUsed")) {
                lastDateUsed = String.valueOf(userparams.get("lastDateUsed").get(0));
            }
            else {
                final Date date = new Date();
                lastDateUsed = new SimpleDateFormat("yyyy/MM/dd").format(date);
            }
            Locale locale = user.getUserLocale();
            if (locale == null) {
                locale = request.getLocale();
            }
            final Map map = new AdvSearchService().getSearchPageInfo(selectedTab, lastDateUsed, user, locale);
            return (Map)new ObjectMapper().readValue(new JSONObject(map).toString(), (TypeReference)new TypeReference<Map<String, Object>>() {});
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final IOException e) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting SearchPageInfo", e);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @POST
    @Path("promotion/{path}/")
    @Produces({ "application/searchPromotionResponse.v1+json" })
    public void removeSearchPromotion(@PathParam("path") final String path, @Context final SecurityContext securityContext) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering verifySearchPromotion action");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            if (path.equals("remove")) {
                new AdvSearchService().removeSearchPromotion(loginId);
            }
            else {
                if (!path.equals("skip")) {
                    throw new Exception("Path needs to either remove or skip");
                }
                new AdvSearchService().setSkipPromotionMap(loginId);
            }
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in updating searchpromotion", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("verifyPromotion")
    @Produces({ "application/verifySearchPromotionResponse.v1+json" })
    public Map verifySearchPromotion(@Context final SecurityContext securityContext) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering verifySearchPromotion action");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            return new AdvSearchService().verifySearchPromotion(loginId);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in verifying searchPromotion", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("docSettResults")
    @Produces({ "application/docSettResultsResponse.v1+json" })
    public Map searchResults(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo, @Context final HttpServletRequest request) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering getSearchResults action");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            final MultivaluedMap<String, String> userParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
            Locale locale = user.getUserLocale();
            if (locale == null) {
                locale = request.getLocale();
            }
            final Map map = new AdvSearchService().getSearchResults(loginId, userParams, locale);
            return (Map)new ObjectMapper().readValue(new JSONObject(map).toString(), (TypeReference)new TypeReference<Map<String, Object>>() {});
        }
        catch (final APIException ex) {
            throw new APIException(ex);
        }
        catch (final Exception ex2) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting results for Features and Articles", ex2);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    @GET
    @Path("searchPageResults")
    @Produces({ "application/showSearchPageResultsResponse.v1+json" })
    public Map showSearchPageResults(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo, @Context final HttpServletRequest request) throws APIException {
        try {
            AdvSearchController.advSearchLogger.fine("Advanced Search : Entering showSearchResults ");
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginId = user.getLoginID();
            final MultivaluedMap<String, String> userParams = (MultivaluedMap<String, String>)uriInfo.getQueryParameters();
            Locale locale = user.getUserLocale();
            if (locale == null) {
                locale = request.getLocale();
            }
            return new AdvSearchService().showSearchPageResults(locale, loginId, userParams);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "Exception in getting searchPageResults", ex);
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    static {
        AdvSearchController.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        AdvSearchController.advSearchLogger = Logger.getLogger("AdvSearchLogger");
    }
}
