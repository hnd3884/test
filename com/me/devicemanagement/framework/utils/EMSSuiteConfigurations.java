package com.me.devicemanagement.framework.utils;

import java.util.logging.Level;
import java.io.File;
import org.json.JSONException;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.JSONObject;

public class EMSSuiteConfigurations
{
    protected static JSONObject emsSuiteConfigurations;
    protected static Logger log;
    public static String configurationFilePath;
    
    public static JSONObject getEmsSuiteConfigurations() throws IOException, JSONException {
        if (JSONObject.NULL.equals(EMSSuiteConfigurations.emsSuiteConfigurations) || EMSSuiteConfigurations.emsSuiteConfigurations.length() == 0) {
            EMSSuiteConfigurations.emsSuiteConfigurations = new JSONObject();
            EMSSuiteConfigurations.emsSuiteConfigurations = loadJsonFile(EMSSuiteConfigurations.emsSuiteConfigurations);
        }
        return EMSSuiteConfigurations.emsSuiteConfigurations;
    }
    
    public static JSONObject loadJsonFile(final JSONObject jsonObject) throws IOException, JSONException {
        final File file = new File(EMSSuiteConfigurations.configurationFilePath);
        if (file.exists() && file.isDirectory()) {
            final File[] listFiles;
            final File[] fileList = listFiles = file.listFiles();
            for (final File jsonFile : listFiles) {
                final Object tempJson = JsonUtils.loadJsonFileWithArray(jsonFile);
                if (tempJson != null) {
                    JsonUtils.mergeJSONWithArray(jsonObject, tempJson);
                }
            }
        }
        return jsonObject;
    }
    
    static {
        EMSSuiteConfigurations.emsSuiteConfigurations = new JSONObject();
        EMSSuiteConfigurations.log = Logger.getLogger(EMSSuiteConfigurations.class.getName());
        EMSSuiteConfigurations.configurationFilePath = PropertyUtils.serverPath + File.separator + "conf" + File.separator + "UEMS_Server" + File.separator + "configurations";
        try {
            EMSSuiteConfigurations.log.log(Level.INFO, "Going to Load framework configurations");
            EMSSuiteConfigurations.emsSuiteConfigurations = loadJsonFile(EMSSuiteConfigurations.emsSuiteConfigurations);
        }
        catch (final Exception ex) {
            EMSSuiteConfigurations.log.log(Level.WARNING, "Exception while loading framework settings json", ex);
        }
    }
}
