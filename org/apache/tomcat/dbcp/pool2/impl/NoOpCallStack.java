package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;

public class NoOpCallStack implements CallStack
{
    public static final CallStack INSTANCE;
    
    private NoOpCallStack() {
    }
    
    @Override
    public boolean printStackTrace(final PrintWriter writer) {
        return false;
    }
    
    @Override
    public void fillInStackTrace() {
    }
    
    @Override
    public void clear() {
    }
    
    static {
        INSTANCE = new NoOpCallStack();
    }
}
