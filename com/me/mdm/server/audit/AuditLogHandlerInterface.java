package com.me.mdm.server.audit;

import com.me.devicemanagement.framework.server.queue.DCQueueData;

public interface AuditLogHandlerInterface
{
    void addEventLogEntry(final DCQueueData p0) throws Exception;
}
