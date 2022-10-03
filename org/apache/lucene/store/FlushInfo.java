package org.apache.lucene.store;

public class FlushInfo
{
    public final int numDocs;
    public final long estimatedSegmentSize;
    
    public FlushInfo(final int numDocs, final long estimatedSegmentSize) {
        this.numDocs = numDocs;
        this.estimatedSegmentSize = estimatedSegmentSize;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.estimatedSegmentSize ^ this.estimatedSegmentSize >>> 32);
        result = 31 * result + this.numDocs;
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
        final FlushInfo other = (FlushInfo)obj;
        return this.estimatedSegmentSize == other.estimatedSegmentSize && this.numDocs == other.numDocs;
    }
    
    @Override
    public String toString() {
        return "FlushInfo [numDocs=" + this.numDocs + ", estimatedSegmentSize=" + this.estimatedSegmentSize + "]";
    }
}
