package com.adventnet.sym.winaccess;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Logger;

public class WmiAccessProvider extends com.me.devicemanagement.onpremise.winaccess.WmiAccessProvider
{
    private static Logger log;
    private static Logger nativeLogger;
    private static WmiAccessProvider wmiAccessProvider;
    private static final String LIBRARY_NAME = "SyMNative";
    
    private WmiAccessProvider() {
    }
    
    public static WmiAccessProvider getInstance() {
        if (WmiAccessProvider.wmiAccessProvider == null) {
            WmiAccessProvider.wmiAccessProvider = new WmiAccessProvider();
        }
        return WmiAccessProvider.wmiAccessProvider;
    }
    
    public native String getOSName() throws SyMException;
    
    public native Hashtable getAntiVirusDetails() throws SyMException;
    
    static {
        WmiAccessProvider.log = Logger.getLogger(WmiAccessProvider.class.getName());
        WmiAccessProvider.nativeLogger = Logger.getLogger("NativeLogger");
        WmiAccessProvider.wmiAccessProvider = null;
        System.loadLibrary("SyMNative");
    }
}
