package com.me.mdm.server.profiles.config;

import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import org.json.JSONArray;

public class IOSAppLockConfigHandler extends DefaultKioskConfigHandler
{
    private JSONArray allowedApp;
    private JSONArray autonomousApp;
    private String autonomousAppAlias;
    private String allowedAppAlias;
    private static final Logger LOGGER;
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        try {
            if (!dataObject.isEmpty() && configJSON.has("payload_id")) {
                final JSONArray configProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
                this.setAliasName(configProperties, configJSON);
                final Row appLockRow = dataObject.getRow("AppLockPolicy");
                final int kioskType = (int)appLockRow.get("KIOSK_MODE");
                if (kioskType != 3) {
                    this.addKioskApps(dataObject, configJSON, configProperties);
                }
                this.addWebClipsRel(configJSON, dataObject, configProperties);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in check and add inner json", e);
            throw e;
        }
    }
    
    public void addKioskApps(final DataObject dataObject, final JSONObject configJSON, final JSONArray configProperties) throws DataAccessException, JSONException {
        try {
            this.setAliasName(configProperties, configJSON);
            final String tableName = "AppLockPolicyApps";
            if (dataObject.containsTable(tableName)) {
                final Long configDataItemId = configJSON.getLong("payload_id");
                final Criteria payloadCriteria = new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                final Iterator iterator = dataObject.getRows(tableName, payloadCriteria);
                while (iterator.hasNext()) {
                    final Row appsRow = iterator.next();
                    final JSONObject innerArrayJSON = new JSONObject();
                    if (appsRow != null) {
                        final Long appGroupId = (Long)appsRow.get("APP_GROUP_ID");
                        final Row appDetailRow = dataObject.getRow("MdAppGroupDetails", new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
                        final String displayName = (String)appDetailRow.get("GROUP_DISPLAY_NAME");
                        final String bundleIdentifier = (String)appDetailRow.get("IDENTIFIER");
                        final Boolean isAutoKiosk = (Boolean)appsRow.get("IS_AUTO_KIOSK_ALLOWED");
                        if (isAutoKiosk) {
                            final JSONObject autonomousArray = new JSONObject();
                            autonomousArray.put(this.getAliasName("APP_ID", this.autonomousApp), (Object)appGroupId);
                            autonomousArray.put(this.getAliasName("GROUP_DISPLAY_NAME", this.autonomousApp), (Object)displayName);
                            configJSON.getJSONArray(this.autonomousAppAlias).put((Object)autonomousArray);
                        }
                        innerArrayJSON.put(this.getAliasName("APP_ID", this.allowedApp), (Object)appGroupId);
                        innerArrayJSON.put(this.getAliasName("GROUP_DISPLAY_NAME", this.allowedApp), (Object)displayName);
                        innerArrayJSON.put("identifier", (Object)bundleIdentifier);
                        configJSON.getJSONArray(this.allowedAppAlias).put((Object)innerArrayJSON);
                        final Row packageRow = dataObject.getRow("MdPackageToAppData", appDetailRow);
                        if (packageRow != null) {
                            Object iconFilePath = packageRow.get("DISPLAY_IMAGE_LOC");
                            if (!MDMStringUtils.isEmpty(String.valueOf(iconFilePath))) {
                                iconFilePath = this.constructFileUrl(iconFilePath);
                                innerArrayJSON.put("display_image_loc", iconFilePath);
                            }
                        }
                        final Row macAppRow = dataObject.getRow("MacAppProperties", new Criteria(new Column("MacAppProperties", "APP_GROUP_ID"), (Object)appGroupId, 0));
                        if (macAppRow == null) {
                            continue;
                        }
                        final String codeRequirement = (String)macAppRow.get("CODE_REQUIREMENT");
                        final String codeSignature = (String)macAppRow.get("SIGNING_IDENTIFIER");
                        innerArrayJSON.put(this.getAliasName("CODE_REQUIREMENT", this.allowedApp), (Object)codeRequirement);
                        innerArrayJSON.put(this.getAliasName("SIGNING_IDENTIFIER", this.allowedApp), (Object)codeSignature);
                        innerArrayJSON.put(this.getAliasName("IDENTIFIER", this.allowedApp), (Object)bundleIdentifier);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw e2;
        }
    }
    
    private void setAliasName(final JSONArray configProperties, final JSONObject configJSON) {
        try {
            for (int i = 0; i < configProperties.length(); ++i) {
                final JSONObject configObject = configProperties.getJSONObject(i);
                final String name = String.valueOf(configObject.get("name"));
                if (name.equalsIgnoreCase("ALLOWED_APPS")) {
                    configJSON.put(this.allowedAppAlias = String.valueOf(configObject.get("alias")), (Object)new JSONArray());
                    this.allowedApp = configObject.getJSONArray("properties");
                }
                else if (name.equalsIgnoreCase("AUTONOMOUS_KIOSK_APPS")) {
                    configJSON.put(this.autonomousAppAlias = String.valueOf(configObject.get("alias")), (Object)new JSONArray());
                    this.autonomousApp = configObject.getJSONArray("properties");
                }
            }
        }
        catch (final JSONException ex) {}
    }
    
    private String getAliasName(final String name, final JSONArray arrayObject) {
        try {
            for (int i = 0; i < arrayObject.length(); ++i) {
                final JSONObject property = arrayObject.getJSONObject(i);
                if (String.valueOf(property.get("name")).equals(name) && property.has("alias")) {
                    return String.valueOf(property.get("alias"));
                }
            }
        }
        catch (final JSONException ex) {}
        return "";
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        final int kioskMode = serverJSON.getInt("KIOSK_MODE");
        boolean resetScreenLayoutJSON = true;
        boolean resetWebclipJSON = true;
        if (kioskMode == 3) {
            if (!serverJSON.has("WebClipPolicies")) {
                throw new APIHTTPException("COM0005", new Object[] { "webclip_policies_ids" });
            }
            final JSONArray allowedAppArray = new JSONArray();
            final JSONObject mdmAppJSON = new JSONObject();
            allowedAppArray.put((Object)mdmAppJSON);
            final Long customerId = serverJSON.getLong("CUSTOMER_ID");
            Long appGroupId = AppsUtil.getInstance().getAppGroupIDFromIdentifier("com.manageengine.mdm.iosagent", 1, customerId);
            if (appGroupId == null) {
                final Long userId = serverJSON.getLong("LAST_MODIFIED_BY");
                new IosNativeAppHandler().addIOSNativeAgent(customerId, userId);
                appGroupId = AppsUtil.getInstance().getAppGroupIDFromIdentifier("com.manageengine.mdm.iosagent", 1, customerId);
            }
            mdmAppJSON.put("APP_ID", (Object)appGroupId);
            serverJSON.put("ALLOWED_APPS", (Object)allowedAppArray);
            resetWebclipJSON = false;
        }
        else if (kioskMode == 1) {
            if (!serverJSON.has("ALLOWED_APPS") || serverJSON.getJSONArray("ALLOWED_APPS").length() > 1) {
                throw new APIHTTPException("COM0005", new Object[] { "ALLOWED_APPS" });
            }
            if (serverJSON.has("WebClipPolicies") && serverJSON.getJSONArray("WebClipPolicies").length() > 0) {
                serverJSON.put("WebClipPolicies", (Object)new JSONArray());
            }
        }
        else if (kioskMode == 2) {
            if (!serverJSON.has("ALLOWED_APPS") || serverJSON.getJSONArray("ALLOWED_APPS").length() < 1) {
                throw new APIHTTPException("COM0005", new Object[] { "ALLOWED_APPS" });
            }
            resetScreenLayoutJSON = false;
            resetWebclipJSON = false;
        }
        else if (kioskMode == 0) {
            if (!serverJSON.has("AUTONOMOUS_KIOSK_APPS") || serverJSON.getJSONArray("AUTONOMOUS_KIOSK_APPS").length() < 1) {
                throw new APIHTTPException("COM0005", new Object[] { "AUTONOMOUS_KIOSK_APPS" });
            }
            if (serverJSON.has("WebClipPolicies") && serverJSON.getJSONArray("WebClipPolicies").length() > 0) {
                serverJSON.put("WebClipPolicies", (Object)new JSONArray());
            }
        }
        if (resetScreenLayoutJSON) {
            serverJSON.put("ScreenLayout", (Object)new JSONObject());
            serverJSON.put("ScreenLayoutSettings", (Object)new JSONObject());
        }
        if (resetWebclipJSON) {
            serverJSON.put("WebClipPolicies", (Object)new JSONArray());
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
