package org.apache.lucene.analysis;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.AttributeSource;
import java.util.List;

public final class CachingTokenFilter extends TokenFilter
{
    private List<State> cache;
    private Iterator<State> iterator;
    private State finalState;
    
    public CachingTokenFilter(final TokenStream input) {
        super(input);
        this.cache = null;
        this.iterator = null;
    }
    
    @Override
    public void reset() throws IOException {
        if (this.cache == null) {
            this.input.reset();
        }
        else {
            this.iterator = this.cache.iterator();
        }
    }
    
    @Override
    public final boolean incrementToken() throws IOException {
        if (this.cache == null) {
            this.cache = new ArrayList<State>(64);
            this.fillCache();
            this.iterator = this.cache.iterator();
        }
        if (!this.iterator.hasNext()) {
            return false;
        }
        this.restoreState(this.iterator.next());
        return true;
    }
    
    @Override
    public final void end() {
        if (this.finalState != null) {
            this.restoreState(this.finalState);
        }
    }
    
    private void fillCache() throws IOException {
        while (this.input.incrementToken()) {
            this.cache.add(this.captureState());
        }
        this.input.end();
        this.finalState = this.captureState();
    }
    
    public boolean isCached() {
        return this.cache != null;
    }
}
