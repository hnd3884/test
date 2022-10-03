package com.sun.org.apache.xml.internal.dtm.ref;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import org.w3c.dom.traversal.NodeFilter;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import org.w3c.dom.traversal.NodeIterator;

public class DTMNodeIterator implements NodeIterator
{
    private DTMIterator dtm_iter;
    private boolean valid;
    
    public DTMNodeIterator(final DTMIterator dtmIterator) {
        this.valid = true;
        try {
            this.dtm_iter = (DTMIterator)dtmIterator.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            throw new WrappedRuntimeException(cnse);
        }
    }
    
    public DTMIterator getDTMIterator() {
        return this.dtm_iter;
    }
    
    @Override
    public void detach() {
        this.valid = false;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return false;
    }
    
    @Override
    public NodeFilter getFilter() {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public Node getRoot() {
        final int handle = this.dtm_iter.getRoot();
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }
    
    @Override
    public int getWhatToShow() {
        return this.dtm_iter.getWhatToShow();
    }
    
    @Override
    public Node nextNode() throws DOMException {
        if (!this.valid) {
            throw new DTMDOMException((short)11);
        }
        final int handle = this.dtm_iter.nextNode();
        if (handle == -1) {
            return null;
        }
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }
    
    @Override
    public Node previousNode() {
        if (!this.valid) {
            throw new DTMDOMException((short)11);
        }
        final int handle = this.dtm_iter.previousNode();
        if (handle == -1) {
            return null;
        }
        return this.dtm_iter.getDTM(handle).getNode(handle);
    }
}
