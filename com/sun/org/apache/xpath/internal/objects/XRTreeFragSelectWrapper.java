package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.XPathContext;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;

public class XRTreeFragSelectWrapper extends XRTreeFrag implements Cloneable
{
    static final long serialVersionUID = -6526177905590461251L;
    
    public XRTreeFragSelectWrapper(final Expression expr) {
        super(expr);
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        ((Expression)this.m_obj).fixupVariables(vars, globalsSize);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XObject m_selected = ((Expression)this.m_obj).execute(xctxt);
        m_selected.allowDetachToRelease(this.m_allowRelease);
        if (m_selected.getType() == 3) {
            return m_selected;
        }
        return new XString(m_selected.str());
    }
    
    @Override
    public void detach() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
    
    @Override
    public double num() throws TransformerException {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
    
    @Override
    public XMLString xstr() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
    
    @Override
    public String str() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
    
    @Override
    public int getType() {
        return 3;
    }
    
    @Override
    public int rtf() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
    
    @Override
    public DTMIterator asNodeIterator() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
    }
}
