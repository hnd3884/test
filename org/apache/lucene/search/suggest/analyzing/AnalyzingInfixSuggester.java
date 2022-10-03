package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.search.SortField;
import java.util.Collections;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.FilterLeafReader;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.search.FieldDoc;
import java.util.ArrayList;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.EarlyTerminatingSortingCollector;
import org.apache.lucene.search.TopFieldCollector;
import java.util.HashSet;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.search.BooleanClause;
import java.util.Map;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import java.util.List;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.document.TextField;
import java.util.Iterator;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.Lucene43EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Path;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.SortingMergePolicy;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.DirectoryReader;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.io.Closeable;
import org.apache.lucene.search.suggest.Lookup;

public class AnalyzingInfixSuggester extends Lookup implements Closeable
{
    protected static final String TEXT_FIELD_NAME = "text";
    protected static final String EXACT_TEXT_FIELD_NAME = "exacttext";
    protected static final String CONTEXTS_FIELD_NAME = "contexts";
    protected final Analyzer queryAnalyzer;
    protected final Analyzer indexAnalyzer;
    final Version matchVersion;
    private final Directory dir;
    final int minPrefixChars;
    private final boolean allTermsRequired;
    private final boolean highlight;
    private final boolean commitOnBuild;
    private IndexWriter writer;
    protected SearcherManager searcherMgr;
    public static final int DEFAULT_MIN_PREFIX_CHARS = 4;
    public static final boolean DEFAULT_ALL_TERMS_REQUIRED = true;
    public static final boolean DEFAULT_HIGHLIGHT = true;
    private static final Sort SORT;
    
    public AnalyzingInfixSuggester(final Directory dir, final Analyzer analyzer) throws IOException {
        this(analyzer.getVersion(), dir, analyzer, analyzer, 4, false, true, true);
    }
    
    @Deprecated
    public AnalyzingInfixSuggester(final Version matchVersion, final Directory dir, final Analyzer analyzer) throws IOException {
        this(matchVersion, dir, analyzer, analyzer, 4, false);
    }
    
    public AnalyzingInfixSuggester(final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final boolean commitOnBuild) throws IOException {
        this(indexAnalyzer.getVersion(), dir, indexAnalyzer, queryAnalyzer, minPrefixChars, commitOnBuild, true, true);
    }
    
    public AnalyzingInfixSuggester(final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final boolean commitOnBuild, final boolean allTermsRequired, final boolean highlight) throws IOException {
        this(indexAnalyzer.getVersion(), dir, indexAnalyzer, queryAnalyzer, minPrefixChars, commitOnBuild, allTermsRequired, highlight);
    }
    
    @Deprecated
    public AnalyzingInfixSuggester(final Version matchVersion, final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final boolean commitOnBuild) throws IOException {
        this(matchVersion, dir, indexAnalyzer, queryAnalyzer, minPrefixChars, commitOnBuild, true, true);
    }
    
    @Deprecated
    public AnalyzingInfixSuggester(final Version matchVersion, final Directory dir, final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int minPrefixChars, final boolean commitOnBuild, final boolean allTermsRequired, final boolean highlight) throws IOException {
        if (minPrefixChars < 0) {
            throw new IllegalArgumentException("minPrefixChars must be >= 0; got: " + minPrefixChars);
        }
        this.queryAnalyzer = queryAnalyzer;
        this.indexAnalyzer = indexAnalyzer;
        this.matchVersion = matchVersion;
        this.dir = dir;
        this.minPrefixChars = minPrefixChars;
        this.commitOnBuild = commitOnBuild;
        this.allTermsRequired = allTermsRequired;
        this.highlight = highlight;
        if (DirectoryReader.indexExists(dir)) {
            this.writer = new IndexWriter(dir, this.getIndexWriterConfig(this.getGramAnalyzer(), IndexWriterConfig.OpenMode.APPEND));
            this.searcherMgr = new SearcherManager(this.writer, (SearcherFactory)null);
        }
    }
    
    protected IndexWriterConfig getIndexWriterConfig(final Analyzer indexAnalyzer, final IndexWriterConfig.OpenMode openMode) {
        final IndexWriterConfig iwc = new IndexWriterConfig(indexAnalyzer);
        iwc.setOpenMode(openMode);
        iwc.setMergePolicy((MergePolicy)new SortingMergePolicy(iwc.getMergePolicy(), AnalyzingInfixSuggester.SORT));
        return iwc;
    }
    
    protected Directory getDirectory(final Path path) throws IOException {
        return (Directory)FSDirectory.open(path);
    }
    
    @Override
    public void build(final InputIterator iter) throws IOException {
        if (this.searcherMgr != null) {
            this.searcherMgr.close();
            this.searcherMgr = null;
        }
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
        boolean success = false;
        try {
            this.writer = new IndexWriter(this.dir, this.getIndexWriterConfig(this.getGramAnalyzer(), IndexWriterConfig.OpenMode.CREATE));
            BytesRef text;
            while ((text = iter.next()) != null) {
                BytesRef payload;
                if (iter.hasPayloads()) {
                    payload = iter.payload();
                }
                else {
                    payload = null;
                }
                this.add(text, iter.contexts(), iter.weight(), payload);
            }
            if (this.commitOnBuild) {
                this.commit();
            }
            this.searcherMgr = new SearcherManager(this.writer, (SearcherFactory)null);
            success = true;
        }
        finally {
            if (!success && this.writer != null) {
                this.writer.rollback();
                this.writer = null;
            }
        }
    }
    
    public void commit() throws IOException {
        if (this.writer == null) {
            throw new IllegalStateException("Cannot commit on an closed writer. Add documents first");
        }
        this.writer.commit();
    }
    
    private Analyzer getGramAnalyzer() {
        return (Analyzer)new AnalyzerWrapper(Analyzer.PER_FIELD_REUSE_STRATEGY) {
            protected Analyzer getWrappedAnalyzer(final String fieldName) {
                return AnalyzingInfixSuggester.this.indexAnalyzer;
            }
            
            protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
                if (fieldName.equals("textgrams") && AnalyzingInfixSuggester.this.minPrefixChars > 0) {
                    TokenFilter filter;
                    if (AnalyzingInfixSuggester.this.matchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
                        filter = (TokenFilter)new EdgeNGramTokenFilter(components.getTokenStream(), 1, AnalyzingInfixSuggester.this.minPrefixChars);
                    }
                    else {
                        filter = (TokenFilter)new Lucene43EdgeNGramTokenFilter(components.getTokenStream(), 1, AnalyzingInfixSuggester.this.minPrefixChars);
                    }
                    return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)filter);
                }
                return components;
            }
        };
    }
    
    private synchronized void ensureOpen() throws IOException {
        if (this.writer == null) {
            if (this.searcherMgr != null) {
                this.searcherMgr.close();
                this.searcherMgr = null;
            }
            this.writer = new IndexWriter(this.dir, this.getIndexWriterConfig(this.getGramAnalyzer(), IndexWriterConfig.OpenMode.CREATE));
            this.searcherMgr = new SearcherManager(this.writer, (SearcherFactory)null);
        }
    }
    
    public void add(final BytesRef text, final Set<BytesRef> contexts, final long weight, final BytesRef payload) throws IOException {
        this.ensureOpen();
        this.writer.addDocument((Iterable)this.buildDocument(text, contexts, weight, payload));
    }
    
    public void update(final BytesRef text, final Set<BytesRef> contexts, final long weight, final BytesRef payload) throws IOException {
        this.ensureOpen();
        this.writer.updateDocument(new Term("exacttext", text.utf8ToString()), (Iterable)this.buildDocument(text, contexts, weight, payload));
    }
    
    private Document buildDocument(final BytesRef text, final Set<BytesRef> contexts, final long weight, final BytesRef payload) throws IOException {
        final String textString = text.utf8ToString();
        final Document doc = new Document();
        final FieldType ft = this.getTextFieldType();
        doc.add((IndexableField)new Field("text", textString, ft));
        doc.add((IndexableField)new Field("textgrams", textString, ft));
        doc.add((IndexableField)new StringField("exacttext", textString, Field.Store.NO));
        doc.add((IndexableField)new BinaryDocValuesField("text", text));
        doc.add((IndexableField)new NumericDocValuesField("weight", weight));
        if (payload != null) {
            doc.add((IndexableField)new BinaryDocValuesField("payloads", payload));
        }
        if (contexts != null) {
            for (final BytesRef context : contexts) {
                doc.add((IndexableField)new StringField("contexts", context, Field.Store.NO));
                doc.add((IndexableField)new SortedSetDocValuesField("contexts", context));
            }
        }
        return doc;
    }
    
    public void refresh() throws IOException {
        if (this.searcherMgr == null) {
            throw new IllegalStateException("suggester was not built");
        }
        this.searcherMgr.maybeRefreshBlocking();
    }
    
    protected FieldType getTextFieldType() {
        final FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
        ft.setIndexOptions(IndexOptions.DOCS);
        ft.setOmitNorms(true);
        return ft;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) throws IOException {
        return this.lookup(key, contexts, num, this.allTermsRequired, this.highlight);
    }
    
    public List<LookupResult> lookup(final CharSequence key, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return this.lookup(key, (BooleanQuery)null, num, allTermsRequired, doHighlight);
    }
    
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return this.lookup(key, this.toQuery(contexts), num, allTermsRequired, doHighlight);
    }
    
    protected Query getLastTokenQuery(final String token) throws IOException {
        if (token.length() < this.minPrefixChars) {
            return (Query)new TermQuery(new Term("textgrams", token));
        }
        return (Query)new PrefixQuery(new Term("text", token));
    }
    
    public List<LookupResult> lookup(final CharSequence key, final Map<BytesRef, BooleanClause.Occur> contextInfo, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        return this.lookup(key, this.toQuery(contextInfo), num, allTermsRequired, doHighlight);
    }
    
    private BooleanQuery toQuery(final Map<BytesRef, BooleanClause.Occur> contextInfo) {
        if (contextInfo == null || contextInfo.isEmpty()) {
            return null;
        }
        final BooleanQuery.Builder contextFilter = new BooleanQuery.Builder();
        for (final Map.Entry<BytesRef, BooleanClause.Occur> entry : contextInfo.entrySet()) {
            this.addContextToQuery(contextFilter, entry.getKey(), entry.getValue());
        }
        return contextFilter.build();
    }
    
    private BooleanQuery toQuery(final Set<BytesRef> contextInfo) {
        if (contextInfo == null || contextInfo.isEmpty()) {
            return null;
        }
        final BooleanQuery.Builder contextFilter = new BooleanQuery.Builder();
        for (final BytesRef context : contextInfo) {
            this.addContextToQuery(contextFilter, context, BooleanClause.Occur.SHOULD);
        }
        return contextFilter.build();
    }
    
    public void addContextToQuery(final BooleanQuery.Builder query, final BytesRef context, final BooleanClause.Occur clause) {
        query.add((Query)new TermQuery(new Term("contexts", context)), clause);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final BooleanQuery contextQuery, final int num, final boolean allTermsRequired, final boolean doHighlight) throws IOException {
        if (this.searcherMgr == null) {
            throw new IllegalStateException("suggester was not built");
        }
        BooleanClause.Occur occur;
        if (allTermsRequired) {
            occur = BooleanClause.Occur.MUST;
        }
        else {
            occur = BooleanClause.Occur.SHOULD;
        }
        String prefixToken = null;
        BooleanQuery.Builder query;
        Set<String> matchedTokens;
        try (final TokenStream ts = this.queryAnalyzer.tokenStream("", (Reader)new StringReader(key.toString()))) {
            ts.reset();
            final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
            final OffsetAttribute offsetAtt = (OffsetAttribute)ts.addAttribute((Class)OffsetAttribute.class);
            String lastToken = null;
            query = new BooleanQuery.Builder();
            int maxEndOffset = -1;
            matchedTokens = new HashSet<String>();
            while (ts.incrementToken()) {
                if (lastToken != null) {
                    matchedTokens.add(lastToken);
                    query.add((Query)new TermQuery(new Term("text", lastToken)), occur);
                }
                lastToken = termAtt.toString();
                if (lastToken != null) {
                    maxEndOffset = Math.max(maxEndOffset, offsetAtt.endOffset());
                }
            }
            ts.end();
            if (lastToken != null) {
                Query lastQuery;
                if (maxEndOffset == offsetAtt.endOffset()) {
                    lastQuery = this.getLastTokenQuery(lastToken);
                    prefixToken = lastToken;
                }
                else {
                    matchedTokens.add(lastToken);
                    lastQuery = (Query)new TermQuery(new Term("text", lastToken));
                }
                if (lastQuery != null) {
                    query.add(lastQuery, occur);
                }
            }
            if (contextQuery != null) {
                boolean allMustNot = true;
                for (final BooleanClause clause : contextQuery.clauses()) {
                    if (clause.getOccur() != BooleanClause.Occur.MUST_NOT) {
                        allMustNot = false;
                        break;
                    }
                }
                if (allMustNot) {
                    for (final BooleanClause clause : contextQuery.clauses()) {
                        query.add(clause);
                    }
                }
                else {
                    query.add((Query)contextQuery, BooleanClause.Occur.MUST);
                }
            }
        }
        final Query finalQuery = this.finishQuery(query, allTermsRequired);
        final TopFieldCollector c = TopFieldCollector.create(AnalyzingInfixSuggester.SORT, num, true, false, false);
        final SortingMergePolicy sortingMergePolicy = (SortingMergePolicy)this.writer.getConfig().getMergePolicy();
        final Collector c2 = (Collector)new EarlyTerminatingSortingCollector((Collector)c, AnalyzingInfixSuggester.SORT, num, sortingMergePolicy.getSort());
        final IndexSearcher searcher = (IndexSearcher)this.searcherMgr.acquire();
        List<LookupResult> results = null;
        try {
            searcher.search(finalQuery, c2);
            final TopFieldDocs hits = c.topDocs();
            results = this.createResults(searcher, hits, num, key, doHighlight, matchedTokens, prefixToken);
        }
        finally {
            this.searcherMgr.release((Object)searcher);
        }
        return results;
    }
    
    protected List<LookupResult> createResults(final IndexSearcher searcher, final TopFieldDocs hits, final int num, final CharSequence charSequence, final boolean doHighlight, final Set<String> matchedTokens, final String prefixToken) throws IOException {
        final BinaryDocValues textDV = MultiDocValues.getBinaryValues(searcher.getIndexReader(), "text");
        final BinaryDocValues payloadsDV = MultiDocValues.getBinaryValues(searcher.getIndexReader(), "payloads");
        final List<LeafReaderContext> leaves = searcher.getIndexReader().leaves();
        final List<LookupResult> results = new ArrayList<LookupResult>();
        for (int i = 0; i < hits.scoreDocs.length; ++i) {
            final FieldDoc fd = (FieldDoc)hits.scoreDocs[i];
            final BytesRef term = textDV.get(fd.doc);
            final String text = term.utf8ToString();
            final long score = (long)fd.fields[0];
            BytesRef payload;
            if (payloadsDV != null) {
                payload = BytesRef.deepCopyOf(payloadsDV.get(fd.doc));
            }
            else {
                payload = null;
            }
            final int segment = ReaderUtil.subIndex(fd.doc, (List)leaves);
            final SortedSetDocValues contextsDV = leaves.get(segment).reader().getSortedSetDocValues("contexts");
            Set<BytesRef> contexts;
            if (contextsDV != null) {
                contexts = new HashSet<BytesRef>();
                contextsDV.setDocument(fd.doc - leaves.get(segment).docBase);
                long ord;
                while ((ord = contextsDV.nextOrd()) != -1L) {
                    final BytesRef context = BytesRef.deepCopyOf(contextsDV.lookupOrd(ord));
                    contexts.add(context);
                }
            }
            else {
                contexts = null;
            }
            LookupResult result;
            if (doHighlight) {
                result = new LookupResult(text, this.highlight(text, matchedTokens, prefixToken), score, payload, contexts);
            }
            else {
                result = new LookupResult(text, score, payload, contexts);
            }
            results.add(result);
        }
        return results;
    }
    
    protected Query finishQuery(final BooleanQuery.Builder in, final boolean allTermsRequired) {
        return (Query)in.build();
    }
    
    protected Object highlight(final String text, final Set<String> matchedTokens, final String prefixToken) throws IOException {
        try (final TokenStream ts = this.queryAnalyzer.tokenStream("text", (Reader)new StringReader(text))) {
            final CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
            final OffsetAttribute offsetAtt = (OffsetAttribute)ts.addAttribute((Class)OffsetAttribute.class);
            ts.reset();
            final StringBuilder sb = new StringBuilder();
            int upto = 0;
            while (ts.incrementToken()) {
                final String token = termAtt.toString();
                final int startOffset = offsetAtt.startOffset();
                final int endOffset = offsetAtt.endOffset();
                if (upto < startOffset) {
                    this.addNonMatch(sb, text.substring(upto, startOffset));
                    upto = startOffset;
                }
                else if (upto > startOffset) {
                    continue;
                }
                if (matchedTokens.contains(token)) {
                    this.addWholeMatch(sb, text.substring(startOffset, endOffset), token);
                    upto = endOffset;
                }
                else {
                    if (prefixToken == null || !token.startsWith(prefixToken)) {
                        continue;
                    }
                    this.addPrefixMatch(sb, text.substring(startOffset, endOffset), token, prefixToken);
                    upto = endOffset;
                }
            }
            ts.end();
            final int endOffset2 = offsetAtt.endOffset();
            if (upto < endOffset2) {
                this.addNonMatch(sb, text.substring(upto));
            }
            return sb.toString();
        }
    }
    
    protected void addNonMatch(final StringBuilder sb, final String text) {
        sb.append(text);
    }
    
    protected void addWholeMatch(final StringBuilder sb, final String surface, final String analyzed) {
        sb.append("<b>");
        sb.append(surface);
        sb.append("</b>");
    }
    
    protected void addPrefixMatch(final StringBuilder sb, final String surface, final String analyzed, final String prefixToken) {
        if (prefixToken.length() >= surface.length()) {
            this.addWholeMatch(sb, surface, analyzed);
            return;
        }
        sb.append("<b>");
        sb.append(surface.substring(0, prefixToken.length()));
        sb.append("</b>");
        sb.append(surface.substring(prefixToken.length()));
    }
    
    @Override
    public boolean store(final DataOutput in) throws IOException {
        return false;
    }
    
    @Override
    public boolean load(final DataInput out) throws IOException {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        if (this.searcherMgr != null) {
            this.searcherMgr.close();
            this.searcherMgr = null;
        }
        if (this.writer != null) {
            this.writer.close();
            this.dir.close();
            this.writer = null;
        }
    }
    
    public long ramBytesUsed() {
        long mem = RamUsageEstimator.shallowSizeOf((Object)this);
        try {
            if (this.searcherMgr != null) {
                final IndexSearcher searcher = (IndexSearcher)this.searcherMgr.acquire();
                try {
                    for (final LeafReaderContext context : searcher.getIndexReader().leaves()) {
                        final LeafReader reader = FilterLeafReader.unwrap(context.reader());
                        if (reader instanceof SegmentReader) {
                            mem += ((SegmentReader)context.reader()).ramBytesUsed();
                        }
                    }
                }
                finally {
                    this.searcherMgr.release((Object)searcher);
                }
            }
            return mem;
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        try {
            if (this.searcherMgr != null) {
                final IndexSearcher searcher = (IndexSearcher)this.searcherMgr.acquire();
                try {
                    for (final LeafReaderContext context : searcher.getIndexReader().leaves()) {
                        final LeafReader reader = FilterLeafReader.unwrap(context.reader());
                        if (reader instanceof SegmentReader) {
                            resources.add(Accountables.namedAccountable("segment", (Accountable)reader));
                        }
                    }
                }
                finally {
                    this.searcherMgr.release((Object)searcher);
                }
            }
            return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    @Override
    public long getCount() throws IOException {
        if (this.searcherMgr == null) {
            return 0L;
        }
        final IndexSearcher searcher = (IndexSearcher)this.searcherMgr.acquire();
        try {
            return searcher.getIndexReader().numDocs();
        }
        finally {
            this.searcherMgr.release((Object)searcher);
        }
    }
    
    static {
        SORT = new Sort(new SortField("weight", SortField.Type.LONG, true));
    }
}
