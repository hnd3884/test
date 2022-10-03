package com.me.mdm.chrome.agent.core;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public abstract class ProcessRequestHandler
{
    public Logger logger;
    
    public ProcessRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public abstract void processRequest(final Request p0, final Response p1);
    
    protected String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
}
