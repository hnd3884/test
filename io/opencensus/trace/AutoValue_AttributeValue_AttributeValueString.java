package io.opencensus.trace;

final class AutoValue_AttributeValue_AttributeValueString extends AttributeValueString
{
    private final String stringValue;
    
    AutoValue_AttributeValue_AttributeValueString(final String stringValue) {
        if (stringValue == null) {
            throw new NullPointerException("Null stringValue");
        }
        this.stringValue = stringValue;
    }
    
    @Override
    String getStringValue() {
        return this.stringValue;
    }
    
    @Override
    public String toString() {
        return "AttributeValueString{stringValue=" + this.stringValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttributeValueString) {
            final AttributeValueString that = (AttributeValueString)o;
            return this.stringValue.equals(that.getStringValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.stringValue.hashCode();
        return h;
    }
}
