package com.me.devicemanagement.framework.server.search.api;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.TreeMap;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import java.util.logging.Logger;

public class AdvSearchAPIDataCollector
{
    public static final String SERVER_HOME;
    private Logger logger;
    private static Logger advSearchErrorLogger;
    
    public AdvSearchAPIDataCollector() {
        this.logger = Logger.getLogger(AdvSearchAPIDataCollector.class.getName());
    }
    
    public JSONObject getSearchResults(final APIRequest apiRequest) throws Exception {
        JSONObject searchDataResults = new JSONObject();
        final HttpServletRequest request = apiRequest.getHttpServletRequest();
        final AdvSearchUtil advSearchUtil = AdvSearchUtil.getInstance();
        try {
            final boolean verifyAuthToken = advSearchUtil.verifyAuthToken(request);
            if (!verifyAuthToken) {
                searchDataResults.put("error_code", 100802);
                searchDataResults.put("error", (Object)"Authorization Failed to AdvSearch Failed");
                searchDataResults.put("error_description", (Object)"Authorization Failed to AdvSearch Failed");
                return searchDataResults;
            }
            final String authKey = request.getHeader("Authorization");
            final TreeMap<String, Object> rolesFromAuthKeyJSONObject = APIUtil.getInstance().getRolesListFromAuthKey(authKey, "301");
            final Long loginId = Long.valueOf(String.valueOf(rolesFromAuthKeyJSONObject.get("LOGIN_ID")));
            final TreeMap<String, Long> authorizedRoleMap = rolesFromAuthKeyJSONObject.get("ROLES_LIST_FOR_AUTHKEY");
            final Properties searchQueryProp = advSearchUtil.getPropertiesFromRequestSearchParameter(request);
            searchDataResults = advSearchUtil.getSearchResults(searchQueryProp, loginId, authorizedRoleMap);
        }
        catch (final Exception ex) {
            AdvSearchAPIDataCollector.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchAPIDataCollector : Exception occurred - getSearchResults() :  ", ex);
            searchDataResults.put("error_code", 8002);
            searchDataResults.put("error", (Object)"Search Failed");
            searchDataResults.put("error_description", (Object)"Search Failed");
        }
        if (searchDataResults.has("error_code") || searchDataResults.has("error_description")) {
            final APIUtil apiUtil = APIUtil.getInstance();
            apiUtil.setErrorCode(String.valueOf(searchDataResults.get("error_code")));
            apiUtil.setErrorMessage(String.valueOf(searchDataResults.get("error_description")));
        }
        return searchDataResults;
    }
    
    static {
        SERVER_HOME = System.getProperty("server.home");
        AdvSearchAPIDataCollector.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
