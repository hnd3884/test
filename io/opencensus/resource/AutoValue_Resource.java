package io.opencensus.resource;

import javax.annotation.Nullable;
import java.util.Map;

final class AutoValue_Resource extends Resource
{
    private final String type;
    private final Map<String, String> labels;
    
    AutoValue_Resource(@Nullable final String type, final Map<String, String> labels) {
        this.type = type;
        if (labels == null) {
            throw new NullPointerException("Null labels");
        }
        this.labels = labels;
    }
    
    @Nullable
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public Map<String, String> getLabels() {
        return this.labels;
    }
    
    @Override
    public String toString() {
        return "Resource{type=" + this.type + ", labels=" + this.labels + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Resource) {
            final Resource that = (Resource)o;
            if (this.type == null) {
                if (that.getType() != null) {
                    return false;
                }
            }
            else if (!this.type.equals(that.getType())) {
                return false;
            }
            if (this.labels.equals(that.getLabels())) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= ((this.type == null) ? 0 : this.type.hashCode());
        h *= 1000003;
        h ^= this.labels.hashCode();
        return h;
    }
}
