package org.apache.lucene.facet.taxonomy;

import java.util.Arrays;
import org.apache.lucene.util.BytesRef;

public class FloatAssociationFacetField extends AssociationFacetField
{
    public FloatAssociationFacetField(final float assoc, final String dim, final String... path) {
        super(floatToBytesRef(assoc), dim, path);
    }
    
    public static BytesRef floatToBytesRef(final float v) {
        return IntAssociationFacetField.intToBytesRef(Float.floatToIntBits(v));
    }
    
    public static float bytesRefToFloat(final BytesRef b) {
        return Float.intBitsToFloat(IntAssociationFacetField.bytesRefToInt(b));
    }
    
    @Override
    public String toString() {
        return "FloatAssociationFacetField(dim=" + this.dim + " path=" + Arrays.toString(this.path) + " value=" + bytesRefToFloat(this.assoc) + ")";
    }
}
