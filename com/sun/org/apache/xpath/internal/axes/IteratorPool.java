package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.util.ArrayList;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import java.io.Serializable;

public final class IteratorPool implements Serializable
{
    static final long serialVersionUID = -460927331149566998L;
    private final DTMIterator m_orig;
    private final ArrayList m_freeStack;
    
    public IteratorPool(final DTMIterator original) {
        this.m_orig = original;
        this.m_freeStack = new ArrayList();
    }
    
    public synchronized DTMIterator getInstanceOrThrow() throws CloneNotSupportedException {
        if (this.m_freeStack.isEmpty()) {
            return (DTMIterator)this.m_orig.clone();
        }
        final DTMIterator result = this.m_freeStack.remove(this.m_freeStack.size() - 1);
        return result;
    }
    
    public synchronized DTMIterator getInstance() {
        if (this.m_freeStack.isEmpty()) {
            try {
                return (DTMIterator)this.m_orig.clone();
            }
            catch (final Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        }
        final DTMIterator result = this.m_freeStack.remove(this.m_freeStack.size() - 1);
        return result;
    }
    
    public synchronized void freeInstance(final DTMIterator obj) {
        this.m_freeStack.add(obj);
    }
}
