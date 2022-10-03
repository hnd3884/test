package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class SelfIteratorNoPredicate extends LocPathIterator
{
    static final long serialVersionUID = -4226887905279814201L;
    
    SelfIteratorNoPredicate(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
    }
    
    public SelfIteratorNoPredicate() throws TransformerException {
        super((PrefixResolver)null);
    }
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        final DTM dtm = this.m_cdtm;
        final int next = this.m_lastFetched = ((-1 == this.m_lastFetched) ? this.m_context : -1);
        if (-1 != next) {
            ++this.m_pos;
            return next;
        }
        this.m_foundLast = true;
        return -1;
    }
    
    @Override
    public int asNode(final XPathContext xctxt) throws TransformerException {
        return xctxt.getCurrentNode();
    }
    
    @Override
    public int getLastPos(final XPathContext xctxt) {
        return 1;
    }
}
