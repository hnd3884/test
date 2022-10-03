package com.me.ems.onpremise.security.securegatewayserver.proxy;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.ems.onpremise.security.securegatewayserver.core.SecureGatewayServerUtils;
import java.util.logging.Logger;

public class SGSDynamicProxyDataSyncHandler
{
    private static Logger logger;
    
    public static void triggerSGSProxyDataSync() {
        try {
            if (SecureGatewayServerUtils.isSecureGatewayServerConfigured()) {
                FwsUtil.getResponseFromSecureGatewayServer("/syncDataServlet");
            }
        }
        catch (final Exception e) {
            SGSDynamicProxyDataSyncHandler.logger.log(Level.SEVERE, "Exception syncing SGS proxy data", e);
        }
    }
    
    static {
        SGSDynamicProxyDataSyncHandler.logger = Logger.getLogger(SGSDynamicProxyDataSyncHandler.class.getName());
    }
}
