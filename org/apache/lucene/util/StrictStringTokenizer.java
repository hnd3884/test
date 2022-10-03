package org.apache.lucene.util;

final class StrictStringTokenizer
{
    private final String s;
    private final char delimiter;
    private int pos;
    
    public StrictStringTokenizer(final String s, final char delimiter) {
        this.s = s;
        this.delimiter = delimiter;
    }
    
    public final String nextToken() {
        if (this.pos < 0) {
            throw new IllegalStateException("no more tokens");
        }
        final int pos1 = this.s.indexOf(this.delimiter, this.pos);
        String s1;
        if (pos1 >= 0) {
            s1 = this.s.substring(this.pos, pos1);
            this.pos = pos1 + 1;
        }
        else {
            s1 = this.s.substring(this.pos);
            this.pos = -1;
        }
        return s1;
    }
    
    public final boolean hasMoreTokens() {
        return this.pos >= 0;
    }
}
