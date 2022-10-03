package com.me.ems.onpremise.security.securegatewayserver.proxy;

import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class BaseSGSProxyFileData implements SGSProxyFileData
{
    protected String filePath;
    protected Logger logger;
    protected static String primaryProxyDataBasePath;
    protected static String dynamicModuleBasePath;
    
    public BaseSGSProxyFileData() {
        this.logger = Logger.getLogger("SecurityLogger");
    }
    
    protected String getProxyData() {
        String proxyData = new String();
        try {
            final byte[] fileContent = Files.readAllBytes(Paths.get(this.filePath, new String[0]));
            proxyData = new String(fileContent);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception reading file {0} {1}", new Object[] { this.filePath, e });
        }
        return proxyData;
    }
    
    @Override
    public abstract StringBuilder getProxyFileData();
    
    @Override
    public abstract Boolean isProxyFileDataApplicable();
    
    static {
        BaseSGSProxyFileData.primaryProxyDataBasePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "security" + File.separator;
        BaseSGSProxyFileData.dynamicModuleBasePath = BaseSGSProxyFileData.primaryProxyDataBasePath + "nginxDynamicModules" + File.separator;
    }
}
