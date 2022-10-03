package org.apache.axiom.om.impl.intf;

import org.apache.axiom.core.CoreNamedNode;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.shared.INSAwareNamedNode;
import org.apache.axiom.om.OMNamedInformationItem;

public interface AxiomNamedInformationItem extends OMNamedInformationItem, AxiomInformationItem, INSAwareNamedNode
{
    String coreGetLocalName();
    
    String coreGetNamespaceURI();
    
    String coreGetPrefix();
    
    void coreSetName(final String p0, final String p1, final String p2);
    
    void coreSetPrefix(final String p0);
    
    OMNamespace defaultGetNamespace();
    
    QName defaultGetQName();
    
    OMNamespace getNamespace();
    
    QName getQName();
    
    boolean hasName(final QName p0);
    
    void initName(final CoreNamedNode p0);
    
    String internalGetLocalName();
    
    void internalSetLocalName(final String p0);
    
    void internalSetNamespace(final OMNamespace p0);
    
    void setLocalName(final String p0);
    
    void updateLocalName();
}
