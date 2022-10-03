package com.me.mdm.server.doc;

import java.util.Hashtable;
import org.json.simple.parser.JSONParser;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class DocTask extends DCQueueDataProcessor implements SchedulerExecutionInterface
{
    public void executeTask(final Properties props) {
        final DataObject dObj = ((Hashtable<K, DataObject>)props).get("DocumentDetails");
        final Long syncRequstTime = ((Hashtable<K, Long>)props).get("SYNC_REQUEST_TIME");
        final Long deviceID = ((Hashtable<K, Long>)props).get("MANAGEDDEVICE_ID");
        final Long agentLastSyncAt = ((Hashtable<K, Long>)props).get("AGENT_APPLIED_TIME");
        try {
            DocMgmtDataHandler.getInstance().processDiffData(dObj, "UPDATE_SYNC_DOC_DIFF", agentLastSyncAt, syncRequstTime, deviceID, false);
        }
        catch (final Exception e) {
            DocMgmt.logger.log(Level.SEVERE, null, e);
        }
    }
    
    public void processData(final DCQueueData qData) {
        try {
            final JSONObject qNode = new JSONObject(String.valueOf(qData.queueData));
            final String value;
            final String taskType = value = String.valueOf(qNode.get("Task"));
            switch (value) {
                case "STATUS_UPDATE_TASK": {
                    DocMgmt.logger.log(Level.INFO, "processing task Details {0}", new Object[] { qNode });
                    final Long time = System.currentTimeMillis();
                    DocMgmt.logger.log(Level.INFO, "{0} - starting doc queue task", new Object[] { time });
                    final org.json.simple.JSONObject qDataObj = (org.json.simple.JSONObject)new JSONParser().parse((String)qData.queueData);
                    DocMgmt.logger.log(Level.INFO, "{0} - updating count and summary", new Object[] { time });
                    DocSummaryHandler.getInstance().updateDocStatus(qDataObj);
                    DocMgmt.logger.log(Level.INFO, "{0} - cleaning doc repo", new Object[] { time });
                    DocMgmt.getInstance().docScheduledTask();
                    DocMgmt.logger.log(Level.INFO, "{0} - doc queue task done", new Object[] { time });
                    break;
                }
                default: {
                    DocMgmt.logger.log(Level.INFO, "can't do much about : {0}", new Object[] { qNode });
                    break;
                }
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
}
