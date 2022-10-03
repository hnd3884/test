package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util;

import org.json.JSONException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class STSMETracker
{
    private static final Logger LOGGER;
    private static STSMETracker stsmeTrackerObj;
    private static JSONObject meTrackingJSON;
    
    private STSMETracker() {
    }
    
    public static STSMETracker getInstance() {
        if (STSMETracker.stsmeTrackerObj == null) {
            STSMETracker.stsmeTrackerObj = new STSMETracker();
        }
        return STSMETracker.stsmeTrackerObj;
    }
    
    public void addToMETracking(final String toolName, final JSONObject details) {
        try {
            STSMETracker.LOGGER.log(Level.INFO, "Going to add ME Tracking details for {0} tool", toolName);
            final JSONObject metrackingJSON = this.getExistingMETrackingJSON();
            STSMETracker.LOGGER.log(Level.INFO, "Adding ME Tracking details for {0} tool", toolName);
            metrackingJSON.put(toolName, (Object)details);
            this.writeJSONFile(metrackingJSON);
            STSMETracker.LOGGER.log(Level.INFO, "ME Tracking details for {0} tool is added successfully", toolName);
        }
        catch (final Exception ex) {
            STSMETracker.LOGGER.log(Level.WARNING, "Caught exception in adding ME tracking details for " + toolName, ex);
        }
    }
    
    private void writeJSONFile(final JSONObject json) {
        STSMETracker.LOGGER.log(Level.INFO, "Going to write new JSON file");
        File jsonFile = null;
        FileWriter writer = null;
        try {
            jsonFile = new File(this.getMETrackingJSONFileLoc());
            jsonFile.getParentFile().mkdirs();
            writer = new FileWriter(jsonFile);
            writer.write(json.toString());
        }
        catch (final IOException ex) {
            STSMETracker.LOGGER.log(Level.WARNING, "Caught exception while writing JSON file :", ex);
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
            catch (final Exception ex2) {
                STSMETracker.LOGGER.log(Level.WARNING, "Caught exception while closing writer :", ex2);
            }
        }
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
            catch (final Exception ex3) {
                STSMETracker.LOGGER.log(Level.WARNING, "Caught exception while closing writer :", ex3);
            }
        }
    }
    
    public JSONObject getExistingMETrackingJSON() {
        if (STSMETracker.meTrackingJSON == null) {
            final String meTrackingJSONFileLoc = this.getMETrackingJSONFileLoc();
            try {
                if (new File(meTrackingJSONFileLoc).exists()) {
                    STSMETracker.meTrackingJSON = new JSONObject(STSToolUtil.readFileAsString(meTrackingJSONFileLoc));
                    STSMETracker.LOGGER.log(Level.INFO, "METracking JSON file is loaded as JSONObject successfully");
                }
                else {
                    STSMETracker.LOGGER.log(Level.INFO, "METracking JSON file not exists. Creating empty JSON...");
                    STSMETracker.meTrackingJSON = new JSONObject();
                }
            }
            catch (final JSONException ex) {
                STSMETracker.LOGGER.log(Level.WARNING, "Caught exception in getting METracking JSON file :", (Throwable)ex);
            }
        }
        return STSMETracker.meTrackingJSON;
    }
    
    public String getMETrackingJSONFileLoc() {
        final String fileLoc = System.getProperty("server.home") + File.separator + "logs" + File.separator + "STSMETrackingDetails.json";
        return fileLoc;
    }
    
    static {
        LOGGER = Logger.getLogger(STSMETracker.class.getName());
        STSMETracker.stsmeTrackerObj = null;
        STSMETracker.meTrackingJSON = null;
    }
}
