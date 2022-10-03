package io.opencensus.metrics.data;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import io.opencensus.internal.Utils;
import java.util.Map;
import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Exemplar
{
    Exemplar() {
    }
    
    public abstract double getValue();
    
    public abstract Timestamp getTimestamp();
    
    public abstract Map<String, AttachmentValue> getAttachments();
    
    public static Exemplar create(final double value, final Timestamp timestamp, final Map<String, AttachmentValue> attachments) {
        Utils.checkNotNull(attachments, "attachments");
        final Map<String, AttachmentValue> attachmentsCopy = Collections.unmodifiableMap((Map<? extends String, ? extends AttachmentValue>)new HashMap<String, AttachmentValue>(attachments));
        for (final Map.Entry<String, AttachmentValue> entry : attachmentsCopy.entrySet()) {
            Utils.checkNotNull(entry.getKey(), "key of attachments");
            Utils.checkNotNull(entry.getValue(), "value of attachments");
        }
        return new AutoValue_Exemplar(value, timestamp, attachmentsCopy);
    }
}
