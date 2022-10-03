package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.listener.LDAPListener;
import java.util.logging.Handler;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class LDAPDebuggerShutdownListener extends Thread
{
    private final Handler logHandler;
    private final LDAPListener listener;
    
    LDAPDebuggerShutdownListener(final LDAPListener listener, final Handler logHandler) {
        this.listener = listener;
        this.logHandler = logHandler;
    }
    
    @Override
    public void run() {
        this.listener.shutDown(true);
        this.logHandler.close();
    }
}
