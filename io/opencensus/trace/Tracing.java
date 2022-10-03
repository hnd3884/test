package io.opencensus.trace;

import java.util.logging.Level;
import io.opencensus.internal.Provider;
import javax.annotation.Nullable;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.export.ExportComponent;
import io.opencensus.common.Clock;
import io.opencensus.trace.propagation.PropagationComponent;
import java.util.logging.Logger;

public final class Tracing
{
    private static final Logger logger;
    private static final TraceComponent traceComponent;
    
    public static Tracer getTracer() {
        return Tracing.traceComponent.getTracer();
    }
    
    public static PropagationComponent getPropagationComponent() {
        return Tracing.traceComponent.getPropagationComponent();
    }
    
    public static Clock getClock() {
        return Tracing.traceComponent.getClock();
    }
    
    public static ExportComponent getExportComponent() {
        return Tracing.traceComponent.getExportComponent();
    }
    
    public static TraceConfig getTraceConfig() {
        return Tracing.traceComponent.getTraceConfig();
    }
    
    static TraceComponent loadTraceComponent(@Nullable final ClassLoader classLoader) {
        try {
            return Provider.createInstance(Class.forName("io.opencensus.impl.trace.TraceComponentImpl", true, classLoader), TraceComponent.class);
        }
        catch (final ClassNotFoundException e) {
            Tracing.logger.log(Level.FINE, "Couldn't load full implementation for TraceComponent, now trying to load lite implementation.", e);
            try {
                return Provider.createInstance(Class.forName("io.opencensus.impllite.trace.TraceComponentImplLite", true, classLoader), TraceComponent.class);
            }
            catch (final ClassNotFoundException e) {
                Tracing.logger.log(Level.FINE, "Couldn't load lite implementation for TraceComponent, now using default implementation for TraceComponent.", e);
                return TraceComponent.newNoopTraceComponent();
            }
        }
    }
    
    private Tracing() {
    }
    
    static {
        logger = Logger.getLogger(Tracing.class.getName());
        traceComponent = loadTraceComponent(TraceComponent.class.getClassLoader());
    }
}
