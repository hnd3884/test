package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ToolInvocationLogShutdownHook extends Thread
{
    private final ToolInvocationLogDetails logDetails;
    
    public ToolInvocationLogShutdownHook(final ToolInvocationLogDetails logDetails) {
        this.logDetails = logDetails;
    }
    
    @Override
    public void run() {
        ToolInvocationLogger.logCompletionMessage(this.logDetails, null, ToolMessages.INFO_TOOL_INTERRUPTED_BY_JVM_SHUTDOWN.get());
    }
}
