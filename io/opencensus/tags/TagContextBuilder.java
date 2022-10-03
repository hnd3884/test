package io.opencensus.tags;

import io.opencensus.common.Scope;

public abstract class TagContextBuilder
{
    private static final TagMetadata METADATA_NO_PROPAGATION;
    private static final TagMetadata METADATA_UNLIMITED_PROPAGATION;
    
    @Deprecated
    public abstract TagContextBuilder put(final TagKey p0, final TagValue p1);
    
    public TagContextBuilder put(final TagKey key, final TagValue value, final TagMetadata tagMetadata) {
        final TagContextBuilder builder = this.put(key, value);
        return builder;
    }
    
    public final TagContextBuilder putLocal(final TagKey key, final TagValue value) {
        return this.put(key, value, TagContextBuilder.METADATA_NO_PROPAGATION);
    }
    
    public final TagContextBuilder putPropagating(final TagKey key, final TagValue value) {
        return this.put(key, value, TagContextBuilder.METADATA_UNLIMITED_PROPAGATION);
    }
    
    public abstract TagContextBuilder remove(final TagKey p0);
    
    public abstract TagContext build();
    
    public abstract Scope buildScoped();
    
    static {
        METADATA_NO_PROPAGATION = TagMetadata.create(TagMetadata.TagTtl.NO_PROPAGATION);
        METADATA_UNLIMITED_PROPAGATION = TagMetadata.create(TagMetadata.TagTtl.UNLIMITED_PROPAGATION);
    }
}
