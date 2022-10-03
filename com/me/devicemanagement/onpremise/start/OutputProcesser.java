package com.me.devicemanagement.onpremise.start;

import java.util.Properties;

public interface OutputProcesser
{
    boolean processOutput(final String p0);
    
    boolean processError(final String p0);
    
    void endStringReached();
    
    void terminated();
    
    boolean hasProcessStarted(final Properties p0);
}
