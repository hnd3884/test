package org.apache.axiom.soap.impl.common;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import java.util.Iterator;

public class HeaderIterator implements Iterator
{
    SOAPHeaderBlock current;
    boolean advance;
    Checker checker;
    
    public HeaderIterator(final SOAPHeader header) {
        this(header, null);
    }
    
    public HeaderIterator(final SOAPHeader header, final Checker checker) {
        this.advance = false;
        this.checker = checker;
        this.current = (SOAPHeaderBlock)header.getFirstElement();
        if (this.current != null && !this.checkHeader(this.current)) {
            this.advance = true;
            this.hasNext();
        }
    }
    
    public void remove() {
    }
    
    public boolean checkHeader(final SOAPHeaderBlock header) {
        return this.checker == null || this.checker.checkHeader(header);
    }
    
    public boolean hasNext() {
        if (!this.advance) {
            return this.current != null;
        }
        this.advance = false;
        for (OMNode sibling = this.current.getNextOMSibling(); sibling != null; sibling = sibling.getNextOMSibling()) {
            if (sibling instanceof SOAPHeaderBlock) {
                final SOAPHeaderBlock possible = (SOAPHeaderBlock)sibling;
                if (this.checkHeader(possible)) {
                    this.current = (SOAPHeaderBlock)sibling;
                    return true;
                }
            }
        }
        this.current = null;
        return false;
    }
    
    public Object next() {
        final SOAPHeaderBlock ret = this.current;
        if (ret != null) {
            this.advance = true;
            this.hasNext();
        }
        return ret;
    }
}
