package org.apache.lucene.queryparser.complexPhrase;

import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanBoostQuery;
import java.io.IOException;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import java.util.List;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import java.util.Iterator;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import java.util.ArrayList;
import org.apache.lucene.queryparser.classic.QueryParser;

public class ComplexPhraseQueryParser extends QueryParser
{
    private ArrayList<ComplexPhraseQuery> complexPhrases;
    private boolean isPass2ResolvingPhrases;
    private boolean inOrder;
    private ComplexPhraseQuery currentPhraseQuery;
    
    public void setInOrder(final boolean inOrder) {
        this.inOrder = inOrder;
    }
    
    public ComplexPhraseQueryParser(final String f, final Analyzer a) {
        super(f, a);
        this.complexPhrases = null;
        this.inOrder = true;
        this.currentPhraseQuery = null;
    }
    
    @Override
    protected Query getFieldQuery(final String field, final String queryText, final int slop) {
        final ComplexPhraseQuery cpq = new ComplexPhraseQuery(field, queryText, slop, this.inOrder);
        this.complexPhrases.add(cpq);
        return cpq;
    }
    
    @Override
    public Query parse(final String query) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            final MultiTermQuery.RewriteMethod oldMethod = this.getMultiTermRewriteMethod();
            try {
                this.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
                return super.parse(query);
            }
            finally {
                this.setMultiTermRewriteMethod(oldMethod);
            }
        }
        this.complexPhrases = new ArrayList<ComplexPhraseQuery>();
        final Query q = super.parse(query);
        this.isPass2ResolvingPhrases = true;
        try {
            final Iterator<ComplexPhraseQuery> iterator = this.complexPhrases.iterator();
            while (iterator.hasNext()) {
                (this.currentPhraseQuery = iterator.next()).parsePhraseElements(this);
            }
        }
        finally {
            this.isPass2ResolvingPhrases = false;
        }
        return q;
    }
    
    protected Query newTermQuery(final Term term) {
        if (this.isPass2ResolvingPhrases) {
            try {
                this.checkPhraseClauseIsForSameField(term.field());
            }
            catch (final ParseException pe) {
                throw new RuntimeException("Error parsing complex phrase", pe);
            }
        }
        return super.newTermQuery(term);
    }
    
    private void checkPhraseClauseIsForSameField(final String field) throws ParseException {
        if (!field.equals(this.currentPhraseQuery.field)) {
            throw new ParseException("Cannot have clause for field \"" + field + "\" nested in phrase " + " for field \"" + this.currentPhraseQuery.field + "\"");
        }
    }
    
    @Override
    protected Query getWildcardQuery(final String field, final String termStr) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getWildcardQuery(field, termStr);
    }
    
    @Override
    protected Query getRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }
    
    @Override
    protected Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
        if (this.isPass2ResolvingPhrases) {
            final TermRangeQuery rangeQuery = TermRangeQuery.newStringRange(field, part1, part2, startInclusive, endInclusive);
            rangeQuery.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_REWRITE);
            return (Query)rangeQuery;
        }
        return super.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }
    
    @Override
    protected Query getFuzzyQuery(final String field, final String termStr, final float minSimilarity) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getFuzzyQuery(field, termStr, minSimilarity);
    }
    
    static class ComplexPhraseQuery extends Query
    {
        final String field;
        final String phrasedQueryStringContents;
        final int slopFactor;
        private final boolean inOrder;
        private final Query[] contents;
        
        public ComplexPhraseQuery(final String field, final String phrasedQueryStringContents, final int slopFactor, final boolean inOrder) {
            this.contents = new Query[1];
            this.field = field;
            this.phrasedQueryStringContents = phrasedQueryStringContents;
            this.slopFactor = slopFactor;
            this.inOrder = inOrder;
        }
        
        protected void parsePhraseElements(final ComplexPhraseQueryParser qp) throws ParseException {
            final String oldDefaultParserField = qp.field;
            try {
                qp.field = this.field;
                this.contents[0] = qp.parse(this.phrasedQueryStringContents);
            }
            finally {
                qp.field = oldDefaultParserField;
            }
        }
        
        public Query rewrite(final IndexReader reader) throws IOException {
            if (this.getBoost() != 1.0f) {
                return super.rewrite(reader);
            }
            final Query contents = this.contents[0];
            if (contents instanceof TermQuery) {
                return contents;
            }
            int numNegatives = 0;
            if (!(contents instanceof BooleanQuery)) {
                throw new IllegalArgumentException("Unknown query type \"" + contents.getClass().getName() + "\" found in phrase query string \"" + this.phrasedQueryStringContents + "\"");
            }
            final BooleanQuery bq = (BooleanQuery)contents;
            final SpanQuery[] allSpanClauses = new SpanQuery[bq.clauses().size()];
            int i = 0;
            for (final BooleanClause clause : bq) {
                Query qc = clause.getQuery();
                qc = new IndexSearcher(reader).rewrite(qc);
                if (clause.getOccur().equals((Object)BooleanClause.Occur.MUST_NOT)) {
                    ++numNegatives;
                }
                while (qc instanceof BoostQuery) {
                    qc = ((BoostQuery)qc).getQuery();
                }
                if (qc instanceof BooleanQuery) {
                    final ArrayList<SpanQuery> sc = new ArrayList<SpanQuery>();
                    this.addComplexPhraseClause(sc, (BooleanQuery)qc);
                    if (sc.size() > 0) {
                        allSpanClauses[i] = sc.get(0);
                    }
                    else {
                        allSpanClauses[i] = (SpanQuery)new SpanTermQuery(new Term(this.field, "Dummy clause because no terms found - must match nothing"));
                    }
                }
                else {
                    if (!(qc instanceof TermQuery)) {
                        throw new IllegalArgumentException("Unknown query type \"" + qc.getClass().getName() + "\" found in phrase query string \"" + this.phrasedQueryStringContents + "\"");
                    }
                    final TermQuery tq = (TermQuery)qc;
                    allSpanClauses[i] = (SpanQuery)new SpanTermQuery(tq.getTerm());
                }
                ++i;
            }
            if (numNegatives == 0) {
                return (Query)new SpanNearQuery(allSpanClauses, this.slopFactor, this.inOrder);
            }
            final ArrayList<SpanQuery> positiveClauses = new ArrayList<SpanQuery>();
            i = 0;
            for (final BooleanClause clause2 : bq) {
                if (!clause2.getOccur().equals((Object)BooleanClause.Occur.MUST_NOT)) {
                    positiveClauses.add(allSpanClauses[i]);
                }
                ++i;
            }
            final SpanQuery[] includeClauses = positiveClauses.toArray(new SpanQuery[positiveClauses.size()]);
            SpanQuery include = null;
            if (includeClauses.length == 1) {
                include = includeClauses[0];
            }
            else {
                include = (SpanQuery)new SpanNearQuery(includeClauses, this.slopFactor + numNegatives, this.inOrder);
            }
            final SpanNearQuery exclude = new SpanNearQuery(allSpanClauses, this.slopFactor, this.inOrder);
            final SpanNotQuery snot = new SpanNotQuery(include, (SpanQuery)exclude);
            return (Query)snot;
        }
        
        private void addComplexPhraseClause(final List<SpanQuery> spanClauses, final BooleanQuery qc) {
            final ArrayList<SpanQuery> ors = new ArrayList<SpanQuery>();
            final ArrayList<SpanQuery> nots = new ArrayList<SpanQuery>();
            for (final BooleanClause clause : qc) {
                Query childQuery = clause.getQuery();
                float boost = 1.0f;
                while (childQuery instanceof BoostQuery) {
                    final BoostQuery bq = (BoostQuery)childQuery;
                    boost *= bq.getBoost();
                    childQuery = bq.getQuery();
                }
                ArrayList<SpanQuery> chosenList = ors;
                if (clause.getOccur() == BooleanClause.Occur.MUST_NOT) {
                    chosenList = nots;
                }
                if (childQuery instanceof TermQuery) {
                    final TermQuery tq = (TermQuery)childQuery;
                    SpanQuery stq = (SpanQuery)new SpanTermQuery(tq.getTerm());
                    if (boost != 1.0f) {
                        stq = (SpanQuery)new SpanBoostQuery(stq, boost);
                    }
                    chosenList.add(stq);
                }
                else {
                    if (!(childQuery instanceof BooleanQuery)) {
                        throw new IllegalArgumentException("Unknown query type:" + childQuery.getClass().getName());
                    }
                    final BooleanQuery cbq = (BooleanQuery)childQuery;
                    this.addComplexPhraseClause(chosenList, cbq);
                }
            }
            if (ors.size() == 0) {
                return;
            }
            final SpanOrQuery soq = new SpanOrQuery((SpanQuery[])ors.toArray(new SpanQuery[ors.size()]));
            if (nots.size() == 0) {
                spanClauses.add((SpanQuery)soq);
            }
            else {
                final SpanOrQuery snqs = new SpanOrQuery((SpanQuery[])nots.toArray(new SpanQuery[nots.size()]));
                final SpanNotQuery snq = new SpanNotQuery((SpanQuery)soq, (SpanQuery)snqs);
                spanClauses.add((SpanQuery)snq);
            }
        }
        
        public String toString(final String field) {
            if (this.slopFactor == 0) {
                return "\"" + this.phrasedQueryStringContents + "\"";
            }
            return "\"" + this.phrasedQueryStringContents + "\"" + "~" + this.slopFactor;
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = 31 * result + ((this.field == null) ? 0 : this.field.hashCode());
            result = 31 * result + ((this.phrasedQueryStringContents == null) ? 0 : this.phrasedQueryStringContents.hashCode());
            result = 31 * result + this.slopFactor;
            result = 31 * result + (this.inOrder ? 1 : 0);
            return result;
        }
        
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
            if (!super.equals(obj)) {
                return false;
            }
            final ComplexPhraseQuery other = (ComplexPhraseQuery)obj;
            if (this.field == null) {
                if (other.field != null) {
                    return false;
                }
            }
            else if (!this.field.equals(other.field)) {
                return false;
            }
            if (this.phrasedQueryStringContents == null) {
                if (other.phrasedQueryStringContents != null) {
                    return false;
                }
            }
            else if (!this.phrasedQueryStringContents.equals(other.phrasedQueryStringContents)) {
                return false;
            }
            return this.slopFactor == other.slopFactor && this.inOrder == other.inOrder;
        }
    }
}
