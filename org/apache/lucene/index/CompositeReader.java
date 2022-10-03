package org.apache.lucene.index;

import java.util.List;

public abstract class CompositeReader extends IndexReader
{
    private volatile CompositeReaderContext readerContext;
    
    protected CompositeReader() {
        this.readerContext = null;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        for (Class<?> clazz = this.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (!clazz.isAnonymousClass()) {
                buffer.append(clazz.getSimpleName());
                break;
            }
        }
        buffer.append('(');
        final List<? extends IndexReader> subReaders = this.getSequentialSubReaders();
        assert subReaders != null;
        if (!subReaders.isEmpty()) {
            buffer.append(subReaders.get(0));
            for (int i = 1, c = subReaders.size(); i < c; ++i) {
                buffer.append(" ").append(subReaders.get(i));
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
    
    protected abstract List<? extends IndexReader> getSequentialSubReaders();
    
    @Override
    public final CompositeReaderContext getContext() {
        this.ensureOpen();
        if (this.readerContext == null) {
            assert this.getSequentialSubReaders() != null;
            this.readerContext = CompositeReaderContext.create(this);
        }
        return this.readerContext;
    }
}
