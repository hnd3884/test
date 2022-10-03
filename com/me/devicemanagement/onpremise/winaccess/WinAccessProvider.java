package com.me.devicemanagement.onpremise.winaccess;

import com.me.devicemanagement.framework.server.util.SomTrackingParameters;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.WinAccessAPI;

public class WinAccessProvider extends com.me.devicemanagement.framework.winaccess.WinAccessProvider implements WinAccessAPI
{
    private static WinAccessProvider winAccessProvider;
    private static final String LIBRARY_NAME = "SyMNative";
    protected String strCurrentDomainNetBIOSName;
    public static Logger agentLogger;
    
    public WinAccessProvider() {
        this.strCurrentDomainNetBIOSName = null;
    }
    
    public static WinAccessProvider getInstance() {
        if (WinAccessProvider.winAccessProvider == null) {
            WinAccessProvider.winAccessProvider = new WinAccessProvider();
        }
        return WinAccessProvider.winAccessProvider;
    }
    
    public native String getOSArchitecture() throws SyMException;
    
    public native void setServiceStartupType(final String p0, final boolean p1) throws SyMException;
    
    public void setServiceStartupType(final String serviceName, final String type) throws SyMException {
        this.setServiceStartupType(serviceName, "automatic".contentEquals(type.toLowerCase()));
    }
    
    public native String getServiceStartupType(final String p0) throws SyMException;
    
    public native boolean nativeIsFirewallEnabledInDCServer(final String p0, final String p1, final String p2, final long p3, final String p4) throws SyMException;
    
    public native boolean nativeOpenFirewallPort(final String p0, final String p1, final String p2, final long p3, final String p4) throws SyMException;
    
    public native String nativeGetCurrentNetBIOSName() throws SyMException;
    
    public native String getDomainFlatName() throws SyMException;
    
    public native int isAD() throws SyMException;
    
    public native String getCurrentDomainName() throws SyMException;
    
    public native String getServerName() throws SyMException;
    
    public native boolean getEnvironment(final SomTrackingParameters p0) throws SyMException;
    
    public native boolean getADComputerCount(final SomTrackingParameters p0) throws SyMException;
    
    public native String decrypt(final String p0, final String p1);
    
    public native String encrypt(final String p0, final String p1);
    
    public native String decryptaes(final String p0, final String p1);
    
    public native String encryptaes(final String p0, final String p1);
    
    public native String[] getDNSSuffix();
    
    public native String getEncryptedString(final String p0, final String p1, final String p2);
    
    public native String getDecryptedString(final String p0, final String p1, final String p2);
    
    public native String encryptaesUsingDeveloperKey(final String p0, final String p1);
    
    public native String decryptaesUsingDeveloperKey(final String p0, final String p1);
    
    public native String nativeGetDCFCMKey() throws SyMException;
    
    public native String nativeGetDefaultDBBackupPassword() throws SyMException;
    
    static {
        WinAccessProvider.winAccessProvider = null;
        WinAccessProvider.agentLogger = Logger.getLogger("AgentInstallerLogger");
        System.loadLibrary("SyMNative");
    }
}
