package org.apache.lucene.queries;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import java.util.Iterator;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.Filter;

@Deprecated
public class BooleanFilter extends Filter implements Iterable<FilterClause>
{
    private final List<FilterClause> clauses;
    
    public BooleanFilter() {
        this.clauses = new ArrayList<FilterClause>();
    }
    
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        BitDocIdSet.Builder res = null;
        final LeafReader reader = context.reader();
        boolean hasShouldClauses = false;
        for (final FilterClause fc : this.clauses) {
            if (fc.getOccur() == BooleanClause.Occur.SHOULD) {
                hasShouldClauses = true;
                final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
                if (disi == null) {
                    continue;
                }
                if (res == null) {
                    res = new BitDocIdSet.Builder(reader.maxDoc());
                }
                res.or(disi);
            }
        }
        if (hasShouldClauses && res == null) {
            return null;
        }
        for (final FilterClause fc : this.clauses) {
            if (fc.getOccur() == BooleanClause.Occur.MUST_NOT) {
                if (res == null) {
                    assert !hasShouldClauses;
                    res = new BitDocIdSet.Builder(reader.maxDoc(), true);
                }
                final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
                if (disi == null) {
                    continue;
                }
                res.andNot(disi);
            }
        }
        for (final FilterClause fc : this.clauses) {
            if (fc.getOccur() == BooleanClause.Occur.MUST) {
                final DocIdSetIterator disi = getDISI(fc.getFilter(), context);
                if (disi == null) {
                    return null;
                }
                if (res == null) {
                    res = new BitDocIdSet.Builder(reader.maxDoc());
                    res.or(disi);
                }
                else {
                    res.and(disi);
                }
            }
        }
        if (res == null) {
            return null;
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)res.build(), acceptDocs);
    }
    
    private static DocIdSetIterator getDISI(final Filter filter, final LeafReaderContext context) throws IOException {
        final DocIdSet set = filter.getDocIdSet(context, (Bits)null);
        return (set == null) ? null : set.iterator();
    }
    
    public void add(final FilterClause filterClause) {
        this.clauses.add(filterClause);
    }
    
    public final void add(final Filter filter, final BooleanClause.Occur occur) {
        this.add(new FilterClause(filter, occur));
    }
    
    public List<FilterClause> clauses() {
        return this.clauses;
    }
    
    public final Iterator<FilterClause> iterator() {
        return this.clauses().iterator();
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final BooleanFilter other = (BooleanFilter)obj;
        return this.clauses.equals(other.clauses);
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + this.clauses.hashCode();
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder("BooleanFilter(");
        final int minLen = buffer.length();
        for (final FilterClause c : this.clauses) {
            if (buffer.length() > minLen) {
                buffer.append(' ');
            }
            buffer.append(c);
        }
        return buffer.append(')').toString();
    }
}
