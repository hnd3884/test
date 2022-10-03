package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MergeInfo;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.FixedBitSet;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;

public abstract class MergePolicy
{
    protected static final double DEFAULT_NO_CFS_RATIO = 1.0;
    protected static final long DEFAULT_MAX_CFS_SEGMENT_SIZE = Long.MAX_VALUE;
    protected double noCFSRatio;
    protected long maxCFSSegmentSize;
    
    public MergePolicy() {
        this(1.0, Long.MAX_VALUE);
    }
    
    protected MergePolicy(final double defaultNoCFSRatio, final long defaultMaxCFSSegmentSize) {
        this.noCFSRatio = 1.0;
        this.maxCFSSegmentSize = Long.MAX_VALUE;
        this.noCFSRatio = defaultNoCFSRatio;
        this.maxCFSSegmentSize = defaultMaxCFSSegmentSize;
    }
    
    public abstract MergeSpecification findMerges(final MergeTrigger p0, final SegmentInfos p1, final IndexWriter p2) throws IOException;
    
    public abstract MergeSpecification findForcedMerges(final SegmentInfos p0, final int p1, final Map<SegmentCommitInfo, Boolean> p2, final IndexWriter p3) throws IOException;
    
    public abstract MergeSpecification findForcedDeletesMerges(final SegmentInfos p0, final IndexWriter p1) throws IOException;
    
    public boolean useCompoundFile(final SegmentInfos infos, final SegmentCommitInfo mergedInfo, final IndexWriter writer) throws IOException {
        if (this.getNoCFSRatio() == 0.0) {
            return false;
        }
        final long mergedInfoSize = this.size(mergedInfo, writer);
        if (mergedInfoSize > this.maxCFSSegmentSize) {
            return false;
        }
        if (this.getNoCFSRatio() >= 1.0) {
            return true;
        }
        long totalSize = 0L;
        for (final SegmentCommitInfo info : infos) {
            totalSize += this.size(info, writer);
        }
        return mergedInfoSize <= this.getNoCFSRatio() * totalSize;
    }
    
    protected long size(final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        final long byteSize = info.sizeInBytes();
        final int delCount = writer.numDeletedDocs(info);
        final double delRatio = (info.info.maxDoc() <= 0) ? 0.0 : (delCount / (float)info.info.maxDoc());
        assert delRatio <= 1.0;
        return (info.info.maxDoc() <= 0) ? byteSize : ((long)(byteSize * (1.0 - delRatio)));
    }
    
    protected final boolean isMerged(final SegmentInfos infos, final SegmentCommitInfo info, final IndexWriter writer) throws IOException {
        assert writer != null;
        final boolean hasDeletions = writer.numDeletedDocs(info) > 0;
        return !hasDeletions && info.info.dir == writer.getDirectory() && this.useCompoundFile(infos, info, writer) == info.info.getUseCompoundFile();
    }
    
    public double getNoCFSRatio() {
        return this.noCFSRatio;
    }
    
    public void setNoCFSRatio(final double noCFSRatio) {
        if (noCFSRatio < 0.0 || noCFSRatio > 1.0) {
            throw new IllegalArgumentException("noCFSRatio must be 0.0 to 1.0 inclusive; got " + noCFSRatio);
        }
        this.noCFSRatio = noCFSRatio;
    }
    
    public final double getMaxCFSSegmentSizeMB() {
        return this.maxCFSSegmentSize / 1024L / 1024.0;
    }
    
    public void setMaxCFSSegmentSizeMB(double v) {
        if (v < 0.0) {
            throw new IllegalArgumentException("maxCFSSegmentSizeMB must be >=0 (got " + v + ")");
        }
        v *= 1048576.0;
        this.maxCFSSegmentSize = ((v > 9.223372036854776E18) ? Long.MAX_VALUE : ((long)v));
    }
    
    public abstract static class DocMap
    {
        protected DocMap() {
        }
        
        public abstract int map(final int p0);
        
        boolean isConsistent(final int maxDoc) {
            final FixedBitSet targets = new FixedBitSet(maxDoc);
            int i = 0;
            while (i < maxDoc) {
                final int target = this.map(i);
                if (target < 0 || target >= maxDoc) {
                    assert false : "out of range: " + target + " not in [0-" + maxDoc + "[";
                    return false;
                }
                else if (targets.get(target)) {
                    assert false : target + " is already taken (" + i + ")";
                    return false;
                }
                else {
                    ++i;
                }
            }
            return true;
        }
    }
    
    public static class OneMerge
    {
        SegmentCommitInfo info;
        boolean registerDone;
        long mergeGen;
        boolean isExternal;
        int maxNumSegments;
        public volatile long estimatedMergeBytes;
        volatile long totalMergeBytes;
        List<SegmentReader> readers;
        public final List<SegmentCommitInfo> segments;
        public final MergeRateLimiter rateLimiter;
        volatile long mergeStartNS;
        public final int totalMaxDoc;
        Throwable error;
        
        public OneMerge(final List<SegmentCommitInfo> segments) {
            this.maxNumSegments = -1;
            this.mergeStartNS = -1L;
            if (0 == segments.size()) {
                throw new RuntimeException("segments must include at least one segment");
            }
            this.segments = new ArrayList<SegmentCommitInfo>(segments);
            int count = 0;
            for (final SegmentCommitInfo info : segments) {
                count += info.info.maxDoc();
            }
            this.totalMaxDoc = count;
            this.rateLimiter = new MergeRateLimiter(this);
        }
        
        public void mergeFinished() throws IOException {
        }
        
        public List<CodecReader> getMergeReaders() throws IOException {
            if (this.readers == null) {
                throw new IllegalStateException("IndexWriter has not initialized readers from the segment infos yet");
            }
            final List<CodecReader> readers = new ArrayList<CodecReader>(this.readers.size());
            for (final SegmentReader reader : this.readers) {
                if (reader.numDocs() > 0) {
                    readers.add(reader);
                }
            }
            return Collections.unmodifiableList((List<? extends CodecReader>)readers);
        }
        
        public void setMergeInfo(final SegmentCommitInfo info) {
            this.info = info;
        }
        
        public SegmentCommitInfo getMergeInfo() {
            return this.info;
        }
        
        public DocMap getDocMap(final MergeState mergeState) {
            return new DocMap() {
                @Override
                public int map(final int docID) {
                    return docID;
                }
            };
        }
        
        synchronized void setException(final Throwable error) {
            this.error = error;
        }
        
        synchronized Throwable getException() {
            return this.error;
        }
        
        public String segString() {
            final StringBuilder b = new StringBuilder();
            for (int numSegments = this.segments.size(), i = 0; i < numSegments; ++i) {
                if (i > 0) {
                    b.append(' ');
                }
                b.append(this.segments.get(i).toString());
            }
            if (this.info != null) {
                b.append(" into ").append(this.info.info.name);
            }
            if (this.maxNumSegments != -1) {
                b.append(" [maxNumSegments=" + this.maxNumSegments + "]");
            }
            if (this.rateLimiter.getAbort()) {
                b.append(" [ABORTED]");
            }
            return b.toString();
        }
        
        public long totalBytesSize() throws IOException {
            return this.totalMergeBytes;
        }
        
        public int totalNumDocs() throws IOException {
            int total = 0;
            for (final SegmentCommitInfo info : this.segments) {
                total += info.info.maxDoc();
            }
            return total;
        }
        
        public MergeInfo getStoreMergeInfo() {
            return new MergeInfo(this.totalMaxDoc, this.estimatedMergeBytes, this.isExternal, this.maxNumSegments);
        }
    }
    
    public static class MergeSpecification
    {
        public final List<OneMerge> merges;
        
        public MergeSpecification() {
            this.merges = new ArrayList<OneMerge>();
        }
        
        public void add(final OneMerge merge) {
            this.merges.add(merge);
        }
        
        public String segString(final Directory dir) {
            final StringBuilder b = new StringBuilder();
            b.append("MergeSpec:\n");
            for (int count = this.merges.size(), i = 0; i < count; ++i) {
                b.append("  ").append(1 + i).append(": ").append(this.merges.get(i).segString());
            }
            return b.toString();
        }
    }
    
    public static class MergeException extends RuntimeException
    {
        private Directory dir;
        
        public MergeException(final String message, final Directory dir) {
            super(message);
            this.dir = dir;
        }
        
        public MergeException(final Throwable exc, final Directory dir) {
            super(exc);
            this.dir = dir;
        }
        
        public Directory getDirectory() {
            return this.dir;
        }
    }
    
    public static class MergeAbortedException extends IOException
    {
        public MergeAbortedException() {
            super("merge is aborted");
        }
        
        public MergeAbortedException(final String message) {
            super(message);
        }
    }
}
