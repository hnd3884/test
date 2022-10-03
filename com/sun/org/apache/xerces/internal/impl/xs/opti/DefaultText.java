package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DefaultText extends NodeImpl implements Text
{
    @Override
    public String getData() throws DOMException {
        return null;
    }
    
    @Override
    public void setData(final String data) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public int getLength() {
        return 0;
    }
    
    @Override
    public String substringData(final int offset, final int count) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void appendData(final String arg) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void insertData(final int offset, final String arg) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void deleteData(final int offset, final int count) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void replaceData(final int offset, final int count, final String arg) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Text splitText(final int offset) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isElementContentWhitespace() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getWholeText() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Text replaceWholeText(final String content) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
