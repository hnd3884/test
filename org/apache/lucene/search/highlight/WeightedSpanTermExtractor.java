package org.apache.lucene.search.highlight;

import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import java.util.Collections;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.FilterLeafReader;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.FieldMaskingSpanQuery;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.spans.SpanWeight;
import java.util.Set;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.search.IndexSearcher;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Iterator;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import java.util.List;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.join.ToChildBlockJoinQuery;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import java.util.Map;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.analysis.TokenStream;

public class WeightedSpanTermExtractor
{
    private String fieldName;
    private TokenStream tokenStream;
    private String defaultField;
    private boolean expandMultiTermQuery;
    private boolean cachedTokenStream;
    private boolean wrapToCaching;
    private int maxDocCharsToAnalyze;
    private boolean usePayloads;
    private LeafReader internalReader;
    
    public WeightedSpanTermExtractor() {
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.internalReader = null;
    }
    
    public WeightedSpanTermExtractor(final String defaultField) {
        this.wrapToCaching = true;
        this.usePayloads = false;
        this.internalReader = null;
        if (defaultField != null) {
            this.defaultField = defaultField;
        }
    }
    
    protected void extract(final Query query, final float boost, final Map<String, WeightedSpanTerm> terms) throws IOException {
        if (query instanceof BoostQuery) {
            final BoostQuery boostQuery = (BoostQuery)query;
            this.extract(boostQuery.getQuery(), boost * boostQuery.getBoost(), terms);
        }
        else if (query instanceof BooleanQuery) {
            for (final BooleanClause clause : (BooleanQuery)query) {
                if (!clause.isProhibited()) {
                    this.extract(clause.getQuery(), boost, terms);
                }
            }
        }
        else if (query instanceof PhraseQuery) {
            final PhraseQuery phraseQuery = (PhraseQuery)query;
            final Term[] phraseQueryTerms = phraseQuery.getTerms();
            if (phraseQueryTerms.length == 1) {
                this.extractWeightedSpanTerms(terms, (SpanQuery)new SpanTermQuery(phraseQueryTerms[0]), boost);
            }
            else {
                final SpanQuery[] clauses = new SpanQuery[phraseQueryTerms.length];
                for (int i = 0; i < phraseQueryTerms.length; ++i) {
                    clauses[i] = (SpanQuery)new SpanTermQuery(phraseQueryTerms[i]);
                }
                int positionGaps = 0;
                final int[] positions = phraseQuery.getPositions();
                if (positions.length >= 2) {
                    positionGaps = Math.max(0, positions[positions.length - 1] - positions[0] - positions.length + 1);
                }
                final boolean inorder = phraseQuery.getSlop() == 0;
                final SpanNearQuery sp = new SpanNearQuery(clauses, phraseQuery.getSlop() + positionGaps, inorder);
                this.extractWeightedSpanTerms(terms, (SpanQuery)sp, boost);
            }
        }
        else if (query instanceof TermQuery) {
            this.extractWeightedTerms(terms, query, boost);
        }
        else if (query instanceof SpanQuery) {
            this.extractWeightedSpanTerms(terms, (SpanQuery)query, boost);
        }
        else if (query instanceof FilteredQuery) {
            this.extract(((FilteredQuery)query).getQuery(), boost, terms);
        }
        else if (query instanceof ConstantScoreQuery) {
            final Query q = ((ConstantScoreQuery)query).getQuery();
            if (q != null) {
                this.extract(q, boost, terms);
            }
        }
        else if (query instanceof CommonTermsQuery) {
            this.extractWeightedTerms(terms, query, boost);
        }
        else if (query instanceof DisjunctionMaxQuery) {
            final Iterator<Query> iterator = ((DisjunctionMaxQuery)query).iterator();
            while (iterator.hasNext()) {
                this.extract(iterator.next(), boost, terms);
            }
        }
        else if (query instanceof ToParentBlockJoinQuery) {
            this.extract(((ToParentBlockJoinQuery)query).getChildQuery(), boost, terms);
        }
        else if (query instanceof ToChildBlockJoinQuery) {
            this.extract(((ToChildBlockJoinQuery)query).getParentQuery(), boost, terms);
        }
        else if (query instanceof MultiPhraseQuery) {
            final MultiPhraseQuery mpq = (MultiPhraseQuery)query;
            final List<Term[]> termArrays = mpq.getTermArrays();
            final int[] positions2 = mpq.getPositions();
            if (positions2.length > 0) {
                int maxPosition = positions2[positions2.length - 1];
                for (int j = 0; j < positions2.length - 1; ++j) {
                    if (positions2[j] > maxPosition) {
                        maxPosition = positions2[j];
                    }
                }
                final List<SpanQuery>[] disjunctLists = new List[maxPosition + 1];
                int distinctPositions = 0;
                for (int k = 0; k < termArrays.size(); ++k) {
                    final Term[] termArray = termArrays.get(k);
                    List<SpanQuery> disjuncts = disjunctLists[positions2[k]];
                    if (disjuncts == null) {
                        final List<SpanQuery>[] array = disjunctLists;
                        final int n = positions2[k];
                        final List<SpanQuery> list = new ArrayList<SpanQuery>(termArray.length);
                        array[n] = list;
                        disjuncts = list;
                        ++distinctPositions;
                    }
                    for (int l = 0; l < termArray.length; ++l) {
                        disjuncts.add((SpanQuery)new SpanTermQuery(termArray[l]));
                    }
                }
                int positionGaps2 = 0;
                int position = 0;
                final SpanQuery[] clauses2 = new SpanQuery[distinctPositions];
                for (int m = 0; m < disjunctLists.length; ++m) {
                    final List<SpanQuery> disjuncts2 = disjunctLists[m];
                    if (disjuncts2 != null) {
                        clauses2[position++] = (SpanQuery)new SpanOrQuery((SpanQuery[])disjuncts2.toArray(new SpanQuery[disjuncts2.size()]));
                    }
                    else {
                        ++positionGaps2;
                    }
                }
                final int slop = mpq.getSlop();
                final boolean inorder2 = slop == 0;
                final SpanNearQuery sp2 = new SpanNearQuery(clauses2, slop + positionGaps2, inorder2);
                this.extractWeightedSpanTerms(terms, (SpanQuery)sp2, boost);
            }
        }
        else if (!(query instanceof MatchAllDocsQuery)) {
            if (query instanceof CustomScoreQuery) {
                this.extract(((CustomScoreQuery)query).getSubQuery(), boost, terms);
            }
            else {
                final Query origQuery = query;
                final IndexReader reader = (IndexReader)this.getLeafContext().reader();
                Query rewritten;
                if (query instanceof MultiTermQuery) {
                    if (!this.expandMultiTermQuery) {
                        return;
                    }
                    rewritten = MultiTermQuery.SCORING_BOOLEAN_REWRITE.rewrite(reader, (MultiTermQuery)query);
                }
                else {
                    rewritten = origQuery.rewrite(reader);
                }
                if (rewritten != origQuery) {
                    this.extract(rewritten, boost, terms);
                }
                else {
                    this.extractUnknownQuery(query, terms);
                }
            }
        }
    }
    
    protected void extractUnknownQuery(final Query query, final Map<String, WeightedSpanTerm> terms) throws IOException {
    }
    
    protected void extractWeightedSpanTerms(final Map<String, WeightedSpanTerm> terms, final SpanQuery spanQuery, final float boost) throws IOException {
        Set<String> fieldNames;
        if (this.fieldName == null) {
            fieldNames = new HashSet<String>();
            this.collectSpanQueryFields(spanQuery, fieldNames);
        }
        else {
            fieldNames = new HashSet<String>(1);
            fieldNames.add(this.fieldName);
        }
        if (this.defaultField != null) {
            fieldNames.add(this.defaultField);
        }
        final Map<String, SpanQuery> queries = new HashMap<String, SpanQuery>();
        final Set<Term> nonWeightedTerms = new HashSet<Term>();
        final boolean mustRewriteQuery = this.mustRewriteQuery(spanQuery);
        final IndexSearcher searcher = new IndexSearcher((IndexReaderContext)this.getLeafContext());
        searcher.setQueryCache((QueryCache)null);
        if (mustRewriteQuery) {
            for (final String field : fieldNames) {
                final SpanQuery rewrittenQuery = (SpanQuery)spanQuery.rewrite((IndexReader)this.getLeafContext().reader());
                queries.put(field, rewrittenQuery);
                rewrittenQuery.createWeight(searcher, false).extractTerms((Set)nonWeightedTerms);
            }
        }
        else {
            spanQuery.createWeight(searcher, false).extractTerms((Set)nonWeightedTerms);
        }
        final List<PositionSpan> spanPositions = new ArrayList<PositionSpan>();
        for (final String field2 : fieldNames) {
            SpanQuery q;
            if (mustRewriteQuery) {
                q = queries.get(field2);
            }
            else {
                q = spanQuery;
            }
            final LeafReaderContext context = this.getLeafContext();
            final SpanWeight w = (SpanWeight)searcher.createNormalizedWeight((Query)q, false);
            final Bits acceptDocs = context.reader().getLiveDocs();
            final Spans spans = w.getSpans(context, SpanWeight.Postings.POSITIONS);
            if (spans == null) {
                return;
            }
            while (spans.nextDoc() != Integer.MAX_VALUE) {
                if (acceptDocs != null && !acceptDocs.get(spans.docID())) {
                    continue;
                }
                while (spans.nextStartPosition() != Integer.MAX_VALUE) {
                    spanPositions.add(new PositionSpan(spans.startPosition(), spans.endPosition() - 1));
                }
            }
        }
        if (spanPositions.size() == 0) {
            return;
        }
        for (final Term queryTerm : nonWeightedTerms) {
            if (this.fieldNameComparator(queryTerm.field())) {
                WeightedSpanTerm weightedSpanTerm = terms.get(queryTerm.text());
                if (weightedSpanTerm == null) {
                    weightedSpanTerm = new WeightedSpanTerm(boost, queryTerm.text());
                    weightedSpanTerm.addPositionSpans(spanPositions);
                    weightedSpanTerm.positionSensitive = true;
                    terms.put(queryTerm.text(), weightedSpanTerm);
                }
                else {
                    if (spanPositions.size() <= 0) {
                        continue;
                    }
                    weightedSpanTerm.addPositionSpans(spanPositions);
                }
            }
        }
    }
    
    protected void extractWeightedTerms(final Map<String, WeightedSpanTerm> terms, final Query query, final float boost) throws IOException {
        final Set<Term> nonWeightedTerms = new HashSet<Term>();
        final IndexSearcher searcher = new IndexSearcher((IndexReaderContext)this.getLeafContext());
        searcher.createNormalizedWeight(query, false).extractTerms((Set)nonWeightedTerms);
        for (final Term queryTerm : nonWeightedTerms) {
            if (this.fieldNameComparator(queryTerm.field())) {
                final WeightedSpanTerm weightedSpanTerm = new WeightedSpanTerm(boost, queryTerm.text());
                terms.put(queryTerm.text(), weightedSpanTerm);
            }
        }
    }
    
    protected boolean fieldNameComparator(final String fieldNameToCheck) {
        final boolean rv = this.fieldName == null || this.fieldName.equals(fieldNameToCheck) || (this.defaultField != null && this.defaultField.equals(fieldNameToCheck));
        return rv;
    }
    
    protected LeafReaderContext getLeafContext() throws IOException {
        if (this.internalReader == null) {
            boolean cacheIt = this.wrapToCaching && !(this.tokenStream instanceof CachingTokenFilter);
            if (this.tokenStream instanceof TokenStreamFromTermVector) {
                cacheIt = false;
                final Terms termVectorTerms = ((TokenStreamFromTermVector)this.tokenStream).getTermVectorTerms();
                if (termVectorTerms.hasPositions() && termVectorTerms.hasOffsets()) {
                    this.internalReader = new TermVectorLeafReader("shadowed_field", termVectorTerms);
                }
            }
            if (this.internalReader == null) {
                final MemoryIndex indexer = new MemoryIndex(true, this.usePayloads);
                if (cacheIt) {
                    assert !this.cachedTokenStream;
                    this.tokenStream = (TokenStream)new CachingTokenFilter((TokenStream)new OffsetLimitTokenFilter(this.tokenStream, this.maxDocCharsToAnalyze));
                    this.cachedTokenStream = true;
                    indexer.addField("shadowed_field", this.tokenStream);
                }
                else {
                    indexer.addField("shadowed_field", (TokenStream)new OffsetLimitTokenFilter(this.tokenStream, this.maxDocCharsToAnalyze));
                }
                final IndexSearcher searcher = indexer.createSearcher();
                this.internalReader = ((LeafReaderContext)searcher.getTopReaderContext()).reader();
            }
            this.internalReader = (LeafReader)new DelegatingLeafReader(this.internalReader);
        }
        return this.internalReader.getContext();
    }
    
    public Map<String, WeightedSpanTerm> getWeightedSpanTerms(final Query query, final float boost, final TokenStream tokenStream) throws IOException {
        return this.getWeightedSpanTerms(query, boost, tokenStream, null);
    }
    
    public Map<String, WeightedSpanTerm> getWeightedSpanTerms(final Query query, final float boost, final TokenStream tokenStream, final String fieldName) throws IOException {
        if (fieldName != null) {
            this.fieldName = fieldName;
        }
        else {
            this.fieldName = null;
        }
        final Map<String, WeightedSpanTerm> terms = (Map<String, WeightedSpanTerm>)new PositionCheckingMap();
        this.tokenStream = tokenStream;
        try {
            this.extract(query, boost, terms);
        }
        finally {
            IOUtils.close(new Closeable[] { (Closeable)this.internalReader });
        }
        return terms;
    }
    
    public Map<String, WeightedSpanTerm> getWeightedSpanTermsWithScores(final Query query, final float boost, final TokenStream tokenStream, final String fieldName, final IndexReader reader) throws IOException {
        if (fieldName != null) {
            this.fieldName = fieldName;
        }
        else {
            this.fieldName = null;
        }
        this.tokenStream = tokenStream;
        final Map<String, WeightedSpanTerm> terms = (Map<String, WeightedSpanTerm>)new PositionCheckingMap();
        this.extract(query, boost, terms);
        final int totalNumDocs = reader.maxDoc();
        final Set<String> weightedTerms = terms.keySet();
        final Iterator<String> it = weightedTerms.iterator();
        try {
            while (it.hasNext()) {
                final WeightedSpanTerm weightedSpanTerm = terms.get(it.next());
                final int docFreq = reader.docFreq(new Term(fieldName, weightedSpanTerm.term));
                final float idf = (float)(Math.log(totalNumDocs / (double)(docFreq + 1)) + 1.0);
                final WeightedSpanTerm weightedSpanTerm2 = weightedSpanTerm;
                weightedSpanTerm2.weight *= idf;
            }
        }
        finally {
            IOUtils.close(new Closeable[] { (Closeable)this.internalReader });
        }
        return terms;
    }
    
    protected void collectSpanQueryFields(final SpanQuery spanQuery, final Set<String> fieldNames) {
        if (spanQuery instanceof FieldMaskingSpanQuery) {
            this.collectSpanQueryFields(((FieldMaskingSpanQuery)spanQuery).getMaskedQuery(), fieldNames);
        }
        else if (spanQuery instanceof SpanFirstQuery) {
            this.collectSpanQueryFields(((SpanFirstQuery)spanQuery).getMatch(), fieldNames);
        }
        else if (spanQuery instanceof SpanNearQuery) {
            for (final SpanQuery clause : ((SpanNearQuery)spanQuery).getClauses()) {
                this.collectSpanQueryFields(clause, fieldNames);
            }
        }
        else if (spanQuery instanceof SpanNotQuery) {
            this.collectSpanQueryFields(((SpanNotQuery)spanQuery).getInclude(), fieldNames);
        }
        else if (spanQuery instanceof SpanOrQuery) {
            for (final SpanQuery clause : ((SpanOrQuery)spanQuery).getClauses()) {
                this.collectSpanQueryFields(clause, fieldNames);
            }
        }
        else {
            fieldNames.add(spanQuery.getField());
        }
    }
    
    protected boolean mustRewriteQuery(final SpanQuery spanQuery) {
        if (!this.expandMultiTermQuery) {
            return false;
        }
        if (spanQuery instanceof FieldMaskingSpanQuery) {
            return this.mustRewriteQuery(((FieldMaskingSpanQuery)spanQuery).getMaskedQuery());
        }
        if (spanQuery instanceof SpanFirstQuery) {
            return this.mustRewriteQuery(((SpanFirstQuery)spanQuery).getMatch());
        }
        if (spanQuery instanceof SpanNearQuery) {
            for (final SpanQuery clause : ((SpanNearQuery)spanQuery).getClauses()) {
                if (this.mustRewriteQuery(clause)) {
                    return true;
                }
            }
            return false;
        }
        if (spanQuery instanceof SpanNotQuery) {
            final SpanNotQuery spanNotQuery = (SpanNotQuery)spanQuery;
            return this.mustRewriteQuery(spanNotQuery.getInclude()) || this.mustRewriteQuery(spanNotQuery.getExclude());
        }
        if (spanQuery instanceof SpanOrQuery) {
            for (final SpanQuery clause : ((SpanOrQuery)spanQuery).getClauses()) {
                if (this.mustRewriteQuery(clause)) {
                    return true;
                }
            }
            return false;
        }
        return !(spanQuery instanceof SpanTermQuery);
    }
    
    public boolean getExpandMultiTermQuery() {
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
    
    public boolean isCachedTokenStream() {
        return this.cachedTokenStream;
    }
    
    public TokenStream getTokenStream() {
        assert this.tokenStream != null;
        return this.tokenStream;
    }
    
    public void setWrapIfNotCachingTokenFilter(final boolean wrap) {
        this.wrapToCaching = wrap;
    }
    
    protected final void setMaxDocCharsToAnalyze(final int maxDocCharsToAnalyze) {
        this.maxDocCharsToAnalyze = maxDocCharsToAnalyze;
    }
    
    static final class DelegatingLeafReader extends FilterLeafReader
    {
        private static final String FIELD_NAME = "shadowed_field";
        
        DelegatingLeafReader(final LeafReader in) {
            super(in);
        }
        
        public FieldInfos getFieldInfos() {
            throw new UnsupportedOperationException();
        }
        
        public Fields fields() throws IOException {
            return (Fields)new FilterLeafReader.FilterFields(super.fields()) {
                public Terms terms(final String field) throws IOException {
                    return super.terms("shadowed_field");
                }
                
                public Iterator<String> iterator() {
                    return Collections.singletonList("shadowed_field").iterator();
                }
                
                public int size() {
                    return 1;
                }
            };
        }
        
        public NumericDocValues getNumericDocValues(final String field) throws IOException {
            return super.getNumericDocValues("shadowed_field");
        }
        
        public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
            return super.getBinaryDocValues("shadowed_field");
        }
        
        public SortedDocValues getSortedDocValues(final String field) throws IOException {
            return super.getSortedDocValues("shadowed_field");
        }
        
        public NumericDocValues getNormValues(final String field) throws IOException {
            return super.getNormValues("shadowed_field");
        }
        
        public Bits getDocsWithField(final String field) throws IOException {
            return super.getDocsWithField("shadowed_field");
        }
    }
    
    protected static class PositionCheckingMap<K> extends HashMap<K, WeightedSpanTerm>
    {
        @Override
        public void putAll(final Map<? extends K, ? extends WeightedSpanTerm> m) {
            for (final Map.Entry<? extends K, ? extends WeightedSpanTerm> entry : m.entrySet()) {
                this.put(entry.getKey(), (WeightedSpanTerm)entry.getValue());
            }
        }
        
        @Override
        public WeightedSpanTerm put(final K key, final WeightedSpanTerm value) {
            final WeightedSpanTerm prev = super.put(key, value);
            if (prev == null) {
                return prev;
            }
            final WeightedSpanTerm prevTerm = prev;
            final WeightedSpanTerm newTerm = value;
            if (!prevTerm.positionSensitive) {
                newTerm.positionSensitive = false;
            }
            return prev;
        }
    }
}
