package io.opencensus.metrics;

final class AutoValue_LabelKey extends LabelKey
{
    private final String key;
    private final String description;
    
    AutoValue_LabelKey(final String key, final String description) {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        this.key = key;
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
    }
    
    @Override
    public String getKey() {
        return this.key;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public String toString() {
        return "LabelKey{key=" + this.key + ", description=" + this.description + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LabelKey) {
            final LabelKey that = (LabelKey)o;
            return this.key.equals(that.getKey()) && this.description.equals(that.getDescription());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.key.hashCode();
        h *= 1000003;
        h ^= this.description.hashCode();
        return h;
    }
}
