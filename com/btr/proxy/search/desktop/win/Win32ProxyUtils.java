package com.btr.proxy.search.desktop.win;

import java.io.File;
import java.io.IOException;

public class Win32ProxyUtils
{
    public static final int WINHTTP_AUTO_DETECT_TYPE_DHCP = 1;
    public static final int WINHTTP_AUTO_DETECT_TYPE_DNS_A = 2;
    
    public native String winHttpDetectAutoProxyConfigUrl(final int p0);
    
    native String winHttpGetDefaultProxyConfiguration();
    
    public native Win32IESettings winHttpGetIEProxyConfigForCurrentUser();
    
    public native String readUserHomedir();
    
    static {
        try {
            final File libFile = DLLManager.findLibFile();
            System.load(libFile.getAbsolutePath());
            DLLManager.cleanupTempFiles();
        }
        catch (final IOException e) {
            throw new RuntimeException("Error loading dll" + e.getMessage(), e);
        }
    }
}
