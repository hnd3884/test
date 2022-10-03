package io.opencensus.stats;

import io.opencensus.tags.TagContext;
import io.opencensus.internal.Utils;
import io.opencensus.metrics.data.AttachmentValue;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class MeasureMap
{
    public abstract MeasureMap put(final Measure.MeasureDouble p0, final double p1);
    
    public abstract MeasureMap put(final Measure.MeasureLong p0, final long p1);
    
    @Deprecated
    public MeasureMap putAttachment(final String key, final String value) {
        return this.putAttachment(key, AttachmentValue.AttachmentValueString.create(value));
    }
    
    public MeasureMap putAttachment(final String key, final AttachmentValue value) {
        Utils.checkNotNull(key, "key");
        Utils.checkNotNull(value, "value");
        return this;
    }
    
    public abstract void record();
    
    public abstract void record(final TagContext p0);
}
