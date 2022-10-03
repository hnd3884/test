package io.opencensus.metrics;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class LabelKey
{
    LabelKey() {
    }
    
    public static LabelKey create(final String key, final String description) {
        return new AutoValue_LabelKey(key, description);
    }
    
    public abstract String getKey();
    
    public abstract String getDescription();
}
