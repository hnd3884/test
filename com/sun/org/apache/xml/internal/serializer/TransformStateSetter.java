package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.Transformer;
import org.w3c.dom.Node;

public interface TransformStateSetter
{
    void setCurrentNode(final Node p0);
    
    void resetState(final Transformer p0);
}
