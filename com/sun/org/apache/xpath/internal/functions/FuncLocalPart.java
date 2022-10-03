package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncLocalPart extends FunctionDef1Arg
{
    static final long serialVersionUID = 7591798770325814746L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final int context = this.getArg0AsNode(xctxt);
        if (-1 == context) {
            return XString.EMPTYSTRING;
        }
        final DTM dtm = xctxt.getDTM(context);
        final String s = (context != -1) ? dtm.getLocalName(context) : "";
        if (s.startsWith("#") || s.equals("xmlns")) {
            return XString.EMPTYSTRING;
        }
        return new XString(s);
    }
}
