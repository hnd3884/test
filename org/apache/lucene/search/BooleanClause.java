package org.apache.lucene.search;

import java.util.Objects;

public final class BooleanClause
{
    private Query query;
    private Occur occur;
    
    public BooleanClause(final Query query, final Occur occur) {
        this.query = Objects.requireNonNull(query, "Query must not be null");
        this.occur = Objects.requireNonNull(occur, "Occur must not be null");
    }
    
    public Occur getOccur() {
        return this.occur;
    }
    
    public Query getQuery() {
        return this.query;
    }
    
    public boolean isProhibited() {
        return Occur.MUST_NOT == this.occur;
    }
    
    public boolean isRequired() {
        return this.occur == Occur.MUST || this.occur == Occur.FILTER;
    }
    
    public boolean isScoring() {
        return this.occur == Occur.MUST || this.occur == Occur.SHOULD;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof BooleanClause)) {
            return false;
        }
        final BooleanClause other = (BooleanClause)o;
        return this.query.equals(other.query) && this.occur == other.occur;
    }
    
    @Override
    public int hashCode() {
        return 31 * this.query.hashCode() + this.occur.hashCode();
    }
    
    @Override
    public String toString() {
        return this.occur.toString() + this.query.toString();
    }
    
    @Deprecated
    public void setOccur(final Occur occur) {
        this.occur = Objects.requireNonNull(occur, "Occur must not be null");
    }
    
    @Deprecated
    public void setQuery(final Query query) {
        this.query = Objects.requireNonNull(query, "Query must not be null");
    }
    
    public enum Occur
    {
        MUST {
            @Override
            public String toString() {
                return "+";
            }
        }, 
        FILTER {
            @Override
            public String toString() {
                return "#";
            }
        }, 
        SHOULD {
            @Override
            public String toString() {
                return "";
            }
        }, 
        MUST_NOT {
            @Override
            public String toString() {
                return "-";
            }
        };
    }
}
