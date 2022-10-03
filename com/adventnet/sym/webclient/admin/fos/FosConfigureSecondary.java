package com.adventnet.sym.webclient.admin.fos;

import com.adventnet.persistence.cache.CacheRepository;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.cache.CacheRepositoryImpl;
import java.util.List;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import java.util.logging.Level;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.logging.Logger;

public class FosConfigureSecondary
{
    private static Logger logger;
    
    public static void main(final String[] args) throws Exception {
        DCLogUtil.initLogger();
        Boolean checksSuccess = Boolean.TRUE;
        final StringBuilder errorMsg = new StringBuilder();
        if (!FailoverServerUtil.isFosConfigured()) {
            printWarning("Failover settings are not configured ..");
            FosConfigureSecondary.logger.log(Level.SEVERE, "Failover settings are not configured ..");
            System.exit(1);
        }
        DCConsoleOut.println("Going to Check Prerequisities..");
        DCConsoleOut.println("\n");
        final String secondaryIP = FailoverServerUtil.failoverUserProps.getProperty("SecondaryServerIP");
        final List staticIP = FailoverServerUtil.getStaticIPs();
        if (!FailoverServerUtil.isStaticIPMatches(secondaryIP)) {
            checksSuccess = Boolean.FALSE;
            FosConfigureSecondary.logger.log(Level.SEVERE, "Secondary IP configured {0} does not match with machine''s static IP {1}", new Object[] { secondaryIP, staticIP.toString() });
            errorMsg.append("Secondary IP configured" + secondaryIP + "does not match with machine''s static IP" + staticIP.toString());
            errorMsg.append("\n");
        }
        else if (!FailoverServerUtil.isSecondaryStatic()) {
            checksSuccess = Boolean.FALSE;
            FosConfigureSecondary.logger.log(Level.SEVERE, "Configured failover server IP{0} is dynamic.\\n", secondaryIP);
            errorMsg.append("Configured failover server IP" + secondaryIP + " is dynamic.\\n");
            errorMsg.append("\n");
        }
        if (!FailoverServerUtil.hasRemoteDB()) {
            checksSuccess = Boolean.FALSE;
            FosConfigureSecondary.logger.log(Level.SEVERE, "Remote DB check failed");
            errorMsg.append("Database is not in remote location.");
            errorMsg.append("\n");
        }
        initializeCache();
        final long diskSpace = FailoverServerUtil.getFreeDiskSpace();
        if (!FailoverServerUtil.isDiskSpaceEnough(diskSpace)) {
            checksSuccess = Boolean.FALSE;
            FosConfigureSecondary.logger.log(Level.SEVERE, "Not enough Disk Space");
            errorMsg.append("Not enough Disk Space");
            errorMsg.append("\n");
        }
        if (FailoverServerUtil.isTimeDiff()) {
            FosConfigureSecondary.logger.log(Level.SEVERE, "Two servers have time difference");
            checksSuccess = Boolean.FALSE;
            errorMsg.append("Two servers have time difference");
            errorMsg.append("\n");
        }
        if (!checksSuccess) {
            DCConsoleOut.println("Fix the below errors to start the failover server");
            printWarning(errorMsg.toString());
            System.exit(1);
        }
        else if (!FailoverServerUtil.isPrimaryAlive()) {
            DCConsoleOut.println("Failover server should be started only after Main server. Start your Main server first.");
            System.exit(1);
        }
        DCConsoleOut.println("Success");
        System.exit(0);
    }
    
    public static void printWarning(final String msg) {
        DCConsoleOut.println("----------------------------------------------------------");
        DCConsoleOut.println(msg);
        DCConsoleOut.println("-----------------------------------------------------------");
    }
    
    private static void initializeCache() {
        final CacheRepository cacheRepository = (CacheRepository)new CacheRepositoryImpl();
        cacheRepository.initialize(-1, true);
        cacheRepository.setCloningStatus(false);
        cacheRepository.setCachingStatus(true);
        CacheManager.setCacheRepository(cacheRepository);
    }
    
    static {
        FosConfigureSecondary.logger = Logger.getLogger("FailoverMonitorLogger");
    }
}
