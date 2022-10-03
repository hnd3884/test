package io.opencensus.tags;

final class AutoValue_TagValue extends TagValue
{
    private final String asString;
    
    AutoValue_TagValue(final String asString) {
        if (asString == null) {
            throw new NullPointerException("Null asString");
        }
        this.asString = asString;
    }
    
    @Override
    public String asString() {
        return this.asString;
    }
    
    @Override
    public String toString() {
        return "TagValue{asString=" + this.asString + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TagValue) {
            final TagValue that = (TagValue)o;
            return this.asString.equals(that.asString());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.asString.hashCode();
        return h;
    }
}
