package com.sun.org.apache.xalan.internal.xsltc.dom;

import javax.xml.transform.SourceLocator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.util.Map;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;

public class AdaptiveResultTreeImpl extends SimpleResultTreeImpl
{
    private static int _documentURIIndex;
    private static final String EMPTY_STRING;
    private SAXImpl _dom;
    private DTMWSFilter _wsfilter;
    private int _initSize;
    private boolean _buildIdIndex;
    private final AttributesImpl _attributes;
    private String _openElementName;
    
    public AdaptiveResultTreeImpl(final XSLTCDTMManager dtmManager, final int documentID, final DTMWSFilter wsfilter, final int initSize, final boolean buildIdIndex) {
        super(dtmManager, documentID);
        this._attributes = new AttributesImpl();
        this._wsfilter = wsfilter;
        this._initSize = initSize;
        this._buildIdIndex = buildIdIndex;
    }
    
    public DOM getNestedDOM() {
        return this._dom;
    }
    
    @Override
    public int getDocument() {
        if (this._dom != null) {
            return this._dom.getDocument();
        }
        return super.getDocument();
    }
    
    @Override
    public String getStringValue() {
        if (this._dom != null) {
            return this._dom.getStringValue();
        }
        return super.getStringValue();
    }
    
    @Override
    public DTMAxisIterator getIterator() {
        if (this._dom != null) {
            return this._dom.getIterator();
        }
        return super.getIterator();
    }
    
    @Override
    public DTMAxisIterator getChildren(final int node) {
        if (this._dom != null) {
            return this._dom.getChildren(node);
        }
        return super.getChildren(node);
    }
    
    @Override
    public DTMAxisIterator getTypedChildren(final int type) {
        if (this._dom != null) {
            return this._dom.getTypedChildren(type);
        }
        return super.getTypedChildren(type);
    }
    
    @Override
    public DTMAxisIterator getAxisIterator(final int axis) {
        if (this._dom != null) {
            return this._dom.getAxisIterator(axis);
        }
        return super.getAxisIterator(axis);
    }
    
    @Override
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
        if (this._dom != null) {
            return this._dom.getTypedAxisIterator(axis, type);
        }
        return super.getTypedAxisIterator(axis, type);
    }
    
    @Override
    public DTMAxisIterator getNthDescendant(final int node, final int n, final boolean includeself) {
        if (this._dom != null) {
            return this._dom.getNthDescendant(node, n, includeself);
        }
        return super.getNthDescendant(node, n, includeself);
    }
    
    @Override
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
        if (this._dom != null) {
            return this._dom.getNamespaceAxisIterator(axis, ns);
        }
        return super.getNamespaceAxisIterator(axis, ns);
    }
    
    @Override
    public DTMAxisIterator getNodeValueIterator(final DTMAxisIterator iter, final int returnType, final String value, final boolean op) {
        if (this._dom != null) {
            return this._dom.getNodeValueIterator(iter, returnType, value, op);
        }
        return super.getNodeValueIterator(iter, returnType, value, op);
    }
    
    @Override
    public DTMAxisIterator orderNodes(final DTMAxisIterator source, final int node) {
        if (this._dom != null) {
            return this._dom.orderNodes(source, node);
        }
        return super.orderNodes(source, node);
    }
    
    @Override
    public String getNodeName(final int node) {
        if (this._dom != null) {
            return this._dom.getNodeName(node);
        }
        return super.getNodeName(node);
    }
    
    @Override
    public String getNodeNameX(final int node) {
        if (this._dom != null) {
            return this._dom.getNodeNameX(node);
        }
        return super.getNodeNameX(node);
    }
    
    @Override
    public String getNamespaceName(final int node) {
        if (this._dom != null) {
            return this._dom.getNamespaceName(node);
        }
        return super.getNamespaceName(node);
    }
    
    @Override
    public int getExpandedTypeID(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getExpandedTypeID(nodeHandle);
        }
        return super.getExpandedTypeID(nodeHandle);
    }
    
    @Override
    public int getNamespaceType(final int node) {
        if (this._dom != null) {
            return this._dom.getNamespaceType(node);
        }
        return super.getNamespaceType(node);
    }
    
    @Override
    public int getParent(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getParent(nodeHandle);
        }
        return super.getParent(nodeHandle);
    }
    
    @Override
    public int getAttributeNode(final int gType, final int element) {
        if (this._dom != null) {
            return this._dom.getAttributeNode(gType, element);
        }
        return super.getAttributeNode(gType, element);
    }
    
    @Override
    public String getStringValueX(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValueX(nodeHandle);
        }
        return super.getStringValueX(nodeHandle);
    }
    
    @Override
    public void copy(final int node, final SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.copy(node, handler);
        }
        else {
            super.copy(node, handler);
        }
    }
    
    @Override
    public void copy(final DTMAxisIterator nodes, final SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.copy(nodes, handler);
        }
        else {
            super.copy(nodes, handler);
        }
    }
    
    @Override
    public String shallowCopy(final int node, final SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            return this._dom.shallowCopy(node, handler);
        }
        return super.shallowCopy(node, handler);
    }
    
    @Override
    public boolean lessThan(final int node1, final int node2) {
        if (this._dom != null) {
            return this._dom.lessThan(node1, node2);
        }
        return super.lessThan(node1, node2);
    }
    
    @Override
    public void characters(final int node, final SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.characters(node, handler);
        }
        else {
            super.characters(node, handler);
        }
    }
    
    @Override
    public Node makeNode(final int index) {
        if (this._dom != null) {
            return this._dom.makeNode(index);
        }
        return super.makeNode(index);
    }
    
    @Override
    public Node makeNode(final DTMAxisIterator iter) {
        if (this._dom != null) {
            return this._dom.makeNode(iter);
        }
        return super.makeNode(iter);
    }
    
    @Override
    public NodeList makeNodeList(final int index) {
        if (this._dom != null) {
            return this._dom.makeNodeList(index);
        }
        return super.makeNodeList(index);
    }
    
    @Override
    public NodeList makeNodeList(final DTMAxisIterator iter) {
        if (this._dom != null) {
            return this._dom.makeNodeList(iter);
        }
        return super.makeNodeList(iter);
    }
    
    @Override
    public String getLanguage(final int node) {
        if (this._dom != null) {
            return this._dom.getLanguage(node);
        }
        return super.getLanguage(node);
    }
    
    @Override
    public int getSize() {
        if (this._dom != null) {
            return this._dom.getSize();
        }
        return super.getSize();
    }
    
    @Override
    public String getDocumentURI(final int node) {
        if (this._dom != null) {
            return this._dom.getDocumentURI(node);
        }
        return "adaptive_rtf" + AdaptiveResultTreeImpl._documentURIIndex++;
    }
    
    @Override
    public void setFilter(final StripFilter filter) {
        if (this._dom != null) {
            this._dom.setFilter(filter);
        }
        else {
            super.setFilter(filter);
        }
    }
    
    @Override
    public void setupMapping(final String[] names, final String[] uris, final int[] types, final String[] namespaces) {
        if (this._dom != null) {
            this._dom.setupMapping(names, uris, types, namespaces);
        }
        else {
            super.setupMapping(names, uris, types, namespaces);
        }
    }
    
    @Override
    public boolean isElement(final int node) {
        if (this._dom != null) {
            return this._dom.isElement(node);
        }
        return super.isElement(node);
    }
    
    @Override
    public boolean isAttribute(final int node) {
        if (this._dom != null) {
            return this._dom.isAttribute(node);
        }
        return super.isAttribute(node);
    }
    
    @Override
    public String lookupNamespace(final int node, final String prefix) throws TransletException {
        if (this._dom != null) {
            return this._dom.lookupNamespace(node, prefix);
        }
        return super.lookupNamespace(node, prefix);
    }
    
    @Override
    public final int getNodeIdent(final int nodehandle) {
        if (this._dom != null) {
            return this._dom.getNodeIdent(nodehandle);
        }
        return super.getNodeIdent(nodehandle);
    }
    
    @Override
    public final int getNodeHandle(final int nodeId) {
        if (this._dom != null) {
            return this._dom.getNodeHandle(nodeId);
        }
        return super.getNodeHandle(nodeId);
    }
    
    @Override
    public DOM getResultTreeFrag(final int initialSize, final int rtfType) {
        if (this._dom != null) {
            return this._dom.getResultTreeFrag(initialSize, rtfType);
        }
        return super.getResultTreeFrag(initialSize, rtfType);
    }
    
    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this;
    }
    
    @Override
    public int getNSType(final int node) {
        if (this._dom != null) {
            return this._dom.getNSType(node);
        }
        return super.getNSType(node);
    }
    
    @Override
    public String getUnparsedEntityURI(final String name) {
        if (this._dom != null) {
            return this._dom.getUnparsedEntityURI(name);
        }
        return super.getUnparsedEntityURI(name);
    }
    
    @Override
    public Map<String, Integer> getElementsWithIDs() {
        if (this._dom != null) {
            return this._dom.getElementsWithIDs();
        }
        return super.getElementsWithIDs();
    }
    
    private void maybeEmitStartElement() throws SAXException {
        if (this._openElementName != null) {
            final int index;
            if ((index = this._openElementName.indexOf(58)) < 0) {
                this._dom.startElement(null, this._openElementName, this._openElementName, this._attributes);
            }
            else {
                final String uri = this._dom.getNamespaceURI(this._openElementName.substring(0, index));
                this._dom.startElement(uri, this._openElementName.substring(index + 1), this._openElementName, this._attributes);
            }
            this._openElementName = null;
        }
    }
    
    private void prepareNewDOM() throws SAXException {
        (this._dom = (SAXImpl)this._dtmManager.getDTM(null, true, this._wsfilter, true, false, false, this._initSize, this._buildIdIndex)).startDocument();
        for (int i = 0; i < this._size; ++i) {
            final String str = this._textArray[i];
            this._dom.characters(str.toCharArray(), 0, str.length());
        }
        this._size = 0;
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
        if (this._dom != null) {
            this._dom.endDocument();
        }
        else {
            super.endDocument();
        }
    }
    
    @Override
    public void characters(final String str) throws SAXException {
        if (this._dom != null) {
            this.characters(str.toCharArray(), 0, str.length());
        }
        else {
            super.characters(str);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int offset, final int length) throws SAXException {
        if (this._dom != null) {
            this.maybeEmitStartElement();
            this._dom.characters(ch, offset, length);
        }
        else {
            super.characters(ch, offset, length);
        }
    }
    
    @Override
    public boolean setEscaping(final boolean escape) throws SAXException {
        if (this._dom != null) {
            return this._dom.setEscaping(escape);
        }
        return super.setEscaping(escape);
    }
    
    @Override
    public void startElement(final String elementName) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._openElementName = elementName;
        this._attributes.clear();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName) throws SAXException {
        this.startElement(qName);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.startElement(qName);
    }
    
    @Override
    public void endElement(final String elementName) throws SAXException {
        this.maybeEmitStartElement();
        this._dom.endElement(null, null, elementName);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.endElement(qName);
    }
    
    @Override
    public void addAttribute(final String qName, final String value) {
        final int colonpos = qName.indexOf(58);
        String uri = AdaptiveResultTreeImpl.EMPTY_STRING;
        String localName = qName;
        if (colonpos > 0) {
            final String prefix = qName.substring(0, colonpos);
            localName = qName.substring(colonpos + 1);
            uri = this._dom.getNamespaceURI(prefix);
        }
        this.addAttribute(uri, localName, qName, "CDATA", value);
    }
    
    @Override
    public void addUniqueAttribute(final String qName, final String value, final int flags) throws SAXException {
        this.addAttribute(qName, value);
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String qname, final String type, final String value) {
        if (this._openElementName != null) {
            this._attributes.addAttribute(uri, localName, qname, type, value);
        }
        else {
            BasisLibrary.runTimeError("STRAY_ATTRIBUTE_ERR", qname);
        }
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this._dom.startPrefixMapping(prefix, uri);
    }
    
    @Override
    public void comment(final String comment) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        final char[] chars = comment.toCharArray();
        this._dom.comment(chars, 0, chars.length);
    }
    
    @Override
    public void comment(final char[] chars, final int offset, final int length) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._dom.comment(chars, offset, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._dom.processingInstruction(target, data);
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) {
        if (this._dom != null) {
            this._dom.setFeature(featureId, state);
        }
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
        if (this._dom != null) {
            this._dom.setProperty(property, value);
        }
    }
    
    @Override
    public DTMAxisTraverser getAxisTraverser(final int axis) {
        if (this._dom != null) {
            return this._dom.getAxisTraverser(axis);
        }
        return super.getAxisTraverser(axis);
    }
    
    @Override
    public boolean hasChildNodes(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.hasChildNodes(nodeHandle);
        }
        return super.hasChildNodes(nodeHandle);
    }
    
    @Override
    public int getFirstChild(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getFirstChild(nodeHandle);
        }
        return super.getFirstChild(nodeHandle);
    }
    
    @Override
    public int getLastChild(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLastChild(nodeHandle);
        }
        return super.getLastChild(nodeHandle);
    }
    
    @Override
    public int getAttributeNode(final int elementHandle, final String namespaceURI, final String name) {
        if (this._dom != null) {
            return this._dom.getAttributeNode(elementHandle, namespaceURI, name);
        }
        return super.getAttributeNode(elementHandle, namespaceURI, name);
    }
    
    @Override
    public int getFirstAttribute(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getFirstAttribute(nodeHandle);
        }
        return super.getFirstAttribute(nodeHandle);
    }
    
    @Override
    public int getFirstNamespaceNode(final int nodeHandle, final boolean inScope) {
        if (this._dom != null) {
            return this._dom.getFirstNamespaceNode(nodeHandle, inScope);
        }
        return super.getFirstNamespaceNode(nodeHandle, inScope);
    }
    
    @Override
    public int getNextSibling(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNextSibling(nodeHandle);
        }
        return super.getNextSibling(nodeHandle);
    }
    
    @Override
    public int getPreviousSibling(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getPreviousSibling(nodeHandle);
        }
        return super.getPreviousSibling(nodeHandle);
    }
    
    @Override
    public int getNextAttribute(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNextAttribute(nodeHandle);
        }
        return super.getNextAttribute(nodeHandle);
    }
    
    @Override
    public int getNextNamespaceNode(final int baseHandle, final int namespaceHandle, final boolean inScope) {
        if (this._dom != null) {
            return this._dom.getNextNamespaceNode(baseHandle, namespaceHandle, inScope);
        }
        return super.getNextNamespaceNode(baseHandle, namespaceHandle, inScope);
    }
    
    @Override
    public int getOwnerDocument(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getOwnerDocument(nodeHandle);
        }
        return super.getOwnerDocument(nodeHandle);
    }
    
    @Override
    public int getDocumentRoot(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentRoot(nodeHandle);
        }
        return super.getDocumentRoot(nodeHandle);
    }
    
    @Override
    public XMLString getStringValue(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValue(nodeHandle);
        }
        return super.getStringValue(nodeHandle);
    }
    
    @Override
    public int getStringValueChunkCount(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValueChunkCount(nodeHandle);
        }
        return super.getStringValueChunkCount(nodeHandle);
    }
    
    @Override
    public char[] getStringValueChunk(final int nodeHandle, final int chunkIndex, final int[] startAndLen) {
        if (this._dom != null) {
            return this._dom.getStringValueChunk(nodeHandle, chunkIndex, startAndLen);
        }
        return super.getStringValueChunk(nodeHandle, chunkIndex, startAndLen);
    }
    
    @Override
    public int getExpandedTypeID(final String namespace, final String localName, final int type) {
        if (this._dom != null) {
            return this._dom.getExpandedTypeID(namespace, localName, type);
        }
        return super.getExpandedTypeID(namespace, localName, type);
    }
    
    @Override
    public String getLocalNameFromExpandedNameID(final int ExpandedNameID) {
        if (this._dom != null) {
            return this._dom.getLocalNameFromExpandedNameID(ExpandedNameID);
        }
        return super.getLocalNameFromExpandedNameID(ExpandedNameID);
    }
    
    @Override
    public String getNamespaceFromExpandedNameID(final int ExpandedNameID) {
        if (this._dom != null) {
            return this._dom.getNamespaceFromExpandedNameID(ExpandedNameID);
        }
        return super.getNamespaceFromExpandedNameID(ExpandedNameID);
    }
    
    @Override
    public String getLocalName(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLocalName(nodeHandle);
        }
        return super.getLocalName(nodeHandle);
    }
    
    @Override
    public String getPrefix(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getPrefix(nodeHandle);
        }
        return super.getPrefix(nodeHandle);
    }
    
    @Override
    public String getNamespaceURI(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNamespaceURI(nodeHandle);
        }
        return super.getNamespaceURI(nodeHandle);
    }
    
    @Override
    public String getNodeValue(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNodeValue(nodeHandle);
        }
        return super.getNodeValue(nodeHandle);
    }
    
    @Override
    public short getNodeType(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNodeType(nodeHandle);
        }
        return super.getNodeType(nodeHandle);
    }
    
    @Override
    public short getLevel(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLevel(nodeHandle);
        }
        return super.getLevel(nodeHandle);
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        if (this._dom != null) {
            return this._dom.isSupported(feature, version);
        }
        return super.isSupported(feature, version);
    }
    
    @Override
    public String getDocumentBaseURI() {
        if (this._dom != null) {
            return this._dom.getDocumentBaseURI();
        }
        return super.getDocumentBaseURI();
    }
    
    @Override
    public void setDocumentBaseURI(final String baseURI) {
        if (this._dom != null) {
            this._dom.setDocumentBaseURI(baseURI);
        }
        else {
            super.setDocumentBaseURI(baseURI);
        }
    }
    
    @Override
    public String getDocumentSystemIdentifier(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentSystemIdentifier(nodeHandle);
        }
        return super.getDocumentSystemIdentifier(nodeHandle);
    }
    
    @Override
    public String getDocumentEncoding(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentEncoding(nodeHandle);
        }
        return super.getDocumentEncoding(nodeHandle);
    }
    
    @Override
    public String getDocumentStandalone(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentStandalone(nodeHandle);
        }
        return super.getDocumentStandalone(nodeHandle);
    }
    
    @Override
    public String getDocumentVersion(final int documentHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentVersion(documentHandle);
        }
        return super.getDocumentVersion(documentHandle);
    }
    
    @Override
    public boolean getDocumentAllDeclarationsProcessed() {
        if (this._dom != null) {
            return this._dom.getDocumentAllDeclarationsProcessed();
        }
        return super.getDocumentAllDeclarationsProcessed();
    }
    
    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        if (this._dom != null) {
            return this._dom.getDocumentTypeDeclarationSystemIdentifier();
        }
        return super.getDocumentTypeDeclarationSystemIdentifier();
    }
    
    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        if (this._dom != null) {
            return this._dom.getDocumentTypeDeclarationPublicIdentifier();
        }
        return super.getDocumentTypeDeclarationPublicIdentifier();
    }
    
    @Override
    public int getElementById(final String elementId) {
        if (this._dom != null) {
            return this._dom.getElementById(elementId);
        }
        return super.getElementById(elementId);
    }
    
    @Override
    public boolean supportsPreStripping() {
        if (this._dom != null) {
            return this._dom.supportsPreStripping();
        }
        return super.supportsPreStripping();
    }
    
    @Override
    public boolean isNodeAfter(final int firstNodeHandle, final int secondNodeHandle) {
        if (this._dom != null) {
            return this._dom.isNodeAfter(firstNodeHandle, secondNodeHandle);
        }
        return super.isNodeAfter(firstNodeHandle, secondNodeHandle);
    }
    
    @Override
    public boolean isCharacterElementContentWhitespace(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.isCharacterElementContentWhitespace(nodeHandle);
        }
        return super.isCharacterElementContentWhitespace(nodeHandle);
    }
    
    @Override
    public boolean isDocumentAllDeclarationsProcessed(final int documentHandle) {
        if (this._dom != null) {
            return this._dom.isDocumentAllDeclarationsProcessed(documentHandle);
        }
        return super.isDocumentAllDeclarationsProcessed(documentHandle);
    }
    
    @Override
    public boolean isAttributeSpecified(final int attributeHandle) {
        if (this._dom != null) {
            return this._dom.isAttributeSpecified(attributeHandle);
        }
        return super.isAttributeSpecified(attributeHandle);
    }
    
    @Override
    public void dispatchCharactersEvents(final int nodeHandle, final ContentHandler ch, final boolean normalize) throws SAXException {
        if (this._dom != null) {
            this._dom.dispatchCharactersEvents(nodeHandle, ch, normalize);
        }
        else {
            super.dispatchCharactersEvents(nodeHandle, ch, normalize);
        }
    }
    
    @Override
    public void dispatchToEvents(final int nodeHandle, final ContentHandler ch) throws SAXException {
        if (this._dom != null) {
            this._dom.dispatchToEvents(nodeHandle, ch);
        }
        else {
            super.dispatchToEvents(nodeHandle, ch);
        }
    }
    
    @Override
    public Node getNode(final int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNode(nodeHandle);
        }
        return super.getNode(nodeHandle);
    }
    
    @Override
    public boolean needsTwoThreads() {
        if (this._dom != null) {
            return this._dom.needsTwoThreads();
        }
        return super.needsTwoThreads();
    }
    
    @Override
    public ContentHandler getContentHandler() {
        if (this._dom != null) {
            return this._dom.getContentHandler();
        }
        return super.getContentHandler();
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        if (this._dom != null) {
            return this._dom.getLexicalHandler();
        }
        return super.getLexicalHandler();
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        if (this._dom != null) {
            return this._dom.getEntityResolver();
        }
        return super.getEntityResolver();
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        if (this._dom != null) {
            return this._dom.getDTDHandler();
        }
        return super.getDTDHandler();
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        if (this._dom != null) {
            return this._dom.getErrorHandler();
        }
        return super.getErrorHandler();
    }
    
    @Override
    public DeclHandler getDeclHandler() {
        if (this._dom != null) {
            return this._dom.getDeclHandler();
        }
        return super.getDeclHandler();
    }
    
    @Override
    public void appendChild(final int newChild, final boolean clone, final boolean cloneDepth) {
        if (this._dom != null) {
            this._dom.appendChild(newChild, clone, cloneDepth);
        }
        else {
            super.appendChild(newChild, clone, cloneDepth);
        }
    }
    
    @Override
    public void appendTextChild(final String str) {
        if (this._dom != null) {
            this._dom.appendTextChild(str);
        }
        else {
            super.appendTextChild(str);
        }
    }
    
    @Override
    public SourceLocator getSourceLocatorFor(final int node) {
        if (this._dom != null) {
            return this._dom.getSourceLocatorFor(node);
        }
        return super.getSourceLocatorFor(node);
    }
    
    @Override
    public void documentRegistration() {
        if (this._dom != null) {
            this._dom.documentRegistration();
        }
        else {
            super.documentRegistration();
        }
    }
    
    @Override
    public void documentRelease() {
        if (this._dom != null) {
            this._dom.documentRelease();
        }
        else {
            super.documentRelease();
        }
    }
    
    @Override
    public void release() {
        if (this._dom != null) {
            this._dom.release();
            this._dom = null;
        }
        super.release();
    }
    
    static {
        AdaptiveResultTreeImpl._documentURIIndex = 0;
        EMPTY_STRING = "".intern();
    }
}
