package org.apache.lucene.search.highlight;

import java.util.HashSet;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import java.util.HashMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.Map;
import java.util.Set;

public class QueryScorer implements Scorer
{
    private float totalScore;
    private Set<String> foundTerms;
    private Map<String, WeightedSpanTerm> fieldWeightedSpanTerms;
    private float maxTermWeight;
    private int position;
    private String defaultField;
    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncAtt;
    private boolean expandMultiTermQuery;
    private Query query;
    private String field;
    private IndexReader reader;
    private boolean skipInitExtractor;
    private boolean wrapToCaching;
    private int maxCharsToAnalyze;
    private boolean usePayloads;
    
    public QueryScorer(final Query query) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.init(query, null, null, true);
    }
    
    public QueryScorer(final Query query, final String field) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.init(query, field, null, true);
    }
    
    public QueryScorer(final Query query, final IndexReader reader, final String field) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.init(query, field, reader, true);
    }
    
    public QueryScorer(final Query query, final IndexReader reader, final String field, final String defaultField) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.defaultField = defaultField;
        this.init(query, field, reader, true);
    }
    
    public QueryScorer(final Query query, final String field, final String defaultField) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.defaultField = defaultField;
        this.init(query, field, null, true);
    }
    
    public QueryScorer(final WeightedSpanTerm[] weightedTerms) {
        this.position = -1;
        this.expandMultiTermQuery = true;
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.fieldWeightedSpanTerms = new HashMap<String, WeightedSpanTerm>(weightedTerms.length);
        for (int i = 0; i < weightedTerms.length; ++i) {
            final WeightedSpanTerm existingTerm = this.fieldWeightedSpanTerms.get(weightedTerms[i].term);
            if (existingTerm == null || existingTerm.weight < weightedTerms[i].weight) {
                this.fieldWeightedSpanTerms.put(weightedTerms[i].term, weightedTerms[i]);
                this.maxTermWeight = Math.max(this.maxTermWeight, weightedTerms[i].getWeight());
            }
        }
        this.skipInitExtractor = true;
    }
    
    @Override
    public float getFragmentScore() {
        return this.totalScore;
    }
    
    public float getMaxTermWeight() {
        return this.maxTermWeight;
    }
    
    @Override
    public float getTokenScore() {
        this.position += this.posIncAtt.getPositionIncrement();
        final String termText = this.termAtt.toString();
        final WeightedSpanTerm weightedSpanTerm;
        if ((weightedSpanTerm = this.fieldWeightedSpanTerms.get(termText)) == null) {
            return 0.0f;
        }
        if (weightedSpanTerm.positionSensitive && !weightedSpanTerm.checkPosition(this.position)) {
            return 0.0f;
        }
        final float score = weightedSpanTerm.getWeight();
        if (!this.foundTerms.contains(termText)) {
            this.totalScore += score;
            this.foundTerms.add(termText);
        }
        return score;
    }
    
    @Override
    public TokenStream init(final TokenStream tokenStream) throws IOException {
        this.position = -1;
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute((Class)CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)tokenStream.addAttribute((Class)PositionIncrementAttribute.class);
        if (!this.skipInitExtractor) {
            if (this.fieldWeightedSpanTerms != null) {
                this.fieldWeightedSpanTerms.clear();
            }
            return this.initExtractor(tokenStream);
        }
        return null;
    }
    
    public WeightedSpanTerm getWeightedSpanTerm(final String token) {
        return this.fieldWeightedSpanTerms.get(token);
    }
    
    private void init(final Query query, final String field, final IndexReader reader, final boolean expandMultiTermQuery) {
        this.reader = reader;
        this.expandMultiTermQuery = expandMultiTermQuery;
        this.query = query;
        this.field = field;
    }
    
    private TokenStream initExtractor(final TokenStream tokenStream) throws IOException {
        final WeightedSpanTermExtractor qse = this.newTermExtractor(this.defaultField);
        qse.setMaxDocCharsToAnalyze(this.maxCharsToAnalyze);
        qse.setExpandMultiTermQuery(this.expandMultiTermQuery);
        qse.setWrapIfNotCachingTokenFilter(this.wrapToCaching);
        qse.setUsePayloads(this.usePayloads);
        if (this.reader == null) {
            this.fieldWeightedSpanTerms = qse.getWeightedSpanTerms(this.query, 1.0f, tokenStream, this.field);
        }
        else {
            this.fieldWeightedSpanTerms = qse.getWeightedSpanTermsWithScores(this.query, 1.0f, tokenStream, this.field, this.reader);
        }
        if (qse.isCachedTokenStream()) {
            return qse.getTokenStream();
        }
        return null;
    }
    
    protected WeightedSpanTermExtractor newTermExtractor(final String defaultField) {
        return (defaultField == null) ? new WeightedSpanTermExtractor() : new WeightedSpanTermExtractor(defaultField);
    }
    
    @Override
    public void startFragment(final TextFragment newFragment) {
        this.foundTerms = new HashSet<String>();
        this.totalScore = 0.0f;
    }
    
    public boolean isExpandMultiTermQuery() {
        return this.expandMultiTermQuery;
    }
    
    public void setExpandMultiTermQuery(final boolean expandMultiTermQuery) {
        this.expandMultiTermQuery = expandMultiTermQuery;
    }
    
    public boolean isUsePayloads() {
        return this.usePayloads;
    }
    
    public void setUsePayloads(final boolean usePayloads) {
        this.usePayloads = usePayloads;
    }
    
    public void setWrapIfNotCachingTokenFilter(final boolean wrap) {
        this.wrapToCaching = wrap;
    }
    
    public void setMaxDocCharsToAnalyze(final int maxDocCharsToAnalyze) {
        this.maxCharsToAnalyze = maxDocCharsToAnalyze;
    }
}
