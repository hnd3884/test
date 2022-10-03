package com.me.devicemanagement.framework.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import org.json.JSONException;
import java.util.logging.Logger;
import org.json.JSONObject;

public class FrameworkConfigurations
{
    protected static JSONObject frameworkConfigurations;
    protected static Logger log;
    private static String file_pattern;
    public static String frameworkConfigurationFilePath;
    public static final int FILE_WRITE_SUCCESS_CODE = 3001;
    
    public static String getSpecificProperty(final String mainObjName, final String subObjName) throws JSONException {
        final JSONObject mainObj = (JSONObject)FrameworkConfigurations.frameworkConfigurations.get(mainObjName);
        return mainObj.get(subObjName).toString();
    }
    
    public static Object getSpecificPropertyIfExists(final String mainObjName, final String subObjName, final Object defaultValue) throws JSONException {
        if (FrameworkConfigurations.frameworkConfigurations.has(mainObjName)) {
            final JSONObject mainObj = (JSONObject)FrameworkConfigurations.frameworkConfigurations.get(mainObjName);
            if (mainObj.has(subObjName)) {
                return mainObj.get(subObjName);
            }
        }
        return defaultValue;
    }
    
    public static JSONObject getFrameworkConfigurations() {
        return FrameworkConfigurations.frameworkConfigurations;
    }
    
    public static int changeFrameworkConfiguration(final String fileName, final String configuration, final String keyData, final String value, final Boolean needServerReload) throws Exception {
        final JsonUtils jsonUtils2 = JsonUtils.class.newInstance();
        final JSONObject fwConfigTemp = JsonUtils.loadJsonFile(new File(fileName));
        final JSONObject conf = fwConfigTemp.getJSONObject(configuration);
        conf.put(keyData, (Object)value);
        fwConfigTemp.put(configuration, (Object)conf);
        final JsonUtils jsonUtils = new JsonUtils();
        if (!needServerReload) {
            JsonUtils.mergeJSON(FrameworkConfigurations.frameworkConfigurations, fwConfigTemp);
        }
        jsonUtils.writeJsonObjectIntoJsonFile(fwConfigTemp, fileName);
        return 3001;
    }
    
    public static JSONObject loadJsonFileFromOrderPropertiesFile(JSONObject jsonObject, final String fileLocation) throws IOException, JSONException {
        final String key = "fw.config.loader.order.";
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        final File propertyFile = new File(PropertyUtils.order_file);
        if (propertyFile.exists()) {
            final Properties order = FileUtils.readPropertyFile(propertyFile);
            final List<String> orderedFileList = PropertyUtils.loadPropertiesBasedOnKey(order, key);
            for (String conf_file_path : orderedFileList) {
                final String conf_file = conf_file_path;
                if (fileLocation != null) {
                    conf_file_path = FrameworkConfigurations.frameworkConfigurationFilePath + File.separator + conf_file;
                }
                final File file = new File(conf_file_path);
                final JSONObject tempJson = JsonUtils.loadJsonFile(file);
                if (tempJson != null) {
                    checkAndLoadJsonFromFilePath(tempJson);
                    JsonUtils.mergeJSON(jsonObject, tempJson);
                }
            }
        }
        else {
            FrameworkConfigurations.log.log(Level.SEVERE, "File does not exist: {0}", propertyFile.getName());
        }
        return jsonObject;
    }
    
    public static void checkAndLoadJsonFromFilePath(final JSONObject jsonObject) {
        final Iterator keys = jsonObject.keys();
        final JSONObject newJson = new JSONObject();
        try {
            while (keys.hasNext()) {
                final String parentKey = keys.next();
                final JSONObject subJson = jsonObject.optJSONObject(parentKey);
                if (subJson != null) {
                    final Iterator subKeys = subJson.keys();
                    while (subKeys.hasNext()) {
                        final String key = subKeys.next();
                        if (key.contains("json_file_path")) {
                            final File file = new File(PropertyUtils.serverPath + File.separator + subJson.get(key));
                            final JSONObject tempJson = JsonUtils.loadJsonFile(file);
                            newJson.put(parentKey, (Object)tempJson);
                        }
                    }
                }
            }
            JsonUtils.mergeJSON(jsonObject, newJson);
        }
        catch (final JSONException e) {
            FrameworkConfigurations.log.log(Level.SEVERE, "Exception in checkAndLoadJsonFromFilePath: ", (Throwable)e);
        }
    }
    
    public static void temporarilySetFrameworkConfigurations(final String key, final Object value) throws JSONException {
        FrameworkConfigurations.frameworkConfigurations.put(key, value);
    }
    
    static {
        FrameworkConfigurations.frameworkConfigurations = null;
        FrameworkConfigurations.log = Logger.getLogger(FrameworkConfigurations.class.getName());
        FrameworkConfigurations.file_pattern = "framework_settings((_[\\S]*)?_[\\d]+)?.json";
        FrameworkConfigurations.frameworkConfigurationFilePath = PropertyUtils.serverPath + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "configurations";
        try {
            FrameworkConfigurations.log.log(Level.INFO, "Going to Load framework configurations");
            FrameworkConfigurations.frameworkConfigurations = loadJsonFileFromOrderPropertiesFile(FrameworkConfigurations.frameworkConfigurations, FrameworkConfigurations.frameworkConfigurationFilePath);
        }
        catch (final Exception ex) {
            FrameworkConfigurations.log.log(Level.WARNING, "Exception while loading framework settings json", ex);
        }
    }
}
