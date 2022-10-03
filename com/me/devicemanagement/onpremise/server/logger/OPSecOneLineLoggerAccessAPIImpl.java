package com.me.devicemanagement.onpremise.server.logger;

import java.util.logging.Logger;
import com.adventnet.authentication.Credential;
import com.adventnet.authentication.util.AuthUtil;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecOneLineLoggerAccessAPI;

public class OPSecOneLineLoggerAccessAPIImpl implements SecOneLineLoggerAccessAPI
{
    public String getSessionUniqId(final ServletRequest request) {
        String sessId = null;
        final Credential cr = AuthUtil.getUserCredential();
        if (cr != null) {
            final Long id = cr.getSessionId();
            if (id == -1L) {
                sessId = "oauth";
            }
            else {
                sessId = "self_client" + id.toString();
            }
        }
        return sessId;
    }
    
    public Logger getLogger(final String module) {
        String loggername = "SecurityOnelineLogger";
        switch (module) {
            case "User_Management": {
                loggername = "UserSecurityOnelineLogger";
                break;
            }
            case "Server": {
                loggername = "ServerSecurityOnelineLogger";
                break;
            }
            case "Security_Management": {
                loggername = "SecurityMgmtOnelineLogger";
                break;
            }
            case "DC_Integration": {
                loggername = "DCIntegrationSecurityOneLineLogger";
                break;
            }
            case "Tools_Management": {
                loggername = "ToolsSecurityOnelineLogger";
                break;
            }
            case "Scope_of_Management": {
                loggername = "SoMSecurityOnelineLogger";
                break;
            }
            case "MDM": {
                loggername = "MDMSecurityOnelineLogger";
                break;
            }
            case "Configuration": {
                loggername = "ConfigSecurityOnelineLogger";
                break;
            }
            case "Software_Deploy": {
                loggername = "SWDeploySecurityOnelineLogger";
                break;
            }
            case "Inventory": {
                loggername = "InventorySecurityOnelineLogger";
                break;
            }
        }
        return Logger.getLogger(loggername);
    }
    
    public boolean isSecurityLoggerEnabled() {
        return true;
    }
}
