package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.mutable.MutableValueStr;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class DocTermsIndexDocValues extends FunctionValues
{
    protected final SortedDocValues termsIndex;
    protected final ValueSource vs;
    protected final MutableValueStr val;
    protected final CharsRefBuilder spareChars;
    
    public DocTermsIndexDocValues(final ValueSource vs, final LeafReaderContext context, final String field) throws IOException {
        this(vs, open(context, field));
    }
    
    protected DocTermsIndexDocValues(final ValueSource vs, final SortedDocValues termsIndex) {
        this.val = new MutableValueStr();
        this.spareChars = new CharsRefBuilder();
        this.vs = vs;
        this.termsIndex = termsIndex;
    }
    
    protected abstract String toTerm(final String p0);
    
    @Override
    public boolean exists(final int doc) {
        return this.ordVal(doc) >= 0;
    }
    
    @Override
    public int ordVal(final int doc) {
        return this.termsIndex.getOrd(doc);
    }
    
    @Override
    public int numOrd() {
        return this.termsIndex.getValueCount();
    }
    
    @Override
    public boolean bytesVal(final int doc, final BytesRefBuilder target) {
        target.clear();
        target.copyBytes(this.termsIndex.get(doc));
        return target.length() > 0;
    }
    
    @Override
    public String strVal(final int doc) {
        final BytesRef term = this.termsIndex.get(doc);
        if (term.length == 0) {
            return null;
        }
        this.spareChars.copyUTF8Bytes(term);
        return this.spareChars.toString();
    }
    
    @Override
    public boolean boolVal(final int doc) {
        return this.exists(doc);
    }
    
    @Override
    public abstract Object objectVal(final int p0);
    
    @Override
    public ValueSourceScorer getRangeScorer(final IndexReader reader, String lowerVal, String upperVal, final boolean includeLower, final boolean includeUpper) {
        lowerVal = ((lowerVal == null) ? null : this.toTerm(lowerVal));
        upperVal = ((upperVal == null) ? null : this.toTerm(upperVal));
        int lower = Integer.MIN_VALUE;
        if (lowerVal != null) {
            lower = this.termsIndex.lookupTerm(new BytesRef((CharSequence)lowerVal));
            if (lower < 0) {
                lower = -lower - 1;
            }
            else if (!includeLower) {
                ++lower;
            }
        }
        int upper = Integer.MAX_VALUE;
        if (upperVal != null) {
            upper = this.termsIndex.lookupTerm(new BytesRef((CharSequence)upperVal));
            if (upper < 0) {
                upper = -upper - 2;
            }
            else if (!includeUpper) {
                --upper;
            }
        }
        final int ll = lower;
        final int uu = upper;
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                final int ord = DocTermsIndexDocValues.this.termsIndex.getOrd(doc);
                return ord >= ll && ord <= uu;
            }
        };
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueStr mval = new MutableValueStr();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                final int ord = DocTermsIndexDocValues.this.termsIndex.getOrd(doc);
                this.mval.value.clear();
                this.mval.exists = (ord >= 0);
                if (this.mval.exists) {
                    this.mval.value.copyBytes(DocTermsIndexDocValues.this.termsIndex.lookupOrd(ord));
                }
            }
        };
    }
    
    static SortedDocValues open(final LeafReaderContext context, final String field) throws IOException {
        try {
            return DocValues.getSorted(context.reader(), field);
        }
        catch (final RuntimeException e) {
            throw new DocTermsIndexException(field, e);
        }
    }
    
    public static final class DocTermsIndexException extends RuntimeException
    {
        public DocTermsIndexException(final String fieldName, final RuntimeException cause) {
            super("Can't initialize DocTermsIndex to generate (function) FunctionValues for field: " + fieldName, cause);
        }
    }
}
