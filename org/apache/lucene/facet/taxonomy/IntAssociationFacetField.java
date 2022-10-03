package org.apache.lucene.facet.taxonomy;

import java.util.Arrays;
import org.apache.lucene.util.BytesRef;

public class IntAssociationFacetField extends AssociationFacetField
{
    public IntAssociationFacetField(final int assoc, final String dim, final String... path) {
        super(intToBytesRef(assoc), dim, path);
    }
    
    public static BytesRef intToBytesRef(final int v) {
        final byte[] bytes = { (byte)(v >> 24), (byte)(v >> 16), (byte)(v >> 8), (byte)v };
        return new BytesRef(bytes);
    }
    
    public static int bytesRefToInt(final BytesRef b) {
        return (b.bytes[b.offset] & 0xFF) << 24 | (b.bytes[b.offset + 1] & 0xFF) << 16 | (b.bytes[b.offset + 2] & 0xFF) << 8 | (b.bytes[b.offset + 3] & 0xFF);
    }
    
    @Override
    public String toString() {
        return "IntAssociationFacetField(dim=" + this.dim + " path=" + Arrays.toString(this.path) + " value=" + bytesRefToInt(this.assoc) + ")";
    }
}
