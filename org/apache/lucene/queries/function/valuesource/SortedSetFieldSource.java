package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DocTermsIndexDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.search.SortedSetSortField;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedSetSelector;

public class SortedSetFieldSource extends FieldCacheSource
{
    protected final SortedSetSelector.Type selector;
    
    public SortedSetFieldSource(final String field) {
        this(field, SortedSetSelector.Type.MIN);
    }
    
    public SortedSetFieldSource(final String field, final SortedSetSelector.Type selector) {
        super(field);
        this.selector = selector;
    }
    
    @Override
    public SortField getSortField(final boolean reverse) {
        return (SortField)new SortedSetSortField(this.field, reverse, this.selector);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final SortedSetDocValues sortedSet = DocValues.getSortedSet(readerContext.reader(), this.field);
        final SortedDocValues view = SortedSetSelector.wrap(sortedSet, this.selector);
        return new DocTermsIndexDocValues(this, view) {
            @Override
            protected String toTerm(final String readableValue) {
                return readableValue;
            }
            
            @Override
            public Object objectVal(final int doc) {
                return this.strVal(doc);
            }
        };
    }
    
    @Override
    public String description() {
        return "sortedset(" + this.field + ",selector=" + this.selector + ')';
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.selector == null) ? 0 : this.selector.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SortedSetFieldSource other = (SortedSetFieldSource)obj;
        return this.selector == other.selector;
    }
}
