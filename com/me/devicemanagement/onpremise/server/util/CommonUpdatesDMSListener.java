package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.dms.DMSDownloadEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.dms.DefaultDMSDownloadListener;

public class CommonUpdatesDMSListener extends DefaultDMSDownloadListener
{
    private static final Logger LOGGER;
    
    public DMSDownloadEvent preFileDownload(final DMSDownloadEvent event) {
        final Properties formData = SyMUtil.getTrackingProps();
        final Properties headers = new Properties();
        ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
        ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
        final String lastModifiedTime = UpdatesParamUtil.getUpdParameter("CommonUpdatesLastModified");
        if (ApiFactoryProvider.getFileAccessAPI().isFileExists(event.getDestinationLocation()) && lastModifiedTime != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            ((Hashtable<String, String>)headers).put("If-Modified-Since", sdf.format(new Date(Long.valueOf(lastModifiedTime))));
        }
        event.setFormData(formData);
        event.setHeaders(headers);
        return event;
    }
    
    public DMSDownloadEvent postDownloadEvent(final DMSDownloadEvent event) {
        final int statusCode = event.getStatusCode();
        if (statusCode == 0) {
            CommonUpdatesDMSListener.LOGGER.log(Level.INFO, "Successfully Downloaded the  File : " + event.getCrsFilePath());
        }
        else {
            CommonUpdatesDMSListener.LOGGER.log(Level.INFO, " Download Failed for  File : " + event.getCrsFilePath() + "with ErrorCode:" + statusCode);
        }
        if (statusCode == 10010) {
            UpdatesParamUtil.updateUpdParams("CommonUpdatesLastModified", System.currentTimeMillis() + "");
        }
        return event;
    }
    
    static {
        LOGGER = Logger.getLogger(CommonUpdatesDMSListener.class.getName());
    }
}
