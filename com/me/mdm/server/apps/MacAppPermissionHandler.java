package com.me.mdm.server.apps;

import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Map;
import java.util.HashMap;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.List;

public class MacAppPermissionHandler extends BaseAppPermissionHandler
{
    public static final String AE_GROUP_NAME = "apple.macos.permission-group.AppleEvents";
    public static final Integer IDENTIFIER_TYPE_BUNDLE;
    public static final Integer IDENTIFIER_TYPE_PATH;
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String APPNAME = "APPNAME";
    private static final String PERMISSIONS = "PERMISSIONS";
    private static final String GROUP_NAME = "GROUP_NAME";
    private static final String PLATFORM = "platform";
    private static final String BUNDLE_ID = "bundle_id";
    private static final String APPLEEVENTS = "APPLEEVENTS";
    private static final String SENDER_ID_TYPE = "SENDER_ID_TYPE";
    private static final List<String> ALLOW_NOT_APPLICABLE_PERMISSIONS;
    private static final List<String> STANDARD_USER_PERMISSION_NOT_APPLICABLE_PERMISSIONS;
    public static MacAppPermissionHandler handler;
    
    public static MacAppPermissionHandler getInstance() {
        if (MacAppPermissionHandler.handler == null) {
            MacAppPermissionHandler.handler = new MacAppPermissionHandler();
        }
        return MacAppPermissionHandler.handler;
    }
    
    @Override
    public JSONObject getAppPermission(final Long customerID, Long appGroupID, final Long permissionConfigID) throws APIHTTPException {
        JSONObject response = new JSONObject();
        try {
            final DataObject dataObject = this.getAppPermissionDO(customerID, appGroupID, permissionConfigID);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Map<Long, String> reversePermissionMap = new HashMap<Long, String>();
            for (final Map.Entry<String, Long> entry : MacAppPermissionHandler.PERMISSION_INDEX_MAP.entrySet()) {
                reversePermissionMap.put(entry.getValue(), entry.getKey());
            }
            final Row invAppRow = dataObject.getFirstRow("InvAppGroupToPermission");
            final Boolean isAppCentric = (Boolean)invAppRow.get("IS_APP_CENTRIC");
            if (isAppCentric) {
                final Row profileRow = dataObject.getFirstRow("Profile");
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                response.put("PROFILE_ID", (Object)profileID);
            }
            else {
                appGroupID = (Long)DBUtil.getValueFromDB("InvAppGroupToPermission", "APP_PERMISSION_CONFIG_ID", (Object)permissionConfigID, "APP_GROUP_ID");
            }
            final String appName = this.getAppNameFromAppGroupID(dataObject, "INV_APPGROUPDETAILS_ALIAS", appGroupID);
            response.put("APP_GROUP_ID", (Object)appGroupID);
            response.put("APPNAME", (Object)appName);
            response.put("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigID);
            response = JSONUtil.mergeJSONObjects(response, this.getMacAppProperties(dataObject, appGroupID, "INV_MACAPPPROPERTIES_ALIAS", "INV_APPGROUPDETAILS_ALIAS"));
            this.addPermissionToJSON(dataObject, response);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            MacAppPermissionHandler.LOGGER.log(Level.SEVERE, "FAILED: To get Mac app permission", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    private String getAppNameFromAppGroupID(final DataObject dataObject, final String tableAlias, final Long appGroupID) throws Exception {
        return (String)dataObject.getRow(tableAlias, new Criteria(Column.getColumn(tableAlias, "APP_GROUP_ID"), (Object)appGroupID, 0)).get("GROUP_DISPLAY_NAME");
    }
    
    void addPermissionToJSON(final DataObject dataObject, final JSONObject response) throws Exception {
        final Map<Long, String> reversePermissionMap = new HashMap<Long, String>();
        for (final Map.Entry<String, Long> entry : MacAppPermissionHandler.PERMISSION_INDEX_MAP.entrySet()) {
            reversePermissionMap.put(entry.getValue(), entry.getKey());
        }
        final Iterator<Row> iterator = dataObject.getRows("AppPermissionConfigDetails");
        final JSONArray permissionArray = new JSONArray();
        final JSONArray aeArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row permissionRow = iterator.next();
            final Long permissionID = (Long)permissionRow.get("APP_PERMISSION_GROUP_ID");
            final Long permissionDtlsID = (Long)permissionRow.get("APP_PERMISSION_CONFIG_DTLS_ID");
            final Integer grantState = (Integer)permissionRow.get("APP_PERMISSION_GRANT_STATE");
            final Boolean staticCode = (Boolean)dataObject.getValue("MacAppPermissionProps", "STATIC_CODE_VALIDATION", new Criteria(Column.getColumn("MacAppPermissionProps", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
            final Integer identifierType = (Integer)dataObject.getValue("MacAppPermissionProps", "IDENTIFIER_TYPE", new Criteria(Column.getColumn("MacAppPermissionProps", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("GRANT_STATE".toLowerCase(), (Object)grantState);
            jsonObject.put("STATIC_CODE_VALIDATION", (Object)staticCode);
            jsonObject.put("IDENTIFIER_TYPE", (Object)identifierType);
            if (reversePermissionMap.get(permissionID).equalsIgnoreCase("apple.macos.permission-group.AppleEvents")) {
                final Long receiverAppGroupID = (Long)dataObject.getValue("AppleEventPreference", "RECEIVER_APP_GROUP_ID", new Criteria(Column.getColumn("AppleEventPreference", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
                final String receiverAppGroupName = this.getAppNameFromAppGroupID(dataObject, "RECEIVER_APPGROUPDETAILS_ALIAS", receiverAppGroupID);
                final Integer receiverIDType = (Integer)dataObject.getValue("AppleEventPreference", "RECEIVER_ID_TYPE", new Criteria(Column.getColumn("AppleEventPreference", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissionDtlsID, 0));
                jsonObject.put("RECEIVER_APP_GROUP_ID", (Object)receiverAppGroupID);
                jsonObject.put("APPNAME", (Object)receiverAppGroupName);
                jsonObject.put("RECEIVER_ID_TYPE", (Object)receiverIDType);
                jsonObject = JSONUtil.mergeJSONObjects(jsonObject, this.getMacAppProperties(dataObject, receiverAppGroupID, "RECEIVER_MACAPPPROPERTIES_ALIAS", "RECEIVER_APPGROUPDETAILS_ALIAS"));
                aeArray.put((Object)jsonObject);
            }
            else {
                jsonObject.put("GROUP_NAME".toLowerCase(), (Object)reversePermissionMap.get(permissionID));
                permissionArray.put((Object)jsonObject);
            }
        }
        if (permissionArray.length() > 0) {
            this.addAllowedKeyToPayload(permissionArray);
            response.put("PERMISSIONS".toLowerCase(), (Object)permissionArray);
        }
        if (aeArray.length() > 0) {
            this.addAllowedKeyToPayload(aeArray);
            response.put("APPLEEVENTS".toLowerCase(), (Object)aeArray);
        }
    }
    
    @Override
    public JSONObject addOrModifyAppPermission(JSONObject jsonObject) throws APIHTTPException {
        JSONObject response = new JSONObject();
        Long appGroupID = null;
        Long customerID = null;
        try {
            if (jsonObject.has("PERMISSIONS".toLowerCase())) {
                this.addAuthorizationKeyToPayload(jsonObject.getJSONArray("PERMISSIONS".toLowerCase()));
            }
            if (jsonObject.has("APPLEEVENTS".toLowerCase())) {
                this.addAuthorizationKeyToPayload(jsonObject.getJSONArray("APPLEEVENTS".toLowerCase()));
            }
            jsonObject = super.addOrModifyAppPermission(jsonObject);
            final Long userID = jsonObject.getLong("USER_ID");
            customerID = jsonObject.getLong("CUSTOMER_ID");
            appGroupID = jsonObject.getLong("APP_GROUP_ID");
            final Boolean isAppCentric = jsonObject.optBoolean("IS_APP_CENTRIC", (boolean)Boolean.FALSE);
            final Long permissionConfigID = jsonObject.getLong("APP_PERMISSION_CONFIG_ID");
            if (jsonObject.has("APPLEEVENTS")) {
                final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
                final JSONArray aEArray = jsonObject.getJSONArray("APPLEEVENTS");
                this.addAppleEventsToRepo(dataObject, appGroupID, aEArray, permissionConfigID);
                MDMUtil.getPersistence().add(dataObject);
            }
            if (isAppCentric) {
                this.publishMacPPPCProfile(permissionConfigID, customerID, userID, appGroupID);
                response = this.getAppPermission(customerID, appGroupID, null);
            }
            else {
                response = this.getAppPermission(customerID, null, permissionConfigID);
            }
        }
        catch (final APIHTTPException e) {
            MacAppPermissionHandler.LOGGER.log(Level.SEVERE, "Failed: add/modify Mac app permission", e);
            throw e;
        }
        catch (final Exception e2) {
            MacAppPermissionHandler.LOGGER.log(Level.SEVERE, "FAILED: add/modify Mac app permission", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
        return response;
    }
    
    private void publishMacPPPCProfile(final Long permissionConfigID, final Long customerID, final Long userID, final Long appGroupID) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("LAST_MODIFIED_BY", (Object)userID);
            final String profileName = "TCC: " + AppsUtil.getInstance().getAppName(appGroupID);
            jsonObject.put("PROFILE_NAME", (Object)profileName);
            jsonObject.put("PROFILE_TYPE", 9);
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"PPPC profile for Mac devices");
            jsonObject.put("CREATED_BY", (Object)userID);
            jsonObject.put("PLATFORM_TYPE", 6);
            jsonObject.put("CURRENT_CONFIG", (Object)"macpppcpolicy");
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", (Object)Boolean.FALSE);
            final JSONObject configJSON = new JSONObject();
            configJSON.put("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigID);
            configJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            configJSON.put("CONFIG_ID", 754);
            configJSON.put("CONFIG_NAME", (Object)"MAC_PPPC_POLICY");
            configJSON.put("CONFIG_DATA_IDENTIFIER", (Object)profileName);
            configJSON.put("TABLE_NAME", (Object)"MacPPPCPolicy");
            jsonObject.put("macpppcpolicy", (Object)configJSON);
            final Long collectionID = this.getCollectionForPermissionConfig(permissionConfigID);
            if (collectionID == null) {
                ProfileConfigHandler.addProfileCollection(jsonObject);
                ProfileConfigHandler.addOrModifyConfiguration(jsonObject);
            }
            else {
                final ProfileHandler handler = new ProfileHandler();
                jsonObject.put("PROFILE_ID", (Object)handler.getProfileIDFromCollectionID(collectionID));
                jsonObject.put("COLLECTION_ID", (Object)collectionID);
            }
            ProfileConfigHandler.publishProfile(jsonObject);
        }
        catch (final Exception e) {
            MacAppPermissionHandler.LOGGER.log(Level.SEVERE, "Failed to create a profile for Mac app configuration", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    void addToDataObject(final DataObject dataObject, final Long appGroupID, final JSONObject permission, final String permissionName, final Object permissionConfigID) throws Exception {
        if (permissionName.equals("APPLEEVENTS")) {
            return;
        }
        super.addToDataObject(dataObject, appGroupID, permission, permissionName, permissionConfigID);
        final Boolean isStaticEvalReqd = permission.optBoolean("STATIC_CODE_VALIDATION", (boolean)Boolean.FALSE);
        final Object permissionConfigDtlsID = permission.get("APP_PERMISSION_CONFIG_DTLS_ID");
        MDMDBUtil.updateRow(dataObject, "MacAppPermissionProps", new Object[][] { { "APP_PERMISSION_CONFIG_DTLS_ID", permissionConfigDtlsID }, { "STATIC_CODE_VALIDATION", isStaticEvalReqd }, { "IDENTIFIER_TYPE", permission.optInt("IDENTIFIER_TYPE", (int)MacAppPermissionHandler.IDENTIFIER_TYPE_BUNDLE) } });
    }
    
    private void addAppleEventsToRepo(final DataObject dataObject, final Long appGroupID, final JSONArray appArray, final Object permissionConfigID) throws Exception {
        for (int i = 0; i < appArray.length(); ++i) {
            final JSONObject reciever = appArray.getJSONObject(i);
            final Long recieverappGroupID = JSONUtil.optLongForUVH(reciever, "APP_GROUP_ID", Long.valueOf(-1L));
            final Integer senderIDType = reciever.optInt("SENDER_ID_TYPE", (int)MacAppPermissionHandler.IDENTIFIER_TYPE_BUNDLE);
            final Boolean staticCodeEvel = reciever.optBoolean("STATIC_CODE_VALIDATION", (boolean)Boolean.FALSE);
            final Integer identifierType = reciever.getInt("RECEIVER_ID_TYPE");
            final Integer grantState = reciever.getInt("GRANT_STATE");
            final Object permissionDtlsID = MDMDBUtil.updateRow(dataObject, "AppPermissionConfigDetails", new Object[][] { { "APP_PERMISSION_CONFIG_ID", permissionConfigID }, { "APP_PERMISSION_GRANT_STATE", grantState }, { "CONFIG_CHOICE", 2 }, { "APP_PERMISSION_GROUP_ID", MacAppPermissionHandler.PERMISSION_INDEX_MAP.get("apple.macos.permission-group.AppleEvents") } }).get("APP_PERMISSION_CONFIG_DTLS_ID");
            MDMDBUtil.updateRow(dataObject, "MacAppPermissionProps", new Object[][] { { "APP_PERMISSION_CONFIG_DTLS_ID", permissionDtlsID }, { "STATIC_CODE_VALIDATION", staticCodeEvel }, { "IDENTIFIER_TYPE", senderIDType } });
            MDMDBUtil.updateRow(dataObject, "AppleEventPreference", new Object[][] { { "APP_PERMISSION_CONFIG_DTLS_ID", permissionDtlsID }, { "RECEIVER_APP_GROUP_ID", recieverappGroupID }, { "RECEIVER_ID_TYPE", identifierType } });
        }
    }
    
    @Override
    void addAppGroupIDs(final JSONObject jsonObject, final Integer platformType) throws Exception {
        super.addAppGroupIDs(jsonObject, platformType);
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        if (jsonObject.has("APPLEEVENTS")) {
            final JSONArray apps = jsonObject.getJSONArray("APPLEEVENTS");
            final JSONArray newApps = new JSONArray();
            for (int i = 0; i < apps.length(); ++i) {
                final JSONObject app = apps.getJSONObject(i);
                if (!app.has("RECEIVER_APP_GROUP_ID")) {
                    this.convertToIDFormat(app, customerID, platformType);
                }
                newApps.put((Object)app);
                jsonObject.put("APPLEEVENTS", (Object)newApps);
            }
        }
    }
    
    @Override
    void validatePermissionGrantStateIsApplicable(final String permissionName, final Integer grantState) throws APIHTTPException {
        if (MacAppPermissionHandler.ALLOW_NOT_APPLICABLE_PERMISSIONS.contains(permissionName) && grantState.equals(1)) {
            throw new APIHTTPException("APP0022", new Object[0]);
        }
        if (MacAppPermissionHandler.STANDARD_USER_PERMISSION_NOT_APPLICABLE_PERMISSIONS.contains(permissionName) && grantState.equals(4)) {
            throw new APIHTTPException("APP0022", new Object[0]);
        }
    }
    
    @Override
    public SelectQuery getAppPermissionQuery(final Long customerID, final Long appGroupID, final Long permissionConfigID) {
        final SelectQuery query = super.getAppPermissionQuery(customerID, appGroupID, permissionConfigID);
        query.addJoin(new Join("AppPermissionConfigDetails", "MacAppPermissionProps", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 1));
        query.addJoin(new Join("AppPermissionConfigDetails", "AppleEventPreference", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 1));
        query.addJoin(new Join("AppleEventPreference", "MdAppGroupDetails", new String[] { "RECEIVER_APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "AppleEventPreference", "RECEIVER_APPGROUPDETAILS_ALIAS", 1));
        query.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "RECEIVER_APPGROUPDETAILS_ALIAS", "RECEIVER_MACAPPPROPERTIES_ALIAS", 1));
        query.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, "INV_APPGROUPDETAILS_ALIAS", "INV_MACAPPPROPERTIES_ALIAS", 2));
        query.addJoin(new Join("AppPermissionConfig", "MacPPPCPolicy", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 1));
        query.addJoin(new Join("MacPPPCPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 1));
        query.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        this.addSelectColumnToQuery(query);
        return query;
    }
    
    @Override
    void addSelectColumnToQuery(final SelectQuery query) {
        super.addSelectColumnToQuery(query);
        query.addSelectColumn(Column.getColumn("RECEIVER_APPGROUPDETAILS_ALIAS", "*"));
        query.addSelectColumn(Column.getColumn("RECEIVER_MACAPPPROPERTIES_ALIAS", "*"));
        query.addSelectColumn(Column.getColumn("AppleEventPreference", "*"));
        query.addSelectColumn(Column.getColumn("MacAppPermissionProps", "*"));
    }
    
    public static Map<String, String> generateApplePermissionMap() {
        try {
            final Map<String, String> permissionGroup = new HashMap<String, String>();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppPermissionGroups"));
            query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"));
            query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"));
            query.setCriteria(new Criteria(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"), (Object)"apple.macos.permission-group.", 12));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator<Row> iterator = dataObject.getRows("AppPermissionGroups");
            while (iterator.hasNext()) {
                final Row permissionRow = iterator.next();
                final String permissionName = (String)permissionRow.get("APP_PERMISSION_GROUP_NAME");
                final String applePermissionName = permissionName.replace("apple.macos.permission-group.", "");
                permissionGroup.put(permissionName, applePermissionName);
            }
            permissionGroup.put("apple.macos.permission-group.AppleEvents", "AppleEvents");
            return permissionGroup;
        }
        catch (final Exception e) {
            MacAppPermissionHandler.LOGGER.log(Level.SEVERE, "Unable to generate Mac permission payload", e);
            return null;
        }
    }
    
    private Long getCollectionForPermissionConfig(final Long permissionConfigID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MacPPPCPolicy"));
        query.addJoin(new Join("MacPPPCPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("MacPPPCPolicy", "InvAppGroupToPermission", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        query.addSelectColumn(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"));
        query.addSelectColumn(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"));
        final Criteria criteria = new Criteria(Column.getColumn("InvAppGroupToPermission", "IS_APP_CENTRIC"), (Object)Boolean.TRUE, 0);
        query.setCriteria(criteria.and(new Criteria(Column.getColumn("MacPPPCPolicy", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigID, 0)));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (dataObject.isEmpty()) {
            return null;
        }
        final Row row = dataObject.getFirstRow("CfgDataToCollection");
        return (Long)row.get("COLLECTION_ID");
    }
    
    private JSONObject getMacAppProperties(final DataObject dataObject, final Long appGroupID, final String appPropTableAlias, final String appGroupTableAlias) throws Exception {
        Row row = dataObject.getRow(appPropTableAlias, new Criteria(Column.getColumn(appPropTableAlias, "APP_GROUP_ID"), (Object)appGroupID, 0));
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("CODE_REQUIREMENT", (Object)row.get("CODE_REQUIREMENT"));
        final String installationPath = (String)row.get("INSTALLATION_PATH");
        if (!MDMStringUtils.isEmpty(installationPath)) {
            jsonObject.put("INSTALLATION_PATH", (Object)installationPath);
        }
        row = dataObject.getRow(appGroupTableAlias, new Criteria(Column.getColumn(appGroupTableAlias, "APP_GROUP_ID"), (Object)appGroupID, 0));
        jsonObject.put("IDENTIFIER", (Object)row.get("IDENTIFIER"));
        return jsonObject;
    }
    
    static {
        IDENTIFIER_TYPE_BUNDLE = 1;
        IDENTIFIER_TYPE_PATH = 2;
        ALLOW_NOT_APPLICABLE_PERMISSIONS = new ArrayList<String>() {
            {
                this.add("apple.macos.permission-group.ScreenCapture");
                this.add("apple.macos.permission-group.Camera");
                this.add("apple.macos.permission-group.Microphone");
                this.add("apple.macos.permission-group.ListenEvent");
            }
        };
        STANDARD_USER_PERMISSION_NOT_APPLICABLE_PERMISSIONS = new ArrayList<String>() {
            {
                this.add("apple.macos.permission-group.AddressBook");
                this.add("apple.macos.permission-group.Calendar");
                this.add("apple.macos.permission-group.Reminders");
                this.add("apple.macos.permission-group.Photos");
                this.add("apple.macos.permission-group.Camera");
                this.add("apple.macos.permission-group.Microphone");
                this.add("apple.macos.permission-group.Accessibility");
                this.add("apple.macos.permission-group.PostEvent");
                this.add("apple.macos.permission-group.SystemPolicyAllFiles");
                this.add("apple.macos.permission-group.SystemPolicySysAdminFiles");
                this.add("apple.macos.permission-group.FileProviderPresence");
                this.add("apple.macos.permission-group.MediaLibrary");
                this.add("apple.macos.permission-group.SpeechRecognition");
                this.add("apple.macos.permission-group.SystemPolicyDesktopFolder");
                this.add("apple.macos.permission-group.SystemPolicyDocumentsFolder");
                this.add("apple.macos.permission-group.SystemPolicyDownloadsFolder");
                this.add("apple.macos.permission-group.SystemPolicyNetworkVolumes");
                this.add("apple.macos.permission-group.SystemPolicyRemovableVolumes");
            }
        };
        MacAppPermissionHandler.handler = null;
    }
}
