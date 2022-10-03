package io.opencensus.tags;

final class AutoValue_TagKey extends TagKey
{
    private final String name;
    
    AutoValue_TagKey(final String name) {
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "TagKey{name=" + this.name + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TagKey) {
            final TagKey that = (TagKey)o;
            return this.name.equals(that.getName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.name.hashCode();
        return h;
    }
}
