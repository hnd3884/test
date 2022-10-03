package com.adventnet.sym.server.mdm.apps.android;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import com.adventnet.sym.server.mdm.apps.android.apkextractor.ApkExtractionHandler;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.net.URLEncoder;
import java.util.logging.Level;
import com.me.mdm.webclient.i18n.MDMI18N;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class AndroidAPKExtractor
{
    static final String PACKAGE_NAME = "PackageName";
    static final String VERSION_NAME = "VersionName";
    private static final String MINIMUM_SDK_VERSION = "MinSdkVersion";
    static String PACKAGE_EXPRESSION;
    static String VERSION_EXPRESSION;
    static String MINIMUM_SDK_EXPRESSION;
    Logger logger;
    
    public AndroidAPKExtractor() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public abstract JSONObject getAndroidAppsDetails(final String p0) throws JSONException;
    
    protected JSONObject getErrorProps(String reason) throws JSONException {
        final JSONObject apkProps = new JSONObject();
        apkProps.put("extractError", (Object)"errorParsing");
        try {
            reason = MDMI18N.getMsg(reason, false);
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidAPKExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        apkProps.put("errorMsg", (Object)reason);
        apkProps.put("errorReason", (Object)reason);
        return apkProps;
    }
    
    protected JSONObject getReqPropsForAddApp() throws JSONException {
        final JSONObject apkProp = new JSONObject();
        apkProp.put("PackageName", (Object)AndroidAPKExtractor.PACKAGE_EXPRESSION);
        apkProp.put("VersionName", (Object)AndroidAPKExtractor.VERSION_EXPRESSION);
        return apkProp;
    }
    
    protected abstract JSONObject getAPKProperties(final JSONObject p0) throws JSONException;
    
    protected abstract String getPropertyValue(final String p0);
    
    private String extractCommonErrorString(String reason) {
        try {
            reason = reason.replace("mdm.app.apk_extraction_error@@@", "");
            final int indexOf = reason.indexOf("ERROR");
            if (indexOf < 0) {
                return null;
            }
            reason = reason.substring(indexOf);
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidAPKExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reason;
    }
    
    private String appendHelpLink(final String errorMsg, final String searchError) {
        try {
            String helpText = "https://stackoverflow.com/search?q=" + URLEncoder.encode(searchError);
            helpText = errorMsg + "@@@<l>" + helpText;
            helpText = MDMI18N.getMsg(helpText, false);
            return helpText;
        }
        catch (final Exception ex) {
            Logger.getLogger(AndroidAPKExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return errorMsg;
        }
    }
    
    protected JSONObject getSignatureInfo(final String filePath) throws Exception {
        String folderPath = null;
        try {
            final String[] fileNameSplit = String.valueOf(filePath).split("\\.");
            final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
            final String fileName = Long.toString(MDMUtil.getCurrentTime()) + "." + strContentType;
            final UUID randomid = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + randomid;
            ApkExtractionHandler extractionHandler = null;
            extractionHandler = new ApkExtractionHandler(folderPath);
            extractionHandler.initialize();
            final String completedFileName = folderPath + File.separator + fileName;
            final File file = new File(completedFileName);
            extractionHandler = new ApkExtractionHandler(file.getCanonicalPath());
            extractionHandler.initialize();
            new FileFacade().writeFile(completedFileName, ApiFactoryProvider.getFileAccessAPI().readFile(filePath));
            extractionHandler.parseKeyToolSignature();
            return extractionHandler.getKeyToolSignData();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot fetch existing signature data");
            try {
                if (folderPath != null) {
                    ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(folderPath);
                    MDMApiFactoryProvider.getUploadDownloadAPI().deleteDirectoryInAppServer(folderPath);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.INFO, "Cannot delete temp directory", e);
            }
        }
        finally {
            try {
                if (folderPath != null) {
                    ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(folderPath);
                    MDMApiFactoryProvider.getUploadDownloadAPI().deleteDirectoryInAppServer(folderPath);
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.INFO, "Cannot delete temp directory", e2);
            }
        }
        return null;
    }
}
