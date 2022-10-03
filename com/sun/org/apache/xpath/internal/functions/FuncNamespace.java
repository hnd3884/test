package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncNamespace extends FunctionDef1Arg
{
    static final long serialVersionUID = -4695674566722321237L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final int context = this.getArg0AsNode(xctxt);
        if (context != -1) {
            final DTM dtm = xctxt.getDTM(context);
            final int t = dtm.getNodeType(context);
            String s;
            if (t == 1) {
                s = dtm.getNamespaceURI(context);
            }
            else {
                if (t != 2) {
                    return XString.EMPTYSTRING;
                }
                s = dtm.getNodeName(context);
                if (s.startsWith("xmlns:") || s.equals("xmlns")) {
                    return XString.EMPTYSTRING;
                }
                s = dtm.getNamespaceURI(context);
            }
            return (null == s) ? XString.EMPTYSTRING : new XString(s);
        }
        return XString.EMPTYSTRING;
    }
}
