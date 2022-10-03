package com.me.mdm.core.lockdown.windows;

import com.me.mdm.server.windows.profile.payload.content.lockdown.AppProperties;
import com.me.mdm.server.windows.profile.payload.content.lockdown.HandheldLockdown;
import com.me.mdm.core.xmlparser.XmlBeanUtil;
import com.me.mdm.core.lockdown.windows.data.TaskbarT;
import com.me.mdm.core.lockdown.windows.data.AppT;
import com.me.mdm.core.lockdown.windows.data.KioskmodeappT;
import com.me.mdm.core.lockdown.windows.data.AutologonAccountT;
import com.me.mdm.core.lockdown.windows.data.AllowedappsT;
import com.me.mdm.core.lockdown.windows.data.AllappslistT;
import com.me.mdm.core.lockdown.windows.data.ConfigT;
import com.me.mdm.core.lockdown.windows.data.ConfigListT;
import com.me.mdm.core.lockdown.windows.data.ProfileIdT;
import java.util.UUID;
import com.me.mdm.core.lockdown.windows.data.ProfileT;
import com.me.mdm.core.lockdown.windows.data.ProfileListT;
import com.me.mdm.core.lockdown.windows.data.AssignedAccessConfiguration;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.core.lockdown.data.LockdownApplication;
import com.me.mdm.core.lockdown.data.LockdownAppToRule;
import com.me.mdm.core.lockdown.data.WindowsLockdownConfig;
import com.me.mdm.core.lockdown.data.LockdownRule;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.mdm.core.lockdown.data.LockdownPolicy;
import java.util.logging.Logger;
import com.me.mdm.core.lockdown.LockdownHandler;

public class WindowsLockdownHandler extends LockdownHandler
{
    public Logger logger;
    
    public WindowsLockdownHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    protected JSONObject convertEnterprisePolicyToApiJSON(final LockdownPolicy enterprisePolicy) throws JSONException, DataAccessException {
        final JSONObject jsonObject = enterprisePolicy.toJSON();
        final JSONArray allowedApps = jsonObject.getJSONObject("appconfig").getJSONArray("allowed_apps");
        final List allowedAppList = new ArrayList();
        for (int i = 0; i < allowedApps.length(); ++i) {
            final JSONObject app = allowedApps.getJSONObject(i);
            allowedAppList.add(String.valueOf(app.get("identifier")).split("!")[0]);
        }
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)allowedAppList.toArray(), 8));
        DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final HashMap nameMap = new HashMap();
        Iterator iterator = dataObject.getRows("MdAppGroupDetails");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String name = (String)row.get("GROUP_DISPLAY_NAME");
            final String identifer = (String)row.get("IDENTIFIER");
            nameMap.put(identifer, name);
        }
        selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsSystemApps"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "PACKAGE_FAMILY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsSystemApps", "APP_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("WindowsSystemApps", "PACKAGE_FAMILY_NAME"), (Object)allowedAppList.toArray(), 8));
        dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        iterator = dataObject.getRows("WindowsSystemApps");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String name = (String)row.get("APP_NAME");
            final String identifer = (String)row.get("PACKAGE_FAMILY_NAME");
            nameMap.put(identifer, name);
        }
        for (int j = 0; j < allowedApps.length(); ++j) {
            final JSONObject app2 = allowedApps.getJSONObject(j);
            final String identifer = String.valueOf(app2.get("identifier")).split("!")[0];
            final String name2 = nameMap.get(identifer);
            if (name2 != null) {
                app2.put("group_display_name", (Object)name2);
            }
        }
        return jsonObject;
    }
    
    @Override
    protected LockdownPolicy convertApiJsonToPolicy(final JSONObject jsonObject) {
        final LockdownPolicy lockdownPolicy = new LockdownPolicy();
        try {
            final String policyName = String.valueOf(jsonObject.get("policy_name"));
            final String policyDescription = jsonObject.optString("policy_desc", "");
            final JSONArray allowedApps = jsonObject.getJSONArray("allowed_apps");
            final JSONObject configuration = jsonObject.getJSONObject("configuration");
            lockdownPolicy.policyName = policyName;
            lockdownPolicy.policyDescription = policyDescription;
            final List<LockdownRule> rules = new ArrayList<LockdownRule>();
            final WindowsLockdownConfig windowsLockdownConfig = new WindowsLockdownConfig();
            windowsLockdownConfig.isAutoCreateUser = configuration.optBoolean("create_user", true);
            windowsLockdownConfig.associatedUser = String.valueOf(configuration.get("associated_user"));
            windowsLockdownConfig.autoLogonApp = configuration.optString("auto_logon_app");
            windowsLockdownConfig.autoDistributeApps = configuration.optBoolean("auto_distribute_apps", (boolean)Boolean.TRUE);
            windowsLockdownConfig.platform = 3;
            lockdownPolicy.rules.add(windowsLockdownConfig);
            final LockdownAppToRule lockdownAppToRule = new LockdownAppToRule(3);
            lockdownAppToRule.platform = 3;
            for (int i = 0; i < allowedApps.length(); ++i) {
                final JSONObject app = allowedApps.getJSONObject(i);
                final LockdownApplication lockdownApplication = new LockdownApplication();
                lockdownApplication.identifier = String.valueOf(app.get("app_identifier"));
                lockdownApplication.appType = LockdownHandler.getAppType(lockdownApplication.identifier);
                lockdownAppToRule.addApplicationToList(lockdownApplication);
            }
            lockdownPolicy.rules.add(lockdownAppToRule);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", (Throwable)e);
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        return lockdownPolicy;
    }
    
    public String getLockDownXML(final LockdownPolicy lockdownPolicy, final Boolean isSingleAppKiosk) throws Exception {
        final AssignedAccessConfiguration assignedAccessConfiguration = new AssignedAccessConfiguration();
        final ProfileListT profileListT = new ProfileListT();
        final ProfileT profileT = new ProfileT();
        final String profileUUID = "{" + UUID.randomUUID().toString() + "}";
        final ProfileIdT profileIdT = new ProfileIdT();
        profileIdT.setId(profileUUID);
        profileT.setId(profileUUID);
        final ConfigListT configListT = new ConfigListT();
        final ConfigT configT = new ConfigT();
        configT.setDefaultProfile(profileIdT);
        final AllappslistT allappslistT = new AllappslistT();
        final AllowedappsT allowedappsT = new AllowedappsT();
        final List lockdownApplicaitons = new ArrayList();
        for (final LockdownRule lockdownRule : lockdownPolicy.rules) {
            if (lockdownRule instanceof WindowsLockdownConfig) {
                final WindowsLockdownConfig windowsLockdownConfig = (WindowsLockdownConfig)lockdownRule;
                if (windowsLockdownConfig.isAutoCreateUser) {
                    final AutologonAccountT autologonAccountT = new AutologonAccountT();
                    configT.setAutoLogonAccount(autologonAccountT);
                }
                else {
                    configT.setAccount(windowsLockdownConfig.associatedUser);
                }
            }
            else {
                if (!(lockdownRule instanceof LockdownAppToRule)) {
                    continue;
                }
                final LockdownAppToRule lockdownAppToRule = (LockdownAppToRule)lockdownRule;
                if (isSingleAppKiosk) {
                    final KioskmodeappT kioskmodeappT = new KioskmodeappT();
                    kioskmodeappT.setAppUserModelId(lockdownAppToRule.lockdownApplications.get(0).identifier);
                    profileT.setKioskModeApp(kioskmodeappT);
                }
                else {
                    for (final LockdownApplication lockdownApplication : lockdownAppToRule.lockdownApplications) {
                        lockdownApplicaitons.add(lockdownApplication);
                        final AppT appT = new AppT();
                        if (lockdownApplication.appType.equals(LockdownApplication.MODERN_APP_TYPE)) {
                            appT.setAppUserModelId(lockdownApplication.identifier);
                        }
                        else {
                            appT.setDesktopAppPath(lockdownApplication.identifier);
                        }
                        allowedappsT.getApp().add(appT);
                    }
                    allappslistT.setAllowedApps(allowedappsT);
                    final TaskbarT taskbarT = new TaskbarT();
                    taskbarT.setShowTaskbar(Boolean.TRUE);
                    profileT.setTaskbar(taskbarT);
                    profileT.setAllAppsList(allappslistT);
                    final String startLayout = new StartLayoutHandler().getDefaultLayout(lockdownApplicaitons);
                    profileT.setStartLayout(startLayout);
                }
            }
        }
        configListT.getConfig().add(configT);
        profileListT.getProfile().add(profileT);
        assignedAccessConfiguration.setConfigs(configListT);
        assignedAccessConfiguration.setProfiles(profileListT);
        final JSONObject beanUtilJSON = new JSONObject();
        beanUtilJSON.put("BEAN_OBJECT", (Object)assignedAccessConfiguration);
        beanUtilJSON.put("jaxb.fragment", (Object)Boolean.TRUE);
        beanUtilJSON.put("jaxb.encoding", (Object)"UTF-8");
        final JSONObject customProps = new JSONObject();
        customProps.put("com.sun.xml.internal.bind.xmlHeaders", (Object)"");
        beanUtilJSON.put("customMarshallerProps", (Object)customProps);
        final XmlBeanUtil<HandheldLockdown> xmlBeanUtil = new XmlBeanUtil<HandheldLockdown>(beanUtilJSON);
        final String lockdownXML = xmlBeanUtil.beanToXmlString();
        return lockdownXML;
    }
    
    public LockdownPolicy convertLockDownPropertiesToPolicy(final HandheldLockdown.EnterpriseLockDownProperties properties) {
        final LockdownPolicy lockdownPolicy = new LockdownPolicy();
        lockdownPolicy.policyName = "Converted from kioskmodeapp payload";
        lockdownPolicy.policyDescription = "";
        final WindowsLockdownConfig windowsLockdownConfig = new WindowsLockdownConfig();
        windowsLockdownConfig.isAutoCreateUser = true;
        windowsLockdownConfig.platform = 3;
        lockdownPolicy.rules.add(windowsLockdownConfig);
        final LockdownAppToRule lockdownAppToRule = new LockdownAppToRule(3);
        lockdownAppToRule.platform = 3;
        final List<AppProperties> allowedApps = properties.getAllowedApps();
        for (int i = 0; i < allowedApps.size(); ++i) {
            final AppProperties appProperties = allowedApps.get(i);
            final LockdownApplication lockdownApplication = new LockdownApplication();
            lockdownApplication.identifier = appProperties.aumid;
            lockdownApplication.appType = LockdownHandler.getAppType(lockdownApplication.identifier);
            lockdownAppToRule.addApplicationToList(lockdownApplication);
        }
        lockdownPolicy.rules.add(lockdownAppToRule);
        return lockdownPolicy;
    }
}
