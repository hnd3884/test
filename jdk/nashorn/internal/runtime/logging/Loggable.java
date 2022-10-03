package jdk.nashorn.internal.runtime.logging;

import jdk.nashorn.internal.runtime.Context;

public interface Loggable
{
    DebugLogger initLogger(final Context p0);
    
    DebugLogger getLogger();
}
