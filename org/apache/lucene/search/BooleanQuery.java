package org.apache.lucene.search;

import java.util.Objects;
import org.apache.lucene.util.ToStringUtils;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.List;

public class BooleanQuery extends Query implements Iterable<BooleanClause>
{
    private static int maxClauseCount;
    private final boolean mutable;
    private final boolean disableCoord;
    private int minimumNumberShouldMatch;
    private List<BooleanClause> clauses;
    private final Map<BooleanClause.Occur, Collection<Query>> clauseSets;
    private int hashCode;
    
    public static int getMaxClauseCount() {
        return BooleanQuery.maxClauseCount;
    }
    
    public static void setMaxClauseCount(final int maxClauseCount) {
        if (maxClauseCount < 1) {
            throw new IllegalArgumentException("maxClauseCount must be >= 1");
        }
        BooleanQuery.maxClauseCount = maxClauseCount;
    }
    
    private BooleanQuery(final boolean disableCoord, final int minimumNumberShouldMatch, final BooleanClause[] clauses) {
        this.disableCoord = disableCoord;
        this.minimumNumberShouldMatch = minimumNumberShouldMatch;
        this.clauses = Collections.unmodifiableList((List<? extends BooleanClause>)Arrays.asList((T[])clauses));
        this.mutable = false;
        (this.clauseSets = new EnumMap<BooleanClause.Occur, Collection<Query>>(BooleanClause.Occur.class)).put(BooleanClause.Occur.SHOULD, new Multiset<Query>());
        this.clauseSets.put(BooleanClause.Occur.MUST, new Multiset<Query>());
        this.clauseSets.put(BooleanClause.Occur.FILTER, new HashSet<Query>());
        this.clauseSets.put(BooleanClause.Occur.MUST_NOT, new HashSet<Query>());
        for (final BooleanClause clause : clauses) {
            this.clauseSets.get(clause.getOccur()).add(clause.getQuery());
        }
    }
    
    public boolean isCoordDisabled() {
        return this.disableCoord;
    }
    
    public int getMinimumNumberShouldMatch() {
        return this.minimumNumberShouldMatch;
    }
    
    public List<BooleanClause> clauses() {
        return this.clauses;
    }
    
    Collection<Query> getClauses(final BooleanClause.Occur occur) {
        if (this.mutable) {
            final List<Query> queries = new ArrayList<Query>();
            for (final BooleanClause clause : this.clauses) {
                if (clause.getOccur() == occur) {
                    queries.add(clause.getQuery());
                }
            }
            return (Collection<Query>)Collections.unmodifiableList((List<?>)queries);
        }
        return this.clauseSets.get(occur);
    }
    
    @Override
    public final Iterator<BooleanClause> iterator() {
        return this.clauses.iterator();
    }
    
    private BooleanQuery rewriteNoScoring() {
        final Builder newQuery = new Builder();
        newQuery.setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch());
        for (final BooleanClause clause : this.clauses) {
            if (clause.getOccur() == BooleanClause.Occur.MUST) {
                newQuery.add(clause.getQuery(), BooleanClause.Occur.FILTER);
            }
            else {
                newQuery.add(clause);
            }
        }
        return newQuery.build();
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        BooleanQuery query = this;
        if (!needsScores) {
            query = this.rewriteNoScoring();
        }
        return new BooleanWeight(query, searcher, needsScores, this.disableCoord);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        if (this.clauses.size() == 1) {
            final BooleanClause c = this.clauses.get(0);
            final Query query = c.getQuery();
            if (this.minimumNumberShouldMatch == 1 && c.getOccur() == BooleanClause.Occur.SHOULD) {
                return query;
            }
            if (this.minimumNumberShouldMatch == 0) {
                switch (c.getOccur()) {
                    case SHOULD:
                    case MUST: {
                        return query;
                    }
                    case FILTER: {
                        return new BoostQuery(new ConstantScoreQuery(query), 0.0f);
                    }
                    case MUST_NOT: {
                        return new MatchNoDocsQuery();
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
            }
        }
        final Builder builder = new Builder();
        builder.setDisableCoord(this.isCoordDisabled());
        builder.setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch());
        boolean actuallyRewritten = false;
        for (final BooleanClause clause : this) {
            final Query query2 = clause.getQuery();
            final Query rewritten = query2.rewrite(reader);
            if (rewritten != query2) {
                actuallyRewritten = true;
            }
            builder.add(rewritten, clause.getOccur());
        }
        if (this.mutable || actuallyRewritten) {
            return builder.build();
        }
        assert !this.mutable;
        int clauseCount = 0;
        for (final Collection<Query> queries : this.clauseSets.values()) {
            clauseCount += queries.size();
        }
        if (clauseCount != this.clauses.size()) {
            final Builder rewritten2 = new Builder();
            rewritten2.setDisableCoord(this.disableCoord);
            rewritten2.setMinimumNumberShouldMatch(this.minimumNumberShouldMatch);
            for (final Map.Entry<BooleanClause.Occur, Collection<Query>> entry : this.clauseSets.entrySet()) {
                final BooleanClause.Occur occur = entry.getKey();
                for (final Query query3 : entry.getValue()) {
                    rewritten2.add(query3, occur);
                }
            }
            return rewritten2.build();
        }
        if (this.clauseSets.get(BooleanClause.Occur.MUST).size() > 0 && this.clauseSets.get(BooleanClause.Occur.FILTER).size() > 0) {
            final Set<Query> filters = new HashSet<Query>(this.clauseSets.get(BooleanClause.Occur.FILTER));
            boolean modified = filters.remove(new MatchAllDocsQuery());
            modified |= filters.removeAll(this.clauseSets.get(BooleanClause.Occur.MUST));
            if (modified) {
                final Builder builder2 = new Builder();
                builder2.setDisableCoord(this.isCoordDisabled());
                builder2.setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch());
                for (final BooleanClause clause2 : this.clauses) {
                    if (clause2.getOccur() != BooleanClause.Occur.FILTER) {
                        builder2.add(clause2);
                    }
                }
                for (final Query filter : filters) {
                    builder2.add(filter, BooleanClause.Occur.FILTER);
                }
                return builder2.build();
            }
        }
        final Collection<Query> musts = this.clauseSets.get(BooleanClause.Occur.MUST);
        final Collection<Query> filters2 = this.clauseSets.get(BooleanClause.Occur.FILTER);
        if (musts.size() == 1 && filters2.size() > 0) {
            Query must = musts.iterator().next();
            float boost = 1.0f;
            if (must instanceof BoostQuery) {
                final BoostQuery boostQuery = (BoostQuery)must;
                must = boostQuery.getQuery();
                boost = boostQuery.getBoost();
            }
            if (must.getClass() == MatchAllDocsQuery.class) {
                Builder builder3 = new Builder();
                for (final BooleanClause clause3 : this.clauses) {
                    switch (clause3.getOccur()) {
                        case FILTER:
                        case MUST_NOT: {
                            builder3.add(clause3);
                            continue;
                        }
                    }
                }
                Query rewritten = builder3.build();
                rewritten = new ConstantScoreQuery(rewritten);
                if (boost != 1.0f) {
                    rewritten = new BoostQuery(rewritten, boost);
                }
                builder3 = new Builder().setDisableCoord(this.isCoordDisabled()).setMinimumNumberShouldMatch(this.getMinimumNumberShouldMatch()).add(rewritten, BooleanClause.Occur.MUST);
                for (final Query query4 : this.clauseSets.get(BooleanClause.Occur.SHOULD)) {
                    builder3.add(query4, BooleanClause.Occur.SHOULD);
                }
                rewritten = builder3.build();
                return rewritten;
            }
        }
        return super.rewrite(reader);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        final boolean needParens = this.getBoost() != 1.0 || this.getMinimumNumberShouldMatch() > 0;
        if (needParens) {
            buffer.append("(");
        }
        int i = 0;
        for (final BooleanClause c : this) {
            buffer.append(c.getOccur().toString());
            final Query subQuery = c.getQuery();
            if (subQuery instanceof BooleanQuery) {
                buffer.append("(");
                buffer.append(subQuery.toString(field));
                buffer.append(")");
            }
            else {
                buffer.append(subQuery.toString(field));
            }
            if (i != this.clauses.size() - 1) {
                buffer.append(" ");
            }
            ++i;
        }
        if (needParens) {
            buffer.append(")");
        }
        if (this.getMinimumNumberShouldMatch() > 0) {
            buffer.append('~');
            buffer.append(this.getMinimumNumberShouldMatch());
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final BooleanQuery that = (BooleanQuery)o;
        if (this.getMinimumNumberShouldMatch() != that.getMinimumNumberShouldMatch()) {
            return false;
        }
        if (this.disableCoord != that.disableCoord) {
            return false;
        }
        if (this.mutable != that.mutable) {
            return false;
        }
        if (this.mutable) {
            return this.clauses.equals(that.clauses);
        }
        return this.clauseSets.equals(that.clauseSets);
    }
    
    private int computeHashCode() {
        int hashCode = Objects.hash(this.disableCoord, this.minimumNumberShouldMatch, this.clauseSets);
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }
    
    @Override
    public int hashCode() {
        if (this.mutable) {
            assert this.clauseSets == null;
            return 31 * super.hashCode() + Objects.hash(this.disableCoord, this.minimumNumberShouldMatch, this.clauses);
        }
        else {
            if (this.hashCode == 0) {
                this.hashCode = this.computeHashCode();
                assert this.hashCode != 0;
            }
            assert this.hashCode == this.computeHashCode();
            return 31 * super.hashCode() + this.hashCode;
        }
    }
    
    @Deprecated
    public BooleanClause[] getClauses() {
        return this.clauses.toArray(new BooleanClause[this.clauses.size()]);
    }
    
    @Override
    public BooleanQuery clone() {
        final BooleanQuery clone = (BooleanQuery)super.clone();
        clone.clauses = new ArrayList<BooleanClause>(this.clauses);
        return clone;
    }
    
    @Deprecated
    public BooleanQuery() {
        this(false);
    }
    
    @Deprecated
    public BooleanQuery(final boolean disableCoord) {
        this.clauses = new ArrayList<BooleanClause>();
        this.disableCoord = disableCoord;
        this.minimumNumberShouldMatch = 0;
        this.mutable = true;
        this.clauseSets = null;
    }
    
    private void ensureMutable(final String method) {
        if (!this.mutable) {
            throw new IllegalStateException("This BooleanQuery has been created with the new BooleanQuery.Builder API. It must not be modified afterwards. The " + method + " method only exists for backward compatibility");
        }
    }
    
    @Deprecated
    public void setMinimumNumberShouldMatch(final int min) {
        this.ensureMutable("setMinimumNumberShouldMatch");
        this.minimumNumberShouldMatch = min;
    }
    
    @Deprecated
    public void add(final Query query, final BooleanClause.Occur occur) {
        this.add(new BooleanClause(query, occur));
    }
    
    @Deprecated
    public void add(final BooleanClause clause) {
        this.ensureMutable("add");
        Objects.requireNonNull(clause, "BooleanClause must not be null");
        if (this.clauses.size() >= BooleanQuery.maxClauseCount) {
            throw new TooManyClauses();
        }
        this.clauses.add(clause);
    }
    
    static {
        BooleanQuery.maxClauseCount = 1024;
    }
    
    public static class TooManyClauses extends RuntimeException
    {
        public TooManyClauses() {
            super("maxClauseCount is set to " + BooleanQuery.maxClauseCount);
        }
    }
    
    public static class Builder
    {
        private boolean disableCoord;
        private int minimumNumberShouldMatch;
        private final List<BooleanClause> clauses;
        
        public Builder() {
            this.clauses = new ArrayList<BooleanClause>();
        }
        
        public Builder setDisableCoord(final boolean disableCoord) {
            this.disableCoord = disableCoord;
            return this;
        }
        
        public Builder setMinimumNumberShouldMatch(final int min) {
            this.minimumNumberShouldMatch = min;
            return this;
        }
        
        public Builder add(final BooleanClause clause) {
            this.add(clause.getQuery(), clause.getOccur());
            return this;
        }
        
        public Builder add(final Query query, final BooleanClause.Occur occur) {
            if (this.clauses.size() >= BooleanQuery.maxClauseCount) {
                throw new TooManyClauses();
            }
            this.clauses.add(new BooleanClause(query, occur));
            return this;
        }
        
        public BooleanQuery build() {
            return new BooleanQuery(this.disableCoord, this.minimumNumberShouldMatch, this.clauses.toArray(new BooleanClause[0]), null);
        }
    }
}
