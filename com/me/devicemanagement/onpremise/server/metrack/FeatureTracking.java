package com.me.devicemanagement.onpremise.server.metrack;

import java.util.logging.Level;
import java.io.FileWriter;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class FeatureTracking
{
    private static Logger logger;
    private static final String FEATURE_FILE_PATH;
    
    private JSONObject getParamsFromFile() {
        JSONObject featureTrackingJSON = new JSONObject();
        final JSONParser jsonParser = new JSONParser();
        try {
            final String featureTrackingFile = System.getProperty("server.home") + File.separator + FeatureTracking.FEATURE_FILE_PATH;
            if (!new File(featureTrackingFile).exists()) {
                FeatureTracking.logger.info("FeatureTracking.Json file is not present");
                return featureTrackingJSON;
            }
            final FileReader updatesReader = new FileReader(featureTrackingFile);
            featureTrackingJSON = (JSONObject)jsonParser.parse((Reader)updatesReader);
            return featureTrackingJSON;
        }
        catch (final Exception e) {
            FeatureTracking.logger.info("Error while getting FeatureTrackin value" + e.getMessage());
            return featureTrackingJSON;
        }
    }
    
    private void writeParamsToFile(final JSONObject featureTrackParams) {
        try {
            final String featureTrackingFile = System.getProperty("server.home") + File.separator + FeatureTracking.FEATURE_FILE_PATH;
            final FileWriter file = new FileWriter(featureTrackingFile);
            file.write(featureTrackParams.toJSONString());
            file.flush();
        }
        catch (final Exception e) {
            FeatureTracking.logger.log(Level.SEVERE, "Error while writing featureTrackingParams", e);
        }
    }
    
    public JSONObject getFeatureTrackingParams(final String featureName) {
        JSONObject featureTrackingJSON = new JSONObject();
        try {
            featureTrackingJSON = this.getParamsFromFile();
            if (featureTrackingJSON.containsKey((Object)featureName)) {
                final JSONObject value = (JSONObject)featureTrackingJSON.get((Object)featureName);
                return value;
            }
        }
        catch (final Exception e) {
            FeatureTracking.logger.info("Error while getting featureTracking value" + e.getMessage());
        }
        return featureTrackingJSON;
    }
    
    public void addOrUpdateFeatureTrackingParams(final String featureName, final JSONObject featureTrackingParams) {
        try {
            final JSONObject featureTrackingJSON = this.getParamsFromFile();
            featureTrackingJSON.put((Object)featureName, (Object)featureTrackingParams);
            this.writeParamsToFile(featureTrackingJSON);
        }
        catch (final Exception e) {
            FeatureTracking.logger.info("Error while getting featureTracking value" + e.getMessage());
        }
    }
    
    static {
        FeatureTracking.logger = Logger.getLogger(FeatureTracking.class.getName());
        FEATURE_FILE_PATH = "conf" + File.separator + "featureTracking.json";
    }
}
