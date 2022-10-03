package com.me.devicemanagement.onpremise.start.util;

import java.util.logging.Level;
import com.adventnet.tools.prevalent.Wield;
import java.util.Properties;
import java.util.logging.Logger;

public class SymLicenseUtil
{
    private static final Logger LOGGER;
    private static SymLicenseUtil symLicenseUtil;
    private static Properties licenseProperties;
    
    private SymLicenseUtil() throws Exception {
        this.initLicenseProps();
    }
    
    public void initLicenseProps() throws Exception {
        final Wield wield = Wield.getInstance();
        wield.validateInvoke("AdventNet Opsym", false);
        if (!wield.isBare()) {
            SymLicenseUtil.LOGGER.log(Level.INFO, " ---- LICENSING FAILED. GOING TO SHUTDOWN OPSYM APPLICATION. PLEASE CONTACT SERVICEDESK-SUPPORT FOR FURTHER DETAILS ----");
            System.exit(0);
        }
        wield.validateInvoke(wield.getProductName(), false);
        SymLicenseUtil.licenseProperties = wield.getModuleProperties("Opsym");
    }
    
    public Properties getLicenseProperties() {
        return SymLicenseUtil.licenseProperties;
    }
    
    public static SymLicenseUtil getInstance() throws Exception {
        if (SymLicenseUtil.symLicenseUtil == null) {
            SymLicenseUtil.symLicenseUtil = new SymLicenseUtil();
        }
        return SymLicenseUtil.symLicenseUtil;
    }
    
    static {
        LOGGER = Logger.getLogger(SymLicenseUtil.class.getName());
        SymLicenseUtil.symLicenseUtil = null;
        SymLicenseUtil.licenseProperties = null;
    }
}
