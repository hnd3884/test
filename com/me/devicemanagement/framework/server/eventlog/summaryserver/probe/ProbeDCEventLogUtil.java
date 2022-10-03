package com.me.devicemanagement.framework.server.eventlog.summaryserver.probe;

import java.util.Map;
import java.util.List;
import com.me.ems.summaryserver.common.ProbeActionAPI;
import java.util.HashMap;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.eventlog.factory.EventLogInterface;

public class ProbeDCEventLogUtil implements EventLogInterface
{
    private static Logger logger;
    
    @Override
    public void updateEventLogDataToSummaryServer(final Long eventLogID, final int eventID, final String userName, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerID, final Long consent_id) throws Exception {
        ProbeDCEventLogUtil.logger.log(Level.INFO, "Entered into ProbeDCEventLogUtil - updateEventLogDataToSummaryServer");
        final ProbeActionAPI probeActionAPI = ProbeMgmtFactoryProvider.getProbeActionAPI();
        final List<Integer> allowedEventIds = probeActionAPI.getProbeEventIDs();
        if (allowedEventIds.contains(eventID)) {
            final Map<String, Object> eventLogData = new HashMap<String, Object>();
            eventLogData.put("event_id", eventID);
            eventLogData.put("userName", userName);
            eventLogData.put("remarks", remarks);
            eventLogData.put("remarks_args", remarksArgs);
            eventLogData.put("updateTime", updateTime);
            eventLogData.put("event_log_id", eventLogID);
            eventLogData.put("customer_id", customerID);
            eventLogData.put("consent_id", consent_id);
            final Long currentProbeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
            probeActionAPI.addToProbeActionQueue(currentProbeID, 1, 8, 1, eventLogData);
            ProbeDCEventLogUtil.logger.log(Level.INFO, "Added Event for EventID :" + eventID + " to probe-action-data queue");
        }
    }
    
    static {
        ProbeDCEventLogUtil.logger = Logger.getLogger("EventLogLogger");
    }
}
