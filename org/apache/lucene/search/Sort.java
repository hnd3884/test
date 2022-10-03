package org.apache.lucene.search;

import java.util.Arrays;
import java.io.IOException;

public class Sort
{
    public static final Sort RELEVANCE;
    public static final Sort INDEXORDER;
    SortField[] fields;
    
    public Sort() {
        this(SortField.FIELD_SCORE);
    }
    
    public Sort(final SortField field) {
        this.setSort(field);
    }
    
    public Sort(final SortField... fields) {
        this.setSort(fields);
    }
    
    public void setSort(final SortField field) {
        this.fields = new SortField[] { field };
    }
    
    public void setSort(final SortField... fields) {
        this.fields = fields;
    }
    
    public SortField[] getSort() {
        return this.fields;
    }
    
    public Sort rewrite(final IndexSearcher searcher) throws IOException {
        boolean changed = false;
        final SortField[] rewrittenSortFields = new SortField[this.fields.length];
        for (int i = 0; i < this.fields.length; ++i) {
            rewrittenSortFields[i] = this.fields[i].rewrite(searcher);
            if (this.fields[i] != rewrittenSortFields[i]) {
                changed = true;
            }
        }
        return changed ? new Sort(rewrittenSortFields) : this;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < this.fields.length; ++i) {
            buffer.append(this.fields[i].toString());
            if (i + 1 < this.fields.length) {
                buffer.append(',');
            }
        }
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sort)) {
            return false;
        }
        final Sort other = (Sort)o;
        return Arrays.equals(this.fields, other.fields);
    }
    
    @Override
    public int hashCode() {
        return 1168832101 + Arrays.hashCode(this.fields);
    }
    
    public boolean needsScores() {
        for (final SortField sortField : this.fields) {
            if (sortField.needsScores()) {
                return true;
            }
        }
        return false;
    }
    
    static {
        RELEVANCE = new Sort();
        INDEXORDER = new Sort(SortField.FIELD_DOC);
    }
}
