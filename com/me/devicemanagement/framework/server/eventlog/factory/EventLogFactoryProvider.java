package com.me.devicemanagement.framework.server.eventlog.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class EventLogFactoryProvider
{
    private static Logger logger;
    private static EventLogInterface eventLogInterface;
    
    public static EventLogInterface getEventLogUtil() {
        try {
            if (EventLogFactoryProvider.eventLogInterface == null) {
                if (SyMUtil.isProbeServer()) {
                    EventLogFactoryProvider.eventLogInterface = (EventLogInterface)Class.forName("com.me.devicemanagement.framework.server.eventlog.summaryserver.probe.ProbeDCEventLogUtil").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    EventLogFactoryProvider.eventLogInterface = (EventLogInterface)Class.forName("com.me.devicemanagement.framework.server.eventlog.summaryserver.summary.SummaryDCEventLogUtil").newInstance();
                }
                else {
                    EventLogFactoryProvider.eventLogInterface = (EventLogInterface)Class.forName("com.me.devicemanagement.framework.server.eventlog.standalone.DCEventLogUtil").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            EventLogFactoryProvider.logger.log(Level.SEVERE, "Exception in getting getEventLogUtil", e);
        }
        return EventLogFactoryProvider.eventLogInterface;
    }
    
    static {
        EventLogFactoryProvider.logger = Logger.getLogger("EventLogLogger");
        EventLogFactoryProvider.eventLogInterface = null;
    }
}
