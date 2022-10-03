package com.adventnet.persistence.fos.filereplication;

import java.util.List;
import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.FOSConfig;

public interface FileReplicationHandler
{
    void initialize(final FOSConfig p0, final String p1) throws FOSException;
    
    void startReplication() throws FOSException;
    
    void stopReplication() throws FOSException;
    
    boolean completePendingReplication() throws FOSException;
    
    boolean replicateOnce(final FOSConfig p0, final List<String> p1) throws FOSException;
}
