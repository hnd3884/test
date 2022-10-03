package org.apache.lucene.search.vectorhighlight;

import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimpleBoundaryScanner implements BoundaryScanner
{
    public static final int DEFAULT_MAX_SCAN = 20;
    public static final Character[] DEFAULT_BOUNDARY_CHARS;
    protected int maxScan;
    protected Set<Character> boundaryChars;
    
    public SimpleBoundaryScanner() {
        this(20, SimpleBoundaryScanner.DEFAULT_BOUNDARY_CHARS);
    }
    
    public SimpleBoundaryScanner(final int maxScan) {
        this(maxScan, SimpleBoundaryScanner.DEFAULT_BOUNDARY_CHARS);
    }
    
    public SimpleBoundaryScanner(final Character[] boundaryChars) {
        this(20, boundaryChars);
    }
    
    public SimpleBoundaryScanner(final int maxScan, final Character[] boundaryChars) {
        this.maxScan = maxScan;
        (this.boundaryChars = new HashSet<Character>()).addAll(Arrays.asList(boundaryChars));
    }
    
    public SimpleBoundaryScanner(final int maxScan, final Set<Character> boundaryChars) {
        this.maxScan = maxScan;
        this.boundaryChars = boundaryChars;
    }
    
    @Override
    public int findStartOffset(final StringBuilder buffer, final int start) {
        if (start > buffer.length() || start < 1) {
            return start;
        }
        int count;
        int offset;
        for (count = this.maxScan, offset = start; offset > 0 && count > 0; --offset, --count) {
            if (this.boundaryChars.contains(buffer.charAt(offset - 1))) {
                return offset;
            }
        }
        if (offset == 0) {
            return 0;
        }
        return start;
    }
    
    @Override
    public int findEndOffset(final StringBuilder buffer, final int start) {
        if (start > buffer.length() || start < 0) {
            return start;
        }
        for (int count = this.maxScan, offset = start; offset < buffer.length() && count > 0; ++offset, --count) {
            if (this.boundaryChars.contains(buffer.charAt(offset))) {
                return offset;
            }
        }
        return start;
    }
    
    static {
        DEFAULT_BOUNDARY_CHARS = new Character[] { '.', ',', '!', '?', ' ', '\t', '\n' };
    }
}
