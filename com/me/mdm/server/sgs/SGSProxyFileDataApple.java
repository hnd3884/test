package com.me.mdm.server.sgs;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Level;
import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSProxyFileData;
import com.me.ems.onpremise.security.securegatewayserver.proxy.BaseSGSProxyFileData;

public class SGSProxyFileDataApple extends BaseSGSProxyFileData
{
    private SGSProxyFileData sgsProxyFileData;
    
    public SGSProxyFileDataApple(final SGSProxyFileData sgsProxyFileData) {
        this.sgsProxyFileData = null;
        try {
            this.sgsProxyFileData = sgsProxyFileData;
            this.filePath = SGSProxyFileDataApple.dynamicModuleBasePath + "nginx-proxy-apple.conf.template";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception initializing SGSProxyFileDataApple", e);
        }
    }
    
    public StringBuilder getProxyFileData() {
        if (this.isProxyFileDataApplicable()) {
            this.logger.log(Level.INFO, "MDM Apple SGS proxy file data included");
            return this.sgsProxyFileData.getProxyFileData().append(this.getProxyData());
        }
        this.logger.log(Level.INFO, "MDM Apple SGS proxy file data not applicable");
        return this.sgsProxyFileData.getProxyFileData();
    }
    
    public Boolean isProxyFileDataApplicable() {
        return MDMEnrollmentUtil.getInstance().isAPNsConfigured();
    }
}
