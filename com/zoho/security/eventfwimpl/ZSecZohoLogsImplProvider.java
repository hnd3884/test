package com.zoho.security.eventfwimpl;

import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import com.zoho.security.eventfw.logImpl.ZohoLogsImplProvider;

public class ZSecZohoLogsImplProvider extends ZohoLogsImplProvider
{
    private static boolean isAppSenseEnabled;
    
    public void doLog(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        if (ZSecZohoLogsImplProvider.isAppSenseEnabled) {
            super.doLog((Map)eventObject, calleeInfo);
        }
    }
    
    public static void setAppSenseEnabledStatus(final boolean appSenseEnabled) {
        ZSecZohoLogsImplProvider.isAppSenseEnabled = appSenseEnabled;
    }
    
    static {
        ZSecZohoLogsImplProvider.isAppSenseEnabled = false;
    }
}
