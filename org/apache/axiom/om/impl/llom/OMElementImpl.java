package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import org.apache.axiom.om.impl.common.AxiomCoreParentNodeSupport;
import org.apache.axiom.core.CoreNamedNode;
import java.io.Reader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.shared.INSAwareNamedNode;
import org.apache.axiom.shared.NSAwareNamedNodeSupport;
import org.apache.axiom.om.OMException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNSAwareElementSupport;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.NodeFilter;
import org.apache.axiom.core.NodeIterator;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreMixedContentContainer;
import org.apache.axiom.core.CoreMixedContentContainerSupport;
import java.util.Iterator;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreParentNodeSupport;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.common.AxiomNamedInformationItemSupport;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.DeferringParentNodeSupport;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreElementSupport;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.core.CoreAttribute;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomElement;

public class OMElementImpl extends OMNodeImpl implements AxiomElement
{
    public OMNamespace namespace;
    public String localName;
    public QName qName;
    public int lineNumber;
    public CoreAttribute firstAttribute;
    public OMXMLParserWrapper builder;
    public Object content;
    
    public OMElementImpl() {
        AxiomElementSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$lineNumber(this);
        CoreElementSupport.ajc$interFieldInit$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$firstAttribute(this);
        DeferringParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$builder(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$namespace(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$localName(this);
        AxiomNamedInformationItemSupport.ajc$interFieldInit$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$qName(this);
        CoreParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(this);
    }
    
    public final void _setAttributeValue(final QName qname, final String value) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$_setAttributeValue(this, qname, value);
    }
    
    public final OMAttribute addAttribute(final String localName, final String value, final OMNamespace ns) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addAttribute(this, localName, value, ns);
    }
    
    public final OMAttribute addAttribute(final OMAttribute attr) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addAttribute(this, attr);
    }
    
    public void addChild(final OMNode omNode) {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, omNode);
    }
    
    public void addChild(final OMNode omNode, final boolean fromBuilder) {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, omNode, fromBuilder);
    }
    
    public final OMNamespace addNamespaceDeclaration(final String uri, final String prefix) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration(this, uri, prefix);
    }
    
    public final void addNamespaceDeclaration(final OMNamespace ns) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$addNamespaceDeclaration(this, ns);
    }
    
    public final void build() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$build(this);
    }
    
    public void buildNext() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(this);
    }
    
    public final void buildWithAttachments() {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$buildWithAttachments(this);
    }
    
    public void checkChild(final OMNode child) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$checkChild(this, child);
    }
    
    public final <T> void cloneChildrenIfNecessary(final ClonePolicy<T> policy, final T options, final CoreNode clone) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$cloneChildrenIfNecessary(this, policy, options, clone);
    }
    
    public final OMElement cloneOMElement() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$cloneOMElement(this);
    }
    
    public final void coreAppendAttribute(final CoreAttribute attr) {
        CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreAppendAttribute(this, attr);
    }
    
    public final void coreAppendChild(final CoreChildNode child, final boolean fromBuilder) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChild(this, child, fromBuilder);
    }
    
    public final void coreAppendChildren(final CoreDocumentFragment fragment) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreAppendChildren(this, fragment);
    }
    
    public final CoreAttribute coreGetAttribute(final AttributeMatcher matcher, final String namespaceURI, final String name) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetAttribute(this, matcher, namespaceURI, name);
    }
    
    public final <T extends CoreAttribute, S> Iterator<S> coreGetAttributesByType(final Class<T> type, final Mapper<T, S> mapper, final Semantics semantics) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetAttributesByType(this, type, mapper, semantics);
    }
    
    public final Object coreGetCharacterData(final ElementAction elementAction) {
        return CoreMixedContentContainerSupport.ajc$interMethod$org_apache_axiom_core_CoreMixedContentContainerSupport$org_apache_axiom_core_CoreMixedContentContainer$coreGetCharacterData(this, elementAction);
    }
    
    public final <T extends CoreElement> NodeIterator<T> coreGetElements(final Axis axis, final Class<T> type, final ElementMatcher<? super T> matcher, final String namespaceURI, final String name, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetElements(this, axis, type, matcher, namespaceURI, name, semantics);
    }
    
    public final CoreAttribute coreGetFirstAttribute() {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetFirstAttribute(this);
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
    
    public final CoreAttribute coreGetLastAttribute() {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreGetLastAttribute(this);
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
    
    public final String coreGetLocalName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetLocalName(this);
    }
    
    public final String coreGetNamespaceURI() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetNamespaceURI(this);
    }
    
    public final NodeType coreGetNodeType() {
        return CoreNSAwareElementSupport.ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$coreGetNodeType(this);
    }
    
    public final <T> NodeIterator<T> coreGetNodes(final Axis axis, final Class<T> type, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetNodes(this, axis, type, semantics);
    }
    
    public final String coreGetPrefix() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreGetPrefix(this);
    }
    
    public final String coreLookupNamespaceURI(final String prefix, final Semantics semantics) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreLookupNamespaceURI(this, prefix, semantics);
    }
    
    public final String coreLookupPrefix(final String namespaceURI, final Semantics semantics) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreLookupPrefix(this, namespaceURI, semantics);
    }
    
    public final boolean coreRemoveAttribute(final AttributeMatcher matcher, final String namespaceURI, final String name, final Semantics semantics) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreRemoveAttribute(this, matcher, namespaceURI, name, semantics);
    }
    
    public final void coreRemoveChildren(final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreRemoveChildren(this, semantics);
    }
    
    public final void coreSetAttribute(final AttributeMatcher matcher, final String namespaceURI, final String name, final String prefix, final String value) {
        CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreSetAttribute(this, matcher, namespaceURI, name, prefix, value);
    }
    
    public final CoreAttribute coreSetAttribute(final AttributeMatcher matcher, final CoreAttribute attr, final Semantics semantics) {
        return CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$coreSetAttribute(this, matcher, attr, semantics);
    }
    
    public final void coreSetBuilder(final OMXMLParserWrapper builder) {
        DeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$coreSetBuilder(this, builder);
    }
    
    public final void coreSetCharacterData(final Object data, final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(this, data, semantics);
    }
    
    public final void coreSetName(final String namespaceURI, final String localName, final String prefix) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetName(this, namespaceURI, localName, prefix);
    }
    
    public final void coreSetPrefix(final String prefix) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$coreSetPrefix(this, prefix);
    }
    
    public final void coreSetState(final int state) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(this, state);
    }
    
    public final OMNamespace declareDefaultNamespace(final String uri) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareDefaultNamespace(this, uri);
    }
    
    public final OMNamespace declareNamespace(final String uri, final String prefix) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareNamespace(this, uri, prefix);
    }
    
    public final OMNamespace declareNamespace(final OMNamespace namespace) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$declareNamespace(this, namespace);
    }
    
    public final OMNamespace defaultGetNamespace() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetNamespace(this);
    }
    
    public final QName defaultGetQName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$defaultGetQName(this);
    }
    
    public final XMLStreamReader defaultGetXMLStreamReader(final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$defaultGetXMLStreamReader(this, cache, configuration);
    }
    
    public final void defaultInternalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$defaultInternalSerialize(this, serializer, format, cache);
    }
    
    public void detachAndDiscardParent() {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$detachAndDiscardParent(this);
    }
    
    public final void discard() {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$discard(this);
    }
    
    public final void discarded() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$discarded(this);
    }
    
    public final OMNamespace findNamespace(final String uri, final String prefix) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespace(this, uri, prefix);
    }
    
    public final OMNamespace findNamespaceURI(final String prefix) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$findNamespaceURI(this, prefix);
    }
    
    public void forceExpand() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(this);
    }
    
    public final Iterator getAllAttributes() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAllAttributes(this);
    }
    
    public final Iterator getAllDeclaredNamespaces() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAllDeclaredNamespaces(this);
    }
    
    public final OMAttribute getAttribute(final QName qname) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAttribute(this, qname);
    }
    
    public String getAttributeValue(final QName qname) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getAttributeValue(this, qname);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return DeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(this);
    }
    
    public final Iterator getChildElements() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getChildElements(this);
    }
    
    public Iterator getChildren() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildren(this);
    }
    
    public Iterator getChildrenWithLocalName(final String localName) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithLocalName(this, localName);
    }
    
    public Iterator getChildrenWithName(final QName elementQName) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithName(this, elementQName);
    }
    
    public Iterator getChildrenWithNamespaceURI(final String uri) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getChildrenWithNamespaceURI(this, uri);
    }
    
    public final OMNamespace getDefaultNamespace() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getDefaultNamespace(this);
    }
    
    public Iterator getDescendants(final boolean includeSelf) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getDescendants(this, includeSelf);
    }
    
    public OMElement getFirstChildWithName(final QName elementQName) throws OMException {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(this, elementQName);
    }
    
    public final OMElement getFirstElement() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
    }
    
    public OMNode getFirstOMChild() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(this);
    }
    
    public final String getImplicitNamespaceURI(final String prefix) {
        return CoreNSAwareElementSupport.ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$getImplicitNamespaceURI(this, prefix);
    }
    
    public final String getImplicitPrefix(final String namespaceURI) {
        return CoreNSAwareElementSupport.ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$getImplicitPrefix(this, namespaceURI);
    }
    
    public final int getLineNumber() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getLineNumber(this);
    }
    
    public final String getLocalName() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getLocalName(this);
    }
    
    public OMNamespace getNamespace() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getNamespace(this);
    }
    
    public NamespaceContext getNamespaceContext(final boolean detached) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getNamespaceContext(this, detached);
    }
    
    public final String getNamespaceURI() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getNamespaceURI(this);
    }
    
    public final Iterator getNamespacesInScope() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getNamespacesInScope(this);
    }
    
    public final String getPrefix() {
        return NSAwareNamedNodeSupport.ajc$interMethod$org_apache_axiom_shared_NSAwareNamedNodeSupport$org_apache_axiom_shared_INSAwareNamedNode$getPrefix(this);
    }
    
    public QName getQName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$getQName(this);
    }
    
    public final SAXResult getSAXResult() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getSAXResult(this);
    }
    
    public final SAXSource getSAXSource(final boolean cache) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getSAXSource(this, cache);
    }
    
    public int getState() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(this);
    }
    
    public String getText() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getText(this);
    }
    
    public QName getTextAsQName() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getTextAsQName(this);
    }
    
    public Reader getTextAsStream(final boolean cache) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getTextAsStream(this, cache);
    }
    
    public final int getType() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getType(this);
    }
    
    public final XMLStreamReader getXMLStreamReader() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(this);
    }
    
    public final XMLStreamReader getXMLStreamReader(final boolean cache) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(this, cache);
    }
    
    public XMLStreamReader getXMLStreamReader(final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReader(this, cache, configuration);
    }
    
    public final XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getXMLStreamReaderWithoutCaching(this);
    }
    
    public final OMNamespace handleNamespace(final String namespaceURI, final String prefix) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$handleNamespace(this, namespaceURI, prefix);
    }
    
    public final boolean hasName(final QName name) {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$hasName(this, name);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$init(this, policy, options, other);
    }
    
    public final void initName(final String localName, final OMNamespace ns, final boolean generateNSDecl) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$initName(this, localName, ns, generateNSDecl);
    }
    
    public final void initName(final CoreNamedNode other) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$initName(this, other);
    }
    
    public <T> void initSource(final ClonePolicy<T> policy, final T options, final CoreElement other) {
        CoreElementSupport.ajc$interMethod$org_apache_axiom_core_CoreElementSupport$org_apache_axiom_core_CoreElement$initSource(this, policy, options, other);
    }
    
    public void insertChild(final Class[] sequence, final int pos, final OMNode newChild) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(this, sequence, pos, newChild);
    }
    
    public final void internalAppendAttribute(final OMAttribute attr) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalAppendAttribute(this, attr);
    }
    
    public final String internalGetLocalName() {
        return AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalGetLocalName(this);
    }
    
    public void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalSerialize(this, serializer, format, cache);
    }
    
    public final void internalSetLocalName(final String localName) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetLocalName(this, localName);
    }
    
    public final void internalSetNamespace(final OMNamespace namespace) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$internalSetNamespace(this, namespace);
    }
    
    public final boolean isComplete() {
        return AxiomCoreParentNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(this);
    }
    
    public boolean isExpanded() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(this);
    }
    
    public final void notifyChildComplete() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$notifyChildComplete(this);
    }
    
    public final AxiomChildNode prepareNewChild(final OMNode omNode) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$prepareNewChild(this, omNode);
    }
    
    public final void removeAttribute(final OMAttribute attr) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$removeAttribute(this, attr);
    }
    
    public void removeChildren() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$removeChildren(this);
    }
    
    public final QName resolveQName(final String qname) {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$resolveQName(this, qname);
    }
    
    public final void serialize(final OutputStream output) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(this, output);
    }
    
    public final void serialize(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(this, output, format);
    }
    
    public final void serialize(final Writer writer) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(this, writer);
    }
    
    public final void serialize(final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serialize(this, writer2, format);
    }
    
    public final void serializeAndConsume(final OutputStream output) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(this, output);
    }
    
    public final void serializeAndConsume(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(this, output, format);
    }
    
    public final void serializeAndConsume(final Writer writer) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(this, writer);
    }
    
    public final void serializeAndConsume(final Writer writer2, final OMOutputFormat format) throws XMLStreamException {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$serializeAndConsume(this, writer2, format);
    }
    
    public void setComplete(final boolean complete) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setComplete(this, complete);
    }
    
    public final void setLineNumber(final int lineNumber) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setLineNumber(this, lineNumber);
    }
    
    public final void setLocalName(final String localName) {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$setLocalName(this, localName);
    }
    
    public final void setNamespace(final OMNamespace namespace) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespace(this, namespace);
    }
    
    public final void setNamespace(final OMNamespace namespace, final boolean decl) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespace(this, namespace, decl);
    }
    
    public final void setNamespaceWithNoFindInCurrentScope(final OMNamespace namespace) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setNamespaceWithNoFindInCurrentScope(this, namespace);
    }
    
    public void setText(final String text) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(this, text);
    }
    
    public final void setText(final QName qname) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(this, qname);
    }
    
    @Override
    public final String toString() {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$toString(this);
    }
    
    public final String toStringWithConsume() throws XMLStreamException {
        return AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$toStringWithConsume(this);
    }
    
    public final void undeclarePrefix(final String prefix) {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$undeclarePrefix(this, prefix);
    }
    
    public void updateLocalName() {
        AxiomNamedInformationItemSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomNamedInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomNamedInformationItem$updateLocalName(this);
    }
    
    public void writeTextTo(final Writer out, final boolean cache) throws IOException {
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$writeTextTo(this, out, cache);
    }
}
