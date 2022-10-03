package org.apache.lucene.store;

public class MergeInfo
{
    public final int totalMaxDoc;
    public final long estimatedMergeBytes;
    public final boolean isExternal;
    public final int mergeMaxNumSegments;
    
    public MergeInfo(final int totalMaxDoc, final long estimatedMergeBytes, final boolean isExternal, final int mergeMaxNumSegments) {
        this.totalMaxDoc = totalMaxDoc;
        this.estimatedMergeBytes = estimatedMergeBytes;
        this.isExternal = isExternal;
        this.mergeMaxNumSegments = mergeMaxNumSegments;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.estimatedMergeBytes ^ this.estimatedMergeBytes >>> 32);
        result = 31 * result + (this.isExternal ? 1231 : 1237);
        result = 31 * result + this.mergeMaxNumSegments;
        result = 31 * result + this.totalMaxDoc;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MergeInfo other = (MergeInfo)obj;
        return this.estimatedMergeBytes == other.estimatedMergeBytes && this.isExternal == other.isExternal && this.mergeMaxNumSegments == other.mergeMaxNumSegments && this.totalMaxDoc == other.totalMaxDoc;
    }
    
    @Override
    public String toString() {
        return "MergeInfo [totalMaxDoc=" + this.totalMaxDoc + ", estimatedMergeBytes=" + this.estimatedMergeBytes + ", isExternal=" + this.isExternal + ", mergeMaxNumSegments=" + this.mergeMaxNumSegments + "]";
    }
}
