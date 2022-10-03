package org.glassfish.jersey.server.internal.monitoring;

import java.util.Map;
import org.glassfish.jersey.server.ServerProperties;
import javax.ws.rs.core.FeatureContext;
import javax.annotation.Priority;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;

@ConstrainedTo(RuntimeType.SERVER)
@Priority(2000)
public final class MonitoringAutodiscoverable implements ForcedAutoDiscoverable
{
    public void configure(final FeatureContext context) {
        if (!context.getConfiguration().isRegistered((Class)MonitoringFeature.class)) {
            final Boolean monitoringEnabled = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.enabled", Boolean.FALSE);
            final Boolean statisticsEnabled = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.enabled", Boolean.FALSE);
            final Boolean mbeansEnabled = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.mbeans.enabled", Boolean.FALSE);
            if (monitoringEnabled || statisticsEnabled || mbeansEnabled) {
                context.register((Class)MonitoringFeature.class);
            }
        }
    }
}
