package io.opencensus.tags;

final class AutoValue_TagMetadata extends TagMetadata
{
    private final TagTtl tagTtl;
    
    AutoValue_TagMetadata(final TagTtl tagTtl) {
        if (tagTtl == null) {
            throw new NullPointerException("Null tagTtl");
        }
        this.tagTtl = tagTtl;
    }
    
    @Override
    public TagTtl getTagTtl() {
        return this.tagTtl;
    }
    
    @Override
    public String toString() {
        return "TagMetadata{tagTtl=" + this.tagTtl + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TagMetadata) {
            final TagMetadata that = (TagMetadata)o;
            return this.tagTtl.equals(that.getTagTtl());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.tagTtl.hashCode();
        return h;
    }
}
