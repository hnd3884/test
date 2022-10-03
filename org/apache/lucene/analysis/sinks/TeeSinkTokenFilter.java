package org.apache.lucene.analysis.sinks;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.AttributeSource;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;

public final class TeeSinkTokenFilter extends TokenFilter
{
    private final States cachedStates;
    
    public TeeSinkTokenFilter(final TokenStream input) {
        super(input);
        this.cachedStates = new States();
    }
    
    public TokenStream newSinkTokenStream() {
        return new SinkTokenStream(this.cloneAttributes(), this.cachedStates);
    }
    
    public void consumeAllTokens() throws IOException {
        while (this.incrementToken()) {}
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            this.cachedStates.add(this.captureState());
            return true;
        }
        return false;
    }
    
    public final void end() throws IOException {
        super.end();
        this.cachedStates.setFinalState(this.captureState());
    }
    
    public void reset() throws IOException {
        this.cachedStates.reset();
        super.reset();
    }
    
    public static final class SinkTokenStream extends TokenStream
    {
        private final States cachedStates;
        private Iterator<AttributeSource.State> it;
        
        private SinkTokenStream(final AttributeSource source, final States cachedStates) {
            super(source);
            this.it = null;
            this.cachedStates = cachedStates;
        }
        
        public final boolean incrementToken() {
            if (!this.it.hasNext()) {
                return false;
            }
            final AttributeSource.State state = this.it.next();
            this.restoreState(state);
            return true;
        }
        
        public void end() throws IOException {
            final AttributeSource.State finalState = this.cachedStates.getFinalState();
            if (finalState != null) {
                this.restoreState(finalState);
            }
        }
        
        public final void reset() {
            this.it = this.cachedStates.getStates();
        }
    }
    
    private static final class States
    {
        private final List<AttributeSource.State> states;
        private AttributeSource.State finalState;
        
        public States() {
            this.states = new ArrayList<AttributeSource.State>();
        }
        
        void setFinalState(final AttributeSource.State finalState) {
            this.finalState = finalState;
        }
        
        AttributeSource.State getFinalState() {
            return this.finalState;
        }
        
        void add(final AttributeSource.State state) {
            this.states.add(state);
        }
        
        Iterator<AttributeSource.State> getStates() {
            return this.states.iterator();
        }
        
        void reset() {
            this.finalState = null;
            this.states.clear();
        }
    }
}
