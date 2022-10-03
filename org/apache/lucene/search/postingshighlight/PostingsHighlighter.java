package org.apache.lucene.search.postingshighlight;

import java.nio.charset.StandardCharsets;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.IndexReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.util.automaton.CharacterRunAutomaton;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import org.apache.lucene.index.IndexReaderContext;
import java.util.SortedSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.InPlaceMergeSorter;
import java.util.Set;
import org.apache.lucene.index.Term;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.lucene.search.ScoreDoc;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import java.util.Locale;
import java.text.BreakIterator;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.IndexSearcher;

public class PostingsHighlighter
{
    private static final IndexSearcher EMPTY_INDEXSEARCHER;
    public static final int DEFAULT_MAX_LENGTH = 10000;
    private final int maxLength;
    private PassageFormatter defaultFormatter;
    private PassageScorer defaultScorer;
    private static final PostingsEnum EMPTY;
    
    public PostingsHighlighter() {
        this(10000);
    }
    
    public PostingsHighlighter(final int maxLength) {
        if (maxLength < 0 || maxLength == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxLength must be < Integer.MAX_VALUE");
        }
        this.maxLength = maxLength;
    }
    
    protected BreakIterator getBreakIterator(final String field) {
        return BreakIterator.getSentenceInstance(Locale.ROOT);
    }
    
    protected PassageFormatter getFormatter(final String field) {
        if (this.defaultFormatter == null) {
            this.defaultFormatter = new DefaultPassageFormatter();
        }
        return this.defaultFormatter;
    }
    
    protected PassageScorer getScorer(final String field) {
        if (this.defaultScorer == null) {
            this.defaultScorer = new PassageScorer();
        }
        return this.defaultScorer;
    }
    
    public String[] highlight(final String field, final Query query, final IndexSearcher searcher, final TopDocs topDocs) throws IOException {
        return this.highlight(field, query, searcher, topDocs, 1);
    }
    
    public String[] highlight(final String field, final Query query, final IndexSearcher searcher, final TopDocs topDocs, final int maxPassages) throws IOException {
        final Map<String, String[]> res = this.highlightFields(new String[] { field }, query, searcher, topDocs, new int[] { maxPassages });
        return res.get(field);
    }
    
    public Map<String, String[]> highlightFields(final String[] fields, final Query query, final IndexSearcher searcher, final TopDocs topDocs) throws IOException {
        final int[] maxPassages = new int[fields.length];
        Arrays.fill(maxPassages, 1);
        return this.highlightFields(fields, query, searcher, topDocs, maxPassages);
    }
    
    public Map<String, String[]> highlightFields(final String[] fields, final Query query, final IndexSearcher searcher, final TopDocs topDocs, final int[] maxPassages) throws IOException {
        final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        final int[] docids = new int[scoreDocs.length];
        for (int i = 0; i < docids.length; ++i) {
            docids[i] = scoreDocs[i].doc;
        }
        return this.highlightFields(fields, query, searcher, docids, maxPassages);
    }
    
    public Map<String, String[]> highlightFields(final String[] fieldsIn, final Query query, final IndexSearcher searcher, final int[] docidsIn, final int[] maxPassagesIn) throws IOException {
        final Map<String, String[]> snippets = new HashMap<String, String[]>();
        for (final Map.Entry<String, Object[]> ent : this.highlightFieldsAsObjects(fieldsIn, query, searcher, docidsIn, maxPassagesIn).entrySet()) {
            final Object[] snippetObjects = ent.getValue();
            final String[] snippetStrings = new String[snippetObjects.length];
            snippets.put(ent.getKey(), snippetStrings);
            for (int i = 0; i < snippetObjects.length; ++i) {
                final Object snippet = snippetObjects[i];
                if (snippet != null) {
                    snippetStrings[i] = snippet.toString();
                }
            }
        }
        return snippets;
    }
    
    protected Map<String, Object[]> highlightFieldsAsObjects(final String[] fieldsIn, final Query query, final IndexSearcher searcher, final int[] docidsIn, final int[] maxPassagesIn) throws IOException {
        if (fieldsIn.length < 1) {
            throw new IllegalArgumentException("fieldsIn must not be empty");
        }
        if (fieldsIn.length != maxPassagesIn.length) {
            throw new IllegalArgumentException("invalid number of maxPassagesIn");
        }
        final SortedSet<Term> queryTerms = new TreeSet<Term>();
        PostingsHighlighter.EMPTY_INDEXSEARCHER.createNormalizedWeight(query, false).extractTerms((Set)queryTerms);
        final IndexReaderContext readerContext = searcher.getIndexReader().getContext();
        final List<LeafReaderContext> leaves = readerContext.leaves();
        final int[] docids = new int[docidsIn.length];
        System.arraycopy(docidsIn, 0, docids, 0, docidsIn.length);
        final String[] fields = new String[fieldsIn.length];
        System.arraycopy(fieldsIn, 0, fields, 0, fieldsIn.length);
        final int[] maxPassages = new int[maxPassagesIn.length];
        System.arraycopy(maxPassagesIn, 0, maxPassages, 0, maxPassagesIn.length);
        Arrays.sort(docids);
        new InPlaceMergeSorter() {
            protected void swap(final int i, final int j) {
                final String tmp = fields[i];
                fields[i] = fields[j];
                fields[j] = tmp;
                final int tmp2 = maxPassages[i];
                maxPassages[i] = maxPassages[j];
                maxPassages[j] = tmp2;
            }
            
            protected int compare(final int i, final int j) {
                return fields[i].compareTo(fields[j]);
            }
        }.sort(0, fields.length);
        final String[][] contents = this.loadFieldValues(searcher, fields, docids, this.maxLength);
        final Map<String, Object[]> highlights = new HashMap<String, Object[]>();
        for (int i = 0; i < fields.length; ++i) {
            final String field = fields[i];
            final int numPassages = maxPassages[i];
            final Term floor = new Term(field, "");
            final Term ceiling = new Term(field, UnicodeUtil.BIG_TERM);
            final SortedSet<Term> fieldTerms = queryTerms.subSet(floor, ceiling);
            final BytesRef[] terms = new BytesRef[fieldTerms.size()];
            int termUpto = 0;
            for (final Term term : fieldTerms) {
                terms[termUpto++] = term.bytes();
            }
            final Map<Integer, Object> fieldHighlights = this.highlightField(field, contents[i], this.getBreakIterator(field), terms, docids, leaves, numPassages, query);
            final Object[] result = new Object[docids.length];
            for (int j = 0; j < docidsIn.length; ++j) {
                result[j] = fieldHighlights.get(docidsIn[j]);
            }
            highlights.put(field, result);
        }
        return highlights;
    }
    
    protected String[][] loadFieldValues(final IndexSearcher searcher, final String[] fields, final int[] docids, final int maxLength) throws IOException {
        final String[][] contents = new String[fields.length][docids.length];
        final char[] valueSeparators = new char[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            valueSeparators[i] = this.getMultiValuedSeparator(fields[i]);
        }
        final LimitedStoredFieldVisitor visitor = new LimitedStoredFieldVisitor(fields, valueSeparators, maxLength);
        for (int j = 0; j < docids.length; ++j) {
            searcher.doc(docids[j], (StoredFieldVisitor)visitor);
            for (int k = 0; k < fields.length; ++k) {
                contents[k][j] = visitor.getValue(k).toString();
            }
            visitor.reset();
        }
        return contents;
    }
    
    protected char getMultiValuedSeparator(final String field) {
        return ' ';
    }
    
    protected Analyzer getIndexAnalyzer(final String field) {
        return null;
    }
    
    private Map<Integer, Object> highlightField(final String field, final String[] contents, final BreakIterator bi, BytesRef[] terms, final int[] docids, final List<LeafReaderContext> leaves, final int maxPassages, final Query query) throws IOException {
        final Map<Integer, Object> highlights = new HashMap<Integer, Object>();
        final PassageFormatter fieldFormatter = this.getFormatter(field);
        if (fieldFormatter == null) {
            throw new NullPointerException("PassageFormatter cannot be null");
        }
        final Analyzer analyzer = this.getIndexAnalyzer(field);
        CharacterRunAutomaton[] automata = new CharacterRunAutomaton[0];
        if (analyzer != null) {
            automata = MultiTermHighlighting.extractAutomata(query, field);
        }
        if (automata.length > 0) {
            final BytesRef[] newTerms = new BytesRef[terms.length + 1];
            System.arraycopy(terms, 0, newTerms, 0, terms.length);
            terms = newTerms;
        }
        PostingsEnum[] postings = null;
        TermsEnum termsEnum = null;
        int lastLeaf = -1;
        for (int i = 0; i < docids.length; ++i) {
            final String content = contents[i];
            if (content.length() != 0) {
                bi.setText(content);
                final int doc = docids[i];
                final int leaf = ReaderUtil.subIndex(doc, (List)leaves);
                final LeafReaderContext subContext = leaves.get(leaf);
                final LeafReader r = subContext.reader();
                assert leaf >= lastLeaf;
                if (leaf != lastLeaf) {
                    final Terms t = r.terms(field);
                    if (t != null) {
                        if (!t.hasOffsets()) {
                            throw new IllegalArgumentException("field '" + field + "' was indexed without offsets, cannot highlight");
                        }
                        termsEnum = t.iterator();
                        postings = new PostingsEnum[terms.length];
                    }
                    else {
                        termsEnum = null;
                    }
                }
                if (termsEnum != null) {
                    if (automata.length > 0) {
                        final PostingsEnum dp = MultiTermHighlighting.getDocsEnum(analyzer.tokenStream(field, content), automata);
                        dp.advance(doc - subContext.docBase);
                        postings[terms.length - 1] = dp;
                    }
                    Passage[] passages = this.highlightDoc(field, terms, content.length(), bi, doc - subContext.docBase, termsEnum, postings, maxPassages);
                    if (passages.length == 0) {
                        passages = this.getEmptyHighlight(field, bi, maxPassages);
                    }
                    if (passages.length > 0) {
                        highlights.put(doc, fieldFormatter.format(passages, content));
                    }
                    lastLeaf = leaf;
                }
            }
        }
        return highlights;
    }
    
    private Passage[] highlightDoc(final String field, final BytesRef[] terms, final int contentLength, final BreakIterator bi, final int doc, final TermsEnum termsEnum, final PostingsEnum[] postings, final int n) throws IOException {
        final PassageScorer scorer = this.getScorer(field);
        if (scorer == null) {
            throw new NullPointerException("PassageScorer cannot be null");
        }
        final PriorityQueue<OffsetsEnum> pq = new PriorityQueue<OffsetsEnum>();
        final float[] weights = new float[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            PostingsEnum de = postings[i];
            if (de != PostingsHighlighter.EMPTY) {
                int pDoc;
                if (de == null) {
                    postings[i] = PostingsHighlighter.EMPTY;
                    if (!termsEnum.seekExact(terms[i])) {
                        continue;
                    }
                    final int n2 = i;
                    final PostingsEnum postings2 = termsEnum.postings((PostingsEnum)null, 56);
                    postings[n2] = postings2;
                    de = postings2;
                    assert de != null;
                    pDoc = de.advance(doc);
                }
                else {
                    pDoc = de.docID();
                    if (pDoc < doc) {
                        pDoc = de.advance(doc);
                    }
                }
                if (doc == pDoc) {
                    weights[i] = scorer.weight(contentLength, de.freq());
                    de.nextPosition();
                    pq.add(new OffsetsEnum(de, i));
                }
            }
        }
        pq.add(new OffsetsEnum(PostingsHighlighter.EMPTY, Integer.MAX_VALUE));
        final PriorityQueue<Passage> passageQueue = new PriorityQueue<Passage>(n, new Comparator<Passage>() {
            @Override
            public int compare(final Passage left, final Passage right) {
                if (left.score < right.score) {
                    return -1;
                }
                if (left.score > right.score) {
                    return 1;
                }
                return left.startOffset - right.startOffset;
            }
        });
        Passage current = new Passage();
        OffsetsEnum off;
        while ((off = pq.poll()) != null) {
            final PostingsEnum dp = off.dp;
            int start = dp.startOffset();
            assert start >= 0;
            int end = dp.endOffset();
            assert PostingsHighlighter.EMPTY.startOffset() == Integer.MAX_VALUE;
            if (start < contentLength && end > contentLength) {
                continue;
            }
            if (start >= current.endOffset) {
                if (current.startOffset >= 0) {
                    final Passage passage = current;
                    passage.score *= scorer.norm(current.startOffset);
                    if (passageQueue.size() == n && current.score < passageQueue.peek().score) {
                        current.reset();
                    }
                    else {
                        passageQueue.offer(current);
                        if (passageQueue.size() > n) {
                            current = passageQueue.poll();
                            current.reset();
                        }
                        else {
                            current = new Passage();
                        }
                    }
                }
                if (start >= contentLength) {
                    final Passage[] passages = new Passage[passageQueue.size()];
                    passageQueue.toArray(passages);
                    for (final Passage p : passages) {
                        p.sort();
                    }
                    Arrays.sort(passages, new Comparator<Passage>() {
                        @Override
                        public int compare(final Passage left, final Passage right) {
                            return left.startOffset - right.startOffset;
                        }
                    });
                    return passages;
                }
                current.startOffset = Math.max(bi.preceding(start + 1), 0);
                current.endOffset = Math.min(bi.next(), contentLength);
            }
            int tf = 0;
            while (true) {
                ++tf;
                BytesRef term = terms[off.id];
                if (term == null) {
                    term = off.dp.getPayload();
                    assert term != null;
                }
                current.addMatch(start, end, term);
                if (off.pos == dp.freq()) {
                    break;
                }
                final OffsetsEnum offsetsEnum = off;
                ++offsetsEnum.pos;
                dp.nextPosition();
                start = dp.startOffset();
                end = dp.endOffset();
                if (start >= current.endOffset || end > contentLength) {
                    pq.offer(off);
                    break;
                }
            }
            final Passage passage2 = current;
            passage2.score += weights[off.id] * scorer.tf(tf, current.endOffset - current.startOffset);
        }
        assert false;
        return null;
    }
    
    protected Passage[] getEmptyHighlight(final String fieldName, final BreakIterator bi, final int maxPassages) {
        final List<Passage> passages = new ArrayList<Passage>();
        int pos = bi.current();
        assert pos == 0;
        while (passages.size() < maxPassages) {
            final int next = bi.next();
            if (next == -1) {
                break;
            }
            final Passage passage = new Passage();
            passage.score = Float.NaN;
            passage.startOffset = pos;
            passage.endOffset = next;
            passages.add(passage);
            pos = next;
        }
        return passages.toArray(new Passage[passages.size()]);
    }
    
    static {
        try {
            final IndexReader emptyReader = (IndexReader)new MultiReader(new IndexReader[0]);
            (EMPTY_INDEXSEARCHER = new IndexSearcher(emptyReader)).setQueryCache((QueryCache)null);
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
        EMPTY = new PostingsEnum() {
            public int nextPosition() throws IOException {
                return -1;
            }
            
            public int startOffset() throws IOException {
                return Integer.MAX_VALUE;
            }
            
            public int endOffset() throws IOException {
                return Integer.MAX_VALUE;
            }
            
            public BytesRef getPayload() throws IOException {
                return null;
            }
            
            public int freq() throws IOException {
                return 0;
            }
            
            public int docID() {
                return Integer.MAX_VALUE;
            }
            
            public int nextDoc() throws IOException {
                return Integer.MAX_VALUE;
            }
            
            public int advance(final int target) throws IOException {
                return Integer.MAX_VALUE;
            }
            
            public long cost() {
                return 0L;
            }
        };
    }
    
    private static class OffsetsEnum implements Comparable<OffsetsEnum>
    {
        PostingsEnum dp;
        int pos;
        int id;
        
        OffsetsEnum(final PostingsEnum dp, final int id) throws IOException {
            this.dp = dp;
            this.id = id;
            this.pos = 1;
        }
        
        @Override
        public int compareTo(final OffsetsEnum other) {
            try {
                final int off = this.dp.startOffset();
                final int otherOff = other.dp.startOffset();
                if (off == otherOff) {
                    return this.id - other.id;
                }
                return Integer.compare(off, otherOff);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static class LimitedStoredFieldVisitor extends StoredFieldVisitor
    {
        private final String[] fields;
        private final char[] valueSeparators;
        private final int maxLength;
        private final StringBuilder[] builders;
        private int currentField;
        
        public LimitedStoredFieldVisitor(final String[] fields, final char[] valueSeparators, final int maxLength) {
            this.currentField = -1;
            assert fields.length == valueSeparators.length;
            this.fields = fields;
            this.valueSeparators = valueSeparators;
            this.maxLength = maxLength;
            this.builders = new StringBuilder[fields.length];
            for (int i = 0; i < this.builders.length; ++i) {
                this.builders[i] = new StringBuilder();
            }
        }
        
        public void stringField(final FieldInfo fieldInfo, final byte[] bytes) throws IOException {
            final String value = new String(bytes, StandardCharsets.UTF_8);
            assert this.currentField >= 0;
            final StringBuilder builder = this.builders[this.currentField];
            if (builder.length() > 0 && builder.length() < this.maxLength) {
                builder.append(this.valueSeparators[this.currentField]);
            }
            if (builder.length() + value.length() > this.maxLength) {
                builder.append(value, 0, this.maxLength - builder.length());
            }
            else {
                builder.append(value);
            }
        }
        
        public StoredFieldVisitor.Status needsField(final FieldInfo fieldInfo) throws IOException {
            this.currentField = Arrays.binarySearch(this.fields, fieldInfo.name);
            if (this.currentField < 0) {
                return StoredFieldVisitor.Status.NO;
            }
            if (this.builders[this.currentField].length() > this.maxLength) {
                return (this.fields.length == 1) ? StoredFieldVisitor.Status.STOP : StoredFieldVisitor.Status.NO;
            }
            return StoredFieldVisitor.Status.YES;
        }
        
        String getValue(final int i) {
            return this.builders[i].toString();
        }
        
        void reset() {
            this.currentField = -1;
            for (int i = 0; i < this.fields.length; ++i) {
                this.builders[i].setLength(0);
            }
        }
    }
}
