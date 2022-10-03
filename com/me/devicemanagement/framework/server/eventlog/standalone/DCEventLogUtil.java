package com.me.devicemanagement.framework.server.eventlog.standalone;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.eventlog.factory.EventLogInterface;

public class DCEventLogUtil implements EventLogInterface
{
    private static Logger logger;
    
    @Override
    public void updateEventLogDataToSummaryServer(final Long eventLogID, final int eventID, final String userName, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerID, final Long consent_id) throws Exception {
        DCEventLogUtil.logger.log(Level.FINE, "Standalone Server - no need to updateEventLogDataToSummaryServer");
    }
    
    static {
        DCEventLogUtil.logger = Logger.getLogger("EventLogLogger");
    }
}
