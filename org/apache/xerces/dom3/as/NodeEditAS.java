package org.apache.xerces.dom3.as;

import org.w3c.dom.Node;

public interface NodeEditAS
{
    public static final short WF_CHECK = 1;
    public static final short NS_WF_CHECK = 2;
    public static final short PARTIAL_VALIDITY_CHECK = 3;
    public static final short STRICT_VALIDITY_CHECK = 4;
    
    boolean canInsertBefore(final Node p0, final Node p1);
    
    boolean canRemoveChild(final Node p0);
    
    boolean canReplaceChild(final Node p0, final Node p1);
    
    boolean canAppendChild(final Node p0);
    
    boolean isNodeValid(final boolean p0, final short p1) throws DOMASException;
}
