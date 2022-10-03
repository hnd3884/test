package com.me.ems.framework.common.api.utils;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.search.AdvSearchProductSpecificHandler;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import java.util.TreeMap;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.Locale;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AdvSearchUtil
{
    private static Logger advSearchErrorLogger;
    private static Logger advSearchLogger;
    
    public void printLog(final JSONObject jObj, final String command) {
        AdvSearchUtil.advSearchLogger.log(Level.INFO, "# " + command + " : " + jObj.toString());
    }
    
    public Map<String, String> convertMultiToRegularMap(final MultivaluedMap<String, String> m) {
        final Map<String, String> map = new HashMap<String, String>();
        if (m == null) {
            return map;
        }
        for (final Map.Entry<String, List<String>> entry : m.entrySet()) {
            final StringBuilder sb = new StringBuilder();
            for (final String s : entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
            map.put(entry.getKey(), sb.toString());
        }
        return map;
    }
    
    public Map setSearchResultParams(final Locale locale, final DataObject dObj, final Long searchParamId, final String searchText, final MultivaluedMap userParams, final String searchParamName) {
        final HashMap map = new HashMap();
        try {
            String baseParamName = "";
            final String selectedTab = "";
            int baseParamType = 1;
            int i = 0;
            final Iterator itr = dObj.getRows("SearchCriteria");
            final Iterator list = dObj.getRows("AaaRole");
            final ArrayList arrayList = new ArrayList();
            while (list.hasNext()) {
                final Row row = list.next();
                arrayList.add(row.get("NAME"));
            }
            final ArrayList<HashMap> viewNameList = new ArrayList<HashMap>();
            while (itr.hasNext()) {
                final HashMap viewMap = com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getViewDetailMap(itr, searchText, locale);
                if (!viewMap.isEmpty() && (arrayList.isEmpty() || ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains(arrayList.get(i)))) {
                    viewNameList.add(viewMap);
                }
                ++i;
            }
            final HashMap baseParamDetailMap = com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getBaseParamDetails(searchParamId);
            baseParamName = baseParamDetailMap.get("PARAM_NAME");
            baseParamType = baseParamDetailMap.get("PARAM_TYPE");
            map.put("views", viewNameList);
        }
        catch (final Exception ex) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAction : Exception occurred - setSearchResultParams() :  ", ex);
        }
        return map;
    }
    
    public Map getSearchResultsMap(final Properties searchQueryProp, final Long loginID, final TreeMap<String, Long> authorizedRoleMap) throws Exception {
        final Map searchDataResults = new HashMap();
        JSONObject settingsFeaturesJsonObj = new JSONObject();
        try {
            final com.me.devicemanagement.framework.server.search.AdvSearchUtil advSearchUtil = com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance();
            final AdvSearchProductSpecificHandler searchProductSpecificHandler = advSearchUtil.getSearchProductSpecificHandler();
            final SearchConfiguration configuration = SearchConfiguration.getConfiguration();
            final Boolean searchSpellCheckerEnabled = configuration.isSearchSpellCheckerEnabled();
            final Boolean searchScoreEnabled = configuration.isSearchScoreEnabled();
            final Boolean searchDocsEnabled = configuration.isSearchDocsEnabled();
            final Boolean searchDocsFacetEnabled = configuration.isSearchDocsFacetEnabled();
            final Boolean searchSettingsEnabled = configuration.isSearchSettingsEnabled();
            final Boolean searchSettingsFacetEnabled = configuration.isSearchSettingsFacetEnabled();
            if (!configuration.isSearchEnabled()) {
                throw new APIException("ADV_SEARCH0002");
            }
            ((Hashtable<String, Long>)searchQueryProp).put("LOGIN_ID", loginID);
            ((Hashtable<String, Boolean>)searchQueryProp).put("search.spellchecker.enabled", searchSpellCheckerEnabled);
            ((Hashtable<String, Boolean>)searchQueryProp).put("search.score.enabled", searchScoreEnabled);
            com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().setPageLimits(searchQueryProp);
            final String querystr = searchQueryProp.getProperty("q");
            if (this.verifySearchTerm(searchDataResults, querystr, loginID)) {
                return searchDataResults;
            }
            final String selectedSearchTab = searchQueryProp.getProperty("src");
            String searchParamId = null;
            final String searchParamName = null;
            if (selectedSearchTab.equalsIgnoreCase("sall") || selectedSearchTab.equalsIgnoreCase("docs") || selectedSearchTab.equalsIgnoreCase("sett")) {
                settingsFeaturesJsonObj = searchProductSpecificHandler.getSettingsSearchParamsUtil();
            }
            if (settingsFeaturesJsonObj != null && settingsFeaturesJsonObj.length() > 0) {
                final String settingsFeaturesStr = settingsFeaturesJsonObj.toString();
                if (settingsFeaturesStr.contains("paramId") && settingsFeaturesStr.contains("paramName")) {
                    searchParamId = String.valueOf(settingsFeaturesJsonObj.get("paramId"));
                }
            }
            ((Hashtable<String, Boolean>)searchQueryProp).put("isapi", true);
            if (selectedSearchTab == null || (!selectedSearchTab.equalsIgnoreCase("sall") && !selectedSearchTab.equalsIgnoreCase("docs") && !selectedSearchTab.equalsIgnoreCase("sett"))) {
                AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Source to Search is Not defined - getSearchResults()" + selectedSearchTab);
                throw new APIException("ADV_SEARCH0006");
            }
            if (com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().isDocFileChecker() && (selectedSearchTab.equalsIgnoreCase("docs") || (selectedSearchTab.equalsIgnoreCase("sall") && searchDocsEnabled))) {
                ((Hashtable<String, Boolean>)searchQueryProp).put("searchFacets", searchDocsFacetEnabled);
                final JSONObject docResults = com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().getSearchResults(searchQueryProp, authorizedRoleMap, "docs");
                searchDataResults.put("documents", docResults);
            }
            if (com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().isStaticFileChecker() && (selectedSearchTab.equalsIgnoreCase("sett") || (selectedSearchTab.equalsIgnoreCase("sall") && searchSettingsEnabled))) {
                ((Hashtable<String, Boolean>)searchQueryProp).put("searchFacets", searchSettingsFacetEnabled);
                final JSONObject settingsResults = com.me.devicemanagement.framework.server.search.AdvSearchUtil.getInstance().getSearchResults(searchQueryProp, authorizedRoleMap, "sett");
                searchDataResults.put("settings", settingsResults);
            }
            if (searchParamId != null && !searchParamId.isEmpty()) {
                final Properties searchHistoryDataUpdateProps = new Properties();
                ((Hashtable<String, String>)searchHistoryDataUpdateProps).put("searchParamId", searchParamId);
                ((Hashtable<String, Long>)searchHistoryDataUpdateProps).put("LOGIN_ID", loginID);
                ((Hashtable<String, String>)searchHistoryDataUpdateProps).put("searchTextDecode", querystr);
                searchProductSpecificHandler.startProcessingHistoryDetailsUpdate(searchHistoryDataUpdateProps);
            }
            searchDataResults.put("scoredataavailable", searchScoreEnabled);
        }
        catch (final APIException exp) {
            throw new APIException(exp);
        }
        catch (final Exception exp2) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - getSearchResults() :  " + searchQueryProp.getProperty("q"), exp2);
            throw new APIException("ADV_SEARCH0005", null, new String[] { searchQueryProp.getProperty("q") });
        }
        return searchDataResults;
    }
    
    public boolean verifySearchTerm(final Map searchDataResults, final String querystr, final Long loginID) throws Exception {
        final boolean isNonSupportCharAvailable = querystr.matches("(.*)[$<>()\\[\\]\\{\\}=$](.*)");
        if (isNonSupportCharAvailable) {
            throw new APIException("ADV_SEARCH0003");
        }
        final Locale userlocale = I18NUtil.getUserLocaleFromDB(loginID);
        final boolean isleastOneAlphaNumericCharAvailable = querystr.matches(".*[a-zA-Z0-9]+.*");
        if (userlocale.equals(Locale.US) && !isleastOneAlphaNumericCharAvailable) {
            throw new APIException("ADV_SEARCH0004");
        }
        return false;
    }
    
    static {
        AdvSearchUtil.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        AdvSearchUtil.advSearchLogger = Logger.getLogger("AdvSearchLogger");
    }
}
