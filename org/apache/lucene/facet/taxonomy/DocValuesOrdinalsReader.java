package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;

public class DocValuesOrdinalsReader extends OrdinalsReader
{
    private final String field;
    
    public DocValuesOrdinalsReader() {
        this("$facets");
    }
    
    public DocValuesOrdinalsReader(final String field) {
        this.field = field;
    }
    
    @Override
    public OrdinalsSegmentReader getReader(final LeafReaderContext context) throws IOException {
        BinaryDocValues values0 = context.reader().getBinaryDocValues(this.field);
        if (values0 == null) {
            values0 = DocValues.emptyBinary();
        }
        final BinaryDocValues values2 = values0;
        return new OrdinalsSegmentReader() {
            @Override
            public void get(final int docID, final IntsRef ordinals) throws IOException {
                final BytesRef bytes = values2.get(docID);
                DocValuesOrdinalsReader.this.decode(bytes, ordinals);
            }
        };
    }
    
    @Override
    public String getIndexFieldName() {
        return this.field;
    }
    
    protected void decode(final BytesRef buf, final IntsRef ordinals) {
        if (ordinals.ints.length < buf.length) {
            ordinals.ints = ArrayUtil.grow(ordinals.ints, buf.length);
        }
        ordinals.offset = 0;
        ordinals.length = 0;
        final int upto = buf.offset + buf.length;
        int value = 0;
        int offset = buf.offset;
        int prev = 0;
        while (offset < upto) {
            final byte b = buf.bytes[offset++];
            if (b >= 0) {
                ordinals.ints[ordinals.length] = (value << 7 | b) + prev;
                value = 0;
                prev = ordinals.ints[ordinals.length];
                ++ordinals.length;
            }
            else {
                value = (value << 7 | (b & 0x7F));
            }
        }
    }
}
