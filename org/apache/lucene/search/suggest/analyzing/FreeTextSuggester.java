package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.store.ByteArrayDataInput;
import java.util.Iterator;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import java.util.Set;
import java.util.List;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import java.nio.file.Path;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.IOException;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.util.Accountables;
import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import java.util.Comparator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.search.suggest.Lookup;

public class FreeTextSuggester extends Lookup
{
    public static final String CODEC_NAME = "freetextsuggest";
    public static final int VERSION_START = 0;
    public static final int VERSION_CURRENT = 0;
    public static final int DEFAULT_GRAMS = 2;
    public static final double ALPHA = 0.4;
    private FST<Long> fst;
    private final Analyzer indexAnalyzer;
    private long totTokens;
    private final Analyzer queryAnalyzer;
    private final int grams;
    private final byte separator;
    private long count;
    public static final byte DEFAULT_SEPARATOR = 30;
    static final Comparator<Long> weightComparator;
    
    public FreeTextSuggester(final Analyzer analyzer) {
        this(analyzer, analyzer, 2);
    }
    
    public FreeTextSuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer) {
        this(indexAnalyzer, queryAnalyzer, 2);
    }
    
    public FreeTextSuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int grams) {
        this(indexAnalyzer, queryAnalyzer, grams, (byte)30);
    }
    
    public FreeTextSuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int grams, final byte separator) {
        this.count = 0L;
        this.grams = grams;
        this.indexAnalyzer = this.addShingles(indexAnalyzer);
        this.queryAnalyzer = this.addShingles(queryAnalyzer);
        if (grams < 1) {
            throw new IllegalArgumentException("grams must be >= 1");
        }
        if ((separator & 0x80) != 0x0) {
            throw new IllegalArgumentException("separator must be simple ascii character");
        }
        this.separator = separator;
    }
    
    public long ramBytesUsed() {
        if (this.fst == null) {
            return 0L;
        }
        return this.fst.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        if (this.fst == null) {
            return (Collection<Accountable>)Collections.emptyList();
        }
        return Collections.singletonList(Accountables.namedAccountable("fst", (Accountable)this.fst));
    }
    
    private Analyzer addShingles(final Analyzer other) {
        if (this.grams == 1) {
            return other;
        }
        return (Analyzer)new AnalyzerWrapper(other.getReuseStrategy()) {
            protected Analyzer getWrappedAnalyzer(final String fieldName) {
                return other;
            }
            
            protected Analyzer.TokenStreamComponents wrapComponents(final String fieldName, final Analyzer.TokenStreamComponents components) {
                final ShingleFilter shingles = new ShingleFilter(components.getTokenStream(), 2, FreeTextSuggester.this.grams);
                shingles.setTokenSeparator(Character.toString((char)FreeTextSuggester.this.separator));
                return new Analyzer.TokenStreamComponents(components.getTokenizer(), (TokenStream)shingles);
            }
        };
    }
    
    @Override
    public void build(final InputIterator iterator) throws IOException {
        this.build(iterator, 16.0);
    }
    
    public void build(final InputIterator iterator, final double ramBufferSizeMB) throws IOException {
        if (iterator.hasPayloads()) {
            throw new IllegalArgumentException("this suggester doesn't support payloads");
        }
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        final String prefix = this.getClass().getSimpleName();
        final Path tempIndexPath = Files.createTempDirectory(prefix + ".index.", (FileAttribute<?>[])new FileAttribute[0]);
        final Directory dir = (Directory)FSDirectory.open(tempIndexPath);
        final IndexWriterConfig iwc = new IndexWriterConfig(this.indexAnalyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(ramBufferSizeMB);
        final IndexWriter writer = new IndexWriter(dir, iwc);
        final FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        ft.setOmitNorms(true);
        ft.freeze();
        final Document doc = new Document();
        final Field field = new Field("body", "", ft);
        doc.add((IndexableField)field);
        this.totTokens = 0L;
        IndexReader reader = null;
        boolean success = false;
        this.count = 0L;
        try {
            while (true) {
                final BytesRef surfaceForm = iterator.next();
                if (surfaceForm == null) {
                    break;
                }
                field.setStringValue(surfaceForm.utf8ToString());
                writer.addDocument((Iterable)doc);
                ++this.count;
            }
            reader = (IndexReader)DirectoryReader.open(writer);
            final Terms terms = MultiFields.getTerms(reader, "body");
            if (terms == null) {
                throw new IllegalArgumentException("need at least one suggestion");
            }
            final TermsEnum termsEnum = terms.iterator();
            final Outputs<Long> outputs = (Outputs<Long>)PositiveIntOutputs.getSingleton();
            final Builder<Long> builder = (Builder<Long>)new Builder(FST.INPUT_TYPE.BYTE1, (Outputs)outputs);
            final IntsRefBuilder scratchInts = new IntsRefBuilder();
            while (true) {
                final BytesRef term = termsEnum.next();
                if (term == null) {
                    this.fst = (FST<Long>)builder.finish();
                    if (this.fst == null) {
                        throw new IllegalArgumentException("need at least one suggestion");
                    }
                    writer.rollback();
                    success = true;
                    break;
                }
                else {
                    final int ngramCount = this.countGrams(term);
                    if (ngramCount > this.grams) {
                        throw new IllegalArgumentException("tokens must not contain separator byte; got token=" + term + " but gramCount=" + ngramCount + ", which is greater than expected max ngram size=" + this.grams);
                    }
                    if (ngramCount == 1) {
                        this.totTokens += termsEnum.totalTermFreq();
                    }
                    builder.add(Util.toIntsRef(term, scratchInts), (Object)this.encodeWeight(termsEnum.totalTermFreq()));
                }
            }
        }
        finally {
            try {
                if (success) {
                    IOUtils.close(new Closeable[] { (Closeable)reader, (Closeable)dir });
                }
                else {
                    IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)reader, (Closeable)writer, (Closeable)dir });
                }
            }
            finally {
                IOUtils.rm(new Path[] { tempIndexPath });
            }
        }
    }
    
    @Override
    public boolean store(final DataOutput output) throws IOException {
        CodecUtil.writeHeader(output, "freetextsuggest", 0);
        output.writeVLong(this.count);
        output.writeByte(this.separator);
        output.writeVInt(this.grams);
        output.writeVLong(this.totTokens);
        this.fst.save(output);
        return true;
    }
    
    @Override
    public boolean load(final DataInput input) throws IOException {
        CodecUtil.checkHeader(input, "freetextsuggest", 0, 0);
        this.count = input.readVLong();
        final byte separatorOrig = input.readByte();
        if (separatorOrig != this.separator) {
            throw new IllegalStateException("separator=" + this.separator + " is incorrect: original model was built with separator=" + separatorOrig);
        }
        final int gramsOrig = input.readVInt();
        if (gramsOrig != this.grams) {
            throw new IllegalStateException("grams=" + this.grams + " is incorrect: original model was built with grams=" + gramsOrig);
        }
        this.totTokens = input.readVLong();
        this.fst = (FST<Long>)new FST(input, (Outputs)PositiveIntOutputs.getSingleton());
        return true;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final boolean onlyMorePopular, final int num) {
        return this.lookup(key, null, onlyMorePopular, num);
    }
    
    public List<LookupResult> lookup(final CharSequence key, final int num) {
        return this.lookup(key, null, true, num);
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) {
        try {
            return this.lookup(key, contexts, num);
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    private int countGrams(final BytesRef token) {
        int count = 1;
        for (int i = 0; i < token.length; ++i) {
            if (token.bytes[token.offset + i] == this.separator) {
                ++count;
            }
        }
        return count;
    }
    
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final int num) throws IOException {
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        if (this.fst == null) {
            throw new IllegalStateException("Lookup not supported at this time");
        }
        try (final TokenStream ts = this.queryAnalyzer.tokenStream("", key.toString())) {
            final TermToBytesRefAttribute termBytesAtt = (TermToBytesRefAttribute)ts.addAttribute((Class)TermToBytesRefAttribute.class);
            final OffsetAttribute offsetAtt = (OffsetAttribute)ts.addAttribute((Class)OffsetAttribute.class);
            final PositionLengthAttribute posLenAtt = (PositionLengthAttribute)ts.addAttribute((Class)PositionLengthAttribute.class);
            final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)ts.addAttribute((Class)PositionIncrementAttribute.class);
            ts.reset();
            final BytesRefBuilder[] lastTokens = new BytesRefBuilder[this.grams];
            int maxEndOffset = -1;
            boolean sawRealToken = false;
            while (ts.incrementToken()) {
                final BytesRef tokenBytes = termBytesAtt.getBytesRef();
                sawRealToken |= (tokenBytes.length > 0);
                final int gramCount = posLenAtt.getPositionLength();
                assert gramCount <= this.grams;
                if (this.countGrams(tokenBytes) != gramCount) {
                    throw new IllegalArgumentException("tokens must not contain separator byte; got token=" + tokenBytes + " but gramCount=" + gramCount + " does not match recalculated count=" + this.countGrams(tokenBytes));
                }
                maxEndOffset = Math.max(maxEndOffset, offsetAtt.endOffset());
                final BytesRefBuilder b = new BytesRefBuilder();
                b.append(tokenBytes);
                lastTokens[gramCount - 1] = b;
            }
            ts.end();
            if (!sawRealToken) {
                throw new IllegalArgumentException("no tokens produced by analyzer, or the only tokens were empty strings");
            }
            final int endPosInc = posIncAtt.getPositionIncrement();
            final boolean lastTokenEnded = offsetAtt.endOffset() > maxEndOffset || endPosInc > 0;
            if (lastTokenEnded) {
                for (int i = this.grams - 1; i > 0; --i) {
                    final BytesRefBuilder token = lastTokens[i - 1];
                    if (token != null) {
                        token.append(this.separator);
                        lastTokens[i] = token;
                    }
                }
                lastTokens[0] = new BytesRefBuilder();
            }
            final FST.Arc<Long> arc = (FST.Arc<Long>)new FST.Arc();
            final FST.BytesReader bytesReader = this.fst.getBytesReader();
            double backoff = 1.0;
            final List<LookupResult> results = new ArrayList<LookupResult>(num);
            final Set<BytesRef> seen = new HashSet<BytesRef>();
            for (int gram = this.grams - 1; gram >= 0; --gram) {
                final BytesRefBuilder token2 = lastTokens[gram];
                if (token2 != null) {
                    if (token2.length() != 0 || key.length() <= 0) {
                        if (endPosInc > 0 && gram <= endPosInc) {
                            break;
                        }
                        Long prefixOutput = null;
                        try {
                            prefixOutput = this.lookupPrefix(this.fst, bytesReader, token2.get(), arc);
                        }
                        catch (final IOException bogus) {
                            throw new RuntimeException(bogus);
                        }
                        if (prefixOutput == null) {
                            backoff *= 0.4;
                        }
                        else {
                            long contextCount = this.totTokens;
                            BytesRef lastTokenFragment = null;
                            int j = token2.length() - 1;
                            while (j >= 0) {
                                if (token2.byteAt(j) == this.separator) {
                                    final BytesRef context = new BytesRef(token2.bytes(), 0, j);
                                    final Long output = (Long)Util.get((FST)this.fst, Util.toIntsRef(context, new IntsRefBuilder()));
                                    assert output != null;
                                    contextCount = this.decodeWeight(output);
                                    lastTokenFragment = new BytesRef(token2.bytes(), j + 1, token2.length() - j - 1);
                                    break;
                                }
                                else {
                                    --j;
                                }
                            }
                            final BytesRefBuilder finalLastToken = new BytesRefBuilder();
                            if (lastTokenFragment == null) {
                                finalLastToken.copyBytes(token2.get());
                            }
                            else {
                                finalLastToken.copyBytes(lastTokenFragment);
                            }
                            final CharsRefBuilder spare = new CharsRefBuilder();
                            Util.TopResults<Long> completions = null;
                            try {
                                final Util.TopNSearcher<Long> searcher = new Util.TopNSearcher<Long>(this.fst, num, num + seen.size(), FreeTextSuggester.weightComparator) {
                                    BytesRefBuilder scratchBytes = new BytesRefBuilder();
                                    
                                    protected void addIfCompetitive(final Util.FSTPath<Long> path) {
                                        if (path.arc.label != FreeTextSuggester.this.separator) {
                                            super.addIfCompetitive((Util.FSTPath)path);
                                        }
                                    }
                                    
                                    protected boolean acceptResult(final IntsRef input, final Long output) {
                                        Util.toBytesRef(input, this.scratchBytes);
                                        finalLastToken.grow(finalLastToken.length() + this.scratchBytes.length());
                                        final int lenSav = finalLastToken.length();
                                        finalLastToken.append(this.scratchBytes);
                                        final boolean ret = !seen.contains(finalLastToken.get());
                                        finalLastToken.setLength(lenSav);
                                        return ret;
                                    }
                                };
                                searcher.addStartPaths((FST.Arc)arc, (Object)prefixOutput, true, new IntsRefBuilder());
                                completions = (Util.TopResults<Long>)searcher.search();
                                assert completions.isComplete;
                            }
                            catch (final IOException bogus2) {
                                throw new RuntimeException(bogus2);
                            }
                            final int prefixLength = token2.length();
                            final BytesRefBuilder suffix = new BytesRefBuilder();
                            for (final Util.Result<Long> completion : completions) {
                                token2.setLength(prefixLength);
                                Util.toBytesRef(completion.input, suffix);
                                token2.append(suffix);
                                BytesRef lastToken = token2.get();
                                int k = token2.length() - 1;
                                while (k >= 0) {
                                    if (token2.byteAt(k) == this.separator) {
                                        assert token2.length() - k - 1 > 0;
                                        lastToken = new BytesRef(token2.bytes(), k + 1, token2.length() - k - 1);
                                        break;
                                    }
                                    else {
                                        --k;
                                    }
                                }
                                if (seen.contains(lastToken)) {
                                    continue;
                                }
                                seen.add(BytesRef.deepCopyOf(lastToken));
                                spare.copyUTF8Bytes(token2.get());
                                final LookupResult result = new LookupResult(spare.toString(), (long)(9.223372036854776E18 * backoff * this.decodeWeight((Long)completion.output) / contextCount));
                                results.add(result);
                                assert results.size() == seen.size();
                            }
                            backoff *= 0.4;
                        }
                    }
                }
            }
            Collections.sort(results, new Comparator<LookupResult>() {
                @Override
                public int compare(final LookupResult a, final LookupResult b) {
                    if (a.value > b.value) {
                        return -1;
                    }
                    if (a.value < b.value) {
                        return 1;
                    }
                    return ((String)a.key).compareTo((String)b.key);
                }
            });
            if (results.size() > num) {
                results.subList(num, results.size()).clear();
            }
            return results;
        }
    }
    
    private long encodeWeight(final long ngramCount) {
        return Long.MAX_VALUE - ngramCount;
    }
    
    private long decodeWeight(final Long output) {
        assert output != null;
        return (int)(Long.MAX_VALUE - output);
    }
    
    private Long lookupPrefix(final FST<Long> fst, final FST.BytesReader bytesReader, final BytesRef scratch, final FST.Arc<Long> arc) throws IOException {
        Long output = (Long)fst.outputs.getNoOutput();
        fst.getFirstArc((FST.Arc)arc);
        final byte[] bytes = scratch.bytes;
        int pos = scratch.offset;
        final int end = pos + scratch.length;
        while (pos < end) {
            if (fst.findTargetArc(bytes[pos++] & 0xFF, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                return null;
            }
            output = (Long)fst.outputs.add((Object)output, arc.output);
        }
        return output;
    }
    
    public Object get(final CharSequence key) {
        throw new UnsupportedOperationException();
    }
    
    static {
        weightComparator = new Comparator<Long>() {
            @Override
            public int compare(final Long left, final Long right) {
                return left.compareTo(right);
            }
        };
    }
    
    private static class AnalyzingComparator implements Comparator<BytesRef>
    {
        private final ByteArrayDataInput readerA;
        private final ByteArrayDataInput readerB;
        private final BytesRef scratchA;
        private final BytesRef scratchB;
        
        private AnalyzingComparator() {
            this.readerA = new ByteArrayDataInput();
            this.readerB = new ByteArrayDataInput();
            this.scratchA = new BytesRef();
            this.scratchB = new BytesRef();
        }
        
        @Override
        public int compare(final BytesRef a, final BytesRef b) {
            this.readerA.reset(a.bytes, a.offset, a.length);
            this.readerB.reset(b.bytes, b.offset, b.length);
            this.scratchA.length = this.readerA.readShort();
            this.scratchA.bytes = a.bytes;
            this.scratchA.offset = this.readerA.getPosition();
            this.scratchB.bytes = b.bytes;
            this.scratchB.length = this.readerB.readShort();
            this.scratchB.offset = this.readerB.getPosition();
            int cmp = this.scratchA.compareTo(this.scratchB);
            if (cmp != 0) {
                return cmp;
            }
            this.readerA.skipBytes((long)this.scratchA.length);
            this.readerB.skipBytes((long)this.scratchB.length);
            cmp = a.length - b.length;
            if (cmp != 0) {
                return cmp;
            }
            this.scratchA.offset = this.readerA.getPosition();
            this.scratchA.length = a.length - this.scratchA.offset;
            this.scratchB.offset = this.readerB.getPosition();
            this.scratchB.length = b.length - this.scratchB.offset;
            return this.scratchA.compareTo(this.scratchB);
        }
    }
}
