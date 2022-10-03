package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ThreadStackTrace implements Serializable
{
    private static final long serialVersionUID = 5032934844534051999L;
    private final int threadID;
    private final List<StackTraceElement> stackTraceElements;
    private final String threadName;
    
    public ThreadStackTrace(final int threadID, final String threadName, final List<StackTraceElement> stackTraceElements) {
        this.threadID = threadID;
        this.threadName = threadName;
        this.stackTraceElements = Collections.unmodifiableList((List<? extends StackTraceElement>)stackTraceElements);
    }
    
    public int getThreadID() {
        return this.threadID;
    }
    
    public String getThreadName() {
        return this.threadName;
    }
    
    public List<StackTraceElement> getStackTraceElements() {
        return this.stackTraceElements;
    }
}
