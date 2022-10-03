package io.opencensus.tags;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Tag
{
    private static final TagMetadata METADATA_UNLIMITED_PROPAGATION;
    
    Tag() {
    }
    
    @Deprecated
    public static Tag create(final TagKey key, final TagValue value) {
        return create(key, value, Tag.METADATA_UNLIMITED_PROPAGATION);
    }
    
    public static Tag create(final TagKey key, final TagValue value, final TagMetadata tagMetadata) {
        return new AutoValue_Tag(key, value, tagMetadata);
    }
    
    public abstract TagKey getKey();
    
    public abstract TagValue getValue();
    
    public abstract TagMetadata getTagMetadata();
    
    static {
        METADATA_UNLIMITED_PROPAGATION = TagMetadata.create(TagMetadata.TagTtl.UNLIMITED_PROPAGATION);
    }
}
