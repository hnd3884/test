package com.adventnet.sym.webclient.mdm;

import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.DCSqlViewController;

public class MDMSqlViewController extends DCSqlViewController
{
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String associatedValue = super.getVariableValue(viewCtx, variableName);
        if (associatedValue == null) {
            final Long loginID = SYMClientUtil.getLoginId(viewCtx.getRequest());
            if (loginID != null) {
                final boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    final String uniqueId = viewCtx.getUniqueId();
                    if (variableName.equalsIgnoreCase("MDMRBDADEVICEJOIN")) {
                        associatedValue = RBDAUtil.getInstance().getUserDeviceMappingJoinString("ManagedDevice", "RESOURCE_ID");
                    }
                    else if (variableName.equalsIgnoreCase("MDMRBDADEVICECRITERIA")) {
                        associatedValue = RBDAUtil.getInstance().getUserDeviceMappingCriteriaString(loginID);
                    }
                    final Boolean isRBDAGroupCheck = RBDAUtil.getInstance().hasRBDAGroupCheck(loginID, true);
                    if (!isRBDAGroupCheck) {
                        if (variableName.equalsIgnoreCase("MDMRBDAGROUPJOIN")) {
                            associatedValue = RBDAUtil.getInstance().getUserCustomGroupMappingJoinString("CustomGroup", "RESOURCE_ID");
                        }
                        else if (variableName.equalsIgnoreCase("MDMRBDAGROUPCRITERIA")) {
                            associatedValue = RBDAUtil.getInstance().getUserCustomGroupCriteriaString(loginID);
                        }
                    }
                }
                if (variableName.equalsIgnoreCase("APPVIEWAPPGROUPJOIN")) {
                    associatedValue = AppSettingsDataHandler.getInstance().getAppViewAppGroupJoinString();
                }
                if (variableName.equalsIgnoreCase("APPVIEWGROUPJOIN")) {
                    associatedValue = AppSettingsDataHandler.getInstance().getAppViewGroupJoinString("MIAR");
                }
            }
        }
        return associatedValue;
    }
}
