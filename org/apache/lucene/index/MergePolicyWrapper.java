package org.apache.lucene.index;

import java.util.Map;
import java.io.IOException;

public class MergePolicyWrapper extends MergePolicy
{
    protected final MergePolicy in;
    
    public MergePolicyWrapper(final MergePolicy in) {
        this.in = in;
    }
    
    @Override
    public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        return this.in.findMerges(mergeTrigger, segmentInfos, writer);
    }
    
    @Override
    public MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        return this.in.findForcedMerges(segmentInfos, maxSegmentCount, segmentsToMerge, writer);
    }
    
    @Override
    public MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        return this.in.findForcedDeletesMerges(segmentInfos, writer);
    }
    
    @Override
    public boolean useCompoundFile(final SegmentInfos infos, final SegmentCommitInfo mergedInfo, final IndexWriter writer) throws IOException {
        return this.in.useCompoundFile(infos, mergedInfo, writer);
    }
    
    @Override
    protected long size(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        return this.in.size(info, writer);
    }
    
    @Override
    public double getNoCFSRatio() {
        return this.in.getNoCFSRatio();
    }
    
    @Override
    public final void setNoCFSRatio(final double noCFSRatio) {
        this.in.setNoCFSRatio(noCFSRatio);
    }
    
    @Override
    public final void setMaxCFSSegmentSizeMB(final double v) {
        this.in.setMaxCFSSegmentSizeMB(v);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.in + ")";
    }
}
