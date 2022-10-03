package io.opencensus.trace;

final class AutoValue_AttributeValue_AttributeValueBoolean extends AttributeValueBoolean
{
    private final Boolean booleanValue;
    
    AutoValue_AttributeValue_AttributeValueBoolean(final Boolean booleanValue) {
        if (booleanValue == null) {
            throw new NullPointerException("Null booleanValue");
        }
        this.booleanValue = booleanValue;
    }
    
    @Override
    Boolean getBooleanValue() {
        return this.booleanValue;
    }
    
    @Override
    public String toString() {
        return "AttributeValueBoolean{booleanValue=" + this.booleanValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttributeValueBoolean) {
            final AttributeValueBoolean that = (AttributeValueBoolean)o;
            return this.booleanValue.equals(that.getBooleanValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.booleanValue.hashCode();
        return h;
    }
}
