package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.CharacterData;

public class DOMCharacterDataImpl extends DOMNodeImpl implements CharacterData
{
    protected DOMCharacterDataImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public String getData() throws DOMException {
        return this.getNodeValue();
    }
    
    public int getLength() {
        int n = 0;
        if (this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            n = this.adaptee.end - this.adaptee.start;
        }
        return n;
    }
    
    public String substringData(final int n, final int n2) throws DOMException {
        String string = null;
        if (n2 < 0) {
            throw new DOMException((short)1, "Invalid length");
        }
        if (this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            if (this.adaptee.start + n >= this.adaptee.end) {
                throw new DOMException((short)1, "Invalid offset");
            }
            int n3 = n2;
            if (this.adaptee.start + n + n3 - 1 >= this.adaptee.end) {
                n3 = this.adaptee.end - this.adaptee.start - n;
            }
            string = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start + n, n3);
        }
        return string;
    }
    
    public void setData(final String s) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public void appendData(final String s) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public void insertData(final int n, final String s) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public void deleteData(final int n, final int n2) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public void replaceData(final int n, final int n2, final String s) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
}
