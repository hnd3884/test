package org.apache.lucene.index;

import java.util.Collection;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;

public abstract class LogMergePolicy extends MergePolicy
{
    public static final double LEVEL_LOG_SPAN = 0.75;
    public static final int DEFAULT_MERGE_FACTOR = 10;
    public static final int DEFAULT_MAX_MERGE_DOCS = Integer.MAX_VALUE;
    public static final double DEFAULT_NO_CFS_RATIO = 0.1;
    protected int mergeFactor;
    protected long minMergeSize;
    protected long maxMergeSize;
    protected long maxMergeSizeForForcedMerge;
    protected int maxMergeDocs;
    protected boolean calibrateSizeByDeletes;
    
    public LogMergePolicy() {
        super(0.1, Long.MAX_VALUE);
        this.mergeFactor = 10;
        this.maxMergeSizeForForcedMerge = Long.MAX_VALUE;
        this.maxMergeDocs = Integer.MAX_VALUE;
        this.calibrateSizeByDeletes = true;
    }
    
    protected boolean verbose(final IndexWriter writer) {
        return writer != null && writer.infoStream.isEnabled("LMP");
    }
    
    protected void message(final String message, final IndexWriter writer) {
        if (this.verbose(writer)) {
            writer.infoStream.message("LMP", message);
        }
    }
    
    public int getMergeFactor() {
        return this.mergeFactor;
    }
    
    public void setMergeFactor(final int mergeFactor) {
        if (mergeFactor < 2) {
            throw new IllegalArgumentException("mergeFactor cannot be less than 2");
        }
        this.mergeFactor = mergeFactor;
    }
    
    public void setCalibrateSizeByDeletes(final boolean calibrateSizeByDeletes) {
        this.calibrateSizeByDeletes = calibrateSizeByDeletes;
    }
    
    public boolean getCalibrateSizeByDeletes() {
        return this.calibrateSizeByDeletes;
    }
    
    protected long sizeDocs(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        if (!this.calibrateSizeByDeletes) {
            return info.info.maxDoc();
        }
        final int delCount = writer.numDeletedDocs(info);
        assert delCount <= info.info.maxDoc();
        return info.info.maxDoc() - (long)delCount;
    }
    
    protected long sizeBytes(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        if (this.calibrateSizeByDeletes) {
            return super.size(info, writer);
        }
        return info.sizeInBytes();
    }
    
    protected boolean isMerged(final SegmentInfos infos, final int maxNumSegments, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        final int numSegments = infos.size();
        int numToMerge = 0;
        SegmentCommitInfo mergeInfo = null;
        boolean segmentIsOriginal = false;
        for (int i = 0; i < numSegments && numToMerge <= maxNumSegments; ++i) {
            final SegmentCommitInfo info = infos.info(i);
            final Boolean isOriginal = segmentsToMerge.get(info);
            if (isOriginal != null) {
                segmentIsOriginal = isOriginal;
                ++numToMerge;
                mergeInfo = info;
            }
        }
        return numToMerge <= maxNumSegments && (numToMerge != 1 || !segmentIsOriginal || this.isMerged(infos, mergeInfo, writer));
    }
    
    private MergeSpecification findForcedMergesSizeLimit(final SegmentInfos infos, final int maxNumSegments, int last, final IndexWriter writer) throws IOException {
        final MergeSpecification spec = new MergeSpecification();
        final List<SegmentCommitInfo> segments = infos.asList();
        int start;
        for (start = last - 1; start >= 0; --start) {
            final SegmentCommitInfo info = infos.info(start);
            if (this.size(info, writer) > this.maxMergeSizeForForcedMerge || this.sizeDocs(info, writer) > this.maxMergeDocs) {
                if (this.verbose(writer)) {
                    this.message("findForcedMergesSizeLimit: skip segment=" + info + ": size is > maxMergeSize (" + this.maxMergeSizeForForcedMerge + ") or sizeDocs is > maxMergeDocs (" + this.maxMergeDocs + ")", writer);
                }
                if (last - start - 1 > 1 || (start != last - 1 && !this.isMerged(infos, infos.info(start + 1), writer))) {
                    spec.add(new OneMerge(segments.subList(start + 1, last)));
                }
                last = start;
            }
            else if (last - start == this.mergeFactor) {
                spec.add(new OneMerge(segments.subList(start, last)));
                last = start;
            }
        }
        if (last > 0 && (++start + 1 < last || !this.isMerged(infos, infos.info(start), writer))) {
            spec.add(new OneMerge(segments.subList(start, last)));
        }
        return (spec.merges.size() == 0) ? null : spec;
    }
    
    private MergeSpecification findForcedMergesMaxNumSegments(final SegmentInfos infos, final int maxNumSegments, int last, final IndexWriter writer) throws IOException {
        final MergeSpecification spec = new MergeSpecification();
        final List<SegmentCommitInfo> segments = infos.asList();
        while (last - maxNumSegments + 1 >= this.mergeFactor) {
            spec.add(new OneMerge(segments.subList(last - this.mergeFactor, last)));
            last -= this.mergeFactor;
        }
        if (0 == spec.merges.size()) {
            if (maxNumSegments == 1) {
                if (last > 1 || !this.isMerged(infos, infos.info(0), writer)) {
                    spec.add(new OneMerge(segments.subList(0, last)));
                }
            }
            else if (last > maxNumSegments) {
                final int finalMergeSize = last - maxNumSegments + 1;
                long bestSize = 0L;
                int bestStart = 0;
                for (int i = 0; i < last - finalMergeSize + 1; ++i) {
                    long sumSize = 0L;
                    for (int j = 0; j < finalMergeSize; ++j) {
                        sumSize += this.size(infos.info(j + i), writer);
                    }
                    if (i == 0 || (sumSize < 2L * this.size(infos.info(i - 1), writer) && sumSize < bestSize)) {
                        bestStart = i;
                        bestSize = sumSize;
                    }
                }
                spec.add(new OneMerge(segments.subList(bestStart, bestStart + finalMergeSize)));
            }
        }
        return (spec.merges.size() == 0) ? null : spec;
    }
    
    @Override
    public MergeSpecification findForcedMerges(final SegmentInfos infos, final int maxNumSegments, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        assert maxNumSegments > 0;
        if (this.verbose(writer)) {
            this.message("findForcedMerges: maxNumSegs=" + maxNumSegments + " segsToMerge=" + segmentsToMerge, writer);
        }
        if (this.isMerged(infos, maxNumSegments, segmentsToMerge, writer)) {
            if (this.verbose(writer)) {
                this.message("already merged; skip", writer);
            }
            return null;
        }
        int last = infos.size();
        while (last > 0) {
            final SegmentCommitInfo info = infos.info(--last);
            if (segmentsToMerge.get(info) != null) {
                ++last;
                break;
            }
        }
        if (last == 0) {
            if (this.verbose(writer)) {
                this.message("last == 0; skip", writer);
            }
            return null;
        }
        if (maxNumSegments == 1 && last == 1 && this.isMerged(infos, infos.info(0), writer)) {
            if (this.verbose(writer)) {
                this.message("already 1 seg; skip", writer);
            }
            return null;
        }
        boolean anyTooLarge = false;
        for (int i = 0; i < last; ++i) {
            final SegmentCommitInfo info2 = infos.info(i);
            if (this.size(info2, writer) > this.maxMergeSizeForForcedMerge || this.sizeDocs(info2, writer) > this.maxMergeDocs) {
                anyTooLarge = true;
                break;
            }
        }
        if (anyTooLarge) {
            return this.findForcedMergesSizeLimit(infos, maxNumSegments, last, writer);
        }
        return this.findForcedMergesMaxNumSegments(infos, maxNumSegments, last, writer);
    }
    
    @Override
    public MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        final List<SegmentCommitInfo> segments = segmentInfos.asList();
        final int numSegments = segments.size();
        if (this.verbose(writer)) {
            this.message("findForcedDeleteMerges: " + numSegments + " segments", writer);
        }
        final MergeSpecification spec = new MergeSpecification();
        int firstSegmentWithDeletions = -1;
        assert writer != null;
        for (int i = 0; i < numSegments; ++i) {
            final SegmentCommitInfo info = segmentInfos.info(i);
            final int delCount = writer.numDeletedDocs(info);
            if (delCount > 0) {
                if (this.verbose(writer)) {
                    this.message("  segment " + info.info.name + " has deletions", writer);
                }
                if (firstSegmentWithDeletions == -1) {
                    firstSegmentWithDeletions = i;
                }
                else if (i - firstSegmentWithDeletions == this.mergeFactor) {
                    if (this.verbose(writer)) {
                        this.message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive", writer);
                    }
                    spec.add(new OneMerge(segments.subList(firstSegmentWithDeletions, i)));
                    firstSegmentWithDeletions = i;
                }
            }
            else if (firstSegmentWithDeletions != -1) {
                if (this.verbose(writer)) {
                    this.message("  add merge " + firstSegmentWithDeletions + " to " + (i - 1) + " inclusive", writer);
                }
                spec.add(new OneMerge(segments.subList(firstSegmentWithDeletions, i)));
                firstSegmentWithDeletions = -1;
            }
        }
        if (firstSegmentWithDeletions != -1) {
            if (this.verbose(writer)) {
                this.message("  add merge " + firstSegmentWithDeletions + " to " + (numSegments - 1) + " inclusive", writer);
            }
            spec.add(new OneMerge(segments.subList(firstSegmentWithDeletions, numSegments)));
        }
        return spec;
    }
    
    @Override
    public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos infos, final IndexWriter writer) throws IOException {
        final int numSegments = infos.size();
        if (this.verbose(writer)) {
            this.message("findMerges: " + numSegments + " segments", writer);
        }
        final List<SegmentInfoAndLevel> levels = new ArrayList<SegmentInfoAndLevel>(numSegments);
        final float norm = (float)Math.log(this.mergeFactor);
        final Collection<SegmentCommitInfo> mergingSegments = writer.getMergingSegments();
        for (int i = 0; i < numSegments; ++i) {
            final SegmentCommitInfo info = infos.info(i);
            long size = this.size(info, writer);
            if (size < 1L) {
                size = 1L;
            }
            final SegmentInfoAndLevel infoLevel = new SegmentInfoAndLevel(info, (float)Math.log((double)size) / norm, i);
            levels.add(infoLevel);
            if (this.verbose(writer)) {
                final long segBytes = this.sizeBytes(info, writer);
                String extra = mergingSegments.contains(info) ? " [merging]" : "";
                if (size >= this.maxMergeSize) {
                    extra += " [skip: too large]";
                }
                this.message("seg=" + writer.segString(info) + " level=" + infoLevel.level + " size=" + String.format(Locale.ROOT, "%.3f MB", segBytes / 1024L / 1024.0) + extra, writer);
            }
        }
        float levelFloor;
        if (this.minMergeSize <= 0L) {
            levelFloor = 0.0f;
        }
        else {
            levelFloor = (float)(Math.log((double)this.minMergeSize) / norm);
        }
        MergeSpecification spec = null;
        int upto;
        for (int numMergeableSegments = levels.size(), start = 0; start < numMergeableSegments; start = 1 + upto) {
            float maxLevel = levels.get(start).level;
            for (int j = 1 + start; j < numMergeableSegments; ++j) {
                final float level = levels.get(j).level;
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
            float levelBottom;
            if (maxLevel <= levelFloor) {
                levelBottom = -1.0f;
            }
            else {
                levelBottom = (float)(maxLevel - 0.75);
                if (levelBottom < levelFloor && maxLevel >= levelFloor) {
                    levelBottom = levelFloor;
                }
            }
            for (upto = numMergeableSegments - 1; upto >= start && levels.get(upto).level < levelBottom; --upto) {}
            if (this.verbose(writer)) {
                this.message("  level " + levelBottom + " to " + maxLevel + ": " + (1 + upto - start) + " segments", writer);
            }
            for (int end = start + this.mergeFactor; end <= 1 + upto; end = start + this.mergeFactor) {
                boolean anyTooLarge = false;
                boolean anyMerging = false;
                for (int k = start; k < end; ++k) {
                    final SegmentCommitInfo info2 = levels.get(k).info;
                    anyTooLarge |= (this.size(info2, writer) >= this.maxMergeSize || this.sizeDocs(info2, writer) >= this.maxMergeDocs);
                    if (mergingSegments.contains(info2)) {
                        anyMerging = true;
                        break;
                    }
                }
                if (!anyMerging) {
                    if (!anyTooLarge) {
                        if (spec == null) {
                            spec = new MergeSpecification();
                        }
                        final List<SegmentCommitInfo> mergeInfos = new ArrayList<SegmentCommitInfo>(end - start);
                        for (int l = start; l < end; ++l) {
                            mergeInfos.add(levels.get(l).info);
                            assert infos.contains(levels.get(l).info);
                        }
                        if (this.verbose(writer)) {
                            this.message("  add merge=" + writer.segString(mergeInfos) + " start=" + start + " end=" + end, writer);
                        }
                        spec.add(new OneMerge(mergeInfos));
                    }
                    else if (this.verbose(writer)) {
                        this.message("    " + start + " to " + end + ": contains segment over maxMergeSize or maxMergeDocs; skipping", writer);
                    }
                }
                start = end;
            }
        }
        return spec;
    }
    
    public void setMaxMergeDocs(final int maxMergeDocs) {
        this.maxMergeDocs = maxMergeDocs;
    }
    
    public int getMaxMergeDocs() {
        return this.maxMergeDocs;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[" + this.getClass().getSimpleName() + ": ");
        sb.append("minMergeSize=").append(this.minMergeSize).append(", ");
        sb.append("mergeFactor=").append(this.mergeFactor).append(", ");
        sb.append("maxMergeSize=").append(this.maxMergeSize).append(", ");
        sb.append("maxMergeSizeForForcedMerge=").append(this.maxMergeSizeForForcedMerge).append(", ");
        sb.append("calibrateSizeByDeletes=").append(this.calibrateSizeByDeletes).append(", ");
        sb.append("maxMergeDocs=").append(this.maxMergeDocs).append(", ");
        sb.append("maxCFSSegmentSizeMB=").append(this.getMaxCFSSegmentSizeMB()).append(", ");
        sb.append("noCFSRatio=").append(this.noCFSRatio);
        sb.append("]");
        return sb.toString();
    }
    
    private static class SegmentInfoAndLevel implements Comparable<SegmentInfoAndLevel>
    {
        SegmentCommitInfo info;
        float level;
        int index;
        
        public SegmentInfoAndLevel(final SegmentCommitInfo info, final float level, final int index) {
            this.info = info;
            this.level = level;
            this.index = index;
        }
        
        @Override
        public int compareTo(final SegmentInfoAndLevel other) {
            return Float.compare(other.level, this.level);
        }
    }
}
