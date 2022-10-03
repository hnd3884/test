package com.me.devicemanagement.onpremise.start.util;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.StartupUtil;

public class ApacheServerMigrationUtil extends ServerMigrationUtil implements ServerMigrationUtilAPI
{
    @Override
    public void modifyProductStartupForMigrationEnabled() throws Exception {
        generateModRewriteConf();
        final String serviceName = ServerMigrationUtil.getDCServiceName();
        StartupUtil.changeServiceStartupType(serviceName, "disabled");
        final String apacheServiceName = ServerMigrationUtil.getApacheServiceName();
        StartupUtil.changeServiceStartupType(apacheServiceName, "auto");
        final String startResult = WebServerUtil.apacheHttpdInvoke(ServerMigrationUtil.getServerHome(), "start");
        ApacheServerMigrationUtil.logger.log(Level.INFO, "Result of Start Apache Service :: {0} ", startResult);
    }
    
    @Override
    public void modifyProductStartupForMigrationDisabled() throws Exception {
        final String serviceName = ServerMigrationUtil.getDCServiceName();
        StartupUtil.changeServiceStartupType(serviceName, "auto");
        final String apacheServiceName = ServerMigrationUtil.getApacheServiceName();
        StartupUtil.changeServiceStartupType(apacheServiceName, "demand");
        clearModRewriteConf();
    }
}
