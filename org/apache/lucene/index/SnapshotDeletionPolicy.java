package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SnapshotDeletionPolicy extends IndexDeletionPolicy
{
    protected final Map<Long, Integer> refCounts;
    protected final Map<Long, IndexCommit> indexCommits;
    private final IndexDeletionPolicy primary;
    protected IndexCommit lastCommit;
    private boolean initCalled;
    
    public SnapshotDeletionPolicy(final IndexDeletionPolicy primary) {
        this.refCounts = new HashMap<Long, Integer>();
        this.indexCommits = new HashMap<Long, IndexCommit>();
        this.primary = primary;
    }
    
    @Override
    public synchronized void onCommit(final List<? extends IndexCommit> commits) throws IOException {
        this.primary.onCommit(this.wrapCommits(commits));
        this.lastCommit = (IndexCommit)commits.get(commits.size() - 1);
    }
    
    @Override
    public synchronized void onInit(final List<? extends IndexCommit> commits) throws IOException {
        this.initCalled = true;
        this.primary.onInit(this.wrapCommits(commits));
        for (final IndexCommit commit : commits) {
            if (this.refCounts.containsKey(commit.getGeneration())) {
                this.indexCommits.put(commit.getGeneration(), commit);
            }
        }
        if (!commits.isEmpty()) {
            this.lastCommit = (IndexCommit)commits.get(commits.size() - 1);
        }
    }
    
    public synchronized void release(final IndexCommit commit) throws IOException {
        final long gen = commit.getGeneration();
        this.releaseGen(gen);
    }
    
    protected void releaseGen(final long gen) throws IOException {
        if (!this.initCalled) {
            throw new IllegalStateException("this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()");
        }
        final Integer refCount = this.refCounts.get(gen);
        if (refCount == null) {
            throw new IllegalArgumentException("commit gen=" + gen + " is not currently snapshotted");
        }
        int refCountInt = refCount;
        assert refCountInt > 0;
        if (--refCountInt == 0) {
            this.refCounts.remove(gen);
            this.indexCommits.remove(gen);
        }
        else {
            this.refCounts.put(gen, refCountInt);
        }
    }
    
    protected synchronized void incRef(final IndexCommit ic) {
        final long gen = ic.getGeneration();
        final Integer refCount = this.refCounts.get(gen);
        int refCountInt;
        if (refCount == null) {
            this.indexCommits.put(gen, this.lastCommit);
            refCountInt = 0;
        }
        else {
            refCountInt = refCount;
        }
        this.refCounts.put(gen, refCountInt + 1);
    }
    
    public synchronized IndexCommit snapshot() throws IOException {
        if (!this.initCalled) {
            throw new IllegalStateException("this instance is not being used by IndexWriter; be sure to use the instance returned from writer.getConfig().getIndexDeletionPolicy()");
        }
        if (this.lastCommit == null) {
            throw new IllegalStateException("No index commit to snapshot");
        }
        this.incRef(this.lastCommit);
        return this.lastCommit;
    }
    
    public synchronized List<IndexCommit> getSnapshots() {
        return new ArrayList<IndexCommit>(this.indexCommits.values());
    }
    
    public synchronized int getSnapshotCount() {
        int total = 0;
        for (final Integer refCount : this.refCounts.values()) {
            total += refCount;
        }
        return total;
    }
    
    public synchronized IndexCommit getIndexCommit(final long gen) {
        return this.indexCommits.get(gen);
    }
    
    private List<IndexCommit> wrapCommits(final List<? extends IndexCommit> commits) {
        final List<IndexCommit> wrappedCommits = new ArrayList<IndexCommit>(commits.size());
        for (final IndexCommit ic : commits) {
            wrappedCommits.add(new SnapshotCommitPoint(ic));
        }
        return wrappedCommits;
    }
    
    private class SnapshotCommitPoint extends IndexCommit
    {
        protected IndexCommit cp;
        
        protected SnapshotCommitPoint(final IndexCommit cp) {
            this.cp = cp;
        }
        
        @Override
        public String toString() {
            return "SnapshotDeletionPolicy.SnapshotCommitPoint(" + this.cp + ")";
        }
        
        @Override
        public void delete() {
            synchronized (SnapshotDeletionPolicy.this) {
                if (!SnapshotDeletionPolicy.this.refCounts.containsKey(this.cp.getGeneration())) {
                    this.cp.delete();
                }
            }
        }
        
        @Override
        public Directory getDirectory() {
            return this.cp.getDirectory();
        }
        
        @Override
        public Collection<String> getFileNames() throws IOException {
            return this.cp.getFileNames();
        }
        
        @Override
        public long getGeneration() {
            return this.cp.getGeneration();
        }
        
        @Override
        public String getSegmentsFileName() {
            return this.cp.getSegmentsFileName();
        }
        
        @Override
        public Map<String, String> getUserData() throws IOException {
            return this.cp.getUserData();
        }
        
        @Override
        public boolean isDeleted() {
            return this.cp.isDeleted();
        }
        
        @Override
        public int getSegmentCount() {
            return this.cp.getSegmentCount();
        }
    }
}
