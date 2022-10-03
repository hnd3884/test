package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Hashtable;
import com.me.tools.zcutil.METrack;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.task.DeviceMgmtTaskUtil;
import java.util.Iterator;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import java.io.InputStreamReader;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class TrackEvaluationTask implements SchedulerExecutionInterface
{
    private static FileAccessAPI fileAccessAPI;
    private Logger logger;
    
    public TrackEvaluationTask() {
        this.logger = Logger.getLogger(TrackEvaluationTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (!licenseType.equalsIgnoreCase("T")) {
            this.deleteTrackEvaluationTask();
        }
        final String trackerFile = System.getProperty("server.home") + File.separator + "bin" + File.separator + "TrackTrial.json";
        try {
            if (TrackEvaluationTask.fileAccessAPI.isFileExists(trackerFile)) {
                final JSONObject jsonFromFile = new JSONObject(IOUtils.toString((Reader)new InputStreamReader(TrackEvaluationTask.fileAccessAPI.readFile(trackerFile))));
                final JSONObject jsonFromServer = EvaluatorTrackerUtil.getModuleJSONObject();
                this.logger.log(Level.INFO, "JSON From File" + jsonFromFile);
                this.logger.log(Level.INFO, "JSON From Memory" + jsonFromServer);
                JsonUpdateExecutor.addOrUpdateJSON(new ModuleJsonUpdator(), jsonFromFile, jsonFromServer);
                TrackEvaluationTask.fileAccessAPI.writeFile(trackerFile, jsonFromFile.toString().getBytes());
                EvaluatorTrackerUtil.cleanUpEvaluatorTrack();
            }
            else {
                TrackEvaluationTask.fileAccessAPI.writeFile(trackerFile, EvaluatorTrackerUtil.getModuleJSONObject().toString().getBytes());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in writing json to file TrackTrial.json", ex);
        }
        try {
            if (SyMUtil.getSyMParameter("EvaluatorInfo") == null) {
                this.addEvaluatorInfo();
            }
            else {
                this.updateEvaluatorInfo();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updating/adding evaluator info", ex);
        }
        this.sendMETrackDataIfRequired();
    }
    
    private void deleteTrackEvaluationTask() {
        if (!CustomerInfoUtil.isSAS) {
            ApiFactoryProvider.getSchedulerAPI().removeScheduler("EvaluationTrackerSchedule");
            final String trackerFile = System.getProperty("server.home") + File.separator + "bin" + File.separator + "TrackTrial.json";
            try {
                ApiFactoryProvider.getFileAccessAPI().deleteFile(trackerFile);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in deleting the file " + trackerFile, ex);
            }
        }
    }
    
    private void addEvaluatorInfo() throws Exception {
        SyMUtil.updateSyMParameter("EvaluatorInfoModified", "true");
        SyMUtil.updateSyMParameter("EvaluatorInfo", this.convertJSONToString(this.getEvaluatorInfoJSON(), "&"));
    }
    
    private JSONObject getEvaluatorInfoJSON() {
        JSONObject evaluatorInfoJSON = new JSONObject();
        try {
            evaluatorInfoJSON = com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider.getEvaluationTrackHandler().getEvaluatorInfoJSON();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getEvaluatorInfoJSON ", ex);
        }
        return evaluatorInfoJSON;
    }
    
    private JSONObject parseStringAsJSON(final String evaluatorInfoString, final String delimiter) throws JSONException {
        final JSONObject parsedJSONObject = new JSONObject();
        final String[] evaluatorTokens = evaluatorInfoString.split(delimiter);
        for (int i = 0; i < evaluatorTokens.length; ++i) {
            final String[] param = evaluatorTokens[i].split("=");
            if (param.length > 1) {
                parsedJSONObject.put(param[0], (Object)param[1]);
            }
        }
        return parsedJSONObject;
    }
    
    private String convertJSONToString(final JSONObject jsonObj, final String delimiterToAppend) throws JSONException {
        final Iterator jsonIterator = jsonObj.keys();
        final StringBuffer stringToReturn = new StringBuffer("");
        while (jsonIterator.hasNext()) {
            final String key = jsonIterator.next().toString();
            stringToReturn.append(key + "=" + jsonObj.get(key));
            if (jsonIterator.hasNext()) {
                stringToReturn.append(delimiterToAppend);
            }
        }
        return stringToReturn.toString();
    }
    
    private void updateEvaluatorInfo() throws JSONException {
        final JSONObject evaluatorJSONFromDB = this.parseStringAsJSON(SyMUtil.getSyMParameter("EvaluatorInfo"), "\\&");
        JSONObject evaluatorJSONOnDemand = new JSONObject();
        try {
            evaluatorJSONOnDemand = this.getEvaluatorInfoJSON();
            final EvaluatorInfoJsonUpdator evaluatorInfoUpdatorObj = new EvaluatorInfoJsonUpdator();
            JsonUpdateExecutor.addOrUpdateJSON(evaluatorInfoUpdatorObj, evaluatorJSONOnDemand, evaluatorJSONFromDB);
            if (evaluatorInfoUpdatorObj.isAnyKeyModified) {
                SyMUtil.updateSyMParameter("EvaluatorInfoModified", "true");
                SyMUtil.updateSyMParameter("EvaluatorInfo", this.convertJSONToString(evaluatorJSONOnDemand, "&"));
            }
            else {
                final String evaluatorInfoChanged = SyMUtil.getSyMParameter("EvaluatorInfoModified");
                if (evaluatorInfoChanged != null && !Boolean.parseBoolean(evaluatorInfoChanged)) {
                    SyMUtil.updateSyMParameter("EvaluatorInfoModified", "false");
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updating the evaluator info.", ex);
        }
    }
    
    private void sendMETrackDataIfRequired() {
        try {
            final DeviceMgmtTaskUtil taskUtil = DeviceMgmtTaskUtil.getInstance();
            if (taskUtil.getTaskDetails(Integer.valueOf(106), (String)null) == 0) {
                final Properties props = new Properties();
                ((Hashtable<String, String>)props).put("userName", EventConstant.DC_SYSTEM_USER);
                ((Hashtable<String, Integer>)props).put("operationType", 106);
                ((Hashtable<String, String>)props).put("STATUS", "CREATION");
                ((Hashtable<String, Integer>)props).put("COUNTER", 1);
                taskUtil.createTaskDetailsDO(props);
            }
            else {
                Long taskIDTemp = new Long(-1L);
                final Properties props2 = new Properties();
                taskIDTemp = taskUtil.gettaskID(taskUtil.getTaskName(106));
                final DataObject taskDetailsDO = taskUtil.getTaskDetailsDO(taskIDTemp);
                final Row evaluatorMEPostRow = taskDetailsDO.getRow("TaskDetails");
                if (evaluatorMEPostRow != null) {
                    final Long creationTime = (Long)evaluatorMEPostRow.get("CREATIONTIME");
                    final Long completionTime = (Long)evaluatorMEPostRow.get("COMPLETIONTIME");
                    long dateDiff = 0L;
                    if (creationTime != null && completionTime != null && completionTime > 0L) {
                        dateDiff = SyMUtil.getDateDiff((long)creationTime, (long)completionTime);
                    }
                    this.logger.log(Level.INFO, "Date Diff in Track Evaluation Tsk for ME Tracking post is " + dateDiff);
                    Integer timesExecuted = (Integer)evaluatorMEPostRow.get("COUNTER");
                    ++timesExecuted;
                    this.logger.log(Level.INFO, "Number of times Track Evaluation Task Executed is " + timesExecuted);
                    if (dateDiff == 0L && ((timesExecuted % 2 == 0 && timesExecuted <= 6) || timesExecuted % 6 == 0)) {
                        this.postMETrackingData();
                    }
                    ((Hashtable<String, String>)props2).put("userName", EventConstant.DC_SYSTEM_USER);
                    ((Hashtable<String, String>)props2).put("STATUS", "COMPLETED");
                    ((Hashtable<String, Long>)props2).put("COMPLETIONTIME", System.currentTimeMillis());
                    ((Hashtable<String, Integer>)props2).put("COUNTER", timesExecuted);
                    taskUtil.updateTaskDetails(taskIDTemp, props2);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in finding the Class or Method.", ex);
        }
    }
    
    private void postMETrackingData() {
        this.logger.log(Level.INFO, "From TrackEvaluation Task, Posting ME Tracking data @" + SyMUtil.getDate(System.currentTimeMillis()));
        try {
            if (!(boolean)FrameworkConfigurations.getSpecificPropertyIfExists("evaluation_tracker", "postWithBaseForm", (Object)false)) {
                final METrackerAPI meTrackerAPI = METrackerAPI.getMETrackerAPI();
                meTrackerAPI.uploadData();
            }
            else {
                METrack.ZCScheduler();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred during postMETrackingData", e);
        }
    }
    
    static {
        TrackEvaluationTask.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
    }
}
