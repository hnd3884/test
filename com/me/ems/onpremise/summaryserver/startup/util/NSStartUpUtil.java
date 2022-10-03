package com.me.ems.onpremise.summaryserver.startup.util;

import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class NSStartUpUtil extends com.me.devicemanagement.onpremise.start.util.NSStartUpUtil
{
    public static final String PRODUCT_CONF;
    public static final String ACTIVE_PRODUCT_CODE;
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1) {
                showSyntaxAndExit(true);
            }
            final String action = args[0];
            if (!SyMUtil.isSummaryServer()) {
                performNSServerAction(action);
            }
            else {
                NSStartUpUtil.LOGGER.log(Level.WARNING, "This product is detect as Summary Server so skipping the NS operations");
            }
        }
        catch (final Exception ex) {
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Caught some error: " + ex);
            NSStartUpUtil.LOGGER.log(Level.WARNING, "Requested operation is failed.");
            ex.printStackTrace();
            showSyntaxAndExit(true);
        }
    }
    
    static {
        PRODUCT_CONF = "conf" + File.separator + "product.conf";
        final String file = System.getProperty("server.home") + File.separator + NSStartUpUtil.PRODUCT_CONF;
        Properties properties = null;
        try {
            properties = readProperties(file);
        }
        catch (final Exception e) {
            NSStartUpUtil.LOGGER.log(Level.SEVERE, "Unable to read  Product config properties");
        }
        ACTIVE_PRODUCT_CODE = properties.getProperty("activeproductcodes", "");
    }
}
