package org.postgresql.replication;

import java.sql.SQLException;
import org.postgresql.replication.fluent.ChainedCreateReplicationSlotBuilder;
import org.postgresql.replication.fluent.ChainedStreamBuilder;

public interface PGReplicationConnection
{
    ChainedStreamBuilder replicationStream();
    
    ChainedCreateReplicationSlotBuilder createReplicationSlot();
    
    void dropReplicationSlot(final String p0) throws SQLException;
}
