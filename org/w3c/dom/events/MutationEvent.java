package org.w3c.dom.events;

import org.w3c.dom.Node;

public interface MutationEvent extends Event
{
    public static final short MODIFICATION = 1;
    public static final short ADDITION = 2;
    public static final short REMOVAL = 3;
    
    Node getRelatedNode();
    
    String getPrevValue();
    
    String getNewValue();
    
    String getAttrName();
    
    short getAttrChange();
    
    void initMutationEvent(final String p0, final boolean p1, final boolean p2, final Node p3, final String p4, final String p5, final String p6, final short p7);
}
