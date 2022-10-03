package com.me.ems.onpremise.security.securegatewayserver.core;

import java.util.Hashtable;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class SecureGatewayServerPropertiesUtils
{
    private static final Logger LOGGER;
    private static String sgsPropsFile;
    private static Properties sgsProperties;
    
    public static String getSGSProperty(final String key) {
        return ((Hashtable<K, String>)SecureGatewayServerPropertiesUtils.sgsProperties).getOrDefault(key, null);
    }
    
    public static void setSGSProperty(final String key, final String value) {
        SecureGatewayServerPropertiesUtils.sgsProperties.setProperty(key, value);
        setAndReloadSGSEntries(getAllSGSProperties());
        FwsUtil.regenerateProps();
    }
    
    public static Map getAllSGSProperties() {
        return new HashMap() {
            {
                this.putAll(SecureGatewayServerPropertiesUtils.sgsProperties);
            }
        };
    }
    
    public static Boolean isSGSPropetiesNotEmpty() {
        return !SecureGatewayServerPropertiesUtils.sgsProperties.isEmpty() && SecureGatewayServerPropertiesUtils.sgsProperties.size() != 0;
    }
    
    public static void regenerateProps() {
        try {
            final String serverHome = System.getProperty("server.home");
            SecureGatewayServerPropertiesUtils.sgsProperties = FileAccessUtil.readProperties(serverHome + File.separator + SecureGatewayServerPropertiesUtils.sgsPropsFile);
        }
        catch (final Exception e) {
            SecureGatewayServerPropertiesUtils.LOGGER.log(Level.SEVERE, "Exception while intiailizing properties", e);
        }
    }
    
    public static void setAndReloadSGSEntries(final Map props) {
        try {
            final String confFilePath = System.getProperty("server.home") + File.separator + SecureGatewayServerPropertiesUtils.sgsPropsFile;
            if (!new File(confFilePath).exists()) {
                new File(confFilePath).createNewFile();
            }
            FileAccessUtil.writeMapAsPropertiesIntoFile(props, confFilePath, "");
            regenerateProps();
        }
        catch (final Exception exception) {
            SecureGatewayServerPropertiesUtils.LOGGER.log(Level.SEVERE, "Exception occured while setting and reloading SGS properties.", exception);
        }
    }
    
    public static void cleanUpSGSEntries() {
        final String methodName = "cleanUpSGSEntries";
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            final Map sgspropsMap = getAllSGSProperties();
            sgspropsMap.put("sgsStatus", "false");
            final String sgsTrialValidity = SecureGatewayServerPropertiesUtils.sgsProperties.getProperty("FwsTrialValidity");
            if (sgsTrialValidity != null) {
                sgspropsMap.put("FwsTrialValidity", sgsTrialValidity);
            }
            final String isSGSTrialed = SecureGatewayServerPropertiesUtils.sgsProperties.getProperty("isFwsTrialed");
            if (isSGSTrialed != null) {
                sgspropsMap.put("isFwsTrialed", isSGSTrialed);
            }
            else {
                sgspropsMap.put("isFwsTrialed", CryptoUtil.encrypt("false"));
            }
            final String sgsTrialPeriod = SecureGatewayServerPropertiesUtils.sgsProperties.getProperty("FwsTrialPeriod");
            if (sgsTrialPeriod != null) {
                sgspropsMap.put("FwsTrialPeriod", sgsTrialPeriod);
            }
            else {
                sgspropsMap.put("FwsTrialPeriod", CryptoUtil.encrypt("30"));
            }
            setAndReloadSGSEntries(sgspropsMap);
        }
        catch (final Exception exception) {
            SecureGatewayServerPropertiesUtils.LOGGER.log(Level.SEVERE, "Exception occured in " + methodName);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("SecurityLogger");
        SecureGatewayServerPropertiesUtils.sgsPropsFile = "conf" + File.separator + "fwsSettings.conf";
        SecureGatewayServerPropertiesUtils.sgsProperties = new Properties();
        try {
            final String serverHome = System.getProperty("server.home");
            SecureGatewayServerPropertiesUtils.sgsProperties = FileAccessUtil.readProperties(serverHome + File.separator + SecureGatewayServerPropertiesUtils.sgsPropsFile);
            SecureGatewayServerPropertiesUtils.LOGGER.log(Level.INFO, "Forwarding server properties loaded" + SecureGatewayServerPropertiesUtils.sgsProperties);
        }
        catch (final Exception e) {
            SecureGatewayServerPropertiesUtils.LOGGER.log(Level.SEVERE, "Exception while intiailizing properties", e);
        }
    }
}
