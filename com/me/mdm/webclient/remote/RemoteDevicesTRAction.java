package com.me.mdm.webclient.remote;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class RemoteDevicesTRAction extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public RemoteDevicesTRAction() {
        this.logger = Logger.getLogger("MDMRemoteControlLogger");
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String scriteria = super.getVariableValue(viewCtx, variableName);
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equalsIgnoreCase("SCRITERIA")) {
            final HttpServletRequest request = viewCtx.getRequest();
            String platformType = request.getParameter("platformType");
            String osCategory = request.getParameter("osCategory");
            String eligibility = request.getParameter("eligibility");
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (platformType == null) {
                platformType = "all";
            }
            if (osCategory == null) {
                osCategory = "all";
            }
            if (eligibility == null || eligibility.equalsIgnoreCase("all")) {
                eligibility = "1";
            }
            if (!platformType.equals("all")) {
                scriteria = scriteria + " and " + "ManagedDevice.PLATFORM_TYPE = " + platformType;
                if (platformType.equals("2") && osCategory.equals("all")) {
                    if (eligibility.equals("1")) {
                        scriteria = scriteria + " and " + "(MdDeviceInfo.OS_VERSION NOT LIKE '2%' AND MdDeviceInfo.OS_VERSION NOT LIKE '3%' AND MdDeviceInfo.OS_VERSION NOT LIKE '4%')";
                    }
                    else if (eligibility.equals("2")) {
                        scriteria = scriteria + " and " + "(MdDeviceInfo.OS_VERSION LIKE '2%' OR MdDeviceInfo.OS_VERSION LIKE '3%' OR MdDeviceInfo.OS_VERSION  LIKE '4%')";
                    }
                }
                else if (platformType.equals("1") && osCategory.equals("all")) {
                    if (eligibility.equals("1")) {
                        scriteria = scriteria + " and " + "(MdDeviceInfo.OS_VERSION NOT LIKE '4%' AND MdDeviceInfo.OS_VERSION NOT LIKE '5%' AND MdDeviceInfo.OS_VERSION NOT LIKE '6%' AND MdDeviceInfo.OS_VERSION NOT LIKE '7%' AND MdDeviceInfo.OS_VERSION NOT LIKE '8%' AND MdDeviceInfo.OS_VERSION NOT LIKE '9%' AND MdDeviceInfo.OS_VERSION NOT LIKE '10%')";
                    }
                    else if (eligibility.equals("2")) {
                        scriteria = scriteria + " and " + "(ManagedDevice.AGENT_TYPE = 8 OR MdDeviceInfo.OS_VERSION LIKE '4%' OR MdDeviceInfo.OS_VERSION LIKE '5%' OR MdDeviceInfo.OS_VERSION LIKE '6%' OR MdDeviceInfo.OS_VERSION LIKE '7%' OR MdDeviceInfo.OS_VERSION LIKE '8%' OR MdDeviceInfo.OS_VERSION LIKE '9%' OR MdDeviceInfo.OS_VERSION LIKE '10%')";
                    }
                }
                request.setAttribute("platform", (Object)platformType);
            }
            else if (!osCategory.equals("all")) {
                scriteria = scriteria + "and " + "MdDeviceInfo.OS_VERSION = '" + osCategory + "'";
            }
            else if (eligibility.equals("1")) {
                scriteria = scriteria + " and " + "((ManagedDevice.PLATFORM_TYPE = 2 AND (MdDeviceInfo.OS_VERSION NOT LIKE '2%' AND MdDeviceInfo.OS_VERSION NOT LIKE '3%' AND MdDeviceInfo.OS_VERSION NOT LIKE '4%'))  OR (ManagedDevice.PLATFORM_TYPE = 1 AND (MdModelInfo.MODEL_TYPE <> 3 AND MdModelInfo.MODEL_TYPE <> 4 AND MdModelInfo.MODEL_TYPE <> 5) AND (MdDeviceInfo.OS_VERSION NOT LIKE '4%' AND MdDeviceInfo.OS_VERSION NOT LIKE '5%' AND MdDeviceInfo.OS_VERSION NOT LIKE '6%' AND MdDeviceInfo.OS_VERSION NOT LIKE '7%' AND MdDeviceInfo.OS_VERSION NOT LIKE '8%' AND MdDeviceInfo.OS_VERSION NOT LIKE '9%' AND MdDeviceInfo.OS_VERSION NOT LIKE '10%')))";
            }
            else if (eligibility.equals("2")) {
                scriteria = scriteria + " and " + "((ManagedDevice.PLATFORM_TYPE = 2 AND (MdDeviceInfo.OS_VERSION LIKE '2%' OR MdDeviceInfo.OS_VERSION LIKE '3%' OR MdDeviceInfo.OS_VERSION  LIKE '4%'))  OR (ManagedDevice.PLATFORM_TYPE = 1 AND (ManagedDevice.AGENT_TYPE = 8 OR MdDeviceInfo.OS_VERSION LIKE '4%' OR MdDeviceInfo.OS_VERSION LIKE '5%' OR MdDeviceInfo.OS_VERSION LIKE '6%' OR MdDeviceInfo.OS_VERSION LIKE '7%' OR MdDeviceInfo.OS_VERSION LIKE '8%' OR MdDeviceInfo.OS_VERSION LIKE '9%' OR MdDeviceInfo.OS_VERSION LIKE '10%')))";
            }
            request.setAttribute("eligibility", (Object)eligibility);
            request.setAttribute("isAuthTokenConfigured", (Object)MDMApiFactoryProvider.getAssistAuthTokenHandler().isAssistIntegrated(customerID));
            scriteria = scriteria + "and  CustomerInfo.CUSTOMER_ID=" + customerID;
            this.logger.log(Level.FINE, "Criteria  ---- {0}", scriteria);
        }
        return scriteria;
    }
}
