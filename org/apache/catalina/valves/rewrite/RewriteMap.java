package org.apache.catalina.valves.rewrite;

public interface RewriteMap
{
    String setParameters(final String p0);
    
    String lookup(final String p0);
}
