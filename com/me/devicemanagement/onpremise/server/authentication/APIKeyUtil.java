package com.me.devicemanagement.onpremise.server.authentication;

import java.util.ArrayList;
import java.util.UUID;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Set;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class APIKeyUtil
{
    private Logger logger;
    public static final int SUCCESS = 100;
    public static final int KEY_EXISTS = 101;
    public static final int UNKNOWN_ID = 102;
    public static final int KEY_EXPIRED = 103;
    public static final int INVALID_SERVICE = 104;
    public static final int INVALID_SCOPE = 105;
    public static final long DEFAULT_VALIDITY = 15552000000L;
    public static final String LOGGED_IN_USER = "logged_in_user";
    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";
    public static final String STATUS_ID = "status_id";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILURE_STATUS = "failure";
    public static final String SCOPE_IDS = "scope_ids";
    public static final String IS_UPDATE = "is_update";
    
    public APIKeyUtil() {
        this.logger = Logger.getLogger(APIKeyUtil.class.getSimpleName());
    }
    
    public static APIKeyUtil getNewInstance() {
        return new APIKeyUtil();
    }
    
    public JSONObject createAPIKey(final JSONObject properties) {
        try {
            final JSONObject result = new JSONObject();
            final List<Long> scopeIds = APIKeyScopeUtil.getNewInstance().convertLongJSONArrayTOList(properties.getJSONArray("scope_ids"));
            final Long userId = Long.valueOf(String.valueOf(properties.get("USER_ID")));
            final Long createdBy = Long.valueOf(String.valueOf(properties.get("logged_in_user")));
            final Long integServiceId = Long.valueOf(String.valueOf(properties.get("SERVICE_ID")));
            final Long validity = this.optLongForUVH(properties, "VALIDITY", System.currentTimeMillis() + 15552000000L);
            if (!new IntegrationServiceUtil().isValidIntegrationServiceId(integServiceId)) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid integration service id");
                result.put("status_id", 104);
                return result;
            }
            if (!new APIKeyScopeUtil().areValidScopeIds(scopeIds)) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"input contains invalid scope ids");
                result.put("status_id", 105);
                return result;
            }
            if (!this.isScopesValidForUser(userId, scopeIds)) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid scope for user");
                result.put("status_id", 105);
                return result;
            }
            final DataObject existingDO = DataAccess.get("APIKeyInfo", new Criteria(Column.getColumn("APIKeyInfo", "USER_ID"), (Object)userId, 0).and(new Criteria(Column.getColumn("APIKeyInfo", "SERVICE_ID"), (Object)integServiceId, 0)));
            if (existingDO.isEmpty()) {
                final Row row = new Row("APIKeyInfo");
                row.set("SERVICE_ID", (Object)integServiceId);
                row.set("API_KEY", (Object)this.generateAPIKey());
                row.set("USER_ID", (Object)userId);
                row.set("CREATED_BY", (Object)createdBy);
                row.set("CREATION_TIME", (Object)System.currentTimeMillis());
                row.set("MODIFIED_BY", (Object)createdBy);
                row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                row.set("VALIDITY", (Object)validity);
                final DataObject apiKeyDO = DataAccess.constructDataObject();
                apiKeyDO.addRow(row);
                final DataObject resultDO = DataAccess.update(apiKeyDO);
                result.put("status", (Object)"success");
                result.put("status_id", 100);
                final Long apiKeyId = Long.valueOf(String.valueOf(resultDO.getFirstRow("APIKeyInfo").get("API_KEY_ID")));
                result.put("API_KEY", resultDO.getFirstRow("APIKeyInfo").get("API_KEY"));
                result.put("API_KEY_ID", (Object)String.valueOf(apiKeyId));
                properties.put("API_KEY_ID", (Object)apiKeyId);
                this.updateApiKeyScopeRelation(properties);
            }
            else {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Key already exists for the provided data");
                result.put("status_id", 101);
            }
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in createAPIKey()...", e);
            return null;
        }
    }
    
    public boolean updateApiKeyScopeRelation(final JSONObject properties) {
        try {
            final Long apiKeyId = properties.getLong("API_KEY_ID");
            final List<Long> scopeIds = APIKeyScopeUtil.getNewInstance().convertLongJSONArrayTOList(properties.getJSONArray("scope_ids"));
            final Long loggedInUser = properties.getLong("logged_in_user");
            final Boolean isUpdate = properties.optBoolean("is_update", false);
            if (isUpdate) {
                final DataObject apiKeyDO = SyMUtil.getPersistence().get("APIKeyInfo", new Criteria(Column.getColumn("APIKeyInfo", "API_KEY_ID"), (Object)apiKeyId, 0));
                if (apiKeyDO.isEmpty()) {
                    return false;
                }
                final Row row = apiKeyDO.getFirstRow("APIKeyInfo");
                row.set("MODIFIED_BY", (Object)loggedInUser);
                row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                apiKeyDO.updateRow(row);
                SyMUtil.getPersistence().update(apiKeyDO);
            }
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("APIKeyScopeRel");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScopeRel", "API_KEY_ID"), (Object)apiKeyId, 0));
            DataAccess.delete(deleteQuery);
            final DataObject relDO = SyMUtil.getPersistence().constructDataObject();
            for (final Long scope : scopeIds) {
                final Row row2 = new Row("APIKeyScopeRel");
                row2.set("API_KEY_ID", (Object)apiKeyId);
                row2.set("SCOPE_ID", (Object)scope);
                relDO.addRow(row2);
            }
            SyMUtil.getPersistence().add(relDO);
            return true;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "exception in updateApiKeyScopeRelation", e);
            return false;
        }
    }
    
    private boolean isScopesValidForUser(final Long userId, final List<Long> scopeIds) throws Exception {
        final Set<Long> scopeRoles = new HashSet<Long>(new APIKeyScopeUtil().getRolesForScopes(scopeIds));
        final Set<Long> userRoles = new HashSet<Long>(DMUserHandler.getRoleIdsFromRoleName(DMUserHandler.getRoleNameListForLoginUser(DMUserHandler.getLoginIdForUserId(userId))));
        return userRoles.containsAll(scopeRoles);
    }
    
    public boolean invalidateAPIKey(final JSONObject properties) {
        try {
            final Long apiKeyId = Long.valueOf(String.valueOf(properties.get("API_KEY_ID")));
            final Long modifiedBy = Long.valueOf(String.valueOf(properties.get("logged_in_user")));
            final DataObject existingDO = DataAccess.get("APIKeyInfo", new Criteria(Column.getColumn("APIKeyInfo", "API_KEY_ID"), (Object)apiKeyId, 0));
            if (existingDO.isEmpty()) {
                return false;
            }
            final Row row = existingDO.getFirstRow("APIKeyInfo");
            row.set("MODIFIED_BY", (Object)modifiedBy);
            row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            row.set("VALIDITY", (Object)0);
            existingDO.updateRow(row);
            DataAccess.update(existingDO);
            return true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in invalidateAPIKey()", e);
            return false;
        }
    }
    
    public JSONObject regenerateAPIKey(final JSONObject properties) {
        try {
            final JSONObject result = new JSONObject();
            final Long apiKeyId = Long.valueOf(String.valueOf(properties.get("API_KEY_ID")));
            if (apiKeyId == 0L) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Unknown ID");
                result.put("status_id", 102);
                return result;
            }
            final DataObject existingDO = DataAccess.get("APIKeyInfo", new Criteria(Column.getColumn("APIKeyInfo", "API_KEY_ID"), (Object)apiKeyId, 0));
            if (existingDO.isEmpty()) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Unknown ID");
                result.put("status_id", 102);
                return result;
            }
            final Row row = existingDO.getFirstRow("APIKeyInfo");
            final Long modifiedBy = Long.valueOf(String.valueOf(properties.get("logged_in_user")));
            final Long validity = this.optLongForUVH(properties, "VALIDITY", System.currentTimeMillis() + 15552000000L);
            row.set("API_KEY", (Object)this.generateAPIKey());
            row.set("MODIFIED_BY", (Object)modifiedBy);
            row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            row.set("VALIDITY", (Object)validity);
            existingDO.updateRow(row);
            final DataObject resultDO = DataAccess.update(existingDO);
            result.put("status", (Object)"success");
            result.put("status_id", 100);
            result.put("API_KEY", resultDO.getFirstRow("APIKeyInfo").get("API_KEY"));
            result.put("API_KEY_ID", resultDO.getFirstRow("APIKeyInfo").get("API_KEY_ID"));
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in regenerateAPIKey()", e);
            return null;
        }
    }
    
    public JSONObject getUserDetails(final JSONObject properties) throws Exception {
        final JSONObject result = new JSONObject();
        final String apiKey = String.valueOf(properties.get("API_KEY"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyInfo"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyInfo", "API_KEY"), (Object)apiKey, 0));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "SERVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "VALIDITY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "API_KEY_ID"));
        final DataObject apiKeyDO = SyMUtil.getPersistence().get(selectQuery);
        if (!apiKeyDO.isEmpty()) {
            final Row row = apiKeyDO.getFirstRow("APIKeyInfo");
            final Long validity = Long.valueOf(String.valueOf(row.get("VALIDITY")));
            if (validity > System.currentTimeMillis()) {
                result.put("SERVICE_ID", row.get("SERVICE_ID"));
                result.put("USER_ID", row.get("USER_ID"));
                result.put("API_KEY_ID", (Object)String.valueOf(row.get("API_KEY_ID")));
                result.put("scope_ids", (Collection)this.getScopeIdsForAPIKeyId(Long.valueOf(String.valueOf(row.get("API_KEY_ID")))));
                result.put("status", (Object)"success");
                result.put("status_id", 100);
            }
            else {
                result.put("status", (Object)"failure");
                result.put("status_id", 103);
                result.put("description", (Object)"API Key has expired, Please generate a new Key");
            }
        }
        else {
            result.put("status", (Object)"failure");
            result.put("status_id", 102);
            result.put("description", (Object)"Unknown api key");
        }
        return result;
    }
    
    private String generateAPIKey() {
        return UUID.randomUUID().toString();
    }
    
    private Long optLongForUVH(final JSONObject jsonObject, final String key, final Long defaultValue) {
        if (defaultValue != null) {
            return Long.parseLong(jsonObject.optString(key, String.valueOf(defaultValue)));
        }
        return Long.parseLong(jsonObject.optString(key, "0"));
    }
    
    public boolean deleteAPIKey(final JSONObject properties) {
        try {
            final Long apiKeyId = Long.valueOf(String.valueOf(properties.get("API_KEY_ID")));
            final DataObject existingDO = DataAccess.get("APIKeyInfo", new Criteria(Column.getColumn("APIKeyInfo", "API_KEY_ID"), (Object)apiKeyId, 0));
            if (existingDO.isEmpty()) {
                return false;
            }
            final Row row = existingDO.getFirstRow("APIKeyInfo");
            DataAccess.delete(row);
            return true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in deleteAPIKey()", e);
            return false;
        }
    }
    
    public List<Long> getScopeIdsForAPIKeyId(final Long apiKeyId) {
        try {
            final List<Long> scopeIds = new ArrayList<Long>();
            final DataObject keyScopeRelDO = SyMUtil.getPersistence().get("APIKeyScopeRel", new Criteria(Column.getColumn("APIKeyScopeRel", "API_KEY_ID"), (Object)apiKeyId, 0));
            final Iterator<Row> rows = keyScopeRelDO.getRows("APIKeyScopeRel");
            while (rows.hasNext()) {
                final Row row = rows.next();
                scopeIds.add(Long.valueOf(String.valueOf(row.get("SCOPE_ID"))));
            }
            return scopeIds;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in getScopeIdsForAPIKeyId()", e);
            return null;
        }
    }
}
