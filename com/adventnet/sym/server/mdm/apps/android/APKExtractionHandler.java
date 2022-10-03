package com.adventnet.sym.server.mdm.apps.android;

import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.apps.EnterpriseAppExtractor;

public class APKExtractionHandler extends EnterpriseAppExtractor
{
    public static final int AAPT_EXTRACTOR = 1;
    public static final int BX2_EXTRACTOR = 2;
    public static final int ADVANCED_AAPT_EXTRACTOR = 3;
    private Logger logger;
    
    public APKExtractionHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Deprecated
    public JSONObject extractApk(final String fileSourceDestFileName) throws Exception {
        JSONObject apkProps = null;
        try {
            if (fileSourceDestFileName != null && !fileSourceDestFileName.equals("")) {
                apkProps = this.getAppDetails(fileSourceDestFileName);
            }
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while extracting APK:  ", (Throwable)ex);
        }
        return apkProps;
    }
    
    @Override
    public JSONObject getAppDetails(final String apkPath) throws JSONException {
        JSONObject apkProps = this.getAndroidAppsDetails(apkPath, 3);
        if (apkProps.has("extractError") && apkProps.get("extractError").equals("commandFailure")) {
            this.logger.log(Level.WARNING, "Retrying with apk-extract.jar");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Android_Module", "aaptCmdAbnormalFailCount");
            apkProps = this.getAndroidAppsDetails(apkPath, 2);
        }
        this.logger.log(Level.INFO, "apkProps:{0}", apkProps);
        return apkProps;
    }
    
    public JSONObject getAndroidAppsDetails(final String apkPath, final int implementationType) throws JSONException {
        AndroidAPKExtractor extractor;
        if (implementationType == 1) {
            extractor = new AaptApkExtractorImpl();
        }
        else if (implementationType == 3) {
            extractor = new AdvancedAaptApkExtractorImpl();
        }
        else {
            extractor = new AndroidBXApkExtractorImpl();
        }
        return extractor.getAndroidAppsDetails(apkPath);
    }
    
    @Override
    public JSONObject getAppSignatureDetails(final String appFilePath) throws Exception {
        if (appFilePath.endsWith(".apk")) {
            return new AdvancedAaptApkExtractorImpl().getSignatureInfo(appFilePath);
        }
        this.logger.log(Level.WARNING, "File {0} was tried to be accessed during app signature validation", new Object[] { appFilePath });
        throw new Exception("Expected APK file,  not fetched");
    }
}
