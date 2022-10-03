package io.opencensus.metrics.data;

import java.util.Map;
import io.opencensus.common.Timestamp;

final class AutoValue_Exemplar extends Exemplar
{
    private final double value;
    private final Timestamp timestamp;
    private final Map<String, AttachmentValue> attachments;
    
    AutoValue_Exemplar(final double value, final Timestamp timestamp, final Map<String, AttachmentValue> attachments) {
        this.value = value;
        if (timestamp == null) {
            throw new NullPointerException("Null timestamp");
        }
        this.timestamp = timestamp;
        if (attachments == null) {
            throw new NullPointerException("Null attachments");
        }
        this.attachments = attachments;
    }
    
    @Override
    public double getValue() {
        return this.value;
    }
    
    @Override
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public Map<String, AttachmentValue> getAttachments() {
        return this.attachments;
    }
    
    @Override
    public String toString() {
        return "Exemplar{value=" + this.value + ", timestamp=" + this.timestamp + ", attachments=" + this.attachments + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Exemplar) {
            final Exemplar that = (Exemplar)o;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.getValue()) && this.timestamp.equals(that.getTimestamp()) && this.attachments.equals(that.getAttachments());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.value) >>> 32 ^ Double.doubleToLongBits(this.value)));
        h *= 1000003;
        h ^= this.timestamp.hashCode();
        h *= 1000003;
        h ^= this.attachments.hashCode();
        return h;
    }
}
