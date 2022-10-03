package org.apache.lucene.search;

import org.apache.lucene.util.IOUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Closeable;

public class SearcherLifetimeManager implements Closeable
{
    static final double NANOS_PER_SEC = 1.0E9;
    private volatile boolean closed;
    private final ConcurrentHashMap<Long, SearcherTracker> searchers;
    
    public SearcherLifetimeManager() {
        this.searchers = new ConcurrentHashMap<Long, SearcherTracker>();
    }
    
    private void ensureOpen() {
        if (this.closed) {
            throw new AlreadyClosedException("this SearcherLifetimeManager instance is closed");
        }
    }
    
    public long record(final IndexSearcher searcher) throws IOException {
        this.ensureOpen();
        final long version = ((DirectoryReader)searcher.getIndexReader()).getVersion();
        SearcherTracker tracker = this.searchers.get(version);
        if (tracker == null) {
            tracker = new SearcherTracker(searcher);
            if (this.searchers.putIfAbsent(version, tracker) != null) {
                tracker.close();
            }
        }
        else if (tracker.searcher != searcher) {
            throw new IllegalArgumentException("the provided searcher has the same underlying reader version yet the searcher instance differs from before (new=" + searcher + " vs old=" + tracker.searcher);
        }
        return version;
    }
    
    public IndexSearcher acquire(final long version) {
        this.ensureOpen();
        final SearcherTracker tracker = this.searchers.get(version);
        if (tracker != null && tracker.searcher.getIndexReader().tryIncRef()) {
            return tracker.searcher;
        }
        return null;
    }
    
    public void release(final IndexSearcher s) throws IOException {
        s.getIndexReader().decRef();
    }
    
    public synchronized void prune(final Pruner pruner) throws IOException {
        final List<SearcherTracker> trackers = new ArrayList<SearcherTracker>();
        for (final SearcherTracker tracker : this.searchers.values()) {
            trackers.add(tracker);
        }
        Collections.sort(trackers);
        double lastRecordTimeSec = 0.0;
        final double now = System.nanoTime() / 1.0E9;
        for (final SearcherTracker tracker2 : trackers) {
            double ageSec;
            if (lastRecordTimeSec == 0.0) {
                ageSec = 0.0;
            }
            else {
                ageSec = now - lastRecordTimeSec;
            }
            if (pruner.doPrune(ageSec, tracker2.searcher)) {
                this.searchers.remove(tracker2.version);
                tracker2.close();
            }
            lastRecordTimeSec = tracker2.recordTimeSec;
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.closed = true;
        final List<SearcherTracker> toClose = new ArrayList<SearcherTracker>(this.searchers.values());
        for (final SearcherTracker tracker : toClose) {
            this.searchers.remove(tracker.version);
        }
        IOUtils.close(toClose);
        if (this.searchers.size() != 0) {
            throw new IllegalStateException("another thread called record while this SearcherLifetimeManager instance was being closed; not all searchers were closed");
        }
    }
    
    private static class SearcherTracker implements Comparable<SearcherTracker>, Closeable
    {
        public final IndexSearcher searcher;
        public final double recordTimeSec;
        public final long version;
        
        public SearcherTracker(final IndexSearcher searcher) {
            this.searcher = searcher;
            this.version = ((DirectoryReader)searcher.getIndexReader()).getVersion();
            searcher.getIndexReader().incRef();
            this.recordTimeSec = System.nanoTime() / 1.0E9;
        }
        
        @Override
        public int compareTo(final SearcherTracker other) {
            return Double.compare(other.recordTimeSec, this.recordTimeSec);
        }
        
        @Override
        public synchronized void close() throws IOException {
            this.searcher.getIndexReader().decRef();
        }
    }
    
    public static final class PruneByAge implements Pruner
    {
        private final double maxAgeSec;
        
        public PruneByAge(final double maxAgeSec) {
            if (maxAgeSec < 0.0) {
                throw new IllegalArgumentException("maxAgeSec must be > 0 (got " + maxAgeSec + ")");
            }
            this.maxAgeSec = maxAgeSec;
        }
        
        @Override
        public boolean doPrune(final double ageSec, final IndexSearcher searcher) {
            return ageSec > this.maxAgeSec;
        }
    }
    
    public interface Pruner
    {
        boolean doPrune(final double p0, final IndexSearcher p1);
    }
}
