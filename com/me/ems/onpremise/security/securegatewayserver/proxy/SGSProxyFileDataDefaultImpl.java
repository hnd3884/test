package com.me.ems.onpremise.security.securegatewayserver.proxy;

import java.util.logging.Logger;

public class SGSProxyFileDataDefaultImpl
{
    private static final Logger LOGGER;
    
    public String getProxyFileData() throws Exception {
        return new SGSProxyFilePrimaryData().getProxyFileData().toString();
    }
    
    static {
        LOGGER = Logger.getLogger(SGSProxyFileDataDefaultImpl.class.getName());
    }
}
