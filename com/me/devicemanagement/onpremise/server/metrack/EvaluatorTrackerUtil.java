package com.me.devicemanagement.onpremise.server.metrack;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Level;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;

public class EvaluatorTrackerUtil implements EvaluatorAPI
{
    private static Logger logger;
    private static ConcurrentHashMap<String, JSONObject> moduleVsPageMap;
    private static EvaluatorTrackerUtil evaluatorTrackerUtil;
    
    public static EvaluatorTrackerUtil getInstance() {
        if (EvaluatorTrackerUtil.evaluatorTrackerUtil == null) {
            EvaluatorTrackerUtil.evaluatorTrackerUtil = new EvaluatorTrackerUtil();
        }
        return EvaluatorTrackerUtil.evaluatorTrackerUtil;
    }
    
    public Properties getModuleTrackerProperties(final HashMap<String, String> meTrackKeyVsModule) {
        final Properties moduleTrackerProperties = new Properties();
        if (meTrackKeyVsModule != null) {
            for (final Map.Entry<String, String> entry : meTrackKeyVsModule.entrySet()) {
                moduleTrackerProperties.setProperty(entry.getKey(), String.valueOf(this.getJSONFromFileForModule(entry.getValue())));
            }
        }
        return moduleTrackerProperties;
    }
    
    private JSONObject loadJSONFromFile() {
        try {
            final File trackerFile = new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "TrackTrial.json");
            if (trackerFile.isFile() && trackerFile.exists()) {
                return new JSONObject(IOUtils.toString((Reader)new FileReader(trackerFile)));
            }
        }
        catch (final Exception ex) {
            EvaluatorTrackerUtil.logger.log(Level.SEVERE, "Exception in obtaining the json object from the TrackTrial.json file", ex);
        }
        return new JSONObject();
    }
    
    public void addOrIncrementClickCountForTrialUsers(final String moduleName, final String pageName) {
        try {
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            if (licenseType.equalsIgnoreCase("T") && pageName != null) {
                JSONObject moduleJSON = EvaluatorTrackerUtil.moduleVsPageMap.get(moduleName);
                if (moduleJSON == null) {
                    moduleJSON = new JSONObject();
                    moduleJSON.put(pageName, 1);
                }
                else {
                    final int clickCount = moduleJSON.optInt(pageName);
                    moduleJSON.put(pageName, clickCount + 1);
                }
                EvaluatorTrackerUtil.moduleVsPageMap.put(moduleName, moduleJSON);
            }
        }
        catch (final Exception ex) {
            EvaluatorTrackerUtil.logger.log(Level.SEVERE, "Exception in adding or incrementing click count for trial users", ex);
        }
    }
    
    public void addOrIncrementClickCountForAll(final String moduleName, final String pageName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void addOrIncrementOnDemandActionsCount(final String moduleName, final String pageName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static JSONObject getModuleJSONObject() {
        return new JSONObject((Map)EvaluatorTrackerUtil.moduleVsPageMap);
    }
    
    public static void cleanUpEvaluatorTrack() {
        EvaluatorTrackerUtil.moduleVsPageMap = new ConcurrentHashMap<String, JSONObject>();
    }
    
    public JSONObject getJSONFromFileForModule(final String moduleName) {
        if (LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T")) {
            try {
                final JSONObject jsonFromFile = this.loadJSONFromFile();
                return jsonFromFile.has(moduleName) ? jsonFromFile.getJSONObject(moduleName) : new JSONObject();
            }
            catch (final JSONException ex) {
                EvaluatorTrackerUtil.logger.log(Level.SEVERE, "Exception in obtaining the json for module" + moduleName, (Throwable)ex);
            }
        }
        return new JSONObject();
    }
    
    public static void writeJSONToFile() {
        if (LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T")) {
            final String trackerFile = System.getProperty("server.home") + File.separator + "bin" + File.separator + "TrackTrial.json";
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            try {
                if (fileAccessAPI.isFileExists(trackerFile)) {
                    final JSONObject jsonFromFile = new JSONObject(IOUtils.toString((Reader)new InputStreamReader(fileAccessAPI.readFile(trackerFile))));
                    final JSONObject jsonFromServer = getModuleJSONObject();
                    EvaluatorTrackerUtil.logger.log(Level.INFO, "JSON From File" + jsonFromFile);
                    EvaluatorTrackerUtil.logger.log(Level.INFO, "JSON From Memory" + jsonFromServer);
                    addOrUpdateJSON(jsonFromFile, jsonFromServer);
                    fileAccessAPI.writeFile(trackerFile, jsonFromFile.toString().getBytes());
                    cleanUpEvaluatorTrack();
                }
                else {
                    fileAccessAPI.writeFile(trackerFile, getModuleJSONObject().toString().getBytes());
                }
            }
            catch (final Exception ex) {
                EvaluatorTrackerUtil.logger.log(Level.SEVERE, "Exception in writing json to file TrackTrial.json", ex);
            }
        }
    }
    
    public void addUserProperty(final String moduleName, final String key, final Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static void addOrUpdateJSON(final JSONObject destination, final JSONObject source) throws JSONException {
        final Iterator currentIterator = source.keys();
        while (currentIterator.hasNext()) {
            final String moduleKey = currentIterator.next().toString();
            if (destination.has(moduleKey)) {
                final JSONObject moduleDestination = (JSONObject)destination.get(moduleKey);
                final JSONObject moduleSource = (JSONObject)source.get(moduleKey);
                final Iterator currentModuleIterator = moduleSource.keys();
                while (currentModuleIterator.hasNext()) {
                    final String pageKey = currentModuleIterator.next().toString();
                    if (moduleDestination.has(pageKey)) {
                        final int existingClickCount = moduleDestination.getInt(pageKey);
                        final int currentClickCount = moduleSource.getInt(pageKey);
                        moduleDestination.put(pageKey, existingClickCount + currentClickCount);
                    }
                    else {
                        moduleDestination.put(pageKey, moduleSource.get(pageKey));
                    }
                }
            }
            else {
                destination.put(moduleKey, source.get(moduleKey));
            }
        }
    }
    
    public int getCount(final String loginId, final String type) {
        try {
            final JSONObject skipCountJSON = EvaluatorTrackerUtil.moduleVsPageMap.get(type);
            if (skipCountJSON == null) {
                return 0;
            }
            final int clickCount = skipCountJSON.optInt(loginId);
            return clickCount;
        }
        catch (final Exception ex) {
            EvaluatorTrackerUtil.logger.log(Level.SEVERE, "Exception in getting skiprequestDemoCount ", ex);
            return 0;
        }
    }
    
    static {
        EvaluatorTrackerUtil.logger = Logger.getLogger(EvaluatorTrackerUtil.class.getName());
        EvaluatorTrackerUtil.moduleVsPageMap = new ConcurrentHashMap<String, JSONObject>();
        EvaluatorTrackerUtil.evaluatorTrackerUtil = null;
    }
}
