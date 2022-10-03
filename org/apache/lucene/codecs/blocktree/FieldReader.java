package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Accountables;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.automaton.RunAutomaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexOptions;
import java.io.IOException;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.index.Terms;

public final class FieldReader extends Terms implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    final long numTerms;
    final FieldInfo fieldInfo;
    final long sumTotalTermFreq;
    final long sumDocFreq;
    final int docCount;
    final long indexStartFP;
    final long rootBlockFP;
    final BytesRef rootCode;
    final BytesRef minTerm;
    final BytesRef maxTerm;
    final int longsSize;
    final BlockTreeTermsReader parent;
    final FST<BytesRef> index;
    
    FieldReader(final BlockTreeTermsReader parent, final FieldInfo fieldInfo, final long numTerms, final BytesRef rootCode, final long sumTotalTermFreq, final long sumDocFreq, final int docCount, final long indexStartFP, final int longsSize, final IndexInput indexIn, final BytesRef minTerm, final BytesRef maxTerm) throws IOException {
        assert numTerms > 0L;
        this.fieldInfo = fieldInfo;
        this.parent = parent;
        this.numTerms = numTerms;
        this.sumTotalTermFreq = sumTotalTermFreq;
        this.sumDocFreq = sumDocFreq;
        this.docCount = docCount;
        this.indexStartFP = indexStartFP;
        this.rootCode = rootCode;
        this.longsSize = longsSize;
        this.minTerm = minTerm;
        this.maxTerm = maxTerm;
        this.rootBlockFP = new ByteArrayDataInput(rootCode.bytes, rootCode.offset, rootCode.length).readVLong() >>> 2;
        if (indexIn != null) {
            final IndexInput clone = indexIn.clone();
            clone.seek(indexStartFP);
            this.index = new FST<BytesRef>(clone, ByteSequenceOutputs.getSingleton());
        }
        else {
            this.index = null;
        }
    }
    
    @Override
    public BytesRef getMin() throws IOException {
        if (this.minTerm == null) {
            return super.getMin();
        }
        return this.minTerm;
    }
    
    @Override
    public BytesRef getMax() throws IOException {
        if (this.maxTerm == null) {
            return super.getMax();
        }
        return this.maxTerm;
    }
    
    @Override
    public Stats getStats() throws IOException {
        return new SegmentTermsEnum(this).computeBlockStats();
    }
    
    @Override
    public boolean hasFreqs() {
        return this.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS) >= 0;
    }
    
    @Override
    public boolean hasOffsets() {
        return this.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
    }
    
    @Override
    public boolean hasPositions() {
        return this.fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
    }
    
    @Override
    public boolean hasPayloads() {
        return this.fieldInfo.hasPayloads();
    }
    
    @Override
    public TermsEnum iterator() throws IOException {
        return new SegmentTermsEnum(this);
    }
    
    @Override
    public long size() {
        return this.numTerms;
    }
    
    @Override
    public long getSumTotalTermFreq() {
        return this.sumTotalTermFreq;
    }
    
    @Override
    public long getSumDocFreq() {
        return this.sumDocFreq;
    }
    
    @Override
    public int getDocCount() {
        return this.docCount;
    }
    
    @Override
    public TermsEnum intersect(final CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
        return new IntersectTermsEnum(this, compiled.automaton, compiled.runAutomaton, compiled.commonSuffixRef, startTerm, compiled.sinkState);
    }
    
    @Override
    public long ramBytesUsed() {
        return FieldReader.BASE_RAM_BYTES_USED + ((this.index != null) ? this.index.ramBytesUsed() : 0L);
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        if (this.index == null) {
            return (Collection<Accountable>)Collections.emptyList();
        }
        return Collections.singleton(Accountables.namedAccountable("term index", this.index));
    }
    
    @Override
    public String toString() {
        return "BlockTreeTerms(terms=" + this.numTerms + ",postings=" + this.sumDocFreq + ",positions=" + this.sumTotalTermFreq + ",docs=" + this.docCount + ")";
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(FieldReader.class) + 3L * RamUsageEstimator.shallowSizeOfInstance(BytesRef.class);
    }
}
