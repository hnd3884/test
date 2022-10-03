package com.sun.org.apache.xpath.internal.objects;

import javax.xml.transform.TransformerException;
import org.w3c.dom.traversal.NodeIterator;
import com.sun.org.apache.xpath.internal.XPathContext;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import org.w3c.dom.Node;

public class XNodeSetForDOM extends XNodeSet
{
    static final long serialVersionUID = -8396190713754624640L;
    Object m_origObj;
    
    public XNodeSetForDOM(final Node node, final DTMManager dtmMgr) {
        this.m_dtmMgr = dtmMgr;
        this.m_origObj = node;
        final int dtmHandle = dtmMgr.getDTMHandleFromNode(node);
        this.setObject(new NodeSetDTM(dtmMgr));
        ((NodeSetDTM)this.m_obj).addNode(dtmHandle);
    }
    
    public XNodeSetForDOM(final XNodeSet val) {
        super(val);
        if (val instanceof XNodeSetForDOM) {
            this.m_origObj = ((XNodeSetForDOM)val).m_origObj;
        }
    }
    
    public XNodeSetForDOM(final NodeList nodeList, final XPathContext xctxt) {
        this.m_dtmMgr = xctxt.getDTMManager();
        this.m_origObj = nodeList;
        final NodeSetDTM nsdtm = new NodeSetDTM(nodeList, xctxt);
        this.m_last = nsdtm.getLength();
        this.setObject(nsdtm);
    }
    
    public XNodeSetForDOM(final NodeIterator nodeIter, final XPathContext xctxt) {
        this.m_dtmMgr = xctxt.getDTMManager();
        this.m_origObj = nodeIter;
        final NodeSetDTM nsdtm = new NodeSetDTM(nodeIter, xctxt);
        this.m_last = nsdtm.getLength();
        this.setObject(nsdtm);
    }
    
    @Override
    public Object object() {
        return this.m_origObj;
    }
    
    @Override
    public NodeIterator nodeset() throws TransformerException {
        return (NodeIterator)((this.m_origObj instanceof NodeIterator) ? this.m_origObj : super.nodeset());
    }
    
    @Override
    public NodeList nodelist() throws TransformerException {
        return (NodeList)((this.m_origObj instanceof NodeList) ? this.m_origObj : super.nodelist());
    }
}
