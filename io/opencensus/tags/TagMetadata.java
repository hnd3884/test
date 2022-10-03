package io.opencensus.tags;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TagMetadata
{
    TagMetadata() {
    }
    
    public static TagMetadata create(final TagTtl tagTtl) {
        return new AutoValue_TagMetadata(tagTtl);
    }
    
    public abstract TagTtl getTagTtl();
    
    public enum TagTtl
    {
        NO_PROPAGATION(0), 
        UNLIMITED_PROPAGATION(-1);
        
        private final int hops;
        
        private TagTtl(final int hops) {
            this.hops = hops;
        }
    }
}
