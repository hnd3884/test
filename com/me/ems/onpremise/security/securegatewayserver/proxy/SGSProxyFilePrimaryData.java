package com.me.ems.onpremise.security.securegatewayserver.proxy;

import java.util.logging.Level;

public class SGSProxyFilePrimaryData extends BaseSGSProxyFileData
{
    public SGSProxyFilePrimaryData() {
        try {
            this.filePath = SGSProxyFilePrimaryData.primaryProxyDataBasePath + "nginx-proxy.conf.template";
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception initializing SGSProxyFilePrimaryData", e);
        }
    }
    
    @Override
    public StringBuilder getProxyFileData() {
        if (this.isProxyFileDataApplicable()) {
            this.logger.log(Level.INFO, "Primary SGS proxy file data included");
            return new StringBuilder(this.getProxyData());
        }
        this.logger.log(Level.INFO, "Primary SGS proxy file data not applicable");
        return new StringBuilder();
    }
    
    @Override
    public Boolean isProxyFileDataApplicable() {
        return true;
    }
}
