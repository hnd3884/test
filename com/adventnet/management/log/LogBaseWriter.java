package com.adventnet.management.log;

import java.util.Properties;

public abstract class LogBaseWriter
{
    public abstract void log(final String p0);
    
    public abstract void logException(final Throwable p0);
    
    public abstract void logStackTrace();
    
    protected abstract void flush();
    
    protected abstract void init(final Properties p0);
}
