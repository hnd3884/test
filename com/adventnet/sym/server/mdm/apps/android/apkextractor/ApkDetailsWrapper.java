package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import org.json.JSONException;
import java.io.IOException;
import com.me.mdm.webclient.i18n.MDMI18N;
import java.util.logging.Level;
import org.json.JSONArray;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.logging.Logger;
import org.json.JSONObject;

public class ApkDetailsWrapper
{
    JSONObject allInfo;
    JSONObject info;
    String error;
    String apkName;
    private String status;
    private static Logger logger;
    
    public ApkDetailsWrapper() {
        this.allInfo = new JSONObject();
        this.info = new JSONObject();
        this.error = null;
        this.apkName = null;
    }
    
    public JSONObject getApkDetails(final String apkSourcePath) throws FileNotFoundException, IOException, JSONException {
        final File file = new File(apkSourcePath);
        if (!file.exists() || !apkSourcePath.endsWith(".apk")) {
            throw new FileNotFoundException(apkSourcePath);
        }
        final ApkExtractionHandler extractionHandler = new ApkExtractionHandler(apkSourcePath);
        boolean packageName = false;
        boolean label = false;
        boolean versionCode = false;
        boolean versionName = false;
        boolean icon = false;
        boolean debug = false;
        boolean minSDK = false;
        boolean targetSDK = false;
        boolean permission = false;
        boolean rstriction = false;
        boolean signing = false;
        boolean supportedScreen = false;
        try {
            extractionHandler.initialize();
            extractionHandler.dumpManifest();
            extractionHandler.dumpResources();
            extractionHandler.dumpAAPT();
            extractionHandler.listResources();
            extractionHandler.parseAAPT();
            this.info.put("PackageName", (Object)extractionHandler.getPackageIdentifier());
            packageName = true;
            this.info.put("package_label", (Object)extractionHandler.getPackageLabel());
            label = true;
            this.info.put("version_code", (Object)extractionHandler.getVersionCode());
            versionCode = true;
            this.info.put("VersionName", (Object)extractionHandler.getVersionName());
            versionName = true;
            this.info.put("min_sdk_version", (Object)extractionHandler.getMinSdkVersion());
            minSDK = true;
            this.info.put("target_sdk_version", (Object)extractionHandler.getTargetSdkVersion());
            targetSDK = true;
            this.info.put("icon", (Object)new JSONArray().put((Object)extractionHandler.getIcon()));
            icon = true;
            this.info.put("permissions", (Object)extractionHandler.getPermissions());
            permission = true;
            this.info.put("supported_screens", (Object)extractionHandler.getSupportedScreens());
            supportedScreen = true;
            extractionHandler.parseRestrictions();
            this.info.put("app_config_form", (Object)new JSONObject().put("restrictions", (Object)extractionHandler.getRestrictions()));
            rstriction = true;
            extractionHandler.parseSignature();
            this.info.put("signing_data", (Object)extractionHandler.getSigningData());
            signing = true;
            this.info.put("debug_enabled", extractionHandler.isDebugEnabled());
            debug = true;
            extractionHandler.parseKeyToolSignature();
            this.info.put("keytool_sign", (Object)extractionHandler.getKeyToolSignData());
        }
        catch (final Exception e) {
            this.error = e.getMessage();
            ApkDetailsWrapper.logger.log(Level.WARNING, "Error occured during execution : ", e);
            try {
                extractionHandler.deleteDumpFiles();
            }
            catch (final Exception e) {
                ApkDetailsWrapper.logger.log(Level.WARNING, "Could not delete dump files ", e);
            }
        }
        finally {
            try {
                extractionHandler.deleteDumpFiles();
            }
            catch (final Exception e2) {
                ApkDetailsWrapper.logger.log(Level.WARNING, "Could not delete dump files ", e2);
            }
        }
        final boolean status = packageName && versionCode && versionName;
        (this.allInfo = this.info).put("status", (Object)(status ? "success" : "failed"));
        if (!status) {
            this.allInfo.put("error", (Object)this.error);
            String reason = MDMI18N.getI18Nmsg("dc.mdm.app.apk_extract.corrupt");
            try {
                reason = MDMI18N.getMsg("mdm.app.apk_extraction_error@@@" + this.error, false);
            }
            catch (final Exception e2) {
                Logger.getLogger(ApkDetailsWrapper.class.getName()).log(Level.SEVERE, null, e2);
            }
            this.allInfo.put("extractError", (Object)"errorParsing");
            this.allInfo.put("errorReason", (Object)reason);
            this.allInfo.put("errorMsg", (Object)reason);
            this.allInfo.put("errorParams", (Object)reason);
        }
        return this.allInfo;
    }
    
    public String getStatus(final int noOfSuccess) {
        if (noOfSuccess <= 0) {
            return "failed";
        }
        if (noOfSuccess == 13) {
            return "success";
        }
        return "partially_successful";
    }
    
    public JSONObject getAllInfo() {
        return this.allInfo;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    static {
        ApkDetailsWrapper.logger = ApkExtractionUtilities.getLogger();
    }
}
