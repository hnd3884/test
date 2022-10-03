package com.zoho.security.wafad.instrument;

import java.util.logging.Level;
import com.zoho.security.eventfwimpl.ZSecBuiltInFieldsImplProvider;
import com.zoho.security.wafad.WAFAttackDiscoveryUtil;
import com.zoho.logs.logclient.LogClientThreadLocal;
import com.zoho.security.eventfw.CalleeInfo;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.config.DataFields;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsProvider;

public class AttackDiscoveryBuiltInFieldsProvider implements BuiltInFieldsProvider
{
    private static final Logger LOGGER;
    private static Object logClientThreadLocalClass;
    private static BuiltInFieldsProvider zsecBuiltInFieldsImplProvider;
    
    public void fillData(final Map<String, Object> params, final DataFields builtInField, final ExecutionTimer timer, final CalleeInfo calleeInfo) {
        if (params.containsKey(builtInField.getName())) {
            return;
        }
        final String name;
        final String fieldName = name = builtInField.getName();
        switch (name) {
            case "RQ_LOG_ID": {
                final String requestId = (AttackDiscoveryBuiltInFieldsProvider.logClientThreadLocalClass != null) ? LogClientThreadLocal.getRequestID() : null;
                if (requestId != null) {
                    params.put("RQ_LOG_ID", requestId);
                }
                return;
            }
            default: {
                if (AttackDiscoveryBuiltInFieldsProvider.zsecBuiltInFieldsImplProvider != null) {
                    AttackDiscoveryBuiltInFieldsProvider.zsecBuiltInFieldsImplProvider.fillData((Map)params, builtInField, timer, calleeInfo);
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AttackDiscoveryBuiltInFieldsProvider.class.getName());
        AttackDiscoveryBuiltInFieldsProvider.logClientThreadLocalClass = null;
        if (WAFAttackDiscoveryUtil.isWafAgentAttached()) {
            AttackDiscoveryBuiltInFieldsProvider.zsecBuiltInFieldsImplProvider = (BuiltInFieldsProvider)new ZSecBuiltInFieldsImplProvider();
        }
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String ceClassName = "com.zoho.logs.logclient.LogClientThreadLocal";
        try {
            AttackDiscoveryBuiltInFieldsProvider.logClientThreadLocalClass = cl.loadClass(ceClassName);
            AttackDiscoveryBuiltInFieldsProvider.LOGGER.log(Level.WARNING, "LogClientThreadLocalClass Loaded Successfully. Class Name : {0} ", new Object[] { ceClassName });
        }
        catch (final ClassNotFoundException e) {
            AttackDiscoveryBuiltInFieldsProvider.LOGGER.log(Level.WARNING, "Unable to Load LogClientThreadLocalClass . Class Name : {0}, Exception {1}", new Object[] { ceClassName, e });
        }
    }
}
