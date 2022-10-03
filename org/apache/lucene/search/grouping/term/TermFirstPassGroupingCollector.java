package org.apache.lucene.search.grouping.term;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.grouping.AbstractFirstPassGroupingCollector;

public class TermFirstPassGroupingCollector extends AbstractFirstPassGroupingCollector<BytesRef>
{
    private SortedDocValues index;
    private String groupField;
    
    public TermFirstPassGroupingCollector(final String groupField, final Sort groupSort, final int topNGroups) throws IOException {
        super(groupSort, topNGroups);
        this.groupField = groupField;
    }
    
    @Override
    protected BytesRef getDocGroupValue(final int doc) {
        final int ord = this.index.getOrd(doc);
        if (ord == -1) {
            return null;
        }
        return this.index.lookupOrd(ord);
    }
    
    @Override
    protected BytesRef copyDocGroupValue(final BytesRef groupValue, final BytesRef reuse) {
        if (groupValue == null) {
            return null;
        }
        if (reuse != null) {
            reuse.bytes = ArrayUtil.grow(reuse.bytes, groupValue.length);
            reuse.offset = 0;
            reuse.length = groupValue.length;
            System.arraycopy(groupValue.bytes, groupValue.offset, reuse.bytes, 0, groupValue.length);
            return reuse;
        }
        return BytesRef.deepCopyOf(groupValue);
    }
    
    @Override
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        super.doSetNextReader(readerContext);
        this.index = DocValues.getSorted(readerContext.reader(), this.groupField);
    }
}
