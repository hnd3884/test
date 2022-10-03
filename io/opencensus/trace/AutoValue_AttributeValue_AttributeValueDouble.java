package io.opencensus.trace;

final class AutoValue_AttributeValue_AttributeValueDouble extends AttributeValueDouble
{
    private final Double doubleValue;
    
    AutoValue_AttributeValue_AttributeValueDouble(final Double doubleValue) {
        if (doubleValue == null) {
            throw new NullPointerException("Null doubleValue");
        }
        this.doubleValue = doubleValue;
    }
    
    @Override
    Double getDoubleValue() {
        return this.doubleValue;
    }
    
    @Override
    public String toString() {
        return "AttributeValueDouble{doubleValue=" + this.doubleValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttributeValueDouble) {
            final AttributeValueDouble that = (AttributeValueDouble)o;
            return this.doubleValue.equals(that.getDoubleValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.doubleValue.hashCode();
        return h;
    }
}
