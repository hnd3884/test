package com.zoho.mickey.ha;

import java.util.logging.Level;
import com.zoho.clustering.failover.FOSUtil;
import java.util.logging.Logger;

public class IPHandler
{
    private static final Logger OUT;
    
    protected static boolean addIP(final HAConfig config) throws Exception {
        int consecutiveFailureCount = 0;
        ipPrecheck(config);
        while (consecutiveFailureCount < config.ipCheckRetryCount()) {
            if (_addIP(config.publicIP(), config.publicIPIfName(), config.publicIPNetMask())) {
                consecutiveFailureCount = 0;
                return true;
            }
            ++consecutiveFailureCount;
            Thread.sleep(config.ipCheckIntervalInSecs() * 1000);
        }
        if (consecutiveFailureCount >= config.ipCheckRetryCount()) {
            if (!isInterfaceUp(config.publicIPIfName())) {
                config.errorHandler().handleError(new HAException(HAErrorCode.ERROR_IF_DOWN, "Network Interface Card is NOT working"));
            }
            else {
                config.errorHandler().handleError(new HAException(HAErrorCode.ERROR_IP_BINDING, "Not able to bind the 'public_ip' to this node"));
            }
        }
        return false;
    }
    
    private static boolean _addIP(final String ipAddr, final String ifName, final String netmask) {
        try {
            final int status = FOSUtil.getInst().addIP(ipAddr, ifName, netmask);
            return status == 0;
        }
        catch (final RuntimeException ignored) {
            IPHandler.OUT.log(Level.SEVERE, "", ignored);
            return false;
        }
    }
    
    protected static void deleteIP(final HAConfig config) throws Exception {
        final int status = FOSUtil.getInst().deleteIP(config.publicIP());
        if (status != 0) {
            config.errorHandler().handleError(new HAException(HAErrorCode.ERROR_IP_UNBINDING, "Problem while starting HA. Unbinding of public_ip failed with status [" + status + "]"));
        }
    }
    
    private static boolean isOtherNodePresentWithIP(final HAConfig config) throws Exception {
        deleteIP(config);
        for (int i = 0; i < 4; ++i) {
            final boolean isPresent = FOSUtil.getInst().ping(config.publicIP());
            if (!isPresent) {
                return false;
            }
            ++i;
            Thread.sleep(1000L);
        }
        return true;
    }
    
    private static boolean isInterfaceUp(final String ifName) throws InterruptedException {
        for (int i = 0; i < 4; ++i) {
            final boolean isUp = FOSUtil.getInst().isInterfaceUp(ifName);
            if (isUp) {
                return true;
            }
            ++i;
            Thread.sleep(1000L);
        }
        return false;
    }
    
    private static void ipPrecheck(final HAConfig config) throws Exception {
        if (isOtherNodePresentWithIP(config)) {
            config.errorHandler().handleError(new HAException(HAErrorCode.ERROR_IP_CLASH, "Another node present in network with 'public_ip' binded to it"));
        }
    }
    
    public static boolean ping(final String host) {
        return FOSUtil.getInst().ping(host);
    }
    
    static {
        OUT = Logger.getLogger(IPHandler.class.getName());
    }
}
