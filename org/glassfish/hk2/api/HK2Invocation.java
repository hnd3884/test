package org.glassfish.hk2.api;

public interface HK2Invocation
{
    void setUserData(final String p0, final Object p1);
    
    Object getUserData(final String p0);
}
