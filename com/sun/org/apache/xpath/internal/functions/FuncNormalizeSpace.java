package com.sun.org.apache.xpath.internal.functions;

import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncNormalizeSpace extends FunctionDef1Arg
{
    static final long serialVersionUID = -3377956872032190880L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XMLString s1 = this.getArg0AsString(xctxt);
        return (XString)s1.fixWhiteSpace(true, true, false);
    }
    
    @Override
    public void executeCharsToContentHandler(final XPathContext xctxt, final ContentHandler handler) throws TransformerException, SAXException {
        if (this.Arg0IsNodesetExpr()) {
            final int node = this.getArg0AsNode(xctxt);
            if (-1 != node) {
                final DTM dtm = xctxt.getDTM(node);
                dtm.dispatchCharactersEvents(node, handler, true);
            }
        }
        else {
            final XObject obj = this.execute(xctxt);
            obj.dispatchCharactersEvents(handler);
        }
    }
}
