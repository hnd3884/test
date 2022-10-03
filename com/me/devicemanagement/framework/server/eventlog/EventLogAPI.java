package com.me.devicemanagement.framework.server.eventlog;

import com.adventnet.persistence.Row;

public interface EventLogAPI
{
    Row getRDSConnectionReasonRow(final Object p0, final String p1);
}
