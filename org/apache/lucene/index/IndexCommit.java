package org.apache.lucene.index;

import java.util.Map;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.Collection;

public abstract class IndexCommit implements Comparable<IndexCommit>
{
    public abstract String getSegmentsFileName();
    
    public abstract Collection<String> getFileNames() throws IOException;
    
    public abstract Directory getDirectory();
    
    public abstract void delete();
    
    public abstract boolean isDeleted();
    
    public abstract int getSegmentCount();
    
    protected IndexCommit() {
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof IndexCommit) {
            final IndexCommit otherCommit = (IndexCommit)other;
            return otherCommit.getDirectory() == this.getDirectory() && otherCommit.getGeneration() == this.getGeneration();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getDirectory().hashCode() + Long.valueOf(this.getGeneration()).hashCode();
    }
    
    public abstract long getGeneration();
    
    public abstract Map<String, String> getUserData() throws IOException;
    
    @Override
    public int compareTo(final IndexCommit commit) {
        if (this.getDirectory() != commit.getDirectory()) {
            throw new UnsupportedOperationException("cannot compare IndexCommits from different Directory instances");
        }
        final long gen = this.getGeneration();
        final long comgen = commit.getGeneration();
        return Long.compare(gen, comgen);
    }
    
    StandardDirectoryReader getReader() {
        return null;
    }
}
