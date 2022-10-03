package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Iterator;
import java.util.Arrays;

public class MultiCollector implements Collector
{
    private final boolean cacheScores;
    private final Collector[] collectors;
    
    public static Collector wrap(final Collector... collectors) {
        return wrap(Arrays.asList(collectors));
    }
    
    public static Collector wrap(final Iterable<? extends Collector> collectors) {
        int n = 0;
        for (final Collector c : collectors) {
            if (c != null) {
                ++n;
            }
        }
        if (n == 0) {
            throw new IllegalArgumentException("At least 1 collector must not be null");
        }
        if (n == 1) {
            Collector col = null;
            for (final Collector c2 : collectors) {
                if (c2 != null) {
                    col = c2;
                    break;
                }
            }
            return col;
        }
        final Collector[] colls = new Collector[n];
        n = 0;
        for (final Collector c2 : collectors) {
            if (c2 != null) {
                colls[n++] = c2;
            }
        }
        return new MultiCollector(colls);
    }
    
    private MultiCollector(final Collector... collectors) {
        this.collectors = collectors;
        int numNeedsScores = 0;
        for (final Collector collector : collectors) {
            if (collector.needsScores()) {
                ++numNeedsScores;
            }
        }
        this.cacheScores = (numNeedsScores >= 2);
    }
    
    @Override
    public boolean needsScores() {
        for (final Collector collector : this.collectors) {
            if (collector.needsScores()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        final List<LeafCollector> leafCollectors = new ArrayList<LeafCollector>();
        for (final Collector collector : this.collectors) {
            Label_0060: {
                LeafCollector leafCollector;
                try {
                    leafCollector = collector.getLeafCollector(context);
                }
                catch (final CollectionTerminatedException e) {
                    break Label_0060;
                }
                leafCollectors.add(leafCollector);
            }
        }
        switch (leafCollectors.size()) {
            case 0: {
                throw new CollectionTerminatedException();
            }
            case 1: {
                return leafCollectors.get(0);
            }
            default: {
                return new MultiLeafCollector((List)leafCollectors, this.cacheScores);
            }
        }
    }
    
    private static class MultiLeafCollector implements LeafCollector
    {
        private final boolean cacheScores;
        private final LeafCollector[] collectors;
        private int numCollectors;
        
        private MultiLeafCollector(final List<LeafCollector> collectors, final boolean cacheScores) {
            this.collectors = collectors.toArray(new LeafCollector[collectors.size()]);
            this.cacheScores = cacheScores;
            this.numCollectors = this.collectors.length;
        }
        
        @Override
        public void setScorer(Scorer scorer) throws IOException {
            if (this.cacheScores) {
                scorer = new ScoreCachingWrappingScorer(scorer);
            }
            for (int i = 0; i < this.numCollectors; ++i) {
                final LeafCollector c = this.collectors[i];
                c.setScorer(scorer);
            }
        }
        
        private void removeCollector(final int i) {
            System.arraycopy(this.collectors, i + 1, this.collectors, i, this.numCollectors - i - 1);
            --this.numCollectors;
            this.collectors[this.numCollectors] = null;
        }
        
        @Override
        public void collect(final int doc) throws IOException {
            final LeafCollector[] collectors = this.collectors;
            int numCollectors = this.numCollectors;
            int i = 0;
            while (i < numCollectors) {
                final LeafCollector collector = collectors[i];
                try {
                    collector.collect(doc);
                    ++i;
                }
                catch (final CollectionTerminatedException e) {
                    this.removeCollector(i);
                    numCollectors = this.numCollectors;
                    if (numCollectors == 0) {
                        throw new CollectionTerminatedException();
                    }
                    continue;
                }
            }
        }
    }
}
