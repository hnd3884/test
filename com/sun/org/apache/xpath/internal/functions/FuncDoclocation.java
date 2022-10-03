package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncDoclocation extends FunctionDef1Arg
{
    static final long serialVersionUID = 7469213946343568769L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        int whereNode = this.getArg0AsNode(xctxt);
        String fileLocation = null;
        if (-1 != whereNode) {
            final DTM dtm = xctxt.getDTM(whereNode);
            if (11 == dtm.getNodeType(whereNode)) {
                whereNode = dtm.getFirstChild(whereNode);
            }
            if (-1 != whereNode) {
                fileLocation = dtm.getDocumentBaseURI();
            }
        }
        return new XString((null != fileLocation) ? fileLocation : "");
    }
}
