package com.me.mdm.server.apps;

import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import java.util.List;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Map;
import java.util.logging.Logger;

public class BaseAppPermissionHandler
{
    public static final String IDENTIFIER = "IDENTIFIER";
    public static final String APPNAME = "APPNAME";
    public static final String PERMISSIONS = "PERMISSIONS";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String PLATFORM = "platform";
    public static final String BUNDLE_ID = "bundle_id";
    public static final String GRANT_STATE = "GRANT_STATE";
    public static final String RECEIVER_APPGROUPDETAILS_ALIAS = "RECEIVER_APPGROUPDETAILS_ALIAS";
    public static final String INV_APPGROUPDETAILS_ALIAS = "INV_APPGROUPDETAILS_ALIAS";
    public static final String INV_MACAPPPROPERTIES_ALIAS = "INV_MACAPPPROPERTIES_ALIAS";
    public static final String RECEIVER_MACAPPPROPERTIES_ALIAS = "RECEIVER_MACAPPPROPERTIES_ALIAS";
    public static final Logger LOGGER;
    public static final Map<String, Long> PERMISSION_INDEX_MAP;
    
    public static BaseAppPermissionHandler getHandler(final int platformType) {
        BaseAppPermissionHandler handler = null;
        switch (platformType) {
            case 1: {
                handler = new MacAppPermissionHandler();
                break;
            }
            default: {
                handler = new BaseAppPermissionHandler();
                break;
            }
        }
        return handler;
    }
    
    public void deleteAppConfig(final Long appGroupID) throws Exception {
        final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("AppPermissionConfig");
        query.addJoin(new Join("AppPermissionConfig", "InvAppGroupToPermission", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("InvAppGroupToPermission", "APP_GROUP_ID"), (Object)appGroupID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "IS_APP_CENTRIC"), (Object)Boolean.TRUE, 0));
        query.setCriteria(criteria);
        MDMUtil.getPersistence().delete(query);
    }
    
    public JSONObject getAppPermission(final Long customerID, final Long appGroupID, final Long permissionConfigID) {
        return new JSONObject();
    }
    
    public void deleteAppPermissionDetails(final Long customerID, final Long permissionConfigID) throws Exception {
        final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("AppPermissionConfig");
        query.addJoin(new Join("AppPermissionConfig", "InvAppGroupToPermission", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        query.addJoin(new Join("InvAppGroupToPermission", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("AppPermissionConfig", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "IS_APP_CENTRIC"), (Object)Boolean.FALSE, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0));
        query.setCriteria(criteria);
        MDMUtil.getPersistence().delete(query);
    }
    
    public DataObject getAppPermissionDO(final Long customerID, final Long appGroupID, final Long permissionConfigID) throws Exception {
        final SelectQuery query = this.getAppPermissionQuery(customerID, appGroupID, permissionConfigID);
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        return dataObject;
    }
    
    public SelectQuery getAppPermissionQuery(final Long customerID, final Long appGroupID, final Long permissionConfigID) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("InvAppGroupToPermission"));
        query.addJoin(new Join("InvAppGroupToPermission", "AppPermissionConfig", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        query.addJoin(new Join("InvAppGroupToPermission", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "InvAppGroupToPermission", "INV_APPGROUPDETAILS_ALIAS", 2));
        query.addJoin(new Join("AppPermissionConfig", "AppPermissionConfigDetails", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 1));
        query.addJoin(new Join("AppPermissionConfigDetails", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("INV_APPGROUPDETAILS_ALIAS", "CUSTOMER_ID"), (Object)customerID, 0);
        if (appGroupID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "APP_GROUP_ID"), (Object)appGroupID, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "IS_APP_CENTRIC"), (Object)Boolean.TRUE, 0));
        }
        else if (permissionConfigID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigID, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("InvAppGroupToPermission", "IS_APP_CENTRIC"), (Object)Boolean.FALSE, 0));
        }
        query.setCriteria(criteria);
        this.addSelectColumnToQuery(query);
        return query;
    }
    
    void addSelectColumnToQuery(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("InvAppGroupToPermission", "*"));
        query.addSelectColumn(Column.getColumn("AppPermissionConfig", "*"));
        query.addSelectColumn(Column.getColumn("INV_APPGROUPDETAILS_ALIAS", "*"));
        query.addSelectColumn(Column.getColumn("INV_MACAPPPROPERTIES_ALIAS", "*"));
        query.addSelectColumn(Column.getColumn("AppPermissionConfigDetails", "*"));
        query.addSelectColumn(Column.getColumn("AppPermissionGroups", "*"));
        query.addSelectColumn(Column.getColumn("Profile", "*"));
    }
    
    protected void addAuthorizationKeyToPayload(final JSONArray permissionArray) {
        for (int j = 0; j < permissionArray.length(); ++j) {
            final JSONObject permissionJSON = permissionArray.getJSONObject(j);
            if (permissionJSON.has("allowed") && !permissionJSON.has("grant_state")) {
                final int grantState = ((boolean)permissionJSON.getBoolean("allowed")) ? 1 : 3;
                permissionJSON.put("grant_state", grantState);
            }
        }
    }
    
    protected void addAllowedKeyToPayload(final JSONArray permissionArray) {
        for (int j = 0; j < permissionArray.length(); ++j) {
            final JSONObject permissionJSON = permissionArray.getJSONObject(j);
            if (!permissionJSON.has("allowed") && permissionJSON.has("GRANT_STATE".toLowerCase())) {
                final Boolean allowed = permissionJSON.getInt("GRANT_STATE".toLowerCase()) == 1;
                permissionJSON.put("allowed", (Object)allowed);
            }
        }
    }
    
    public JSONObject addOrModifyAppPermission(JSONObject jsonObject) throws APIHTTPException {
        Long appGroupID = null;
        Long customerID = null;
        try {
            customerID = jsonObject.getLong("CUSTOMER_ID");
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            final Integer platformType = jsonObject.getInt("platform");
            jsonObject = JSONUtil.getInstance().changeJSONKeyCase(jsonObject, 1);
            this.addAppGroupIDs(jsonObject, platformType);
            appGroupID = jsonObject.getLong("APP_GROUP_ID");
            final Boolean isAppCentric = jsonObject.optBoolean("IS_APP_CENTRIC", (boolean)Boolean.FALSE);
            Object permissionConfigID = jsonObject.optLong("APP_PERMISSION_CONFIG_ID", -1L);
            final DataObject dataObject = isAppCentric ? this.getAppPermissionDO(customerID, appGroupID, null) : this.getAppPermissionDO(customerID, null, (Long)permissionConfigID);
            final String permissionName = "GroupID: " + appGroupID.toString();
            permissionConfigID = MDMDBUtil.updateFirstRow(dataObject, "AppPermissionConfig", new Object[][] { { "APP_PERMISSION_CONFIG_NAME", permissionName } }).get("APP_PERMISSION_CONFIG_ID");
            this.deleteAppPermissionConfig(dataObject, permissionConfigID, isAppCentric);
            MDMDBUtil.updateRow(dataObject, "InvAppGroupToPermission", new Object[][] { { "APP_GROUP_ID", appGroupID }, { "APP_PERMISSION_CONFIG_ID", permissionConfigID }, { "IS_APP_CENTRIC", isAppCentric } });
            if (jsonObject.has("PERMISSIONS")) {
                final JSONArray permissionArray = jsonObject.getJSONArray("PERMISSIONS");
                for (int j = 0; j < permissionArray.length(); ++j) {
                    final JSONObject permissionJSON = permissionArray.getJSONObject(j);
                    final String permission = String.valueOf(permissionJSON.get("GROUP_NAME"));
                    if (!BaseAppPermissionHandler.PERMISSION_INDEX_MAP.containsKey(permission)) {
                        throw new APIHTTPException("APP0022", new Object[0]);
                    }
                    permissionJSON.put("APP_GROUP_ID", (Object)appGroupID);
                    this.addToDataObject(dataObject, appGroupID, permissionJSON, permission, permissionConfigID);
                }
            }
            MDMUtil.getPersistence().update(dataObject);
            permissionConfigID = dataObject.getRow("AppPermissionConfig").get("APP_PERMISSION_CONFIG_ID");
            jsonObject.put("APP_PERMISSION_CONFIG_ID", permissionConfigID);
            jsonObject.put("APP_GROUP_ID", (Object)appGroupID);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            BaseAppPermissionHandler.LOGGER.log(Level.SEVERE, "Unable to add App permission", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return jsonObject;
    }
    
    void deleteAppPermissionConfig(final DataObject dataObject, final Object permissionConfigID, final Boolean isAppCentric) throws Exception {
        if (permissionConfigID instanceof UniqueValueHolder) {
            return;
        }
        dataObject.deleteRows("AppPermissionConfigDetails", new Criteria(Column.getColumn("AppPermissionConfigDetails", "APP_PERMISSION_CONFIG_ID"), permissionConfigID, 0));
        if (isAppCentric) {
            final Long appGroupID = (Long)dataObject.getValue("InvAppGroupToPermission", "APP_GROUP_ID", new Criteria(Column.getColumn("InvAppGroupToPermission", "APP_PERMISSION_CONFIG_ID"), permissionConfigID, 0));
            final Long customerID = (Long)dataObject.getValue("INV_APPGROUPDETAILS_ALIAS", "CUSTOMER_ID", new Criteria(Column.getColumn("INV_APPGROUPDETAILS_ALIAS", "APP_GROUP_ID"), (Object)appGroupID, 0));
            final Long configDataItemID = (Long)DBUtil.getValueFromDB("MacPPPCPolicy", "APP_PERMISSION_CONFIG_ID", permissionConfigID, "CONFIG_DATA_ITEM_ID");
            final Long profileID = ProfileHandler.getProfileIDFromConfigDataItemID(configDataItemID);
            if (profileID != null) {
                final List<String> profileList = new ArrayList<String>();
                profileList.add(profileID.toString());
                ProfileConfigHandler.deleteProfilePermanently(profileList, customerID);
            }
        }
    }
    
    void addAppGroupIDs(final JSONObject jsonObject, final Integer platformType) throws Exception {
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        this.convertToIDFormat(jsonObject, customerID, platformType);
    }
    
    void convertToIDFormat(final JSONObject jsonObject, final Long customerID, final Integer platformType) throws APIHTTPException {
        try {
            Long appGroupID = JSONUtil.optLongForUVH(jsonObject, "APP_GROUP_ID", Long.valueOf(-1L));
            if (appGroupID == -1L) {
                final String identifier = jsonObject.optString("IDENTIFIER", (String)null);
                final String appName = jsonObject.optString("APPNAME", (String)null);
                final String codeRequirement = jsonObject.optString("CODE_REQUIREMENT", (String)null);
                final String installationPath = jsonObject.optString("INSTALLATION_PATH", (String)null);
                if (identifier == null || appName == null || (codeRequirement == null && platformType == 1)) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
                final JSONObject blacklist = new JSONObject();
                blacklist.put("IDENTIFIER".toLowerCase(), (Object)identifier);
                blacklist.put("APPNAME".toLowerCase(), (Object)appName);
                blacklist.put("platform".toLowerCase(), (Object)platformType);
                if (platformType == 1) {
                    blacklist.put("CODE_REQUIREMENT".toLowerCase(), (Object)codeRequirement);
                    blacklist.put("INSTALLATION_PATH".toLowerCase(), (Object)installationPath);
                }
                appGroupID = this.addToBlackListApp(blacklist, customerID);
                jsonObject.put("APP_GROUP_ID", (Object)appGroupID);
            }
        }
        catch (final APIHTTPException e) {
            BaseAppPermissionHandler.LOGGER.log(Level.SEVERE, "Can't validate the input JSON for Mac permission", e);
            throw e;
        }
        catch (final Exception e2) {
            BaseAppPermissionHandler.LOGGER.log(Level.SEVERE, "Can't validate the input JSON for Mac permission", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    Long addToBlackListApp(final JSONObject jsonObject, final Long customerID) throws Exception {
        final JSONArray apps = new JSONArray();
        apps.put((Object)jsonObject);
        final BlacklistPolicyFacade facade = new BlacklistPolicyFacade();
        final List appList = new BlacklistAppHandler().addAndGetAppsInRepository(facade.getPlatformMapFromAppJSON(apps), customerID);
        final JSONObject app = appList.get(0);
        return app.getLong("APP_GROUP_ID");
    }
    
    void addToDataObject(final DataObject dataObject, final Long appGroupID, final JSONObject permission, final String permissionName, final Object permissionConfigID) throws Exception {
        final Integer grantState = permission.getInt("GRANT_STATE");
        this.validatePermissionGrantStateIsApplicable(permissionName, grantState);
        final Row appPermissionDetailsRow = MDMDBUtil.updateRow(dataObject, "AppPermissionConfigDetails", new Object[][] { { "APP_PERMISSION_CONFIG_ID", permissionConfigID }, { "APP_PERMISSION_GRANT_STATE", grantState }, { "APP_PERMISSION_GROUP_ID", BaseAppPermissionHandler.PERMISSION_INDEX_MAP.get(permissionName) }, { "CONFIG_CHOICE", 2 } });
        permission.put("APP_PERMISSION_CONFIG_DTLS_ID", appPermissionDetailsRow.get("APP_PERMISSION_CONFIG_DTLS_ID"));
    }
    
    Boolean isOnlyDenyPermission(final String permissionName) {
        return permissionName.equalsIgnoreCase("apple.macos.permission-group.Camera") || permissionName.equalsIgnoreCase("apple.macos.permission-group.Microphone");
    }
    
    void validatePermissionGrantStateIsApplicable(final String permissionName, final Integer grantState) throws APIHTTPException {
    }
    
    static Map<String, Long> getPermissionIndexMap() {
        final Map<String, Long> permissionMap = new HashMap<String, Long>();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppPermissionGroups"));
            query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"));
            query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"));
            query.setCriteria((Criteria)null);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator<Row> iterator = dataObject.getRows("AppPermissionGroups");
            final List<String> permissions = new ArrayList<String>();
            while (iterator.hasNext()) {
                final Row permissionRow = iterator.next();
                final Long groupID = (Long)permissionRow.get("APP_PERMISSION_GROUP_ID");
                final String permissionName = (String)permissionRow.get("APP_PERMISSION_GROUP_NAME");
                permissionMap.put(permissionName, groupID);
                permissions.add(permissionName);
            }
        }
        catch (final Exception e) {
            BaseAppPermissionHandler.LOGGER.log(Level.SEVERE, "Unable to fetch Permission details", e);
        }
        return permissionMap;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        PERMISSION_INDEX_MAP = getPermissionIndexMap();
    }
}
