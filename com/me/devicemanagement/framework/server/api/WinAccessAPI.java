package com.me.devicemanagement.framework.server.api;

import com.me.devicemanagement.framework.server.util.SomTrackingParameters;
import com.me.devicemanagement.framework.server.exception.SyMException;

public interface WinAccessAPI
{
    boolean nativeIsFirewallEnabledInDCServer(final String p0, final String p1, final String p2, final long p3, final String p4) throws SyMException;
    
    boolean nativeOpenFirewallPort(final String p0, final String p1, final String p2, final long p3, final String p4) throws SyMException;
    
    String nativeGetCurrentNetBIOSName() throws SyMException;
    
    String getDomainFlatName() throws SyMException;
    
    int isAD() throws SyMException;
    
    String getCurrentDomainName() throws SyMException;
    
    String getServerName() throws SyMException;
    
    boolean getEnvironment(final SomTrackingParameters p0) throws SyMException;
    
    boolean getADComputerCount(final SomTrackingParameters p0) throws SyMException;
    
    String nativeGetDCFCMKey() throws SyMException;
}
