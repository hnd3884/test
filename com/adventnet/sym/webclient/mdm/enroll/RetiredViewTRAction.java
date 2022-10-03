package com.adventnet.sym.webclient.mdm.enroll;

import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class RetiredViewTRAction extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public RetiredViewTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private String modifyToRBDAQuery(final String variable) {
        String result = "";
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (loginID != null) {
                final Boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, true);
                if (!isMDMAdmin) {
                    if (variable.equalsIgnoreCase("join")) {
                        result += " LEFT JOIN CustomGroupMemberRel ON ManagedDevice.RESOURCE_ID = CustomGroupMemberRel.MEMBER_RESOURCE_ID ";
                        result += " LEFT JOIN UserCustomGroupMapping DeviceGroups ON CustomGroupMemberRel.GROUP_RESOURCE_ID = DeviceGroups.GROUP_RESOURCE_ID ";
                    }
                    else if (variable.equalsIgnoreCase("criteria")) {
                        final String cgCriteria = " (DeviceGroups.GROUP_RESOURCE_ID is not null)";
                        final String cgReqCriteria = " (DeviceEnrollmentRequest.USER_ID = " + userID + ")";
                        final String mdCriteria = " (ManagedDevice.RESOURCE_ID is not null)";
                        final String nomdCriteria = " (ManagedDevice.RESOURCE_ID is null)";
                        final String userCustomGroupCriteria = " (DeviceGroups.LOGIN_ID = " + loginID + ")";
                        result = result + " (" + userCustomGroupCriteria + " and " + mdCriteria + " and " + cgCriteria + " ) or ( " + nomdCriteria + " and " + cgReqCriteria + " )";
                        result = " and ( " + result + " ) ";
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(RetiredViewTRAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String result = "";
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final HttpServletRequest request = viewCtx.getRequest();
            final String ownedBy = request.getParameter("ownedBy");
            final String platform = request.getParameter("platformType");
            request.setAttribute("retiredviewtoolID", (Object)"40082");
            request.setAttribute("ownedBy", (Object)ownedBy);
            request.setAttribute("platformType", (Object)platform);
            final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
            request.setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            String cCust = null;
            String selectedTab = request.getParameter("selectedTab");
            selectedTab = ((selectedTab != null) ? selectedTab : "retired");
            request.setAttribute("selectedTabInput", (Object)selectedTab);
            if (customerId != null) {
                cCust = " (UserResource.CUSTOMER_ID = " + String.valueOf(customerId) + ") ";
            }
            request.setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
            if (variableName.trim().startsWith("DBRANGECRITERIA")) {
                return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
            }
            if (variableName.equals("SCRITERIA")) {
                String criteria = " (ManagedDevice.MANAGED_STATUS = 11) ";
                if (ownedBy != null && !ownedBy.equals("-1") && !ownedBy.equalsIgnoreCase("") && ownedBy != null && !ownedBy.equals("-1")) {
                    criteria = " (" + criteria + ") and DeviceEnrollmentRequest.OWNED_BY = " + ownedBy;
                }
                if (platform != null && !platform.equals("-1") && !platform.equalsIgnoreCase("")) {
                    criteria = " (" + criteria + ") and DeviceEnrollmentRequest.PLATFORM_TYPE = " + platform;
                }
                if (cCust != null) {
                    criteria = " (" + criteria + ") and (" + cCust + ")";
                }
                final String[] searchColumns = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
                if (searchColumns != null && searchColumns.length != 0) {
                    for (int i = 0; i < searchColumns.length; ++i) {
                        searchColumns[i] = searchColumns[i].replaceAll("DeprovisionHistory", "subquery");
                    }
                    final String[] seacrhVal = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
                    for (int j = 0; j < searchColumns.length; ++j) {
                        criteria = criteria + " and (" + searchColumns[j] + " like '%" + DMIAMEncoder.encodeSQLForNonPatternContext(seacrhVal[j]) + "%')";
                    }
                }
                result = criteria + this.modifyToRBDAQuery("criteria");
            }
            else if (variableName.equals("SJOIN")) {
                result = this.modifyToRBDAQuery("join");
            }
            else if (variableName.equals("CCRITERIA")) {
                result = " (r.CUSTOMER_ID = " + String.valueOf(customerId) + ") ";
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(RetiredViewTRAction.class.getName()).log(Level.SEVERE, "Exception in retiredViewTRAction:setCriteria()", ex);
        }
        return result;
    }
}
