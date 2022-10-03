package com.sun.org.apache.xml.internal.security.transforms.implementations;

import java.util.Vector;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Node;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.functions.Function;

public class FuncHere extends Function
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final Node node = (Node)xctxt.getOwnerObject();
        if (node == null) {
            return null;
        }
        final int dtmHandleFromNode = xctxt.getDTMHandleFromNode(node);
        final int currentNode = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(currentNode);
        if (-1 == dtm.getDocument()) {
            this.error(xctxt, "ER_CONTEXT_HAS_NO_OWNERDOC", null);
        }
        if (XMLUtils.getOwnerDocument(dtm.getNode(currentNode)) != XMLUtils.getOwnerDocument(node)) {
            throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer"));
        }
        final XNodeSet set = new XNodeSet(xctxt.getDTMManager());
        final NodeSetDTM mutableNodeset = set.mutableNodeset();
        switch (dtm.getNodeType(dtmHandleFromNode)) {
            case 2:
            case 7: {
                mutableNodeset.addNode(dtmHandleFromNode);
                break;
            }
            case 3: {
                mutableNodeset.addNode(dtm.getParent(dtmHandleFromNode));
                break;
            }
        }
        mutableNodeset.detach();
        return set;
    }
    
    @Override
    public void fixupVariables(final Vector vector, final int n) {
    }
}
