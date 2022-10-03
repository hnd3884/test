package org.w3c.dom.ranges;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface Range
{
    public static final short START_TO_START = 0;
    public static final short START_TO_END = 1;
    public static final short END_TO_END = 2;
    public static final short END_TO_START = 3;
    
    Node getStartContainer() throws DOMException;
    
    int getStartOffset() throws DOMException;
    
    Node getEndContainer() throws DOMException;
    
    int getEndOffset() throws DOMException;
    
    boolean getCollapsed() throws DOMException;
    
    Node getCommonAncestorContainer() throws DOMException;
    
    void setStart(final Node p0, final int p1) throws RangeException, DOMException;
    
    void setEnd(final Node p0, final int p1) throws RangeException, DOMException;
    
    void setStartBefore(final Node p0) throws RangeException, DOMException;
    
    void setStartAfter(final Node p0) throws RangeException, DOMException;
    
    void setEndBefore(final Node p0) throws RangeException, DOMException;
    
    void setEndAfter(final Node p0) throws RangeException, DOMException;
    
    void collapse(final boolean p0) throws DOMException;
    
    void selectNode(final Node p0) throws RangeException, DOMException;
    
    void selectNodeContents(final Node p0) throws RangeException, DOMException;
    
    short compareBoundaryPoints(final short p0, final Range p1) throws DOMException;
    
    void deleteContents() throws DOMException;
    
    DocumentFragment extractContents() throws DOMException;
    
    DocumentFragment cloneContents() throws DOMException;
    
    void insertNode(final Node p0) throws DOMException, RangeException;
    
    void surroundContents(final Node p0) throws DOMException, RangeException;
    
    Range cloneRange() throws DOMException;
    
    String toString() throws DOMException;
    
    void detach() throws DOMException;
}
