package com.zoho.security.eventfw.builtinfieldsImpl;

import java.util.logging.Level;
import java.net.InetAddress;
import com.zoho.security.eventfw.CalleeInfo;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.config.DataFields;
import java.util.Map;
import java.util.logging.Logger;

public final class BuiltInFieldsImplProvider implements BuiltInFieldsProvider
{
    public static final Logger LOGGER;
    private static final String TIME_TAKEN = "TIME_TAKEN";
    public static final String HOST_IP = "RQ_HOST";
    public static final String ORIGIN_CLASS_AND_METHOD_NAME = "ORIGIN_CLASS_AND_METHOD_NAME";
    public static final String CALLEE_CLASS_AND_METHOD_NAME = "CALLEE_CLASS_AND_METHOD_NAME";
    private static String hostIp;
    
    @Override
    public void fillData(final Map<String, Object> params, final DataFields builtInField, final ExecutionTimer timer, final CalleeInfo calleeInfo) {
        final String name;
        final String fieldName = name = builtInField.getName();
        switch (name) {
            case "TIME_TAKEN": {
                if (timer != null) {
                    params.put("TIME_TAKEN", timer.getExecutionTime());
                    break;
                }
                break;
            }
            case "RQ_HOST": {
                params.put("RQ_HOST", BuiltInFieldsImplProvider.hostIp);
                break;
            }
            case "ORIGIN_CLASS_AND_METHOD_NAME": {
                if (calleeInfo.getMonitoringClassName() != null && calleeInfo.getMonitoringMethodName() != null) {
                    params.put("ORIGIN_CLASS_AND_METHOD_NAME", calleeInfo.getMonitoringClassName() + ":" + calleeInfo.getMonitoringMethodName());
                    break;
                }
                break;
            }
            case "CALLEE_CLASS_AND_METHOD_NAME": {
                if (calleeInfo.getCalleeClassName() != null && calleeInfo.getCalleeMethodName() != null) {
                    params.put("CALLEE_CLASS_AND_METHOD_NAME", calleeInfo.getCalleeClassName() + ":" + calleeInfo.getCalleeMethodName());
                    break;
                }
                break;
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(BuiltInFieldsImplProvider.class.getName());
        BuiltInFieldsImplProvider.hostIp = "";
        try {
            BuiltInFieldsImplProvider.hostIp = InetAddress.getLocalHost().getHostAddress();
        }
        catch (final Exception e) {
            BuiltInFieldsImplProvider.LOGGER.log(Level.WARNING, "Exception while getting host ip address", e);
        }
    }
}
