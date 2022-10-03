package sun.management;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.lang.management.PlatformManagedObject;
import java.util.List;

public final class ExtendedPlatformComponent
{
    private ExtendedPlatformComponent() {
    }
    
    public static List<? extends PlatformManagedObject> getMXBeans() {
        final PlatformManagedObject flightRecorderBean = getFlightRecorderBean();
        if (flightRecorderBean != null) {
            return Collections.singletonList(flightRecorderBean);
        }
        return Collections.emptyList();
    }
    
    public static <T extends PlatformManagedObject> T getMXBean(final Class<T> clazz) {
        if ("jdk.management.jfr.FlightRecorderMXBean".equals(clazz.getName())) {
            return (T)getFlightRecorderBean();
        }
        return null;
    }
    
    private static PlatformManagedObject getFlightRecorderBean() {
        PlatformManagedObject platformManagedObject = null;
        try {
            platformManagedObject = (PlatformManagedObject)Class.forName("jdk.management.jfr.internal.FlightRecorderMXBeanProvider").getDeclaredMethod("getFlightRecorderMXBean", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {}
        return platformManagedObject;
    }
}
