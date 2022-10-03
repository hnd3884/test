package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class ChildIterator extends LocPathIterator
{
    static final long serialVersionUID = -6935428015142993583L;
    
    ChildIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        this.initNodeTest(-1);
    }
    
    @Override
    public int asNode(final XPathContext xctxt) throws TransformerException {
        final int current = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(current);
        return dtm.getFirstChild(current);
    }
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        final int next = this.m_lastFetched = ((-1 == this.m_lastFetched) ? this.m_cdtm.getFirstChild(this.m_context) : this.m_cdtm.getNextSibling(this.m_lastFetched));
        if (-1 != next) {
            ++this.m_pos;
            return next;
        }
        this.m_foundLast = true;
        return -1;
    }
    
    @Override
    public int getAxis() {
        return 3;
    }
}
