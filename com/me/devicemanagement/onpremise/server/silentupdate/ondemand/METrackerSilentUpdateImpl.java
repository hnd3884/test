package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;

public class METrackerSilentUpdateImpl implements MEDMTracker
{
    Logger logger;
    String sourceClass;
    private static Properties silentUpdateTrackingProperties;
    
    public METrackerSilentUpdateImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "METrackerSilentUpdateImpl";
    }
    
    @Override
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getTrackerProperties", "SilentUpdate params implementation starts...");
        METrackerSilentUpdateImpl.silentUpdateTrackingProperties = new Properties();
        this.addExportFailedRequest();
        this.addQPPMDownloadFailedStatus();
        this.addDynamicCheckerDownloadFailedStatus();
        this.addSilentUpdateEnabledStatus();
        this.addRemindMELaterCount();
        this.addDismissQPPMs();
        this.addIgnoreTheQPPMs();
        SyMLogger.info(this.logger, this.sourceClass, "getTrackerProperties", "Details Summary : " + getSilentUpdateTrackingProperties());
        SyMLogger.info(this.logger, this.sourceClass, "getTrackerProperties", "SilentUpdate params implementation ends ...");
        return getSilentUpdateTrackingProperties();
    }
    
    private static Properties getSilentUpdateTrackingProperties() {
        return METrackerSilentUpdateImpl.silentUpdateTrackingProperties;
    }
    
    public void addExportFailedRequest() {
        try {
            final Properties meTrackParams = METrackerUtil.getMETrackParams("SUExportFailReq");
            if (meTrackParams.containsKey("SUExportFailReq")) {
                ((Hashtable<String, Object>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUExportFailReq", ((Hashtable<K, Object>)meTrackParams).get("SUExportFailReq"));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateQPPMApplyStatus", "addExportFailedRequest has been failed : ", (Throwable)e);
        }
    }
    
    public void addQPPMDownloadFailedStatus() {
        try {
            final JSONObject jsonObject = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("QPPMDownldFailDtls."));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("QPPMDownldFailDtls", String.valueOf(jsonObject));
            ((Hashtable<String, Integer>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("QPPMDownldFailDtlsTotlCunt", jsonObject.length());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addQPPMDownloadFailedStatus", "addQPPMDownloadFailedStatus has been failed : ", (Throwable)e);
        }
    }
    
    public void addDynamicCheckerDownloadFailedStatus() {
        try {
            final JSONObject jsonObject = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("DynamicCheckerDownldFailDtls."));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("DynamicCheckerDownldFailDtls", String.valueOf(jsonObject));
            ((Hashtable<String, Integer>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("DynamicCheckerDownldFailDtlsTotlCunt", jsonObject.length());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addDynamicCheckerDownloadFailedStatus", "addDynamicCheckerDownloadFailedStatus has been failed : ", (Throwable)e);
        }
    }
    
    public void addRemindMELaterCount() {
        try {
            final JSONObject jsonObject = this.removeStartsWithUniqueKey(METrackerUtil.getMETrackParamsStartsWith("SUReminderMELatter."));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUReminderMELatter", String.valueOf(jsonObject));
            ((Hashtable<String, Integer>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUReminderMELatterTotlCunt", jsonObject.length());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addRemindMELaterCount", "addRemindMELaterCount has been failed : ", (Throwable)e);
        }
    }
    
    public void addDismissQPPMs() {
        try {
            final JSONArray jsonArray = this.getDBColumnValues("SilentUpdateDetails", "QPPM_ID", new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "IS_SHOW_ALERT_MSG"), (Object)false, 0));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUDismissQPPMs", String.valueOf(jsonArray));
            ((Hashtable<String, Integer>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUDismissQPPMsTotlCunt", jsonArray.length());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateDismisQPPMs", "updateDismisQPPMs has been failed : ", (Throwable)e);
        }
    }
    
    public void addIgnoreTheQPPMs() {
        try {
            final JSONArray jsonArray = this.getDBColumnValues("SilentUpdateDetails", "QPPM_ID", new Criteria(Column.getColumn("SilentUpdateDetailsExtn", "TASK_STATUS"), (Object)(-2), 0));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUIgnoreTheQPPMs", String.valueOf(jsonArray));
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUIgnoreTheQPPMsTotlCunt", String.valueOf(jsonArray));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIgnoreTheQPPMs", "addIgnoreTheQPPMs has been failed : ", (Throwable)e);
        }
    }
    
    public void addSilentUpdateEnabledStatus() {
        try {
            ((Hashtable<String, String>)METrackerSilentUpdateImpl.silentUpdateTrackingProperties).put("SUAutoApproveEnabled", String.valueOf(new SilentUpdateHandler().isAutoApproveEnabled()));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSilentUpdateEnabledStatus", "addSilentUpdateEnabledStatus has been failed : ", (Throwable)e);
        }
    }
    
    private JSONObject removeStartsWithUniqueKey(final JSONObject inputJson) throws Exception {
        final JSONObject outputJson = new JSONObject();
        final Iterator keys = inputJson.keys();
        while (keys.hasNext()) {
            final String oldKey = keys.next().toString();
            outputJson.put(oldKey.substring(oldKey.indexOf(".") + 1), inputJson.get(oldKey));
        }
        return outputJson;
    }
    
    private JSONArray getDBColumnValues(final String returnValueTableName, final String returnColumnName, final Criteria criteria) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final DataObject qppmDetails = SilentUpdateHelper.getInstance().getQPPMDetails(criteria, false);
        if (!qppmDetails.isEmpty()) {
            final Iterator rows = qppmDetails.getRows(returnValueTableName);
            while (rows.hasNext()) {
                final Row row = rows.next();
                jsonArray.put(row.get(returnColumnName));
            }
        }
        return jsonArray;
    }
    
    static {
        METrackerSilentUpdateImpl.silentUpdateTrackingProperties = null;
    }
}
