package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;

public class JoinDocFreqValueSource extends FieldCacheSource
{
    public static final String NAME = "joindf";
    protected final String qfield;
    
    public JoinDocFreqValueSource(final String field, final String qfield) {
        super(field);
        this.qfield = qfield;
    }
    
    @Override
    public String description() {
        return "joindf(" + this.field + ":(" + this.qfield + "))";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final BinaryDocValues terms = DocValues.getBinary(readerContext.reader(), this.field);
        final IndexReader top = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader();
        final Terms t = MultiFields.getTerms(top, this.qfield);
        final TermsEnum termsEnum = (t == null) ? TermsEnum.EMPTY : t.iterator();
        return new IntDocValues(this) {
            @Override
            public int intVal(final int doc) {
                try {
                    final BytesRef term = terms.get(doc);
                    if (termsEnum.seekExact(term)) {
                        return termsEnum.docFreq();
                    }
                    return 0;
                }
                catch (final IOException e) {
                    throw new RuntimeException("caught exception in function " + JoinDocFreqValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o.getClass() != JoinDocFreqValueSource.class) {
            return false;
        }
        final JoinDocFreqValueSource other = (JoinDocFreqValueSource)o;
        return this.qfield.equals(other.qfield) && super.equals(other);
    }
    
    @Override
    public int hashCode() {
        return this.qfield.hashCode() + super.hashCode();
    }
}
