package org.apache.lucene.analysis.query;

import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import org.apache.lucene.index.MultiFields;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;

public final class QueryAutoStopWordAnalyzer extends AnalyzerWrapper
{
    private final Analyzer delegate;
    private final Map<String, Set<String>> stopWordsPerField;
    public static final float defaultMaxDocFreqPercent = 0.4f;
    
    public QueryAutoStopWordAnalyzer(final Analyzer delegate, final IndexReader indexReader) throws IOException {
        this(delegate, indexReader, 0.4f);
    }
    
    public QueryAutoStopWordAnalyzer(final Analyzer delegate, final IndexReader indexReader, final int maxDocFreq) throws IOException {
        this(delegate, indexReader, MultiFields.getIndexedFields(indexReader), maxDocFreq);
    }
    
    public QueryAutoStopWordAnalyzer(final Analyzer delegate, final IndexReader indexReader, final float maxPercentDocs) throws IOException {
        this(delegate, indexReader, MultiFields.getIndexedFields(indexReader), maxPercentDocs);
    }
    
    public QueryAutoStopWordAnalyzer(final Analyzer delegate, final IndexReader indexReader, final Collection<String> fields, final float maxPercentDocs) throws IOException {
        this(delegate, indexReader, fields, (int)(indexReader.numDocs() * maxPercentDocs));
    }
    
    public QueryAutoStopWordAnalyzer(final Analyzer delegate, final IndexReader indexReader, final Collection<String> fields, final int maxDocFreq) throws IOException {
        super(delegate.getReuseStrategy());
        this.stopWordsPerField = new HashMap<String, Set<String>>();
        this.delegate = delegate;
        for (final String field : fields) {
            final Set<String> stopWords = new HashSet<String>();
            final Terms terms = MultiFields.getTerms(indexReader, field);
            final CharsRefBuilder spare = new CharsRefBuilder();
            if (terms != null) {
                final TermsEnum te = terms.iterator();
                BytesRef text;
                while ((text = te.next()) != null) {
                    if (te.docFreq() > maxDocFreq) {
                        spare.copyUTF8Bytes(text);
                        stopWords.add(spare.toString());
                    }
                }
            }
            this.stopWordsPerField.put(field, stopWords);
        }
    }
    
    protected Analyzer getWrappedAnalyzer(final String fieldName) {
        return this.delegate;
    }
    
    protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
        final Set<String> stopWords = this.stopWordsPerField.get(fieldName);
        if (stopWords == null) {
            return components;
        }
        final StopFilter stopFilter = new StopFilter(components.getTokenStream(), new CharArraySet(stopWords, false));
        return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)stopFilter);
    }
    
    public String[] getStopWords(final String fieldName) {
        final Set<String> stopWords = this.stopWordsPerField.get(fieldName);
        return (stopWords != null) ? stopWords.toArray(new String[stopWords.size()]) : new String[0];
    }
    
    public Term[] getStopWords() {
        final List<Term> allStopWords = new ArrayList<Term>();
        for (final String fieldName : this.stopWordsPerField.keySet()) {
            final Set<String> stopWords = this.stopWordsPerField.get(fieldName);
            for (final String text : stopWords) {
                allStopWords.add(new Term(fieldName, text));
            }
        }
        return allStopWords.toArray(new Term[allStopWords.size()]);
    }
}
