package com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util;

import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSMETracker;
import com.adventnet.db.schema.analyze.SchemaAnalyzerUtil;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.DiffResultFilter;
import org.json.JSONArray;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.schedule.ScheduledWorkerTask;

public class DiffGenerator implements ScheduledWorkerTask
{
    private static final Logger LOGGER;
    private static final String SCHEDULER_NAME;
    public static boolean isExecutionCompleted;
    
    @Override
    public String getSchedulerName() {
        return DiffGenerator.SCHEDULER_NAME;
    }
    
    @Override
    public void run() {
        DiffGenerator.LOGGER.log(Level.INFO, "Starting Diff tool execution");
        final Map<String, JSONArray> resultDiffMap = this.generateDiff();
        this.addDiffResultTOMETracking(resultDiffMap);
        DiffGenerator.LOGGER.log(Level.INFO, "Flag file deletion status : {0}", DiffToolUtil.deleteFlagFile());
        DiffGenerator.isExecutionCompleted = Boolean.TRUE;
        DiffGenerator.LOGGER.log(Level.INFO, "Diff tool execution is completed");
    }
    
    private Map<String, JSONArray> generateDiff() {
        Map<String, JSONArray> diffMap = null;
        Map<String, JSONArray> filteredDiffMap = null;
        final DiffResultFilter diffResultFilter = new DiffResultFilter();
        try {
            diffMap = this.getDiff();
            if (diffMap != null && diffMap.size() != 0) {
                DiffToolUtil.writeAsJSONFile(diffMap, "SchemaDiff.json");
                filteredDiffMap = diffResultFilter.filterDiffOutput(diffMap);
                if (filteredDiffMap != null && filteredDiffMap.size() != 0) {
                    DiffToolUtil.writeAsJSONFile(filteredDiffMap, "Filtered-SchemaDiff.json");
                }
                else {
                    DiffGenerator.LOGGER.log(Level.INFO, "No filtered diff found. Going to delete existing Diff JSON files");
                    DiffToolUtil.deleteDiffJSONFile("SchemaDiff.json");
                    DiffToolUtil.deleteDiffJSONFile("Filtered-SchemaDiff.json");
                }
            }
            else {
                DiffGenerator.LOGGER.log(Level.INFO, "No diff found");
            }
        }
        catch (final Exception ex) {
            DiffGenerator.LOGGER.log(Level.WARNING, "Caught exception in in generating diff: ", ex);
        }
        return filteredDiffMap;
    }
    
    private JSONObject getDiffIDSummary(final Map<String, JSONArray> diffMap) {
        JSONObject diffIDSummary = null;
        try {
            if (diffMap != null && diffMap.size() != 0) {
                diffIDSummary = new JSONObject();
                for (final JSONArray diffValue : diffMap.values()) {
                    for (int index = 0; index < diffValue.length(); ++index) {
                        final JSONObject diff = diffValue.getJSONObject(index);
                        final String diffType = String.valueOf(diff.get("difftype"));
                        final int diffID = DiffToolUtil.getDiffID(diffType);
                        this.addDiffID(diffIDSummary, diffID);
                    }
                }
            }
        }
        catch (final Exception ex) {
            DiffGenerator.LOGGER.log(Level.WARNING, "Caught exception in getting summarized diff ID :", ex);
        }
        return diffIDSummary;
    }
    
    private void addDiffID(final JSONObject diffIDSummary, final int diffID) throws JSONException {
        int count = 0;
        final String key = diffID + "";
        if (diffIDSummary.has(key)) {
            count = diffIDSummary.getInt(key);
        }
        ++count;
        diffIDSummary.put(key, count);
    }
    
    private Map<String, JSONArray> getDiff() throws Exception {
        Map<String, JSONArray> diffResultMap = null;
        try {
            DiffGenerator.LOGGER.log(Level.INFO, "Generating diff...");
            diffResultMap = SchemaAnalyzerUtil.compare();
            DiffGenerator.LOGGER.log(Level.INFO, "Diff generated");
        }
        catch (final Exception ex) {
            DiffGenerator.LOGGER.log(Level.SEVERE, "Caught exception while comparing File system and database: ", ex);
            throw ex;
        }
        return diffResultMap;
    }
    
    private void addDiffResultTOMETracking(final Map<String, JSONArray> resultDiffMap) {
        JSONObject meTrackingDetails = null;
        try {
            meTrackingDetails = new JSONObject();
            final JSONObject diffIDSummary = this.getDiffIDSummary(resultDiffMap);
            meTrackingDetails.put("Diff-ID-Summary", (Object)diffIDSummary);
            STSMETracker.getInstance().addToMETracking("DiffTool", meTrackingDetails);
        }
        catch (final JSONException ex) {
            DiffGenerator.LOGGER.log(Level.WARNING, "Caught exception while adding METracking details: ", (Throwable)ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("DiffToolLogger");
        SCHEDULER_NAME = DiffGenerator.class.getName();
        DiffGenerator.isExecutionCompleted = Boolean.FALSE;
    }
}
