package org.apache.axiom.om.impl.llom;

import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.NodeFilter;
import org.apache.axiom.core.NodeIterator;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreCharacterDataContainingParentNode;
import org.apache.axiom.core.CoreCharacterDataContainingParentNodeSupport;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.NonDeferringParentNode;
import org.apache.axiom.core.NonDeferringParentNodeSupport;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.core.CoreParentNodeSupport;
import org.apache.axiom.core.CoreAttributeSupport;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreAttribute;

public abstract class Attribute extends OMInformationItemImpl implements CoreAttribute
{
    public Object content;
    public CoreParentNode owner;
    public CoreAttribute nextAttribute;
    
    public Attribute() {
        CoreAttributeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$owner(this);
        CoreAttributeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$nextAttribute(this);
        CoreParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(this);
    }
    
    public final void build() {
        NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$build(this);
    }
    
    public void buildNext() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(this);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final void coreAppendChild(final CoreChildNode child, final boolean fromBuilder) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChild(this, child, fromBuilder);
    }
    
    public final void coreAppendChildren(final CoreDocumentFragment fragment) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChildren(this, fragment);
    }
    
    public final Object coreGetCharacterData() {
        return CoreCharacterDataContainingParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport$org_apache_axiom_core_CoreCharacterDataContainingParentNode$coreGetCharacterData(this);
    }
    
    public final <T extends CoreElement> NodeIterator<T> coreGetElements(final Axis axis, final Class<T> type, final ElementMatcher<? super T> matcher, final String namespaceURI, final String name, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetElements(this, axis, type, matcher, namespaceURI, name, semantics);
    }
    
    public CoreChildNode coreGetFirstChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this);
    }
    
    public final CoreChildNode coreGetFirstChild(final NodeFilter filter) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChild(this, filter);
    }
    
    public final CoreChildNode coreGetFirstChildIfAvailable() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetFirstChildIfAvailable(this);
    }
    
    public final CoreChildNode coreGetLastChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(this);
    }
    
    public final CoreChildNode coreGetLastChild(final NodeFilter filter) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastChild(this, filter);
    }
    
    public CoreChildNode coreGetLastKnownChild() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastKnownChild(this);
    }
    
    public final CoreAttribute coreGetNextAttribute() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetNextAttribute(this);
    }
    
    public final <T> NodeIterator<T> coreGetNodes(final Axis axis, final Class<T> type, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetNodes(this, axis, type, semantics);
    }
    
    public final CoreElement coreGetOwnerElement() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetOwnerElement(this);
    }
    
    public final CoreAttribute coreGetPreviousAttribute() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetPreviousAttribute(this);
    }
    
    public final boolean coreGetSpecified() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreGetSpecified(this);
    }
    
    public final boolean coreHasOwnerElement() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreHasOwnerElement(this);
    }
    
    public final boolean coreRemove(final Semantics semantics) {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreRemove(this, semantics);
    }
    
    public final void coreRemoveChildren(final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreRemoveChildren(this, semantics);
    }
    
    public final void coreSetBuilder(final OMXMLParserWrapper builder) {
        NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$coreSetBuilder(this, builder);
    }
    
    public final void coreSetCharacterData(final Object data, final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(this, data, semantics);
    }
    
    public final void coreSetOwnerDocument(final CoreDocument document) {
        CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreSetOwnerDocument(this, document);
    }
    
    public final void coreSetSpecified(final boolean specified) {
        CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$coreSetSpecified(this, specified);
    }
    
    public final void coreSetState(final int state) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(this, state);
    }
    
    public void forceExpand() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(this);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return NonDeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_NonDeferringParentNodeSupport$org_apache_axiom_core_NonDeferringParentNode$getBuilder(this);
    }
    
    public final CoreNode getRootOrOwnerDocument() {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$getRootOrOwnerDocument(this);
    }
    
    public int getState() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(this);
    }
    
    public final boolean internalRemove(final Semantics semantics, final CoreElement newOwner) {
        return CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalRemove(this, semantics, newOwner);
    }
    
    public final void internalSetNextAttribute(final CoreAttribute nextAttribute) {
        CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetNextAttribute(this, nextAttribute);
    }
    
    public final void internalSetOwnerElement(final CoreElement element) {
        CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalSetOwnerElement(this, element);
    }
    
    public final void internalUnsetOwnerElement(final CoreDocument newOwnerDocument) {
        CoreAttributeSupport.ajc$interMethod$org_apache_axiom_core_CoreAttributeSupport$org_apache_axiom_core_CoreAttribute$internalUnsetOwnerElement(this, newOwnerDocument);
    }
    
    public boolean isExpanded() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(this);
    }
}
