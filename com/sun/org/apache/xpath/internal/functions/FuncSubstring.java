package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncSubstring extends Function3Args
{
    static final long serialVersionUID = -5996676095024715502L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XMLString s1 = this.m_arg0.execute(xctxt).xstr();
        double start = this.m_arg1.execute(xctxt).num();
        final int lenOfS1 = s1.length();
        if (lenOfS1 <= 0) {
            return XString.EMPTYSTRING;
        }
        int startIndex;
        if (Double.isNaN(start)) {
            start = -1000000.0;
            startIndex = 0;
        }
        else {
            start = (double)Math.round(start);
            startIndex = ((start > 0.0) ? ((int)start - 1) : 0);
        }
        XMLString substr;
        if (null != this.m_arg2) {
            final double len = this.m_arg2.num(xctxt);
            int end = (int)(Math.round(len) + start) - 1;
            if (end < 0) {
                end = 0;
            }
            else if (end > lenOfS1) {
                end = lenOfS1;
            }
            if (startIndex > lenOfS1) {
                startIndex = lenOfS1;
            }
            substr = s1.substring(startIndex, end);
        }
        else {
            if (startIndex > lenOfS1) {
                startIndex = lenOfS1;
            }
            substr = s1.substring(startIndex);
        }
        return (XString)substr;
    }
    
    @Override
    public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
        if (argNum < 2) {
            this.reportWrongNumberArgs();
        }
    }
    
    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("ER_TWO_OR_THREE", null));
    }
}
