package io.opencensus.trace;

final class AutoValue_AttributeValue_AttributeValueLong extends AttributeValueLong
{
    private final Long longValue;
    
    AutoValue_AttributeValue_AttributeValueLong(final Long longValue) {
        if (longValue == null) {
            throw new NullPointerException("Null longValue");
        }
        this.longValue = longValue;
    }
    
    @Override
    Long getLongValue() {
        return this.longValue;
    }
    
    @Override
    public String toString() {
        return "AttributeValueLong{longValue=" + this.longValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttributeValueLong) {
            final AttributeValueLong that = (AttributeValueLong)o;
            return this.longValue.equals(that.getLongValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.longValue.hashCode();
        return h;
    }
}
