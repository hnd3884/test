package io.opencensus.trace.config;

import io.opencensus.trace.Sampler;

final class AutoValue_TraceParams extends TraceParams
{
    private final Sampler sampler;
    private final int maxNumberOfAttributes;
    private final int maxNumberOfAnnotations;
    private final int maxNumberOfMessageEvents;
    private final int maxNumberOfLinks;
    
    private AutoValue_TraceParams(final Sampler sampler, final int maxNumberOfAttributes, final int maxNumberOfAnnotations, final int maxNumberOfMessageEvents, final int maxNumberOfLinks) {
        this.sampler = sampler;
        this.maxNumberOfAttributes = maxNumberOfAttributes;
        this.maxNumberOfAnnotations = maxNumberOfAnnotations;
        this.maxNumberOfMessageEvents = maxNumberOfMessageEvents;
        this.maxNumberOfLinks = maxNumberOfLinks;
    }
    
    @Override
    public Sampler getSampler() {
        return this.sampler;
    }
    
    @Override
    public int getMaxNumberOfAttributes() {
        return this.maxNumberOfAttributes;
    }
    
    @Override
    public int getMaxNumberOfAnnotations() {
        return this.maxNumberOfAnnotations;
    }
    
    @Override
    public int getMaxNumberOfMessageEvents() {
        return this.maxNumberOfMessageEvents;
    }
    
    @Override
    public int getMaxNumberOfLinks() {
        return this.maxNumberOfLinks;
    }
    
    @Override
    public String toString() {
        return "TraceParams{sampler=" + this.sampler + ", maxNumberOfAttributes=" + this.maxNumberOfAttributes + ", maxNumberOfAnnotations=" + this.maxNumberOfAnnotations + ", maxNumberOfMessageEvents=" + this.maxNumberOfMessageEvents + ", maxNumberOfLinks=" + this.maxNumberOfLinks + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TraceParams) {
            final TraceParams that = (TraceParams)o;
            return this.sampler.equals(that.getSampler()) && this.maxNumberOfAttributes == that.getMaxNumberOfAttributes() && this.maxNumberOfAnnotations == that.getMaxNumberOfAnnotations() && this.maxNumberOfMessageEvents == that.getMaxNumberOfMessageEvents() && this.maxNumberOfLinks == that.getMaxNumberOfLinks();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.sampler.hashCode();
        h *= 1000003;
        h ^= this.maxNumberOfAttributes;
        h *= 1000003;
        h ^= this.maxNumberOfAnnotations;
        h *= 1000003;
        h ^= this.maxNumberOfMessageEvents;
        h *= 1000003;
        h ^= this.maxNumberOfLinks;
        return h;
    }
    
    @Override
    public TraceParams.Builder toBuilder() {
        return new Builder((TraceParams)this);
    }
    
    static final class Builder extends TraceParams.Builder
    {
        private Sampler sampler;
        private Integer maxNumberOfAttributes;
        private Integer maxNumberOfAnnotations;
        private Integer maxNumberOfMessageEvents;
        private Integer maxNumberOfLinks;
        
        Builder() {
        }
        
        private Builder(final TraceParams source) {
            this.sampler = source.getSampler();
            this.maxNumberOfAttributes = source.getMaxNumberOfAttributes();
            this.maxNumberOfAnnotations = source.getMaxNumberOfAnnotations();
            this.maxNumberOfMessageEvents = source.getMaxNumberOfMessageEvents();
            this.maxNumberOfLinks = source.getMaxNumberOfLinks();
        }
        
        @Override
        public TraceParams.Builder setSampler(final Sampler sampler) {
            if (sampler == null) {
                throw new NullPointerException("Null sampler");
            }
            this.sampler = sampler;
            return this;
        }
        
        @Override
        public TraceParams.Builder setMaxNumberOfAttributes(final int maxNumberOfAttributes) {
            this.maxNumberOfAttributes = maxNumberOfAttributes;
            return this;
        }
        
        @Override
        public TraceParams.Builder setMaxNumberOfAnnotations(final int maxNumberOfAnnotations) {
            this.maxNumberOfAnnotations = maxNumberOfAnnotations;
            return this;
        }
        
        @Override
        public TraceParams.Builder setMaxNumberOfMessageEvents(final int maxNumberOfMessageEvents) {
            this.maxNumberOfMessageEvents = maxNumberOfMessageEvents;
            return this;
        }
        
        @Override
        public TraceParams.Builder setMaxNumberOfLinks(final int maxNumberOfLinks) {
            this.maxNumberOfLinks = maxNumberOfLinks;
            return this;
        }
        
        @Override
        TraceParams autoBuild() {
            String missing = "";
            if (this.sampler == null) {
                missing += " sampler";
            }
            if (this.maxNumberOfAttributes == null) {
                missing += " maxNumberOfAttributes";
            }
            if (this.maxNumberOfAnnotations == null) {
                missing += " maxNumberOfAnnotations";
            }
            if (this.maxNumberOfMessageEvents == null) {
                missing += " maxNumberOfMessageEvents";
            }
            if (this.maxNumberOfLinks == null) {
                missing += " maxNumberOfLinks";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_TraceParams(this.sampler, this.maxNumberOfAttributes, this.maxNumberOfAnnotations, this.maxNumberOfMessageEvents, this.maxNumberOfLinks, null);
        }
    }
}
