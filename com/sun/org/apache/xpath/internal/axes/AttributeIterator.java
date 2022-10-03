package com.sun.org.apache.xpath.internal.axes;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class AttributeIterator extends ChildTestIterator
{
    static final long serialVersionUID = -8417986700712229686L;
    
    AttributeIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
    }
    
    @Override
    protected int getNextNode() {
        return this.m_lastFetched = ((-1 == this.m_lastFetched) ? this.m_cdtm.getFirstAttribute(this.m_context) : this.m_cdtm.getNextAttribute(this.m_lastFetched));
    }
    
    @Override
    public int getAxis() {
        return 2;
    }
}
