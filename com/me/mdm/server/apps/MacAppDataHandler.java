package com.me.mdm.server.apps;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;

public class MacAppDataHandler extends IOSAppDatahandler
{
    public MacAppDataHandler(final JSONObject params) {
        super(params);
    }
    
    @Override
    public JSONObject addEnterpriseApp(final JSONObject requestJson) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            this.addAppProps(requestJson);
            jsonObject = super.addEnterpriseApp(requestJson);
        }
        catch (final JSONException e) {
            MacAppDataHandler.logger.log(Level.SEVERE, "Exception in adding macOS enterprise app", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[] { "" });
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject updateEnterpriseApp(final JSONObject requestJson) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            this.addAppProps(requestJson);
            jsonObject = super.updateEnterpriseApp(requestJson);
        }
        catch (final JSONException e) {
            MacAppDataHandler.logger.log(Level.SEVERE, "Exception in updating macOS enterprise app", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[] { "" });
        }
        return jsonObject;
    }
    
    private void addAppProps(final JSONObject requestJson) throws Exception {
        requestJson.put("app_info", (Object)(this.appProps = this.getAppDetailsFromAppFile(requestJson)));
        requestJson.put("supported_devices", 16);
    }
    
    public JSONObject modifyEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJSON) throws Exception {
        final String appFilePath = jsonObject.getString("APP_FILE");
        final String identifier = String.valueOf(this.appProps.get("bundle_identifier"));
        final Long size = ApiFactoryProvider.getFileAccessAPI().getFileSize(appFilePath);
        jsonObject.put("PACKAGE_SHA", (Object)ChecksumProvider.getInstance().GetSHA256CheckSum(appFilePath));
        jsonObject.put("PACKAGE_MD5", (Object)ChecksumProvider.getInstance().GetMD5HashFromFile(appFilePath));
        jsonObject.put("PACKAGE_SIZE", (Object)size);
        jsonObject.put("FILE_LOCATION", (Object)appFilePath);
        jsonObject.put("packageIdentifier", (Object)identifier);
        return jsonObject;
    }
    
    @Override
    public JSONObject getAppDetailsFromAppFile(final JSONObject requestJson) throws Exception {
        final JSONObject jsonObject = requestJson.has("msg_body") ? requestJson.getJSONObject("msg_body") : requestJson;
        final String bundleID = JSONUtil.optStringIgnoreKeyCase(jsonObject, "bundle_identifier");
        final String version = JSONUtil.optStringIgnoreKeyCase(jsonObject, "app_version", "--");
        final String versionCode = JSONUtil.optStringIgnoreKeyCase(jsonObject, "app_version_code", version);
        jsonObject.put("packagename", (Object)bundleID);
        jsonObject.put("PackageName", (Object)bundleID);
        jsonObject.put("VersionName", (Object)version);
        jsonObject.put("version_code", (Object)versionCode);
        return jsonObject;
    }
}
