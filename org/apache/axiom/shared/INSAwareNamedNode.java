package org.apache.axiom.shared;

import org.apache.axiom.core.CoreNSAwareNamedNode;

public interface INSAwareNamedNode extends CoreNSAwareNamedNode
{
    String getLocalName();
    
    String getNamespaceURI();
    
    String getPrefix();
}
