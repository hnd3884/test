package com.me.devicemanagement.framework.webclient.common;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import java.util.Properties;

public class OnlineUrlLoader
{
    private static OnlineUrlLoader onlineUrlLoader;
    private static Properties urlProperties;
    Logger logger;
    
    private OnlineUrlLoader() {
        this.logger = Logger.getLogger(OnlineUrlLoader.class.getName());
        try {
            final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "online_help_urls.conf";
            OnlineUrlLoader.urlProperties = FileAccessUtil.readProperties(fname);
            this.logger.log(Level.INFO, "OnlineUrlLoader:OnlinrUrlLoader(): properties are := ", OnlineUrlLoader.urlProperties);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exeption in OnlineUrlLoader:OnlinrUrlLoader() := ", ex);
        }
    }
    
    public static OnlineUrlLoader getInstance() {
        if (OnlineUrlLoader.onlineUrlLoader == null) {
            OnlineUrlLoader.onlineUrlLoader = new OnlineUrlLoader();
        }
        return OnlineUrlLoader.onlineUrlLoader;
    }
    
    public Properties getURLProperites() {
        return OnlineUrlLoader.urlProperties;
    }
    
    public String getValue(String urlValue, final boolean isFileLookupRequired) {
        if (isFileLookupRequired) {
            urlValue = this.getValue(urlValue);
        }
        return UrlReplacementUtil.replaceUrlAndAppendTrackCode(urlValue);
    }
    
    private String getValue(final String key) {
        final String value = ((Hashtable<K, String>)OnlineUrlLoader.urlProperties).get(key);
        if (value != null) {
            return value;
        }
        return key;
    }
    
    static {
        OnlineUrlLoader.onlineUrlLoader = null;
        OnlineUrlLoader.urlProperties = new Properties();
    }
}
