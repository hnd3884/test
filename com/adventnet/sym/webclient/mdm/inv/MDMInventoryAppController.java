package com.adventnet.sym.webclient.mdm.inv;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class MDMInventoryAppController extends MDMEmberSqlViewController
{
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final String unique = viewCtx.getUniqueId();
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equals("CUSTOMERCRITERIA")) {
            String customerCrit = CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("Resource.CUSTOMER_ID");
            if (customerCrit.equals("")) {
                customerCrit = "(1=1)";
            }
            associatedValue = customerCrit;
        }
        if (variableName.equalsIgnoreCase("APPCUSTOMERCRITERIA")) {
            String appCustomerCri = CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("MdAppGroupDetails.CUSTOMER_ID");
            if (appCustomerCri.equals("")) {
                appCustomerCri = "(1=1)";
            }
            associatedValue = " and " + appCustomerCri;
        }
        if (variableName.equalsIgnoreCase("APPPLATFORMCRITERIA")) {
            final String platform = viewCtx.getRequest().getParameter("platform");
            if (platform != null && !platform.equalsIgnoreCase("0") && !platform.equalsIgnoreCase("all")) {
                associatedValue = "and MdAppGroupDetails.PLATFORM_TYPE = ".concat(Integer.valueOf(platform).toString());
            }
        }
        if (variableName.equalsIgnoreCase("PLATFORMCRITERIA")) {
            final String platform = viewCtx.getRequest().getParameter("platform");
            if (platform != null && !platform.equalsIgnoreCase("0") && !platform.equalsIgnoreCase("all")) {
                associatedValue = "and ManagedDevice.PLATFORM_TYPE = ".concat(Integer.valueOf(platform).toString());
            }
        }
        if (variableName.equalsIgnoreCase("SHOWAPPCRITERIA")) {
            final String appType = viewCtx.getRequest().getParameter("appType");
            final JSONObject appViewData = AppSettingsDataHandler.getInstance().getAppViewSettings(customerId);
            boolean isShowUserInstalledApp = appViewData.optBoolean("SHOW_USER_INSTALLED_APPS", true);
            boolean isShowSystemApp = appViewData.optBoolean("SHOW_SYSTEM_APPS", false);
            boolean isShowManagedApp = appViewData.optBoolean("SHOW_MANAGED_APPS", true);
            if (!MDMStringUtils.isEmpty(appType) && !appType.equalsIgnoreCase("all")) {
                switch (Integer.parseInt(appType)) {
                    case 1: {
                        isShowManagedApp = false;
                        isShowSystemApp = false;
                        break;
                    }
                    case 2: {
                        isShowManagedApp = false;
                        isShowUserInstalledApp = false;
                        break;
                    }
                    case 3: {
                        isShowSystemApp = false;
                        isShowUserInstalledApp = false;
                        break;
                    }
                }
            }
            if (isShowUserInstalledApp && !isShowManagedApp && isShowSystemApp) {
                associatedValue = " (MdAppCatalogToResource.APP_GROUP_ID is null) ";
            }
            if (isShowUserInstalledApp && !isShowManagedApp && !isShowSystemApp) {
                associatedValue = " (MdAppCatalogToResource.APP_GROUP_ID is null and MdInstalledAppResourceRel.USER_INSTALLED_APPS = 1 and MdAppGroupDetails.APP_TYPE!=2) ";
            }
            if (!isShowUserInstalledApp && !isShowManagedApp && isShowSystemApp) {
                associatedValue = " ((MdAppCatalogToResource.APP_GROUP_ID is null and MdInstalledAppResourceRel.USER_INSTALLED_APPS = 2))";
            }
            if (!isShowUserInstalledApp && isShowManagedApp && !isShowSystemApp) {
                associatedValue = " (MdAppCatalogToResource.APP_GROUP_ID is not null) ";
            }
            if (isShowUserInstalledApp && isShowManagedApp && isShowSystemApp) {
                associatedValue = " (1=1) ";
            }
            if (!isShowUserInstalledApp && !isShowManagedApp && !isShowSystemApp) {
                associatedValue = " (MdInstalledAppResourceRel.APP_ID is null) ";
            }
            if (isShowUserInstalledApp && isShowManagedApp && !isShowSystemApp) {
                associatedValue = " (MdAppCatalogToResource.APP_GROUP_ID is not null or MdInstalledAppResourceRel.USER_INSTALLED_APPS = 1) ";
            }
            if (!isShowUserInstalledApp && isShowManagedApp && isShowSystemApp) {
                associatedValue = " (MdAppCatalogToResource.APP_GROUP_ID is not null or MdInstalledAppResourceRel.USER_INSTALLED_APPS = 2) ";
            }
        }
        if (variableName.equalsIgnoreCase("INSTALLJOINTYPE")) {
            final String appType = viewCtx.getRequest().getParameter("appType");
            if (!MDMStringUtils.isEmpty(appType) && !appType.equalsIgnoreCase("all")) {
                associatedValue = "INNER";
            }
            else {
                associatedValue = "LEFT";
            }
        }
        if (variableName.equalsIgnoreCase("SHOWDEFAULTAPPS")) {
            final JSONObject appViewData2 = AppSettingsDataHandler.getInstance().getAppViewSettings(customerId);
            final boolean isShowSystemApp2 = appViewData2.optBoolean("SHOW_SYSTEM_APPS");
            if (!isShowSystemApp2) {
                associatedValue = "and (MdAppGroupDetails.APP_TYPE != 2)";
            }
        }
        if (variableName.equalsIgnoreCase("BLACKLISTCRITERIA")) {
            final String blacklist = viewCtx.getRequest().getParameter("blacklist");
            if (MDMStringUtils.isEmpty(blacklist) || blacklist.equalsIgnoreCase("all")) {
                associatedValue = " and (1=1) ";
            }
            else if (blacklist.equalsIgnoreCase("false")) {
                associatedValue = " and (BlackListTable.GLOBAL_BLACKLIST is null) ";
            }
            else if (blacklist.equalsIgnoreCase("true")) {
                associatedValue = " and (BlackListTable.GLOBAL_BLACKLIST is not null) ";
            }
            else if (blacklist.equalsIgnoreCase("network")) {
                associatedValue = " and (BlackListTable.GLOBAL_BLACKLIST = 'true' ) ";
            }
            else if (blacklist.equalsIgnoreCase("selected")) {
                associatedValue = " and (BlackListTable.GLOBAL_BLACKLIST = 'false' ) ";
            }
        }
        if (variableName.equalsIgnoreCase("ALLOWLISTCRITERIA")) {
            final String allowlist = viewCtx.getRequest().getParameter("allowlist");
            if (!MDMStringUtils.isEmpty(allowlist) && allowlist.equalsIgnoreCase("true")) {
                associatedValue = " and (BlackListTable.GLOBAL_BLACKLIST is null) ";
            }
            else {
                associatedValue = " and (1=1) ";
            }
        }
        if (variableName.equalsIgnoreCase("BLACKLISTPENDINGCRITERIA")) {
            final String blacklist = viewCtx.getRequest().getParameter("blacklistpending");
            final String blacklistpending = viewCtx.getRequest().getParameter("devicewithblacklistapp");
            if (MDMStringUtils.isEmpty(blacklist) || blacklist.equalsIgnoreCase("all")) {
                associatedValue = "LEFT";
            }
            else {
                associatedValue = "INNER";
            }
            if (!MDMStringUtils.isEmpty(blacklistpending) && blacklistpending.equalsIgnoreCase("true")) {
                associatedValue = "INNER";
            }
        }
        if (variableName.equalsIgnoreCase("INSTALLCOUNTCRITERIA")) {
            final String installed = viewCtx.getRequest().getParameter("installed");
            if (MDMStringUtils.isEmpty(installed) || installed.equalsIgnoreCase("undefined") || installed.equalsIgnoreCase("all")) {
                associatedValue = " and (1=1) ";
            }
            else {
                associatedValue = " and (COALESCE (MdInstalledAppResourceRel.INSTALLATION_COUNT, 0) >0 )";
            }
        }
        if (variableName.equalsIgnoreCase("DEVICECRITERIA")) {
            final String resource = viewCtx.getRequest().getParameter("RESOURCE_ID");
            if (MDMStringUtils.isEmpty(resource) || resource.equalsIgnoreCase("undefined")) {
                associatedValue = " and ManagedDevice.RESOURCE_ID is null ";
            }
            else {
                associatedValue = " and ManagedDevice.RESOURCE_ID = ".concat(Long.valueOf(resource).toString());
            }
        }
        if (variableName.equalsIgnoreCase("BLACKLISTEDDEVICESCRITERIA")) {
            final String blacklist = viewCtx.getRequest().getParameter("devicewithblacklistapp");
            if (MDMStringUtils.isEmpty(blacklist) || blacklist.equalsIgnoreCase("all")) {
                associatedValue = "";
            }
            else {
                associatedValue = " INNER JOIN MdAppToGroupRel ON MdAppGroupDetails.APP_GROUP_ID = MdAppToGroupRel.APP_GROUP_ID INNER JOIN MdInstalledAppResourceRel ON ManagedDevice.RESOURCE_ID = MdInstalledAppResourceRel.RESOURCE_ID AND MdAppToGroupRel.APP_ID=MdInstalledAppResourceRel.APP_ID";
            }
        }
        if (variableName.equalsIgnoreCase("MANAGEDAPPSJOIN")) {
            final String managedapp = viewCtx.getRequest().getParameter("managedapp");
            if (MDMStringUtils.isEmpty(managedapp) || managedapp.equalsIgnoreCase("all")) {
                associatedValue = "LEFT";
            }
            else {
                associatedValue = "INNER";
            }
        }
        if (variableName.equalsIgnoreCase("INSTALLEDBY")) {
            final String installedBy = viewCtx.getRequest().getParameter("installedBy");
            if (!MDMStringUtils.isEmpty(installedBy) && !installedBy.equalsIgnoreCase("all")) {
                switch (Integer.parseInt(installedBy)) {
                    case 1: {
                        associatedValue = " and (MdInstalledAppResourceRel.USER_INSTALLED_APPS = 1 and MdAppCatalogToResource.RESOURCE_ID is null )";
                        break;
                    }
                    case 2: {
                        associatedValue = " and (MdInstalledAppResourceRel.USER_INSTALLED_APPS = 2 and MdAppCatalogToResource.RESOURCE_ID is null )";
                        break;
                    }
                    case 3: {
                        associatedValue = " and (MdAppCatalogToResource.RESOURCE_ID is not null) ";
                        break;
                    }
                }
            }
            else {
                associatedValue = " and (1=1)";
            }
        }
        return associatedValue;
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        return super.getSQLString(viewCtx);
    }
}
