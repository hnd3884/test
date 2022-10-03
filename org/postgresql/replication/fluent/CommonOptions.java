package org.postgresql.replication.fluent;

import org.postgresql.replication.LogSequenceNumber;

public interface CommonOptions
{
    String getSlotName();
    
    LogSequenceNumber getStartLSNPosition();
    
    int getStatusInterval();
}
