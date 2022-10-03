package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import com.adventnet.persistence.ReadOnlyPersistence;
import org.json.JSONObject;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class AppSettingsDataHandler
{
    private static AppSettingsDataHandler appSettingsDataHandler;
    private Logger logger;
    
    public AppSettingsDataHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void updateHideNonManagedApp(final boolean hideApps, final Long customerID) {
        try {
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppBlackListSetting", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject appDO = MDMUtil.getPersistence().get("MdAppBlackListSetting", customerCri);
            if (appDO.isEmpty()) {
                final Row row = new Row("MdAppBlackListSetting");
                row.set("CUSTOMER_ID", (Object)customerID);
                row.set("HIDE_NON_MANAGED_APPS", (Object)hideApps);
                appDO.addRow(row);
                MDMUtil.getPersistence().add(appDO);
            }
            else {
                final Row row = appDO.getFirstRow("MdAppBlackListSetting");
                row.set("HIDE_NON_MANAGED_APPS", (Object)hideApps);
                appDO.updateRow(row);
                MDMUtil.getPersistence().update(appDO);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(AppSettingsDataHandler.class.getName()).log(Level.SEVERE, "Exception in updateHideNonManagedApp", ex);
        }
    }
    
    public boolean getHideAppStatus(final Long customerID) throws Exception {
        return (boolean)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerID, "HIDE_NON_MANAGED_APPS");
    }
    
    public static AppSettingsDataHandler getInstance() {
        if (AppSettingsDataHandler.appSettingsDataHandler == null) {
            AppSettingsDataHandler.appSettingsDataHandler = new AppSettingsDataHandler();
        }
        return AppSettingsDataHandler.appSettingsDataHandler;
    }
    
    public void addOrUpdateAppViewSettings(final Properties props) {
        try {
            final Long customerID = ((Hashtable<K, Long>)props).get("CUSTOMER_ID");
            final Criteria custCrit = new Criteria(new Column("AppViewSetting", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject appViewSettingDO = MDMUtil.getPersistence().get("AppViewSetting", custCrit);
            this.logger.log(Level.INFO, "AppView Settings : {0}", props);
            if (appViewSettingDO.isEmpty()) {
                final Row row = new Row("AppViewSetting");
                row.set("CUSTOMER_ID", ((Hashtable<K, Object>)props).get("CUSTOMER_ID"));
                row.set("SHOW_MANAGED_APPS", ((Hashtable<K, Object>)props).get("SHOW_MANAGED_APPS"));
                row.set("SHOW_USER_INSTALLED_APPS", ((Hashtable<K, Object>)props).get("SHOW_USER_INSTALLED_APPS"));
                row.set("SHOW_SYSTEM_APPS", ((Hashtable<K, Object>)props).get("SHOW_SYSTEM_APPS"));
                appViewSettingDO.addRow(row);
                MDMUtil.getPersistence().add(appViewSettingDO);
                this.logger.log(Level.INFO, "AppViewSetting row Added");
            }
            else {
                final Row row = appViewSettingDO.getFirstRow("AppViewSetting");
                row.set("SHOW_MANAGED_APPS", ((Hashtable<K, Object>)props).get("SHOW_MANAGED_APPS"));
                row.set("SHOW_USER_INSTALLED_APPS", ((Hashtable<K, Object>)props).get("SHOW_USER_INSTALLED_APPS"));
                row.set("SHOW_SYSTEM_APPS", ((Hashtable<K, Object>)props).get("SHOW_SYSTEM_APPS"));
                appViewSettingDO.updateRow(row);
                MDMUtil.getPersistence().update(appViewSettingDO);
                this.logger.log(Level.INFO, "AppViewSetting row Modified");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in addOrUpdateAppViewSettings", ex);
        }
    }
    
    public JSONObject getAppViewSettings(final Long customerID) {
        JSONObject appViewSettingData = new JSONObject();
        if (customerID != null) {
            try {
                final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
                final DataObject appViewDO = cachedPersistence.get("AppViewSetting", new Criteria(Column.getColumn("AppViewSetting", "CUSTOMER_ID"), (Object)customerID, 0));
                if (!appViewDO.isEmpty()) {
                    final Row appViewDataRow = appViewDO.getFirstRow("AppViewSetting");
                    appViewSettingData = this.setAppViewData(appViewSettingData, (boolean)appViewDataRow.get("SHOW_USER_INSTALLED_APPS"), (boolean)appViewDataRow.get("SHOW_SYSTEM_APPS"), (boolean)appViewDataRow.get("SHOW_MANAGED_APPS"));
                }
                else {
                    appViewSettingData = this.setAppViewData(appViewSettingData, true, false, true);
                }
                appViewSettingData.put("CUSTOMER_ID", (Object)customerID);
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while getAppViewSettings", ex);
            }
        }
        else {
            this.logger.log(Level.WARNING, "Should Provide Valid Customer ID Instead of null");
        }
        this.logger.log(Level.INFO, "AppViewSettings Data : {0}", appViewSettingData);
        return appViewSettingData;
    }
    
    private JSONObject setAppViewData(final JSONObject appViewData, final boolean isShowUserInstalledApp, final boolean isShowSystemApp, final boolean isShowManagedApp) throws JSONException {
        appViewData.put("SHOW_USER_INSTALLED_APPS", isShowUserInstalledApp);
        appViewData.put("SHOW_SYSTEM_APPS", isShowSystemApp);
        appViewData.put("SHOW_MANAGED_APPS", isShowManagedApp);
        return appViewData;
    }
    
    public SelectQuery setOnViewFilterCriteria(SelectQuery selectQuery, final HttpServletRequest request, final String viewName) {
        final String mdAppFilter = request.getParameter("mdAppFilter");
        selectQuery = this.configureAppViewTables(selectQuery);
        final Criteria onViewFilterCrit = this.getOnViewFilterCriteria(mdAppFilter);
        Criteria criteria = selectQuery.getCriteria();
        if (criteria != null) {
            criteria = criteria.and(onViewFilterCrit);
        }
        else {
            criteria = onViewFilterCrit;
        }
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    private SelectQuery configureAppViewTables(final SelectQuery selectQuery) {
        try {
            final List queryTable = selectQuery.getTableList();
            if (queryTable.contains(new Table("MdAppToGroupRel"))) {
                if (!queryTable.contains(new Table("MdAppCatalogToResource"))) {
                    final Criteria appInsCrit = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"), 0);
                    final Criteria appGrpCrit = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
                    final Join appCatJoin = new Join("MdAppToGroupRel", "MdAppCatalogToResource", appGrpCrit.and(appInsCrit), 1);
                    selectQuery.addJoin(appCatJoin);
                    final Column appCatalogColumn = new Column("MdAppCatalogToResource", "RESOURCE_ID", "RESOURCE_ID");
                    selectQuery.addSelectColumn(appCatalogColumn);
                }
            }
            else if (queryTable.contains(new Table("MdAppDetails")) && !queryTable.contains(new Table("MdAppCatalogToResource"))) {
                final Join mdAppGrpJoin = new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
                final Criteria appInsCrit2 = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"), 0);
                final Criteria appGrpCrit2 = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
                final Join appCatJoin2 = new Join("MdAppToGroupRel", "MdAppCatalogToResource", appGrpCrit2.and(appInsCrit2), 1);
                selectQuery.addJoin(mdAppGrpJoin);
                selectQuery.addJoin(appCatJoin2);
                final Column appCatalogColumn2 = new Column("MdAppCatalogToResource", "RESOURCE_ID", "RESOURCE_ID");
                selectQuery.addSelectColumn(appCatalogColumn2);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while configureAppViewTables", e);
        }
        return selectQuery;
    }
    
    private Criteria getOnViewFilterCriteria(final String mdAppFilter) {
        Criteria criteria = null;
        if (mdAppFilter != null && !mdAppFilter.equals("all")) {
            final int mdAppFilterInt = Integer.parseInt(mdAppFilter);
            if (mdAppFilterInt == 0) {
                criteria = this.getGlobalAppViewCriteria();
            }
            else {
                criteria = this.getAppCriteria(mdAppFilterInt);
            }
        }
        else {
            criteria = this.getGlobalAppViewCriteria();
        }
        return criteria;
    }
    
    private Criteria getAppCriteria(final int mdAppFilterInt) {
        Criteria criteria = null;
        if (mdAppFilterInt == 1) {
            criteria = this.getManagedAppCriteria();
        }
        else if (mdAppFilterInt == 2) {
            criteria = this.getUserInstalledAppCriteria();
        }
        else if (mdAppFilterInt == 3) {
            criteria = this.getSystemAppCriteria();
        }
        return criteria;
    }
    
    private Criteria getManagedAppCriteria() {
        Criteria criteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideManagedApp = appViewSettings.optBoolean("SHOW_MANAGED_APPS");
        if (!isHideManagedApp) {
            criteria = this.getUnknownCriteria();
        }
        return criteria;
    }
    
    private Criteria getUserInstalledAppCriteria() {
        final Criteria mdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
        final Criteria showUserInstApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
        Criteria criteria = mdAppCrit.and(showUserInstApp);
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideUserInstalledApp = appViewSettings.optBoolean("SHOW_USER_INSTALLED_APPS");
        if (!isHideUserInstalledApp) {
            criteria = this.getUnknownCriteria();
        }
        return criteria;
    }
    
    private Criteria getSystemAppCriteria() {
        final Criteria mdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
        final Criteria showSystemApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
        Criteria criteria = mdAppCrit.and(showSystemApp);
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideSystemApp = appViewSettings.optBoolean("SHOW_SYSTEM_APPS");
        if (!isHideSystemApp) {
            criteria = this.getUnknownCriteria();
        }
        return criteria;
    }
    
    private Criteria getUnknownCriteria() {
        return new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)(-2), 0);
    }
    
    private Criteria getViewSpecificBlacklistFilter(final String viewName, final HttpServletRequest request) {
        Criteria criteria = null;
        final Criteria appContCrit = new Criteria(Column.getColumn("MdAppControlStatus", "IS_ALLOWED"), (Object)false, 0);
        if (viewName.equalsIgnoreCase("DevicesByApplication") || viewName.equalsIgnoreCase("MDMDevByAppSummary") || viewName.equalsIgnoreCase("MDMDeviceByAppSummary") || viewName.equalsIgnoreCase("BlacklistedDevicesByApplication")) {
            criteria = appContCrit;
        }
        else if (viewName.equalsIgnoreCase("InstalledAndroidAppList") || viewName.equalsIgnoreCase("InstalledKnoxAppList") || viewName.equalsIgnoreCase("InstalledAppList")) {
            final String isBlacklist = request.getParameter("status");
            if (isBlacklist == null || isBlacklist.equalsIgnoreCase("false") || isBlacklist.equalsIgnoreCase("")) {
                criteria = appContCrit;
            }
        }
        return criteria;
    }
    
    public String getOnViewFilterCriteriaString(final String instAppColRef, final HttpServletRequest request, final String viewName) {
        final String criteriaStr = this.getOnViewFilterCriteriaString(instAppColRef, request);
        return criteriaStr;
    }
    
    public String getOnViewFilterCriteriaString(final String instAppColRef, final HttpServletRequest request) {
        final String mdAppTypeFilter = request.getParameter("mdAppFilter");
        String criteriaStr = " ";
        if (mdAppTypeFilter != null && !mdAppTypeFilter.equals("all")) {
            int mdAppTypeFilterInt = 0;
            if (mdAppTypeFilter != null) {
                mdAppTypeFilterInt = Integer.parseInt(mdAppTypeFilter);
            }
            if (mdAppTypeFilterInt == 0) {
                criteriaStr = this.getGloballAppViewSettingCriteriaString(instAppColRef);
            }
            else {
                criteriaStr += this.getAppFilterCriteriaString(mdAppTypeFilterInt, instAppColRef);
            }
        }
        else {
            criteriaStr = this.getGloballAppViewSettingCriteriaString(instAppColRef);
        }
        return criteriaStr;
    }
    
    private String getAppFilterCriteriaString(final int mdAppTypeFilter, final String instAppColRef) {
        String criteria = "";
        if (mdAppTypeFilter == 1) {
            criteria = this.getManagedAppCriteriaString(instAppColRef);
        }
        else if (mdAppTypeFilter == 2) {
            criteria = this.getUserInstalledAppCriteriaString(instAppColRef);
        }
        else if (mdAppTypeFilter == 3) {
            criteria = this.getSystemAppCriteriaString(instAppColRef);
        }
        return criteria;
    }
    
    private String getManagedAppCriteriaString(final String instColRef) {
        String criteriaString = " AND MdAppCatalogToResource.APP_GROUP_ID IS NOT NULL ";
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideManagedApp = appViewSettings.optBoolean("SHOW_MANAGED_APPS");
        if (!isHideManagedApp) {
            criteriaString = this.getUnknownCriteriaString(instColRef);
        }
        return criteriaString;
    }
    
    private String getUserInstalledAppCriteriaString(final String instColRef) {
        String criteriaString = " AND MdAppCatalogToResource.APP_GROUP_ID IS NULL AND " + instColRef + ".USER_INSTALLED_APPS = 1 ";
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideUserInstalledApp = appViewSettings.optBoolean("SHOW_USER_INSTALLED_APPS");
        if (!isHideUserInstalledApp) {
            criteriaString = this.getUnknownCriteriaString(instColRef);
        }
        return criteriaString;
    }
    
    private String getSystemAppCriteriaString(final String instColRef) {
        String criteriaString = " AND MdAppCatalogToResource.APP_GROUP_ID IS NULL AND " + instColRef + ".USER_INSTALLED_APPS = 2 ";
        final JSONObject appViewSettings = this.getAppViewSettings(CustomerInfoUtil.getInstance().getCustomerId());
        final boolean isHideSystemApp = appViewSettings.optBoolean("SHOW_SYSTEM_APPS");
        if (!isHideSystemApp) {
            criteriaString = this.getUnknownCriteriaString(instColRef);
        }
        return criteriaString;
    }
    
    private String getUnknownCriteriaString(final String instColRef) {
        return " AND " + instColRef + ".USER_INSTALLED_APPS = -2 ";
    }
    
    private String getViewSpecifiedBlacklistFilter(final String viewName, final HttpServletRequest request, String existingCriteriaStr) {
        final String currentDatabase = DBUtil.getActiveDBName();
        if (viewName.equalsIgnoreCase("MDMAppRestrictList")) {
            final String isBlacklist = request.getParameter("status");
            if (isBlacklist == null || isBlacklist.equalsIgnoreCase("false") || isBlacklist.equalsIgnoreCase("")) {
                existingCriteriaStr += " AND MdAppControlStatus.IS_ALLOWED = 'false' ";
                if (currentDatabase.equalsIgnoreCase("mysql")) {
                    existingCriteriaStr += " AND MdAppControlStatus.IS_ALLOWED = false ";
                }
            }
        }
        else if (viewName.equalsIgnoreCase("MDMAppList") || viewName.equalsIgnoreCase("MDMDeviceToAllApp")) {
            final String isBlacklist = request.getParameter("status");
            if (isBlacklist == null || isBlacklist.equalsIgnoreCase("false") || isBlacklist.equalsIgnoreCase("")) {
                existingCriteriaStr += " OR MdAppControlStatus.IS_ALLOWED = 'false' ";
                if (currentDatabase.equalsIgnoreCase("mysql")) {
                    existingCriteriaStr += " OR MdAppControlStatus.IS_ALLOWED = false ";
                }
            }
        }
        return existingCriteriaStr;
    }
    
    private String getAppViewCriteriaString(final String instAppColRef, final boolean isShowUserInstalledApp, final boolean isShowSystemApp, final boolean isShowManagedApp) {
        String critStr = "";
        if (isShowManagedApp && isShowUserInstalledApp && isShowSystemApp) {
            critStr = " ";
        }
        else if (isShowManagedApp && isShowUserInstalledApp) {
            critStr = " AND (" + instAppColRef + ".USER_INSTALLED_APPS = 1 OR MdAppCatalogToResource.APP_GROUP_ID IS NOT NULL) ";
        }
        else if (isShowManagedApp && isShowSystemApp) {
            critStr = " AND (" + instAppColRef + ".USER_INSTALLED_APPS = 2 OR MdAppCatalogToResource.APP_GROUP_ID IS NOT NULL) ";
        }
        else if (isShowUserInstalledApp && isShowSystemApp) {
            critStr = " AND MdAppCatalogToResource.APP_GROUP_ID IS NULL ";
        }
        else if (isShowManagedApp) {
            critStr = " AND MdAppCatalogToResource.APP_GROUP_ID IS NOT NULL ";
        }
        else if (isShowUserInstalledApp) {
            critStr = " AND MdAppCatalogToResource.APP_GROUP_ID IS NULL AND " + instAppColRef + ".USER_INSTALLED_APPS=1 ";
        }
        else if (isShowSystemApp) {
            critStr = " AND MdAppCatalogToResource.APP_GROUP_ID IS NULL AND " + instAppColRef + ".USER_INSTALLED_APPS=2 ";
        }
        return critStr;
    }
    
    private Criteria getGlobalAppViewCriteria() {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final JSONObject appViewSettingData = this.getAppViewSettings(customerID);
        final boolean isShowSystemApp = appViewSettingData.optBoolean("SHOW_SYSTEM_APPS");
        final boolean isShowUserInstalledApp = appViewSettingData.optBoolean("SHOW_USER_INSTALLED_APPS");
        final boolean isShowManagedApp = appViewSettingData.optBoolean("SHOW_MANAGED_APPS");
        return this.getGlobalAppViewCriteria(isShowUserInstalledApp, isShowSystemApp, isShowManagedApp);
    }
    
    private Criteria getGlobalAppViewCriteria(final boolean isShowUserInstalledApp, final boolean isShowSystemApp, final boolean isShowManagedApp) {
        Criteria appViewCriteria = null;
        if (isShowManagedApp && isShowUserInstalledApp && isShowSystemApp) {
            appViewCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)(-1), 1);
        }
        else if (isShowManagedApp && isShowUserInstalledApp) {
            final Criteria showMdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            final Criteria showUserInstApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            appViewCriteria = showMdAppCrit.or(showUserInstApp);
        }
        else if (isShowManagedApp && isShowSystemApp) {
            final Criteria showMdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            final Criteria showUserInstApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
            appViewCriteria = showMdAppCrit.or(showUserInstApp);
        }
        else if (isShowUserInstalledApp && isShowSystemApp) {
            appViewCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
        }
        else if (isShowUserInstalledApp) {
            final Criteria showMdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            final Criteria showUserInstApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            appViewCriteria = showMdAppCrit.and(showUserInstApp);
        }
        else if (isShowSystemApp) {
            final Criteria showMdAppCrit = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            final Criteria showUserInstApp = new Criteria(new Column("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
            appViewCriteria = showMdAppCrit.and(showUserInstApp);
        }
        else if (isShowManagedApp) {
            appViewCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
        }
        return appViewCriteria;
    }
    
    public String getAppViewAppGroupJoinString() {
        return " INNER JOIN MdAppToGroupRel ON MdAppToGroupRel.APP_ID=MdInstalledAppResourceRel.APP_ID LEFT JOIN MdAppCatalogToResource ON MdAppCatalogToResource.RESOURCE_ID=MdInstalledAppResourceRel.RESOURCE_ID AND MdAppCatalogToResource.APP_GROUP_ID=MdAppToGroupRel.APP_GROUP_ID";
    }
    
    public String getAppViewGroupJoinString(final String instAppRef) {
        return " LEFT JOIN MdAppCatalogToResource ON MdAppCatalogToResource.RESOURCE_ID=" + instAppRef + ".RESOURCE_ID AND MdAppCatalogToResource.APP_GROUP_ID=MdAppGroupDetails.APP_GROUP_ID";
    }
    
    public String getGloballAppViewSettingCriteriaString(final String instAppColRef) {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final JSONObject appViewSettingData = this.getAppViewSettings(customerID);
        final boolean isShowSystemApp = appViewSettingData.optBoolean("SHOW_SYSTEM_APPS", false);
        final boolean isShowUserInstalledApp = appViewSettingData.optBoolean("SHOW_USER_INSTALLED_APPS", true);
        final boolean isShowManagedApp = appViewSettingData.optBoolean("SHOW_MANAGED_APPS", true);
        final String critStr = this.getAppViewCriteriaString(instAppColRef, isShowUserInstalledApp, isShowSystemApp, isShowManagedApp);
        return critStr;
    }
    
    public void setAppViewFilterAttribute(final HttpServletRequest request) {
        final Map filterAttribute = new HashMap();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final JSONObject appViewSettingData = this.getAppViewSettings(customerID);
            final boolean isShowSystemApp = appViewSettingData.optBoolean("SHOW_SYSTEM_APPS", false);
            final boolean isShowUserInstalledApp = appViewSettingData.optBoolean("SHOW_USER_INSTALLED_APPS", true);
            final boolean isShowManagedApp = appViewSettingData.optBoolean("SHOW_MANAGED_APPS", true);
            filterAttribute.put("IS_SHOW_MANAGED_APP", isShowManagedApp);
            filterAttribute.put("IS_SHOW_USER_INSTALLED_APP", isShowUserInstalledApp);
            filterAttribute.put("IS_SHOW_SYSTEM_APP", isShowSystemApp);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while gettingAppView filter attribute", ex);
        }
        request.setAttribute("APP_VIEW_FILTER", (Object)filterAttribute);
    }
    
    public String getAppViewLocalTransformerText(final int appType) {
        String renderedText = "--";
        switch (appType) {
            case 1: {
                renderedText = "User Installed";
                break;
            }
            case 2: {
                renderedText = "Pre-Installed with OS";
                break;
            }
            case 3: {
                renderedText = "Distributed by MDM";
                break;
            }
        }
        return renderedText;
    }
    
    public String getAppViewTransformerText(final int appType) {
        String renderedText = "--";
        switch (appType) {
            case 1: {
                renderedText = "dc.mdm.apps.user_installed_apps";
                break;
            }
            case 2: {
                renderedText = "dc.mdm.inv.app.filter.system_apps";
                break;
            }
            case 3: {
                renderedText = "dc.mdm.apps.managed_by_mdm";
                break;
            }
        }
        return renderedText;
    }
    
    static {
        AppSettingsDataHandler.appSettingsDataHandler = null;
    }
}
