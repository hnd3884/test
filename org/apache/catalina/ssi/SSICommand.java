package org.apache.catalina.ssi;

import java.io.PrintWriter;

public interface SSICommand
{
    long process(final SSIMediator p0, final String p1, final String[] p2, final String[] p3, final PrintWriter p4) throws SSIStopProcessingException;
}
