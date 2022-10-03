package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_CONSUMPTION_ANOMALY
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "CONSUMPTION_ANOMALY";
    private static final EventProcessor FILE_UPLOAD_DISK_CONSUMPTION_ALERT_EVENT;
    
    public static void pushFileUploadDiskConsumptionAlert(final String FOLDER_DIR, final long TOTAL_DISK_SIZE, final long AVAILABLE_DISK_SIZE, final long USED_DISK_SIZE, final long FOLDER_SIZE, final long THRESHOLD_SIZE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(7);
        dataMap.put("FOLDER_DIR", FOLDER_DIR);
        dataMap.put("TOTAL_DISK_SIZE", TOTAL_DISK_SIZE);
        dataMap.put("AVAILABLE_DISK_SIZE", AVAILABLE_DISK_SIZE);
        dataMap.put("USED_DISK_SIZE", USED_DISK_SIZE);
        dataMap.put("FOLDER_SIZE", FOLDER_SIZE);
        dataMap.put("THRESHOLD_SIZE", THRESHOLD_SIZE);
        EventDataProcessor.pushData(ZSEC_CONSUMPTION_ANOMALY.FILE_UPLOAD_DISK_CONSUMPTION_ALERT_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_CONSUMPTION_ANOMALY.FILE_UPLOAD_DISK_CONSUMPTION_ALERT_EVENT, "CONSUMPTION_ANOMALY", "pushFileUploadDiskConsumptionAlert"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        FILE_UPLOAD_DISK_CONSUMPTION_ALERT_EVENT = EventDataProcessor.getEventProcessor(ZSEC_CONSUMPTION_ANOMALY.TYPE, "CONSUMPTION_ANOMALY", "FILE_UPLOAD_DISK_CONSUMPTION_ALERT");
    }
}
