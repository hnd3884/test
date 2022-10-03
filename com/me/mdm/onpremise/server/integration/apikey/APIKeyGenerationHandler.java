package com.me.mdm.onpremise.server.integration.apikey;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyScopeUtil;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.onpremise.server.integration.IntegrationProductUtil;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import org.json.JSONArray;
import com.me.devicemanagement.onpremise.server.authentication.IntegrationServiceUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;

public class APIKeyGenerationHandler
{
    private static APIKeyGenerationHandler apiHandler;
    private static final String SCOPE_READ = "SCOPE_READ";
    private static final String SCOPE_WRITE = "SCOPE_WRITE";
    private static final String SCOPE_READ_NAME = "SCOPE_READ_NAME";
    private static final String SCOPE_WRITE_NAME = "SCOPE_WRITE_NAME";
    public static final String IS_PREDEFINED_APP = "IS_PREDEFINED_APP";
    
    public static APIKeyGenerationHandler getInstance() {
        if (APIKeyGenerationHandler.apiHandler == null) {
            APIKeyGenerationHandler.apiHandler = new APIKeyGenerationHandler();
        }
        return APIKeyGenerationHandler.apiHandler;
    }
    
    public JSONObject handleAPIKeyGeneration(final JSONObject apiKeyJSON) {
        JSONObject apiKeyResultJSON = null;
        try {
            final Long createdUserId = MDMUtil.getInstance().getLoggedInUserID();
            apiKeyJSON.put("USER_ID", (Object)createdUserId);
            apiKeyJSON.put("logged_in_user", (Object)createdUserId);
            final JSONObject integJSON = IntegrationServiceUtil.getNewInstance().addOrUpdateIntegrationService(apiKeyJSON);
            if (integJSON.optString("status").equals("success")) {
                final Long integServiceID = integJSON.getLong("SERVICE_ID");
                apiKeyJSON.put("SERVICE_ID", (Object)integServiceID);
                final JSONArray scopeArray = new JSONArray(apiKeyJSON.optString("SCOPE_ID"));
                apiKeyJSON.put("scope_ids", (Object)scopeArray);
                apiKeyResultJSON = APIKeyUtil.getNewInstance().createAPIKey(apiKeyJSON);
                final Long productId = apiKeyJSON.optLong("PRODUCT_ID", -1L);
                if (productId != -1L) {
                    IntegrationProductUtil.getNewInstance().linkProductAndService(productId, integServiceID);
                }
            }
            else {
                apiKeyResultJSON = integJSON;
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return apiKeyResultJSON;
    }
    
    public JSONObject handleAPIScopeUpdate(final JSONObject apiKeyJSON) {
        JSONObject apiKeyResultJSON = null;
        try {
            final Long createdUserId = MDMUtil.getInstance().getLoggedInUserID();
            apiKeyJSON.put("USER_ID", (Object)createdUserId);
            apiKeyJSON.put("logged_in_user", (Object)createdUserId);
            final JSONObject integJSON = IntegrationServiceUtil.getNewInstance().addOrUpdateIntegrationService(apiKeyJSON);
            if (integJSON.optString("status").equals("success")) {
                final Long integServiceID = integJSON.getLong("SERVICE_ID");
                apiKeyJSON.put("SERVICE_ID", (Object)integServiceID);
                final JSONArray scopeArray = new JSONArray(apiKeyJSON.optString("SCOPE_ID"));
                apiKeyJSON.put("scope_ids", (Object)scopeArray);
                apiKeyJSON.put("is_update", true);
                APIKeyUtil.getNewInstance().updateApiKeyScopeRelation(apiKeyJSON);
            }
            apiKeyResultJSON = integJSON;
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return apiKeyResultJSON;
    }
    
    public boolean handleAPIServiceNameUpdate(final JSONObject apiScopeNameJSON) {
        boolean isServiceNameUpdated = false;
        try {
            final Long modifiedUserId = MDMUtil.getInstance().getLoggedInUserID();
            apiScopeNameJSON.put("logged_in_user", (Object)modifiedUserId);
            final JSONObject apiScopeNameResultJSON = IntegrationServiceUtil.getNewInstance().modifyIntegrationService(apiScopeNameJSON);
            isServiceNameUpdated = apiScopeNameResultJSON.optString("status").equals("success");
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isServiceNameUpdated;
    }
    
    public boolean handleAPIKeyRemove(final JSONObject apiKeyJSON) {
        boolean isApiKeyRemoved = false;
        try {
            isApiKeyRemoved = APIKeyUtil.getNewInstance().deleteAPIKey(apiKeyJSON);
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isApiKeyRemoved;
    }
    
    public boolean handleAPIKeyRevoke(final JSONObject apiKeyJSON) {
        boolean isApiKeyRevoked = false;
        try {
            final Long modifiedUserId = MDMUtil.getInstance().getLoggedInUserID();
            apiKeyJSON.put("logged_in_user", (Object)modifiedUserId);
            isApiKeyRevoked = APIKeyUtil.getNewInstance().invalidateAPIKey(apiKeyJSON);
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isApiKeyRevoked;
    }
    
    public boolean handleAPIKeyRegenerate(final JSONObject apiKeyJSON) {
        boolean isApiKeyRegenerated = false;
        try {
            final Long modifiedUserId = MDMUtil.getInstance().getLoggedInUserID();
            apiKeyJSON.put("logged_in_user", (Object)modifiedUserId);
            final JSONObject apiKeyInfoJSON = APIKeyUtil.getNewInstance().regenerateAPIKey(apiKeyJSON);
            final int statusId = apiKeyInfoJSON.optInt("status_id");
            isApiKeyRegenerated = (statusId == 100);
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isApiKeyRegenerated;
    }
    
    public TreeMap getParsedScopeMap(final List scopeList) {
        final TreeMap parsedScopeMap = new TreeMap();
        HashMap parsedScopeRWMap = null;
        String parsedScopeName = "";
        boolean isWrite = false;
        String scopeName = "";
        Long scopeId = -1L;
        try {
            for (int i = 0; i < scopeList.size(); ++i) {
                final HashMap scopeMap = scopeList.get(i);
                scopeName = scopeMap.get("SCOPE_NAME");
                scopeId = scopeMap.get("SCOPE_ID");
                isWrite = scopeName.contains("Write");
                final int scopeIndex = scopeName.indexOf("(");
                if (scopeIndex != -1) {
                    parsedScopeName = scopeName.substring(0, scopeIndex).trim();
                }
                else {
                    parsedScopeName = scopeName.trim();
                }
                if (!parsedScopeMap.containsKey(parsedScopeName)) {
                    parsedScopeRWMap = new HashMap();
                }
                else {
                    parsedScopeRWMap = parsedScopeMap.get(parsedScopeName);
                }
                if (isWrite) {
                    parsedScopeRWMap.put("SCOPE_WRITE", scopeId);
                    parsedScopeRWMap.put("SCOPE_WRITE_NAME", scopeName);
                }
                else {
                    parsedScopeRWMap.put("SCOPE_READ", scopeId);
                    parsedScopeRWMap.put("SCOPE_READ_NAME", scopeName);
                }
                parsedScopeMap.put(parsedScopeName, parsedScopeRWMap);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parsedScopeMap;
    }
    
    public JSONArray getAllPermissions() {
        JSONArray scopeArray = null;
        boolean isWrite = false;
        String scopeName = "";
        Long scopeId = -1L;
        try {
            final List scopeList = APIKeyScopeUtil.getNewInstance().getAllAPIScopes();
            scopeArray = new JSONArray();
            for (int i = 0; i < scopeList.size(); ++i) {
                final HashMap scopeMap = scopeList.get(i);
                scopeName = scopeMap.get("SCOPE_NAME");
                scopeId = scopeMap.get("SCOPE_ID");
                isWrite = scopeName.contains("Write");
                if (isWrite) {
                    scopeArray.put((Object)scopeId);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scopeArray;
    }
    
    public ArrayList getParsedScopeListAPIkey(final Long apiKeyId) {
        String parsedScopeName = "";
        Long scopeId = null;
        String scopeName = "";
        final ArrayList scopeListforAPIKey = new ArrayList();
        boolean isWrite = false;
        try {
            final List scopeList = APIKeyScopeUtil.getNewInstance().getAllAPIScopes();
            final List<Long> scopeIds = APIKeyUtil.getNewInstance().getScopeIdsForAPIKeyId(apiKeyId);
            for (int i = 0; i < scopeList.size(); ++i) {
                final HashMap scopeMap = scopeList.get(i);
                scopeId = scopeMap.get("SCOPE_ID");
                scopeName = scopeMap.get("SCOPE_NAME");
                if (scopeIds.contains(scopeId)) {
                    final int scopeIndex = scopeName.indexOf("(");
                    if (scopeIndex != -1) {
                        parsedScopeName = scopeName.substring(0, scopeIndex).trim();
                    }
                    else {
                        parsedScopeName = scopeName.trim();
                    }
                    isWrite = scopeName.contains("Write");
                    scopeMap.put("IS_WRITE", isWrite);
                    scopeMap.put("PARSED_SCOPE_NAME", parsedScopeName);
                    scopeListforAPIKey.add(scopeMap);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scopeListforAPIKey;
    }
    
    public boolean deleteIntegrationService(final JSONObject jsonObject) {
        try {
            final Long serviceId = Long.valueOf(String.valueOf(jsonObject.get("SERVICE_ID")));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationService"));
            selectQuery.addJoin(new Join("IntegrationService", "APIKeyInfo", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "SERVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "API_KEY_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)serviceId, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject != null) {
                Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.INFO, "deleting the integration service dataObject {0}", dataObject);
                final Row apiKeyInfoRow = dataObject.getRow("APIKeyInfo");
                dataObject.deleteRow(apiKeyInfoRow);
                final Row integrationServiceRow = dataObject.getRow("IntegrationService");
                dataObject.deleteRow(integrationServiceRow);
                MDMUtil.getPersistence().update(dataObject);
                return true;
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(APIKeyGenerationHandler.class.getName()).log(Level.SEVERE, "Exception while deleting Integration Service", ex);
        }
        return false;
    }
    
    static {
        APIKeyGenerationHandler.apiHandler = null;
    }
}
