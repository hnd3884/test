package io.opencensus.metrics.data;

import javax.annotation.concurrent.Immutable;

public abstract class AttachmentValue
{
    public abstract String getValue();
    
    @Immutable
    public abstract static class AttachmentValueString extends AttachmentValue
    {
        AttachmentValueString() {
        }
        
        public static AttachmentValueString create(final String value) {
            return new AutoValue_AttachmentValue_AttachmentValueString(value);
        }
    }
}
