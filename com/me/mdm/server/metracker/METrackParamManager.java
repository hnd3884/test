package com.me.mdm.server.metracker;

import java.lang.reflect.Method;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class METrackParamManager
{
    private static String sourceClass;
    private static Logger logger;
    
    public static void incrementMETrackParams(final String param) {
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            invokeMethod(param, 1);
        }
    }
    
    public static void incrementMETrackParams(final String param, final int value) {
        CustomerInfoUtil.getInstance();
        if (!CustomerInfoUtil.isSAS()) {
            invokeMethod(param, value);
        }
    }
    
    private static void invokeMethod(final String param, final int value) {
        try {
            final String methodName = "incrementMETrackParams";
            final Object instance = getMetrackUtilInstance();
            final Method method = instance.getClass().getMethod(methodName, String.class, Integer.TYPE);
            method.invoke(instance, param, value);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackParamManager.logger, METrackParamManager.sourceClass, "invokeMethod", "Exception occurred : ", (Throwable)e);
        }
    }
    
    private static Object getMetrackUtilInstance() {
        try {
            return Class.forName("com.me.devicemanagement.onpremise.server.metrack.METrackerUtil").newInstance();
        }
        catch (final Exception e) {
            SyMLogger.error(METrackParamManager.logger, METrackParamManager.sourceClass, "getMetrackUtilInstance", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    static {
        METrackParamManager.sourceClass = "METrackParamManager";
        METrackParamManager.logger = Logger.getLogger("METrackLog");
    }
}
