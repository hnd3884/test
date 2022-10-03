package com.sun.org.apache.xml.internal.dtm.ref;

import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;

public class DTMNodeList extends DTMNodeListBase
{
    private DTMIterator m_iter;
    
    private DTMNodeList() {
    }
    
    public DTMNodeList(final DTMIterator dtmIterator) {
        if (dtmIterator != null) {
            final int pos = dtmIterator.getCurrentPos();
            try {
                this.m_iter = dtmIterator.cloneWithReset();
            }
            catch (final CloneNotSupportedException cnse) {
                this.m_iter = dtmIterator;
            }
            this.m_iter.setShouldCacheNodes(true);
            this.m_iter.runTo(-1);
            this.m_iter.setCurrentPos(pos);
        }
    }
    
    public DTMIterator getDTMIterator() {
        return this.m_iter;
    }
    
    @Override
    public Node item(final int index) {
        if (this.m_iter == null) {
            return null;
        }
        final int handle = this.m_iter.item(index);
        if (handle == -1) {
            return null;
        }
        return this.m_iter.getDTM(handle).getNode(handle);
    }
    
    @Override
    public int getLength() {
        return (this.m_iter != null) ? this.m_iter.getLength() : 0;
    }
}
