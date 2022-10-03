package org.apache.lucene.search.spell;

import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermsEnum;
import java.util.ArrayList;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.DirectoryReader;
import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import java.io.Closeable;

public class SpellChecker implements Closeable
{
    public static final float DEFAULT_ACCURACY = 0.5f;
    public static final String F_WORD = "word";
    Directory spellIndex;
    private float bStart;
    private float bEnd;
    private IndexSearcher searcher;
    private final Object searcherLock;
    private final Object modifyCurrentIndexLock;
    private volatile boolean closed;
    private float accuracy;
    private StringDistance sd;
    private Comparator<SuggestWord> comparator;
    
    public SpellChecker(final Directory spellIndex, final StringDistance sd) throws IOException {
        this(spellIndex, sd, SuggestWordQueue.DEFAULT_COMPARATOR);
    }
    
    public SpellChecker(final Directory spellIndex) throws IOException {
        this(spellIndex, new LevensteinDistance());
    }
    
    public SpellChecker(final Directory spellIndex, final StringDistance sd, final Comparator<SuggestWord> comparator) throws IOException {
        this.bStart = 2.0f;
        this.bEnd = 1.0f;
        this.searcherLock = new Object();
        this.modifyCurrentIndexLock = new Object();
        this.closed = false;
        this.accuracy = 0.5f;
        this.setSpellIndex(spellIndex);
        this.setStringDistance(sd);
        this.comparator = comparator;
    }
    
    public void setSpellIndex(final Directory spellIndexDir) throws IOException {
        synchronized (this.modifyCurrentIndexLock) {
            this.ensureOpen();
            if (!DirectoryReader.indexExists(spellIndexDir)) {
                final IndexWriter writer = new IndexWriter(spellIndexDir, new IndexWriterConfig((Analyzer)null));
                writer.close();
            }
            this.swapSearcher(spellIndexDir);
        }
    }
    
    public void setComparator(final Comparator<SuggestWord> comparator) {
        this.comparator = comparator;
    }
    
    public Comparator<SuggestWord> getComparator() {
        return this.comparator;
    }
    
    public void setStringDistance(final StringDistance sd) {
        this.sd = sd;
    }
    
    public StringDistance getStringDistance() {
        return this.sd;
    }
    
    public void setAccuracy(final float acc) {
        this.accuracy = acc;
    }
    
    public float getAccuracy() {
        return this.accuracy;
    }
    
    public String[] suggestSimilar(final String word, final int numSug) throws IOException {
        return this.suggestSimilar(word, numSug, null, null, SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX);
    }
    
    public String[] suggestSimilar(final String word, final int numSug, final float accuracy) throws IOException {
        return this.suggestSimilar(word, numSug, null, null, SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX, accuracy);
    }
    
    public String[] suggestSimilar(final String word, final int numSug, final IndexReader ir, final String field, final SuggestMode suggestMode) throws IOException {
        return this.suggestSimilar(word, numSug, ir, field, suggestMode, this.accuracy);
    }
    
    public String[] suggestSimilar(final String word, final int numSug, IndexReader ir, String field, SuggestMode suggestMode, float accuracy) throws IOException {
        final IndexSearcher indexSearcher = this.obtainSearcher();
        try {
            if (ir == null || field == null) {
                suggestMode = SuggestMode.SUGGEST_ALWAYS;
            }
            if (suggestMode == SuggestMode.SUGGEST_ALWAYS) {
                ir = null;
                field = null;
            }
            final int lengthWord = word.length();
            final int freq = (ir != null && field != null) ? ir.docFreq(new Term(field, word)) : 0;
            final int goalFreq = (suggestMode == SuggestMode.SUGGEST_MORE_POPULAR) ? freq : 0;
            if (suggestMode == SuggestMode.SUGGEST_WHEN_NOT_IN_INDEX && freq > 0) {
                return new String[] { word };
            }
            final BooleanQuery.Builder query = new BooleanQuery.Builder();
            for (int ng = getMin(lengthWord); ng <= getMax(lengthWord); ++ng) {
                final String key = "gram" + ng;
                final String[] grams = formGrams(word, ng);
                if (grams.length != 0) {
                    if (this.bStart > 0.0f) {
                        add(query, "start" + ng, grams[0], this.bStart);
                    }
                    if (this.bEnd > 0.0f) {
                        add(query, "end" + ng, grams[grams.length - 1], this.bEnd);
                    }
                    for (int i = 0; i < grams.length; ++i) {
                        add(query, key, grams[i]);
                    }
                }
            }
            final int maxHits = 10 * numSug;
            final ScoreDoc[] hits = indexSearcher.search((Query)query.build(), maxHits).scoreDocs;
            final SuggestWordQueue sugQueue = new SuggestWordQueue(numSug, this.comparator);
            final int stop = Math.min(hits.length, maxHits);
            SuggestWord sugWord = new SuggestWord();
            for (int j = 0; j < stop; ++j) {
                sugWord.string = indexSearcher.doc(hits[j].doc).get("word");
                if (!sugWord.string.equals(word)) {
                    sugWord.score = this.sd.getDistance(word, sugWord.string);
                    if (sugWord.score >= accuracy) {
                        if (ir != null && field != null) {
                            sugWord.freq = ir.docFreq(new Term(field, sugWord.string));
                            if (suggestMode == SuggestMode.SUGGEST_MORE_POPULAR && goalFreq > sugWord.freq) {
                                continue;
                            }
                            if (sugWord.freq < 1) {
                                continue;
                            }
                        }
                        sugQueue.insertWithOverflow((Object)sugWord);
                        if (sugQueue.size() == numSug) {
                            accuracy = ((SuggestWord)sugQueue.top()).score;
                        }
                        sugWord = new SuggestWord();
                    }
                }
            }
            final String[] list = new String[sugQueue.size()];
            for (int k = sugQueue.size() - 1; k >= 0; --k) {
                list[k] = ((SuggestWord)sugQueue.pop()).string;
            }
            return list;
        }
        finally {
            this.releaseSearcher(indexSearcher);
        }
    }
    
    private static void add(final BooleanQuery.Builder q, final String name, final String value, final float boost) {
        final Query tq = (Query)new TermQuery(new Term(name, value));
        q.add(new BooleanClause((Query)new BoostQuery(tq, boost), BooleanClause.Occur.SHOULD));
    }
    
    private static void add(final BooleanQuery.Builder q, final String name, final String value) {
        q.add(new BooleanClause((Query)new TermQuery(new Term(name, value)), BooleanClause.Occur.SHOULD));
    }
    
    private static String[] formGrams(final String text, final int ng) {
        final int len = text.length();
        final String[] res = new String[len - ng + 1];
        for (int i = 0; i < len - ng + 1; ++i) {
            res[i] = text.substring(i, i + ng);
        }
        return res;
    }
    
    public void clearIndex() throws IOException {
        synchronized (this.modifyCurrentIndexLock) {
            this.ensureOpen();
            final Directory dir = this.spellIndex;
            final IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig((Analyzer)null).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            writer.close();
            this.swapSearcher(dir);
        }
    }
    
    public boolean exist(final String word) throws IOException {
        final IndexSearcher indexSearcher = this.obtainSearcher();
        try {
            return indexSearcher.getIndexReader().docFreq(new Term("word", word)) > 0;
        }
        finally {
            this.releaseSearcher(indexSearcher);
        }
    }
    
    public final void indexDictionary(final Dictionary dict, final IndexWriterConfig config, final boolean fullMerge) throws IOException {
        synchronized (this.modifyCurrentIndexLock) {
            this.ensureOpen();
            final Directory dir = this.spellIndex;
            final IndexWriter writer = new IndexWriter(dir, config);
            final IndexSearcher indexSearcher = this.obtainSearcher();
            final List<TermsEnum> termsEnums = new ArrayList<TermsEnum>();
            final IndexReader reader = this.searcher.getIndexReader();
            if (reader.maxDoc() > 0) {
                for (final LeafReaderContext ctx : reader.leaves()) {
                    final Terms terms = ctx.reader().terms("word");
                    if (terms != null) {
                        termsEnums.add(terms.iterator());
                    }
                }
            }
            final boolean isEmpty = termsEnums.isEmpty();
            try {
                final BytesRefIterator iter = (BytesRefIterator)dict.getEntryIterator();
                BytesRef currentTerm;
            Label_0146:
                while ((currentTerm = iter.next()) != null) {
                    final String word = currentTerm.utf8ToString();
                    final int len = word.length();
                    if (len < 3) {
                        continue;
                    }
                    if (!isEmpty) {
                        for (final TermsEnum te : termsEnums) {
                            if (te.seekExact(currentTerm)) {
                                continue Label_0146;
                            }
                        }
                    }
                    final Document doc = createDocument(word, getMin(len), getMax(len));
                    writer.addDocument((Iterable)doc);
                }
            }
            finally {
                this.releaseSearcher(indexSearcher);
            }
            if (fullMerge) {
                writer.forceMerge(1);
            }
            writer.close();
            this.swapSearcher(dir);
        }
    }
    
    private static int getMin(final int l) {
        if (l > 5) {
            return 3;
        }
        if (l == 5) {
            return 2;
        }
        return 1;
    }
    
    private static int getMax(final int l) {
        if (l > 5) {
            return 4;
        }
        if (l == 5) {
            return 3;
        }
        return 2;
    }
    
    private static Document createDocument(final String text, final int ng1, final int ng2) {
        final Document doc = new Document();
        final Field f = (Field)new StringField("word", text, Field.Store.YES);
        doc.add((IndexableField)f);
        addGram(text, doc, ng1, ng2);
        return doc;
    }
    
    private static void addGram(final String text, final Document doc, final int ng1, final int ng2) {
        final int len = text.length();
        for (int ng3 = ng1; ng3 <= ng2; ++ng3) {
            final String key = "gram" + ng3;
            String end = null;
            for (int i = 0; i < len - ng3 + 1; ++i) {
                final String gram = text.substring(i, i + ng3);
                final FieldType ft = new FieldType(StringField.TYPE_NOT_STORED);
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
                final Field ngramField = new Field(key, gram, ft);
                doc.add((IndexableField)ngramField);
                if (i == 0) {
                    final Field startField = (Field)new StringField("start" + ng3, gram, Field.Store.NO);
                    doc.add((IndexableField)startField);
                }
                end = gram;
            }
            if (end != null) {
                final Field endField = (Field)new StringField("end" + ng3, end, Field.Store.NO);
                doc.add((IndexableField)endField);
            }
        }
    }
    
    private IndexSearcher obtainSearcher() {
        synchronized (this.searcherLock) {
            this.ensureOpen();
            this.searcher.getIndexReader().incRef();
            return this.searcher;
        }
    }
    
    private void releaseSearcher(final IndexSearcher aSearcher) throws IOException {
        aSearcher.getIndexReader().decRef();
    }
    
    private void ensureOpen() {
        if (this.closed) {
            throw new AlreadyClosedException("Spellchecker has been closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.searcherLock) {
            this.ensureOpen();
            this.closed = true;
            if (this.searcher != null) {
                this.searcher.getIndexReader().close();
            }
            this.searcher = null;
        }
    }
    
    private void swapSearcher(final Directory dir) throws IOException {
        final IndexSearcher indexSearcher = this.createSearcher(dir);
        synchronized (this.searcherLock) {
            if (this.closed) {
                indexSearcher.getIndexReader().close();
                throw new AlreadyClosedException("Spellchecker has been closed");
            }
            if (this.searcher != null) {
                this.searcher.getIndexReader().close();
            }
            this.searcher = indexSearcher;
            this.spellIndex = dir;
        }
    }
    
    IndexSearcher createSearcher(final Directory dir) throws IOException {
        return new IndexSearcher((IndexReader)DirectoryReader.open(dir));
    }
    
    boolean isClosed() {
        return this.closed;
    }
}
