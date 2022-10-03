package org.apache.axiom.om.impl.llom;

import org.apache.axiom.core.CoreNamedNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.shared.INSAwareNamedNode;
import org.apache.axiom.shared.NSAwareNamedNodeSupport;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNSAwareAttributeSupport;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.om.impl.common.AxiomAttributeSupport;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.common.AxiomNamedInformationItemSupport;
import org.apache.axiom.core.CoreTypedAttribute;
import org.apache.axiom.core.CoreTypedAttributeSupport;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomAttribute;

public class OMAttributeImpl extends Attribute implements AxiomAttribute
{
    public String type;
    public OMNamespace namespace;
    public String localName;
    public QName qName;
    
    public OMAttributeImpl() {
        CoreTypedAttributeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$type(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(this);
    }
    
    public final String coreGetLocalName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetLocalName(this);
    }
    
    public final String coreGetNamespaceURI() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetNamespaceURI(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreNSAwareAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreNSAwareAttributeSupport$org_apache_axiom_core_CoreNSAwareAttribute$coreGetNodeType(this);
    }
    
    public final String coreGetPrefix() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetPrefix(this);
    }
    
    public final String coreGetType() {
        return CoreTypedAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$coreGetType(this);
    }
    
    public final void coreSetName(final String namespaceURI, final String localName, final String prefix) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetName(this, namespaceURI, localName, prefix);
    }
    
    public final void coreSetPrefix(final String prefix) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetPrefix(this, prefix);
    }
    
    public final void coreSetType(final String type) {
        CoreTypedAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$coreSetType(this, type);
    }
    
    public final OMNamespace defaultGetNamespace() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetNamespace(this);
    }
    
    public final QName defaultGetQName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetQName(this);
    }
    
    public final String getAttributeType() {
        return AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getAttributeType(this);
    }
    
    public final String getAttributeValue() {
        return AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getAttributeValue(this);
    }
    
    public final String getLocalName() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getLocalName(this);
    }
    
    public OMNamespace getNamespace() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace(this);
    }
    
    public final String getNamespaceURI() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getNamespaceURI(this);
    }
    
    public final OMElement getOwner() {
        return AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$getOwner(this);
    }
    
    public final String getPrefix() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getPrefix(this);
    }
    
    public QName getQName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getQName(this);
    }
    
    public final boolean hasName(final QName name) {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$hasName(this, name);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreTypedAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$init(this, policy, options, other);
    }
    
    public final void initName(final CoreNamedNode other) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$initName(this, other);
    }
    
    public final String internalGetLocalName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(this);
    }
    
    public final void internalSetLocalName(final String localName) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(this, localName);
    }
    
    public final void internalSetNamespace(final OMNamespace namespace) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(this, namespace);
    }
    
    public final void setAttributeType(final String type) {
        AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setAttributeType(this, type);
    }
    
    public final void setAttributeValue(final String value) {
        AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setAttributeValue(this, value);
    }
    
    public final void setLocalName(final String localName) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$setLocalName(this, localName);
    }
    
    public final void setNamespace(final OMNamespace namespace, final boolean decl) {
        AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setNamespace(this, namespace, decl);
    }
    
    public final void setOMNamespace(final OMNamespace omNamespace) {
        AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setOMNamespace(this, omNamespace);
    }
    
    public final void setSpecified(final boolean specified) {
        AxiomAttributeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomAttributeSupport$org_apache_axiom_om_impl_intf_AxiomAttribute$setSpecified(this, specified);
    }
    
    public void updateLocalName() {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$updateLocalName(this);
    }
}
