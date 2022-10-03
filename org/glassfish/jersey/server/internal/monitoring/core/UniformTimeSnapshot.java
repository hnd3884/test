package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public interface UniformTimeSnapshot
{
    long size();
    
    long getMax();
    
    long getMin();
    
    double getMean();
    
    long getTimeInterval(final TimeUnit p0);
    
    double getRate(final TimeUnit p0);
}
