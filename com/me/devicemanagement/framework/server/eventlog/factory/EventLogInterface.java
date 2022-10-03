package com.me.devicemanagement.framework.server.eventlog.factory;

public interface EventLogInterface
{
    default void updateEventLogDataToSummaryServer(final Long eventLogID, final int eventID, final String userName, final String remarks, final Object remarksArgs, final Boolean updateTime, final Long customerID, final Long consent_id) throws Exception {
        throw new Exception("Invalid Implementation");
    }
}
