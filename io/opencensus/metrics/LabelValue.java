package io.opencensus.metrics;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class LabelValue
{
    LabelValue() {
    }
    
    public static LabelValue create(@Nullable final String value) {
        return new AutoValue_LabelValue(value);
    }
    
    @Nullable
    public abstract String getValue();
}
