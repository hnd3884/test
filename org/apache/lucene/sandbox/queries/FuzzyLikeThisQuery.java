package org.apache.lucene.sandbox.queries;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.BooleanClause;
import java.util.HashMap;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.TermsEnum;
import java.util.Iterator;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.TermQuery;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.MaxNonCompetitiveBoostAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Term;
import java.util.HashSet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import java.util.ArrayList;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.Query;

public class FuzzyLikeThisQuery extends Query
{
    static TFIDFSimilarity sim;
    ArrayList<FieldVals> fieldVals;
    Analyzer analyzer;
    int MAX_VARIANTS_PER_TERM;
    boolean ignoreTF;
    private int maxNumTerms;
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.analyzer == null) ? 0 : this.analyzer.hashCode());
        result = 31 * result + ((this.fieldVals == null) ? 0 : this.fieldVals.hashCode());
        result = 31 * result + (this.ignoreTF ? 1231 : 1237);
        result = 31 * result + this.maxNumTerms;
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
        final FuzzyLikeThisQuery other = (FuzzyLikeThisQuery)obj;
        if (this.analyzer == null) {
            if (other.analyzer != null) {
                return false;
            }
        }
        else if (!this.analyzer.equals(other.analyzer)) {
            return false;
        }
        if (this.fieldVals == null) {
            if (other.fieldVals != null) {
                return false;
            }
        }
        else if (!this.fieldVals.equals(other.fieldVals)) {
            return false;
        }
        return this.ignoreTF == other.ignoreTF && this.maxNumTerms == other.maxNumTerms;
    }
    
    public FuzzyLikeThisQuery(final int maxNumTerms, final Analyzer analyzer) {
        this.fieldVals = new ArrayList<FieldVals>();
        this.MAX_VARIANTS_PER_TERM = 50;
        this.ignoreTF = false;
        this.analyzer = analyzer;
        this.maxNumTerms = maxNumTerms;
    }
    
    public void addTerms(final String queryString, final String fieldName, final float minSimilarity, final int prefixLength) {
        this.fieldVals.add(new FieldVals(fieldName, minSimilarity, prefixLength, queryString));
    }
    
    private void addTerms(final IndexReader reader, final FieldVals f, final ScoreTermQueue q) throws IOException {
        if (f.queryString == null) {
            return;
        }
        final Terms terms = MultiFields.getTerms(reader, f.fieldName);
        if (terms == null) {
            return;
        }
        try (final TokenStream ts = this.analyzer.tokenStream(f.fieldName, f.queryString)) {
            final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
            final int corpusNumDocs = reader.numDocs();
            final HashSet<String> processedTerms = new HashSet<String>();
            ts.reset();
            while (ts.incrementToken()) {
                final String term = termAtt.toString();
                if (!processedTerms.contains(term)) {
                    processedTerms.add(term);
                    final ScoreTermQueue variantsQ = new ScoreTermQueue(this.MAX_VARIANTS_PER_TERM);
                    float minScore = 0.0f;
                    final Term startTerm = new Term(f.fieldName, term);
                    final AttributeSource atts = new AttributeSource();
                    final MaxNonCompetitiveBoostAttribute maxBoostAtt = (MaxNonCompetitiveBoostAttribute)atts.addAttribute((Class)MaxNonCompetitiveBoostAttribute.class);
                    final SlowFuzzyTermsEnum fe = new SlowFuzzyTermsEnum(terms, atts, startTerm, f.minSimilarity, f.prefixLength);
                    int df = reader.docFreq(startTerm);
                    int numVariants = 0;
                    int totalVariantDocFreqs = 0;
                    final BoostAttribute boostAtt = (BoostAttribute)fe.attributes().addAttribute((Class)BoostAttribute.class);
                    BytesRef possibleMatch;
                    while ((possibleMatch = fe.next()) != null) {
                        ++numVariants;
                        totalVariantDocFreqs += fe.docFreq();
                        final float score = boostAtt.getBoost();
                        if (variantsQ.size() < this.MAX_VARIANTS_PER_TERM || score > minScore) {
                            final ScoreTerm st = new ScoreTerm(new Term(startTerm.field(), BytesRef.deepCopyOf(possibleMatch)), score, startTerm);
                            variantsQ.insertWithOverflow((Object)st);
                            minScore = ((ScoreTerm)variantsQ.top()).score;
                        }
                        maxBoostAtt.setMaxNonCompetitiveBoost((variantsQ.size() >= this.MAX_VARIANTS_PER_TERM) ? minScore : Float.NEGATIVE_INFINITY);
                    }
                    if (numVariants <= 0) {
                        continue;
                    }
                    final int avgDf = totalVariantDocFreqs / numVariants;
                    if (df == 0) {
                        df = avgDf;
                    }
                    for (int size = variantsQ.size(), i = 0; i < size; ++i) {
                        final ScoreTerm st2 = (ScoreTerm)variantsQ.pop();
                        st2.score = st2.score * st2.score * FuzzyLikeThisQuery.sim.idf((long)df, (long)corpusNumDocs);
                        q.insertWithOverflow((Object)st2);
                    }
                }
            }
            ts.end();
        }
    }
    
    private Query newTermQuery(final IndexReader reader, final Term term) throws IOException {
        if (this.ignoreTF) {
            return (Query)new ConstantScoreQuery((Query)new TermQuery(term));
        }
        final TermContext context = new TermContext(reader.getContext());
        for (final LeafReaderContext leafContext : reader.leaves()) {
            final Terms terms = leafContext.reader().terms(term.field());
            if (terms != null) {
                final TermsEnum termsEnum = terms.iterator();
                if (!termsEnum.seekExact(term.bytes())) {
                    continue;
                }
                final int freq = 1 - context.docFreq();
                context.register(termsEnum.termState(), leafContext.ord, freq, (long)freq);
            }
        }
        return (Query)new TermQuery(term, context);
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final ScoreTermQueue q = new ScoreTermQueue(this.maxNumTerms);
        for (final FieldVals f : this.fieldVals) {
            this.addTerms(reader, f, q);
        }
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        final HashMap<Term, ArrayList<ScoreTerm>> variantQueries = new HashMap<Term, ArrayList<ScoreTerm>>();
        for (int size = q.size(), i = 0; i < size; ++i) {
            final ScoreTerm st = (ScoreTerm)q.pop();
            ArrayList<ScoreTerm> l = variantQueries.get(st.fuzziedSourceTerm);
            if (l == null) {
                l = new ArrayList<ScoreTerm>();
                variantQueries.put(st.fuzziedSourceTerm, l);
            }
            l.add(st);
        }
        for (final ArrayList<ScoreTerm> variants : variantQueries.values()) {
            if (variants.size() == 1) {
                final ScoreTerm st2 = variants.get(0);
                final Query tq = this.newTermQuery(reader, st2.term);
                tq.setBoost(st2.score);
                bq.add(tq, BooleanClause.Occur.SHOULD);
            }
            else {
                final BooleanQuery.Builder termVariants = new BooleanQuery.Builder();
                termVariants.setDisableCoord(true);
                for (final ScoreTerm st3 : variants) {
                    final Query tq2 = this.newTermQuery(reader, st3.term);
                    tq2.setBoost(st3.score);
                    termVariants.add(tq2, BooleanClause.Occur.SHOULD);
                }
                bq.add((Query)termVariants.build(), BooleanClause.Occur.SHOULD);
            }
        }
        return (Query)bq.build();
    }
    
    public String toString(final String field) {
        return null;
    }
    
    public boolean isIgnoreTF() {
        return this.ignoreTF;
    }
    
    public void setIgnoreTF(final boolean ignoreTF) {
        this.ignoreTF = ignoreTF;
    }
    
    static {
        FuzzyLikeThisQuery.sim = (TFIDFSimilarity)new DefaultSimilarity();
    }
    
    class FieldVals
    {
        String queryString;
        String fieldName;
        float minSimilarity;
        int prefixLength;
        
        public FieldVals(final String name, final float similarity, final int length, final String queryString) {
            this.fieldName = name;
            this.minSimilarity = similarity;
            this.prefixLength = length;
            this.queryString = queryString;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.fieldName == null) ? 0 : this.fieldName.hashCode());
            result = 31 * result + Float.floatToIntBits(this.minSimilarity);
            result = 31 * result + this.prefixLength;
            result = 31 * result + ((this.queryString == null) ? 0 : this.queryString.hashCode());
            return result;
        }
        
        @Override
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
            final FieldVals other = (FieldVals)obj;
            if (this.fieldName == null) {
                if (other.fieldName != null) {
                    return false;
                }
            }
            else if (!this.fieldName.equals(other.fieldName)) {
                return false;
            }
            if (Float.floatToIntBits(this.minSimilarity) != Float.floatToIntBits(other.minSimilarity)) {
                return false;
            }
            if (this.prefixLength != other.prefixLength) {
                return false;
            }
            if (this.queryString == null) {
                if (other.queryString != null) {
                    return false;
                }
            }
            else if (!this.queryString.equals(other.queryString)) {
                return false;
            }
            return true;
        }
    }
    
    private static class ScoreTerm
    {
        public Term term;
        public float score;
        Term fuzziedSourceTerm;
        
        public ScoreTerm(final Term term, final float score, final Term fuzziedSourceTerm) {
            this.term = term;
            this.score = score;
            this.fuzziedSourceTerm = fuzziedSourceTerm;
        }
    }
    
    private static class ScoreTermQueue extends PriorityQueue<ScoreTerm>
    {
        public ScoreTermQueue(final int size) {
            super(size);
        }
        
        protected boolean lessThan(final ScoreTerm termA, final ScoreTerm termB) {
            if (termA.score == termB.score) {
                return termA.term.compareTo(termB.term) > 0;
            }
            return termA.score < termB.score;
        }
    }
}
