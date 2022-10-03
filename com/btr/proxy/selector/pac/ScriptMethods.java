package com.btr.proxy.selector.pac;

public interface ScriptMethods
{
    boolean isPlainHostName(final String p0);
    
    boolean dnsDomainIs(final String p0, final String p1);
    
    boolean localHostOrDomainIs(final String p0, final String p1);
    
    boolean isResolvable(final String p0);
    
    boolean isResolvableEx(final String p0);
    
    boolean isInNet(final String p0, final String p1, final String p2);
    
    boolean isInNetEx(final String p0, final String p1);
    
    String dnsResolve(final String p0);
    
    String dnsResolveEx(final String p0);
    
    String myIpAddress();
    
    String myIpAddressEx();
    
    int dnsDomainLevels(final String p0);
    
    boolean shExpMatch(final String p0, final String p1);
    
    boolean weekdayRange(final String p0, final String p1, final String p2);
    
    boolean dateRange(final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6);
    
    boolean timeRange(final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6);
    
    String sortIpAddressList(final String p0);
    
    String getClientVersion();
}
