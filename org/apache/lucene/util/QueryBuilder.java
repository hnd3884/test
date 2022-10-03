package org.apache.lucene.util;

import org.apache.lucene.search.TermQuery;
import java.util.List;
import org.apache.lucene.search.MultiPhraseQuery;
import java.util.ArrayList;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.index.Term;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.CachingTokenFilter;
import java.util.Iterator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;

public class QueryBuilder
{
    private Analyzer analyzer;
    private boolean enablePositionIncrements;
    
    public QueryBuilder(final Analyzer analyzer) {
        this.enablePositionIncrements = true;
        this.analyzer = analyzer;
    }
    
    public Query createBooleanQuery(final String field, final String queryText) {
        return this.createBooleanQuery(field, queryText, BooleanClause.Occur.SHOULD);
    }
    
    public Query createBooleanQuery(final String field, final String queryText, final BooleanClause.Occur operator) {
        if (operator != BooleanClause.Occur.SHOULD && operator != BooleanClause.Occur.MUST) {
            throw new IllegalArgumentException("invalid operator: only SHOULD or MUST are allowed");
        }
        return this.createFieldQuery(this.analyzer, operator, field, queryText, false, 0);
    }
    
    public Query createPhraseQuery(final String field, final String queryText) {
        return this.createPhraseQuery(field, queryText, 0);
    }
    
    public Query createPhraseQuery(final String field, final String queryText, final int phraseSlop) {
        return this.createFieldQuery(this.analyzer, BooleanClause.Occur.MUST, field, queryText, true, phraseSlop);
    }
    
    public Query createMinShouldMatchQuery(final String field, final String queryText, final float fraction) {
        if (Float.isNaN(fraction) || fraction < 0.0f || fraction > 1.0f) {
            throw new IllegalArgumentException("fraction should be >= 0 and <= 1");
        }
        if (fraction == 1.0f) {
            return this.createBooleanQuery(field, queryText, BooleanClause.Occur.MUST);
        }
        Query query = this.createFieldQuery(this.analyzer, BooleanClause.Occur.SHOULD, field, queryText, false, 0);
        if (query instanceof BooleanQuery) {
            final BooleanQuery bq = (BooleanQuery)query;
            final BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.setDisableCoord(bq.isCoordDisabled());
            builder.setMinimumNumberShouldMatch((int)(fraction * bq.clauses().size()));
            for (final BooleanClause clause : bq) {
                builder.add(clause);
            }
            query = builder.build();
        }
        return query;
    }
    
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public void setAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    public boolean getEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }
    
    public void setEnablePositionIncrements(final boolean enable) {
        this.enablePositionIncrements = enable;
    }
    
    protected final Query createFieldQuery(final Analyzer analyzer, final BooleanClause.Occur operator, final String field, final String queryText, final boolean quoted, final int phraseSlop) {
        assert operator == BooleanClause.Occur.MUST;
        try (final TokenStream source = analyzer.tokenStream(field, queryText);
             final CachingTokenFilter stream = new CachingTokenFilter(source)) {
            final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
            final PositionIncrementAttribute posIncAtt = stream.addAttribute(PositionIncrementAttribute.class);
            if (termAtt == null) {
                return null;
            }
            int numTokens = 0;
            int positionCount = 0;
            boolean hasSynonyms = false;
            stream.reset();
            while (stream.incrementToken()) {
                ++numTokens;
                final int positionIncrement = posIncAtt.getPositionIncrement();
                if (positionIncrement != 0) {
                    positionCount += positionIncrement;
                }
                else {
                    hasSynonyms = true;
                }
            }
            if (numTokens == 0) {
                return null;
            }
            if (numTokens == 1) {
                return this.analyzeTerm(field, stream);
            }
            if (quoted && positionCount > 1) {
                if (hasSynonyms) {
                    return this.analyzeMultiPhrase(field, stream, phraseSlop);
                }
                return this.analyzePhrase(field, stream, phraseSlop);
            }
            else {
                if (positionCount == 1) {
                    return this.analyzeBoolean(field, stream);
                }
                return this.analyzeMultiBoolean(field, stream, operator);
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("Error analyzing query text", e);
        }
    }
    
    private Query analyzeTerm(final String field, final TokenStream stream) throws IOException {
        final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
        stream.reset();
        if (!stream.incrementToken()) {
            throw new AssertionError();
        }
        return this.newTermQuery(new Term(field, termAtt.getBytesRef()));
    }
    
    private Query analyzeBoolean(final String field, final TokenStream stream) throws IOException {
        final BooleanQuery.Builder q = new BooleanQuery.Builder();
        q.setDisableCoord(true);
        final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            final Query currentQuery = this.newTermQuery(new Term(field, termAtt.getBytesRef()));
            q.add(currentQuery, BooleanClause.Occur.SHOULD);
        }
        return q.build();
    }
    
    private void add(final BooleanQuery.Builder q, final BooleanQuery current, final BooleanClause.Occur operator) {
        if (current.clauses().isEmpty()) {
            return;
        }
        if (current.clauses().size() == 1) {
            q.add(current.clauses().iterator().next().getQuery(), operator);
        }
        else {
            q.add(current, operator);
        }
    }
    
    private Query analyzeMultiBoolean(final String field, final TokenStream stream, final BooleanClause.Occur operator) throws IOException {
        final BooleanQuery.Builder q = this.newBooleanQuery(false);
        BooleanQuery.Builder currentQuery = this.newBooleanQuery(true);
        final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
        final PositionIncrementAttribute posIncrAtt = stream.getAttribute(PositionIncrementAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            final BytesRef bytes = termAtt.getBytesRef();
            if (posIncrAtt.getPositionIncrement() != 0) {
                this.add(q, currentQuery.build(), operator);
                currentQuery = this.newBooleanQuery(true);
            }
            currentQuery.add(this.newTermQuery(new Term(field, termAtt.getBytesRef())), BooleanClause.Occur.SHOULD);
        }
        this.add(q, currentQuery.build(), operator);
        return q.build();
    }
    
    private Query analyzePhrase(final String field, final TokenStream stream, final int slop) throws IOException {
        final PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.setSlop(slop);
        final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
        final PositionIncrementAttribute posIncrAtt = stream.getAttribute(PositionIncrementAttribute.class);
        int position = -1;
        stream.reset();
        while (stream.incrementToken()) {
            final BytesRef bytes = termAtt.getBytesRef();
            if (this.enablePositionIncrements) {
                position += posIncrAtt.getPositionIncrement();
            }
            else {
                ++position;
            }
            builder.add(new Term(field, bytes), position);
        }
        return builder.build();
    }
    
    private Query analyzeMultiPhrase(final String field, final TokenStream stream, final int slop) throws IOException {
        final MultiPhraseQuery mpq = this.newMultiPhraseQuery();
        mpq.setSlop(slop);
        final TermToBytesRefAttribute termAtt = stream.getAttribute(TermToBytesRefAttribute.class);
        final PositionIncrementAttribute posIncrAtt = stream.getAttribute(PositionIncrementAttribute.class);
        int position = -1;
        final List<Term> multiTerms = new ArrayList<Term>();
        stream.reset();
        while (stream.incrementToken()) {
            final int positionIncrement = posIncrAtt.getPositionIncrement();
            if (positionIncrement > 0 && multiTerms.size() > 0) {
                if (this.enablePositionIncrements) {
                    mpq.add(multiTerms.toArray(new Term[0]), position);
                }
                else {
                    mpq.add(multiTerms.toArray(new Term[0]));
                }
                multiTerms.clear();
            }
            position += positionIncrement;
            multiTerms.add(new Term(field, termAtt.getBytesRef()));
        }
        if (this.enablePositionIncrements) {
            mpq.add(multiTerms.toArray(new Term[0]), position);
        }
        else {
            mpq.add(multiTerms.toArray(new Term[0]));
        }
        return mpq;
    }
    
    protected BooleanQuery.Builder newBooleanQuery(final boolean disableCoord) {
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.setDisableCoord(disableCoord);
        return builder;
    }
    
    protected Query newTermQuery(final Term term) {
        return new TermQuery(term);
    }
    
    protected MultiPhraseQuery newMultiPhraseQuery() {
        return new MultiPhraseQuery();
    }
}
