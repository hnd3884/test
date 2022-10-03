package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;

public interface OMElementEx extends OMElement, OMNodeEx, OMContainerEx
{
    OMNamespace addNamespaceDeclaration(final String p0, final String p1);
    
    void detachAndDiscardParent();
}
