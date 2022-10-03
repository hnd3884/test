package org.apache.lucene.search.suggest;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

public interface InputIterator extends BytesRefIterator
{
    public static final InputIterator EMPTY = new InputIteratorWrapper(BytesRefIterator.EMPTY);
    
    long weight();
    
    BytesRef payload();
    
    boolean hasPayloads();
    
    Set<BytesRef> contexts();
    
    boolean hasContexts();
    
    public static class InputIteratorWrapper implements InputIterator
    {
        private final BytesRefIterator wrapped;
        
        public InputIteratorWrapper(final BytesRefIterator wrapped) {
            this.wrapped = wrapped;
        }
        
        @Override
        public long weight() {
            return 1L;
        }
        
        public BytesRef next() throws IOException {
            return this.wrapped.next();
        }
        
        @Override
        public BytesRef payload() {
            return null;
        }
        
        @Override
        public boolean hasPayloads() {
            return false;
        }
        
        @Override
        public Set<BytesRef> contexts() {
            return null;
        }
        
        @Override
        public boolean hasContexts() {
            return false;
        }
    }
}
