package io.opencensus.trace.config;

import io.opencensus.internal.Utils;
import io.opencensus.trace.samplers.Samplers;
import io.opencensus.trace.Sampler;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TraceParams
{
    private static final double DEFAULT_PROBABILITY = 1.0E-4;
    private static final Sampler DEFAULT_SAMPLER;
    private static final int DEFAULT_SPAN_MAX_NUM_ATTRIBUTES = 32;
    private static final int DEFAULT_SPAN_MAX_NUM_ANNOTATIONS = 32;
    private static final int DEFAULT_SPAN_MAX_NUM_MESSAGE_EVENTS = 128;
    private static final int DEFAULT_SPAN_MAX_NUM_LINKS = 32;
    public static final TraceParams DEFAULT;
    
    public abstract Sampler getSampler();
    
    public abstract int getMaxNumberOfAttributes();
    
    public abstract int getMaxNumberOfAnnotations();
    
    public abstract int getMaxNumberOfMessageEvents();
    
    @Deprecated
    public int getMaxNumberOfNetworkEvents() {
        return this.getMaxNumberOfMessageEvents();
    }
    
    public abstract int getMaxNumberOfLinks();
    
    private static Builder builder() {
        return new AutoValue_TraceParams.Builder();
    }
    
    public abstract Builder toBuilder();
    
    static {
        DEFAULT_SAMPLER = Samplers.probabilitySampler(1.0E-4);
        DEFAULT = builder().setSampler(TraceParams.DEFAULT_SAMPLER).setMaxNumberOfAttributes(32).setMaxNumberOfAnnotations(32).setMaxNumberOfMessageEvents(128).setMaxNumberOfLinks(32).build();
    }
    
    public abstract static class Builder
    {
        public abstract Builder setSampler(final Sampler p0);
        
        public abstract Builder setMaxNumberOfAttributes(final int p0);
        
        public abstract Builder setMaxNumberOfAnnotations(final int p0);
        
        public abstract Builder setMaxNumberOfMessageEvents(final int p0);
        
        @Deprecated
        public Builder setMaxNumberOfNetworkEvents(final int maxNumberOfNetworkEvents) {
            return this.setMaxNumberOfMessageEvents(maxNumberOfNetworkEvents);
        }
        
        public abstract Builder setMaxNumberOfLinks(final int p0);
        
        abstract TraceParams autoBuild();
        
        public TraceParams build() {
            final TraceParams traceParams = this.autoBuild();
            Utils.checkArgument(traceParams.getMaxNumberOfAttributes() > 0, (Object)"maxNumberOfAttributes");
            Utils.checkArgument(traceParams.getMaxNumberOfAnnotations() > 0, (Object)"maxNumberOfAnnotations");
            Utils.checkArgument(traceParams.getMaxNumberOfMessageEvents() > 0, (Object)"maxNumberOfMessageEvents");
            Utils.checkArgument(traceParams.getMaxNumberOfLinks() > 0, (Object)"maxNumberOfLinks");
            return traceParams;
        }
    }
}
