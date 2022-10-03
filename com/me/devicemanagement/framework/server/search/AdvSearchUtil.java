package com.me.devicemanagement.framework.server.search;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.search.SuggestSearchHandler;
import org.apache.commons.lang.StringUtils;
import java.util.TreeSet;
import java.util.Comparator;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.Iterator;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import org.json.JSONArray;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import org.json.JSONObject;
import java.util.TreeMap;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Set;
import java.util.logging.Logger;

public class AdvSearchUtil
{
    private static Logger logger;
    private static Logger advSearchErrorLogger;
    private static AdvSearchUtil criteria;
    private static SearchTrackAPI searchTrackAPI;
    private int pageIndex;
    private int pageLimit;
    private Set<Long> notifyLoginIdSet;
    private AdvSearchProductSpecificHandler searchProductSpecificHandler;
    private boolean docFileChecker;
    private boolean staticFileChecker;
    
    private AdvSearchUtil() throws Exception {
        this.pageIndex = 1;
        this.pageLimit = 25;
        this.notifyLoginIdSet = this.getNotifyLoginIDsForAdvSearch();
        this.searchProductSpecificHandler = ApiFactoryProvider.getSearchProductSpecificHandler();
        if (SearchConfiguration.getConfiguration().isSearchEnabled()) {
            this.setDocFileChecker();
            this.setStaticFileChecker();
        }
    }
    
    public static synchronized AdvSearchUtil getInstance() throws Exception {
        if (AdvSearchUtil.criteria == null) {
            AdvSearchUtil.criteria = new AdvSearchUtil();
        }
        return AdvSearchUtil.criteria;
    }
    
    public AdvSearchProductSpecificHandler getSearchProductSpecificHandler() {
        return this.searchProductSpecificHandler;
    }
    
    public boolean getAdvSearchPromotionEnable() throws Exception {
        final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        return this.notifyLoginIdSet.contains(loginId) && this.checkAdvSearchPromotion(loginId, false);
    }
    
    public boolean checkAdvSearchPromotion(final Long loginId, final boolean toRemove) throws DataAccessException {
        AdvSearchUtil.logger.info("Invoke checkAdvSearchPromotion with login ID : " + loginId + "  with toRemove : " + toRemove);
        try {
            Criteria notifyCri = new Criteria(Column.getColumn("NotifyChangesToUser", "LOGIN_ID"), (Object)loginId, 0);
            notifyCri = notifyCri.and(new Criteria(Column.getColumn("NotifyChangesToUser", "FUNCTIONALITY"), (Object)"SEARCH_PROMOTION", 0));
            final DataObject dObj = SyMUtil.getPersistence().get("NotifyChangesToUser", notifyCri);
            if (!dObj.isEmpty()) {
                if (toRemove) {
                    dObj.deleteRows("NotifyChangesToUser", notifyCri);
                    SyMUtil.getPersistence().update(dObj);
                    this.notifyLoginIdSet.remove(loginId);
                    AdvSearchUtil.advSearchErrorLogger.log(Level.INFO, "AdvSearchUtil : checkAdvSearchPromotion() :  Deleted Successfully loginID : " + loginId);
                }
                return true;
            }
        }
        catch (final DataAccessException e) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - checkAdvSearchPromotion() :  ", (Throwable)e);
            throw e;
        }
        return false;
    }
    
    private Set<Long> getNotifyLoginIDsForAdvSearch() {
        AdvSearchUtil.logger.log(Level.INFO, "Invoke getLoginIDs");
        final Set<Long> loginIDs = new HashSet<Long>();
        Connection con = null;
        DataSet dataSet = null;
        try {
            final Table table = new Table("NotifyChangesToUser");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
            final Column column1 = Column.getColumn(table.getTableName(), "LOGIN_ID");
            final Column column2 = Column.getColumn(table.getTableName(), "FUNCTIONALITY");
            selectQuery.addSelectColumn(column1);
            selectQuery.addSelectColumn(column2);
            final RelationalAPI relationalAPI = RelationalAPI.getInstance();
            con = relationalAPI.getConnection();
            dataSet = relationalAPI.executeQuery((Query)selectQuery, con);
            while (dataSet.next()) {
                final String string = String.valueOf(dataSet.getValue("FUNCTIONALITY"));
                if (string.equals("SEARCH_PROMOTION")) {
                    loginIDs.add((Long)dataSet.getValue("LOGIN_ID"));
                }
            }
        }
        catch (final Exception e) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - getNotifyLoginIDsForAdvSearch() :  ", e);
            if (dataSet != null) {
                try {
                    dataSet.close();
                }
                catch (final Exception e) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception in Closing the DataSet - getNotifyLoginIDsForAdvSearch() :  ", e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception in Closing the Connection - getNotifyLoginIDsForAdvSearch() :  ", e);
                }
            }
        }
        finally {
            if (dataSet != null) {
                try {
                    dataSet.close();
                }
                catch (final Exception e2) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception in Closing the DataSet - getNotifyLoginIDsForAdvSearch() :  ", e2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e2) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception in Closing the Connection - getNotifyLoginIDsForAdvSearch() :  ", e2);
                }
            }
        }
        return loginIDs;
    }
    
    public HashMap getSearchViewDetails(final String criteriaIdStr) {
        HashMap viewDetailsMap = null;
        try {
            if (criteriaIdStr != null) {
                final Long criteriaId = Long.parseLong(criteriaIdStr);
                final Criteria criSearch = new Criteria(Column.getColumn("SearchCriteria", "CRITERIA_ID"), (Object)criteriaId, 0);
                final DataObject dObj = SyMUtil.getPersistence().get("SearchCriteria", criSearch);
                viewDetailsMap = new HashMap();
                if (!dObj.isEmpty()) {
                    final Row row = dObj.getFirstRow("SearchCriteria");
                    viewDetailsMap.put("view_name", row.get("VIEW_NAME"));
                    viewDetailsMap.put("table_name", row.get("TABLE_NAME"));
                    viewDetailsMap.put("column_name", row.get("COLUMN_NAME"));
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - getSearchViewDetails() :  ", ex);
        }
        return viewDetailsMap;
    }
    
    public boolean verifyAuthToken(final HttpServletRequest request) throws Exception {
        final String authKey = request.getHeader("Authorization");
        final String loginAuthKey = getInstance().getSearchProductSpecificHandler().getAuthKey(request);
        return authKey.equals(loginAuthKey);
    }
    
    public Properties getPropertiesFromRequestSearchParameter(final HttpServletRequest request) throws Exception {
        final Properties searchQueryProp = new Properties();
        final String querystr = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("q")), Boolean.FALSE);
        String selectedSearchTab = "";
        String selectedTab = "";
        String selectedTreeElement = "";
        String selectedCategory = "";
        String isTriggerFromMenu = "false";
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.containsKey("src")) {
            selectedSearchTab = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("src")), Boolean.FALSE);
        }
        if (parameterMap.containsKey("stab")) {
            selectedTab = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("stab")), Boolean.TRUE);
        }
        if (parameterMap.containsKey("streeelem")) {
            selectedTreeElement = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("streeelem")), Boolean.TRUE);
        }
        if (parameterMap.containsKey("category")) {
            selectedCategory = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("category")), Boolean.TRUE);
        }
        if (parameterMap.containsKey("isTriggerFromMenu")) {
            isTriggerFromMenu = AdvSearchCommonUtil.validateSearchInput(String.valueOf(request.getParameter("isTriggerFromMenu")), Boolean.TRUE);
        }
        ((Hashtable<String, String>)searchQueryProp).put("q", querystr);
        ((Hashtable<String, String>)searchQueryProp).put("src", selectedSearchTab);
        ((Hashtable<String, String>)searchQueryProp).put("selectedTab", selectedTab);
        ((Hashtable<String, String>)searchQueryProp).put("selectedTreeElem", selectedTreeElement);
        ((Hashtable<String, String>)searchQueryProp).put("category", selectedCategory);
        ((Hashtable<String, String>)searchQueryProp).put("trigger", isTriggerFromMenu);
        ((Hashtable<String, String>)searchQueryProp).put("page", request.getParameter("page"));
        ((Hashtable<String, String>)searchQueryProp).put("pagelimit", request.getParameter("pagelimit"));
        ((Hashtable<String, String>)searchQueryProp).put("id", request.getParameter("id"));
        return searchQueryProp;
    }
    
    public Properties getPropertiesFromRequestSearchParameter(final MultivaluedMap map) throws Exception {
        final Properties searchQueryProp = new Properties();
        final String querystr = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"q")).get(0)), Boolean.FALSE);
        String selectedSearchTab = "";
        String selectedTab = "";
        String selectedTreeElement = "";
        String selectedCategory = "";
        String isTriggerFromMenu = "false";
        if (map.containsKey((Object)"src")) {
            selectedSearchTab = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"src")).get(0)), Boolean.FALSE);
        }
        if (map.containsKey((Object)"stab")) {
            selectedTab = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"stab")).get(0)), Boolean.TRUE);
        }
        if (map.containsKey((Object)"streeelem")) {
            selectedTreeElement = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"streeelem")).get(0)), Boolean.TRUE);
        }
        if (map.containsKey((Object)"category")) {
            selectedCategory = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"category")).get(0)), Boolean.TRUE);
        }
        if (map.containsKey((Object)"isTriggerFromMenu")) {
            isTriggerFromMenu = AdvSearchCommonUtil.validateSearchInput(String.valueOf(((List)map.get((Object)"isTriggerFromMenu")).get(0)), Boolean.TRUE);
        }
        ((Hashtable<String, String>)searchQueryProp).put("q", querystr);
        ((Hashtable<String, String>)searchQueryProp).put("src", selectedSearchTab);
        ((Hashtable<String, String>)searchQueryProp).put("selectedTab", selectedTab);
        ((Hashtable<String, String>)searchQueryProp).put("selectedTreeElem", selectedTreeElement);
        ((Hashtable<String, String>)searchQueryProp).put("category", selectedCategory);
        ((Hashtable<String, String>)searchQueryProp).put("trigger", isTriggerFromMenu);
        ((Hashtable<String, Object>)searchQueryProp).put("page", ((List)map.get((Object)"page")).get(0));
        ((Hashtable<String, Object>)searchQueryProp).put("pagelimit", ((List)map.get((Object)"pagelimit")).get(0));
        return searchQueryProp;
    }
    
    public JSONObject getSearchResults(final Properties searchQueryProp, final Long loginID, final TreeMap<String, Long> authorizedRoleMap) throws Exception {
        final JSONObject searchDataResults = new JSONObject();
        JSONObject settingsFeaturesJsonObj = new JSONObject();
        try {
            final AdvSearchUtil advSearchUtil = getInstance();
            final AdvSearchProductSpecificHandler searchProductSpecificHandler = advSearchUtil.getSearchProductSpecificHandler();
            final SearchConfiguration configuration = SearchConfiguration.getConfiguration();
            final Boolean searchSpellCheckerEnabled = configuration.isSearchSpellCheckerEnabled();
            final Boolean searchScoreEnabled = configuration.isSearchScoreEnabled();
            final Boolean searchDocsEnabled = configuration.isSearchDocsEnabled();
            final Boolean searchDocsFacetEnabled = configuration.isSearchDocsFacetEnabled();
            final Boolean searchSettingsEnabled = configuration.isSearchSettingsEnabled();
            final Boolean searchSettingsFacetEnabled = configuration.isSearchSettingsFacetEnabled();
            if (configuration.isSearchEnabled()) {
                ((Hashtable<String, Long>)searchQueryProp).put("LOGIN_ID", loginID);
                ((Hashtable<String, Boolean>)searchQueryProp).put("search.spellchecker.enabled", searchSpellCheckerEnabled);
                ((Hashtable<String, Boolean>)searchQueryProp).put("search.score.enabled", searchScoreEnabled);
                this.setPageLimits(searchQueryProp);
                final String querystr = searchQueryProp.getProperty("q");
                if (this.verifySearchTerm(searchDataResults, querystr, loginID)) {
                    return searchDataResults;
                }
                final String selectedSearchTab = searchQueryProp.getProperty("src");
                String searchParamId = null;
                String searchParamName = null;
                if (selectedSearchTab.equalsIgnoreCase("sall") || selectedSearchTab.equalsIgnoreCase("docs") || selectedSearchTab.equalsIgnoreCase("sett")) {
                    settingsFeaturesJsonObj = searchProductSpecificHandler.getSettingsSearchParamsUtil();
                }
                if (settingsFeaturesJsonObj != null && settingsFeaturesJsonObj.length() > 0) {
                    final String settingsFeaturesStr = settingsFeaturesJsonObj.toString();
                    if (settingsFeaturesStr.contains("searchParamId") && settingsFeaturesStr.contains("searchParamName")) {
                        searchParamId = String.valueOf(settingsFeaturesJsonObj.get("searchParamId"));
                        searchParamName = String.valueOf(settingsFeaturesJsonObj.get("searchParamName"));
                    }
                }
                ((Hashtable<String, Boolean>)searchQueryProp).put("isapi", false);
                if (selectedSearchTab == null || (!selectedSearchTab.equalsIgnoreCase("sall") && !selectedSearchTab.equalsIgnoreCase("docs") && !selectedSearchTab.equalsIgnoreCase("sett"))) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Source to Search is Not defined - getSearchResults()");
                    throw new Exception("Source to Search is Not defined..:" + selectedSearchTab);
                }
                if (this.isDocFileChecker() && (selectedSearchTab.equalsIgnoreCase("docs") || (selectedSearchTab.equalsIgnoreCase("sall") && searchDocsEnabled))) {
                    ((Hashtable<String, Boolean>)searchQueryProp).put("searchFacets", searchDocsFacetEnabled);
                    final JSONObject docResults = this.getSearchResults(searchQueryProp, authorizedRoleMap, "docs");
                    searchDataResults.put("documents", (Object)docResults);
                }
                if (this.isStaticFileChecker() && (selectedSearchTab.equalsIgnoreCase("sett") || (selectedSearchTab.equalsIgnoreCase("sall") && searchSettingsEnabled))) {
                    ((Hashtable<String, Boolean>)searchQueryProp).put("searchFacets", searchSettingsFacetEnabled);
                    final JSONObject settingsResults = this.getSearchResults(searchQueryProp, authorizedRoleMap, "sett");
                    searchDataResults.put("settings", (Object)settingsResults);
                }
                if (searchParamId != null && !searchParamId.isEmpty()) {
                    final Properties searchHistoryDataUpdateProps = new Properties();
                    ((Hashtable<String, String>)searchHistoryDataUpdateProps).put("searchParamId", searchParamId);
                    ((Hashtable<String, Long>)searchHistoryDataUpdateProps).put("LOGIN_ID", loginID);
                    ((Hashtable<String, String>)searchHistoryDataUpdateProps).put("searchTextDecode", querystr);
                    searchProductSpecificHandler.startProcessingHistoryDetailsUpdate(searchHistoryDataUpdateProps);
                }
                searchDataResults.put("scoredataavailable", (Object)searchScoreEnabled);
            }
            else {
                searchDataResults.put("error_code", 8001);
                searchDataResults.put("error", (Object)"Search Disabled");
                searchDataResults.put("error_description", (Object)"Search Disabled");
            }
        }
        catch (final Exception exp) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - getSearchResults() :  " + searchQueryProp.getProperty("q"), exp);
            searchDataResults.put("error_code", 100805);
            searchDataResults.put("Exception Message", (Object)exp.getMessage());
            searchDataResults.put("error", (Object)"Search Failed");
            searchDataResults.put("error_description", (Object)"Search Failed");
        }
        return searchDataResults;
    }
    
    public boolean verifySearchTerm(final JSONObject searchDataResults, final String querystr, final Long loginID) throws Exception {
        final boolean isNonSupportCharAvailable = querystr.matches("(.*)[$<>()\\[\\]\\{\\}=$](.*)");
        if (isNonSupportCharAvailable) {
            searchDataResults.put("error_code", 100803);
            searchDataResults.put("error", (Object)"Search terms can't contain the following characters: # & % = < > ( ) [ ] { }");
            searchDataResults.put("error_description", (Object)"Search terms can't contain the following characters: # & % = < > ( ) [ ] { }");
            return true;
        }
        final Locale userlocale = I18NUtil.getUserLocaleFromDB(loginID);
        final boolean isleastOneAlphaNumericCharAvailable = querystr.matches(".*[a-zA-Z0-9]+.*");
        if (userlocale.equals(Locale.US) && !isleastOneAlphaNumericCharAvailable) {
            searchDataResults.put("error_code", 100804);
            searchDataResults.put("error", (Object)"Search terms contain only symbol characters");
            searchDataResults.put("error_description", (Object)"Search terms contain only symbol characters");
            return true;
        }
        return false;
    }
    
    public JSONObject getSearchResults(final Properties searchQueryProp, final TreeMap<String, Long> authorizedRoleMap, final String searchType) throws Exception {
        JSONObject results = CompleteSearchUtil.searchData(searchQueryProp, authorizedRoleMap, searchType);
        if (results != null && results.length() > 0) {
            results = this.getSearchResultLimit(results);
        }
        AdvSearchLogger.getInstance().printQueryRequest(searchQueryProp, searchType, results);
        return results;
    }
    
    public JSONObject getSearchResultLimit(final JSONObject results) throws Exception {
        final Long totalResults = results.getLong("total");
        if (totalResults > 0L) {
            JSONArray resultDataArray = results.getJSONArray("searchdata");
            resultDataArray = this.getJsonArrayLimit(resultDataArray);
            results.remove("searchdata");
            results.put("searchdata", (Object)resultDataArray);
        }
        return results;
    }
    
    public JSONArray getJsonArrayLimit(final JSONArray jsonArray) throws Exception {
        final int pageLength = this.getPageLimit();
        final int pageIndex = this.getPageIndex();
        final int startIndex = pageLength * pageIndex - (pageLength - 1);
        final JSONArray tempArray = new JSONArray();
        if (jsonArray.length() > 0) {
            for (int i = startIndex - 1; i < jsonArray.length() && i < startIndex + pageLength - 1; ++i) {
                tempArray.put((Object)jsonArray.getJSONObject(i));
            }
        }
        return tempArray;
    }
    
    public void setPageLimits(final Properties searchQueryProp) {
        if (searchQueryProp.containsKey("page")) {
            this.setPageIndex(Integer.parseInt(searchQueryProp.getProperty("page")));
        }
        if (searchQueryProp.containsKey("pagelimit")) {
            this.setPageLimit(Integer.parseInt(searchQueryProp.getProperty("pagelimit")));
        }
    }
    
    public int getPageIndex() {
        return this.pageIndex;
    }
    
    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }
    
    public int getPageLimit() {
        return this.pageLimit;
    }
    
    public void setPageLimit(final int pageLimit) {
        this.pageLimit = pageLimit;
    }
    
    public boolean isDocFileChecker() {
        return this.docFileChecker;
    }
    
    public void setDocFileChecker() throws Exception {
        this.docFileChecker = this.searchIndexFileChecker(CompleteSearchUtil.getStaticActionMainIndexDir());
    }
    
    public boolean isStaticFileChecker() {
        return this.staticFileChecker;
    }
    
    public void setStaticFileChecker() throws Exception {
        this.staticFileChecker = this.searchIndexFileChecker(CompleteSearchUtil.getStaticActionMainIndexDir());
    }
    
    public boolean searchIndexFileChecker(final String indexDir) throws Exception {
        AdvSearchUtil.logger.log(Level.INFO, "Invoked searchIndexFileChecker(): " + indexDir);
        final String indexChecksumFile = indexDir + File.separator + "indexChecksum.json";
        final JSONObject searchChecksumJson = CompleteSearchUtil.getJsonObjectFromFile(indexChecksumFile);
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        if (fileAccessAPI.isFileExists(indexDir)) {
            final ArrayList<String> allFilesList = fileAccessAPI.getAllFilesList(indexDir, null, null);
            for (final String file : allFilesList) {
                final String filename = FilenameUtils.getName(file);
                if (!searchChecksumJson.has(filename) && !filename.equalsIgnoreCase("indexChecksum.json")) {
                    AdvSearchUtil.logger.log(Level.WARNING, "File Not Found: " + file);
                    new Exception("File Not Found: " + file);
                }
            }
        }
        return Boolean.TRUE;
    }
    
    public static SearchTrackAPI getSearchTrackAPI() {
        String className = null;
        try {
            className = ProductClassLoader.getSingleImplProductClass("DM_ADVSEARCH_TRACK_CLASS");
            if (className != null && className.trim().length() != 0) {
                return AdvSearchUtil.searchTrackAPI = (SearchTrackAPI)Class.forName(className).newInstance();
            }
        }
        catch (final Exception ex) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "Exception During Instantiation  for" + className, ex);
        }
        return null;
    }
    
    public Set<JSONObject> getSuggestionCategory(final String queryText, final String classId, final JSONObject searchParamJsonObj) {
        Set<JSONObject> suggestionCategorySet = null;
        try {
            final JSONArray searchParamList = searchParamJsonObj.getJSONArray("searchParamList");
            final Map<String, JSONObject> searchParamJsonMap = this.getSearchParamMap(searchParamList);
            suggestionCategorySet = this.getSuggestionCategorySet(searchParamJsonMap, queryText, classId);
        }
        catch (final Exception ex) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAction : Exception occurred - getSuggestionCategory() :  ", ex);
        }
        return suggestionCategorySet;
    }
    
    public boolean hasNoResult(final JSONObject searchDataResults, final String category) throws Exception {
        boolean isNoResult = false;
        if (searchDataResults.has(category)) {
            final JSONObject json = searchDataResults.getJSONObject(category);
            if (json.has("total")) {
                isNoResult = (json.getInt("total") == 0);
            }
        }
        return isNoResult;
    }
    
    public Set<JSONObject> getSuggestionCategorySet(final Map<String, JSONObject> searchParamJsonMap, final String queryText, final String classId) throws Exception {
        final Set<JSONObject> suggestionSearchParamSet = new TreeSet<JSONObject>(new Comparator<JSONObject>() {
            @Override
            public int compare(final JSONObject json1, final JSONObject json2) {
                int compare = 0;
                try {
                    compare = json2.getInt("total") - json1.getInt("total");
                }
                catch (final Exception ex) {
                    AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchUtil : Exception occurred - getSuggestionCategorySet() :  ", ex);
                }
                return compare;
            }
        });
        for (final String searchName : searchParamJsonMap.keySet()) {
            final JSONObject jsonObject = searchParamJsonMap.get(searchName);
            final String searchParamId = String.valueOf(jsonObject.get("searchParamId"));
            final List searchSuggestList = this.getSearchSuggestionList(queryText, classId, searchParamId);
            if (searchSuggestList != null && !searchSuggestList.isEmpty()) {
                jsonObject.put("total", searchSuggestList.size());
                suggestionSearchParamSet.add(jsonObject);
            }
        }
        return suggestionSearchParamSet;
    }
    
    public Map<String, JSONObject> getSearchParamMap(final JSONArray searchParamList) throws Exception {
        final Map<String, JSONObject> searchParamJsonMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < searchParamList.length(); ++i) {
            final JSONObject json = searchParamList.getJSONObject(i);
            if (json.has("searchChildNode")) {
                final JSONArray searchChildNode = json.getJSONArray("searchChildNode");
                for (int j = 0; j < searchChildNode.length(); ++j) {
                    final JSONObject childJson = searchChildNode.getJSONObject(j);
                    searchParamJsonMap.put(String.valueOf(childJson.get("searchParamName")), childJson);
                }
                searchParamJsonMap.put(String.valueOf(json.get("searchParamName")), json);
            }
        }
        return searchParamJsonMap;
    }
    
    public List getSearchSuggestionList(final String searchValue, final String classId, final String selectedSearchParamId) {
        final SuggestSearchHandler suggestSearchHandler = ApiFactoryProvider.getSuggestSearchHandler();
        final String className = suggestSearchHandler.getClassNameFromId(classId);
        List dataList = null;
        try {
            if (StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(searchValue)) {
                final SuggestQueryIfc suggestQuery = (SuggestQueryIfc)Class.forName(className).newInstance();
                dataList = suggestQuery.getSuggestData(searchValue, selectedSearchParamId);
            }
            else {
                AdvSearchUtil.logger.log(Level.WARNING, "Values cannot be empty for Suggest Class Name or queryText.");
            }
        }
        catch (final Exception ex) {
            AdvSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "SuggestSearch : Exception occurred - showSearchList() :  ", ex);
            AdvSearchUtil.logger.log(Level.WARNING, "Exception while getting suggest query class...", ex);
        }
        return dataList;
    }
    
    static {
        AdvSearchUtil.logger = Logger.getLogger(AdvSearchUtil.class.getName());
        AdvSearchUtil.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        AdvSearchUtil.criteria = null;
        AdvSearchUtil.searchTrackAPI = null;
    }
}
