package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;

public class ChildTestIterator extends BasicTestIterator
{
    static final long serialVersionUID = -7936835957960705722L;
    protected transient DTMAxisTraverser m_traverser;
    
    ChildTestIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
    }
    
    public ChildTestIterator(final DTMAxisTraverser traverser) {
        super((PrefixResolver)null);
        this.m_traverser = traverser;
    }
    
    @Override
    protected int getNextNode() {
        return this.m_lastFetched = ((-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched));
    }
    
    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final ChildTestIterator clone = (ChildTestIterator)super.cloneWithReset();
        clone.m_traverser = this.m_traverser;
        return clone;
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        this.m_traverser = this.m_cdtm.getAxisTraverser(3);
    }
    
    @Override
    public int getAxis() {
        return 3;
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_traverser = null;
            super.detach();
        }
    }
}
