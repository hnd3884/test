package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.EvaluationTrackHandler;

public class MDMEvaluationTrackHandler implements EvaluationTrackHandler
{
    private static Logger logger;
    
    public JSONObject getEvaluatorInfoJSON() throws JSONException, Exception {
        final JSONObject evaluatorJSON = new JSONObject();
        final String mdmSummary = MDMUtil.getInstance().getMDMPropertyForTracking();
        if (mdmSummary != null && mdmSummary.trim().length() > 0) {
            final String[] summaryArray = mdmSummary.split("\\|");
            for (int i = 1; i < summaryArray.length; ++i) {
                final String[] keyValues = summaryArray[i].split("-");
                evaluatorJSON.put(keyValues[0], (Object)keyValues[1]);
            }
        }
        else {
            MDMEvaluationTrackHandler.logger.log(Level.SEVERE, "MDM Tracking summary not obtained");
        }
        final Properties creatorProps = FileAccessUtil.readProperties(SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "ZohoCreator.properties");
        if (creatorProps != null) {
            evaluatorJSON.put("metrID", (Object)(((Hashtable<K, Object>)creatorProps).get("ID") + ""));
        }
        final String didValue = SyMUtil.getDIDValue();
        evaluatorJSON.put("DID", (Object)(didValue + ""));
        return evaluatorJSON;
    }
    
    static {
        MDMEvaluationTrackHandler.logger = Logger.getLogger(MDMEvaluationTrackHandler.class.getName());
    }
}
