package com.adventnet.persistence.fos.slave;

import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.FOSConfig;

public interface MasterHealthDetector
{
    void initialize(final FOSConfig p0) throws FOSException;
    
    boolean isMasterDown(final String p0) throws FOSException;
    
    String getName();
}
