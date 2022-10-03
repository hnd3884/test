package org.apache.lucene.search;

import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.DocValues;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.BytesRef;

@Deprecated
public abstract class DocTermOrdsRangeFilter extends Filter
{
    final String field;
    final BytesRef lowerVal;
    final BytesRef upperVal;
    final boolean includeLower;
    final boolean includeUpper;
    
    private DocTermOrdsRangeFilter(final String field, final BytesRef lowerVal, final BytesRef upperVal, final boolean includeLower, final boolean includeUpper) {
        this.field = field;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }
    
    public abstract DocIdSet getDocIdSet(final LeafReaderContext p0, final Bits p1) throws IOException;
    
    public static DocTermOrdsRangeFilter newBytesRefRange(final String field, final BytesRef lowerVal, final BytesRef upperVal, final boolean includeLower, final boolean includeUpper) {
        return new DocTermOrdsRangeFilter(field, lowerVal, upperVal, includeLower, includeUpper) {
            @Override
            public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
                final SortedSetDocValues docTermOrds = DocValues.getSortedSet(context.reader(), this.field);
                final long lowerPoint = (this.lowerVal == null) ? -1L : docTermOrds.lookupTerm(this.lowerVal);
                final long upperPoint = (this.upperVal == null) ? -1L : docTermOrds.lookupTerm(this.upperVal);
                long inclusiveLowerPoint;
                if (lowerPoint == -1L && this.lowerVal == null) {
                    inclusiveLowerPoint = 0L;
                }
                else if (this.includeLower && lowerPoint >= 0L) {
                    inclusiveLowerPoint = lowerPoint;
                }
                else if (lowerPoint >= 0L) {
                    inclusiveLowerPoint = lowerPoint + 1L;
                }
                else {
                    inclusiveLowerPoint = Math.max(0L, -lowerPoint - 1L);
                }
                long inclusiveUpperPoint;
                if (upperPoint == -1L && this.upperVal == null) {
                    inclusiveUpperPoint = Long.MAX_VALUE;
                }
                else if (this.includeUpper && upperPoint >= 0L) {
                    inclusiveUpperPoint = upperPoint;
                }
                else if (upperPoint >= 0L) {
                    inclusiveUpperPoint = upperPoint - 1L;
                }
                else {
                    inclusiveUpperPoint = -upperPoint - 2L;
                }
                if (inclusiveUpperPoint < 0L || inclusiveLowerPoint > inclusiveUpperPoint) {
                    return null;
                }
                assert inclusiveLowerPoint >= 0L && inclusiveUpperPoint >= 0L;
                return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                    protected final boolean matchDoc(final int doc) {
                        docTermOrds.setDocument(doc);
                        long ord;
                        while ((ord = docTermOrds.nextOrd()) != -1L) {
                            if (ord > inclusiveUpperPoint) {
                                return false;
                            }
                            if (ord >= inclusiveLowerPoint) {
                                return true;
                            }
                        }
                        return false;
                    }
                };
            }
        };
    }
    
    public final String toString(final String defaultField) {
        final StringBuilder sb = new StringBuilder(this.field).append(":");
        return sb.append(this.includeLower ? '[' : '{').append((this.lowerVal == null) ? "*" : this.lowerVal.toString()).append(" TO ").append((this.upperVal == null) ? "*" : this.upperVal.toString()).append(this.includeUpper ? ']' : '}').toString();
    }
    
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        final DocTermOrdsRangeFilter other = (DocTermOrdsRangeFilter)o;
        if (!this.field.equals(other.field) || this.includeLower != other.includeLower || this.includeUpper != other.includeUpper) {
            return false;
        }
        Label_0093: {
            if (this.lowerVal != null) {
                if (this.lowerVal.equals((Object)other.lowerVal)) {
                    break Label_0093;
                }
            }
            else if (other.lowerVal == null) {
                break Label_0093;
            }
            return false;
        }
        if (this.upperVal != null) {
            if (this.upperVal.equals((Object)other.upperVal)) {
                return true;
            }
        }
        else if (other.upperVal == null) {
            return true;
        }
        return false;
    }
    
    public final int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.field.hashCode();
        h ^= ((this.lowerVal != null) ? this.lowerVal.hashCode() : 550356204);
        h = (h << 1 | h >>> 31);
        h ^= ((this.upperVal != null) ? this.upperVal.hashCode() : -1674416163);
        h ^= ((this.includeLower ? 1549299360 : -365038026) ^ (this.includeUpper ? 1721088258 : 1948649653));
        return h;
    }
    
    public String getField() {
        return this.field;
    }
    
    public boolean includesLower() {
        return this.includeLower;
    }
    
    public boolean includesUpper() {
        return this.includeUpper;
    }
    
    public BytesRef getLowerVal() {
        return this.lowerVal;
    }
    
    public BytesRef getUpperVal() {
        return this.upperVal;
    }
}
