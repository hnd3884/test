package com.adventnet.sym.winaccess;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class WinAccessProvider extends com.me.devicemanagement.onpremise.winaccess.WinAccessProvider
{
    private static Logger log;
    public static Logger adLogger;
    public static Logger nativeLogger;
    public static Logger eventLogger;
    public static Logger agentLogger;
    public static Logger remoteaccessLogger;
    private static String name;
    private static String key;
    private static int currentScopeType;
    private static String currentScopeValue;
    private static String defaultDCName;
    
    private WinAccessProvider() {
    }
    
    public static String getUserName() {
        if (WinAccessProvider.name == null && System.getProperty("isStandAlone") == null) {
            WinAccessProvider.name = SyMUtil.getSyMParameter("domainUserName");
        }
        return WinAccessProvider.name;
    }
    
    public static String getDefaultDCName() {
        if (System.getProperty("isStandAlone") == null) {
            return SyMUtil.getSyMParameter("ALTERNATE_DC");
        }
        return WinAccessProvider.defaultDCName;
    }
    
    public static String getKey() throws SyMException {
        if (WinAccessProvider.key == null && System.getProperty("isStandAlone") == null) {
            try {
                WinAccessProvider.key = Encoder.convertFromBase(SyMUtil.getSyMParameter("domainPassword"));
            }
            catch (final Exception ex) {
                WinAccessProvider.log.warning("Exception while getting Password " + ex);
                throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
            }
        }
        return WinAccessProvider.key;
    }
    
    public static void setScope(final int scopeType, final String scopeValue) {
        WinAccessProvider.currentScopeType = scopeType;
        WinAccessProvider.currentScopeValue = scopeValue;
    }
    
    public static int getScopeType() {
        return WinAccessProvider.currentScopeType;
    }
    
    public static String getScopeValue() {
        return WinAccessProvider.currentScopeValue;
    }
    
    static {
        WinAccessProvider.log = Logger.getLogger(WinAccessProvider.class.getName());
        WinAccessProvider.adLogger = Logger.getLogger("ADLogger");
        WinAccessProvider.nativeLogger = Logger.getLogger("NativeLogger");
        WinAccessProvider.eventLogger = Logger.getLogger("EventLogger");
        WinAccessProvider.agentLogger = Logger.getLogger("AgentInstallerLogger");
        WinAccessProvider.remoteaccessLogger = Logger.getLogger("RemoteAccessLog");
        WinAccessProvider.name = null;
        WinAccessProvider.key = null;
        WinAccessProvider.currentScopeType = 0;
        WinAccessProvider.currentScopeValue = null;
        WinAccessProvider.defaultDCName = null;
    }
}
