package com.sun.java.accessibility;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import jdk.Exported;

@Exported(false)
abstract class AccessBridgeLoader
{
    boolean useJAWT_DLL;
    
    AccessBridgeLoader() {
        this.useJAWT_DLL = false;
        final String property = System.getProperty("java.version");
        if (property != null) {
            this.useJAWT_DLL = (property.compareTo("1.4.1") >= 0);
        }
        if (this.useJAWT_DLL) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    System.loadLibrary("JAWT");
                    System.loadLibrary("JAWTAccessBridge-64");
                    return null;
                }
            }, null, new RuntimePermission("loadLibrary.JAWT"), new RuntimePermission("loadLibrary.JAWTAccessBridge-64"));
        }
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                System.loadLibrary("JavaAccessBridge-64");
                return null;
            }
        }, null, new RuntimePermission("loadLibrary.JavaAccessBridge-64"));
    }
}
