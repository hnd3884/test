package com.adventnet.sym.webclient.mdm.inv;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.webclient.common.MDMWebClientUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.reports.AppByDeviceViewController;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class MDMAppViewController extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public MDMAppViewController() {
        this.logger = Logger.getLogger(AppByDeviceViewController.class.getName());
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
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
        if (variableName.equalsIgnoreCase("PLATFORMCRITERIA")) {
            final String platform = viewCtx.getRequest().getParameter("platform");
            if (platform != null && !platform.equalsIgnoreCase("0") && !platform.equalsIgnoreCase("all")) {
                if (unique.equalsIgnoreCase("MDMDeviceAppRestrictionList") || unique.equalsIgnoreCase("MDMDeviceToAllApp")) {
                    associatedValue = "and ManagedDevice.PLATFORM_TYPE = " + platform;
                }
                else {
                    associatedValue = "and MdAppGroupDetails.PLATFORM_TYPE = " + platform;
                }
            }
        }
        if (variableName.equalsIgnoreCase("HIDEALLCRITERIA")) {
            if (unique.equalsIgnoreCase("MDMAppList")) {
                final String hideAppsParam = viewCtx.getRequest().getParameter("hideOtherDiscoveredApps");
                if (hideAppsParam != null && !hideAppsParam.equalsIgnoreCase("")) {
                    final boolean hideApps = Boolean.valueOf(hideAppsParam);
                    if (hideApps) {
                        associatedValue = "and MdInstalledAppResourceRel.install_count != 0";
                    }
                    else {
                        associatedValue = "";
                    }
                }
                else {
                    associatedValue = "and MdInstalledAppResourceRel.install_count != 0";
                }
            }
            else {
                associatedValue = "and MdInstalledAppResourceRel.install_count != 0";
            }
        }
        if (variableName.equalsIgnoreCase("MDMRBDAPPALLCRITERIA") && (unique.equalsIgnoreCase("MDMAppList") || unique.equalsIgnoreCase("MDMAppRestrictList"))) {
            final String hideOtherDiscoveredApps = viewCtx.getRequest().getParameter("hideOtherDiscoveredApps");
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, false);
            if (!isMDMAdmin) {
                if (hideOtherDiscoveredApps != null && !hideOtherDiscoveredApps.equalsIgnoreCase("")) {
                    final boolean hideApps2 = Boolean.valueOf(hideOtherDiscoveredApps);
                    if (hideApps2) {
                        associatedValue = RBDAUtil.getInstance().getUserDeviceMappingCriteriaString(loginID);
                    }
                    else {
                        associatedValue = "";
                    }
                }
                else {
                    associatedValue = RBDAUtil.getInstance().getUserDeviceMappingCriteriaString(loginID);
                }
            }
        }
        if (variableName.equalsIgnoreCase("MDMRBDAPPALLJOIN") && (unique.equalsIgnoreCase("MDMAppList") || unique.equalsIgnoreCase("MDMAppRestrictList"))) {
            final String hideOtherDiscoveredApps = viewCtx.getRequest().getParameter("hideOtherDiscoveredApps");
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            final boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, false);
            if (!isMDMAdmin) {
                if (hideOtherDiscoveredApps != null && !hideOtherDiscoveredApps.equalsIgnoreCase("")) {
                    final boolean hideApps2 = Boolean.valueOf(hideOtherDiscoveredApps);
                    if (hideApps2) {
                        associatedValue = RBDAUtil.getInstance().getUserDeviceMappingJoinString("ManagedDevice", "RESOURCE_ID");
                    }
                    else {
                        associatedValue = "";
                    }
                }
                else {
                    associatedValue = RBDAUtil.getInstance().getUserDeviceMappingJoinString("ManagedDevice", "RESOURCE_ID");
                }
            }
        }
        final Boolean isCriteriaBasedScheduledReport = Boolean.parseBoolean(viewCtx.getRequest().getParameter("isScheduledReport"));
        final Boolean reportInitByScheduledReport = Boolean.parseBoolean(viewCtx.getRequest().getParameter("reportInitByScheduledReport"));
        if (variableName.equalsIgnoreCase("APPCUSTOMERCRITERIA")) {
            String appCustomerCri = CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("MdAppGroupDetails.CUSTOMER_ID");
            if (appCustomerCri.equals("")) {
                appCustomerCri = "(1=1)";
            }
            associatedValue = " and " + appCustomerCri;
        }
        if (variableName.equalsIgnoreCase("HIDESYSTEMAPPCRITERIA")) {
            if (unique.equalsIgnoreCase("MDMAppList") || unique.equalsIgnoreCase("MDMAppRestrictList")) {
                associatedValue = AppSettingsDataHandler.getInstance().getOnViewFilterCriteriaString("MdInstalledAppResourceRel", viewCtx.getRequest(), unique);
            }
            else if (unique.equalsIgnoreCase("MDMDeviceToAllApp") || unique.equalsIgnoreCase("MDMDeviceAppRestrictionList")) {
                associatedValue = AppSettingsDataHandler.getInstance().getOnViewFilterCriteriaString("MIAR", viewCtx.getRequest(), unique);
            }
            else if (unique.equalsIgnoreCase("MDMAppRestrictListInv")) {
                associatedValue = AppSettingsDataHandler.getInstance().getOnViewFilterCriteriaString("MdInstalledAppResourceRel", viewCtx.getRequest(), unique);
            }
        }
        if (unique.equalsIgnoreCase("MDMAppRestrictListInv") && variableName.equalsIgnoreCase("APP_SETTING_JOIN_FIX")) {
            final String uiAppFilter = viewCtx.getRequest().getParameter("mdAppFilter");
            if (uiAppFilter != null && !uiAppFilter.equals("0") && !uiAppFilter.equalsIgnoreCase("all")) {
                associatedValue = "INNER JOIN";
            }
            else {
                associatedValue = "LEFT JOIN";
            }
        }
        if ((unique.equalsIgnoreCase("MDMDeviceToAllApp") || unique.equalsIgnoreCase("MDMDeviceAppRestrictionList")) && variableName.equalsIgnoreCase("HIDESYSTEMAPPWLCRITERIA")) {
            associatedValue = AppSettingsDataHandler.getInstance().getOnViewFilterCriteriaString("MIAR", viewCtx.getRequest());
        }
        if (variableName.equalsIgnoreCase("FILTERCRITERIA") && (unique.equalsIgnoreCase("MDMAppList") || unique.equalsIgnoreCase("MDMAppRestrictList"))) {
            final String currentDataBase = DBUtil.getActiveDBName();
            final String status = viewCtx.getRequest().getParameter("select");
            if (!MDMStringUtils.isEmpty(status)) {
                if (status.equalsIgnoreCase("blackList")) {
                    associatedValue = " AND BlacklistAppToCollection.blacklist_count > 0 ";
                }
                else if (status.equalsIgnoreCase("whiteList")) {
                    associatedValue = " AND BlacklistAppToCollection.blacklist_count IS null";
                }
                else if (status.equalsIgnoreCase("networkLevel")) {
                    associatedValue = "AND BlacklistAppToCollection.GLOBAL_BLACKLIST = 'true'";
                    if ("mysql".equalsIgnoreCase(currentDataBase)) {
                        associatedValue = "AND BlacklistAppToCollection.GLOBAL_BLACKLIST = true";
                    }
                }
            }
            else {
                associatedValue = "";
            }
        }
        if (variableName.equalsIgnoreCase("BLACKLISTEDDEVICEFILTERCRITERIA")) {
            final String status2 = viewCtx.getRequest().getParameter("select");
            final String uiAppFilter2 = viewCtx.getRequest().getParameter("mdAppFilter");
            if (!MDMStringUtils.isEmpty(status2)) {
                if (status2.equalsIgnoreCase("blacklistedDevices") && (uiAppFilter2 == null || uiAppFilter2.equals("0") || uiAppFilter2.equalsIgnoreCase("all"))) {
                    associatedValue = " AND BlackListTemp.BLACKLIST_COUNT > 0 ";
                }
                else if (status2.equalsIgnoreCase("devicesWithBlacklistedApps")) {
                    associatedValue = " AND BlacklistAppToDevice.BLACKLIST_APP_IN_DEVICE > 0 ";
                }
            }
            else {
                associatedValue = "";
            }
        }
        if (variableName.equalsIgnoreCase("SCRITERIA")) {
            final String[] searchValuesString = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
            final String[] searchColumnsString = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
            final JSONObject responseJSON = MDMWebClientUtil.getInstance().encodeViewSearchParameters(searchValuesString, searchColumnsString);
            final String encodedSearchText = responseJSON.optString("SEARCH_VALUE");
            final String encodedSearchColumn = responseJSON.optString("SEARCH_COLUMN");
            if (MDMUtil.isStringEmpty(encodedSearchText)) {
                viewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)null);
                viewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)null);
            }
            else {
                viewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)encodedSearchText);
                viewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)encodedSearchColumn);
            }
            associatedValue = super.getVariableValue(viewCtx, variableName);
        }
        return associatedValue;
    }
}
