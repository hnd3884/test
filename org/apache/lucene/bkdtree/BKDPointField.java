package org.apache.lucene.bkdtree;

import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;

@Deprecated
public final class BKDPointField extends Field
{
    public static final FieldType TYPE;
    
    public BKDPointField(final String name, final double lat, final double lon) {
        super(name, BKDPointField.TYPE);
        if (!BKDTreeWriter.validLat(lat)) {
            throw new IllegalArgumentException("invalid lat (" + lat + "): must be -90 to 90");
        }
        if (!BKDTreeWriter.validLon(lon)) {
            throw new IllegalArgumentException("invalid lon (" + lon + "): must be -180 to 180");
        }
        this.fieldsData = ((long)BKDTreeWriter.encodeLat(lat) << 32 | ((long)BKDTreeWriter.encodeLon(lon) & 0xFFFFFFFFL));
    }
    
    static {
        (TYPE = new FieldType()).setDocValuesType(DocValuesType.SORTED_NUMERIC);
        BKDPointField.TYPE.freeze();
    }
}
