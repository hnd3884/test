package com.me.devicemanagement.framework.server.eventlog.summaryserver.summary;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.eventlog.factory.EventLogInterface;

public class SummaryDCEventLogUtil implements EventLogInterface
{
    private static Logger logger;
    
    @Override
    public void updateEventLogDataToSummaryServer(final Long eventLogID, final int eventID, final String userName, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerID, final Long consent_id) throws Exception {
        SummaryDCEventLogUtil.logger.log(Level.FINE, "Summary Server - no need to updateEventLogDataToSummaryServer");
    }
    
    static {
        SummaryDCEventLogUtil.logger = Logger.getLogger("EventLogLogger");
    }
}
