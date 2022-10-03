package com.sun.org.apache.xerces.internal.util;

import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public final class DOMInputSource extends XMLInputSource
{
    private Node fNode;
    
    public DOMInputSource() {
        this((Node)null);
    }
    
    public DOMInputSource(final Node node) {
        super(null, getSystemIdFromNode(node), null);
        this.fNode = node;
    }
    
    public DOMInputSource(final Node node, final String systemId) {
        super(null, systemId, null);
        this.fNode = node;
    }
    
    public Node getNode() {
        return this.fNode;
    }
    
    public void setNode(final Node node) {
        this.fNode = node;
    }
    
    private static String getSystemIdFromNode(final Node node) {
        if (node != null) {
            try {
                return node.getBaseURI();
            }
            catch (final NoSuchMethodError e) {
                return null;
            }
            catch (final Exception e2) {
                return null;
            }
        }
        return null;
    }
}
