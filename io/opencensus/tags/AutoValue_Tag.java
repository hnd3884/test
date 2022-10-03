package io.opencensus.tags;

final class AutoValue_Tag extends Tag
{
    private final TagKey key;
    private final TagValue value;
    private final TagMetadata tagMetadata;
    
    AutoValue_Tag(final TagKey key, final TagValue value, final TagMetadata tagMetadata) {
        if (key == null) {
            throw new NullPointerException("Null key");
        }
        this.key = key;
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
        if (tagMetadata == null) {
            throw new NullPointerException("Null tagMetadata");
        }
        this.tagMetadata = tagMetadata;
    }
    
    @Override
    public TagKey getKey() {
        return this.key;
    }
    
    @Override
    public TagValue getValue() {
        return this.value;
    }
    
    @Override
    public TagMetadata getTagMetadata() {
        return this.tagMetadata;
    }
    
    @Override
    public String toString() {
        return "Tag{key=" + this.key + ", value=" + this.value + ", tagMetadata=" + this.tagMetadata + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Tag) {
            final Tag that = (Tag)o;
            return this.key.equals(that.getKey()) && this.value.equals(that.getValue()) && this.tagMetadata.equals(that.getTagMetadata());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.key.hashCode();
        h *= 1000003;
        h ^= this.value.hashCode();
        h *= 1000003;
        h ^= this.tagMetadata.hashCode();
        return h;
    }
}
