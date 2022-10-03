package org.antlr.v4.runtime.tree.pattern;

class TagChunk extends Chunk
{
    private final String tag;
    private final String label;
    
    public TagChunk(final String tag) {
        this(null, tag);
    }
    
    public TagChunk(final String label, final String tag) {
        if (tag == null || tag.isEmpty()) {
            throw new IllegalArgumentException("tag cannot be null or empty");
        }
        this.label = label;
        this.tag = tag;
    }
    
    public final String getTag() {
        return this.tag;
    }
    
    public final String getLabel() {
        return this.label;
    }
    
    @Override
    public String toString() {
        if (this.label != null) {
            return this.label + ":" + this.tag;
        }
        return this.tag;
    }
}
