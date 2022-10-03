package org.antlr.v4.runtime.tree.pattern;

class TextChunk extends Chunk
{
    private final String text;
    
    public TextChunk(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        this.text = text;
    }
    
    public final String getText() {
        return this.text;
    }
    
    @Override
    public String toString() {
        return "'" + this.text + "'";
    }
}
