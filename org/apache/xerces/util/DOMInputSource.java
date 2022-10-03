package org.apache.xerces.util;

import org.w3c.dom.Node;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class DOMInputSource extends XMLInputSource
{
    private Node fNode;
    
    public DOMInputSource() {
        this((Node)null);
    }
    
    public DOMInputSource(final Node fNode) {
        super(null, getSystemIdFromNode(fNode), null);
        this.fNode = fNode;
    }
    
    public DOMInputSource(final Node fNode, final String s) {
        super(null, s, null);
        this.fNode = fNode;
    }
    
    public Node getNode() {
        return this.fNode;
    }
    
    public void setNode(final Node fNode) {
        this.fNode = fNode;
    }
    
    private static String getSystemIdFromNode(final Node node) {
        if (node != null) {
            try {
                return node.getBaseURI();
            }
            catch (final NoSuchMethodError noSuchMethodError) {
                return null;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        return null;
    }
}
