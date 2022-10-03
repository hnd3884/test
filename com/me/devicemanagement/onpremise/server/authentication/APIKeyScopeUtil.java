package com.me.devicemanagement.onpremise.server.authentication;

import java.sql.SQLException;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class APIKeyScopeUtil
{
    Logger logger;
    public static final int SUCCESS = 100;
    public static final int SCOPE_EXISTS = 101;
    public static final int INVALID_INPUT = 103;
    public static final int DELETE_FAILED = 104;
    public static final String LOGGED_IN_USER = "logged_in_user";
    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";
    public static final String STATUS_ID = "status_id";
    public static final String SUCCESS_STATUS = "success";
    public static final String FAILURE_STATUS = "failure";
    public static final int SCOPE_BEING_USED = 105;
    public static final int UNKNOWN_SCOPE = 106;
    public static final String ROLES = "roles";
    
    public APIKeyScopeUtil() {
        this.logger = Logger.getLogger(APIKeyScopeUtil.class.getSimpleName());
    }
    
    public static APIKeyScopeUtil getNewInstance() {
        return new APIKeyScopeUtil();
    }
    
    public JSONObject addOrUpdateAPIKeyScope(final JSONObject properties) {
        try {
            if (!properties.has("SCOPE_ID")) {
                return this.createAPIKeyScope(properties);
            }
            return this.modifyApiKeyScope(properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addOrUpdateAPIKeyScope()", e);
            return null;
        }
    }
    
    public JSONObject createAPIKeyScope(final JSONObject properties) {
        final JSONObject result = new JSONObject();
        try {
            final String scopeName = String.valueOf(properties.get("SCOPE_NAME"));
            final String description = properties.optString("DESCRIPTION", "");
            final Long loggedInUser = properties.getLong("logged_in_user");
            if (scopeName.length() == 0) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid Scope Name");
                result.put("status_id", 103);
                return result;
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_NAME"), (Object)scopeName, 0));
            selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID"));
            final DataObject existingDO = SyMUtil.getPersistence().get(selectQuery);
            if (existingDO.isEmpty()) {
                final Row row = new Row("APIKeyScope");
                row.set("SCOPE_NAME", (Object)scopeName);
                row.set("DESCRIPTION", (Object)description);
                row.set("CREATED_BY", (Object)loggedInUser);
                row.set("CREATION_TIME", (Object)System.currentTimeMillis());
                row.set("MODIFIED_BY", (Object)loggedInUser);
                row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                final DataObject apiKeyScopeDO = DataAccess.constructDataObject();
                apiKeyScopeDO.addRow(row);
                final DataObject resultDO = DataAccess.update(apiKeyScopeDO);
                result.put("status", (Object)"success");
                result.put("status_id", 100);
                final Long scope_id = Long.valueOf(String.valueOf(resultDO.getFirstRow("APIKeyScope").get("SCOPE_ID")));
                result.put("SCOPE_ID", (Object)scope_id);
                if (properties.has("roles")) {
                    properties.put("SCOPE_ID", (Object)scope_id);
                    this.setRolesForAPIScope(properties);
                }
            }
            else {
                result.put("status", (Object)"success");
                result.put("status_id", 100);
                final Long scope_id2 = Long.valueOf(String.valueOf(existingDO.getFirstRow("APIKeyScope").get("SCOPE_ID")));
                result.put("SCOPE_ID", (Object)scope_id2);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in createAPIKeyScope()", e);
        }
        return result;
    }
    
    public JSONObject modifyApiKeyScope(final JSONObject properties) {
        final JSONObject result = new JSONObject();
        try {
            final String scopeName = String.valueOf(properties.get("SCOPE_NAME"));
            final String description = properties.optString("DESCRIPTION", "");
            final Long loggedInUser = properties.getLong("logged_in_user");
            if (scopeName.length() == 0) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid Scope Name");
                result.put("status_id", 103);
                return result;
            }
            final Long scopeId = properties.optLong("SCOPE_ID", -1L);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeId, 0));
            selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID"));
            final SelectQuery existingNameCheckQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
            existingNameCheckQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeId, 1).and(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_NAME"), (Object)scopeName, 0)));
            existingNameCheckQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID").count());
            final int count = DBUtil.getRecordCount(existingNameCheckQuery);
            if (count > 0) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Scope name already exists");
                result.put("status_id", 101);
            }
            final DataObject existingDO = SyMUtil.getPersistence().get(selectQuery);
            if (existingDO.isEmpty()) {
                result.put("status", (Object)"failure");
                result.put("description", (Object)"Invalid Scope Id");
                result.put("status_id", 103);
                return result;
            }
            final Row row = existingDO.getFirstRow("APIKeyScope");
            row.set("SCOPE_NAME", (Object)scopeName);
            row.set("DESCRIPTION", (Object)description);
            row.set("MODIFIED_BY", (Object)loggedInUser);
            row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
            existingDO.updateRow(row);
            final DataObject resultDO = DataAccess.update(existingDO);
            result.put("status", (Object)"success");
            result.put("status_id", 100);
            final Long scope_id = Long.valueOf(String.valueOf(resultDO.getFirstRow("APIKeyScope").get("SCOPE_ID")));
            result.put("SCOPE_ID", (Object)scope_id);
            if (properties.has("roles")) {
                properties.put("SCOPE_ID", (Object)scope_id);
                this.setRolesForAPIScope(properties);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in modifyApiKeyScope()", e);
        }
        return result;
    }
    
    public int deleteAPIKeyScope(final JSONObject properties) {
        final JSONObject result = new JSONObject();
        try {
            final Long scopeId = properties.getLong("SCOPE_ID");
            if (!this.scopeExists(scopeId)) {
                return 106;
            }
            if (!this.isScopeUsed(scopeId)) {
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("APIKeyScope");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeId, 0));
                SyMUtil.getPersistence().delete(deleteQuery);
                return 100;
            }
            return 105;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in deleteAPIKeyScope()", e);
            return 104;
        }
    }
    
    public boolean scopeExists(final Long scopeId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeId, 0));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID").count());
        final int count = DBUtil.getRecordCount(selectQuery);
        return count == 1;
    }
    
    public boolean isScopeUsed(final Long scopeId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScopeRel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyScopeRel", "SCOPE_ID"), (Object)scopeId, 0));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScopeRel", "SCOPE_ID").count());
        final int count = DBUtil.getRecordCount(selectQuery);
        return count > 0;
    }
    
    private Boolean setRolesForAPIScope(final JSONObject properties) {
        try {
            final Long scopeId = properties.getLong("SCOPE_ID");
            final List<Long> curRoles = this.getRolesForScope(scopeId);
            final List<Long> newRoles = this.convertLongJSONArrayTOList(properties.getJSONArray("roles"));
            final List<Long> rolesToRemove = new ArrayList<Long>(curRoles);
            rolesToRemove.removeAll(newRoles);
            this.removeRolesForScope(scopeId, rolesToRemove);
            newRoles.removeAll(curRoles);
            this.addRolesForScope(scopeId, newRoles);
            return true;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "error occurred in setRolesForAPIScope()", e);
            return false;
        }
    }
    
    private void addRolesForScope(final Long scopeId, final List<Long> rolesToAdd) throws DataAccessException {
        final DataObject newDO = SyMUtil.getPersistence().constructDataObject();
        for (final Long role : rolesToAdd) {
            final Row row = new Row("ScopeRoleRel");
            row.set("SCOPE_ID", (Object)scopeId);
            row.set("AAA_ROLE_ID", (Object)role);
            newDO.addRow(row);
        }
        SyMUtil.getPersistence().update(newDO);
    }
    
    public void removeRolesForScope(final Long scopeId, final List<Long> rolesToRemove) throws DataAccessException {
        if (rolesToRemove != null && rolesToRemove.size() > 0) {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ScopeRoleRel");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("ScopeRoleRel", "AAA_ROLE_ID"), (Object)rolesToRemove.toArray(), 8).and(new Criteria(Column.getColumn("ScopeRoleRel", "SCOPE_ID"), (Object)scopeId, 0)));
            SyMUtil.getPersistence().delete(deleteQuery);
        }
    }
    
    public List<Long> getRolesForScope(final Long scopeId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ScopeRoleRel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ScopeRoleRel", "SCOPE_ID"), (Object)scopeId, 0));
        selectQuery.addSelectColumn(Column.getColumn("ScopeRoleRel", "AAA_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ScopeRoleRel", "SCOPE_ID"));
        final List<Long> rolesList = new ArrayList<Long>();
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("ScopeRoleRel");
            while (rows.hasNext()) {
                final Row row = rows.next();
                rolesList.add(Long.valueOf(String.valueOf(row.get("AAA_ROLE_ID"))));
            }
        }
        return rolesList;
    }
    
    public List<Long> convertLongJSONArrayTOList(final JSONArray array) {
        final List arrayList = new ArrayList();
        try {
            for (int i = 0; i < array.length(); ++i) {
                arrayList.add(Long.valueOf(array.get(i).toString()));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "{" + this.getClass().getCanonicalName() + ".convertJSONArrayTOList[JSONArray]}. Array : " + array + " Error :" + ex.getMessage());
        }
        return arrayList;
    }
    
    public boolean isValidScopeId(final Long scopeId) throws DataAccessException {
        final DataObject existingDO = DataAccess.get("APIKeyScope", new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeId, 0));
        return !existingDO.isEmpty();
    }
    
    public List getAllAPIScopes() throws DataAccessException {
        final SelectQuery scopeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
        scopeQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID"));
        scopeQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_NAME"));
        final List scopeList = new ArrayList();
        final DataObject dataObject = SyMUtil.getPersistence().get(scopeQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("APIKeyScope");
            HashMap scopeMap = null;
            while (rows.hasNext()) {
                scopeMap = new HashMap();
                final Row row = rows.next();
                scopeMap.put("SCOPE_ID", row.get("SCOPE_ID"));
                scopeMap.put("SCOPE_NAME", row.get("SCOPE_NAME"));
                scopeMap.put("DESCRIPTION", row.get("DESCRIPTION"));
                scopeList.add(scopeMap);
            }
        }
        return scopeList;
    }
    
    public List getAPIScopesForLoggedInUser() {
        final List scopeList = new ArrayList();
        try {
            final List<Long> roleIds = DMUserHandler.getRoleIdsFromRoleName(ApiFactoryProvider.getAuthUtilAccessAPI().getRoles());
            final SelectQuery scopeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
            scopeQuery.addJoin(new Join("APIKeyScope", "ScopeRoleRel", new String[] { "SCOPE_ID" }, new String[] { "SCOPE_ID" }, 2));
            final Criteria rolecriteria = new Criteria(new Column("ScopeRoleRel", "AAA_ROLE_ID"), (Object)roleIds.toArray(), 8);
            scopeQuery.setCriteria(rolecriteria);
            scopeQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID"));
            scopeQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_NAME"));
            scopeQuery.addSelectColumn(Column.getColumn("APIKeyScope", "DESCRIPTION"));
            final DataObject dataObject = SyMUtil.getPersistenceLite().get(scopeQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("APIKeyScope");
                while (iterator.hasNext()) {
                    final HashMap scopeMap = new HashMap();
                    final Row row = iterator.next();
                    scopeMap.put("SCOPE_ID", row.get("SCOPE_ID"));
                    scopeMap.put("SCOPE_NAME", row.get("SCOPE_NAME"));
                    scopeMap.put("DESCRIPTION", row.get("DESCRIPTION"));
                    scopeList.add(scopeMap);
                }
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, "Exception in getting roles", e);
        }
        catch (final DataAccessException e2) {
            this.logger.log(Level.SEVERE, "Exception in getting the roles", (Throwable)e2);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in getting the roles", e3);
        }
        return scopeList;
    }
    
    public boolean areValidScopeIds(final List<Long> scopeIds) throws Exception {
        return DBUtil.getRecordCount("APIKeyScope", "SCOPE_ID", new Criteria(Column.getColumn("APIKeyScope", "SCOPE_ID"), (Object)scopeIds.toArray(), 8)) == scopeIds.size();
    }
    
    public List<Long> getRolesForScopes(final List<Long> scopeIds) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ScopeRoleRel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ScopeRoleRel", "SCOPE_ID"), (Object)scopeIds.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("ScopeRoleRel", "AAA_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ScopeRoleRel", "SCOPE_ID"));
        final List<Long> rolesList = new ArrayList<Long>();
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("ScopeRoleRel");
            while (rows.hasNext()) {
                final Row row = rows.next();
                rolesList.add(Long.valueOf(String.valueOf(row.get("AAA_ROLE_ID"))));
            }
        }
        return rolesList;
    }
}
