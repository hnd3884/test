package org.apache.lucene.index;

import java.util.Map;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

public class TieredMergePolicy extends MergePolicy
{
    public static final double DEFAULT_NO_CFS_RATIO = 0.1;
    private int maxMergeAtOnce;
    private long maxMergedSegmentBytes;
    private int maxMergeAtOnceExplicit;
    private long floorSegmentBytes;
    private double segsPerTier;
    private double forceMergeDeletesPctAllowed;
    private double reclaimDeletesWeight;
    
    public TieredMergePolicy() {
        super(0.1, Long.MAX_VALUE);
        this.maxMergeAtOnce = 10;
        this.maxMergedSegmentBytes = 5368709120L;
        this.maxMergeAtOnceExplicit = 30;
        this.floorSegmentBytes = 2097152L;
        this.segsPerTier = 10.0;
        this.forceMergeDeletesPctAllowed = 10.0;
        this.reclaimDeletesWeight = 2.0;
    }
    
    public TieredMergePolicy setMaxMergeAtOnce(final int v) {
        if (v < 2) {
            throw new IllegalArgumentException("maxMergeAtOnce must be > 1 (got " + v + ")");
        }
        this.maxMergeAtOnce = v;
        return this;
    }
    
    public int getMaxMergeAtOnce() {
        return this.maxMergeAtOnce;
    }
    
    public TieredMergePolicy setMaxMergeAtOnceExplicit(final int v) {
        if (v < 2) {
            throw new IllegalArgumentException("maxMergeAtOnceExplicit must be > 1 (got " + v + ")");
        }
        this.maxMergeAtOnceExplicit = v;
        return this;
    }
    
    public int getMaxMergeAtOnceExplicit() {
        return this.maxMergeAtOnceExplicit;
    }
    
    public TieredMergePolicy setMaxMergedSegmentMB(double v) {
        if (v < 0.0) {
            throw new IllegalArgumentException("maxMergedSegmentMB must be >=0 (got " + v + ")");
        }
        v *= 1048576.0;
        this.maxMergedSegmentBytes = ((v > 9.223372036854776E18) ? Long.MAX_VALUE : ((long)v));
        return this;
    }
    
    public double getMaxMergedSegmentMB() {
        return this.maxMergedSegmentBytes / 1024L / 1024.0;
    }
    
    public TieredMergePolicy setReclaimDeletesWeight(final double v) {
        if (v < 0.0) {
            throw new IllegalArgumentException("reclaimDeletesWeight must be >= 0.0 (got " + v + ")");
        }
        this.reclaimDeletesWeight = v;
        return this;
    }
    
    public double getReclaimDeletesWeight() {
        return this.reclaimDeletesWeight;
    }
    
    public TieredMergePolicy setFloorSegmentMB(double v) {
        if (v <= 0.0) {
            throw new IllegalArgumentException("floorSegmentMB must be > 0.0 (got " + v + ")");
        }
        v *= 1048576.0;
        this.floorSegmentBytes = ((v > 9.223372036854776E18) ? Long.MAX_VALUE : ((long)v));
        return this;
    }
    
    public double getFloorSegmentMB() {
        return this.floorSegmentBytes / 1048576.0;
    }
    
    public TieredMergePolicy setForceMergeDeletesPctAllowed(final double v) {
        if (v < 0.0 || v > 100.0) {
            throw new IllegalArgumentException("forceMergeDeletesPctAllowed must be between 0.0 and 100.0 inclusive (got " + v + ")");
        }
        this.forceMergeDeletesPctAllowed = v;
        return this;
    }
    
    public double getForceMergeDeletesPctAllowed() {
        return this.forceMergeDeletesPctAllowed;
    }
    
    public TieredMergePolicy setSegmentsPerTier(final double v) {
        if (v < 2.0) {
            throw new IllegalArgumentException("segmentsPerTier must be >= 2.0 (got " + v + ")");
        }
        this.segsPerTier = v;
        return this;
    }
    
    public double getSegmentsPerTier() {
        return this.segsPerTier;
    }
    
    @Override
    public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos infos, final IndexWriter writer) throws IOException {
        if (this.verbose(writer)) {
            this.message("findMerges: " + infos.size() + " segments", writer);
        }
        if (infos.size() == 0) {
            return null;
        }
        final Collection<SegmentCommitInfo> merging = writer.getMergingSegments();
        final Collection<SegmentCommitInfo> toBeMerged = new HashSet<SegmentCommitInfo>();
        final List<SegmentCommitInfo> infosSorted = new ArrayList<SegmentCommitInfo>(infos.asList());
        Collections.sort(infosSorted, new SegmentByteSizeDescending(writer));
        long totIndexBytes = 0L;
        long minSegmentBytes = Long.MAX_VALUE;
        for (final SegmentCommitInfo info : infosSorted) {
            final long segBytes = this.size(info, writer);
            if (this.verbose(writer)) {
                String extra = merging.contains(info) ? " [merging]" : "";
                if (segBytes >= this.maxMergedSegmentBytes / 2.0) {
                    extra += " [skip: too large]";
                }
                else if (segBytes < this.floorSegmentBytes) {
                    extra += " [floored]";
                }
                this.message("  seg=" + writer.segString(info) + " size=" + String.format(Locale.ROOT, "%.3f", segBytes / 1024L / 1024.0) + " MB" + extra, writer);
            }
            minSegmentBytes = Math.min(segBytes, minSegmentBytes);
            totIndexBytes += segBytes;
        }
        int tooBigCount;
        for (tooBigCount = 0; tooBigCount < infosSorted.size(); ++tooBigCount) {
            final long segBytes2 = this.size(infosSorted.get(tooBigCount), writer);
            if (segBytes2 < this.maxMergedSegmentBytes / 2.0) {
                break;
            }
            totIndexBytes -= segBytes2;
        }
        long levelSize;
        minSegmentBytes = (levelSize = this.floorSize(minSegmentBytes));
        long bytesLeft = totIndexBytes;
        double allowedSegCount = 0.0;
        double segCountLevel;
        while (true) {
            segCountLevel = bytesLeft / (double)levelSize;
            if (segCountLevel < this.segsPerTier) {
                break;
            }
            allowedSegCount += this.segsPerTier;
            bytesLeft -= (long)(this.segsPerTier * levelSize);
            levelSize *= this.maxMergeAtOnce;
        }
        allowedSegCount += Math.ceil(segCountLevel);
        final int allowedSegCountInt = (int)allowedSegCount;
        MergeSpecification spec = null;
        while (true) {
            long mergingBytes = 0L;
            final List<SegmentCommitInfo> eligible = new ArrayList<SegmentCommitInfo>();
            for (int idx = tooBigCount; idx < infosSorted.size(); ++idx) {
                final SegmentCommitInfo info2 = infosSorted.get(idx);
                if (merging.contains(info2)) {
                    mergingBytes += this.size(info2, writer);
                }
                else if (!toBeMerged.contains(info2)) {
                    eligible.add(info2);
                }
            }
            final boolean maxMergeIsRunning = mergingBytes >= this.maxMergedSegmentBytes;
            if (this.verbose(writer)) {
                this.message("  allowedSegmentCount=" + allowedSegCountInt + " vs count=" + infosSorted.size() + " (eligible count=" + eligible.size() + ") tooBigCount=" + tooBigCount, writer);
            }
            if (eligible.size() == 0) {
                return spec;
            }
            if (eligible.size() <= allowedSegCountInt) {
                return spec;
            }
            MergeScore bestScore = null;
            List<SegmentCommitInfo> best = null;
            boolean bestTooLarge = false;
            long bestMergeBytes = 0L;
            for (int startIdx = 0; startIdx <= eligible.size() - this.maxMergeAtOnce; ++startIdx) {
                long totAfterMergeBytes = 0L;
                final List<SegmentCommitInfo> candidate = new ArrayList<SegmentCommitInfo>();
                boolean hitTooLarge = false;
                for (int idx2 = startIdx; idx2 < eligible.size() && candidate.size() < this.maxMergeAtOnce; ++idx2) {
                    final SegmentCommitInfo info3 = eligible.get(idx2);
                    final long segBytes3 = this.size(info3, writer);
                    if (totAfterMergeBytes + segBytes3 > this.maxMergedSegmentBytes) {
                        hitTooLarge = true;
                    }
                    else {
                        candidate.add(info3);
                        totAfterMergeBytes += segBytes3;
                    }
                }
                assert candidate.size() > 0;
                final MergeScore score = this.score(candidate, hitTooLarge, mergingBytes, writer);
                if (this.verbose(writer)) {
                    this.message("  maybe=" + writer.segString(candidate) + " score=" + score.getScore() + " " + score.getExplanation() + " tooLarge=" + hitTooLarge + " size=" + String.format(Locale.ROOT, "%.3f MB", totAfterMergeBytes / 1024.0 / 1024.0), writer);
                }
                if ((bestScore == null || score.getScore() < bestScore.getScore()) && (!hitTooLarge || !maxMergeIsRunning)) {
                    best = candidate;
                    bestScore = score;
                    bestTooLarge = hitTooLarge;
                    bestMergeBytes = totAfterMergeBytes;
                }
            }
            if (best == null) {
                return spec;
            }
            if (spec == null) {
                spec = new MergeSpecification();
            }
            final OneMerge merge = new OneMerge(best);
            spec.add(merge);
            for (final SegmentCommitInfo info4 : merge.segments) {
                toBeMerged.add(info4);
            }
            if (!this.verbose(writer)) {
                continue;
            }
            this.message("  add merge=" + writer.segString(merge.segments) + " size=" + String.format(Locale.ROOT, "%.3f MB", bestMergeBytes / 1024.0 / 1024.0) + " score=" + String.format(Locale.ROOT, "%.3f", bestScore.getScore()) + " " + bestScore.getExplanation() + (bestTooLarge ? " [max merge]" : ""), writer);
        }
    }
    
    protected MergeScore score(final List<SegmentCommitInfo> candidate, final boolean hitTooLarge, final long mergingBytes, final IndexWriter writer) throws IOException {
        long totBeforeMergeBytes = 0L;
        long totAfterMergeBytes = 0L;
        long totAfterMergeBytesFloored = 0L;
        for (final SegmentCommitInfo info : candidate) {
            final long segBytes = this.size(info, writer);
            totAfterMergeBytes += segBytes;
            totAfterMergeBytesFloored += this.floorSize(segBytes);
            totBeforeMergeBytes += info.sizeInBytes();
        }
        double skew;
        if (hitTooLarge) {
            skew = 1.0 / this.maxMergeAtOnce;
        }
        else {
            skew = this.floorSize(this.size(candidate.get(0), writer)) / (double)totAfterMergeBytesFloored;
        }
        double mergeScore = skew;
        mergeScore *= Math.pow((double)totAfterMergeBytes, 0.05);
        final double nonDelRatio = totAfterMergeBytes / (double)totBeforeMergeBytes;
        final double finalMergeScore;
        mergeScore = (finalMergeScore = mergeScore * Math.pow(nonDelRatio, this.reclaimDeletesWeight));
        return new MergeScore() {
            public double getScore() {
                return finalMergeScore;
            }
            
            public String getExplanation() {
                return "skew=" + String.format(Locale.ROOT, "%.3f", skew) + " nonDelRatio=" + String.format(Locale.ROOT, "%.3f", nonDelRatio);
            }
        };
    }
    
    @Override
    public MergeSpecification findForcedMerges(final SegmentInfos infos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        if (this.verbose(writer)) {
            this.message("findForcedMerges maxSegmentCount=" + maxSegmentCount + " infos=" + writer.segString(infos) + " segmentsToMerge=" + segmentsToMerge, writer);
        }
        final List<SegmentCommitInfo> eligible = new ArrayList<SegmentCommitInfo>();
        boolean forceMergeRunning = false;
        final Collection<SegmentCommitInfo> merging = writer.getMergingSegments();
        boolean segmentIsOriginal = false;
        for (final SegmentCommitInfo info : infos) {
            final Boolean isOriginal = segmentsToMerge.get(info);
            if (isOriginal != null) {
                segmentIsOriginal = isOriginal;
                if (!merging.contains(info)) {
                    eligible.add(info);
                }
                else {
                    forceMergeRunning = true;
                }
            }
        }
        if (eligible.size() == 0) {
            return null;
        }
        if ((maxSegmentCount > 1 && eligible.size() <= maxSegmentCount) || (maxSegmentCount == 1 && eligible.size() == 1 && (!segmentIsOriginal || this.isMerged(infos, eligible.get(0), writer)))) {
            if (this.verbose(writer)) {
                this.message("already merged", writer);
            }
            return null;
        }
        Collections.sort(eligible, new SegmentByteSizeDescending(writer));
        if (this.verbose(writer)) {
            this.message("eligible=" + eligible, writer);
            this.message("forceMergeRunning=" + forceMergeRunning, writer);
        }
        int end = eligible.size();
        MergeSpecification spec = null;
        while (end >= this.maxMergeAtOnceExplicit + maxSegmentCount - 1) {
            if (spec == null) {
                spec = new MergeSpecification();
            }
            final OneMerge merge = new OneMerge(eligible.subList(end - this.maxMergeAtOnceExplicit, end));
            if (this.verbose(writer)) {
                this.message("add merge=" + writer.segString(merge.segments), writer);
            }
            spec.add(merge);
            end -= this.maxMergeAtOnceExplicit;
        }
        if (spec == null && !forceMergeRunning) {
            final int numToMerge = end - maxSegmentCount + 1;
            final OneMerge merge2 = new OneMerge(eligible.subList(end - numToMerge, end));
            if (this.verbose(writer)) {
                this.message("add final merge=" + merge2.segString(), writer);
            }
            spec = new MergeSpecification();
            spec.add(merge2);
        }
        return spec;
    }
    
    @Override
    public MergeSpecification findForcedDeletesMerges(final SegmentInfos infos, final IndexWriter writer) throws IOException {
        if (this.verbose(writer)) {
            this.message("findForcedDeletesMerges infos=" + writer.segString(infos) + " forceMergeDeletesPctAllowed=" + this.forceMergeDeletesPctAllowed, writer);
        }
        final List<SegmentCommitInfo> eligible = new ArrayList<SegmentCommitInfo>();
        final Collection<SegmentCommitInfo> merging = writer.getMergingSegments();
        for (final SegmentCommitInfo info : infos) {
            final double pctDeletes = 100.0 * writer.numDeletedDocs(info) / info.info.maxDoc();
            if (pctDeletes > this.forceMergeDeletesPctAllowed && !merging.contains(info)) {
                eligible.add(info);
            }
        }
        if (eligible.size() == 0) {
            return null;
        }
        Collections.sort(eligible, new SegmentByteSizeDescending(writer));
        if (this.verbose(writer)) {
            this.message("eligible=" + eligible, writer);
        }
        int start = 0;
        MergeSpecification spec = null;
        while (start < eligible.size()) {
            final int end = Math.min(start + this.maxMergeAtOnceExplicit, eligible.size());
            if (spec == null) {
                spec = new MergeSpecification();
            }
            final OneMerge merge = new OneMerge(eligible.subList(start, end));
            if (this.verbose(writer)) {
                this.message("add merge=" + writer.segString(merge.segments), writer);
            }
            spec.add(merge);
            start = end;
        }
        return spec;
    }
    
    private long floorSize(final long bytes) {
        return Math.max(this.floorSegmentBytes, bytes);
    }
    
    private boolean verbose(final IndexWriter writer) {
        return writer != null && writer.infoStream.isEnabled("TMP");
    }
    
    private void message(final String message, final IndexWriter writer) {
        writer.infoStream.message("TMP", message);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[" + this.getClass().getSimpleName() + ": ");
        sb.append("maxMergeAtOnce=").append(this.maxMergeAtOnce).append(", ");
        sb.append("maxMergeAtOnceExplicit=").append(this.maxMergeAtOnceExplicit).append(", ");
        sb.append("maxMergedSegmentMB=").append(this.maxMergedSegmentBytes / 1024L / 1024.0).append(", ");
        sb.append("floorSegmentMB=").append(this.floorSegmentBytes / 1024L / 1024.0).append(", ");
        sb.append("forceMergeDeletesPctAllowed=").append(this.forceMergeDeletesPctAllowed).append(", ");
        sb.append("segmentsPerTier=").append(this.segsPerTier).append(", ");
        sb.append("maxCFSSegmentSizeMB=").append(this.getMaxCFSSegmentSizeMB()).append(", ");
        sb.append("noCFSRatio=").append(this.noCFSRatio);
        return sb.toString();
    }
    
    private class SegmentByteSizeDescending implements Comparator<SegmentCommitInfo>
    {
        private final IndexWriter writer;
        
        SegmentByteSizeDescending(final IndexWriter writer) {
            this.writer = writer;
        }
        
        @Override
        public int compare(final SegmentCommitInfo o1, final SegmentCommitInfo o2) {
            try {
                final long sz1 = TieredMergePolicy.this.size(o1, this.writer);
                final long sz2 = TieredMergePolicy.this.size(o2, this.writer);
                if (sz1 > sz2) {
                    return -1;
                }
                if (sz2 > sz1) {
                    return 1;
                }
                return o1.info.name.compareTo(o2.info.name);
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
    
    protected abstract static class MergeScore
    {
        abstract double getScore();
        
        abstract String getExplanation();
    }
}
