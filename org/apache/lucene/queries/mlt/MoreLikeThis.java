package org.apache.lucene.queries.mlt;

import java.util.ArrayList;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import java.io.StringReader;
import java.util.Iterator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.util.PriorityQueue;
import java.util.HashMap;
import java.io.Reader;
import java.util.Map;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.analysis.Analyzer;
import java.util.Set;

public final class MoreLikeThis
{
    public static final int DEFAULT_MAX_NUM_TOKENS_PARSED = 5000;
    public static final int DEFAULT_MIN_TERM_FREQ = 2;
    public static final int DEFAULT_MIN_DOC_FREQ = 5;
    public static final int DEFAULT_MAX_DOC_FREQ = Integer.MAX_VALUE;
    public static final boolean DEFAULT_BOOST = false;
    public static final String[] DEFAULT_FIELD_NAMES;
    public static final int DEFAULT_MIN_WORD_LENGTH = 0;
    public static final int DEFAULT_MAX_WORD_LENGTH = 0;
    public static final Set<?> DEFAULT_STOP_WORDS;
    private Set<?> stopWords;
    public static final int DEFAULT_MAX_QUERY_TERMS = 25;
    private Analyzer analyzer;
    private int minTermFreq;
    private int minDocFreq;
    private int maxDocFreq;
    private boolean boost;
    private String[] fieldNames;
    private int maxNumTokensParsed;
    private int minWordLen;
    private int maxWordLen;
    private int maxQueryTerms;
    private TFIDFSimilarity similarity;
    private final IndexReader ir;
    private float boostFactor;
    
    public float getBoostFactor() {
        return this.boostFactor;
    }
    
    public void setBoostFactor(final float boostFactor) {
        this.boostFactor = boostFactor;
    }
    
    public MoreLikeThis(final IndexReader ir) {
        this(ir, (TFIDFSimilarity)new DefaultSimilarity());
    }
    
    public MoreLikeThis(final IndexReader ir, final TFIDFSimilarity sim) {
        this.stopWords = MoreLikeThis.DEFAULT_STOP_WORDS;
        this.analyzer = null;
        this.minTermFreq = 2;
        this.minDocFreq = 5;
        this.maxDocFreq = Integer.MAX_VALUE;
        this.boost = false;
        this.fieldNames = MoreLikeThis.DEFAULT_FIELD_NAMES;
        this.maxNumTokensParsed = 5000;
        this.minWordLen = 0;
        this.maxWordLen = 0;
        this.maxQueryTerms = 25;
        this.boostFactor = 1.0f;
        this.ir = ir;
        this.similarity = sim;
    }
    
    public TFIDFSimilarity getSimilarity() {
        return this.similarity;
    }
    
    public void setSimilarity(final TFIDFSimilarity similarity) {
        this.similarity = similarity;
    }
    
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
    
    public void setAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    public int getMinTermFreq() {
        return this.minTermFreq;
    }
    
    public void setMinTermFreq(final int minTermFreq) {
        this.minTermFreq = minTermFreq;
    }
    
    public int getMinDocFreq() {
        return this.minDocFreq;
    }
    
    public void setMinDocFreq(final int minDocFreq) {
        this.minDocFreq = minDocFreq;
    }
    
    public int getMaxDocFreq() {
        return this.maxDocFreq;
    }
    
    public void setMaxDocFreq(final int maxFreq) {
        this.maxDocFreq = maxFreq;
    }
    
    public void setMaxDocFreqPct(final int maxPercentage) {
        this.maxDocFreq = maxPercentage * this.ir.numDocs() / 100;
    }
    
    public boolean isBoost() {
        return this.boost;
    }
    
    public void setBoost(final boolean boost) {
        this.boost = boost;
    }
    
    public String[] getFieldNames() {
        return this.fieldNames;
    }
    
    public void setFieldNames(final String[] fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    public int getMinWordLen() {
        return this.minWordLen;
    }
    
    public void setMinWordLen(final int minWordLen) {
        this.minWordLen = minWordLen;
    }
    
    public int getMaxWordLen() {
        return this.maxWordLen;
    }
    
    public void setMaxWordLen(final int maxWordLen) {
        this.maxWordLen = maxWordLen;
    }
    
    public void setStopWords(final Set<?> stopWords) {
        this.stopWords = stopWords;
    }
    
    public Set<?> getStopWords() {
        return this.stopWords;
    }
    
    public int getMaxQueryTerms() {
        return this.maxQueryTerms;
    }
    
    public void setMaxQueryTerms(final int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }
    
    public int getMaxNumTokensParsed() {
        return this.maxNumTokensParsed;
    }
    
    public void setMaxNumTokensParsed(final int i) {
        this.maxNumTokensParsed = i;
    }
    
    public Query like(final int docNum) throws IOException {
        if (this.fieldNames == null) {
            final Collection<String> fields = MultiFields.getIndexedFields(this.ir);
            this.fieldNames = fields.toArray(new String[fields.size()]);
        }
        return this.createQuery(this.retrieveTerms(docNum));
    }
    
    public Query like(final Map<String, Collection<Object>> filteredDocument) throws IOException {
        if (this.fieldNames == null) {
            final Collection<String> fields = MultiFields.getIndexedFields(this.ir);
            this.fieldNames = fields.toArray(new String[fields.size()]);
        }
        return this.createQuery(this.retrieveTerms(filteredDocument));
    }
    
    public Query like(final String fieldName, final Reader... readers) throws IOException {
        final Map<String, Int> words = new HashMap<String, Int>();
        for (final Reader r : readers) {
            this.addTermFrequencies(r, words, fieldName);
        }
        return this.createQuery(this.createQueue(words));
    }
    
    private Query createQuery(final PriorityQueue<ScoreTerm> q) {
        final BooleanQuery.Builder query = new BooleanQuery.Builder();
        float bestScore = -1.0f;
        ScoreTerm scoreTerm;
        while ((scoreTerm = (ScoreTerm)q.pop()) != null) {
            Query tq = (Query)new TermQuery(new Term(scoreTerm.topField, scoreTerm.word));
            if (this.boost) {
                if (bestScore == -1.0f) {
                    bestScore = scoreTerm.score;
                }
                final float myScore = scoreTerm.score;
                tq = (Query)new BoostQuery(tq, this.boostFactor * myScore / bestScore);
            }
            try {
                query.add(tq, BooleanClause.Occur.SHOULD);
            }
            catch (final BooleanQuery.TooManyClauses ignore) {
                break;
            }
        }
        return (Query)query.build();
    }
    
    private PriorityQueue<ScoreTerm> createQueue(final Map<String, Int> words) throws IOException {
        final int numDocs = this.ir.numDocs();
        final int limit = Math.min(this.maxQueryTerms, words.size());
        final FreqQ queue = new FreqQ(limit);
        for (final String word : words.keySet()) {
            final int tf = words.get(word).x;
            if (this.minTermFreq > 0 && tf < this.minTermFreq) {
                continue;
            }
            String topField = this.fieldNames[0];
            int docFreq = 0;
            for (final String fieldName : this.fieldNames) {
                final int freq = this.ir.docFreq(new Term(fieldName, word));
                topField = ((freq > docFreq) ? fieldName : topField);
                docFreq = ((freq > docFreq) ? freq : docFreq);
            }
            if (this.minDocFreq > 0 && docFreq < this.minDocFreq) {
                continue;
            }
            if (docFreq > this.maxDocFreq) {
                continue;
            }
            if (docFreq == 0) {
                continue;
            }
            final float idf = this.similarity.idf((long)docFreq, (long)numDocs);
            final float score = tf * idf;
            if (queue.size() < limit) {
                queue.add((Object)new ScoreTerm(word, topField, score, idf, docFreq, tf));
            }
            else {
                final ScoreTerm term = (ScoreTerm)queue.top();
                if (term.score >= score) {
                    continue;
                }
                term.update(word, topField, score, idf, docFreq, tf);
                queue.updateTop();
            }
        }
        return queue;
    }
    
    public String describeParams() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\t").append("maxQueryTerms  : ").append(this.maxQueryTerms).append("\n");
        sb.append("\t").append("minWordLen     : ").append(this.minWordLen).append("\n");
        sb.append("\t").append("maxWordLen     : ").append(this.maxWordLen).append("\n");
        sb.append("\t").append("fieldNames     : ");
        String delim = "";
        for (final String fieldName : this.fieldNames) {
            sb.append(delim).append(fieldName);
            delim = ", ";
        }
        sb.append("\n");
        sb.append("\t").append("boost          : ").append(this.boost).append("\n");
        sb.append("\t").append("minTermFreq    : ").append(this.minTermFreq).append("\n");
        sb.append("\t").append("minDocFreq     : ").append(this.minDocFreq).append("\n");
        return sb.toString();
    }
    
    private PriorityQueue<ScoreTerm> retrieveTerms(final int docNum) throws IOException {
        final Map<String, Int> termFreqMap = new HashMap<String, Int>();
        for (final String fieldName : this.fieldNames) {
            final Fields vectors = this.ir.getTermVectors(docNum);
            Terms vector;
            if (vectors != null) {
                vector = vectors.terms(fieldName);
            }
            else {
                vector = null;
            }
            if (vector == null) {
                final Document d = this.ir.document(docNum);
                final IndexableField[] arr$2;
                final IndexableField[] fields = arr$2 = d.getFields(fieldName);
                for (final IndexableField field : arr$2) {
                    final String stringValue = field.stringValue();
                    if (stringValue != null) {
                        this.addTermFrequencies(new StringReader(stringValue), termFreqMap, fieldName);
                    }
                }
            }
            else {
                this.addTermFrequencies(termFreqMap, vector);
            }
        }
        return this.createQueue(termFreqMap);
    }
    
    private PriorityQueue<ScoreTerm> retrieveTerms(final Map<String, Collection<Object>> fields) throws IOException {
        final HashMap<String, Int> termFreqMap = new HashMap<String, Int>();
        for (final String fieldName : this.fieldNames) {
            for (final String field : fields.keySet()) {
                final Collection<Object> fieldValues = fields.get(field);
                if (fieldValues == null) {
                    continue;
                }
                for (final Object fieldValue : fieldValues) {
                    if (fieldValue != null) {
                        this.addTermFrequencies(new StringReader(String.valueOf(fieldValue)), termFreqMap, fieldName);
                    }
                }
            }
        }
        return this.createQueue(termFreqMap);
    }
    
    private void addTermFrequencies(final Map<String, Int> termFreqMap, final Terms vector) throws IOException {
        final TermsEnum termsEnum = vector.iterator();
        final CharsRefBuilder spare = new CharsRefBuilder();
        BytesRef text;
        while ((text = termsEnum.next()) != null) {
            spare.copyUTF8Bytes(text);
            final String term = spare.toString();
            if (this.isNoiseWord(term)) {
                continue;
            }
            final int freq = (int)termsEnum.totalTermFreq();
            Int cnt = termFreqMap.get(term);
            if (cnt == null) {
                cnt = new Int();
                termFreqMap.put(term, cnt);
                cnt.x = freq;
            }
            else {
                final Int int1 = cnt;
                int1.x += freq;
            }
        }
    }
    
    private void addTermFrequencies(final Reader r, final Map<String, Int> termFreqMap, final String fieldName) throws IOException {
        if (this.analyzer == null) {
            throw new UnsupportedOperationException("To use MoreLikeThis without term vectors, you must provide an Analyzer");
        }
        try (final TokenStream ts = this.analyzer.tokenStream(fieldName, r)) {
            int tokenCount = 0;
            final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                final String word = termAtt.toString();
                if (++tokenCount > this.maxNumTokensParsed) {
                    break;
                }
                if (this.isNoiseWord(word)) {
                    continue;
                }
                final Int cnt = termFreqMap.get(word);
                if (cnt == null) {
                    termFreqMap.put(word, new Int());
                }
                else {
                    final Int int1 = cnt;
                    ++int1.x;
                }
            }
            ts.end();
        }
    }
    
    private boolean isNoiseWord(final String term) {
        final int len = term.length();
        return (this.minWordLen > 0 && len < this.minWordLen) || (this.maxWordLen > 0 && len > this.maxWordLen) || (this.stopWords != null && this.stopWords.contains(term));
    }
    
    private PriorityQueue<ScoreTerm> retrieveTerms(final Reader r, final String fieldName) throws IOException {
        final Map<String, Int> words = new HashMap<String, Int>();
        this.addTermFrequencies(r, words, fieldName);
        return this.createQueue(words);
    }
    
    public String[] retrieveInterestingTerms(final int docNum) throws IOException {
        final ArrayList<Object> al = new ArrayList<Object>(this.maxQueryTerms);
        final PriorityQueue<ScoreTerm> pq = this.retrieveTerms(docNum);
        int lim = this.maxQueryTerms;
        ScoreTerm scoreTerm;
        while ((scoreTerm = (ScoreTerm)pq.pop()) != null && lim-- > 0) {
            al.add(scoreTerm.word);
        }
        final String[] res = new String[al.size()];
        return al.toArray(res);
    }
    
    public String[] retrieveInterestingTerms(final Reader r, final String fieldName) throws IOException {
        final ArrayList<Object> al = new ArrayList<Object>(this.maxQueryTerms);
        final PriorityQueue<ScoreTerm> pq = this.retrieveTerms(r, fieldName);
        int lim = this.maxQueryTerms;
        ScoreTerm scoreTerm;
        while ((scoreTerm = (ScoreTerm)pq.pop()) != null && lim-- > 0) {
            al.add(scoreTerm.word);
        }
        final String[] res = new String[al.size()];
        return al.toArray(res);
    }
    
    static {
        DEFAULT_FIELD_NAMES = new String[] { "contents" };
        DEFAULT_STOP_WORDS = null;
    }
    
    private static class FreqQ extends PriorityQueue<ScoreTerm>
    {
        FreqQ(final int maxSize) {
            super(maxSize);
        }
        
        protected boolean lessThan(final ScoreTerm a, final ScoreTerm b) {
            return a.score < b.score;
        }
    }
    
    private static class ScoreTerm
    {
        String word;
        String topField;
        float score;
        float idf;
        int docFreq;
        int tf;
        
        ScoreTerm(final String word, final String topField, final float score, final float idf, final int docFreq, final int tf) {
            this.word = word;
            this.topField = topField;
            this.score = score;
            this.idf = idf;
            this.docFreq = docFreq;
            this.tf = tf;
        }
        
        void update(final String word, final String topField, final float score, final float idf, final int docFreq, final int tf) {
            this.word = word;
            this.topField = topField;
            this.score = score;
            this.idf = idf;
            this.docFreq = docFreq;
            this.tf = tf;
        }
    }
    
    private static class Int
    {
        int x;
        
        Int() {
            this.x = 1;
        }
    }
}
