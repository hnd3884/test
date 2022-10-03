package com.me.devicemanagement.onpremise.winaccess;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;

public class WmiAccessProvider
{
    private static WmiAccessProvider wmiAccessProvider;
    private static final String LIBRARY_NAME = "SyMNative";
    
    public static WmiAccessProvider getInstance() {
        if (WmiAccessProvider.wmiAccessProvider == null) {
            WmiAccessProvider.wmiAccessProvider = new WmiAccessProvider();
        }
        return WmiAccessProvider.wmiAccessProvider;
    }
    
    public native String getRAMDetails() throws SyMException;
    
    public native Hashtable getOSDetails() throws SyMException;
    
    public native Hashtable getSystemProperties() throws SyMException;
    
    public native Hashtable getAntiVirusDetails() throws SyMException;
    
    static {
        WmiAccessProvider.wmiAccessProvider = null;
        System.loadLibrary("SyMNative");
    }
}
