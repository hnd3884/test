package com.sun.org.apache.xpath.internal.objects;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;

public class XStringForChars extends XString
{
    static final long serialVersionUID = -2235248887220850467L;
    int m_start;
    int m_length;
    protected String m_strCache;
    
    public XStringForChars(final char[] val, final int start, final int length) {
        super(val);
        this.m_strCache = null;
        this.m_start = start;
        this.m_length = length;
        if (null == val) {
            throw new IllegalArgumentException(XPATHMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
        }
    }
    
    private XStringForChars(final String val) {
        super(val);
        this.m_strCache = null;
        throw new IllegalArgumentException(XPATHMessages.createXPATHMessage("ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING", null));
    }
    
    public FastStringBuffer fsb() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS", null));
    }
    
    @Override
    public void appendToFsb(final FastStringBuffer fsb) {
        fsb.append((char[])this.m_obj, this.m_start, this.m_length);
    }
    
    @Override
    public boolean hasString() {
        return null != this.m_strCache;
    }
    
    @Override
    public String str() {
        if (null == this.m_strCache) {
            this.m_strCache = new String((char[])this.m_obj, this.m_start, this.m_length);
        }
        return this.m_strCache;
    }
    
    @Override
    public Object object() {
        return this.str();
    }
    
    @Override
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
        ch.characters((char[])this.m_obj, this.m_start, this.m_length);
    }
    
    @Override
    public void dispatchAsComment(final LexicalHandler lh) throws SAXException {
        lh.comment((char[])this.m_obj, this.m_start, this.m_length);
    }
    
    @Override
    public int length() {
        return this.m_length;
    }
    
    @Override
    public char charAt(final int index) {
        return ((char[])this.m_obj)[index + this.m_start];
    }
    
    @Override
    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        System.arraycopy(this.m_obj, this.m_start + srcBegin, dst, dstBegin, srcEnd);
    }
}
