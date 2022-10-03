package com.adventnet.tools.update.installer.log;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.tools.update.CommonUtil;
import com.adventnet.tools.update.installer.UpdateManagerParser;

public class UpdateManagerLogManager
{
    private static UpdateManagerLogInterface smartLogImpl;
    
    public UpdateManagerLogManager(final String confFilePath) {
        UpdateManagerLogManager.smartLogImpl = this.getLogImpl(confFilePath);
    }
    
    private UpdateManagerLogInterface getLogImpl(final String confFilePath) {
        Class logImplClass = null;
        final UpdateManagerParser ump = new UpdateManagerParser(confFilePath);
        Properties logImplClassProps = ump.getLogImplClassProps();
        String logImplClassName = null;
        if (logImplClassProps != null) {
            logImplClassName = ((Hashtable<K, String>)logImplClassProps).remove("className");
        }
        if (logImplClassName == null) {
            logImplClassName = "com.adventnet.tools.update.installer.log.UpdateManagerLogImpl";
        }
        try {
            logImplClass = Class.forName(logImplClassName);
        }
        catch (final ClassNotFoundException ce) {
            CommonUtil.printError("Unable to load LogImpl Class " + logImplClassName);
        }
        try {
            if (UpdateManagerLogInterface.class.isAssignableFrom(logImplClass)) {
                UpdateManagerLogManager.smartLogImpl = logImplClass.newInstance();
                if (logImplClassProps == null) {
                    logImplClassProps = new Properties();
                }
                UpdateManagerLogManager.smartLogImpl.init(logImplClassProps);
            }
            else {
                CommonUtil.printError("Class " + logImplClassName + " doesn't implement " + "UpdateManagerLogInterface interface");
            }
        }
        catch (final InstantiationException ie) {
            CommonUtil.printError("Unable to instantiate LogImpl Class " + logImplClassName);
        }
        catch (final IllegalAccessException iae) {
            CommonUtil.printError("Unable to access LogImpl Class " + logImplClassName);
        }
        return UpdateManagerLogManager.smartLogImpl;
    }
    
    public UpdateManagerLogInterface getLogInterface() {
        return UpdateManagerLogManager.smartLogImpl;
    }
    
    public static void log(final String message) {
        UpdateManagerLogManager.smartLogImpl.log(message);
    }
    
    public static void log(final String message, final int level) {
        UpdateManagerLogManager.smartLogImpl.log(message, level);
    }
    
    public static void fail(final String message) {
        UpdateManagerLogManager.smartLogImpl.fail(message);
    }
    
    public static void fail(final String message, final Throwable e) {
        UpdateManagerLogManager.smartLogImpl.fail(message, e);
    }
    
    public static void dispose() {
        UpdateManagerLogManager.smartLogImpl = null;
    }
    
    public static void close() {
        UpdateManagerLogManager.smartLogImpl.close();
    }
    
    static {
        UpdateManagerLogManager.smartLogImpl = null;
    }
}
