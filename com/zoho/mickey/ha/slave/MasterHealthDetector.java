package com.zoho.mickey.ha.slave;

import com.zoho.mickey.ha.HAException;
import com.zoho.mickey.ha.HAConfig;

public interface MasterHealthDetector
{
    void initialize(final HAConfig p0) throws HAException;
    
    boolean isMasterDown(final String p0) throws HAException;
    
    String getName();
}
