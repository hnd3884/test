package io.opencensus.trace.samplers;

import io.opencensus.trace.Sampler;

public final class Samplers
{
    private static final Sampler ALWAYS_SAMPLE;
    private static final Sampler NEVER_SAMPLE;
    
    private Samplers() {
    }
    
    public static Sampler alwaysSample() {
        return Samplers.ALWAYS_SAMPLE;
    }
    
    public static Sampler neverSample() {
        return Samplers.NEVER_SAMPLE;
    }
    
    public static Sampler probabilitySampler(final double probability) {
        return ProbabilitySampler.create(probability);
    }
    
    static {
        ALWAYS_SAMPLE = new AlwaysSampleSampler();
        NEVER_SAMPLE = new NeverSampleSampler();
    }
}
