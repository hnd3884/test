package org.apache.xerces.parsers;

import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.NotationImpl;
import org.w3c.dom.NamedNodeMap;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.w3c.dom.DOMError;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.NodeList;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.w3c.dom.Text;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.apache.xerces.xni.XMLString;
import org.w3c.dom.EntityReference;
import org.apache.xerces.dom.EntityReferenceImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import java.util.Locale;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.ls.LSParserFilter;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.QName;
import java.util.Stack;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.EntityImpl;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.w3c.dom.Document;
import org.apache.xerces.util.DOMErrorHandlerWrapper;

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
    protected static final String DEFAULT_DOCUMENT_CLASS_NAME = "org.apache.xerces.dom.DocumentImpl";
    protected static final String CORE_DOCUMENT_CLASS_NAME = "org.apache.xerces.dom.CoreDocumentImpl";
    protected static final String PSVI_DOCUMENT_CLASS_NAME = "org.apache.xerces.dom.PSVIDocumentImpl";
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
    protected final StringBuffer fStringBuffer;
    protected StringBuffer fInternalSubset;
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
    
    protected AbstractDOMParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
        this.fErrorHandler = null;
        this.fStringBuffer = new StringBuffer(50);
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
        this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.DocumentImpl");
    }
    
    protected String getDocumentClassName() {
        return this.fDocumentClassName;
    }
    
    protected void setDocumentClassName(String fDocumentClassName) {
        if (fDocumentClassName == null) {
            fDocumentClassName = "org.apache.xerces.dom.DocumentImpl";
        }
        if (!fDocumentClassName.equals("org.apache.xerces.dom.DocumentImpl") && !fDocumentClassName.equals("org.apache.xerces.dom.PSVIDocumentImpl")) {
            try {
                if (!Document.class.isAssignableFrom(ObjectFactory.findProviderClass(fDocumentClassName, ObjectFactory.findClassLoader(), true))) {
                    throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidDocumentClassName", new Object[] { fDocumentClassName }));
                }
            }
            catch (final ClassNotFoundException ex) {
                throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "MissingDocumentClassName", new Object[] { fDocumentClassName }));
            }
        }
        this.fDocumentClassName = fDocumentClassName;
        if (!fDocumentClassName.equals("org.apache.xerces.dom.DocumentImpl")) {
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
        this.fStringBuffer.setLength(0);
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
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String inputEncoding, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            this.setCharacterData(true);
            final EntityReference entityReference = this.fDocument.createEntityReference(s);
            if (this.fDocumentImpl != null) {
                final EntityReferenceImpl entityReferenceImpl = (EntityReferenceImpl)entityReference;
                entityReferenceImpl.setBaseURI(xmlResourceIdentifier.getExpandedSystemId());
                if (this.fDocumentType != null) {
                    this.fCurrentEntityDecl = (EntityImpl)this.fDocumentType.getEntities().getNamedItem(s);
                    if (this.fCurrentEntityDecl != null) {
                        this.fCurrentEntityDecl.setInputEncoding(inputEncoding);
                    }
                }
                entityReferenceImpl.needsSyncChildren(false);
            }
            this.fInEntityRef = true;
            this.fCurrentNode.appendChild(entityReference);
            this.fCurrentNode = entityReference;
        }
        else {
            final int deferredEntityReference = this.fDeferredDocumentImpl.createDeferredEntityReference(s, xmlResourceIdentifier.getExpandedSystemId());
            if (this.fDocumentTypeIndex != -1) {
                for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
                    if (this.fDeferredDocumentImpl.getNodeType(i, false) == 6 && this.fDeferredDocumentImpl.getNodeName(i, false).equals(s)) {
                        this.fDeferredEntityDecl = i;
                        this.fDeferredDocumentImpl.setInputEncoding(i, inputEncoding);
                        break;
                    }
                }
            }
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, deferredEntityReference);
            this.fCurrentNodeIndex = deferredEntityReference;
        }
    }
    
    public void textDecl(final String xmlVersion, final String xmlEncoding, final Augmentations augmentations) throws XNIException {
        if (this.fInDTD) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            if (this.fCurrentEntityDecl != null && !this.fFilterReject) {
                this.fCurrentEntityDecl.setXmlEncoding(xmlEncoding);
                if (xmlVersion != null) {
                    this.fCurrentEntityDecl.setXmlVersion(xmlVersion);
                }
            }
        }
        else if (this.fDeferredEntityDecl != -1) {
            this.fDeferredDocumentImpl.setEntityInfo(this.fDeferredEntityDecl, xmlVersion, xmlEncoding);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fInDTD) {
            if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
                this.fInternalSubset.append("<!--");
                if (xmlString.length > 0) {
                    this.fInternalSubset.append(xmlString.ch, xmlString.offset, xmlString.length);
                }
                this.fInternalSubset.append("-->");
            }
            return;
        }
        if (!this.fIncludeComments || this.fFilterReject) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            final Comment comment = this.fDocument.createComment(xmlString.toString());
            this.setCharacterData(false);
            this.fCurrentNode.appendChild(comment);
            if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x80) != 0x0) {
                switch (this.fDOMFilter.acceptNode(comment)) {
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
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDeferredDocumentImpl.createDeferredComment(xmlString.toString()));
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fInDTD) {
            if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
                this.fInternalSubset.append("<?");
                this.fInternalSubset.append(s);
                if (xmlString.length > 0) {
                    this.fInternalSubset.append(' ').append(xmlString.ch, xmlString.offset, xmlString.length);
                }
                this.fInternalSubset.append("?>");
            }
            return;
        }
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            final ProcessingInstruction processingInstruction = this.fDocument.createProcessingInstruction(s, xmlString.toString());
            this.setCharacterData(false);
            this.fCurrentNode.appendChild(processingInstruction);
            if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x40) != 0x0) {
                switch (this.fDOMFilter.acceptNode(processingInstruction)) {
                    case 4: {
                        throw Abort.INSTANCE;
                    }
                    case 2:
                    case 3: {
                        this.fCurrentNode.removeChild(processingInstruction);
                        this.fFirstChunk = true;
                    }
                }
            }
        }
        else {
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDeferredDocumentImpl.createDeferredProcessingInstruction(s, xmlString.toString()));
        }
    }
    
    public void startDocument(final XMLLocator fLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
        this.fLocator = fLocator;
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentClassName.equals("org.apache.xerces.dom.DocumentImpl")) {
                this.fDocument = new DocumentImpl();
                (this.fDocumentImpl = (CoreDocumentImpl)this.fDocument).setStrictErrorChecking(false);
                this.fDocumentImpl.setInputEncoding(s);
                this.fDocumentImpl.setDocumentURI(fLocator.getExpandedSystemId());
            }
            else if (this.fDocumentClassName.equals("org.apache.xerces.dom.PSVIDocumentImpl")) {
                this.fDocument = new PSVIDocumentImpl();
                this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
                this.fStorePSVI = true;
                this.fDocumentImpl.setStrictErrorChecking(false);
                this.fDocumentImpl.setInputEncoding(s);
                this.fDocumentImpl.setDocumentURI(fLocator.getExpandedSystemId());
            }
            else {
                try {
                    final ClassLoader classLoader = ObjectFactory.findClassLoader();
                    final Class providerClass = ObjectFactory.findProviderClass(this.fDocumentClassName, classLoader, true);
                    this.fDocument = (Document)providerClass.newInstance();
                    if (ObjectFactory.findProviderClass("org.apache.xerces.dom.CoreDocumentImpl", classLoader, true).isAssignableFrom(providerClass)) {
                        this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
                        if (ObjectFactory.findProviderClass("org.apache.xerces.dom.PSVIDocumentImpl", classLoader, true).isAssignableFrom(providerClass)) {
                            this.fStorePSVI = true;
                        }
                        this.fDocumentImpl.setStrictErrorChecking(false);
                        this.fDocumentImpl.setInputEncoding(s);
                        if (fLocator != null) {
                            this.fDocumentImpl.setDocumentURI(fLocator.getExpandedSystemId());
                        }
                    }
                }
                catch (final ClassNotFoundException ex) {}
                catch (final Exception ex2) {
                    throw new RuntimeException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotCreateDocumentClass", new Object[] { this.fDocumentClassName }));
                }
            }
            this.fCurrentNode = this.fDocument;
        }
        else {
            this.fDeferredDocumentImpl = new DeferredDocumentImpl(this.fNamespaceAware);
            this.fDocument = this.fDeferredDocumentImpl;
            this.fDocumentIndex = this.fDeferredDocumentImpl.createDeferredDocument();
            this.fDeferredDocumentImpl.setInputEncoding(s);
            this.fDeferredDocumentImpl.setDocumentURI(fLocator.getExpandedSystemId());
            this.fCurrentNodeIndex = this.fDocumentIndex;
        }
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                if (s != null) {
                    this.fDocumentImpl.setXmlVersion(s);
                }
                this.fDocumentImpl.setXmlEncoding(s2);
                this.fDocumentImpl.setXmlStandalone("yes".equals(s3));
            }
        }
        else {
            if (s != null) {
                this.fDeferredDocumentImpl.setXmlVersion(s);
            }
            this.fDeferredDocumentImpl.setXmlEncoding(s2);
            this.fDeferredDocumentImpl.setXmlStandalone("yes".equals(s3));
        }
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                this.fDocumentType = this.fDocumentImpl.createDocumentType(s, s2, s3);
                this.fCurrentNode.appendChild(this.fDocumentType);
            }
        }
        else {
            this.fDocumentTypeIndex = this.fDeferredDocumentImpl.createDeferredDocumentType(s, s2, s3);
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDocumentTypeIndex);
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                ++this.fRejectedElementDepth;
                return;
            }
            final Element elementNode = this.createElementNode(qName);
            final int length = xmlAttributes.getLength();
            int n = 0;
            for (int i = 0; i < length; ++i) {
                xmlAttributes.getName(i, this.fAttrQName);
                final Attr attrNode = this.createAttrNode(this.fAttrQName);
                final String value = xmlAttributes.getValue(i);
                final AttributePSVI psvi = (AttributePSVI)xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (this.fStorePSVI && psvi != null) {
                    ((PSVIAttrNSImpl)attrNode).setPSVI(psvi);
                }
                attrNode.setValue(value);
                final boolean specified = xmlAttributes.isSpecified(i);
                if (!specified && (n != 0 || (this.fAttrQName.uri != null && this.fAttrQName.uri != NamespaceContext.XMLNS_URI && this.fAttrQName.prefix == null))) {
                    elementNode.setAttributeNodeNS(attrNode);
                    n = 1;
                }
                else {
                    elementNode.setAttributeNode(attrNode);
                }
                if (this.fDocumentImpl != null) {
                    final AttrImpl attrImpl = (AttrImpl)attrNode;
                    Object type = null;
                    boolean b = false;
                    if (psvi != null && this.fNamespaceAware) {
                        final XSSimpleTypeDefinition memberTypeDefinition = psvi.getMemberTypeDefinition();
                        if (memberTypeDefinition == null) {
                            final XSTypeDefinition typeDefinition = psvi.getTypeDefinition();
                            if (typeDefinition != null) {
                                b = ((XSSimpleType)typeDefinition).isIDType();
                                attrImpl.setType(typeDefinition);
                            }
                        }
                        else {
                            b = ((XSSimpleType)memberTypeDefinition).isIDType();
                            attrImpl.setType(memberTypeDefinition);
                        }
                    }
                    else {
                        if (Boolean.TRUE.equals(xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"))) {
                            type = xmlAttributes.getType(i);
                            b = "ID".equals(type);
                        }
                        attrImpl.setType(type);
                    }
                    if (b) {
                        ((ElementImpl)elementNode).setIdAttributeNode(attrNode, true);
                    }
                    attrImpl.setSpecified(specified);
                }
            }
            this.setCharacterData(false);
            if (augmentations != null) {
                final ElementPSVI elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
                if (elementPSVI != null && this.fNamespaceAware) {
                    XSTypeDefinition type2 = elementPSVI.getMemberTypeDefinition();
                    if (type2 == null) {
                        type2 = elementPSVI.getTypeDefinition();
                    }
                    ((ElementNSImpl)elementNode).setType(type2);
                }
            }
            if (this.fDOMFilter != null && !this.fInEntityRef) {
                if (this.fRoot == null) {
                    this.fRoot = elementNode;
                }
                else {
                    switch (this.fDOMFilter.startElement(elementNode)) {
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
            this.fCurrentNode.appendChild(elementNode);
            this.fCurrentNode = elementNode;
        }
        else {
            final int deferredElement = this.fDeferredDocumentImpl.createDeferredElement(this.fNamespaceAware ? qName.uri : null, qName.rawname);
            Object o = null;
            for (int j = xmlAttributes.getLength() - 1; j >= 0; --j) {
                final AttributePSVI attributePSVI = (AttributePSVI)xmlAttributes.getAugmentations(j).getItem("ATTRIBUTE_PSVI");
                boolean b2 = false;
                if (attributePSVI != null && this.fNamespaceAware) {
                    o = attributePSVI.getMemberTypeDefinition();
                    if (o == null) {
                        o = attributePSVI.getTypeDefinition();
                        if (o != null) {
                            b2 = ((XSSimpleType)o).isIDType();
                        }
                    }
                    else {
                        b2 = ((XSSimpleType)o).isIDType();
                    }
                }
                else if (Boolean.TRUE.equals(xmlAttributes.getAugmentations(j).getItem("ATTRIBUTE_DECLARED"))) {
                    o = xmlAttributes.getType(j);
                    b2 = "ID".equals(o);
                }
                this.fDeferredDocumentImpl.setDeferredAttribute(deferredElement, xmlAttributes.getQName(j), xmlAttributes.getURI(j), xmlAttributes.getValue(j), xmlAttributes.isSpecified(j), b2, o);
            }
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, deferredElement);
            this.fCurrentNodeIndex = deferredElement;
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            if (this.fInCDATASection && this.fCreateCDATANodes) {
                if (this.fCurrentCDATASection == null) {
                    this.fCurrentCDATASection = this.fDocument.createCDATASection(xmlString.toString());
                    this.fCurrentNode.appendChild(this.fCurrentCDATASection);
                    this.fCurrentNode = this.fCurrentCDATASection;
                }
                else {
                    this.fCurrentCDATASection.appendData(xmlString.toString());
                }
            }
            else if (!this.fInDTD) {
                if (xmlString.length == 0) {
                    return;
                }
                final Node lastChild = this.fCurrentNode.getLastChild();
                if (lastChild != null && lastChild.getNodeType() == 3) {
                    if (this.fFirstChunk) {
                        if (this.fDocumentImpl != null) {
                            this.fStringBuffer.append(((TextImpl)lastChild).removeData());
                        }
                        else {
                            this.fStringBuffer.append(((Text)lastChild).getData());
                            ((Text)lastChild).setNodeValue(null);
                        }
                        this.fFirstChunk = false;
                    }
                    if (xmlString.length > 0) {
                        this.fStringBuffer.append(xmlString.ch, xmlString.offset, xmlString.length);
                    }
                }
                else {
                    this.fFirstChunk = true;
                    this.fCurrentNode.appendChild(this.fDocument.createTextNode(xmlString.toString()));
                }
            }
        }
        else if (this.fInCDATASection && this.fCreateCDATANodes) {
            if (this.fCurrentCDATASectionIndex == -1) {
                final int deferredCDATASection = this.fDeferredDocumentImpl.createDeferredCDATASection(xmlString.toString());
                this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, deferredCDATASection);
                this.fCurrentCDATASectionIndex = deferredCDATASection;
                this.fCurrentNodeIndex = deferredCDATASection;
            }
            else {
                this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDeferredDocumentImpl.createDeferredTextNode(xmlString.toString(), false));
            }
        }
        else if (!this.fInDTD) {
            if (xmlString.length == 0) {
                return;
            }
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDeferredDocumentImpl.createDeferredTextNode(xmlString.toString(), false));
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.fIncludeIgnorableWhitespace || this.fFilterReject) {
            return;
        }
        if (!this.fDeferNodeExpansion) {
            final Node lastChild = this.fCurrentNode.getLastChild();
            if (lastChild != null && lastChild.getNodeType() == 3) {
                ((Text)lastChild).appendData(xmlString.toString());
            }
            else {
                final Text textNode = this.fDocument.createTextNode(xmlString.toString());
                if (this.fDocumentImpl != null) {
                    ((TextImpl)textNode).setIgnorableWhitespace(true);
                }
                this.fCurrentNode.appendChild(textNode);
            }
        }
        else {
            this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDeferredDocumentImpl.createDeferredTextNode(xmlString.toString(), true));
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (augmentations != null && this.fDocumentImpl != null && (this.fNamespaceAware || this.fStorePSVI)) {
                final ElementPSVI psvi = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
                if (psvi != null) {
                    if (this.fNamespaceAware) {
                        XSTypeDefinition type = psvi.getMemberTypeDefinition();
                        if (type == null) {
                            type = psvi.getTypeDefinition();
                        }
                        ((ElementNSImpl)this.fCurrentNode).setType(type);
                    }
                    if (this.fStorePSVI) {
                        ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(psvi);
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
                    switch (this.fDOMFilter.acceptNode(this.fCurrentNode)) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2: {
                            final Node parentNode = this.fCurrentNode.getParentNode();
                            parentNode.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parentNode;
                            return;
                        }
                        case 3: {
                            this.fFirstChunk = true;
                            final Node parentNode2 = this.fCurrentNode.getParentNode();
                            final NodeList childNodes = this.fCurrentNode.getChildNodes();
                            for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
                                parentNode2.appendChild(childNodes.item(0));
                            }
                            parentNode2.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parentNode2;
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
            if (augmentations != null) {
                final ElementPSVI elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
                if (elementPSVI != null) {
                    XSTypeDefinition xsTypeDefinition = elementPSVI.getMemberTypeDefinition();
                    if (xsTypeDefinition == null) {
                        xsTypeDefinition = elementPSVI.getTypeDefinition();
                    }
                    this.fDeferredDocumentImpl.setTypeInfo(this.fCurrentNodeIndex, xsTypeDefinition);
                }
            }
            this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
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
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATASection = false;
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            if (this.fCurrentCDATASection != null) {
                if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x8) != 0x0) {
                    switch (this.fDOMFilter.acceptNode(this.fCurrentCDATASection)) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2:
                        case 3: {
                            final Node parentNode = this.fCurrentNode.getParentNode();
                            parentNode.removeChild(this.fCurrentCDATASection);
                            this.fCurrentNode = parentNode;
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
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fDocumentImpl != null) {
                if (this.fLocator != null) {
                    this.fDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
                }
                this.fDocumentImpl.setStrictErrorChecking(true);
            }
            this.fCurrentNode = null;
        }
        else {
            if (this.fLocator != null) {
                this.fDeferredDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
            }
            this.fCurrentNodeIndex = -1;
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (!this.fDeferNodeExpansion) {
            if (this.fFilterReject) {
                return;
            }
            this.setCharacterData(true);
            if (this.fDocumentType != null) {
                this.fCurrentEntityDecl = (EntityImpl)this.fDocumentType.getEntities().getNamedItem(s);
                if (this.fCurrentEntityDecl != null) {
                    if (this.fCurrentEntityDecl != null && this.fCurrentEntityDecl.getFirstChild() == null) {
                        this.fCurrentEntityDecl.setReadOnly(false, true);
                        for (Node node = this.fCurrentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                            this.fCurrentEntityDecl.appendChild(node.cloneNode(true));
                        }
                        this.fCurrentEntityDecl.setReadOnly(true, true);
                    }
                    this.fCurrentEntityDecl = null;
                }
            }
            this.fInEntityRef = false;
            boolean b = false;
            if (this.fCreateEntityRefNodes) {
                if (this.fDocumentImpl != null) {
                    ((NodeImpl)this.fCurrentNode).setReadOnly(true, true);
                }
                if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x10) != 0x0) {
                    switch (this.fDOMFilter.acceptNode(this.fCurrentNode)) {
                        case 4: {
                            throw Abort.INSTANCE;
                        }
                        case 2: {
                            final Node parentNode = this.fCurrentNode.getParentNode();
                            parentNode.removeChild(this.fCurrentNode);
                            this.fCurrentNode = parentNode;
                            return;
                        }
                        case 3: {
                            this.fFirstChunk = true;
                            b = true;
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
            if (!this.fCreateEntityRefNodes || b) {
                final NodeList childNodes = this.fCurrentNode.getChildNodes();
                final Node parentNode2 = this.fCurrentNode.getParentNode();
                final int length = childNodes.getLength();
                if (length > 0) {
                    final Node previousSibling = this.fCurrentNode.getPreviousSibling();
                    final Node item = childNodes.item(0);
                    if (previousSibling != null && previousSibling.getNodeType() == 3 && item.getNodeType() == 3) {
                        ((Text)previousSibling).appendData(item.getNodeValue());
                        this.fCurrentNode.removeChild(item);
                    }
                    else {
                        this.handleBaseURI(parentNode2.insertBefore(item, this.fCurrentNode));
                    }
                    for (int i = 1; i < length; ++i) {
                        this.handleBaseURI(parentNode2.insertBefore(childNodes.item(0), this.fCurrentNode));
                    }
                }
                parentNode2.removeChild(this.fCurrentNode);
                this.fCurrentNode = parentNode2;
            }
        }
        else {
            if (this.fDocumentTypeIndex != -1) {
                for (int j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false)) {
                    if (this.fDeferredDocumentImpl.getNodeType(j, false) == 6 && this.fDeferredDocumentImpl.getNodeName(j, false).equals(s)) {
                        this.fDeferredEntityDecl = j;
                        break;
                    }
                }
            }
            if (this.fDeferredEntityDecl != -1 && this.fDeferredDocumentImpl.getLastChild(this.fDeferredEntityDecl, false) == -1) {
                int n = -1;
                for (int k = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false); k != -1; k = this.fDeferredDocumentImpl.getRealPrevSibling(k, false)) {
                    final int cloneNode = this.fDeferredDocumentImpl.cloneNode(k, true);
                    this.fDeferredDocumentImpl.insertBefore(this.fDeferredEntityDecl, cloneNode, n);
                    n = cloneNode;
                }
            }
            if (this.fCreateEntityRefNodes) {
                this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
            }
            else {
                int l = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false);
                final int parentNode3 = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
                int fCurrentNodeIndex = this.fCurrentNodeIndex;
                final int n2 = l;
                while (l != -1) {
                    this.handleBaseURI(l);
                    final int realPrevSibling = this.fDeferredDocumentImpl.getRealPrevSibling(l, false);
                    this.fDeferredDocumentImpl.insertBefore(parentNode3, l, fCurrentNodeIndex);
                    fCurrentNodeIndex = l;
                    l = realPrevSibling;
                }
                if (n2 != -1) {
                    this.fDeferredDocumentImpl.setAsLastChild(parentNode3, n2);
                }
                else {
                    this.fDeferredDocumentImpl.setAsLastChild(parentNode3, this.fDeferredDocumentImpl.getRealPrevSibling(fCurrentNodeIndex, false));
                }
                this.fCurrentNodeIndex = parentNode3;
            }
            this.fDeferredEntityDecl = -1;
        }
    }
    
    protected final void handleBaseURI(final Node node) {
        if (this.fDocumentImpl != null) {
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
                final String baseURI = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
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
                final String baseURI2 = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
                if (baseURI2 != null && this.fErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                    domErrorImpl.fType = "pi-base-uri-not-preserved";
                    domErrorImpl.fRelatedData = baseURI2;
                    domErrorImpl.fSeverity = 1;
                    this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
                }
            }
        }
    }
    
    protected final void handleBaseURI(final int n) {
        final short nodeType = this.fDeferredDocumentImpl.getNodeType(n, false);
        if (nodeType == 1) {
            String s = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
            if (s == null) {
                s = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
            }
            if (s != null && !s.equals(this.fDeferredDocumentImpl.getDocumentURI())) {
                this.fDeferredDocumentImpl.setDeferredAttribute(n, "xml:base", "http://www.w3.org/XML/1998/namespace", s, true);
            }
        }
        else if (nodeType == 7) {
            String fRelatedData = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
            if (fRelatedData == null) {
                fRelatedData = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl);
            }
            if (fRelatedData != null && this.fErrorHandler != null) {
                final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                domErrorImpl.fType = "pi-base-uri-not-preserved";
                domErrorImpl.fRelatedData = fRelatedData;
                domErrorImpl.fSeverity = 1;
                this.fErrorHandler.getErrorHandler().handleError(domErrorImpl);
            }
        }
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        if (xmlLocator != null) {
            this.fBaseURIStack.push(xmlLocator.getBaseSystemId());
        }
        if (this.fDeferNodeExpansion || this.fDocumentImpl != null) {
            this.fInternalSubset = new StringBuffer(1024);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
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
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        this.fBaseURIStack.push(xmlResourceIdentifier.getBaseSystemId());
        this.fInDTDExternalSubset = true;
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        this.fInDTDExternalSubset = false;
        this.fBaseURIStack.pop();
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            if (s.startsWith("%")) {
                this.fInternalSubset.append("% ");
                this.fInternalSubset.append(s.substring(1));
            }
            else {
                this.fInternalSubset.append(s);
            }
            this.fInternalSubset.append(' ');
            final String string = xmlString2.toString();
            final boolean b = string.indexOf(39) == -1;
            this.fInternalSubset.append((char)(b ? 39 : 34));
            this.fInternalSubset.append(string);
            this.fInternalSubset.append((char)(b ? 39 : 34));
            this.fInternalSubset.append(">\n");
        }
        if (s.startsWith("%")) {
            return;
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            if (entities.getNamedItem(s) == null) {
                final EntityImpl namedItem = (EntityImpl)this.fDocumentImpl.createEntity(s);
                namedItem.setBaseURI(this.fBaseURIStack.peek());
                entities.setNamedItem(namedItem);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean b2 = false;
            for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
                if (this.fDeferredDocumentImpl.getNodeType(i, false) == 6 && this.fDeferredDocumentImpl.getNodeName(i, false).equals(s)) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, this.fDeferredDocumentImpl.createDeferredEntity(s, null, null, null, this.fBaseURIStack.peek()));
            }
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        final String publicId = xmlResourceIdentifier.getPublicId();
        final String literalSystemId = xmlResourceIdentifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            if (s.startsWith("%")) {
                this.fInternalSubset.append("% ");
                this.fInternalSubset.append(s.substring(1));
            }
            else {
                this.fInternalSubset.append(s);
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
        if (s.startsWith("%")) {
            return;
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            if (entities.getNamedItem(s) == null) {
                final EntityImpl namedItem = (EntityImpl)this.fDocumentImpl.createEntity(s);
                namedItem.setPublicId(publicId);
                namedItem.setSystemId(literalSystemId);
                namedItem.setBaseURI(xmlResourceIdentifier.getBaseSystemId());
                entities.setNamedItem(namedItem);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean b = false;
            for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
                if (this.fDeferredDocumentImpl.getNodeType(i, false) == 6 && this.fDeferredDocumentImpl.getNodeName(i, false).equals(s)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, this.fDeferredDocumentImpl.createDeferredEntity(s, publicId, literalSystemId, null, xmlResourceIdentifier.getBaseSystemId()));
            }
        }
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (augmentations != null && this.fInternalSubset != null && !this.fInDTDExternalSubset && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
            this.fInternalSubset.append(s).append(";\n");
        }
        this.fBaseURIStack.push(xmlResourceIdentifier.getExpandedSystemId());
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        this.fBaseURIStack.pop();
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String notationName, final Augmentations augmentations) throws XNIException {
        final String publicId = xmlResourceIdentifier.getPublicId();
        final String literalSystemId = xmlResourceIdentifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ENTITY ");
            this.fInternalSubset.append(s);
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
            this.fInternalSubset.append(notationName);
            this.fInternalSubset.append(">\n");
        }
        if (this.fDocumentType != null) {
            final NamedNodeMap entities = this.fDocumentType.getEntities();
            if (entities.getNamedItem(s) == null) {
                final EntityImpl namedItem = (EntityImpl)this.fDocumentImpl.createEntity(s);
                namedItem.setPublicId(publicId);
                namedItem.setSystemId(literalSystemId);
                namedItem.setNotationName(notationName);
                namedItem.setBaseURI(xmlResourceIdentifier.getBaseSystemId());
                entities.setNamedItem(namedItem);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean b = false;
            for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
                if (this.fDeferredDocumentImpl.getNodeType(i, false) == 6 && this.fDeferredDocumentImpl.getNodeName(i, false).equals(s)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, this.fDeferredDocumentImpl.createDeferredEntity(s, publicId, literalSystemId, notationName, xmlResourceIdentifier.getBaseSystemId()));
            }
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        final String publicId = xmlResourceIdentifier.getPublicId();
        final String literalSystemId = xmlResourceIdentifier.getLiteralSystemId();
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!NOTATION ");
            this.fInternalSubset.append(s);
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
            if (notations.getNamedItem(s) == null) {
                final NotationImpl namedItem = (NotationImpl)this.fDocumentImpl.createNotation(s);
                namedItem.setPublicId(publicId);
                namedItem.setSystemId(literalSystemId);
                namedItem.setBaseURI(xmlResourceIdentifier.getBaseSystemId());
                notations.setNamedItem(namedItem);
            }
        }
        if (this.fDocumentTypeIndex != -1) {
            boolean b = false;
            for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getPrevSibling(i, false)) {
                if (this.fDeferredDocumentImpl.getNodeType(i, false) == 12 && this.fDeferredDocumentImpl.getNodeName(i, false).equals(s)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, this.fDeferredDocumentImpl.createDeferredNotation(s, publicId, literalSystemId, xmlResourceIdentifier.getBaseSystemId()));
            }
        }
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ELEMENT ");
            this.fInternalSubset.append(s);
            this.fInternalSubset.append(' ');
            this.fInternalSubset.append(s2);
            this.fInternalSubset.append(">\n");
        }
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
            this.fInternalSubset.append("<!ATTLIST ");
            this.fInternalSubset.append(s);
            this.fInternalSubset.append(' ');
            this.fInternalSubset.append(s2);
            this.fInternalSubset.append(' ');
            if (s3.equals("ENUMERATION")) {
                this.fInternalSubset.append('(');
                for (int i = 0; i < array.length; ++i) {
                    if (i > 0) {
                        this.fInternalSubset.append('|');
                    }
                    this.fInternalSubset.append(array[i]);
                }
                this.fInternalSubset.append(')');
            }
            else {
                this.fInternalSubset.append(s3);
            }
            if (s4 != null) {
                this.fInternalSubset.append(' ');
                this.fInternalSubset.append(s4);
            }
            if (xmlString != null) {
                this.fInternalSubset.append(" '");
                for (int j = 0; j < xmlString.length; ++j) {
                    final char c = xmlString.ch[xmlString.offset + j];
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
            if (xmlString != null) {
                int n = this.fDeferredDocumentImpl.lookupElementDefinition(s);
                if (n == -1) {
                    n = this.fDeferredDocumentImpl.createDeferredElementDefinition(s);
                    this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, n);
                }
                final boolean fNamespaceAware = this.fNamespaceAware;
                String s5 = null;
                if (fNamespaceAware) {
                    if (s2.startsWith("xmlns:") || s2.equals("xmlns")) {
                        s5 = NamespaceContext.XMLNS_URI;
                    }
                    else if (s2.startsWith("xml:")) {
                        s5 = NamespaceContext.XML_URI;
                    }
                }
                final int deferredAttribute = this.fDeferredDocumentImpl.createDeferredAttribute(s2, s5, xmlString.toString(), false);
                if ("ID".equals(s3)) {
                    this.fDeferredDocumentImpl.setIdAttribute(deferredAttribute);
                }
                this.fDeferredDocumentImpl.appendChild(n, deferredAttribute);
            }
        }
        else if (this.fDocumentImpl != null && xmlString != null) {
            ElementDefinitionImpl elementDefinition = (ElementDefinitionImpl)((DocumentTypeImpl)this.fDocumentType).getElements().getNamedItem(s);
            if (elementDefinition == null) {
                elementDefinition = this.fDocumentImpl.createElementDefinition(s);
                ((DocumentTypeImpl)this.fDocumentType).getElements().setNamedItem(elementDefinition);
            }
            final boolean fNamespaceAware2 = this.fNamespaceAware;
            AttrImpl attrImpl;
            if (fNamespaceAware2) {
                String s6 = null;
                if (s2.startsWith("xmlns:") || s2.equals("xmlns")) {
                    s6 = NamespaceContext.XMLNS_URI;
                }
                else if (s2.startsWith("xml:")) {
                    s6 = NamespaceContext.XML_URI;
                }
                attrImpl = (AttrImpl)this.fDocumentImpl.createAttributeNS(s6, s2);
            }
            else {
                attrImpl = (AttrImpl)this.fDocumentImpl.createAttribute(s2);
            }
            attrImpl.setValue(xmlString.toString());
            attrImpl.setSpecified(false);
            attrImpl.setIdAttribute("ID".equals(s3));
            if (fNamespaceAware2) {
                elementDefinition.getAttributes().setNamedItemNS(attrImpl);
            }
            else {
                elementDefinition.getAttributes().setNamedItem(attrImpl);
            }
        }
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
    }
    
    protected Element createElementNode(final QName qName) {
        Element element;
        if (this.fNamespaceAware) {
            if (this.fDocumentImpl != null) {
                element = this.fDocumentImpl.createElementNS(qName.uri, qName.rawname, qName.localpart);
            }
            else {
                element = this.fDocument.createElementNS(qName.uri, qName.rawname);
            }
        }
        else {
            element = this.fDocument.createElement(qName.rawname);
        }
        return element;
    }
    
    protected Attr createAttrNode(final QName qName) {
        Attr attr;
        if (this.fNamespaceAware) {
            if (this.fDocumentImpl != null) {
                attr = this.fDocumentImpl.createAttributeNS(qName.uri, qName.rawname, qName.localpart);
            }
            else {
                attr = this.fDocument.createAttributeNS(qName.uri, qName.rawname);
            }
        }
        else {
            attr = this.fDocument.createAttribute(qName.rawname);
        }
        return attr;
    }
    
    protected void setCharacterData(final boolean fFirstChunk) {
        this.fFirstChunk = fFirstChunk;
        final Node lastChild = this.fCurrentNode.getLastChild();
        if (lastChild != null) {
            if (this.fStringBuffer.length() > 0) {
                if (lastChild.getNodeType() == 3) {
                    if (this.fDocumentImpl != null) {
                        ((TextImpl)lastChild).replaceData(this.fStringBuffer.toString());
                    }
                    else {
                        ((Text)lastChild).setData(this.fStringBuffer.toString());
                    }
                }
                this.fStringBuffer.setLength(0);
            }
            if (this.fDOMFilter != null && !this.fInEntityRef && lastChild.getNodeType() == 3 && (this.fDOMFilter.getWhatToShow() & 0x4) != 0x0) {
                switch (this.fDOMFilter.acceptNode(lastChild)) {
                    case 4: {
                        throw Abort.INSTANCE;
                    }
                    case 2:
                    case 3: {
                        this.fCurrentNode.removeChild(lastChild);
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
        
        public Throwable fillInStackTrace() {
            return this;
        }
        
        static {
            INSTANCE = new Abort();
        }
    }
}
