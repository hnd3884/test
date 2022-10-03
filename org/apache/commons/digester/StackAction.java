package org.apache.commons.digester;

public interface StackAction
{
    Object onPush(final Digester p0, final String p1, final Object p2);
    
    Object onPop(final Digester p0, final String p1, final Object p2);
}
