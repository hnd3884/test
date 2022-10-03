package com.me.mdm.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Properties;
import java.util.logging.Logger;

public class DownloadUtil
{
    public static Logger logger;
    
    public static int downloadFile(final String agentUrl, final String destination) throws Exception {
        try {
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
            ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
            final DownloadStatus downloadSatus = DownloadManager.getInstance().downloadFile(agentUrl, destination, new SSLValidationType[] { SSLValidationType.DEFAULT_SSL_VALIDATION });
            final int status = downloadSatus.getStatus();
            final String message = downloadSatus.getErrorMessage();
            if (status == 0) {
                DownloadUtil.logger.log(Level.INFO, "File succesfully downloaded.");
            }
            else {
                DownloadUtil.logger.log(Level.SEVERE, "Download FAILED!! {0}", message);
            }
            return status;
        }
        catch (final Exception e) {
            DownloadUtil.logger.log(Level.SEVERE, "Download failed!!", e);
            throw e;
        }
    }
    
    static {
        DownloadUtil.logger = Logger.getLogger("MDMLogger");
    }
}
