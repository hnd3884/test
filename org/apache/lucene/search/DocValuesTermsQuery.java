package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.ToStringUtils;
import java.util.AbstractList;
import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import java.util.Objects;
import java.util.Collection;
import org.apache.lucene.util.BytesRef;

public class DocValuesTermsQuery extends Query
{
    private final String field;
    private final BytesRef[] terms;
    
    public DocValuesTermsQuery(final String field, final Collection<BytesRef> terms) {
        this.field = Objects.requireNonNull(field);
        Objects.requireNonNull(terms, "Collection of terms must not be null");
        ArrayUtil.timSort((Object[])(this.terms = terms.toArray(new BytesRef[terms.size()])), BytesRef.getUTF8SortedAsUnicodeComparator());
    }
    
    public DocValuesTermsQuery(final String field, final BytesRef... terms) {
        this(field, Arrays.asList(terms));
    }
    
    public DocValuesTermsQuery(final String field, final String... terms) {
        this(field, new AbstractList<BytesRef>() {
            @Override
            public BytesRef get(final int index) {
                return new BytesRef((CharSequence)terms[index]);
            }
            
            @Override
            public int size() {
                return terms.length;
            }
        });
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DocValuesTermsQuery that = (DocValuesTermsQuery)obj;
        return this.field.equals(that.field) && Arrays.equals(this.terms, that.terms);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(this.field, Arrays.asList(this.terms));
    }
    
    public String toString(final String defaultField) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.field).append(": [");
        for (final BytesRef term : this.terms) {
            sb.append(term).append(", ");
        }
        if (this.terms.length > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append(']').append(ToStringUtils.boost(this.getBoost())).toString();
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new RandomAccessWeight(this) {
            protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
                final SortedSetDocValues values = DocValues.getSortedSet(context.reader(), DocValuesTermsQuery.this.field);
                final LongBitSet bits = new LongBitSet(values.getValueCount());
                for (final BytesRef term : DocValuesTermsQuery.this.terms) {
                    final long ord = values.lookupTerm(term);
                    if (ord >= 0L) {
                        bits.set(ord);
                    }
                }
                return (Bits)new Bits() {
                    public boolean get(final int doc) {
                        values.setDocument(doc);
                        for (long ord = values.nextOrd(); ord != -1L; ord = values.nextOrd()) {
                            if (bits.get(ord)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    
                    public int length() {
                        return context.reader().maxDoc();
                    }
                };
            }
        };
    }
}
