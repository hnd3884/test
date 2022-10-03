package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.BinaryDocValues;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.FieldDoc;
import java.util.TreeSet;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import java.util.Map;
import java.util.List;
import org.apache.lucene.util.BytesRef;
import java.util.Set;
import org.apache.lucene.util.Version;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.suggest.Lookup;
import java.util.Comparator;

public class BlendedInfixSuggester extends AnalyzingInfixSuggester
{
    protected static double LINEAR_COEF;
    private Double exponent;
    public static int DEFAULT_NUM_FACTOR;
    private final int numFactor;
    private final BlenderType blenderType;
    private static Comparator<LookupResult> LOOKUP_COMP;
    
    public BlendedInfixSuggester(final Directory dir, final Analyzer analyzer) throws IOException {
        this(analyzer.getVersion(), dir, analyzer);
    }
    
    @Deprecated
    public BlendedInfixSuggester(final Version matchVersion, final Directory dir, final Analyzer analyzer) throws IOException {
        super(matchVersion, dir, analyzer);
        this.exponent = 2.0;
        this.blenderType = BlenderType.POSITION_LINEAR;
        this.numFactor = BlendedInfixSuggester.DEFAULT_NUM_FACTOR;
    }
    
    public BlendedInfixSuggester(final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final BlenderType blenderType, final int numFactor, final boolean commitOnBuild) throws IOException {
        this(indexAnalyzer.getVersion(), dir, indexAnalyzer, queryAnalyzer, minPrefixChars, blenderType, numFactor, commitOnBuild);
    }
    
    @Deprecated
    public BlendedInfixSuggester(final Version matchVersion, final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final BlenderType blenderType, final int numFactor, final boolean commitOnBuild) throws IOException {
        super(matchVersion, dir, indexAnalyzer, queryAnalyzer, minPrefixChars, commitOnBuild);
        this.exponent = 2.0;
        this.blenderType = blenderType;
        this.numFactor = numFactor;
    }
    
    public BlendedInfixSuggester(final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final BlenderType blenderType, final int numFactor, final Double exponent, final boolean commitOnBuild, final boolean allTermsRequired, final boolean highlight) throws IOException {
        super(dir, indexAnalyzer, queryAnalyzer, minPrefixChars, commitOnBuild, allTermsRequired, highlight);
        this.exponent = 2.0;
        this.blenderType = blenderType;
        this.numFactor = numFactor;
        if (exponent != null) {
            this.exponent = exponent;
        }
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) throws IOException {
        return super.lookup(key, contexts, onlyMorePopular, num);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return super.lookup(key, contexts, num, allTermsRequired, doHighlight);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Map<BytesRef, BooleanClause.Occur> contextInfo, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return super.lookup(key, contextInfo, num, allTermsRequired, doHighlight);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final BooleanQuery contextQuery, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return super.lookup(key, contextQuery, num * this.numFactor, allTermsRequired, doHighlight);
    }
    
    @Override
    protected FieldType getTextFieldType() {
        final FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setOmitNorms(true);
        return ft;
    }
    
    @Override
    protected List<LookupResult> createResults(final IndexSearcher searcher, final TopFieldDocs hits, final int num, final CharSequence key, final boolean doHighlight, final Set<String> matchedTokens, final String prefixToken) throws IOException {
        final BinaryDocValues textDV = MultiDocValues.getBinaryValues(searcher.getIndexReader(), "text");
        assert textDV != null;
        final BinaryDocValues payloadsDV = MultiDocValues.getBinaryValues(searcher.getIndexReader(), "payloads");
        final TreeSet<LookupResult> results = new TreeSet<LookupResult>(BlendedInfixSuggester.LOOKUP_COMP);
        final int actualNum = num / this.numFactor;
        for (int i = 0; i < hits.scoreDocs.length; ++i) {
            final FieldDoc fd = (FieldDoc)hits.scoreDocs[i];
            final String text = textDV.get(fd.doc).utf8ToString();
            final long weight = (long)fd.fields[0];
            BytesRef payload;
            if (payloadsDV != null) {
                payload = BytesRef.deepCopyOf(payloadsDV.get(fd.doc));
            }
            else {
                payload = null;
            }
            double coefficient;
            if (text.startsWith(key.toString())) {
                coefficient = 1.0;
            }
            else {
                coefficient = this.createCoefficient(searcher, fd.doc, matchedTokens, prefixToken);
            }
            final long score = (long)(weight * coefficient);
            LookupResult result;
            if (doHighlight) {
                result = new LookupResult(text, this.highlight(text, matchedTokens, prefixToken), score, payload);
            }
            else {
                result = new LookupResult(text, score, payload);
            }
            boundedTreeAdd(results, result, actualNum);
        }
        return new ArrayList<LookupResult>(results.descendingSet());
    }
    
    private static void boundedTreeAdd(final TreeSet<LookupResult> results, final LookupResult result, final int num) {
        if (results.size() >= num) {
            if (results.first().value >= result.value) {
                return;
            }
            results.pollFirst();
        }
        results.add(result);
    }
    
    private double createCoefficient(final IndexSearcher searcher, final int doc, final Set<String> matchedTokens, final String prefixToken) throws IOException {
        final Terms tv = searcher.getIndexReader().getTermVector(doc, "text");
        final TermsEnum it = tv.iterator();
        Integer position = Integer.MAX_VALUE;
        BytesRef term;
        while ((term = it.next()) != null) {
            final String docTerm = term.utf8ToString();
            if (matchedTokens.contains(docTerm) || (prefixToken != null && docTerm.startsWith(prefixToken))) {
                final PostingsEnum docPosEnum = it.postings((PostingsEnum)null, 56);
                docPosEnum.nextDoc();
                final int p = docPosEnum.nextPosition();
                if (p >= position) {
                    continue;
                }
                position = p;
            }
        }
        return this.calculateCoefficient(position);
    }
    
    protected double calculateCoefficient(final int position) {
        double coefficient = 0.0;
        switch (this.blenderType) {
            case POSITION_LINEAR: {
                coefficient = 1.0 - BlendedInfixSuggester.LINEAR_COEF * position;
                break;
            }
            case POSITION_RECIPROCAL: {
                coefficient = 1.0 / (position + 1);
                break;
            }
            case POSITION_EXPONENTIAL_RECIPROCAL: {
                coefficient = 1.0 / Math.pow(position + 1.0, this.exponent);
                break;
            }
            default: {
                coefficient = 1.0;
                break;
            }
        }
        return coefficient;
    }
    
    static {
        BlendedInfixSuggester.LINEAR_COEF = 0.1;
        BlendedInfixSuggester.DEFAULT_NUM_FACTOR = 10;
        BlendedInfixSuggester.LOOKUP_COMP = new LookUpComparator();
    }
    
    public enum BlenderType
    {
        CUSTOM, 
        POSITION_LINEAR, 
        POSITION_RECIPROCAL, 
        POSITION_EXPONENTIAL_RECIPROCAL;
    }
    
    private static class LookUpComparator implements Comparator<LookupResult>
    {
        @Override
        public int compare(final LookupResult o1, final LookupResult o2) {
            if (o1.value > o2.value) {
                return 1;
            }
            if (o1.value < o2.value) {
                return -1;
            }
            final int keyCompare = Lookup.CHARSEQUENCE_COMPARATOR.compare(o1.key, o2.key);
            if (keyCompare != 0) {
                return keyCompare;
            }
            if (o1.payload != null) {
                return o1.payload.compareTo(o2.payload);
            }
            return 0;
        }
    }
}
