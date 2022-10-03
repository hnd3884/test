package com.adventnet.sym.webclient.mdm.config.formbean;

import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.server.payload.PayloadException;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppLockPolicyFormBean extends DefaultKioskFormBean
{
    public static final String KEY_ALLOWED_APPS = "ALLOWED_APPS";
    public static final String KEY_AUTONOMOUS_KIOSK_APPS = "AUTONOMOUS_KIOSK_APPS";
    public static final String KEY_APP_ID = "APP_ID";
    public static final String KEY_IS_SYSTEM_APP = "IS_SYSTEM_APP";
    public static final String KEY_GROUP_DISPLAY_NAME = "GROUP_DISPLAY_NAME";
    public static final String KEY_KIOSK_MODE = "KIOSK_MODE";
    private static final Logger logger;
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            super.dynaFormToDO(multipleConfigForm, dynaActionForm, dataObject);
            final Object configDataItemId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
            for (final JSONObject dynaForm : dynaActionForm) {
                final String allowedAppsStr = dynaForm.optString("ALLOWED_APPS");
                final String autoAppsStr = dynaForm.optString("AUTONOMOUS_KIOSK_APPS");
                final JSONArray allowedAppsJson = (allowedAppsStr == null || allowedAppsStr.equals("")) ? null : new JSONArray(allowedAppsStr);
                final JSONArray autoAppsJson = (autoAppsStr == null || autoAppsStr.equals("")) ? null : new JSONArray(autoAppsStr);
                this.modifyAppLockPolicyApps(dataObject, allowedAppsJson, autoAppsJson);
                this.addScreenLayout(dataObject, dynaForm, multipleConfigForm);
                this.addWebClipsRel(dataObject, dynaForm, configDataItemId);
            }
        }
        catch (final PayloadException e) {
            throw e;
        }
        catch (final Exception exp) {
            AppLockPolicyFormBean.logger.log(Level.SEVERE, "AppLockPolicyFormBean: Error while dynaFormToDO() ", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    public void modifyAppLockPolicyApps(final DataObject dataObject, final JSONArray allowedAppsJson, final JSONArray autoAppsJson) throws Exception {
        final Object configId = dataObject.getValue("ConfigDataItem", "CONFIG_DATA_ITEM_ID", (Criteria)null);
        final List<Long> addedList = new ArrayList<Long>();
        if (allowedAppsJson != null && allowedAppsJson.length() > 0) {
            for (int i = 0; i < allowedAppsJson.length(); ++i) {
                final JSONObject appJson = new JSONObject(allowedAppsJson.get(i).toString());
                final Long appId = Long.parseLong(appJson.optString("APP_ID"));
                addedList.add(appId);
                final Criteria appCriteria = new Criteria(new Column("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appId, 0);
                final Row appLockRow = dataObject.getRow("AppLockPolicyApps", appCriteria);
                if (appLockRow == null) {
                    dataObject.addRow(this.createPolicyAppRow(configId, appId, this.isAppAutonomousAllowed(appId, autoAppsJson)));
                }
                else {
                    dataObject.updateRow(this.createPolicyAppRow(configId, appId, this.isAppAutonomousAllowed(appId, autoAppsJson)));
                }
            }
        }
        else if (autoAppsJson != null) {
            for (int i = 0; i < autoAppsJson.length(); ++i) {
                final JSONObject appJson = new JSONObject(autoAppsJson.get(i).toString());
                final Long appId = Long.parseLong(appJson.optString("APP_ID"));
                addedList.add(appId);
                final Criteria appCriteria = new Criteria(new Column("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appId, 0);
                final Row appLockRow = dataObject.getRow("AppLockPolicyApps", appCriteria);
                if (appLockRow == null) {
                    dataObject.addRow(this.createPolicyAppRow(configId, appId, true));
                }
                else {
                    dataObject.updateRow(this.createPolicyAppRow(configId, appId, true));
                }
            }
        }
        if (!(configId instanceof UniqueValueHolder)) {
            this.deleteExistingAppLockPolicyApps(dataObject, addedList);
        }
    }
    
    private void deleteExistingAppLockPolicyApps(final DataObject dataObject, final List appIds) {
        try {
            dataObject.deleteRows("AppLockPolicyApps", new Criteria(new Column("AppLockPolicyApps", "APP_GROUP_ID"), (Object)appIds.toArray(), 9));
        }
        catch (final Exception e) {
            AppLockPolicyFormBean.logger.log(Level.SEVERE, "AppLockPolicyFormBean: Error while deleteExistingAppLockPolicyApps() ", e);
        }
    }
    
    private Row createPolicyAppRow(final Object configId, final Long appId, final Boolean isAutoKiosk) throws Exception {
        final Row policyToAppRow = new Row("AppLockPolicyApps");
        policyToAppRow.set("CONFIG_DATA_ITEM_ID", configId);
        policyToAppRow.set("APP_GROUP_ID", (Object)appId);
        policyToAppRow.set("IS_AUTO_KIOSK_ALLOWED", (Object)isAutoKiosk);
        return policyToAppRow;
    }
    
    private boolean isAppAutonomousAllowed(final Long appId, final JSONArray autoAppsJson) {
        if (autoAppsJson != null) {
            JSONObject appJson = null;
            for (int i = 0; i < autoAppsJson.length(); ++i) {
                try {
                    appJson = new JSONObject(autoAppsJson.get(i).toString());
                    if (Long.valueOf(appJson.getLong("APP_ID")).equals(appId)) {
                        return true;
                    }
                }
                catch (final Exception e) {
                    AppLockPolicyFormBean.logger.log(Level.SEVERE, "AppLockPolicyFormBean: Error while isAppAutonomousAllowed() {0}", new String[] { e.toString() });
                }
            }
            return false;
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger("MDMConfigLogger");
    }
}
