package com.me.devicemanagement.onpremise.server.general;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.dms.DMSDownloadEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.dms.DefaultDMSDownloadListener;

public class TimezoneCountryCodeDMSListener extends DefaultDMSDownloadListener
{
    private static Logger logger;
    
    public DMSDownloadEvent preFileDownload(final DMSDownloadEvent event) {
        try {
            final Properties headers = new Properties();
            final Properties userGeneralProps = FileAccessUtil.readProperties(CountryProvider.userSettings);
            if (userGeneralProps.containsKey(CountryProvider.ifModSinceKey) && userGeneralProps.getProperty(CountryProvider.ifModSinceKey).trim().length() > 0) {
                ((Hashtable<String, String>)headers).put("If-Modified-Since", userGeneralProps.getProperty(CountryProvider.ifModSinceKey).trim());
                TimezoneCountryCodeDMSListener.logger.log(Level.INFO, "Headers for crs download : " + headers);
            }
            event.setHeaders(headers);
        }
        catch (final Exception ex) {
            TimezoneCountryCodeDMSListener.logger.log(Level.WARNING, "TimezoneCountryCodeDMSListener : PrefileDownload failed");
        }
        return event;
    }
    
    public DMSDownloadEvent postDownloadEvent(final DMSDownloadEvent event) {
        try {
            final int statusCode = event.getStatusCode();
            if (statusCode == 0) {
                TimezoneCountryCodeDMSListener.logger.log(Level.INFO, "Time zone vs country db download success, Going to reinitialize exists db.");
                CountryProvider.getInstance().reinitializeTimeZoneIdVSCountryCodeDB();
                final Properties properties = new Properties();
                ((Hashtable<String, String>)properties).put(CountryProvider.ifModSinceKey, event.getDownloadStatus().getLastModifiedTime());
                FileAccessUtil.storeProperties(properties, CountryProvider.userSettings, true);
            }
            else if (statusCode == 10010) {
                TimezoneCountryCodeDMSListener.logger.log(Level.INFO, "Time zone vs country db not modified");
            }
            else {
                TimezoneCountryCodeDMSListener.logger.log(Level.WARNING, "Time zone vs country db download failed.");
            }
        }
        catch (final Exception ex) {
            TimezoneCountryCodeDMSListener.logger.log(Level.WARNING, "Time zone vs country db download failed.");
        }
        return event;
    }
    
    static {
        TimezoneCountryCodeDMSListener.logger = Logger.getLogger(TimezoneCountryCodeDMSListener.class.getName());
    }
}
