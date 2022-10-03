package jdk.jfr.internal;

import jdk.jfr.internal.handlers.EventHandler;
import jdk.jfr.internal.instrument.JDKEvents;
import java.lang.reflect.Modifier;
import jdk.jfr.Event;

final class JVMUpcalls
{
    static byte[] onRetransform(final long n, final boolean b, final Class<?> clazz, final byte[] array) throws Throwable {
        try {
            if (!Event.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                return JDKEvents.retransformCallback(clazz, array);
            }
            if (Utils.getHandler(clazz.asSubclass(Event.class)) == null) {
                Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "No event handler found for " + clazz.getName() + ". Ignoring instrumentation request.");
                return array;
            }
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Adding instrumentation to event class " + clazz.getName() + " using retransform");
            final byte[] buildInstrumented = new EventInstrumentation(clazz.getSuperclass(), array, n).buildInstrumented();
            ASMToolkit.logASM(clazz.getName(), buildInstrumented);
            return buildInstrumented;
        }
        catch (final Throwable t) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, "Unexpected error when adding instrumentation to event class " + clazz.getName());
            return array;
        }
    }
    
    static byte[] bytesForEagerInstrumentation(final long n, final boolean b, final Class<?> clazz, final byte[] array) throws Throwable {
        if (JVMSupport.isNotAvailable()) {
            return array;
        }
        String eventName = "<Unknown>";
        try {
            final EventInstrumentation eventInstrumentation = new EventInstrumentation(clazz, array, n);
            eventName = eventInstrumentation.getEventName();
            if (!b && ((!MetadataRepository.getInstance().isEnabled(eventInstrumentation.getEventName()) && !eventInstrumentation.isEnabled()) || !eventInstrumentation.isRegistered())) {
                Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Skipping instrumentation for event type " + eventName + " since event was disabled on class load");
                return array;
            }
            eventInstrumentation.setGuardHandler(true);
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Adding " + (b ? "forced " : "") + "instrumentation for event type " + eventName + " during initial class load");
            new EventHandlerCreator(n, eventInstrumentation.getSettingInfos(), eventInstrumentation.getFieldInfos()).makeEventHandlerClass();
            final byte[] buildInstrumented = eventInstrumentation.buildInstrumented();
            ASMToolkit.logASM(eventInstrumentation.getClassName() + "(" + n + ")", buildInstrumented);
            return buildInstrumented;
        }
        catch (final Throwable t) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, "Unexpected error when adding instrumentation for event type " + eventName);
            return array;
        }
    }
    
    static Thread createRecorderThread(final ThreadGroup threadGroup, final ClassLoader classLoader) {
        return SecuritySupport.createRecorderThread(threadGroup, classLoader);
    }
    
    static Class<? extends EventHandler> getEventHandlerProxyClass() {
        return EventHandlerProxyCreator.proxyClass;
    }
}
