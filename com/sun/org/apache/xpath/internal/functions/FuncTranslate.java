package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncTranslate extends Function3Args
{
    static final long serialVersionUID = -1672834340026116482L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String theFirstString = this.m_arg0.execute(xctxt).str();
        final String theSecondString = this.m_arg1.execute(xctxt).str();
        final String theThirdString = this.m_arg2.execute(xctxt).str();
        final int theFirstStringLength = theFirstString.length();
        final int theThirdStringLength = theThirdString.length();
        final StringBuffer sbuffer = new StringBuffer();
        for (int i = 0; i < theFirstStringLength; ++i) {
            final char theCurrentChar = theFirstString.charAt(i);
            final int theIndex = theSecondString.indexOf(theCurrentChar);
            if (theIndex < 0) {
                sbuffer.append(theCurrentChar);
            }
            else if (theIndex < theThirdStringLength) {
                sbuffer.append(theThirdString.charAt(theIndex));
            }
        }
        return new XString(sbuffer.toString());
    }
}
