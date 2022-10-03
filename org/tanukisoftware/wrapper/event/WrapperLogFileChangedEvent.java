package org.tanukisoftware.wrapper.event;

import java.io.File;

public class WrapperLogFileChangedEvent extends WrapperLoggingEvent
{
    private final File m_logFile;
    
    public WrapperLogFileChangedEvent(final File logFile) {
        this.m_logFile = logFile;
    }
    
    public File getLogFile() {
        return this.m_logFile;
    }
    
    public String toString() {
        return "WrapperLogFileChangedEvent[logFile=" + this.getLogFile() + "]";
    }
}
