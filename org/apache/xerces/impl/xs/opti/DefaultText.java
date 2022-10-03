package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DefaultText extends NodeImpl implements Text
{
    public String getData() throws DOMException {
        return null;
    }
    
    public void setData(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public int getLength() {
        return 0;
    }
    
    public String substringData(final int n, final int n2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void appendData(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void insertData(final int n, final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void deleteData(final int n, final int n2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void replaceData(final int n, final int n2, final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Text splitText(final int n) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public boolean isElementContentWhitespace() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String getWholeText() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Text replaceWholeText(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
