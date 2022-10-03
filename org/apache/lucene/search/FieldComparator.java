package org.apache.lucene.search;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public abstract class FieldComparator<T>
{
    public abstract int compare(final int p0, final int p1);
    
    public abstract void setTopValue(final T p0);
    
    public abstract T value(final int p0);
    
    public abstract LeafFieldComparator getLeafComparator(final LeafReaderContext p0) throws IOException;
    
    public int compareValues(final T first, final T second) {
        if (first == null) {
            if (second == null) {
                return 0;
            }
            return -1;
        }
        else {
            if (second == null) {
                return 1;
            }
            return ((Comparable)first).compareTo(second);
        }
    }
    
    public abstract static class NumericComparator<T extends Number> extends SimpleFieldComparator<T>
    {
        protected final T missingValue;
        protected final String field;
        protected Bits docsWithField;
        protected NumericDocValues currentReaderValues;
        
        public NumericComparator(final String field, final T missingValue) {
            this.field = field;
            this.missingValue = missingValue;
        }
        
        @Override
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.currentReaderValues = this.getNumericDocValues(context, this.field);
            if (this.missingValue != null) {
                this.docsWithField = this.getDocsWithValue(context, this.field);
                if (this.docsWithField instanceof Bits.MatchAllBits) {
                    this.docsWithField = null;
                }
            }
            else {
                this.docsWithField = null;
            }
        }
        
        protected NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
            return DocValues.getNumeric(context.reader(), field);
        }
        
        protected Bits getDocsWithValue(final LeafReaderContext context, final String field) throws IOException {
            return DocValues.getDocsWithField(context.reader(), field);
        }
    }
    
    public static class DoubleComparator extends NumericComparator<Double>
    {
        private final double[] values;
        private double bottom;
        private double topValue;
        
        public DoubleComparator(final int numHits, final String field, final Double missingValue) {
            super(field, missingValue);
            this.values = new double[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return Double.compare(this.values[slot1], this.values[slot2]);
        }
        
        @Override
        public int compareBottom(final int doc) {
            double v2 = Double.longBitsToDouble(this.currentReaderValues.get(doc));
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (double)this.missingValue;
            }
            return Double.compare(this.bottom, v2);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            double v2 = Double.longBitsToDouble(this.currentReaderValues.get(doc));
            if (this.docsWithField != null && v2 == 0.0 && !this.docsWithField.get(doc)) {
                v2 = (double)this.missingValue;
            }
            this.values[slot] = v2;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        @Override
        public void setTopValue(final Double value) {
            this.topValue = value;
        }
        
        @Override
        public Double value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            double docValue = Double.longBitsToDouble(this.currentReaderValues.get(doc));
            if (this.docsWithField != null && docValue == 0.0 && !this.docsWithField.get(doc)) {
                docValue = (double)this.missingValue;
            }
            return Double.compare(this.topValue, docValue);
        }
    }
    
    public static class FloatComparator extends NumericComparator<Float>
    {
        private final float[] values;
        private float bottom;
        private float topValue;
        
        public FloatComparator(final int numHits, final String field, final Float missingValue) {
            super(field, missingValue);
            this.values = new float[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return Float.compare(this.values[slot1], this.values[slot2]);
        }
        
        @Override
        public int compareBottom(final int doc) {
            float v2 = Float.intBitsToFloat((int)this.currentReaderValues.get(doc));
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = (float)this.missingValue;
            }
            return Float.compare(this.bottom, v2);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            float v2 = Float.intBitsToFloat((int)this.currentReaderValues.get(doc));
            if (this.docsWithField != null && v2 == 0.0f && !this.docsWithField.get(doc)) {
                v2 = (float)this.missingValue;
            }
            this.values[slot] = v2;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        @Override
        public void setTopValue(final Float value) {
            this.topValue = value;
        }
        
        @Override
        public Float value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            float docValue = Float.intBitsToFloat((int)this.currentReaderValues.get(doc));
            if (this.docsWithField != null && docValue == 0.0f && !this.docsWithField.get(doc)) {
                docValue = (float)this.missingValue;
            }
            return Float.compare(this.topValue, docValue);
        }
    }
    
    public static class IntComparator extends NumericComparator<Integer>
    {
        private final int[] values;
        private int bottom;
        private int topValue;
        
        public IntComparator(final int numHits, final String field, final Integer missingValue) {
            super(field, missingValue);
            this.values = new int[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return Integer.compare(this.values[slot1], this.values[slot2]);
        }
        
        @Override
        public int compareBottom(final int doc) {
            int v2 = (int)this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (int)this.missingValue;
            }
            return Integer.compare(this.bottom, v2);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            int v2 = (int)this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0 && !this.docsWithField.get(doc)) {
                v2 = (int)this.missingValue;
            }
            this.values[slot] = v2;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        @Override
        public void setTopValue(final Integer value) {
            this.topValue = value;
        }
        
        @Override
        public Integer value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            int docValue = (int)this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0 && !this.docsWithField.get(doc)) {
                docValue = (int)this.missingValue;
            }
            return Integer.compare(this.topValue, docValue);
        }
    }
    
    public static class LongComparator extends NumericComparator<Long>
    {
        private final long[] values;
        private long bottom;
        private long topValue;
        
        public LongComparator(final int numHits, final String field, final Long missingValue) {
            super(field, missingValue);
            this.values = new long[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return Long.compare(this.values[slot1], this.values[slot2]);
        }
        
        @Override
        public int compareBottom(final int doc) {
            long v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0L && !this.docsWithField.get(doc)) {
                v2 = (long)this.missingValue;
            }
            return Long.compare(this.bottom, v2);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            long v2 = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && v2 == 0L && !this.docsWithField.get(doc)) {
                v2 = (long)this.missingValue;
            }
            this.values[slot] = v2;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        @Override
        public void setTopValue(final Long value) {
            this.topValue = value;
        }
        
        @Override
        public Long value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            long docValue = this.currentReaderValues.get(doc);
            if (this.docsWithField != null && docValue == 0L && !this.docsWithField.get(doc)) {
                docValue = (long)this.missingValue;
            }
            return Long.compare(this.topValue, docValue);
        }
    }
    
    public static final class RelevanceComparator extends FieldComparator<Float> implements LeafFieldComparator
    {
        private final float[] scores;
        private float bottom;
        private Scorer scorer;
        private float topValue;
        
        public RelevanceComparator(final int numHits) {
            this.scores = new float[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return Float.compare(this.scores[slot2], this.scores[slot1]);
        }
        
        @Override
        public int compareBottom(final int doc) throws IOException {
            final float score = this.scorer.score();
            assert !Float.isNaN(score);
            return Float.compare(score, this.bottom);
        }
        
        @Override
        public void copy(final int slot, final int doc) throws IOException {
            this.scores[slot] = this.scorer.score();
            assert !Float.isNaN(this.scores[slot]);
        }
        
        @Override
        public LeafFieldComparator getLeafComparator(final LeafReaderContext context) {
            return this;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.scores[bottom];
        }
        
        @Override
        public void setTopValue(final Float value) {
            this.topValue = value;
        }
        
        @Override
        public void setScorer(final Scorer scorer) {
            if (!(scorer instanceof ScoreCachingWrappingScorer)) {
                this.scorer = new ScoreCachingWrappingScorer(scorer);
            }
            else {
                this.scorer = scorer;
            }
        }
        
        @Override
        public Float value(final int slot) {
            return this.scores[slot];
        }
        
        @Override
        public int compareValues(final Float first, final Float second) {
            return second.compareTo(first);
        }
        
        @Override
        public int compareTop(final int doc) throws IOException {
            final float docValue = this.scorer.score();
            assert !Float.isNaN(docValue);
            return Float.compare(docValue, this.topValue);
        }
    }
    
    public static final class DocComparator extends FieldComparator<Integer> implements LeafFieldComparator
    {
        private final int[] docIDs;
        private int docBase;
        private int bottom;
        private int topValue;
        
        public DocComparator(final int numHits) {
            this.docIDs = new int[numHits];
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            return this.docIDs[slot1] - this.docIDs[slot2];
        }
        
        @Override
        public int compareBottom(final int doc) {
            return this.bottom - (this.docBase + doc);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            this.docIDs[slot] = this.docBase + doc;
        }
        
        @Override
        public LeafFieldComparator getLeafComparator(final LeafReaderContext context) {
            this.docBase = context.docBase;
            return this;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.docIDs[bottom];
        }
        
        @Override
        public void setTopValue(final Integer value) {
            this.topValue = value;
        }
        
        @Override
        public Integer value(final int slot) {
            return this.docIDs[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            final int docValue = this.docBase + doc;
            return Integer.compare(this.topValue, docValue);
        }
        
        @Override
        public void setScorer(final Scorer scorer) {
        }
    }
    
    public static class TermOrdValComparator extends FieldComparator<BytesRef> implements LeafFieldComparator
    {
        final int[] ords;
        final BytesRef[] values;
        private final BytesRefBuilder[] tempBRs;
        final int[] readerGen;
        int currentReaderGen;
        SortedDocValues termsIndex;
        private final String field;
        int bottomSlot;
        int bottomOrd;
        boolean bottomSameReader;
        BytesRef bottomValue;
        BytesRef topValue;
        boolean topSameReader;
        int topOrd;
        final int missingSortCmp;
        final int missingOrd;
        
        public TermOrdValComparator(final int numHits, final String field) {
            this(numHits, field, false);
        }
        
        public TermOrdValComparator(final int numHits, final String field, final boolean sortMissingLast) {
            this.currentReaderGen = -1;
            this.bottomSlot = -1;
            this.ords = new int[numHits];
            this.values = new BytesRef[numHits];
            this.tempBRs = new BytesRefBuilder[numHits];
            this.readerGen = new int[numHits];
            this.field = field;
            if (sortMissingLast) {
                this.missingSortCmp = 1;
                this.missingOrd = Integer.MAX_VALUE;
            }
            else {
                this.missingSortCmp = -1;
                this.missingOrd = -1;
            }
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            if (this.readerGen[slot1] == this.readerGen[slot2]) {
                return this.ords[slot1] - this.ords[slot2];
            }
            final BytesRef val1 = this.values[slot1];
            final BytesRef val2 = this.values[slot2];
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return this.missingSortCmp;
            }
            else {
                if (val2 == null) {
                    return -this.missingSortCmp;
                }
                return val1.compareTo(val2);
            }
        }
        
        @Override
        public int compareBottom(final int doc) {
            assert this.bottomSlot != -1;
            int docOrd = this.termsIndex.getOrd(doc);
            if (docOrd == -1) {
                docOrd = this.missingOrd;
            }
            if (this.bottomSameReader) {
                return this.bottomOrd - docOrd;
            }
            if (this.bottomOrd >= docOrd) {
                return 1;
            }
            return -1;
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            int ord = this.termsIndex.getOrd(doc);
            if (ord == -1) {
                ord = this.missingOrd;
                this.values[slot] = null;
            }
            else {
                assert ord >= 0;
                if (this.tempBRs[slot] == null) {
                    this.tempBRs[slot] = new BytesRefBuilder();
                }
                this.tempBRs[slot].copyBytes(this.termsIndex.lookupOrd(ord));
                this.values[slot] = this.tempBRs[slot].get();
            }
            this.ords[slot] = ord;
            this.readerGen[slot] = this.currentReaderGen;
        }
        
        protected SortedDocValues getSortedDocValues(final LeafReaderContext context, final String field) throws IOException {
            return DocValues.getSorted(context.reader(), field);
        }
        
        @Override
        public LeafFieldComparator getLeafComparator(final LeafReaderContext context) throws IOException {
            this.termsIndex = this.getSortedDocValues(context, this.field);
            ++this.currentReaderGen;
            if (this.topValue != null) {
                final int ord = this.termsIndex.lookupTerm(this.topValue);
                if (ord >= 0) {
                    this.topSameReader = true;
                    this.topOrd = ord;
                }
                else {
                    this.topSameReader = false;
                    this.topOrd = -ord - 2;
                }
            }
            else {
                this.topOrd = this.missingOrd;
                this.topSameReader = true;
            }
            if (this.bottomSlot != -1) {
                this.setBottom(this.bottomSlot);
            }
            return this;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottomSlot = bottom;
            this.bottomValue = this.values[this.bottomSlot];
            if (this.currentReaderGen == this.readerGen[this.bottomSlot]) {
                this.bottomOrd = this.ords[this.bottomSlot];
                this.bottomSameReader = true;
            }
            else if (this.bottomValue == null) {
                assert this.ords[this.bottomSlot] == this.missingOrd;
                this.bottomOrd = this.missingOrd;
                this.bottomSameReader = true;
                this.readerGen[this.bottomSlot] = this.currentReaderGen;
            }
            else {
                final int ord = this.termsIndex.lookupTerm(this.bottomValue);
                if (ord < 0) {
                    this.bottomOrd = -ord - 2;
                    this.bottomSameReader = false;
                }
                else {
                    this.bottomOrd = ord;
                    this.bottomSameReader = true;
                    this.readerGen[this.bottomSlot] = this.currentReaderGen;
                    this.ords[this.bottomSlot] = this.bottomOrd;
                }
            }
        }
        
        @Override
        public void setTopValue(final BytesRef value) {
            this.topValue = value;
        }
        
        @Override
        public BytesRef value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareTop(final int doc) {
            int ord = this.termsIndex.getOrd(doc);
            if (ord == -1) {
                ord = this.missingOrd;
            }
            if (this.topSameReader) {
                return this.topOrd - ord;
            }
            if (ord <= this.topOrd) {
                return 1;
            }
            return -1;
        }
        
        @Override
        public int compareValues(final BytesRef val1, final BytesRef val2) {
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return this.missingSortCmp;
            }
            else {
                if (val2 == null) {
                    return -this.missingSortCmp;
                }
                return val1.compareTo(val2);
            }
        }
        
        @Override
        public void setScorer(final Scorer scorer) {
        }
    }
    
    public static class TermValComparator extends FieldComparator<BytesRef> implements LeafFieldComparator
    {
        private final BytesRef[] values;
        private final BytesRefBuilder[] tempBRs;
        private BinaryDocValues docTerms;
        private Bits docsWithField;
        private final String field;
        private BytesRef bottom;
        private BytesRef topValue;
        private final int missingSortCmp;
        
        public TermValComparator(final int numHits, final String field, final boolean sortMissingLast) {
            this.values = new BytesRef[numHits];
            this.tempBRs = new BytesRefBuilder[numHits];
            this.field = field;
            this.missingSortCmp = (sortMissingLast ? 1 : -1);
        }
        
        @Override
        public int compare(final int slot1, final int slot2) {
            final BytesRef val1 = this.values[slot1];
            final BytesRef val2 = this.values[slot2];
            return this.compareValues(val1, val2);
        }
        
        @Override
        public int compareBottom(final int doc) {
            final BytesRef comparableBytes = this.getComparableBytes(doc, this.docTerms.get(doc));
            return this.compareValues(this.bottom, comparableBytes);
        }
        
        @Override
        public void copy(final int slot, final int doc) {
            final BytesRef comparableBytes = this.getComparableBytes(doc, this.docTerms.get(doc));
            if (comparableBytes == null) {
                this.values[slot] = null;
            }
            else {
                if (this.tempBRs[slot] == null) {
                    this.tempBRs[slot] = new BytesRefBuilder();
                }
                this.tempBRs[slot].copyBytes(comparableBytes);
                this.values[slot] = this.tempBRs[slot].get();
            }
        }
        
        protected BinaryDocValues getBinaryDocValues(final LeafReaderContext context, final String field) throws IOException {
            return DocValues.getBinary(context.reader(), field);
        }
        
        protected Bits getDocsWithField(final LeafReaderContext context, final String field) throws IOException {
            return DocValues.getDocsWithField(context.reader(), field);
        }
        
        protected boolean isNull(final int doc, final BytesRef term) {
            return this.docsWithField != null && !this.docsWithField.get(doc);
        }
        
        @Override
        public LeafFieldComparator getLeafComparator(final LeafReaderContext context) throws IOException {
            this.docTerms = this.getBinaryDocValues(context, this.field);
            this.docsWithField = this.getDocsWithField(context, this.field);
            if (this.docsWithField instanceof Bits.MatchAllBits) {
                this.docsWithField = null;
            }
            return this;
        }
        
        @Override
        public void setBottom(final int bottom) {
            this.bottom = this.values[bottom];
        }
        
        @Override
        public void setTopValue(final BytesRef value) {
            this.topValue = value;
        }
        
        @Override
        public BytesRef value(final int slot) {
            return this.values[slot];
        }
        
        @Override
        public int compareValues(final BytesRef val1, final BytesRef val2) {
            if (val1 == null) {
                if (val2 == null) {
                    return 0;
                }
                return this.missingSortCmp;
            }
            else {
                if (val2 == null) {
                    return -this.missingSortCmp;
                }
                return val1.compareTo(val2);
            }
        }
        
        @Override
        public int compareTop(final int doc) {
            final BytesRef comparableBytes = this.getComparableBytes(doc, this.docTerms.get(doc));
            return this.compareValues(this.topValue, comparableBytes);
        }
        
        private BytesRef getComparableBytes(final int doc, final BytesRef term) {
            if (term.length == 0 && this.isNull(doc, term)) {
                return null;
            }
            return term;
        }
        
        @Override
        public void setScorer(final Scorer scorer) {
        }
    }
}
