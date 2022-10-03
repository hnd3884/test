package io.opencensus.trace;

import java.util.Map;

final class AutoValue_Annotation extends Annotation
{
    private final String description;
    private final Map<String, AttributeValue> attributes;
    
    AutoValue_Annotation(final String description, final Map<String, AttributeValue> attributes) {
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
        if (attributes == null) {
            throw new NullPointerException("Null attributes");
        }
        this.attributes = attributes;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public Map<String, AttributeValue> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String toString() {
        return "Annotation{description=" + this.description + ", attributes=" + this.attributes + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Annotation) {
            final Annotation that = (Annotation)o;
            return this.description.equals(that.getDescription()) && this.attributes.equals(that.getAttributes());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.description.hashCode();
        h *= 1000003;
        h ^= this.attributes.hashCode();
        return h;
    }
}
