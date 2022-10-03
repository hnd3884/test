package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.HashMap;
import java.util.HashSet;

public class QueryTermScorer implements Scorer
{
    TextFragment currentTextFragment;
    HashSet<String> uniqueTermsInFragment;
    float totalScore;
    float maxTermWeight;
    private HashMap<String, WeightedTerm> termsToFind;
    private CharTermAttribute termAtt;
    
    public QueryTermScorer(final Query query) {
        this(QueryTermExtractor.getTerms(query));
    }
    
    public QueryTermScorer(final Query query, final String fieldName) {
        this(QueryTermExtractor.getTerms(query, false, fieldName));
    }
    
    public QueryTermScorer(final Query query, final IndexReader reader, final String fieldName) {
        this(QueryTermExtractor.getIdfWeightedTerms(query, reader, fieldName));
    }
    
    public QueryTermScorer(final WeightedTerm[] weightedTerms) {
        this.currentTextFragment = null;
        this.totalScore = 0.0f;
        this.maxTermWeight = 0.0f;
        this.termsToFind = new HashMap<String, WeightedTerm>();
        for (int i = 0; i < weightedTerms.length; ++i) {
            final WeightedTerm existingTerm = this.termsToFind.get(weightedTerms[i].term);
            if (existingTerm == null || existingTerm.weight < weightedTerms[i].weight) {
                this.termsToFind.put(weightedTerms[i].term, weightedTerms[i]);
                this.maxTermWeight = Math.max(this.maxTermWeight, weightedTerms[i].getWeight());
            }
        }
    }
    
    @Override
    public TokenStream init(final TokenStream tokenStream) {
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute((Class)CharTermAttribute.class);
        return null;
    }
    
    @Override
    public void startFragment(final TextFragment newFragment) {
        this.uniqueTermsInFragment = new HashSet<String>();
        this.currentTextFragment = newFragment;
        this.totalScore = 0.0f;
    }
    
    @Override
    public float getTokenScore() {
        final String termText = this.termAtt.toString();
        final WeightedTerm queryTerm = this.termsToFind.get(termText);
        if (queryTerm == null) {
            return 0.0f;
        }
        if (!this.uniqueTermsInFragment.contains(termText)) {
            this.totalScore += queryTerm.getWeight();
            this.uniqueTermsInFragment.add(termText);
        }
        return queryTerm.getWeight();
    }
    
    @Override
    public float getFragmentScore() {
        return this.totalScore;
    }
    
    public void allFragmentsProcessed() {
    }
    
    public float getMaxTermWeight() {
        return this.maxTermWeight;
    }
}
