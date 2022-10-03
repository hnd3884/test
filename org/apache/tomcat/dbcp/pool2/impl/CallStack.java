package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;

public interface CallStack
{
    boolean printStackTrace(final PrintWriter p0);
    
    void fillInStackTrace();
    
    void clear();
}
