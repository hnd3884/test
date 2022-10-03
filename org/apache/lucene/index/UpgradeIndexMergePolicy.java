package org.apache.lucene.index;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.util.Version;

public class UpgradeIndexMergePolicy extends MergePolicyWrapper
{
    public UpgradeIndexMergePolicy(final MergePolicy in) {
        super(in);
    }
    
    protected boolean shouldUpgradeSegment(final SegmentCommitInfo si) {
        return !Version.LATEST.equals(si.info.getVersion());
    }
    
    @Override
    public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        return this.in.findMerges(null, segmentInfos, writer);
    }
    
    @Override
    public MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        final Map<SegmentCommitInfo, Boolean> oldSegments = new HashMap<SegmentCommitInfo, Boolean>();
        for (final SegmentCommitInfo si : segmentInfos) {
            final Boolean v = segmentsToMerge.get(si);
            if (v != null && this.shouldUpgradeSegment(si)) {
                oldSegments.put(si, v);
            }
        }
        if (this.verbose(writer)) {
            this.message("findForcedMerges: segmentsToUpgrade=" + oldSegments, writer);
        }
        if (oldSegments.isEmpty()) {
            return null;
        }
        MergeSpecification spec = this.in.findForcedMerges(segmentInfos, maxSegmentCount, oldSegments, writer);
        if (spec != null) {
            for (final OneMerge om : spec.merges) {
                oldSegments.keySet().removeAll(om.segments);
            }
        }
        if (!oldSegments.isEmpty()) {
            if (this.verbose(writer)) {
                this.message("findForcedMerges: " + this.in.getClass().getSimpleName() + " does not want to merge all old segments, merge remaining ones into new segment: " + oldSegments, writer);
            }
            final List<SegmentCommitInfo> newInfos = new ArrayList<SegmentCommitInfo>();
            for (final SegmentCommitInfo si2 : segmentInfos) {
                if (oldSegments.containsKey(si2)) {
                    newInfos.add(si2);
                }
            }
            if (spec == null) {
                spec = new MergeSpecification();
            }
            spec.add(new OneMerge(newInfos));
        }
        return spec;
    }
    
    private boolean verbose(final IndexWriter writer) {
        return writer != null && writer.infoStream.isEnabled("UPGMP");
    }
    
    private void message(final String message, final IndexWriter writer) {
        writer.infoStream.message("UPGMP", message);
    }
}
