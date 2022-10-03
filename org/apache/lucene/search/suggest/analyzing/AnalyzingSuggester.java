package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.analysis.TokenStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import java.util.Set;
import org.apache.lucene.util.IntsRef;
import java.nio.file.Path;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.HashSet;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.automaton.LimitedFiniteStringsIterator;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.BytesRefBuilder;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.util.OfflineSorter;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.analysis.TokenStreamToAutomaton;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.Accountables;
import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import java.util.Comparator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.search.suggest.Lookup;

public class AnalyzingSuggester extends Lookup
{
    private FST<PairOutputs.Pair<Long, BytesRef>> fst;
    private final Analyzer indexAnalyzer;
    private final Analyzer queryAnalyzer;
    private final boolean exactFirst;
    private final boolean preserveSep;
    public static final int EXACT_FIRST = 1;
    public static final int PRESERVE_SEP = 2;
    private static final int SEP_LABEL = 31;
    private static final int END_BYTE = 0;
    private final int maxSurfaceFormsPerAnalyzedForm;
    private final int maxGraphExpansions;
    private int maxAnalyzedPathsForOneInput;
    private boolean hasPayloads;
    private static final int PAYLOAD_SEP = 31;
    private boolean preservePositionIncrements;
    private long count;
    static final Comparator<PairOutputs.Pair<Long, BytesRef>> weightComparator;
    
    public AnalyzingSuggester(final Analyzer analyzer) {
        this(analyzer, analyzer, 3, 256, -1, true);
    }
    
    public AnalyzingSuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer) {
        this(indexAnalyzer, queryAnalyzer, 3, 256, -1, true);
    }
    
    public AnalyzingSuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int options, final int maxSurfaceFormsPerAnalyzedForm, final int maxGraphExpansions, final boolean preservePositionIncrements) {
        this.fst = null;
        this.count = 0L;
        this.indexAnalyzer = indexAnalyzer;
        this.queryAnalyzer = queryAnalyzer;
        if ((options & 0xFFFFFFFC) != 0x0) {
            throw new IllegalArgumentException("options should only contain EXACT_FIRST and PRESERVE_SEP; got " + options);
        }
        this.exactFirst = ((options & 0x1) != 0x0);
        this.preserveSep = ((options & 0x2) != 0x0);
        if (maxSurfaceFormsPerAnalyzedForm <= 0 || maxSurfaceFormsPerAnalyzedForm > 256) {
            throw new IllegalArgumentException("maxSurfaceFormsPerAnalyzedForm must be > 0 and < 256 (got: " + maxSurfaceFormsPerAnalyzedForm + ")");
        }
        this.maxSurfaceFormsPerAnalyzedForm = maxSurfaceFormsPerAnalyzedForm;
        if (maxGraphExpansions < 1 && maxGraphExpansions != -1) {
            throw new IllegalArgumentException("maxGraphExpansions must -1 (no limit) or > 0 (got: " + maxGraphExpansions + ")");
        }
        this.maxGraphExpansions = maxGraphExpansions;
        this.preservePositionIncrements = preservePositionIncrements;
    }
    
    public long ramBytesUsed() {
        return (this.fst == null) ? 0L : this.fst.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        if (this.fst == null) {
            return (Collection<Accountable>)Collections.emptyList();
        }
        return Collections.singletonList(Accountables.namedAccountable("fst", (Accountable)this.fst));
    }
    
    private Automaton replaceSep(final Automaton a) {
        final int numStates = a.getNumStates();
        final Automaton.Builder result = new Automaton.Builder(numStates, a.getNumTransitions());
        result.copyStates(a);
        final Transition t = new Transition();
        final int[] topoSortStates = Operations.topoSortStates(a);
        for (int i = 0; i < topoSortStates.length; ++i) {
            final int state = topoSortStates[topoSortStates.length - 1 - i];
            for (int count = a.initTransition(state, t), j = 0; j < count; ++j) {
                a.getNextTransition(t);
                if (t.min == 31) {
                    assert t.max == 31;
                    if (this.preserveSep) {
                        result.addTransition(state, t.dest, 31);
                    }
                    else {
                        result.addEpsilon(state, t.dest);
                    }
                }
                else if (t.min == 30) {
                    assert t.max == 30;
                    result.addEpsilon(state, t.dest);
                }
                else {
                    result.addTransition(state, t.dest, t.min, t.max);
                }
            }
        }
        return result.finish();
    }
    
    protected Automaton convertAutomaton(final Automaton a) {
        return a;
    }
    
    TokenStreamToAutomaton getTokenStreamToAutomaton() {
        final TokenStreamToAutomaton tsta = new TokenStreamToAutomaton();
        tsta.setPreservePositionIncrements(this.preservePositionIncrements);
        return tsta;
    }
    
    @Override
    public void build(final InputIterator iterator) throws IOException {
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        final String prefix = this.getClass().getSimpleName();
        final Path directory = OfflineSorter.getDefaultTempDir();
        final Path tempInput = Files.createTempFile(directory, prefix, ".input", (FileAttribute<?>[])new FileAttribute[0]);
        final Path tempSorted = Files.createTempFile(directory, prefix, ".sorted", (FileAttribute<?>[])new FileAttribute[0]);
        this.hasPayloads = iterator.hasPayloads();
        final OfflineSorter.ByteSequencesWriter writer = new OfflineSorter.ByteSequencesWriter(tempInput);
        OfflineSorter.ByteSequencesReader reader = null;
        final BytesRefBuilder scratch = new BytesRefBuilder();
        final TokenStreamToAutomaton ts2a = this.getTokenStreamToAutomaton();
        boolean success = false;
        this.count = 0L;
        byte[] buffer = new byte[8];
        try {
            final ByteArrayDataOutput output = new ByteArrayDataOutput(buffer);
            BytesRef surfaceForm;
            while ((surfaceForm = iterator.next()) != null) {
                final LimitedFiniteStringsIterator finiteStrings = new LimitedFiniteStringsIterator(this.toAutomaton(surfaceForm, ts2a), this.maxGraphExpansions);
                IntsRef string;
                while ((string = finiteStrings.next()) != null) {
                    Util.toBytesRef(string, scratch);
                    if (scratch.length() > 32765) {
                        throw new IllegalArgumentException("cannot handle analyzed forms > 32765 in length (got " + scratch.length() + ")");
                    }
                    final short analyzedLength = (short)scratch.length();
                    int requiredLength = analyzedLength + 4 + surfaceForm.length + 2;
                    BytesRef payload;
                    if (this.hasPayloads) {
                        if (surfaceForm.length > 32765) {
                            throw new IllegalArgumentException("cannot handle surface form > 32765 in length (got " + surfaceForm.length + ")");
                        }
                        payload = iterator.payload();
                        requiredLength += payload.length + 2;
                    }
                    else {
                        payload = null;
                    }
                    buffer = ArrayUtil.grow(buffer, requiredLength);
                    output.reset(buffer);
                    output.writeShort(analyzedLength);
                    output.writeBytes(scratch.bytes(), 0, scratch.length());
                    output.writeInt(encodeWeight(iterator.weight()));
                    if (this.hasPayloads) {
                        for (int i = 0; i < surfaceForm.length; ++i) {
                            if (surfaceForm.bytes[i] == 31) {
                                throw new IllegalArgumentException("surface form cannot contain unit separator character U+001F; this character is reserved");
                            }
                        }
                        output.writeShort((short)surfaceForm.length);
                        output.writeBytes(surfaceForm.bytes, surfaceForm.offset, surfaceForm.length);
                        output.writeBytes(payload.bytes, payload.offset, payload.length);
                    }
                    else {
                        output.writeBytes(surfaceForm.bytes, surfaceForm.offset, surfaceForm.length);
                    }
                    assert output.getPosition() == requiredLength : output.getPosition() + " vs " + requiredLength;
                    writer.write(buffer, 0, output.getPosition());
                    ++this.count;
                }
                this.maxAnalyzedPathsForOneInput = Math.max(this.maxAnalyzedPathsForOneInput, finiteStrings.size());
            }
            writer.close();
            new OfflineSorter((Comparator)new AnalyzingComparator(this.hasPayloads)).sort(tempInput, tempSorted);
            Files.delete(tempInput);
            reader = new OfflineSorter.ByteSequencesReader(tempSorted);
            final PairOutputs<Long, BytesRef> outputs = (PairOutputs<Long, BytesRef>)new PairOutputs((Outputs)PositiveIntOutputs.getSingleton(), (Outputs)ByteSequenceOutputs.getSingleton());
            final Builder<PairOutputs.Pair<Long, BytesRef>> builder = (Builder<PairOutputs.Pair<Long, BytesRef>>)new Builder(FST.INPUT_TYPE.BYTE1, (Outputs)outputs);
            BytesRefBuilder previousAnalyzed = null;
            final BytesRefBuilder analyzed = new BytesRefBuilder();
            final BytesRef surface = new BytesRef();
            final IntsRefBuilder scratchInts = new IntsRefBuilder();
            final ByteArrayDataInput input = new ByteArrayDataInput();
            final Set<BytesRef> seenSurfaceForms = new HashSet<BytesRef>();
            int dedup = 0;
            while (reader.read(scratch)) {
                input.reset(scratch.bytes(), 0, scratch.length());
                final short analyzedLength2 = input.readShort();
                analyzed.grow(analyzedLength2 + 2);
                input.readBytes(analyzed.bytes(), 0, (int)analyzedLength2);
                analyzed.setLength((int)analyzedLength2);
                final long cost = input.readInt();
                surface.bytes = scratch.bytes();
                if (this.hasPayloads) {
                    surface.length = input.readShort();
                    surface.offset = input.getPosition();
                }
                else {
                    surface.offset = input.getPosition();
                    surface.length = scratch.length() - surface.offset;
                }
                if (previousAnalyzed == null) {
                    previousAnalyzed = new BytesRefBuilder();
                    previousAnalyzed.copyBytes(analyzed.get());
                    seenSurfaceForms.add(BytesRef.deepCopyOf(surface));
                }
                else if (analyzed.get().equals((Object)previousAnalyzed.get())) {
                    if (++dedup >= this.maxSurfaceFormsPerAnalyzedForm) {
                        continue;
                    }
                    if (seenSurfaceForms.contains(surface)) {
                        continue;
                    }
                    seenSurfaceForms.add(BytesRef.deepCopyOf(surface));
                }
                else {
                    dedup = 0;
                    previousAnalyzed.copyBytes(analyzed);
                    seenSurfaceForms.clear();
                    seenSurfaceForms.add(BytesRef.deepCopyOf(surface));
                }
                analyzed.append((byte)0);
                analyzed.append((byte)dedup);
                Util.toIntsRef(analyzed.get(), scratchInts);
                if (!this.hasPayloads) {
                    builder.add(scratchInts.get(), (Object)outputs.newPair((Object)cost, (Object)BytesRef.deepCopyOf(surface)));
                }
                else {
                    final int payloadOffset = input.getPosition() + surface.length;
                    final int payloadLength = scratch.length() - payloadOffset;
                    final BytesRef br = new BytesRef(surface.length + 1 + payloadLength);
                    System.arraycopy(surface.bytes, surface.offset, br.bytes, 0, surface.length);
                    br.bytes[surface.length] = 31;
                    System.arraycopy(scratch.bytes(), payloadOffset, br.bytes, surface.length + 1, payloadLength);
                    br.length = br.bytes.length;
                    builder.add(scratchInts.get(), (Object)outputs.newPair((Object)cost, (Object)br));
                }
            }
            this.fst = (FST<PairOutputs.Pair<Long, BytesRef>>)builder.finish();
            success = true;
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)reader, (Closeable)writer });
            if (success) {
                IOUtils.deleteFilesIfExist(new Path[] { tempInput, tempSorted });
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { tempInput, tempSorted });
            }
        }
    }
    
    @Override
    public boolean store(final DataOutput output) throws IOException {
        output.writeVLong(this.count);
        if (this.fst == null) {
            return false;
        }
        this.fst.save(output);
        output.writeVInt(this.maxAnalyzedPathsForOneInput);
        output.writeByte((byte)(byte)(this.hasPayloads ? 1 : 0));
        return true;
    }
    
    @Override
    public boolean load(final DataInput input) throws IOException {
        this.count = input.readVLong();
        this.fst = (FST<PairOutputs.Pair<Long, BytesRef>>)new FST(input, (Outputs)new PairOutputs((Outputs)PositiveIntOutputs.getSingleton(), (Outputs)ByteSequenceOutputs.getSingleton()));
        this.maxAnalyzedPathsForOneInput = input.readVInt();
        this.hasPayloads = (input.readByte() == 1);
        return true;
    }
    
    private LookupResult getLookupResult(final Long output1, final BytesRef output2, final CharsRefBuilder spare) {
        LookupResult result;
        if (this.hasPayloads) {
            int sepIndex = -1;
            for (int i = 0; i < output2.length; ++i) {
                if (output2.bytes[output2.offset + i] == 31) {
                    sepIndex = i;
                    break;
                }
            }
            assert sepIndex != -1;
            spare.grow(sepIndex);
            final int payloadLen = output2.length - sepIndex - 1;
            spare.copyUTF8Bytes(output2.bytes, output2.offset, sepIndex);
            final BytesRef payload = new BytesRef(payloadLen);
            System.arraycopy(output2.bytes, sepIndex + 1, payload.bytes, 0, payloadLen);
            payload.length = payloadLen;
            result = new LookupResult(spare.toString(), decodeWeight(output1), payload);
        }
        else {
            spare.grow(output2.length);
            spare.copyUTF8Bytes(output2);
            result = new LookupResult(spare.toString(), decodeWeight(output1));
        }
        return result;
    }
    
    private boolean sameSurfaceForm(final BytesRef key, final BytesRef output2) {
        if (!this.hasPayloads) {
            return key.bytesEquals(output2);
        }
        if (key.length >= output2.length) {
            return false;
        }
        for (int i = 0; i < key.length; ++i) {
            if (key.bytes[key.offset + i] != output2.bytes[output2.offset + i]) {
                return false;
            }
        }
        return output2.bytes[output2.offset + key.length] == 31;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, final int num) {
        assert num > 0;
        if (onlyMorePopular) {
            throw new IllegalArgumentException("this suggester only works with onlyMorePopular=false");
        }
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        if (this.fst == null) {
            return Collections.emptyList();
        }
        for (int i = 0; i < key.length(); ++i) {
            if (key.charAt(i) == '\u001e') {
                throw new IllegalArgumentException("lookup key cannot contain HOLE character U+001E; this character is reserved");
            }
            if (key.charAt(i) == '\u001f') {
                throw new IllegalArgumentException("lookup key cannot contain unit separator character U+001F; this character is reserved");
            }
        }
        final BytesRef utf8Key = new BytesRef(key);
        try {
            final Automaton lookupAutomaton = this.toLookupAutomaton(key);
            final CharsRefBuilder spare = new CharsRefBuilder();
            final FST.BytesReader bytesReader = this.fst.getBytesReader();
            final FST.Arc<PairOutputs.Pair<Long, BytesRef>> scratchArc = (FST.Arc<PairOutputs.Pair<Long, BytesRef>>)new FST.Arc();
            final List<LookupResult> results = new ArrayList<LookupResult>();
            List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> prefixPaths = FSTUtil.intersectPrefixPaths(this.convertAutomaton(lookupAutomaton), this.fst);
            if (this.exactFirst) {
                int count = 0;
                for (final FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>> path : prefixPaths) {
                    if (this.fst.findTargetArc(0, (FST.Arc)path.fstNode, (FST.Arc)scratchArc, bytesReader) != null) {
                        ++count;
                    }
                }
                final Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>> searcher = (Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>>)new Util.TopNSearcher((FST)this.fst, count * this.maxSurfaceFormsPerAnalyzedForm, count * this.maxSurfaceFormsPerAnalyzedForm, (Comparator)AnalyzingSuggester.weightComparator);
                for (final FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>> path2 : prefixPaths) {
                    if (this.fst.findTargetArc(0, (FST.Arc)path2.fstNode, (FST.Arc)scratchArc, bytesReader) != null) {
                        searcher.addStartPaths((FST.Arc)scratchArc, this.fst.outputs.add((Object)path2.output, scratchArc.output), false, path2.input);
                    }
                }
                final Util.TopResults<PairOutputs.Pair<Long, BytesRef>> completions = (Util.TopResults<PairOutputs.Pair<Long, BytesRef>>)searcher.search();
                assert completions.isComplete;
                for (final Util.Result<PairOutputs.Pair<Long, BytesRef>> completion : completions) {
                    final BytesRef output2 = (BytesRef)((PairOutputs.Pair)completion.output).output2;
                    if (this.sameSurfaceForm(utf8Key, output2)) {
                        results.add(this.getLookupResult((Long)((PairOutputs.Pair)completion.output).output1, output2, spare));
                        break;
                    }
                }
                if (results.size() == num) {
                    return results;
                }
            }
            final Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>> searcher2 = new Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>>(this.fst, num - results.size(), num * this.maxAnalyzedPathsForOneInput, AnalyzingSuggester.weightComparator) {
                private final Set<BytesRef> seen = new HashSet<BytesRef>();
                
                protected boolean acceptResult(final IntsRef input, final PairOutputs.Pair<Long, BytesRef> output) {
                    if (this.seen.contains(output.output2)) {
                        return false;
                    }
                    this.seen.add((BytesRef)output.output2);
                    if (!AnalyzingSuggester.this.exactFirst) {
                        return true;
                    }
                    if (!AnalyzingSuggester.this.sameSurfaceForm(utf8Key, (BytesRef)output.output2)) {
                        return true;
                    }
                    assert results.size() == 1;
                    return false;
                }
            };
            prefixPaths = this.getFullPrefixPaths(prefixPaths, lookupAutomaton, this.fst);
            for (final FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>> path : prefixPaths) {
                searcher2.addStartPaths((FST.Arc)path.fstNode, (Object)path.output, true, path.input);
            }
            final Util.TopResults<PairOutputs.Pair<Long, BytesRef>> completions2 = (Util.TopResults<PairOutputs.Pair<Long, BytesRef>>)searcher2.search();
            assert completions2.isComplete;
            for (final Util.Result<PairOutputs.Pair<Long, BytesRef>> completion2 : completions2) {
                final LookupResult result = this.getLookupResult((Long)((PairOutputs.Pair)completion2.output).output1, (BytesRef)((PairOutputs.Pair)completion2.output).output2, spare);
                results.add(result);
                if (results.size() == num) {
                    break;
                }
            }
            return results;
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    protected List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> getFullPrefixPaths(final List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> prefixPaths, final Automaton lookupAutomaton, final FST<PairOutputs.Pair<Long, BytesRef>> fst) throws IOException {
        return prefixPaths;
    }
    
    final Automaton toAutomaton(final BytesRef surfaceForm, final TokenStreamToAutomaton ts2a) throws IOException {
        Automaton automaton;
        try (final TokenStream ts = this.indexAnalyzer.tokenStream("", surfaceForm.utf8ToString())) {
            automaton = ts2a.toAutomaton(ts);
        }
        automaton = this.replaceSep(automaton);
        automaton = this.convertAutomaton(automaton);
        return automaton;
    }
    
    final Automaton toLookupAutomaton(final CharSequence key) throws IOException {
        Automaton automaton = null;
        try (final TokenStream ts = this.queryAnalyzer.tokenStream("", key.toString())) {
            automaton = this.getTokenStreamToAutomaton().toAutomaton(ts);
        }
        automaton = this.replaceSep(automaton);
        automaton = Operations.determinize(automaton, 10000);
        return automaton;
    }
    
    public Object get(final CharSequence key) {
        throw new UnsupportedOperationException();
    }
    
    private static int decodeWeight(final long encoded) {
        return (int)(2147483647L - encoded);
    }
    
    private static int encodeWeight(final long value) {
        if (value < 0L || value > 2147483647L) {
            throw new UnsupportedOperationException("cannot encode value: " + value);
        }
        return Integer.MAX_VALUE - (int)value;
    }
    
    static {
        weightComparator = new Comparator<PairOutputs.Pair<Long, BytesRef>>() {
            @Override
            public int compare(final PairOutputs.Pair<Long, BytesRef> left, final PairOutputs.Pair<Long, BytesRef> right) {
                return ((Long)left.output1).compareTo((Long)right.output1);
            }
        };
    }
    
    private static class AnalyzingComparator implements Comparator<BytesRef>
    {
        private final boolean hasPayloads;
        private final ByteArrayDataInput readerA;
        private final ByteArrayDataInput readerB;
        private final BytesRef scratchA;
        private final BytesRef scratchB;
        
        public AnalyzingComparator(final boolean hasPayloads) {
            this.readerA = new ByteArrayDataInput();
            this.readerB = new ByteArrayDataInput();
            this.scratchA = new BytesRef();
            this.scratchB = new BytesRef();
            this.hasPayloads = hasPayloads;
        }
        
        @Override
        public int compare(final BytesRef a, final BytesRef b) {
            this.readerA.reset(a.bytes, a.offset, a.length);
            this.scratchA.length = this.readerA.readShort();
            this.scratchA.bytes = a.bytes;
            this.scratchA.offset = this.readerA.getPosition();
            this.readerB.reset(b.bytes, b.offset, b.length);
            this.scratchB.bytes = b.bytes;
            this.scratchB.length = this.readerB.readShort();
            this.scratchB.offset = this.readerB.getPosition();
            final int cmp = this.scratchA.compareTo(this.scratchB);
            if (cmp != 0) {
                return cmp;
            }
            this.readerA.skipBytes((long)this.scratchA.length);
            this.readerB.skipBytes((long)this.scratchB.length);
            final long aCost = this.readerA.readInt();
            final long bCost = this.readerB.readInt();
            assert decodeWeight(aCost) >= 0;
            assert decodeWeight(bCost) >= 0;
            if (aCost < bCost) {
                return -1;
            }
            if (aCost > bCost) {
                return 1;
            }
            if (this.hasPayloads) {
                this.scratchA.length = this.readerA.readShort();
                this.scratchB.length = this.readerB.readShort();
                this.scratchA.offset = this.readerA.getPosition();
                this.scratchB.offset = this.readerB.getPosition();
            }
            else {
                this.scratchA.offset = this.readerA.getPosition();
                this.scratchB.offset = this.readerB.getPosition();
                this.scratchA.length = a.length - this.scratchA.offset;
                this.scratchB.length = b.length - this.scratchB.offset;
            }
            return this.scratchA.compareTo(this.scratchB);
        }
    }
}
