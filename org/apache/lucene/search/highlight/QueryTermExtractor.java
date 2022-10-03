package org.apache.lucene.search.highlight;

import org.apache.lucene.search.QueryCache;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.BooleanClause;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import java.util.HashSet;
import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;

public final class QueryTermExtractor
{
    private static final IndexSearcher EMPTY_INDEXSEARCHER;
    
    public static final WeightedTerm[] getTerms(final Query query) {
        return getTerms(query, false);
    }
    
    public static final WeightedTerm[] getIdfWeightedTerms(final Query query, final IndexReader reader, final String fieldName) {
        final WeightedTerm[] terms = getTerms(query, false, fieldName);
        final int totalNumDocs = reader.maxDoc();
        for (int i = 0; i < terms.length; ++i) {
            try {
                final int docFreq = reader.docFreq(new Term(fieldName, terms[i].term));
                final float idf = (float)(Math.log(totalNumDocs / (double)(docFreq + 1)) + 1.0);
                final WeightedTerm weightedTerm = terms[i];
                weightedTerm.weight *= idf;
            }
            catch (final IOException ex) {}
        }
        return terms;
    }
    
    public static final WeightedTerm[] getTerms(final Query query, final boolean prohibited, final String fieldName) {
        final HashSet<WeightedTerm> terms = new HashSet<WeightedTerm>();
        getTerms(query, 1.0f, terms, prohibited, fieldName);
        return terms.toArray(new WeightedTerm[0]);
    }
    
    public static final WeightedTerm[] getTerms(final Query query, final boolean prohibited) {
        return getTerms(query, prohibited, null);
    }
    
    private static final void getTerms(final Query query, final float boost, final HashSet<WeightedTerm> terms, final boolean prohibited, final String fieldName) {
        try {
            if (query instanceof BoostQuery) {
                final BoostQuery boostQuery = (BoostQuery)query;
                getTerms(boostQuery.getQuery(), boost * boostQuery.getBoost(), terms, prohibited, fieldName);
            }
            else if (query instanceof BooleanQuery) {
                getTermsFromBooleanQuery((BooleanQuery)query, boost, terms, prohibited, fieldName);
            }
            else if (query instanceof FilteredQuery) {
                getTermsFromFilteredQuery((FilteredQuery)query, boost, terms, prohibited, fieldName);
            }
            else {
                final HashSet<Term> nonWeightedTerms = new HashSet<Term>();
                try {
                    QueryTermExtractor.EMPTY_INDEXSEARCHER.createNormalizedWeight(query, false).extractTerms((Set)nonWeightedTerms);
                }
                catch (final IOException bogus) {
                    throw new RuntimeException("Should not happen on an empty index", bogus);
                }
                for (final Term term : nonWeightedTerms) {
                    if (fieldName == null || term.field().equals(fieldName)) {
                        terms.add(new WeightedTerm(boost, term.text()));
                    }
                }
            }
        }
        catch (final UnsupportedOperationException ex) {}
    }
    
    private static final void getTermsFromBooleanQuery(final BooleanQuery query, final float boost, final HashSet<WeightedTerm> terms, final boolean prohibited, final String fieldName) {
        for (final BooleanClause clause : query) {
            if (prohibited || clause.getOccur() != BooleanClause.Occur.MUST_NOT) {
                getTerms(clause.getQuery(), boost, terms, prohibited, fieldName);
            }
        }
    }
    
    private static void getTermsFromFilteredQuery(final FilteredQuery query, final float boost, final HashSet<WeightedTerm> terms, final boolean prohibited, final String fieldName) {
        getTerms(query.getQuery(), boost, terms, prohibited, fieldName);
    }
    
    static {
        try {
            final IndexReader emptyReader = (IndexReader)new MultiReader(new IndexReader[0]);
            (EMPTY_INDEXSEARCHER = new IndexSearcher(emptyReader)).setQueryCache((QueryCache)null);
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
    }
}
