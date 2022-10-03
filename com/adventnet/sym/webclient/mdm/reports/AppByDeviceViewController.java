package com.adventnet.sym.webclient.mdm.reports;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.webclient.common.MDMWebClientUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class AppByDeviceViewController extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public AppByDeviceViewController() {
        this.logger = Logger.getLogger(AppByDeviceViewController.class.getName());
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        if (variableName.equals("CUSTOMERCRITERIA")) {
            final String customerCrit = associatedValue = CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("Resource.CUSTOMER_ID");
        }
        if (variableName.equalsIgnoreCase("PLATFORMCRITERIA")) {
            final String platform = viewCtx.getRequest().getParameter("platform");
            viewCtx.getRequest().setAttribute("platform", (Object)platform);
            if (platform != null && !platform.equalsIgnoreCase("0") && !platform.equalsIgnoreCase("all")) {
                associatedValue = "and MdAppDetails.PLATFORM_TYPE = " + platform;
            }
        }
        if (variableName.equalsIgnoreCase("HIDESYSTEMAPPCRITERIA")) {
            associatedValue = AppSettingsDataHandler.getInstance().getOnViewFilterCriteriaString("MdInstalledAppResourceRel", viewCtx.getRequest(), viewCtx.getUniqueId());
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
