package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.common.AxiomChildNodeSupport;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.NodeFilter;
import org.apache.axiom.om.OMException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.CoreChildNodeSupport;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.impl.intf.AxiomChildNode;

public abstract class OMNodeImpl extends OMSerializableImpl implements AxiomChildNode
{
    public CoreParentNode owner;
    public CoreChildNode nextSibling;
    public CoreChildNode previousSibling;
    
    public OMNodeImpl() {
        CoreChildNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$owner(this);
        CoreChildNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$nextSibling(this);
        CoreChildNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$previousSibling(this);
    }
    
    public final <T> CoreNode coreClone(final ClonePolicy<T> policy, final T options, final CoreParentNode targetParent) {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreClone(this, policy, options, targetParent);
    }
    
    public final void coreDetach(final Semantics semantics) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreDetach(this, semantics);
    }
    
    public final CoreChildNode coreGetNextSibling() throws OMException {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(this);
    }
    
    public final CoreChildNode coreGetNextSibling(final NodeFilter filter) {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSibling(this, filter);
    }
    
    public final CoreChildNode coreGetNextSiblingIfAvailable() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetNextSiblingIfAvailable(this);
    }
    
    public final CoreParentNode coreGetParent() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParent(this);
    }
    
    public final CoreElement coreGetParentElement() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetParentElement(this);
    }
    
    public final CoreChildNode coreGetPreviousSibling() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetPreviousSibling(this);
    }
    
    public final CoreChildNode coreGetPreviousSibling(final NodeFilter filter) {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreGetPreviousSibling(this, filter);
    }
    
    public final boolean coreHasParent() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreHasParent(this);
    }
    
    public final void coreInsertSiblingAfter(final CoreChildNode sibling) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingAfter(this, sibling);
    }
    
    public final void coreInsertSiblingBefore(final CoreChildNode sibling) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingBefore(this, sibling);
    }
    
    public final void coreInsertSiblingsBefore(final CoreDocumentFragment fragment) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreInsertSiblingsBefore(this, fragment);
    }
    
    public final void coreReplaceWith(final CoreChildNode newNode, final Semantics semantics) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreReplaceWith(this, newNode, semantics);
    }
    
    public final void coreSetNextSibling(final CoreChildNode nextSibling) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetNextSibling(this, nextSibling);
    }
    
    public final void coreSetOwnerDocument(final CoreDocument document) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetOwnerDocument(this, document);
    }
    
    public final void coreSetPreviousSibling(final CoreChildNode previousSibling) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$coreSetPreviousSibling(this, previousSibling);
    }
    
    public OMNode detach() {
        return AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$detach(this);
    }
    
    public final OMNode getNextOMSibling() {
        return AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getNextOMSibling(this);
    }
    
    public final OMContainer getParent() {
        return AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getParent(this);
    }
    
    public final OMNode getPreviousOMSibling() {
        return AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$getPreviousOMSibling(this);
    }
    
    public final CoreNode getRootOrOwnerDocument() {
        return CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$getRootOrOwnerDocument(this);
    }
    
    public final void insertSiblingAfter(final OMNode sibling) throws OMException {
        AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$insertSiblingAfter(this, sibling);
    }
    
    public final void insertSiblingBefore(final OMNode sibling) throws OMException {
        AxiomChildNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomChildNodeSupport$org_apache_axiom_om_impl_intf_AxiomChildNode$insertSiblingBefore(this, sibling);
    }
    
    public void internalSetParent(final CoreParentNode parent) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalSetParent(this, parent);
    }
    
    public final void internalUnsetParent(final CoreDocument newOwnerDocument) {
        CoreChildNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreChildNodeSupport$org_apache_axiom_core_CoreChildNode$internalUnsetParent(this, newOwnerDocument);
    }
}
