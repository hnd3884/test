package com.me.mdm.core.enrollment.settings;

import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.idps.core.crud.DMDomainDataHandler;
import org.json.JSONObject;

public class ModernMgmtUserAssignmentHandler extends BaseUserAssignmentHandler
{
    public static final String DEFAULT_RULE_NAME = "Modern Mgmt Enrollment Default Rule";
    
    @Override
    public void userRuleMatched(final JSONObject deviceProps, final JSONObject userProps, final Long customerID) throws Exception {
        final String domain = userProps.optString("DOMAIN_NETBIOS_NAME", "MDM");
        final Boolean isADUser = DMDomainDataHandler.getInstance().isADManagedDomain(domain, customerID);
        if (isADUser || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowAllModernMgmtDevices")) {
            if (isADUser) {
                userProps.put("EMAIL_ADDRESS", (Object)ADSyncDataHandler.getInstance().getDirUserProps(customerID, domain, userProps.getString("NAME")).getProperty("EMAIL_ADDRESS"));
            }
            else {
                final JSONObject userIdJson = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(customerID, Boolean.TRUE);
                userProps.put("NAME", userIdJson.get("NAME"));
                userProps.put("DOMAIN_NETBIOS_NAME", userIdJson.get("DOMAIN_NETBIOS_NAME"));
                userProps.put("EMAIL_ADDRESS", userIdJson.get("EMAIL_ADDRESS"));
            }
            super.userRuleMatched(deviceProps, userProps, customerID);
        }
    }
}
