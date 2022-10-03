package com.me.devicemanagement.framework.server.search;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class AdvSearchLogger
{
    public static final String PREVIOUS_SEARCHQUERY = "perviousSearchquery";
    public static final String PERVIOUS_FEATURE_RESULT_SELECTED_ARRAY = "perviousFeatureResultSelectedArray";
    public static final String PERVIOUS_FEATURE_RESULT_SELECTED_POSITION_ARRAY = "perviousFeatureResultSelectedPositionArray";
    public static final String PERVIOUS_ARTICLE_RESULT_SELECTED_ARRAY = "perviousArticleResultSelectedArray";
    public static final String PERVIOUS_ARTICLE_RESULT_SELECTED_POSITION_ARRAY = "perviousArticleResultSelectedPositionArray";
    public static final String REQUEST = "Request";
    public static final String FILTER = "Filter";
    public static final String LOAD_MORE = "Load More";
    public static final String CHAT_QUERY = "Chat Query";
    public static final String RESULTS_SELECTED = "Results Selected";
    public static final String NO_RESULT_SELECTED = "No Result Selected";
    public static final String VERSION = "Version";
    public static final String INFORMATION = "Information";
    public static final String MESSAGE = "message";
    public static final String SUGGEST = "Suggest";
    private static AdvSearchLogger advSearchLogger;
    private Logger logger;
    private static Logger advSearchErrorLogger;
    
    public AdvSearchLogger() {
        this.logger = Logger.getLogger("AdvSearchLogger");
    }
    
    public static synchronized AdvSearchLogger getInstance() {
        if (AdvSearchLogger.advSearchLogger == null) {
            AdvSearchLogger.advSearchLogger = new AdvSearchLogger();
        }
        return AdvSearchLogger.advSearchLogger;
    }
    
    public void printQueryRequest(final Properties searchStringProps, final String searchType, final JSONObject resultJsonObj) throws JSONException {
        final JSONObject jObj = new JSONObject();
        final String query = searchStringProps.getProperty("q");
        if (this.isNotEmpty(query)) {
            jObj.put("q", (Object)query);
        }
        final long loginID = Long.parseLong(String.valueOf(((Hashtable<K, Object>)searchStringProps).get("LOGIN_ID")));
        String requestId = searchStringProps.getProperty("id");
        requestId = loginID + "," + requestId;
        jObj.put("id", (Object)requestId);
        final String role = DMUserHandler.getRoleForUser(loginID);
        jObj.put("role", (Object)role);
        jObj.put("type", (Object)searchType);
        final String source = searchStringProps.getProperty("src");
        final String selectedTab = searchStringProps.getProperty("selectedTab");
        if (this.isNotEmpty(selectedTab)) {
            jObj.put("selectedTab", (Object)selectedTab);
        }
        final String selectedTreeElement = searchStringProps.getProperty("selectedTreeElem");
        if (this.isNotEmpty(selectedTreeElement)) {
            jObj.put("selectedTreeElem", (Object)selectedTreeElement);
        }
        final JSONArray suggestData = resultJsonObj.getJSONArray("suggestdata");
        if (suggestData.length() > 0) {
            jObj.put("suggestdata", (Object)suggestData);
        }
        final JSONArray facetData = resultJsonObj.getJSONArray("facetdata");
        if (facetData.length() > 0) {
            jObj.put("facetdata", (Object)facetData);
        }
        final String selectedFilterTab = searchStringProps.getProperty("category");
        if (this.isNotEmpty(selectedFilterTab)) {
            jObj.put("selectedTabFilter", (Object)selectedFilterTab);
        }
        final String isTriggerFromMenu = searchStringProps.getProperty("trigger");
        if (this.isNotEmpty(isTriggerFromMenu)) {
            jObj.put("trigger", (Object)isTriggerFromMenu);
        }
        final int page = Integer.parseInt(searchStringProps.getProperty("page"));
        jObj.put("page", page);
        jObj.put("total", resultJsonObj.get("total"));
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String licenseType = licenseProvider.getLicenseType();
        jObj.put("licenseType", (Object)licenseType);
        String msg = null;
        if (page <= 1) {
            if (selectedFilterTab.trim().length() > 1) {
                msg = "Filter";
                this.removeUnwantedKeys(jObj);
                jObj.remove("page");
            }
            else {
                msg = "Request";
                jObj.remove("selectedTabFilter");
            }
        }
        else {
            msg = "Load More";
            this.removeUnwantedKeys(jObj);
            jObj.remove("selectedTabFilter");
        }
        this.printLog(jObj, msg);
    }
    
    private void removeUnwantedKeys(final JSONObject jObj) {
        jObj.remove("q");
        jObj.remove("licenseType");
        jObj.remove("role");
        jObj.remove("selectedTab");
        jObj.remove("selectedTreeElem");
        jObj.remove("suggestdata");
        jObj.remove("facetdata");
        jObj.remove("total");
        jObj.remove("trigger");
    }
    
    private boolean isNotEmpty(final String value) {
        return value != null && !value.isEmpty();
    }
    
    public JSONObject printPreviousResultSelected(final Properties properties) throws JSONException {
        final JSONObject jObj = new JSONObject();
        final String searchQuery = properties.getProperty("perviousSearchquery");
        if (!this.isNotEmpty(searchQuery)) {
            return jObj;
        }
        String featureResultSelectedArray = properties.getProperty("perviousFeatureResultSelectedArray");
        final String featureResultSelectedPositionArray = properties.getProperty("perviousFeatureResultSelectedPositionArray");
        String articleResultSelectedArray = properties.getProperty("perviousArticleResultSelectedArray");
        final String articleResultSelectedPositionArray = properties.getProperty("perviousArticleResultSelectedPositionArray");
        final String selectedFilterTab = properties.getProperty("selectedTabFilter");
        if (this.isNotEmpty(selectedFilterTab)) {
            jObj.put("selectedTabFilter", (Object)selectedFilterTab);
        }
        if (!this.isNotEmpty(featureResultSelectedArray)) {
            featureResultSelectedArray = "0";
        }
        if (!this.isNotEmpty(articleResultSelectedArray)) {
            articleResultSelectedArray = "0";
        }
        jObj.put("q", (Object)searchQuery);
        final String requestId = this.getRequestId(properties);
        jObj.put("id", (Object)requestId);
        jObj.put("settings", (Object)featureResultSelectedArray);
        jObj.put("sett", (Object)featureResultSelectedPositionArray);
        jObj.put("documents", (Object)articleResultSelectedArray);
        jObj.put("docs", (Object)articleResultSelectedPositionArray);
        if (!searchQuery.isEmpty()) {
            final String msg = "Results Selected";
            if ("0".equals(featureResultSelectedArray)) {
                jObj.remove("settings");
                jObj.remove("sett");
            }
            if ("0".equals(articleResultSelectedArray)) {
                jObj.remove("documents");
                jObj.remove("docs");
            }
            if ("0".equals(featureResultSelectedArray) && "0".equals(articleResultSelectedArray)) {
                this.logger.log(Level.FINE, "# " + msg + " : " + jObj.toString());
            }
            else {
                this.updateJsonValueToEncrypt(jObj, "settings");
                this.printLog(jObj, msg);
            }
        }
        return jObj;
    }
    
    public Map printPreviousResultSelected(final MultivaluedMap map) throws JSONException {
        final HashMap hashMap = new HashMap();
        final String searchQuery = (String)map.getFirst((Object)"perviousSearchquery");
        if (!this.isNotEmpty(searchQuery)) {
            return hashMap;
        }
        String featureResultSelectedArray = (String)map.get((Object)"perviousFeatureResultSelectedArray");
        final String featureResultSelectedPositionArray = (String)map.get((Object)"perviousFeatureResultSelectedPositionArray");
        String articleResultSelectedArray = (String)map.get((Object)"perviousArticleResultSelectedArray");
        final String articleResultSelectedPositionArray = (String)map.get((Object)"perviousArticleResultSelectedPositionArray");
        final String selectedFilterTab = (String)map.get((Object)"selectedTabFilter");
        if (this.isNotEmpty(selectedFilterTab)) {
            hashMap.put("selectedTabFilter", selectedFilterTab);
        }
        if (!this.isNotEmpty(featureResultSelectedArray)) {
            featureResultSelectedArray = "0";
        }
        if (!this.isNotEmpty(articleResultSelectedArray)) {
            articleResultSelectedArray = "0";
        }
        hashMap.put("q", searchQuery);
        final String requestId = this.getRequestId(map);
        hashMap.put("id", requestId);
        hashMap.put("settings", featureResultSelectedArray);
        hashMap.put("sett", featureResultSelectedPositionArray);
        hashMap.put("documents", articleResultSelectedArray);
        hashMap.put("docs", articleResultSelectedPositionArray);
        if (!searchQuery.isEmpty()) {
            final String msg = "Results Selected";
            if ("0".equals(featureResultSelectedArray)) {
                hashMap.remove("settings");
                hashMap.remove("sett");
            }
            if ("0".equals(articleResultSelectedArray)) {
                hashMap.remove("documents");
                hashMap.remove("docs");
            }
            if ("0".equals(featureResultSelectedArray) && "0".equals(articleResultSelectedArray)) {
                this.logger.log(Level.FINE, "# " + msg + " : " + new JSONObject((Map)hashMap).toString());
            }
            else {
                this.updateJsonValueToEncrypt(new JSONObject((Map)hashMap), "settings");
                this.printLog(new JSONObject((Map)hashMap), msg);
            }
        }
        return hashMap;
    }
    
    private void updateJsonValueToEncrypt(final JSONObject jObj, final String key) throws JSONException {
        if (jObj.has(key)) {
            final String value = String.valueOf(jObj.get(key));
            jObj.put(key, (Object)this.encryptData(value));
        }
    }
    
    private String encryptData(String value) {
        value = value.replaceAll("-and-", "&");
        final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
        if (searchTrackAPI != null) {
            value = searchTrackAPI.encryptString(value);
        }
        return value;
    }
    
    public JSONObject printChatQuery(final Properties properties) throws JSONException {
        final JSONObject jObj = new JSONObject();
        final String previousSearchQuery = properties.getProperty("perviousSearchquery");
        final String searchType = properties.getProperty("type");
        final String requestId = this.getRequestId(properties);
        jObj.put("q", (Object)previousSearchQuery);
        jObj.put("id", (Object)requestId);
        jObj.put("type", (Object)searchType);
        this.printLog(jObj, "Chat Query");
        return jObj;
    }
    
    public JSONObject printSuggestionOptions(final Properties properties) throws JSONException {
        final JSONObject jObj = new JSONObject();
        final String suggestData = properties.getProperty("suggestdata");
        final String requestId = this.getRequestId(properties);
        jObj.put("id", (Object)requestId);
        jObj.put("suggestdata", (Object)suggestData);
        this.printLog(jObj, "Suggest");
        return jObj;
    }
    
    public void printVersion(final String message) throws Exception {
        final JSONObject mainIndexDirJson = CompleteSearchUtil.getMainIndexDirJson();
        mainIndexDirJson.put("message", (Object)message);
        this.printLog(mainIndexDirJson, "Version");
    }
    
    public void printProductInfo(final String message) throws Exception {
        final JSONObject json = AdvSearchUtil.getInstance().getSearchProductSpecificHandler().getProductInfo();
        json.put("message", (Object)message);
        this.printLog(json, "Information");
    }
    
    private String getRequestId(final Properties properties) {
        return properties.getProperty("LOGIN_ID") + "," + properties.getProperty("id");
    }
    
    private String getRequestId(final MultivaluedMap map) {
        return map.get((Object)"LOGIN_ID") + "," + map.get((Object)"id");
    }
    
    private void printLog(final JSONObject jObj, final String command) {
        this.logger.log(Level.INFO, "# " + command + " : " + jObj.toString());
    }
    
    static {
        AdvSearchLogger.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
