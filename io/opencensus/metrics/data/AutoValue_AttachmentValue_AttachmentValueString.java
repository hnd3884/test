package io.opencensus.metrics.data;

final class AutoValue_AttachmentValue_AttachmentValueString extends AttachmentValueString
{
    private final String value;
    
    AutoValue_AttachmentValue_AttachmentValueString(final String value) {
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "AttachmentValueString{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttachmentValueString) {
            final AttachmentValueString that = (AttachmentValueString)o;
            return this.value.equals(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.value.hashCode();
        return h;
    }
}
