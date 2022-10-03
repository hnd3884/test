package io.opencensus.trace.export;

import io.opencensus.trace.AttributeValue;
import java.util.Map;

final class AutoValue_SpanData_Attributes extends SpanData.Attributes
{
    private final Map<String, AttributeValue> attributeMap;
    private final int droppedAttributesCount;
    
    AutoValue_SpanData_Attributes(final Map<String, AttributeValue> attributeMap, final int droppedAttributesCount) {
        if (attributeMap == null) {
            throw new NullPointerException("Null attributeMap");
        }
        this.attributeMap = attributeMap;
        this.droppedAttributesCount = droppedAttributesCount;
    }
    
    @Override
    public Map<String, AttributeValue> getAttributeMap() {
        return this.attributeMap;
    }
    
    @Override
    public int getDroppedAttributesCount() {
        return this.droppedAttributesCount;
    }
    
    @Override
    public String toString() {
        return "Attributes{attributeMap=" + this.attributeMap + ", droppedAttributesCount=" + this.droppedAttributesCount + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SpanData.Attributes) {
            final SpanData.Attributes that = (SpanData.Attributes)o;
            return this.attributeMap.equals(that.getAttributeMap()) && this.droppedAttributesCount == that.getDroppedAttributesCount();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.attributeMap.hashCode();
        h *= 1000003;
        h ^= this.droppedAttributesCount;
        return h;
    }
}
