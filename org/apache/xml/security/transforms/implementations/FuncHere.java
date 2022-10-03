package org.apache.xml.security.transforms.implementations;

import org.apache.xpath.Expression;
import java.util.Vector;
import org.apache.xpath.NodeSetDTM;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.objects.XNodeSet;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function;

public class FuncHere extends Function
{
    private static final long serialVersionUID = 1L;
    
    public XObject execute(final XPathContext xPathContext) throws TransformerException {
        final Node node = (Node)xPathContext.getOwnerObject();
        if (node == null) {
            return null;
        }
        final int dtmHandleFromNode = xPathContext.getDTMHandleFromNode(node);
        final int currentNode = xPathContext.getCurrentNode();
        final DTM dtm = xPathContext.getDTM(currentNode);
        if (-1 == dtm.getDocument()) {
            ((Expression)this).error(xPathContext, "ER_CONTEXT_HAS_NO_OWNERDOC", (Object[])null);
        }
        if (XMLUtils.getOwnerDocument(dtm.getNode(currentNode)) != XMLUtils.getOwnerDocument(node)) {
            throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer"));
        }
        final XNodeSet set = new XNodeSet(xPathContext.getDTMManager());
        final NodeSetDTM mutableNodeset = set.mutableNodeset();
        switch (dtm.getNodeType(dtmHandleFromNode)) {
            case 2: {
                mutableNodeset.addNode(dtmHandleFromNode);
                break;
            }
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
        return (XObject)set;
    }
    
    public void fixupVariables(final Vector vector, final int n) {
    }
}
