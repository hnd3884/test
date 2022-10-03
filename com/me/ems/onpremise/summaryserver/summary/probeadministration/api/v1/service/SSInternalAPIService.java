package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SSInternalAPIService
{
    static SSInternalAPIService ssInternalAPIService;
    public static Logger logger;
    
    public static SSInternalAPIService getInstance() {
        if (SSInternalAPIService.ssInternalAPIService == null) {
            SSInternalAPIService.ssInternalAPIService = new SSInternalAPIService();
        }
        return SSInternalAPIService.ssInternalAPIService;
    }
    
    public Map processDataToProbe(final Long probeId, final Map requestData) {
        final Map responseData = new HashMap();
        if (requestData.get("eventCodeList") != null) {
            final List<Integer> eventCodeList = requestData.get("eventCodeList");
            final Properties taskProps = new Properties();
            ((Hashtable<String, Long>)taskProps).put("probeId", probeId);
            ((Hashtable<String, List<Integer>>)taskProps).put("eventList", eventCodeList);
            SSInternalAPIService.logger.log(Level.INFO, "----------------SSInternalAPIService SummaryEventDataProcessor() Task Properties : {0} -----------------", new Object[] { taskProps });
            final HashMap taskInfoMap = new HashMap();
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.ems.summaryserver.summary.probedistribution.SummaryEventDataProcessor", taskInfoMap, taskProps, "asynchThreadPool");
            SSInternalAPIService.logger.log(Level.INFO, "----------------SSInternalAPIService SummaryEventDataProcessor() Task called");
            responseData.put("message-events", "processing events data");
        }
        if (requestData.get("tableName") != null) {
            final String tableName = requestData.get("tableName");
            final Properties taskProps = new Properties();
            ((Hashtable<String, Long>)taskProps).put("probeId", probeId);
            ((Hashtable<String, String>)taskProps).put("tableName", tableName);
            ((Hashtable<String, Boolean>)taskProps).put("isTableData", true);
            SSInternalAPIService.logger.log(Level.INFO, "----------------SSInternalAPIService SummaryEventDataProcessor() Task Properties : {0} -----------------", new Object[] { taskProps });
            final HashMap taskInfoMap = new HashMap();
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.ems.summaryserver.summary.probedistribution.SummaryEventDataProcessor", taskInfoMap, taskProps, "asynchThreadPool");
            SSInternalAPIService.logger.log(Level.INFO, "----------------SSInternalAPIService SummaryEventDataProcessor() Task called");
            responseData.put("message-tabledata", "processing table data");
        }
        return responseData;
    }
    
    static {
        SSInternalAPIService.ssInternalAPIService = null;
        SSInternalAPIService.logger = Logger.getLogger("probeActionsLogger");
    }
}
