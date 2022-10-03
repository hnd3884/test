package org.apache.lucene.analysis.util;

import org.apache.lucene.util.SparseFixedBitSet;
import org.apache.lucene.util.Bits;

public final class UnicodeProps
{
    public static final String UNICODE_VERSION = "7.0.0.0";
    public static final Bits WHITESPACE;
    
    private UnicodeProps() {
    }
    
    private static Bits createBits(final int... codepoints) {
        final int len = codepoints[codepoints.length - 1] + 1;
        final SparseFixedBitSet bitset = new SparseFixedBitSet(len);
        for (final int i : codepoints) {
            bitset.set(i);
        }
        return (Bits)new Bits() {
            public boolean get(final int index) {
                return index < len && bitset.get(index);
            }
            
            public int length() {
                return 1114112;
            }
        };
    }
    
    static {
        WHITESPACE = createBits(9, 10, 11, 12, 13, 32, 133, 160, 5760, 8192, 8193, 8194, 8195, 8196, 8197, 8198, 8199, 8200, 8201, 8202, 8232, 8233, 8239, 8287, 12288);
    }
}
