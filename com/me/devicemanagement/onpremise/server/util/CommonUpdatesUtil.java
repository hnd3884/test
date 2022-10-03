package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.File;
import java.util.logging.Logger;

public class CommonUpdatesUtil extends com.me.devicemanagement.framework.server.util.CommonUpdatesUtil
{
    static Logger logger;
    String outFileName;
    private static CommonUpdatesUtil handler;
    
    public CommonUpdatesUtil() throws Exception {
        this.outFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "UpdatesCommon.json";
    }
    
    public static CommonUpdatesUtil getInstance() {
        if (CommonUpdatesUtil.handler == null) {
            try {
                CommonUpdatesUtil.handler = new CommonUpdatesUtil();
            }
            catch (final Exception e) {
                CommonUpdatesUtil.logger.info("Exception while creating commonupdatesutil obj" + e);
            }
        }
        return CommonUpdatesUtil.handler;
    }
    
    public String downloadCommonJSON() {
        CommonUpdatesUtil.logger.info("Inside downloadCommonJSON");
        final String updatesCheckerURL = ProductUrlLoader.getInstance().getValue("updates_common_check_url");
        if (updatesCheckerURL == null || updatesCheckerURL.trim().length() == 0) {
            return null;
        }
        try {
            final String proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
            if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                this.copyRemoteFileUsingHTTP(updatesCheckerURL, this.outFileName);
            }
            else {
                CommonUpdatesUtil.logger.log(Level.INFO, "Proxy is Not Defined, Hence couldn't download Updates JSON");
            }
            return this.outFileName;
        }
        catch (final Exception e) {
            CommonUpdatesUtil.logger.log(Level.SEVERE, "Exception while Downloading Updates JSON", e);
            return null;
        }
    }
    
    public void copyRemoteFileUsingHTTP(final String updChkURL, final String outFileName) {
        try {
            CommonUpdatesUtil.logger.log(Level.INFO, "Going to access the URL : " + updChkURL + " to find out the latest updates.");
            CommonUpdatesUtil.logger.log(Level.INFO, "Destination File : " + outFileName);
            final Properties formdata = SyMUtil.getTrackingProps();
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
            ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
            final String lastModifiedTime = UpdatesParamUtil.getUpdParameter("CommonUpdatesLastModified");
            if (lastModifiedTime != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ((Hashtable<String, String>)headers).put("If-Modified-Since", sdf.format(new Date(Long.valueOf(lastModifiedTime))));
            }
            final boolean enableChecksumValidation = Boolean.parseBoolean(ProductUrlLoader.getInstance().getValue("enableChecksumValidation", Boolean.FALSE.toString()));
            DownloadStatus downloadStatus;
            if (enableChecksumValidation) {
                downloadStatus = DMSDownloadUtil.getInstance().downloadRequestedFileForComponent("Framework", "commonUpdates", outFileName, formdata, headers).getDownloadStatus();
            }
            else {
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                downloadStatus = downloadMgr.downloadFile(updChkURL, outFileName, formdata, headers, new SSLValidationType[0]);
            }
            final int statusCode = downloadStatus.getStatus();
            if (statusCode == 0) {
                CommonUpdatesUtil.logger.log(Level.INFO, "Successfully Downloaded the  File : " + updChkURL);
            }
            else {
                CommonUpdatesUtil.logger.log(Level.INFO, " Download Failed for  File : " + updChkURL + "with ErrorCode:" + statusCode);
            }
            if (statusCode == 0 || statusCode == 10010) {
                UpdatesParamUtil.updateUpdParams("CommonUpdatesLastModified", System.currentTimeMillis() + "");
            }
        }
        catch (final Exception ee) {
            CommonUpdatesUtil.logger.log(Level.INFO, "Exception while Downloading File From" + updChkURL);
        }
    }
    
    static {
        CommonUpdatesUtil.logger = Logger.getLogger(CommonUpdatesUtil.class.getName());
        CommonUpdatesUtil.handler = null;
    }
}
