package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.Accountables;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexOptions;
import java.io.IOException;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.index.Terms;

final class VersionFieldReader extends Terms implements Accountable
{
    final long numTerms;
    final FieldInfo fieldInfo;
    final long sumTotalTermFreq;
    final long sumDocFreq;
    final int docCount;
    final long indexStartFP;
    final long rootBlockFP;
    final PairOutputs.Pair<BytesRef, Long> rootCode;
    final BytesRef minTerm;
    final BytesRef maxTerm;
    final int longsSize;
    final VersionBlockTreeTermsReader parent;
    final FST<PairOutputs.Pair<BytesRef, Long>> index;
    
    VersionFieldReader(final VersionBlockTreeTermsReader parent, final FieldInfo fieldInfo, final long numTerms, final PairOutputs.Pair<BytesRef, Long> rootCode, final long sumTotalTermFreq, final long sumDocFreq, final int docCount, final long indexStartFP, final int longsSize, final IndexInput indexIn, final BytesRef minTerm, final BytesRef maxTerm) throws IOException {
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
        this.rootBlockFP = new ByteArrayDataInput(((BytesRef)rootCode.output1).bytes, ((BytesRef)rootCode.output1).offset, ((BytesRef)rootCode.output1).length).readVLong() >>> 2;
        if (indexIn != null) {
            final IndexInput clone = indexIn.clone();
            clone.seek(indexStartFP);
            this.index = (FST<PairOutputs.Pair<BytesRef, Long>>)new FST((DataInput)clone, (Outputs)VersionBlockTreeTermsWriter.FST_OUTPUTS);
        }
        else {
            this.index = null;
        }
    }
    
    public BytesRef getMin() throws IOException {
        if (this.minTerm == null) {
            return super.getMin();
        }
        return this.minTerm;
    }
    
    public BytesRef getMax() throws IOException {
        if (this.maxTerm == null) {
            return super.getMax();
        }
        return this.maxTerm;
    }
    
    public boolean hasFreqs() {
        return this.fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS) >= 0;
    }
    
    public boolean hasOffsets() {
        return this.fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
    }
    
    public boolean hasPositions() {
        return this.fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
    }
    
    public boolean hasPayloads() {
        return this.fieldInfo.hasPayloads();
    }
    
    public TermsEnum iterator() throws IOException {
        return new IDVersionSegmentTermsEnum(this);
    }
    
    public long size() {
        return this.numTerms;
    }
    
    public long getSumTotalTermFreq() {
        return this.sumTotalTermFreq;
    }
    
    public long getSumDocFreq() {
        return this.sumDocFreq;
    }
    
    public int getDocCount() {
        return this.docCount;
    }
    
    public long ramBytesUsed() {
        return (this.index != null) ? this.index.ramBytesUsed() : 0L;
    }
    
    public Collection<Accountable> getChildResources() {
        if (this.index == null) {
            return (Collection<Accountable>)Collections.emptyList();
        }
        return Collections.singletonList(Accountables.namedAccountable("term index", (Accountable)this.index));
    }
    
    public String toString() {
        return "IDVersionTerms(terms=" + this.numTerms + ",postings=" + this.sumDocFreq + ",positions=" + this.sumTotalTermFreq + ",docs=" + this.docCount + ")";
    }
}
