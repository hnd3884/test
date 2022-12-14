package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.dom.ElementDefinitionImpl;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import org.w3c.dom.DOMError;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import org.w3c.dom.Text;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.EntityReference;
import com.sun.org.apache.xerces.internal.dom.EntityReferenceImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.w3c.dom.ls.LSParserFilter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;

public class AbstractDOMParser extends AbstractXMLDocumentParser
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String CREATE_ENTITY_REF_NODES = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
    protected static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
    protected static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
    protected static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    protected static final String DEFER_NODE_EXPANSION = "http://apache.org/xml/features/dom/defer-node-expansion";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final String DOCUMENT_CLASS_NAME = "http://apache.org/xml/properties/dom/document-class-name";
    protected static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    private static final String[] RECOGNIZED_PROPERTIES;
    protected static final String DEFAULT_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.DocumentImpl";
    protected static final String CORE_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl";
    protected static final String PSVI_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl";
    private static final boolean DEBUG_EVENTS = false;
    private static final boolean DEBUG_BASEURI = false;
    protected DOMErrorHandlerWrapper fErrorHandler;
    protected boolean fInDTD;
    protected boolean fCreateEntityRefNodes;
    protected boolean fIncludeIgnorableWhitespace;
    protected boolean fIncludeComments;
    protected boolean fCreateCDATANodes;
    protected Document fDocument;
    protected CoreDocumentImpl fDocumentImpl;
    protected boolean fStorePSVI;
    protected String fDocumentClassName;
    protected DocumentType fDocumentType;
    protected Node fCurrentNode;
    protected CDATASection fCurrentCDATASection;
    protected EntityImpl fCurrentEntityDecl;
    protected int fDeferredEntityDecl;
    protected final StringBuilder fStringBuilder;
    protected StringBuilder fInternalSubset;
    protected boolean fDeferNodeExpansion;
    protected boolean fNamespaceAware;
    protected DeferredDocumentImpl fDeferredDocumentImpl;
    protected int fDocumentIndex;
    protected int fDocumentTypeIndex;
    protected int fCurrentNodeIndex;
    protected int fCurrentCDATASectionIndex;
    protected boolean fInDTDExternalSubset;
    protected Node fRoot;
    protected boolean fInCDATASection;
    protected boolean fFirstChunk;
    protected boolean fFilterReject;
    protected final Stack fBaseURIStack;
    protected int fRejectedElementDepth;
    protected Stack fSkippedElemStack;
    protected boolean fInEntityRef;
    private final QName fAttrQName;
    private XMLLocator fLocator;
    protected LSParserFilter fDOMFilter;
    
    protected AbstractDOMParser(final XMLParserConfiguration config) {
        super(config);
        this.fErrorHandler = null;
        this.fStringBuilder = new StringBuilder(50);
        this.fFirstChunk = false;
        this.fFilterReject = false;
        this.fBaseURIStack = new Stack();
        this.fRejectedElementDepth = 0;
        this.fSkippedElemStack = null;
        this.fInEntityRef = false;
        this.fAttrQName = new QName();
        this.fDOMFilter = null;
        this.fConfiguration.addRecognizedFeatures(AbstractDOMParser.RECOGNIZED_FEATURES);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", true);
        this.fConfiguration.addRecognizedProperties(AbstractDOMParser.RECOGNIZED_PROPERTIES);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "com.sun.org.apache.xerces.internal.dom.DocumentImpl");
    }
    
    protected String getDocumentClassName() {
        return this.fDocumentClassName;
    }
    
    protected void setDocumentClassName(String documentClassName) {
        if (documentClassName == null) {
            documentClassName = "com.sun.org.apache.xerces.internal.dom.DocumentImpl";
        }
        if (!documentClassName.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl") && !documentClassName.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl")) {
            try {
                final Class _class = ObjectFactory.findProviderClass(documentClassName, true);
                if (!Document.class.isAssignableFrom(_class)) {
                    throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidDocumentClassName", new Object[] { documentClassName }));
                }
            }
            catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "MissingDocumentClassName", new Object[] { documentClassName }));
            }
        }
        this.fDocumentClassName = documentClassName;
        if (!documentClassName.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl")) {
            this.fDeferNodeExpansion = false;
        }
    }
    
    public Document getDocument() {
        return this.fDocument;
    }
    
    public final void dropDocumentReferences() {
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fDeferredDocumentImpl = null;
        this.fDocumentType = null;
        this.fCurrentNode = null;
        this.fCurrentCDATASection = null;
        this.fCurrentEntityDecl = null;
        this.fRoot = null;
    }
    
    public void reset() throws XNIException {
        super.reset();
        this.fCreateEntityRefNodes = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes");
        this.fIncludeIgnorableWhitespace = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace");
        this.fDeferNodeExpansion = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
        this.fNamespaceAware = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
        this.fIncludeComments = this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments");
        this.fCreateCDATANodes = this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
        this.setDocumentClassName((String)this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name"));
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
        this.fDocumentType = null;
        this.fDocumentTypeIndex = -1;
        this.fDeferredDocumentImpl = null;
        this.fCurrentNode = null;
        this.fStringBuilder.setLength(0);
        this.fRoot = null;
        this.fInDTD = false;
        this.fInDTDExternalSubset = false;
        this.fInCDATASection = false;
        this.fFirstChunk = false;
        this.fCurrentCDATASection = null;
        this.fCurrentCDATASectionIndex = -1;
        this.fBaseURIStack.removeAllElements();
    }
    
    public void setLocale(final Locale locale) {
        this.fConfiguration.setLocale(locale);
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            this.setCharacterData(true);
            final EntityReference er = this.fDocument.createEntityReference(name);
            if (this.fDocumentImpl != null) {
                final EntityReferenceImpl erImpl = (EntityReferenceImpl)er;
                erImpl.setBaseURI(identifier.getExpandedSystemId());
                if (this.fDocumentType != null) {
                    final NamedNodeMap entities = this.fDocumentType.getEntities();
                    this.fCurrentEntityDecl = (EntityImpl)entities.getNamedItem(name);
                    if (this.fCurrentEntityDecl != null) {
                        this.fCurrentEntityDecl.setInputEncoding(encoding);
                    }
                }
                erImpl.needsSyncChildren(false);
            }
            this.fInEntityRef = true;
            this.fCurrentNode.appendChild(er);
            this.fCurrentNode = er;
        }
        else {
            final int er2 = this.fDeferredDocumentImpl.createDeferredEntityReference(name, identifier.getExpandedSystemId());
            if (this.fDocumentTypeIndex != -1) {
                for (int node = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); node != -1; node = this.fDeferredDocumentImpl.getRealPrevSibling(node, false)) {
                    final short nodeType = this.fDeferredDocumentImpl.getNodeType(node, false);
                    if (nodeType == 6) {
                        final String nodeName = this.fDeferredDocumentImpl.getNodeName(node, false);
                        if (nodeName.equals(name)) {
                            this.fDeferredEntityDecl = node;
                            this.fDeferredDocumentImpl.setInputEncoding(node, encoding);
                            break;
                        }
                    }
                }
            }
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, er2);
            this.fCurrentNodeIndex = er2;
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fInDTD) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            if (this.fCurrentEntityDecl != null && !this.fFilterReject) {
                this.fCurrentEntityDecl.setXmlEncoding(encoding);
                if (version != null) {
                    this.fCurrentEntityDecl.setXmlVersion(version);
                }
            }
        }
        else if (this.fDeferredEntityDecl != -1) {
            this.fDeferredDocumentImpl.setEntityInfo(this.fDeferredEntityDecl, version, encoding);
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fInDTD) {
            if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
                this.fInternalSubset.append("<!--");
                if (text.length > 0) {
                    this.fInternalSubset.append(text.ch, text.offset, text.length);
                }
                this.fInternalSubset.append("-->");
            }
            return;
        }
        if (!this.fIncludeComments || this.fFilterReject) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            final Comment comment = this.fDocument.createComment(text.toString());
            this.setCharacterData(false);
            this.fCurrentNode.appendChild(comment);
            if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x80) != 0x0) {
                final short code = this.fDOMFilter.acceptNode(comment);
                switch (code) {
                    case 4: {
                        throw Abort.INSTANCE;
                    }
                    case 2:
                    case 3: {
                        this.fCurrentNode.removeChild(comment);
                        this.fFirstChunk = true;
                    }
                }
            }
        }
        else {
            final int comment2 = this.fDeferredDocumentImpl.createDeferredComment(text.toString());
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, comment2);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fInDTD) {
            if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
                this.fInternalSubset.append("<?");
                this.fInternalSubset.append(target);
                if (data.length > 0) {
                    this.fInternalSubset.append(' ').append(data.ch, data.offset, data.length);
                }
                this.fInternalSubset.append("?>");
            }
            return;
        }
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            final ProcessingInstruction pi = this.fDocument.createProcessingInstruction(target, data.toString());
            this.setCharacterData(false);
            this.fCurrentNode.appendChild(pi);
            if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x40) != 0x0) {
                final short code = this.fDOMFilter.acceptNode(pi);
                switch (code) {
                    case 4: {
                        throw Abort.INSTANCE;
                    }
                    case 2:
                    case 3: {
                        this.fCurrentNode.removeChild(pi);
                        this.fFirstChunk = true;
                    }
                }
            }
        }
        else {
            final int pi2 = this.fDeferredDocumentImpl.createDeferredProcessingInstruction(target, data.toString());
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, pi2);
        }
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.fLocator = locator;
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl")) {
                this.fDocument = new DocumentImpl();
                (this.fDocumentImpl = (CoreDocumentImpl)this.fDocument).setStrictErrorChecking(false);
                this.fDocumentImpl.setInputEncoding(encoding);
                this.fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
            }
            else if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl")) {
                this.fDocument = new PSVIDocumentImpl();
                this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
                this.fStorePSVI = true;
                this.fDocumentImpl.setStrictErrorChecking(false);
                this.fDocumentImpl.setInputEncoding(encoding);
                this.fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
            }
            else {
                try {
                    final Class documentClass = ObjectFactory.findProviderClass(this.fDocumentClassName, true);
                    this.fDocument = documentClass.newInstance();
                    final Class defaultDocClass = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl", true);
                    if (defaultDocClass.isAssignableFrom(documentClass)) {
                        this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
                        final Class psviDocClass = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl", true);
                        if (psviDocClass.isAssignableFrom(documentClass)) {
                            this.fStorePSVI = true;
                        }
                        this.fDocumentImpl.setStrictErrorChecking(false);
                        this.fDocumentImpl.setInputEncoding(encoding);
                        if (locator != null) {
                            this.fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
                        }
                    }
                }
                catch (final ClassNotFoundException ex) {}
                catch (final Exception e) {
                    throw new RuntimeException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotCreateDocumentClass", new Object[] { this.fDocumentClassName }));
                }
            }
            this.fCurrentNode = this.fDocument;
        }
        else {
            this.fDeferredDocumentImpl = new DeferredDocumentImpl(this.fNamespaceAware);
            this.fDocument = this.fDeferredDocumentImpl;
            this.fDocumentIndex = this.fDeferredDocumentImpl.createDeferredDocument();
            this.fDeferredDocumentImpl.setInputEncoding(encoding);
            this.fDeferredDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
            this.fCurrentNodeIndex = this.fDocumentIndex;
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                if (version != null) {
                    this.fDocumentImpl.setXmlVersion(version);
                }
                this.fDocumentImpl.setXmlEncoding(encoding);
                this.fDocumentImpl.setXmlStandalone("yes".equals(standalone));
            }
        }
        else {
            if (version != null) {
                this.fDeferredDocumentImpl.setXmlVersion(version);
            }
            this.fDeferredDocumentImpl.setXmlEncoding(encoding);
            this.fDeferredDocumentImpl.setXmlStandalone("yes".equals(standalone));
        }
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                this.fDocumentType = this.fDocumentImpl.createDocumentType(rootElement, publicId, systemId);
                this.fCurrentNode.appendChild(this.fDocumentType);
            }
        }
        else {
            this.fDocumentTypeIndex = this.fDeferredDocumentImpl.createDeferredDocumentType(rootElement, publicId, systemId);
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDocumentTypeIndex);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                ++this.fRejectedElementDepth;
                return;
            }
            final Element el = this.createElementNode(element);
            final int attrCount = attributes.getLength();
            boolean seenSchemaDefault = false;
            for (int i = 0; i < attrCount; ++i) {
                attributes.getName(i, this.fAttrQName);
                final Attr attr = this.createAttrNode(this.fAttrQName);
                final String attrValue = attributes.getValue(i);
                final AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (this.fStorePSVI && attrPSVI != null) {
                    ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
                }
                attr.setValue(attrValue);
                final boolean specified = attributes.isSpecified(i);
                if (!specified && (seenSchemaDefault || (this.fAttrQName.uri != null && this.fAttrQName.uri != NamespaceContext.XMLNS_URI && this.fAttrQName.prefix == null))) {
                    el.setAttributeNodeNS(attr);
                    seenSchemaDefault = true;
                }
                else {
                    el.setAttributeNode(attr);
                }
                if (this.fDocumentImpl != null) {
                    final AttrImpl attrImpl = (AttrImpl)attr;
                    Object type = null;
                    boolean id = false;
                    if (attrPSVI != null && this.fNamespaceAware) {
                        type = attrPSVI.getMemberTypeDefinition();
                        if (type == null) {
                            type = attrPSVI.getTypeDefinition();
                            if (type != null) {
                                id = ((XSSimpleType)type).isIDType();
                                attrImpl.setType(type);
                            }
                        }
                        else {
                            id = ((XSSimpleType)type).isIDType();
                            attrImpl.setType(type);
                        }
                    }
                    else {
                        final boolean isDeclared = Boolean.TRUE.equals(attributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
                        if (isDeclared) {
                            type = attributes.getType(i);
                            id = "ID".equals(type);
                        }
                        attrImpl.setType(type);
                    }
                    if (id) {
                        ((ElementImpl)el).setIdAttributeNode(attr, true);
                    }
                    attrImpl.setSpecified(specified);
                }
            }
            this.setCharacterData(false);
            if (augs != null) {
                final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
                if (elementPSVI != null && this.fNamespaceAware) {
                    XSTypeDefinition type2 = elementPSVI.getMemberTypeDefinition();
                    if (type2 == null) {
                        type2 = elementPSVI.getTypeDefinition();
                    }
                    ((ElementNSImpl)el).setType(type2);
                }
            }
            if (this.fDOMFilter != null && !this.fInEntityRef) {
                if (this.fRoot == null) {
                    this.fRoot = el;
                }
                else {
                    final short code = this.fDOMFilter.startElement(el);
                    switch (code) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2: {
                            this.fFilterReject = true;
                            this.fRejectedElementDepth = 0;
                            return;
                        }
                        case 3: {
                            this.fFirstChunk = true;
                            this.fSkippedElemStack.push(Boolean.TRUE);
                            return;
                        }
                        default: {
                            if (!this.fSkippedElemStack.isEmpty()) {
                                this.fSkippedElemStack.push(Boolean.FALSE);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            this.fCurrentNode.appendChild(el);
            this.fCurrentNode = el;
        }
        else {
            final int el2 = this.fDeferredDocumentImpl.createDeferredElement(this.fNamespaceAware ? element.uri : null, element.rawname);
            Object type3 = null;
            final int attrCount2 = attributes.getLength();
            for (int i = attrCount2 - 1; i >= 0; --i) {
                final AttributePSVI attrPSVI2 = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                boolean id2 = false;
                if (attrPSVI2 != null && this.fNamespaceAware) {
                    type3 = attrPSVI2.getMemberTypeDefinition();
                    if (type3 == null) {
                        type3 = attrPSVI2.getTypeDefinition();
                        if (type3 != null) {
                            id2 = ((XSSimpleType)type3).isIDType();
                        }
                    }
                    else {
                        id2 = ((XSSimpleType)type3).isIDType();
                    }
                }
                else {
                    final boolean isDeclared2 = Boolean.TRUE.equals(attributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
                    if (isDeclared2) {
                        type3 = attributes.getType(i);
                        id2 = "ID".equals(type3);
                    }
                }
                this.fDeferredDocumentImpl.setDeferredAttribute(el2, attributes.getQName(i), attributes.getURI(i), attributes.getValue(i), attributes.isSpecified(i), id2, type3);
            }
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, el2);
            this.fCurrentNodeIndex = el2;
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            if (this.fInCDATASection && this.fCreateCDATANodes) {
                if (this.fCurrentCDATASection == null) {
                    this.fCurrentCDATASection = this.fDocument.createCDATASection(text.toString());
                    this.fCurrentNode.appendChild(this.fCurrentCDATASection);
                    this.fCurrentNode = this.fCurrentCDATASection;
                }
                else {
                    this.fCurrentCDATASection.appendData(text.toString());
                }
            }
            else if (!this.fInDTD) {
                if (text.length == 0) {
                    return;
                }
                final Node child = this.fCurrentNode.getLastChild();
                if (child != null && child.getNodeType() == 3) {
                    if (this.fFirstChunk) {
                        if (this.fDocumentImpl != null) {
                            this.fStringBuilder.append(((TextImpl)child).removeData());
                        }
                        else {
                            this.fStringBuilder.append(((Text)child).getData());
                            child.setNodeValue(null);
                        }
                        this.fFirstChunk = false;
                    }
                    if (text.length > 0) {
                        this.fStringBuilder.append(text.ch, text.offset, text.length);
                    }
                }
                else {
                    this.fFirstChunk = true;
                    final Text textNode = this.fDocument.createTextNode(text.toString());
                    this.fCurrentNode.appendChild(textNode);
                }
            }
        }
        else if (this.fInCDATASection && this.fCreateCDATANodes) {
            if (this.fCurrentCDATASectionIndex == -1) {
                final int cs = this.fDeferredDocumentImpl.createDeferredCDATASection(text.toString());
                this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, cs);
                this.fCurrentCDATASectionIndex = cs;
                this.fCurrentNodeIndex = cs;
            }
            else {
                final int txt = this.fDeferredDocumentImpl.createDeferredTextNode(text.toString(), false);
                this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, txt);
            }
        }
        else if (!this.fInDTD) {
            if (text.length == 0) {
                return;
            }
            final String value = text.toString();
            final int txt2 = this.fDeferredDocumentImpl.createDeferredTextNode(value, false);
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, txt2);
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.fIncludeIgnorableWhitespace || this.fFilterReject) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            final Node child = this.fCurrentNode.getLastChild();
            if (child != null && child.getNodeType() == 3) {
                final Text textNode = (Text)child;
                textNode.appendData(text.toString());
            }
            else {
                final Text textNode = this.fDocument.createTextNode(text.toString());
                if (this.fDocumentImpl != null) {
                    final TextImpl textNodeImpl = (TextImpl)textNode;
                    textNodeImpl.setIgnorableWhitespace(true);
                }
                this.fCurrentNode.appendChild(textNode);
            }
        }
        else {
            final int txt = this.fDeferredDocumentImpl.createDeferredTextNode(text.toString(), true);
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, txt);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (augs != null && this.fDocumentImpl != null && (this.fNamespaceAware || this.fStorePSVI)) {
                final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
                if (elementPSVI != null) {
                    if (this.fNamespaceAware) {
                        XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                        if (type == null) {
                            type = elementPSVI.getTypeDefinition();
                        }
                        ((ElementNSImpl)this.fCurrentNode).setType(type);
                    }
                    if (this.fStorePSVI) {
                        ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
                    }
                }
            }
            if (this.fDOMFilter != null) {
                if (this.fFilterReject) {
                    if (this.fRejectedElementDepth-- == 0) {
                        this.fFilterReject = false;
                    }
                    return;
                }
                if (!this.fSkippedElemStack.isEmpty() && this.fSkippedElemStack.pop() == Boolean.TRUE) {
                    return;
                }
                this.setCharacterData(false);
                if (this.fCurrentNode != this.fRoot && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x1) != 0x0) {
                    final short code = this.fDOMFilter.acceptNode(this.fCurrentNode);
                    switch (code) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2: {
                            final Node parent = this.fCurrentNode.getParentNode();
                            parent.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parent;
                            return;
                        }
                        case 3: {
                            this.fFirstChunk = true;
                            final Node parent = this.fCurrentNode.getParentNode();
                            final NodeList ls = this.fCurrentNode.getChildNodes();
                            for (int length = ls.getLength(), i = 0; i < length; ++i) {
                                parent.appendChild(ls.item(0));
                            }
                            parent.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parent;
                            return;
                        }
                    }
                }
                this.fCurrentNode = this.fCurrentNode.getParentNode();
            }
            else {
                this.setCharacterData(false);
                this.fCurrentNode = this.fCurrentNode.getParentNode();
            }
        }
        else {
            if (augs != null) {
                final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
                if (elementPSVI != null) {
                    XSTypeDefinition type = elementPSVI.getMemberTypeDefinition();
                    if (type == null) {
                        type = elementPSVI.getTypeDefinition();
                    }
                    this.fDeferredDocumentImpl.setTypeInfo(this.fCurrentNodeIndex, type);
                }
            }
            this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            if (this.fCreateCDATANodes) {
                this.setCharacterData(false);
            }
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            if (this.fCurrentCDATASection != null) {
                if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x8) != 0x0) {
                    final short code = this.fDOMFilter.acceptNode(this.fCurrentCDATASection);
                    switch (code) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2:
                        case 3: {
                            final Node parent = this.fCurrentNode.getParentNode();
                            parent.removeChild(this.fCurrentCDATASection);
                            this.fCurrentNode = parent;
                            return;
                        }
                    }
                }
                this.fCurrentNode = this.fCurrentNode.getParentNode();
                this.fCurrentCDATASection = null;
            }
        }
        else if (this.fCurrentCDATASectionIndex != -1) {
            this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
            this.fCurrentCDATASectionIndex = -1;
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                if (this.fLocator != null && this.fLocator.getEncoding() != null) {
                    this.fDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
                }
                this.fDocumentImpl.setStrictErrorChecking(true);
            }
            this.fCurrentNode = null;
        }
        else {
            if (this.fLocator != null && this.fLocator.getEncoding() != null) {
                this.fDeferredDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
            }
            this.fCurrentNodeIndex = -1;
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            this.setCharacterData(true);
            if (this.fDocumentType != null) {
                final NamedNodeMap entities = this.fDocumentType.getEntities();
                this.fCurrentEntityDecl = (EntityImpl)entities.getNamedItem(name);
                if (this.fCurrentEntityDecl != null) {
                    if (this.fCurrentEntityDecl != null && this.fCurrentEntityDecl.getFirstChild() == null) {
                        this.fCurrentEntityDecl.setReadOnly(false, true);
                        for (Node child = this.fCurrentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                            final Node copy = child.cloneNode(true);
                            this.fCurrentEntityDecl.appendChild(copy);
                        }
                        this.fCurrentEntityDecl.setReadOnly(true, true);
                    }
                    this.fCurrentEntityDecl = null;
                }
            }
            this.fInEntityRef = false;
            boolean removeEntityRef = false;
            if (this.fCreateEntityRefNodes) {
                if (this.fDocumentImpl != null) {
                    ((NodeImpl)this.fCurrentNode).setReadOnly(true, true);
                }
                if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x10) != 0x0) {
                    final short code = this.fDOMFilter.acceptNode(this.fCurrentNode);
                    switch (code) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2: {
                            final Node parent = this.fCurrentNode.getParentNode();
                            parent.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parent;
                            return;
                        }
                        case 3: {
                            this.fFirstChunk = true;
                            removeEntityRef = true;
                            break;
                        }
                        default: {
                            this.fCurrentNode = this.fCurrentNode.getParentNode();
                            break;
                        }
                    }
                }
                else {
                    this.fCurrentNode = this.fCurrentNode.getParentNode();
                }
            }
            if (!this.fCreateEntityRefNodes || removeEntityRef) {
                final NodeList children = this.fCurrentNode.getChildNodes();
                final Node parent = this.fCurrentNode.getParentNode();
                final int length = children.getLength();
                if (length > 0) {
                    Node node = this.fCurrentNode.getPreviousSibling();
                    final Node child2 = children.item(0);
                    if (node != null && node.getNodeType() == 3 && child2.getNodeType() == 3) {
                        ((Text)node).appendData(child2.getNodeValue());
                        this.fCurrentNode.removeChild(child2);
                    }
                    else {
                        node = parent.insertBefore(child2, this.fCurrentNode);
                        this.handleBaseURI(node);
                    }
                    for (int i = 1; i < length; ++i) {
                        node = parent.insertBefore(children.item(0), this.fCurrentNode);
                        this.handleBaseURI(node);
                    }
                }
                parent.removeChild(this.fCurrentNode);
                this.fCurrentNode = parent;
            }
        }
        else {
            if (this.fDocumentTypeIndex != -1) {
                for (int node2 = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); node2 != -1; node2 = this.fDeferredDocumentImpl.getRealPrevSibling(node2, false)) {
                    final short nodeType = this.fDeferredDocumentImpl.getNodeType(node2, false);
                    if (nodeType == 6) {
                        final String nodeName = this.fDeferredDocumentImpl.getNodeName(node2, false);
                        if (nodeName.equals(name)) {
                            this.fDeferredEntityDecl = node2;
                            break;
                        }
                    }
                }
            }
            if (this.fDeferredEntityDecl != -1 && this.fDeferredDocumentImpl.getLastChild(this.fDeferredEntityDecl, false) == -1) {
                int prevIndex = -1;
                for (int childIndex = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false); childIndex != -1; childIndex = this.fDeferredDocumentImpl.getRealPrevSibling(childIndex, false)) {
                    final int cloneIndex = this.fDeferredDocumentImpl.cloneNode(childIndex, true);
                    this.fDeferredDocumentImpl.insertBefore(this.fDeferredEntityDecl, cloneIndex, prevIndex);
                    prevIndex = cloneIndex;
                }
            }
            if (this.fCreateEntityRefNodes) {
                this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
            }
            else {
                int childIndex2 = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false);
                final int parentIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
                int prevIndex2 = this.fCurrentNodeIndex;
                final int lastChild = childIndex2;
                for (int sibling = -1; childIndex2 != -1; childIndex2 = sibling) {
                    this.handleBaseURI(childIndex2);
                    sibling = this.fDeferredDocumentImpl.getRealPrevSibling(childIndex2, false);
                    this.fDeferredDocumentImpl.insertBefore(parentIndex, childIndex2, prevIndex2);
                    prevIndex2 = childIndex2;
                }
                if (lastChild != -1) {
                    this.fDeferredDocumentImpl.setAsLastChild(parentIndex, lastChild);
                }
                else {
                    final int sibling = this.fDeferredDocumentImpl.getRealPrevSibling(prevIndex2, false);
                    this.fDeferredDocumentImpl.setAsLastChild(parentIndex, sibling);
                }
                this.fCurrentNodeIndex = parentIndex;
            }
            this.fDeferredEntityDecl = -1;
        }
    }
    
    protected final void handleBaseURI(final Node node) {
        if (this.fDocumentImpl != null) {
            String baseURI = null;
            final short nodeType = node.getNodeType();
            if (nodeType == 1) {
                if (this.fNamespaceAware) {
                    if (((Element)node).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base") != null) {
                        return;
                    }
                }
                else if (((Element)node).getAttributeNode("xml:base") != null) {
                    return;
                }
                baseURI = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
                if (baseURI != null && !baseURI.equals(this.fDocumentImpl.getDocumentURI())) {
                    if (this.fNamespaceAware) {
                        ((Element)node).setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", baseURI);
                    }
                    else {
                        ((Element)node).setAttribute("xml:base", baseURI);
                    }
                }
            }
            else if (nodeType == 7) {
                baseURI = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
                if (baseURI != null && this.fErrorHandler != null) {
                    final DOMErrorImpl error = new DOMErrorImpl();
                    error.fType = "pi-base-uri-not-preserved";
                    error.fRelatedData = baseURI;
                    error.fSeverity = 1;
                    this.fErrorHandler.getErrorHandler().handleError(error);
                }
            }
        }
    }
    
    protected final void handleBaseURI(final int node) {
        final short nodeType = this.fDeferredDocumentImpl.getNodeType(node, false);
        if (nodeType == 1) {
            String baseURI = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
            if (baseURI == null) {
                baseURI = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
            }
            if (baseURI != null && !baseURI.equals(this.fDeferredDocumentImpl.getDocumentURI())) {
                this.fDeferredDocumentImpl.setDeferredAttribute(node, "xml:base", "http://www.w3.org/XML/1998/namespace", baseURI, true);
            }
        }
        else if (nodeType == 7) {
            String baseURI = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
            if (baseURI == null) {
                baseURI = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
            }
            if (baseURI != null && this.fErrorHandler != null) {
                final DOMErrorImpl error = new DOMErrorImpl();
                error.fType = "pi-base-uri-not-preserved";
                error.fRelatedData = baseURI;
                error.fSeverity = 1;
                this.fErrorHandler.getErrorHandler().handleError(error);
            }
        }
    }
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augs) throws XNIException {
        this.fInDTD = true;
        if (locator != null) {
            this.fBaseURIStack.push(locator.getBaseSystemId());
        }
        if (this.fDeferNodeExpansion || this.fDocumentImpl != null) {
            this.fInternalSubset = new StringBuilder(1024);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augs) throws XNIException {
        this.fInDTD = false;
        if (!this.fBaseURIStack.isEmpty()) {
            this.fBaseURIStack.pop();
        }
        final String internalSubset = (this.fInternalSubset != null && this.fInternalSubset.length() > 0) ? this.fInternalSubset.toString() : null;
        if (this.fDeferNodeExpansion) {
            if (internalSubset != null) {
                this.fDeferredDocumentImpl.setInternalSubset(this.fDocumentTypeIndex, internalSubset);
            }
        }
        else if (this.fDocumentImpl != null && internalSubset != null) {
            ((DocumentTypeImpl)this.fDocumentType).setInternalSubset(internalSubset);
        }
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endConditional(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        this.fBaseURIStack.push(identifier.getBaseSystemId());
        this.fInDTDExternalSubset = true;
    }
    
    @Override
    public void endExternalSubset(final Augmentations augs) throws XNIException {
        this.fInDTDExternalSubset = false;
        this.fBaseURIStack.pop();
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augs) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            if (name.startsWith("%")) {
                this.fInternalSubset.append("% ");
                this.fInternalSubset.append(name.substring(1));
            }
            else {
                this.fInternalSubset.append(name);
            }
            this.fInternalSubset.append(' ');
            final String value = nonNormalizedText.toString();
            final boolean singleQuote = value.indexOf(39) == -1;
            this.fInternalSubset.append(singleQuote ? '\'' : '\"');
            this.fInternalSubset.append(value);
            this.fInternalSubset.append(singleQuote ? '\'' : '\"');
            this.fInternalSubset.append(">\n");
        }
        if (name.startsWith("%")) {
            return;
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)this.fDocumentImpl.createEntity(name);
                entity.setBaseURI(this.fBaseURIStack.peek());
                entities.setNamedItem(entity);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean found = false;
            for (int node = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); node != -1; node = this.fDeferredDocumentImpl.getRealPrevSibling(node, false)) {
                final short nodeType = this.fDeferredDocumentImpl.getNodeType(node, false);
                if (nodeType == 6) {
                    final String nodeName = this.fDeferredDocumentImpl.getNodeName(node, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                final int entityIndex = this.fDeferredDocumentImpl.createDeferredEntity(name, null, null, null, this.fBaseURIStack.peek());
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, entityIndex);
            }
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        final String publicId = identifier.getPublicId();
        final String literalSystemId = identifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            if (name.startsWith("%")) {
                this.fInternalSubset.append("% ");
                this.fInternalSubset.append(name.substring(1));
            }
            else {
                this.fInternalSubset.append(name);
            }
            this.fInternalSubset.append(' ');
            if (publicId != null) {
                this.fInternalSubset.append("PUBLIC '");
                this.fInternalSubset.append(publicId);
                this.fInternalSubset.append("' '");
            }
            else {
                this.fInternalSubset.append("SYSTEM '");
            }
            this.fInternalSubset.append(literalSystemId);
            this.fInternalSubset.append("'>\n");
        }
        if (name.startsWith("%")) {
            return;
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)this.fDocumentImpl.createEntity(name);
                entity.setPublicId(publicId);
                entity.setSystemId(literalSystemId);
                entity.setBaseURI(identifier.getBaseSystemId());
                entities.setNamedItem(entity);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean found = false;
            for (int nodeIndex = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); nodeIndex != -1; nodeIndex = this.fDeferredDocumentImpl.getRealPrevSibling(nodeIndex, false)) {
                final short nodeType = this.fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == 6) {
                    final String nodeName = this.fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                final int entityIndex = this.fDeferredDocumentImpl.createDeferredEntity(name, publicId, literalSystemId, null, identifier.getBaseSystemId());
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, entityIndex);
            }
        }
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (augs != null && this.fInternalSubset != null && !this.fInDTDExternalSubset && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
            this.fInternalSubset.append(name).append(";\n");
        }
        this.fBaseURIStack.push(identifier.getExpandedSystemId());
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augs) throws XNIException {
        this.fBaseURIStack.pop();
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augs) throws XNIException {
        final String publicId = identifier.getPublicId();
        final String literalSystemId = identifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            this.fInternalSubset.append(name);
            this.fInternalSubset.append(' ');
            if (publicId != null) {
                this.fInternalSubset.append("PUBLIC '");
                this.fInternalSubset.append(publicId);
                if (literalSystemId != null) {
                    this.fInternalSubset.append("' '");
                    this.fInternalSubset.append(literalSystemId);
                }
            }
            else {
                this.fInternalSubset.append("SYSTEM '");
                this.fInternalSubset.append(literalSystemId);
            }
            this.fInternalSubset.append("' NDATA ");
            this.fInternalSubset.append(notation);
            this.fInternalSubset.append(">\n");
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)this.fDocumentImpl.createEntity(name);
                entity.setPublicId(publicId);
                entity.setSystemId(literalSystemId);
                entity.setNotationName(notation);
                entity.setBaseURI(identifier.getBaseSystemId());
                entities.setNamedItem(entity);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean found = false;
            for (int nodeIndex = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); nodeIndex != -1; nodeIndex = this.fDeferredDocumentImpl.getRealPrevSibling(nodeIndex, false)) {
                final short nodeType = this.fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == 6) {
                    final String nodeName = this.fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                final int entityIndex = this.fDeferredDocumentImpl.createDeferredEntity(name, publicId, literalSystemId, notation, identifier.getBaseSystemId());
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, entityIndex);
            }
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        final String publicId = identifier.getPublicId();
        final String literalSystemId = identifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!NOTATION ");
            this.fInternalSubset.append(name);
            if (publicId != null) {
                this.fInternalSubset.append(" PUBLIC '");
                this.fInternalSubset.append(publicId);
                if (literalSystemId != null) {
                    this.fInternalSubset.append("' '");
                    this.fInternalSubset.append(literalSystemId);
                }
            }
            else {
                this.fInternalSubset.append(" SYSTEM '");
                this.fInternalSubset.append(literalSystemId);
            }
            this.fInternalSubset.append("'>\n");
        }
        if (this.fDocumentImpl != null && this.fDocumentType != null) {
            final NamedNodeMap notations = this.fDocumentType.getNotations();
            if (notations.getNamedItem(name) == null) {
                final NotationImpl notation = (NotationImpl)this.fDocumentImpl.createNotation(name);
                notation.setPublicId(publicId);
                notation.setSystemId(literalSystemId);
                notation.setBaseURI(identifier.getBaseSystemId());
                notations.setNamedItem(notation);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean found = false;
            for (int nodeIndex = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); nodeIndex != -1; nodeIndex = this.fDeferredDocumentImpl.getPrevSibling(nodeIndex, false)) {
                final short nodeType = this.fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == 12) {
                    final String nodeName = this.fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                final int notationIndex = this.fDeferredDocumentImpl.createDeferredNotation(name, publicId, literalSystemId, identifier.getBaseSystemId());
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, notationIndex);
            }
        }
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augs) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ELEMENT ");
            this.fInternalSubset.append(name);
            this.fInternalSubset.append(' ');
            this.fInternalSubset.append(contentModel);
            this.fInternalSubset.append(">\n");
        }
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augs) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ATTLIST ");
            this.fInternalSubset.append(elementName);
            this.fInternalSubset.append(' ');
            this.fInternalSubset.append(attributeName);
            this.fInternalSubset.append(' ');
            if (type.equals("ENUMERATION")) {
                this.fInternalSubset.append('(');
                for (int i = 0; i < enumeration.length; ++i) {
                    if (i > 0) {
                        this.fInternalSubset.append('|');
                    }
                    this.fInternalSubset.append(enumeration[i]);
                }
                this.fInternalSubset.append(')');
            }
            else {
                this.fInternalSubset.append(type);
            }
            if (defaultType != null) {
                this.fInternalSubset.append(' ');
                this.fInternalSubset.append(defaultType);
            }
            if (defaultValue != null) {
                this.fInternalSubset.append(" '");
                for (int i = 0; i < defaultValue.length; ++i) {
                    final char c = defaultValue.ch[defaultValue.offset + i];
                    if (c == '\'') {
                        this.fInternalSubset.append("&apos;");
                    }
                    else {
                        this.fInternalSubset.append(c);
                    }
                }
                this.fInternalSubset.append('\'');
            }
            this.fInternalSubset.append(">\n");
        }
        if (this.fDeferredDocumentImpl != null) {
            if (defaultValue != null) {
                int elementDefIndex = this.fDeferredDocumentImpl.lookupElementDefinition(elementName);
                if (elementDefIndex == -1) {
                    elementDefIndex = this.fDeferredDocumentImpl.createDeferredElementDefinition(elementName);
                    this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, elementDefIndex);
                }
                final boolean nsEnabled = this.fNamespaceAware;
                String namespaceURI = null;
                if (nsEnabled) {
                    if (attributeName.startsWith("xmlns:") || attributeName.equals("xmlns")) {
                        namespaceURI = NamespaceContext.XMLNS_URI;
                    }
                    else if (attributeName.startsWith("xml:")) {
                        namespaceURI = NamespaceContext.XML_URI;
                    }
                }
                final int attrIndex = this.fDeferredDocumentImpl.createDeferredAttribute(attributeName, namespaceURI, defaultValue.toString(), false);
                if ("ID".equals(type)) {
                    this.fDeferredDocumentImpl.setIdAttribute(attrIndex);
                }
                this.fDeferredDocumentImpl.appendChild(elementDefIndex, attrIndex);
            }
        }
        else if (this.fDocumentImpl != null && defaultValue != null) {
            final NamedNodeMap elements = ((DocumentTypeImpl)this.fDocumentType).getElements();
            ElementDefinitionImpl elementDef = (ElementDefinitionImpl)elements.getNamedItem(elementName);
            if (elementDef == null) {
                elementDef = this.fDocumentImpl.createElementDefinition(elementName);
                ((DocumentTypeImpl)this.fDocumentType).getElements().setNamedItem(elementDef);
            }
            final boolean nsEnabled2 = this.fNamespaceAware;
            AttrImpl attr;
            if (nsEnabled2) {
                String namespaceURI2 = null;
                if (attributeName.startsWith("xmlns:") || attributeName.equals("xmlns")) {
                    namespaceURI2 = NamespaceContext.XMLNS_URI;
                }
                else if (attributeName.startsWith("xml:")) {
                    namespaceURI2 = NamespaceContext.XML_URI;
                }
                attr = (AttrImpl)this.fDocumentImpl.createAttributeNS(namespaceURI2, attributeName);
            }
            else {
                attr = (AttrImpl)this.fDocumentImpl.createAttribute(attributeName);
            }
            attr.setValue(defaultValue.toString());
            attr.setSpecified(false);
            attr.setIdAttribute("ID".equals(type));
            if (nsEnabled2) {
                elementDef.getAttributes().setNamedItemNS(attr);
            }
            else {
                elementDef.getAttributes().setNamedItem(attr);
            }
        }
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endAttlist(final Augmentations augs) throws XNIException {
    }
    
    protected Element createElementNode(final QName element) {
        Element el = null;
        if (this.fNamespaceAware) {
            if (this.fDocumentImpl != null) {
                el = this.fDocumentImpl.createElementNS(element.uri, element.rawname, element.localpart);
            }
            else {
                el = this.fDocument.createElementNS(element.uri, element.rawname);
            }
        }
        else {
            el = this.fDocument.createElement(element.rawname);
        }
        return el;
    }
    
    protected Attr createAttrNode(final QName attrQName) {
        Attr attr = null;
        if (this.fNamespaceAware) {
            if (this.fDocumentImpl != null) {
                attr = this.fDocumentImpl.createAttributeNS(attrQName.uri, attrQName.rawname, attrQName.localpart);
            }
            else {
                attr = this.fDocument.createAttributeNS(attrQName.uri, attrQName.rawname);
            }
        }
        else {
            attr = this.fDocument.createAttribute(attrQName.rawname);
        }
        return attr;
    }
    
    protected void setCharacterData(final boolean sawChars) {
        this.fFirstChunk = sawChars;
        final Node child = this.fCurrentNode.getLastChild();
        if (child != null) {
            if (this.fStringBuilder.length() > 0) {
                if (child.getNodeType() == 3) {
                    if (this.fDocumentImpl != null) {
                        ((TextImpl)child).replaceData(this.fStringBuilder.toString());
                    }
                    else {
                        ((Text)child).setData(this.fStringBuilder.toString());
                    }
                }
                this.fStringBuilder.setLength(0);
            }
            if (this.fDOMFilter != null && !this.fInEntityRef && child.getNodeType() == 3 && (this.fDOMFilter.getWhatToShow() & 0x4) != 0x0) {
                final short code = this.fDOMFilter.acceptNode(child);
                switch (code) {
                    case 4: {
                        throw Abort.INSTANCE;
                    }
                    case 2:
                    case 3: {
                        this.fCurrentNode.removeChild(child);
                    }
                }
            }
        }
    }
    
    public void abort() {
        throw Abort.INSTANCE;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/dom/create-entity-ref-nodes", "http://apache.org/xml/features/include-comments", "http://apache.org/xml/features/create-cdata-nodes", "http://apache.org/xml/features/dom/include-ignorable-whitespace", "http://apache.org/xml/features/dom/defer-node-expansion" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/dom/document-class-name", "http://apache.org/xml/properties/dom/current-element-node" };
    }
    
    static final class Abort extends RuntimeException
    {
        private static final long serialVersionUID = 1687848994976808490L;
        static final Abort INSTANCE;
        
        private Abort() {
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
        
        static {
            INSTANCE = new Abort();
        }
    }
}
