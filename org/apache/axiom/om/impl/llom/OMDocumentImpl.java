package org.apache.axiom.om.impl.llom;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import org.apache.axiom.om.impl.common.AxiomCoreParentNodeSupport;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.axiom.om.OMException;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.NodeFilter;
import org.apache.axiom.core.NodeIterator;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.common.AxiomDocumentSupport;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreParentNodeSupport;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreDocumentSupport;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.DeferringParentNodeSupport;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.intf.AxiomDocument;

public class OMDocumentImpl extends OMSerializableImpl implements AxiomDocument
{
    public OMXMLParserWrapper builder;
    public Object content;
    public String inputEncoding;
    public String xmlVersion;
    public String xmlEncoding;
    public boolean standalone;
    
    public OMDocumentImpl() {
        DeferringParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$builder(this);
        CoreDocumentSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$inputEncoding(this);
        CoreDocumentSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlVersion(this);
        CoreDocumentSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$xmlEncoding(this);
        CoreDocumentSupport.ajc$interFieldInit$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$standalone(this);
        CoreParentNodeSupport.ajc$interFieldInit$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$content(this);
    }
    
    public void addChild(final OMNode omNode) {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, omNode);
    }
    
    public void addChild(final OMNode omNode, final boolean fromBuilder) {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, omNode, fromBuilder);
    }
    
    public final void build() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$build(this);
    }
    
    public void buildNext() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$buildNext(this);
    }
    
    public final void checkChild(final OMNode child) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$checkChild(this, child);
    }
    
    public void checkDocumentElement(final OMElement element) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$checkDocumentElement(this, element);
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
    
    public final CoreElement coreGetDocumentElement() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetDocumentElement(this);
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
    
    public final String coreGetInputEncoding() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetInputEncoding(this);
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
    
    public final NodeType coreGetNodeType() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetNodeType(this);
    }
    
    public final <T> NodeIterator<T> coreGetNodes(final Axis axis, final Class<T> type, final Semantics semantics) {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetNodes(this, axis, type, semantics);
    }
    
    public final String coreGetXmlEncoding() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetXmlEncoding(this);
    }
    
    public final String coreGetXmlVersion() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreGetXmlVersion(this);
    }
    
    public final boolean coreIsStandalone() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreIsStandalone(this);
    }
    
    public final void coreRemoveChildren(final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreRemoveChildren(this, semantics);
    }
    
    public final void coreSetBuilder(final OMXMLParserWrapper builder) {
        DeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$coreSetBuilder(this, builder);
    }
    
    public final void coreSetCharacterData(final Object data, final Semantics semantics) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetCharacterData(this, data, semantics);
    }
    
    public final void coreSetInputEncoding(final String inputEncoding) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetInputEncoding(this, inputEncoding);
    }
    
    public final void coreSetOwnerDocument(final CoreDocument document) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetOwnerDocument(this, document);
    }
    
    public final void coreSetStandalone(final boolean standalone) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetStandalone(this, standalone);
    }
    
    public final void coreSetState(final int state) {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(this, state);
    }
    
    public final void coreSetXmlEncoding(final String xmlEncoding) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetXmlEncoding(this, xmlEncoding);
    }
    
    public final void coreSetXmlVersion(final String xmlVersion) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$coreSetXmlVersion(this, xmlVersion);
    }
    
    public final XMLStreamReader defaultGetXMLStreamReader(final boolean cache, final OMXMLStreamReaderConfiguration configuration) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$defaultGetXMLStreamReader(this, cache, configuration);
    }
    
    public final void discarded() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$discarded(this);
    }
    
    public void forceExpand() {
        CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(this);
    }
    
    public final OMXMLParserWrapper getBuilder() {
        return DeferringParentNodeSupport.ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(this);
    }
    
    public final String getCharsetEncoding() {
        return AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getCharsetEncoding(this);
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
    
    public Iterator getDescendants(final boolean includeSelf) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getDescendants(this, includeSelf);
    }
    
    public OMElement getFirstChildWithName(final QName elementQName) throws OMException {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(this, elementQName);
    }
    
    public OMNode getFirstOMChild() {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstOMChild(this);
    }
    
    public final OMElement getOMDocumentElement() {
        return AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getOMDocumentElement(this);
    }
    
    public final CoreNode getRootOrOwnerDocument() {
        return CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$getRootOrOwnerDocument(this);
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
    
    public final String getXMLEncoding() {
        return AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getXMLEncoding(this);
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
    
    public final String getXMLVersion() {
        return AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getXMLVersion(this);
    }
    
    public final <T> void init(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        CoreDocumentSupport.ajc$interMethod$org_apache_axiom_core_CoreDocumentSupport$org_apache_axiom_core_CoreDocument$init(this, policy, options, other);
    }
    
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$internalSerialize(this, serializer, format, cache);
    }
    
    public void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache, final boolean includeXMLDeclaration) throws OutputException {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$internalSerialize(this, serializer, format, cache, includeXMLDeclaration);
    }
    
    public final boolean isComplete() {
        return AxiomCoreParentNodeSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(this);
    }
    
    public boolean isExpanded() {
        return CoreParentNodeSupport.ajc$interMethod$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$isExpanded(this);
    }
    
    public final String isStandalone() {
        return AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$isStandalone(this);
    }
    
    public final void notifyChildComplete() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$notifyChildComplete(this);
    }
    
    public final AxiomChildNode prepareNewChild(final OMNode omNode) {
        return AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$prepareNewChild(this, omNode);
    }
    
    public void removeChildren() {
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$removeChildren(this);
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
    
    public final void setCharsetEncoding(final String charsetEncoding) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setCharsetEncoding(this, charsetEncoding);
    }
    
    public final void setComplete(final boolean complete) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setComplete(this, complete);
    }
    
    public final void setOMDocumentElement(final OMElement documentElement) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setOMDocumentElement(this, documentElement);
    }
    
    public final void setStandalone(final String standalone) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setStandalone(this, standalone);
    }
    
    public final void setXMLEncoding(final String xmlEncoding) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setXMLEncoding(this, xmlEncoding);
    }
    
    public final void setXMLVersion(final String xmlVersion) {
        AxiomDocumentSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setXMLVersion(this, xmlVersion);
    }
}
