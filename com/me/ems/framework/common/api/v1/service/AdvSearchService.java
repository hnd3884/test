package com.me.ems.framework.common.api.v1.service;

import java.util.Hashtable;
import java.util.HashSet;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.TreeMap;
import java.util.Properties;
import org.apache.commons.lang.BooleanUtils;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.search.AdvSearchProductSpecificHandler;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.search.AdvSearchLogger;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;
import com.me.ems.framework.common.api.v1.model.ChatQuery;
import com.me.devicemanagement.framework.server.search.SearchTrackAPI;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import com.me.ems.framework.common.api.v1.model.SearchTabTrack;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class AdvSearchService
{
    private static Logger advSearchErrorLogger;
    private static Logger advSearchLogger;
    private static Set<Long> skipPromotionSet;
    
    public Map setSkipPromotionMap(final Long loginId) {
        final HashMap hashMap = new HashMap();
        hashMap.put("skipLoginSet", AdvSearchService.skipPromotionSet);
        final boolean isAdded = AdvSearchService.skipPromotionSet.add(loginId);
        hashMap.put("currentLoginId", loginId);
        hashMap.put("isAdded", isAdded);
        return hashMap;
    }
    
    public Map getTrackData(final SearchTabTrack searchTabTrack) {
        final HashMap map = new HashMap();
        try {
            final String searchTab = searchTabTrack.getSearchTab();
            map.put("selectedTab", searchTab);
            final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
            if (searchTrackAPI != null) {
                if (searchTab.equals("settingsDiv")) {
                    searchTrackAPI.incrementSearchTrackData("selDocTabCount");
                    map.put("selectedArticlesTabCount", Boolean.TRUE);
                }
                if (searchTab.equals("documentsDiv")) {
                    searchTrackAPI.incrementSearchTrackData("selSettTabCount");
                    map.put("selectedFeaturesTabCount", Boolean.TRUE);
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchService.advSearchErrorLogger.severe("Advanced Search : Exception in  getTrackData" + ex.getMessage());
        }
        return map;
    }
    
    public Map printChatQuery(final ChatQuery chatQuery, final Long loginID) {
        AdvSearchService.advSearchLogger.fine("Advanced Search : Entering printChatQuery action");
        final HashMap jObj = new HashMap();
        try {
            final String previousSearchQuery = chatQuery.getPreviousSearchQuery();
            final String searchType = chatQuery.getType();
            final String requestId = loginID + "," + chatQuery.getId();
            jObj.put("q", previousSearchQuery);
            jObj.put("type", searchType);
            new com.me.ems.framework.common.api.utils.AdvSearchUtil().printLog(new JSONObject((Map)jObj), "Chat Query");
            final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
            if (searchTrackAPI != null) {
                searchTrackAPI.incrementSearchTrackData("chatCount");
            }
        }
        catch (final Exception ex) {
            AdvSearchService.advSearchErrorLogger.severe("Advanced Search : Entering printChatQuery" + ex.getMessage());
        }
        return jObj;
    }
    
    public Map previousSearchQuery(final MultivaluedMap userParams, final Long loginId) {
        Map map = new HashMap();
        try {
            AdvSearchService.advSearchLogger.fine("Advanced Search : Entering printPreviousResultSelected action");
            map = AdvSearchLogger.getInstance().printPreviousResultSelected(userParams);
            final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
            if (searchTrackAPI != null) {
                searchTrackAPI.updateSelectedResultDetails((String)userParams.get((Object)"perviousFeatureResultSelectedArray"), (String)userParams.get((Object)"perviousFeatureResultSelectedArray"), new JSONObject(map));
            }
        }
        catch (final Exception exception) {
            AdvSearchService.advSearchErrorLogger.severe("Advanced Search : Exception in  printPreviousResultSelected " + exception.getMessage());
        }
        return map;
    }
    
    public Map getSearchHistoryForParamID(final Long searchParamId, final Long loginId) throws Exception {
        SelectQuery paramQuery = null;
        Set searchHistoryDataSet = null;
        final HashMap map = new HashMap();
        final AdvSearchProductSpecificHandler productSpecificHandler = AdvSearchUtil.getInstance().getSearchProductSpecificHandler();
        paramQuery = productSpecificHandler.getSearchHistoryParamQueryForParamID(loginId, (long)searchParamId);
        searchHistoryDataSet = productSpecificHandler.setSearchHistoryDataUtil(paramQuery);
        if (searchHistoryDataSet != null && !searchHistoryDataSet.isEmpty()) {
            map.put("history", searchHistoryDataSet);
        }
        return map;
    }
    
    public ArrayList getSearchParams(final String searchLevelStr, final String searchParamIdStr, final Boolean isHistory, final Long loginId) throws Exception {
        ArrayList paramList = new ArrayList();
        int searchLevel = -1;
        Long searchParamId = null;
        SelectQuery paramQuery = null;
        final AdvSearchProductSpecificHandler searchProductSpecificHandler = AdvSearchUtil.getInstance().getSearchProductSpecificHandler();
        if (isHistory) {
            paramQuery = searchProductSpecificHandler.getSearchHistoryParamQuery(loginId);
        }
        else {
            if (searchLevelStr != null && searchParamIdStr != null) {
                searchLevel = Integer.valueOf(searchLevelStr);
                searchParamId = Long.valueOf(searchParamIdStr);
            }
            paramQuery = searchProductSpecificHandler.getSearchParamQuery(searchLevel, searchParamId);
        }
        paramList = searchProductSpecificHandler.setSearchParams(paramQuery, paramList, isHistory);
        return paramList;
    }
    
    public ArrayList getBaseSearchParams(final List roles, final Locale locale) throws Exception {
        return AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getBaseSearchParamsUtil(true, roles, locale);
    }
    
    public Map getDefaultSearchParams(final List roles) throws Exception {
        return AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getDefaultSearchParamsUtil(roles);
    }
    
    public Map getSearchPageInfo(final String selectedTab, final String lastDateUsed, final User user, final Locale locale) throws APIException {
        try {
            final HashMap resultMap = new HashMap();
            final HashMap map = new HashMap();
            map.put("selectedTab", selectedTab);
            map.put("authToken", user.getAuthToken());
            map.put("roles", user.getAllRoles());
            final AdvSearchProductSpecificHandler searchProductSpecificHandler = AdvSearchUtil.getInstance().getSearchProductSpecificHandler();
            final JSONObject searchParamJsonObj = searchProductSpecificHandler.getSearchParamsListAsJsonObjectFromCache(map, locale);
            final Long loginId = user.getLoginID();
            final JSONObject lastSearchParamJSONObj = searchProductSpecificHandler.getLastSearchParam(loginId, locale);
            searchParamJsonObj.remove("authParamKey");
            if (lastSearchParamJSONObj.length() != 0) {
                searchParamJsonObj.put("selectedParam", (Object)lastSearchParamJSONObj);
            }
            resultMap.put("result", searchParamJsonObj);
            try {
                final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
                if (searchTrackAPI != null) {
                    searchTrackAPI.updateSearchUsageDays(lastDateUsed);
                    searchTrackAPI.incrementSearchTrackData("searchCount");
                }
            }
            catch (final JSONException ex) {
                AdvSearchService.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAction : Exception occurred - showSearchPage() :  ", (Throwable)ex);
                throw ex;
            }
            final JSONObject result = resultMap.get("result");
            final JSONArray arrayList = (JSONArray)result.get("paramList");
            if (arrayList.length() == 0) {
                throw new APIException("ADV_SEARCH0007", I18NUtil.getJSMsgFromLocale(locale, "dm.advsearch.verfication.failed", new Object[0]), new String[0]);
            }
            return resultMap;
        }
        catch (final APIException ex2) {
            throw ex2;
        }
        catch (final Exception e) {
            throw new APIException("ADV_SEARCH0001");
        }
    }
    
    public Map removeSearchPromotion(final Long loginId) throws Exception {
        final HashMap map = new HashMap();
        final boolean advSearchPromotion = AdvSearchUtil.getInstance().checkAdvSearchPromotion(loginId, true);
        AdvSearchService.advSearchLogger.log(Level.INFO, "Successfully deleted IS_ADV_SEARCH_PROMOTION_ENABLE : " + advSearchPromotion);
        int statusCode = 100810;
        if (advSearchPromotion) {
            statusCode = 100809;
            AdvSearchService.skipPromotionSet.remove(loginId);
        }
        map.put("searchPromotionStatus", statusCode);
        map.put("searchPromotionRemoved", advSearchPromotion);
        return map;
    }
    
    public Map verifySearchPromotion(final Long loginId) throws Exception {
        final Map map = new HashMap();
        final boolean searchEnabled = SearchConfiguration.getConfiguration().isSearchEnabled();
        final boolean isAdvSearchPromotionEnable = searchEnabled && AdvSearchUtil.getInstance().getAdvSearchPromotionEnable();
        final boolean skipPromotion = AdvSearchService.skipPromotionSet.contains(loginId);
        int statusCode = 100809;
        if (isAdvSearchPromotionEnable) {
            statusCode = 100808;
        }
        if (isAdvSearchPromotionEnable && !skipPromotion) {
            map.put("promotionNeeded", true);
        }
        else {
            map.put("promotionNeeded", false);
        }
        return map;
    }
    
    public Map getSearchResults(final Long loginId, final MultivaluedMap map, final Locale locale) throws Exception {
        final AdvSearchUtil advSearchUtil = AdvSearchUtil.getInstance();
        final TreeMap<String, Long> authorizedRoleMap = DMUserHandler.getAuthorizedRolesForAccId(loginId);
        final Properties searchQueryProp = advSearchUtil.getPropertiesFromRequestSearchParameter(map);
        final Map searchDataResults = new com.me.ems.framework.common.api.utils.AdvSearchUtil().getSearchResultsMap(searchQueryProp, loginId, authorizedRoleMap);
        final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
        if (searchDataResults.containsKey("error_code") || searchDataResults.containsKey("error_description")) {
            searchDataResults.put("status", "error");
        }
        else {
            searchDataResults.put("status", "success");
            if (searchTrackAPI != null) {
                searchTrackAPI.updateDetails(searchQueryProp);
            }
            final boolean isSuggestionRequired = BooleanUtils.toBoolean(String.valueOf(map.get((Object)"isCategorySuggestionRequired")));
            final boolean hasNoResultForSetting = advSearchUtil.hasNoResult(new JSONObject(searchDataResults), "settings");
            if (isSuggestionRequired) {
                final String classId = String.valueOf(map.get((Object)"classId"));
                final HashMap hashMap = (HashMap)new com.me.ems.framework.common.api.utils.AdvSearchUtil().convertMultiToRegularMap((MultivaluedMap<String, String>)map);
                final JSONObject searchParamJsonObj = advSearchUtil.getSearchProductSpecificHandler().getSearchParamsListAsJsonObjectFromCache(hashMap, locale);
                if (hasNoResultForSetting) {
                    final String queryText = searchQueryProp.getProperty("q");
                    final Set<JSONObject> suggestionCategorySet = advSearchUtil.getSuggestionCategory(queryText, classId, searchParamJsonObj);
                    if (!suggestionCategorySet.isEmpty()) {
                        searchDataResults.put("suggestionCategory", suggestionCategorySet);
                    }
                }
            }
            if (searchTrackAPI != null) {
                if (hasNoResultForSetting) {
                    searchTrackAPI.incrementSearchTrackData("noSett");
                }
                if (advSearchUtil.hasNoResult(new JSONObject(searchDataResults), "documents")) {
                    searchTrackAPI.incrementSearchTrackData("noDoc");
                }
            }
        }
        final Long searchParamId = Long.valueOf(((List)map.get((Object)"searchparamId")).get(0).toString());
        if (searchQueryProp.getProperty("src").equals("sall") && searchTrackAPI != null && searchParamId != null) {
            searchTrackAPI.recordSearchParamCount(searchParamId);
        }
        final Properties searchHistoryDataProps = new Properties();
        ((Hashtable<String, Long>)searchHistoryDataProps).put("searchParamId", searchParamId);
        ((Hashtable<String, Long>)searchHistoryDataProps).put("LOGIN_ID", loginId);
        ((Hashtable<String, String>)searchHistoryDataProps).put("searchTextDecode", String.valueOf(((List)map.get((Object)"q")).get(0)));
        AdvSearchUtil.getInstance().getSearchProductSpecificHandler().startProcessingHistoryDetailsUpdate(searchHistoryDataProps);
        return searchDataResults;
    }
    
    public Map showSearchPageResults(final Locale locale, final Long loginId, final MultivaluedMap userparams) throws Exception {
        Long searchParamId = null;
        Map hashMap = new HashMap();
        final String searchTextDecode = "";
        DataObject dObj = null;
        final String searchParamIdStr = ((List)userparams.get((Object)"searchParamId")).get(0).toString();
        final String searchText = ((List)userparams.get((Object)"searchText")).get(0).toString();
        final Criteria criteria = new Criteria(Column.getColumn("SearchParams", "PARAM_ID"), (Object)searchParamIdStr, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("SearchParams", criteria);
        final String searchParamName = String.valueOf(dataObject.getRow("SearchParams").get("PARAM_NAME"));
        if (searchParamIdStr != null && searchText != null) {
            searchParamId = Long.valueOf(searchParamIdStr);
            final Criteria criSearch = new Criteria(Column.getColumn("SearchCriteria", "PARAM_ID"), (Object)searchParamId, 0);
            final Properties searchHistoryDataProps = new Properties();
            ((Hashtable<String, Long>)searchHistoryDataProps).put("searchParamId", searchParamId);
            ((Hashtable<String, Long>)searchHistoryDataProps).put("LOGIN_ID", loginId);
            ((Hashtable<String, String>)searchHistoryDataProps).put("searchTextDecode", searchText);
            AdvSearchUtil.getInstance().getSearchProductSpecificHandler().startProcessingHistoryDetailsUpdate(searchHistoryDataProps);
            if (searchParamName.equalsIgnoreCase("dc.js.common.COMPUTER_NAME") || searchParamName.equalsIgnoreCase("dc.js.common.DEVICE_NAME")) {
                dObj = AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getCompDeviceDetailParam(searchParamId);
            }
            else {
                dObj = SyMUtil.getPersistence().get("SearchCriteria", criSearch);
            }
            if (!dObj.isEmpty()) {
                hashMap = new com.me.ems.framework.common.api.utils.AdvSearchUtil().setSearchResultParams(locale, dObj, searchParamId, searchText, userparams, searchParamName);
            }
        }
        final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
        if (searchTrackAPI != null && searchParamId != null) {
            searchTrackAPI.recordSearchParamCount(searchParamId);
        }
        return hashMap;
    }
    
    static {
        AdvSearchService.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        AdvSearchService.advSearchLogger = Logger.getLogger("AdvSearchLogger");
        AdvSearchService.skipPromotionSet = new HashSet<Long>();
    }
}
