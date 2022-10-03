package org.apache.lucene.search.suggest.document;

import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.store.IndexInput;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.search.suggest.analyzing.FSTUtil;
import org.apache.lucene.util.Bits;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.Accountable;

public final class NRTSuggester implements Accountable
{
    private final FST<PairOutputs.Pair<Long, BytesRef>> fst;
    private final int maxAnalyzedPathsPerOutput;
    private final int payloadSep;
    private static final long MAX_TOP_N_QUEUE_SIZE = 5000L;
    
    private NRTSuggester(final FST<PairOutputs.Pair<Long, BytesRef>> fst, final int maxAnalyzedPathsPerOutput, final int payloadSep) {
        this.fst = fst;
        this.maxAnalyzedPathsPerOutput = maxAnalyzedPathsPerOutput;
        this.payloadSep = payloadSep;
    }
    
    public long ramBytesUsed() {
        return (this.fst == null) ? 0L : this.fst.ramBytesUsed();
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public void lookup(final CompletionScorer scorer, final Bits acceptDocs, final TopSuggestDocsCollector collector) throws IOException {
        final double liveDocsRatio = calculateLiveDocRatio(scorer.reader.numDocs(), scorer.reader.maxDoc());
        if (liveDocsRatio == -1.0) {
            return;
        }
        final List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> prefixPaths = FSTUtil.intersectPrefixPaths(scorer.automaton, this.fst);
        final int topN = collector.getCountToCollect() * prefixPaths.size();
        final int queueSize = this.getMaxTopNSearcherQueueSize(topN, scorer.reader.numDocs(), liveDocsRatio, scorer.filtered);
        final Comparator<PairOutputs.Pair<Long, BytesRef>> comparator = getComparator();
        final Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>> searcher = new Util.TopNSearcher<PairOutputs.Pair<Long, BytesRef>>(this.fst, topN, queueSize, comparator, new ScoringPathComparator(scorer)) {
            private final CharsRefBuilder spare = new CharsRefBuilder();
            
            protected boolean acceptResult(final Util.FSTPath<PairOutputs.Pair<Long, BytesRef>> path) {
                final int payloadSepIndex = PayLoadProcessor.parseSurfaceForm((BytesRef)((PairOutputs.Pair)path.cost).output2, NRTSuggester.this.payloadSep, this.spare);
                final int docID = PayLoadProcessor.parseDocID((BytesRef)((PairOutputs.Pair)path.cost).output2, payloadSepIndex);
                if (!scorer.accept(docID, acceptDocs)) {
                    return false;
                }
                try {
                    final float score = scorer.score((float)NRTSuggester.decode((long)((PairOutputs.Pair)path.cost).output1), path.boost);
                    collector.collect(docID, (CharSequence)this.spare.toCharsRef(), path.context, score);
                    return true;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        for (final FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>> path : prefixPaths) {
            scorer.weight.setNextMatch(path.input.get());
            searcher.addStartPaths((FST.Arc)path.fstNode, (Object)path.output, false, path.input, scorer.weight.boost(), scorer.weight.context());
        }
        searcher.search();
    }
    
    private static Comparator<PairOutputs.Pair<Long, BytesRef>> getComparator() {
        return new Comparator<PairOutputs.Pair<Long, BytesRef>>() {
            @Override
            public int compare(final PairOutputs.Pair<Long, BytesRef> o1, final PairOutputs.Pair<Long, BytesRef> o2) {
                return Long.compare((long)o1.output1, (long)o2.output1);
            }
        };
    }
    
    private int getMaxTopNSearcherQueueSize(final int topN, final int numDocs, final double liveDocsRatio, final boolean filterEnabled) {
        long maxQueueSize = topN * this.maxAnalyzedPathsPerOutput;
        assert liveDocsRatio <= 1.0;
        maxQueueSize /= (long)liveDocsRatio;
        if (filterEnabled) {
            maxQueueSize += numDocs / 2;
        }
        return (int)Math.min(5000L, maxQueueSize);
    }
    
    private static double calculateLiveDocRatio(final int numDocs, final int maxDocs) {
        return (numDocs > 0) ? (numDocs / (double)maxDocs) : -1.0;
    }
    
    public static NRTSuggester load(final IndexInput input) throws IOException {
        final FST<PairOutputs.Pair<Long, BytesRef>> fst = (FST<PairOutputs.Pair<Long, BytesRef>>)new FST((DataInput)input, (Outputs)new PairOutputs((Outputs)PositiveIntOutputs.getSingleton(), (Outputs)ByteSequenceOutputs.getSingleton()));
        final int maxAnalyzedPathsPerOutput = input.readVInt();
        final int endByte = input.readVInt();
        final int payloadSep = input.readVInt();
        return new NRTSuggester(fst, maxAnalyzedPathsPerOutput, payloadSep);
    }
    
    static long encode(final long input) {
        if (input < 0L || input > 2147483647L) {
            throw new UnsupportedOperationException("cannot encode value: " + input);
        }
        return 2147483647L - input;
    }
    
    static long decode(final long output) {
        assert output >= 0L && output <= 2147483647L : "decoded output: " + output + " is not within 0 and Integer.MAX_VALUE";
        return 2147483647L - output;
    }
    
    private static class ScoringPathComparator implements Comparator<Util.FSTPath<PairOutputs.Pair<Long, BytesRef>>>
    {
        private final CompletionScorer scorer;
        
        public ScoringPathComparator(final CompletionScorer scorer) {
            this.scorer = scorer;
        }
        
        @Override
        public int compare(final Util.FSTPath<PairOutputs.Pair<Long, BytesRef>> first, final Util.FSTPath<PairOutputs.Pair<Long, BytesRef>> second) {
            final int cmp = Float.compare(this.scorer.score((float)NRTSuggester.decode((long)((PairOutputs.Pair)second.cost).output1), second.boost), this.scorer.score((float)NRTSuggester.decode((long)((PairOutputs.Pair)first.cost).output1), first.boost));
            return (cmp != 0) ? cmp : first.input.get().compareTo(second.input.get());
        }
    }
    
    static final class PayLoadProcessor
    {
        private static final int MAX_DOC_ID_LEN_WITH_SEP = 6;
        
        static int parseSurfaceForm(final BytesRef output, final int payloadSep, final CharsRefBuilder spare) {
            int surfaceFormLen = -1;
            for (int i = 0; i < output.length; ++i) {
                if (output.bytes[output.offset + i] == payloadSep) {
                    surfaceFormLen = i;
                    break;
                }
            }
            assert surfaceFormLen != -1 : "no payloadSep found, unable to determine surface form";
            spare.copyUTF8Bytes(output.bytes, output.offset, surfaceFormLen);
            return surfaceFormLen;
        }
        
        static int parseDocID(final BytesRef output, final int payloadSepIndex) {
            assert payloadSepIndex != -1 : "payload sep index can not be -1";
            final ByteArrayDataInput input = new ByteArrayDataInput(output.bytes, payloadSepIndex + output.offset + 1, output.length - (payloadSepIndex + output.offset));
            return input.readVInt();
        }
        
        static BytesRef make(final BytesRef surface, final int docID, final int payloadSep) throws IOException {
            final int len = surface.length + 6;
            final byte[] buffer = new byte[len];
            final ByteArrayDataOutput output = new ByteArrayDataOutput(buffer);
            output.writeBytes(surface.bytes, surface.length - surface.offset);
            output.writeByte((byte)payloadSep);
            output.writeVInt(docID);
            return new BytesRef(buffer, 0, output.getPosition());
        }
    }
}
