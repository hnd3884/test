package org.apache.lucene.queries;

import java.io.IOException;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.util.BytesRef;
import java.util.Iterator;
import org.apache.lucene.index.Term;
import java.util.List;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.search.Filter;

@Deprecated
public final class TermsFilter extends Filter implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private final int[] offsets;
    private final byte[] termsBytes;
    private final TermsAndField[] termsAndFields;
    private final int hashCode;
    private static final int PRIME = 31;
    
    public TermsFilter(final List<Term> terms) {
        this(new FieldAndTermEnum() {
            final Iterator<Term> iter = sort((List<Comparable>)terms).iterator();
            
            @Override
            public BytesRef next() {
                if (this.iter.hasNext()) {
                    final Term next = this.iter.next();
                    this.field = next.field();
                    return next.bytes();
                }
                return null;
            }
        }, terms.size());
    }
    
    public TermsFilter(final String field, final List<BytesRef> terms) {
        this(new FieldAndTermEnum(field) {
            final Iterator<BytesRef> iter = sort((List<Comparable>)terms).iterator();
            
            @Override
            public BytesRef next() {
                if (this.iter.hasNext()) {
                    return this.iter.next();
                }
                return null;
            }
        }, terms.size());
    }
    
    public TermsFilter(final String field, final BytesRef... terms) {
        this(field, Arrays.asList(terms));
    }
    
    public TermsFilter(final Term... terms) {
        this(Arrays.asList(terms));
    }
    
    private TermsFilter(final FieldAndTermEnum iter, final int length) {
        int hash = 9;
        byte[] serializedTerms = new byte[0];
        this.offsets = new int[length + 1];
        int lastEndOffset = 0;
        int index = 0;
        final ArrayList<TermsAndField> termsAndFields = new ArrayList<TermsAndField>();
        TermsAndField lastTermsAndField = null;
        BytesRef previousTerm = null;
        String previousField = null;
        BytesRef currentTerm;
        while ((currentTerm = iter.next()) != null) {
            final String currentField = iter.field();
            if (currentField == null) {
                throw new IllegalArgumentException("Field must not be null");
            }
            if (previousField != null) {
                if (previousField.equals(currentField)) {
                    if (previousTerm.bytesEquals(currentTerm)) {
                        continue;
                    }
                }
                else {
                    final int start = (lastTermsAndField == null) ? 0 : lastTermsAndField.end;
                    lastTermsAndField = new TermsAndField(start, index, previousField);
                    termsAndFields.add(lastTermsAndField);
                }
            }
            hash = 31 * hash + currentField.hashCode();
            hash = 31 * hash + currentTerm.hashCode();
            if (serializedTerms.length < lastEndOffset + currentTerm.length) {
                serializedTerms = ArrayUtil.grow(serializedTerms, lastEndOffset + currentTerm.length);
            }
            System.arraycopy(currentTerm.bytes, currentTerm.offset, serializedTerms, lastEndOffset, currentTerm.length);
            this.offsets[index] = lastEndOffset;
            lastEndOffset += currentTerm.length;
            ++index;
            previousTerm = currentTerm;
            previousField = currentField;
        }
        this.offsets[index] = lastEndOffset;
        final int start = (lastTermsAndField == null) ? 0 : lastTermsAndField.end;
        lastTermsAndField = new TermsAndField(start, index, previousField);
        termsAndFields.add(lastTermsAndField);
        this.termsBytes = ArrayUtil.shrink(serializedTerms, lastEndOffset);
        this.termsAndFields = termsAndFields.toArray(new TermsAndField[termsAndFields.size()]);
        this.hashCode = hash;
    }
    
    public long ramBytesUsed() {
        return TermsFilter.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf((Accountable[])this.termsAndFields) + RamUsageEstimator.sizeOf(this.termsBytes) + RamUsageEstimator.sizeOf(this.offsets);
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final LeafReader reader = context.reader();
        final BitDocIdSet.Builder builder = new BitDocIdSet.Builder(reader.maxDoc());
        final Fields fields = reader.fields();
        final BytesRef spare = new BytesRef(this.termsBytes);
        Terms terms = null;
        TermsEnum termsEnum = null;
        PostingsEnum docs = null;
        for (final TermsAndField termsAndField : this.termsAndFields) {
            if ((terms = fields.terms(termsAndField.field)) != null) {
                termsEnum = terms.iterator();
                for (int i = termsAndField.start; i < termsAndField.end; ++i) {
                    spare.offset = this.offsets[i];
                    spare.length = this.offsets[i + 1] - this.offsets[i];
                    if (termsEnum.seekExact(spare)) {
                        docs = termsEnum.postings(docs, 0);
                        builder.or((DocIdSetIterator)docs);
                    }
                }
            }
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)builder.build(), acceptDocs);
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final TermsFilter test = (TermsFilter)obj;
        if (test.hashCode == this.hashCode && Arrays.equals(this.termsAndFields, test.termsAndFields)) {
            final int lastOffset = this.termsAndFields[this.termsAndFields.length - 1].end;
            if (ArrayUtil.equals(this.offsets, 0, test.offsets, 0, lastOffset + 1)) {
                return ArrayUtil.equals(this.termsBytes, 0, test.termsBytes, 0, this.offsets[lastOffset]);
            }
        }
        return false;
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + this.hashCode;
    }
    
    public String toString(final String defaultField) {
        final StringBuilder builder = new StringBuilder();
        final BytesRef spare = new BytesRef(this.termsBytes);
        boolean first = true;
        for (int i = 0; i < this.termsAndFields.length; ++i) {
            final TermsAndField current = this.termsAndFields[i];
            for (int j = current.start; j < current.end; ++j) {
                spare.offset = this.offsets[j];
                spare.length = this.offsets[j + 1] - this.offsets[j];
                if (!first) {
                    builder.append(' ');
                }
                first = false;
                builder.append(current.field).append(':');
                builder.append(spare.utf8ToString());
            }
        }
        return builder.toString();
    }
    
    private static <T extends Comparable<? super T>> List<T> sort(final List<T> toSort) {
        if (toSort.isEmpty()) {
            throw new IllegalArgumentException("no terms provided");
        }
        Collections.sort(toSort);
        return toSort;
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance((Class)TermsFilter.class);
    }
    
    private static final class TermsAndField implements Accountable
    {
        private static final long BASE_RAM_BYTES_USED;
        final int start;
        final int end;
        final String field;
        
        TermsAndField(final int start, final int end, final String field) {
            this.start = start;
            this.end = end;
            this.field = field;
        }
        
        public long ramBytesUsed() {
            return TermsAndField.BASE_RAM_BYTES_USED + this.field.length() * 2;
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.field == null) ? 0 : this.field.hashCode());
            result = 31 * result + this.end;
            result = 31 * result + this.start;
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final TermsAndField other = (TermsAndField)obj;
            if (this.field == null) {
                if (other.field != null) {
                    return false;
                }
            }
            else if (!this.field.equals(other.field)) {
                return false;
            }
            return this.end == other.end && this.start == other.start;
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance((Class)TermsAndField.class) + RamUsageEstimator.shallowSizeOfInstance((Class)String.class) + RamUsageEstimator.NUM_BYTES_ARRAY_HEADER;
        }
    }
    
    private abstract static class FieldAndTermEnum
    {
        protected String field;
        
        public abstract BytesRef next();
        
        public FieldAndTermEnum() {
        }
        
        public FieldAndTermEnum(final String field) {
            this.field = field;
        }
        
        public String field() {
            return this.field;
        }
    }
}
