package jdk.management.jfr.internal;

import jdk.jfr.internal.management.ManagementSupport;
import javax.management.openmbean.CompositeData;
import jdk.management.jfr.SettingDescriptorInfo;
import jdk.management.jfr.FlightRecorderMXBean;
import java.util.concurrent.Callable;

public final class FlightRecorderMXBeanProvider
{
    private static Callable<FlightRecorderMXBean> flightRecorderMXBeanFactory;
    private static volatile FlightRecorderMXBean flightRecorderMXBean;
    
    public static FlightRecorderMXBean getFlightRecorderMXBean() {
        FlightRecorderMXBean flightRecorderMXBean = FlightRecorderMXBeanProvider.flightRecorderMXBean;
        if (flightRecorderMXBean == null) {
            SettingDescriptorInfo.from(null);
            synchronized (FlightRecorderMXBeanProvider.flightRecorderMXBeanFactory) {
                flightRecorderMXBean = FlightRecorderMXBeanProvider.flightRecorderMXBean;
                if (flightRecorderMXBean != null) {
                    return flightRecorderMXBean;
                }
                try {
                    flightRecorderMXBean = (FlightRecorderMXBeanProvider.flightRecorderMXBean = FlightRecorderMXBeanProvider.flightRecorderMXBeanFactory.call());
                }
                catch (final Exception ex) {
                    ManagementSupport.logError("Could not create Flight Recorder instance for MBeanServer. " + ex.getMessage());
                }
            }
        }
        return flightRecorderMXBean;
    }
    
    public static void setFlightRecorderMXBeanFactory(final Callable<FlightRecorderMXBean> flightRecorderMXBeanFactory) {
        FlightRecorderMXBeanProvider.flightRecorderMXBeanFactory = flightRecorderMXBeanFactory;
    }
    
    private static final class SingleMBeanComponent
    {
        private final String objectName;
        private final Class<FlightRecorderMXBean> mbeanInterface;
        
        public SingleMBeanComponent(final String objectName, final Class<FlightRecorderMXBean> mbeanInterface) {
            this.objectName = objectName;
            this.mbeanInterface = mbeanInterface;
        }
    }
}
