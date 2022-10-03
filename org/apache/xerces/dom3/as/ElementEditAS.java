package org.apache.xerces.dom3.as;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

public interface ElementEditAS extends NodeEditAS
{
    NodeList getDefinedElementTypes();
    
    short contentType();
    
    boolean canSetAttribute(final String p0, final String p1);
    
    boolean canSetAttributeNode(final Attr p0);
    
    boolean canSetAttributeNS(final String p0, final String p1, final String p2);
    
    boolean canRemoveAttribute(final String p0);
    
    boolean canRemoveAttributeNS(final String p0, final String p1);
    
    boolean canRemoveAttributeNode(final Node p0);
    
    NodeList getChildElements();
    
    NodeList getParentElements();
    
    NodeList getAttributeList();
    
    boolean isElementDefined(final String p0);
    
    boolean isElementDefinedNS(final String p0, final String p1, final String p2);
}
