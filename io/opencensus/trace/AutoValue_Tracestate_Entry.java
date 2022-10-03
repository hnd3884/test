package io.opencensus.trace;

final class AutoValue_Tracestate_Entry extends Tracestate.Entry
{
    private final String key;
    private final String value;
    
    AutoValue_Tracestate_Entry(final String key, final String value) {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        this.key = key;
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
    }
    
    @Override
    public String getKey() {
        return this.key;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "Entry{key=" + this.key + ", value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Tracestate.Entry) {
            final Tracestate.Entry that = (Tracestate.Entry)o;
            return this.key.equals(that.getKey()) && this.value.equals(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.key.hashCode();
        h *= 1000003;
        h ^= this.value.hashCode();
        return h;
    }
}
