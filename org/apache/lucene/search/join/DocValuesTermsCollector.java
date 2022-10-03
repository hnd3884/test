package org.apache.lucene.search.join;

import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.BinaryDocValues;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

abstract class DocValuesTermsCollector<DV> extends SimpleCollector
{
    protected DV docValues;
    private final Function<DV> docValuesCall;
    
    public DocValuesTermsCollector(final Function<DV> docValuesCall) {
        this.docValuesCall = docValuesCall;
    }
    
    protected final void doSetNextReader(final LeafReaderContext context) throws IOException {
        this.docValues = this.docValuesCall.apply(context.reader());
    }
    
    static Function<BinaryDocValues> binaryDocValues(final String field) {
        return new Function<BinaryDocValues>() {
            @Override
            public BinaryDocValues apply(final LeafReader ctx) throws IOException {
                return DocValues.getBinary(ctx, field);
            }
        };
    }
    
    static Function<SortedSetDocValues> sortedSetDocValues(final String field) {
        return new Function<SortedSetDocValues>() {
            @Override
            public SortedSetDocValues apply(final LeafReader ctx) throws IOException {
                return DocValues.getSortedSet(ctx, field);
            }
        };
    }
    
    static Function<BinaryDocValues> numericAsBinaryDocValues(final String field, final FieldType.NumericType numTyp) {
        return new Function<BinaryDocValues>() {
            @Override
            public BinaryDocValues apply(final LeafReader ctx) throws IOException {
                final NumericDocValues numeric = DocValues.getNumeric(ctx, field);
                final BytesRefBuilder bytes = new BytesRefBuilder();
                final LongConsumer coder = DocValuesTermsCollector.coder(bytes, numTyp, field);
                return new BinaryDocValues() {
                    public BytesRef get(final int docID) {
                        final long lVal = numeric.get(docID);
                        coder.accept(lVal);
                        return bytes.get();
                    }
                };
            }
        };
    }
    
    static LongConsumer coder(final BytesRefBuilder bytes, final FieldType.NumericType type, final String fieldName) {
        switch (type) {
            case INT: {
                return new LongConsumer() {
                    @Override
                    public void accept(final long value) {
                        NumericUtils.intToPrefixCoded((int)value, 0, bytes);
                    }
                };
            }
            case LONG: {
                return new LongConsumer() {
                    @Override
                    public void accept(final long value) {
                        NumericUtils.longToPrefixCoded((long)(int)value, 0, bytes);
                    }
                };
            }
            default: {
                throw new IllegalArgumentException("Unsupported " + type + ". Only " + FieldType.NumericType.INT + " and " + FieldType.NumericType.LONG + " are supported." + "Field " + fieldName);
            }
        }
    }
    
    static Function<SortedSetDocValues> sortedNumericAsSortedSetDocValues(final String field, final FieldType.NumericType numTyp) {
        return new Function<SortedSetDocValues>() {
            @Override
            public SortedSetDocValues apply(final LeafReader ctx) throws IOException {
                final SortedNumericDocValues numerics = DocValues.getSortedNumeric(ctx, field);
                final BytesRefBuilder bytes = new BytesRefBuilder();
                final LongConsumer coder = DocValuesTermsCollector.coder(bytes, numTyp, field);
                return new SortedSetDocValues() {
                    private int index = Integer.MIN_VALUE;
                    
                    public long nextOrd() {
                        return (this.index < numerics.count() - 1) ? (++this.index) : -1L;
                    }
                    
                    public void setDocument(final int docID) {
                        numerics.setDocument(docID);
                        this.index = -1;
                    }
                    
                    public BytesRef lookupOrd(final long ord) {
                        assert ord >= 0L && ord < numerics.count();
                        final long value = numerics.valueAt((int)ord);
                        coder.accept(value);
                        return bytes.get();
                    }
                    
                    public long getValueCount() {
                        throw new UnsupportedOperationException("it's just number encoding wrapper");
                    }
                    
                    public long lookupTerm(final BytesRef key) {
                        throw new UnsupportedOperationException("it's just number encoding wrapper");
                    }
                };
            }
        };
    }
    
    interface LongConsumer
    {
        void accept(final long p0);
    }
    
    interface Function<R>
    {
        R apply(final LeafReader p0) throws IOException;
    }
}
