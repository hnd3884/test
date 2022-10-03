package com.zoho.security.wafad;

import java.io.InputStream;
import java.util.logging.Level;
import com.zoho.security.eventfw.EventDataProcessor;
import java.util.logging.Logger;

public class WAFAttackDiscoveryUtil
{
    private static final Logger LOGGER;
    private static final String MONITORING_EVENT_CONFIG_FILE = "waf-monitoringevents.xml";
    private static boolean eventXmlInitialized;
    private static boolean wafAgentAttached;
    
    public static void initEventXML() {
        if (WAFAttackDiscoveryUtil.eventXmlInitialized) {
            return;
        }
        WAFAttackDiscoveryUtil.eventXmlInitialized = true;
        final InputStream monitoringEventConfigStream = WAFAttackDiscovery.class.getResourceAsStream("/waf-monitoringevents.xml");
        if (monitoringEventConfigStream != null) {
            EventDataProcessor.init(monitoringEventConfigStream, "waf-monitoringevents.xml");
            WAFAttackDiscoveryUtil.LOGGER.log(Level.INFO, "{0} Successfully loaded from ClassLoader.", new Object[] { "waf-monitoringevents.xml" });
        }
        else {
            WAFAttackDiscoveryUtil.LOGGER.log(Level.WARNING, "\"{0}\" file not found.", "waf-monitoringevents.xml");
        }
    }
    
    public static boolean isWafAgentAttached() {
        return WAFAttackDiscoveryUtil.wafAgentAttached;
    }
    
    static {
        LOGGER = Logger.getLogger(WAFAttackDiscoveryUtil.class.getName());
        try {
            Class.forName("com.adventnet.iam.security.SecurityRequestWrapper", false, WAFAttackDiscovery.class.getClassLoader());
            WAFAttackDiscoveryUtil.wafAgentAttached = true;
        }
        catch (final ClassNotFoundException cnfe) {
            WAFAttackDiscoveryUtil.LOGGER.log(Level.WARNING, "WAF Agent not attached.");
        }
    }
}
