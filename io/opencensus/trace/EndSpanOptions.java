package io.opencensus.trace;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class EndSpanOptions
{
    public static final EndSpanOptions DEFAULT;
    
    public static Builder builder() {
        return new AutoValue_EndSpanOptions.Builder().setSampleToLocalSpanStore(false);
    }
    
    public abstract boolean getSampleToLocalSpanStore();
    
    @Nullable
    public abstract Status getStatus();
    
    EndSpanOptions() {
    }
    
    static {
        DEFAULT = builder().build();
    }
    
    public abstract static class Builder
    {
        public abstract Builder setStatus(final Status p0);
        
        public abstract Builder setSampleToLocalSpanStore(final boolean p0);
        
        public abstract EndSpanOptions build();
        
        Builder() {
        }
    }
}
