package com.zoho.security.eventfw;

import com.zoho.security.eventfw.config.EventConfigParser;
import com.zoho.security.eventfw.type.EventProcessor;

public class EventCallerInferrer
{
    private static final CalleeInfoStackWalk POJO_CALLEE_INFO_STACK_WALK;
    
    public static CalleeInfo inferClass(final EventProcessor eventProcessor, final String defaultClassName, final String defaultMethodName) {
        return inferClass(eventProcessor, EventCallerInferrer.POJO_CALLEE_INFO_STACK_WALK, defaultClassName, defaultMethodName);
    }
    
    public static CalleeInfo inferClass(final EventProcessor eventProcessor, final CalleeInfoStackWalk calleeInfoStackWalk, final String defaultClassName, final String defaultMethodName) {
        if (eventProcessor == null || eventProcessor.getCallerInferrerMode() == CallerInferrerMode.DISABLE) {
            return new CalleeInfo(null, null, null, null);
        }
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        final int stackTraceStartIndex = calleeInfoStackWalk.getStackTraceStartIndex(trace);
        if (stackTraceStartIndex != -1) {
            if (eventProcessor.getCallerInferrerMode() == CallerInferrerMode.ENABLE) {
                for (int traceIndex = stackTraceStartIndex + 1; traceIndex < trace.length; ++traceIndex) {
                    final String nexttoImmediateClassName = trace[traceIndex].getClassName();
                    if (!calleeInfoStackWalk.isInvalidCalleeClass(nexttoImmediateClassName)) {
                        return new CalleeInfo(trace[stackTraceStartIndex].getClassName(), trace[stackTraceStartIndex].getMethodName(), nexttoImmediateClassName, trace[traceIndex].getMethodName());
                    }
                }
            }
            return new CalleeInfo(trace[stackTraceStartIndex].getClassName(), trace[stackTraceStartIndex].getMethodName(), null, null);
        }
        return new CalleeInfo(defaultClassName, defaultMethodName, null, null);
    }
    
    static {
        POJO_CALLEE_INFO_STACK_WALK = new CalleeInfoStackWalk() {
            @Override
            public int getStackTraceStartIndex(final StackTraceElement[] traces) {
                return (traces.length > 4) ? 4 : -1;
            }
            
            @Override
            public boolean isInvalidCalleeClass(final String className) {
                final EventConfigParser parser = EventDataProcessor.getParser();
                return parser.getCalleeInfoExcludePattern().matcher(className).matches() || this.isInheritClassMatches(parser.getCalleeInfoInheritClassExcludes(), className);
            }
        };
    }
    
    public enum CallerInferrerMode
    {
        DISABLE, 
        MONITOR_CLS_ONLY, 
        ENABLE;
    }
    
    public interface CalleeInfoStackWalk
    {
        int getStackTraceStartIndex(final StackTraceElement[] p0);
        
        boolean isInvalidCalleeClass(final String p0);
        
        default boolean isInheritClassMatches(final Class<?>[] inheritClassToBeExcludes, final String className) {
            if (inheritClassToBeExcludes.length == 0) {
                return false;
            }
            Class<?> cls;
            try {
                cls = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            }
            catch (final ClassNotFoundException e) {
                return false;
            }
            for (final Class<?> inheritClass : inheritClassToBeExcludes) {
                if (inheritClass.isAssignableFrom(cls)) {
                    return true;
                }
            }
            return false;
        }
    }
}
