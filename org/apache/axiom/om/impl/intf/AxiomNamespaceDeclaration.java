package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.core.CoreNamespaceDeclaration;

public interface AxiomNamespaceDeclaration extends AxiomInformationItem, CoreNamespaceDeclaration
{
    String coreGetDeclaredPrefix();
    
    void coreSetDeclaredNamespace(final String p0, final String p1);
    
    OMNamespace getDeclaredNamespace();
    
    void setDeclaredNamespace(final OMNamespace p0);
}
