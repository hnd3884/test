package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface TreeWalker
{
    Node getRoot();
    
    int getWhatToShow();
    
    NodeFilter getFilter();
    
    boolean getExpandEntityReferences();
    
    Node getCurrentNode();
    
    void setCurrentNode(final Node p0) throws DOMException;
    
    Node parentNode();
    
    Node firstChild();
    
    Node lastChild();
    
    Node previousSibling();
    
    Node nextSibling();
    
    Node previousNode();
    
    Node nextNode();
}
