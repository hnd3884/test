package org.apache.xerces.dom;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.UserDataHandler;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import java.util.Enumeration;
import org.w3c.dom.NamedNodeMap;
import java.util.WeakHashMap;
import org.w3c.dom.Notation;
import org.w3c.dom.Entity;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.DOMImplementationLS;
import org.apache.xerces.util.URI;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Attr;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import java.util.Hashtable;
import java.util.Map;
import org.w3c.dom.Document;

public class CoreDocumentImpl extends ParentNode implements Document
{
    static final long serialVersionUID = 0L;
    protected DocumentTypeImpl docType;
    protected ElementImpl docElement;
    transient NodeListCache fFreeNLCache;
    protected String encoding;
    protected String actualEncoding;
    protected String version;
    protected boolean standalone;
    protected String fDocumentURI;
    protected Map userData;
    protected Hashtable identifiers;
    transient DOMNormalizer domNormalizer;
    transient DOMConfigurationImpl fConfiguration;
    transient Object fXPathEvaluator;
    private static final int[] kidOK;
    protected int changes;
    protected boolean allowGrammarAccess;
    protected boolean errorChecking;
    protected boolean xmlVersionChanged;
    private int documentNumber;
    private int nodeCounter;
    private Map nodeTable;
    private boolean xml11Version;
    
    public CoreDocumentImpl() {
        this(false);
    }
    
    public CoreDocumentImpl(final boolean allowGrammarAccess) {
        super(null);
        this.domNormalizer = null;
        this.fConfiguration = null;
        this.fXPathEvaluator = null;
        this.changes = 0;
        this.errorChecking = true;
        this.xmlVersionChanged = false;
        this.documentNumber = 0;
        this.nodeCounter = 0;
        this.xml11Version = false;
        this.ownerDocument = this;
        this.allowGrammarAccess = allowGrammarAccess;
    }
    
    public CoreDocumentImpl(final DocumentType documentType) {
        this(documentType, false);
    }
    
    public CoreDocumentImpl(final DocumentType documentType, final boolean b) {
        this(b);
        if (documentType != null) {
            DocumentTypeImpl documentTypeImpl;
            try {
                documentTypeImpl = (DocumentTypeImpl)documentType;
            }
            catch (final ClassCastException ex) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            (documentTypeImpl.ownerDocument = this).appendChild(documentType);
        }
    }
    
    public final Document getOwnerDocument() {
        return null;
    }
    
    public short getNodeType() {
        return 9;
    }
    
    public String getNodeName() {
        return "#document";
    }
    
    public Node cloneNode(final boolean b) {
        final CoreDocumentImpl coreDocumentImpl = new CoreDocumentImpl();
        this.callUserDataHandlers(this, coreDocumentImpl, (short)1);
        this.cloneNode(coreDocumentImpl, b);
        return coreDocumentImpl;
    }
    
    protected void cloneNode(final CoreDocumentImpl coreDocumentImpl, final boolean b) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (b) {
            HashMap<Object, Object> hashMap = null;
            if (this.identifiers != null) {
                hashMap = new HashMap<Object, Object>();
                final Iterator iterator = this.identifiers.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry entry = (Map.Entry)iterator.next();
                    hashMap.put(entry.getValue(), entry.getKey());
                }
            }
            for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
                coreDocumentImpl.appendChild(coreDocumentImpl.importNode(childNode, true, true, hashMap));
            }
        }
        coreDocumentImpl.allowGrammarAccess = this.allowGrammarAccess;
        coreDocumentImpl.errorChecking = this.errorChecking;
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        final short nodeType = node.getNodeType();
        if (this.errorChecking) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if ((nodeType == 1 && this.docElement != null) || (nodeType == 10 && this.docType != null)) {
                throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
            }
        }
        if (node.getOwnerDocument() == null && node instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)node).ownerDocument = this;
        }
        super.insertBefore(node, node2);
        if (nodeType == 1) {
            this.docElement = (ElementImpl)node;
        }
        else if (nodeType == 10) {
            this.docType = (DocumentTypeImpl)node;
        }
        return node;
    }
    
    public Node removeChild(final Node node) throws DOMException {
        super.removeChild(node);
        final short nodeType = node.getNodeType();
        if (nodeType == 1) {
            this.docElement = null;
        }
        else if (nodeType == 10) {
            this.docType = null;
        }
        return node;
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        if (node.getOwnerDocument() == null && node instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)node).ownerDocument = this;
        }
        if (this.errorChecking && ((this.docType != null && node2.getNodeType() != 10 && node.getNodeType() == 10) || (this.docElement != null && node2.getNodeType() != 1 && node.getNodeType() == 1))) {
            throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
        }
        super.replaceChild(node, node2);
        final short nodeType = node2.getNodeType();
        if (nodeType == 1) {
            this.docElement = (ElementImpl)node;
        }
        else if (nodeType == 10) {
            this.docType = (DocumentTypeImpl)node;
        }
        return node2;
    }
    
    public String getTextContent() throws DOMException {
        return null;
    }
    
    public void setTextContent(final String s) throws DOMException {
    }
    
    public Object getFeature(final String s, final String s2) {
        final boolean b = s2 == null || s2.length() == 0;
        if (s.equalsIgnoreCase("+XPath") && (b || s2.equals("3.0"))) {
            if (this.fXPathEvaluator != null) {
                return this.fXPathEvaluator;
            }
            try {
                final Class providerClass = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true);
                final Constructor constructor = providerClass.getConstructor(Document.class);
                final Class[] interfaces = providerClass.getInterfaces();
                for (int i = 0; i < interfaces.length; ++i) {
                    if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                        return this.fXPathEvaluator = constructor.newInstance(this);
                    }
                }
                return null;
            }
            catch (final Exception ex) {
                return null;
            }
        }
        return super.getFeature(s, s2);
    }
    
    public Attr createAttribute(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new AttrImpl(this, s);
    }
    
    public CDATASection createCDATASection(final String s) throws DOMException {
        return new CDATASectionImpl(this, s);
    }
    
    public Comment createComment(final String s) {
        return new CommentImpl(this, s);
    }
    
    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(this);
    }
    
    public Element createElement(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new ElementImpl(this, s);
    }
    
    public EntityReference createEntityReference(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new EntityReferenceImpl(this, s);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String s, final String s2) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new ProcessingInstructionImpl(this, s, s2);
    }
    
    public Text createTextNode(final String s) {
        return new TextImpl(this, s);
    }
    
    public DocumentType getDoctype() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docType;
    }
    
    public Element getDocumentElement() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docElement;
    }
    
    public NodeList getElementsByTagName(final String s) {
        return new DeepNodeListImpl(this, s);
    }
    
    public DOMImplementation getImplementation() {
        return CoreDOMImplementationImpl.getDOMImplementation();
    }
    
    public void setErrorChecking(final boolean errorChecking) {
        this.errorChecking = errorChecking;
    }
    
    public void setStrictErrorChecking(final boolean errorChecking) {
        this.errorChecking = errorChecking;
    }
    
    public boolean getErrorChecking() {
        return this.errorChecking;
    }
    
    public boolean getStrictErrorChecking() {
        return this.errorChecking;
    }
    
    public String getInputEncoding() {
        return this.actualEncoding;
    }
    
    public void setInputEncoding(final String actualEncoding) {
        this.actualEncoding = actualEncoding;
    }
    
    public void setXmlEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setEncoding(final String xmlEncoding) {
        this.setXmlEncoding(xmlEncoding);
    }
    
    public String getXmlEncoding() {
        return this.encoding;
    }
    
    public String getEncoding() {
        return this.getXmlEncoding();
    }
    
    public void setXmlVersion(final String version) {
        if (version.equals("1.0") || version.equals("1.1")) {
            if (!this.getXmlVersion().equals(version)) {
                this.xmlVersionChanged = true;
                this.isNormalized(false);
                this.version = version;
            }
            if (this.getXmlVersion().equals("1.1")) {
                this.xml11Version = true;
            }
            else {
                this.xml11Version = false;
            }
            return;
        }
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    public void setVersion(final String xmlVersion) {
        this.setXmlVersion(xmlVersion);
    }
    
    public String getXmlVersion() {
        return (this.version == null) ? "1.0" : this.version;
    }
    
    public String getVersion() {
        return this.getXmlVersion();
    }
    
    public void setXmlStandalone(final boolean standalone) throws DOMException {
        this.standalone = standalone;
    }
    
    public void setStandalone(final boolean xmlStandalone) {
        this.setXmlStandalone(xmlStandalone);
    }
    
    public boolean getXmlStandalone() {
        return this.standalone;
    }
    
    public boolean getStandalone() {
        return this.getXmlStandalone();
    }
    
    public String getDocumentURI() {
        return this.fDocumentURI;
    }
    
    protected boolean canRenameElements(final String s, final String s2, final ElementImpl elementImpl) {
        return true;
    }
    
    public Node renameNode(final Node node, final String s, final String s2) throws DOMException {
        if (this.errorChecking && node.getOwnerDocument() != this && node != this) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
        }
        switch (node.getNodeType()) {
            case 1: {
                ElementImpl elementImpl = (ElementImpl)node;
                if (elementImpl instanceof ElementNSImpl) {
                    if (this.canRenameElements(s, s2, elementImpl)) {
                        ((ElementNSImpl)elementImpl).rename(s, s2);
                        this.callUserDataHandlers(elementImpl, null, (short)4);
                    }
                    else {
                        elementImpl = this.replaceRenameElement(elementImpl, s, s2);
                    }
                }
                else if (s == null && this.canRenameElements(null, s2, elementImpl)) {
                    elementImpl.rename(s2);
                    this.callUserDataHandlers(elementImpl, null, (short)4);
                }
                else {
                    elementImpl = this.replaceRenameElement(elementImpl, s, s2);
                }
                this.renamedElement((Element)node, elementImpl);
                return elementImpl;
            }
            case 2: {
                AttrImpl attrImpl = (AttrImpl)node;
                final Element ownerElement = attrImpl.getOwnerElement();
                if (ownerElement != null) {
                    ownerElement.removeAttributeNode(attrImpl);
                }
                if (node instanceof AttrNSImpl) {
                    ((AttrNSImpl)attrImpl).rename(s, s2);
                    if (ownerElement != null) {
                        ownerElement.setAttributeNodeNS(attrImpl);
                    }
                    this.callUserDataHandlers(attrImpl, null, (short)4);
                }
                else if (s == null) {
                    attrImpl.rename(s2);
                    if (ownerElement != null) {
                        ownerElement.setAttributeNode(attrImpl);
                    }
                    this.callUserDataHandlers(attrImpl, null, (short)4);
                }
                else {
                    final AttrNSImpl attributeNode = (AttrNSImpl)this.createAttributeNS(s, s2);
                    this.copyEventListeners(attrImpl, attributeNode);
                    final Hashtable removeUserDataTable = this.removeUserDataTable(attrImpl);
                    for (Node node2 = attrImpl.getFirstChild(); node2 != null; node2 = attrImpl.getFirstChild()) {
                        attrImpl.removeChild(node2);
                        attributeNode.appendChild(node2);
                    }
                    this.setUserDataTable(attributeNode, removeUserDataTable);
                    this.callUserDataHandlers(attrImpl, attributeNode, (short)4);
                    if (ownerElement != null) {
                        ownerElement.setAttributeNode(attributeNode);
                    }
                    attrImpl = attributeNode;
                }
                this.renamedAttrNode((Attr)node, attrImpl);
                return attrImpl;
            }
            default: {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
            }
        }
    }
    
    private ElementImpl replaceRenameElement(final ElementImpl elementImpl, final String s, final String s2) {
        final ElementNSImpl elementNSImpl = (ElementNSImpl)this.createElementNS(s, s2);
        this.copyEventListeners(elementImpl, elementNSImpl);
        final Hashtable removeUserDataTable = this.removeUserDataTable(elementImpl);
        final Node parentNode = elementImpl.getParentNode();
        final Node nextSibling = elementImpl.getNextSibling();
        if (parentNode != null) {
            parentNode.removeChild(elementImpl);
        }
        for (Node node = elementImpl.getFirstChild(); node != null; node = elementImpl.getFirstChild()) {
            elementImpl.removeChild(node);
            elementNSImpl.appendChild(node);
        }
        elementNSImpl.moveSpecifiedAttributes(elementImpl);
        this.setUserDataTable(elementNSImpl, removeUserDataTable);
        this.callUserDataHandlers(elementImpl, elementNSImpl, (short)4);
        if (parentNode != null) {
            parentNode.insertBefore(elementNSImpl, nextSibling);
        }
        return elementNSImpl;
    }
    
    public void normalizeDocument() {
        if (this.isNormalized() && !this.isNormalizeDocRequired()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.domNormalizer == null) {
            this.domNormalizer = new DOMNormalizer();
        }
        if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
        }
        else {
            this.fConfiguration.reset();
        }
        this.domNormalizer.normalizeDocument(this, this.fConfiguration);
        this.isNormalized(true);
        this.xmlVersionChanged = false;
    }
    
    public DOMConfiguration getDomConfig() {
        if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
        }
        return this.fConfiguration;
    }
    
    public String getBaseURI() {
        if (this.fDocumentURI != null && this.fDocumentURI.length() != 0) {
            try {
                return new URI(this.fDocumentURI).toString();
            }
            catch (final URI.MalformedURIException ex) {
                return null;
            }
        }
        return this.fDocumentURI;
    }
    
    public void setDocumentURI(final String fDocumentURI) {
        this.fDocumentURI = fDocumentURI;
    }
    
    public boolean getAsync() {
        return false;
    }
    
    public void setAsync(final boolean b) {
        if (b) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
    }
    
    public void abort() {
    }
    
    public boolean load(final String s) {
        return false;
    }
    
    public boolean loadXML(final String s) {
        return false;
    }
    
    public String saveXML(Node node) throws DOMException {
        if (this.errorChecking && node != null && this != node.getOwnerDocument()) {
            throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
        }
        final LSSerializer lsSerializer = ((DOMImplementationLS)DOMImplementationImpl.getDOMImplementation()).createLSSerializer();
        if (node == null) {
            node = this;
        }
        return lsSerializer.writeToString(node);
    }
    
    void setMutationEvents(final boolean b) {
    }
    
    boolean getMutationEvents() {
        return false;
    }
    
    public DocumentType createDocumentType(final String s, final String s2, final String s3) throws DOMException {
        return new DocumentTypeImpl(this, s, s2, s3);
    }
    
    public Entity createEntity(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new EntityImpl(this, s);
    }
    
    public Notation createNotation(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new NotationImpl(this, s);
    }
    
    public ElementDefinitionImpl createElementDefinition(final String s) throws DOMException {
        if (this.errorChecking && !isXMLName(s, this.xml11Version)) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
        return new ElementDefinitionImpl(this, s);
    }
    
    protected int getNodeNumber() {
        if (this.documentNumber == 0) {
            this.documentNumber = ((CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation()).assignDocumentNumber();
        }
        return this.documentNumber;
    }
    
    protected int getNodeNumber(final Node node) {
        int intValue;
        if (this.nodeTable == null) {
            this.nodeTable = new WeakHashMap();
            final int nodeCounter = this.nodeCounter - 1;
            this.nodeCounter = nodeCounter;
            intValue = nodeCounter;
            this.nodeTable.put(node, new Integer(intValue));
        }
        else {
            final Integer n = this.nodeTable.get(node);
            if (n == null) {
                final int nodeCounter2 = this.nodeCounter - 1;
                this.nodeCounter = nodeCounter2;
                intValue = nodeCounter2;
                this.nodeTable.put(node, new Integer(intValue));
            }
            else {
                intValue = n;
            }
        }
        return intValue;
    }
    
    public Node importNode(final Node node, final boolean b) throws DOMException {
        return this.importNode(node, b, false, null);
    }
    
    private Node importNode(final Node node, boolean b, final boolean b2, final HashMap hashMap) throws DOMException {
        Hashtable userDataRecord = null;
        if (node instanceof NodeImpl) {
            userDataRecord = ((NodeImpl)node).getUserDataRecord();
        }
        Object o = null;
        switch (node.getNodeType()) {
            case 1: {
                final boolean hasFeature = node.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
                Element element;
                if (!hasFeature || node.getLocalName() == null) {
                    element = this.createElement(node.getNodeName());
                }
                else {
                    element = this.createElementNS(node.getNamespaceURI(), node.getNodeName());
                }
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        if (attr.getSpecified() || b2) {
                            final Attr attr2 = (Attr)this.importNode(attr, true, b2, hashMap);
                            if (!hasFeature || attr.getLocalName() == null) {
                                element.setAttributeNode(attr2);
                            }
                            else {
                                element.setAttributeNodeNS(attr2);
                            }
                        }
                    }
                }
                if (hashMap != null) {
                    final Object value = hashMap.get(node);
                    if (value != null) {
                        if (this.identifiers == null) {
                            this.identifiers = new Hashtable();
                        }
                        this.identifiers.put(value, element);
                    }
                }
                o = element;
                break;
            }
            case 2: {
                if (node.getOwnerDocument().getImplementation().hasFeature("XML", "2.0")) {
                    if (node.getLocalName() == null) {
                        o = this.createAttribute(node.getNodeName());
                    }
                    else {
                        o = this.createAttributeNS(node.getNamespaceURI(), node.getNodeName());
                    }
                }
                else {
                    o = this.createAttribute(node.getNodeName());
                }
                if (node instanceof AttrImpl) {
                    final AttrImpl attrImpl = (AttrImpl)node;
                    if (attrImpl.hasStringValue()) {
                        ((AttrImpl)o).setValue(attrImpl.getValue());
                        b = false;
                        break;
                    }
                    b = true;
                    break;
                }
                else {
                    if (node.getFirstChild() == null) {
                        ((Node)o).setNodeValue(node.getNodeValue());
                        b = false;
                        break;
                    }
                    b = true;
                    break;
                }
                break;
            }
            case 3: {
                o = this.createTextNode(node.getNodeValue());
                break;
            }
            case 4: {
                o = this.createCDATASection(node.getNodeValue());
                break;
            }
            case 5: {
                o = this.createEntityReference(node.getNodeName());
                b = false;
                break;
            }
            case 6: {
                final Entity entity = (Entity)node;
                final EntityImpl entityImpl = (EntityImpl)this.createEntity(node.getNodeName());
                entityImpl.setPublicId(entity.getPublicId());
                entityImpl.setSystemId(entity.getSystemId());
                entityImpl.setNotationName(entity.getNotationName());
                entityImpl.isReadOnly(false);
                o = entityImpl;
                break;
            }
            case 7: {
                o = this.createProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 8: {
                o = this.createComment(node.getNodeValue());
                break;
            }
            case 10: {
                if (!b2) {
                    throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
                }
                final DocumentType documentType = (DocumentType)node;
                final DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)this.createDocumentType(documentType.getNodeName(), documentType.getPublicId(), documentType.getSystemId());
                documentTypeImpl.setInternalSubset(documentType.getInternalSubset());
                final NamedNodeMap entities = documentType.getEntities();
                final NamedNodeMap entities2 = documentTypeImpl.getEntities();
                if (entities != null) {
                    for (int j = 0; j < entities.getLength(); ++j) {
                        entities2.setNamedItem(this.importNode(entities.item(j), true, true, hashMap));
                    }
                }
                final NamedNodeMap notations = documentType.getNotations();
                final NamedNodeMap notations2 = documentTypeImpl.getNotations();
                if (notations != null) {
                    for (int k = 0; k < notations.getLength(); ++k) {
                        notations2.setNamedItem(this.importNode(notations.item(k), true, true, hashMap));
                    }
                }
                o = documentTypeImpl;
                break;
            }
            case 11: {
                o = this.createDocumentFragment();
                break;
            }
            case 12: {
                final Notation notation = (Notation)node;
                final NotationImpl notationImpl = (NotationImpl)this.createNotation(node.getNodeName());
                notationImpl.setPublicId(notation.getPublicId());
                notationImpl.setSystemId(notation.getSystemId());
                o = notationImpl;
                break;
            }
            default: {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
            }
        }
        if (userDataRecord != null) {
            this.callUserDataHandlers(node, (Node)o, (short)2, userDataRecord);
        }
        if (b) {
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                ((Node)o).appendChild(this.importNode(node2, true, b2, hashMap));
            }
        }
        if (((Node)o).getNodeType() == 6) {
            ((NodeImpl)o).setReadOnly(true, true);
        }
        return (Node)o;
    }
    
    public Node adoptNode(final Node node) {
        NodeImpl nodeImpl;
        try {
            nodeImpl = (NodeImpl)node;
        }
        catch (final ClassCastException ex) {
            return null;
        }
        if (node == null) {
            return null;
        }
        if (node != null && node.getOwnerDocument() != null) {
            final DOMImplementation implementation = this.getImplementation();
            final DOMImplementation implementation2 = node.getOwnerDocument().getImplementation();
            if (implementation != implementation2) {
                if (implementation instanceof DOMImplementationImpl && implementation2 instanceof DeferredDOMImplementationImpl) {
                    this.undeferChildren(nodeImpl);
                }
                else if (!(implementation instanceof DeferredDOMImplementationImpl) || !(implementation2 instanceof DOMImplementationImpl)) {
                    return null;
                }
            }
            else if (implementation2 instanceof DeferredDOMImplementationImpl) {
                this.undeferChildren(nodeImpl);
            }
        }
        Hashtable hashtable = null;
        switch (nodeImpl.getNodeType()) {
            case 2: {
                final AttrImpl attrImpl = (AttrImpl)nodeImpl;
                if (attrImpl.getOwnerElement() != null) {
                    attrImpl.getOwnerElement().removeAttributeNode(attrImpl);
                }
                attrImpl.isSpecified(true);
                hashtable = nodeImpl.getUserDataRecord();
                attrImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                    break;
                }
                break;
            }
            case 6:
            case 12: {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            case 9:
            case 10: {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
            }
            case 5: {
                hashtable = nodeImpl.getUserDataRecord();
                final Node parentNode = nodeImpl.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(node);
                }
                Node firstChild;
                while ((firstChild = nodeImpl.getFirstChild()) != null) {
                    nodeImpl.removeChild(firstChild);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                }
                if (this.docType == null) {
                    break;
                }
                final Node namedItem = this.docType.getEntities().getNamedItem(nodeImpl.getNodeName());
                if (namedItem == null) {
                    break;
                }
                for (Node node2 = namedItem.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    nodeImpl.appendChild(node2.cloneNode(true));
                }
                break;
            }
            case 1: {
                hashtable = nodeImpl.getUserDataRecord();
                final Node parentNode2 = nodeImpl.getParentNode();
                if (parentNode2 != null) {
                    parentNode2.removeChild(node);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                }
                ((ElementImpl)nodeImpl).reconcileDefaultAttributes();
                break;
            }
            default: {
                hashtable = nodeImpl.getUserDataRecord();
                final Node parentNode3 = nodeImpl.getParentNode();
                if (parentNode3 != null) {
                    parentNode3.removeChild(node);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                    break;
                }
                break;
            }
        }
        if (hashtable != null) {
            this.callUserDataHandlers(node, null, (short)5, hashtable);
        }
        return nodeImpl;
    }
    
    protected void undeferChildren(Node parentNode) {
        final Node node = parentNode;
        while (null != parentNode) {
            if (((NodeImpl)parentNode).needsSyncData()) {
                ((NodeImpl)parentNode).synchronizeData();
            }
            final NamedNodeMap attributes = parentNode.getAttributes();
            if (attributes != null) {
                for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                    this.undeferChildren(attributes.item(i));
                }
            }
            Node node2 = parentNode.getFirstChild();
            while (null == node2) {
                if (node.equals(parentNode)) {
                    break;
                }
                node2 = parentNode.getNextSibling();
                if (null != node2) {
                    continue;
                }
                parentNode = parentNode.getParentNode();
                if (null == parentNode || node.equals(parentNode)) {
                    node2 = null;
                    break;
                }
            }
            parentNode = node2;
        }
    }
    
    public Element getElementById(final String s) {
        return this.getIdentifier(s);
    }
    
    protected final void clearIdentifiers() {
        if (this.identifiers != null) {
            this.identifiers.clear();
        }
    }
    
    public void putIdentifier(final String s, final Element element) {
        if (element == null) {
            this.removeIdentifier(s);
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        this.identifiers.put(s, element);
    }
    
    public Element getIdentifier(final String s) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return null;
        }
        final Element element = this.identifiers.get(s);
        if (element != null) {
            for (Node node = element.getParentNode(); node != null; node = node.getParentNode()) {
                if (node == this) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public void removeIdentifier(final String s) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return;
        }
        this.identifiers.remove(s);
    }
    
    public Enumeration getIdentifiers() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        return this.identifiers.keys();
    }
    
    public Element createElementNS(final String s, final String s2) throws DOMException {
        return new ElementNSImpl(this, s, s2);
    }
    
    public Element createElementNS(final String s, final String s2, final String s3) throws DOMException {
        return new ElementNSImpl(this, s, s2, s3);
    }
    
    public Attr createAttributeNS(final String s, final String s2) throws DOMException {
        return new AttrNSImpl(this, s, s2);
    }
    
    public Attr createAttributeNS(final String s, final String s2, final String s3) throws DOMException {
        return new AttrNSImpl(this, s, s2, s3);
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        return new DeepNodeListImpl(this, s, s2);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final CoreDocumentImpl coreDocumentImpl = (CoreDocumentImpl)super.clone();
        coreDocumentImpl.docType = null;
        coreDocumentImpl.docElement = null;
        return coreDocumentImpl;
    }
    
    public static final boolean isXMLName(final String s, final boolean b) {
        if (s == null) {
            return false;
        }
        if (!b) {
            return XMLChar.isValidName(s);
        }
        return XML11Char.isXML11ValidName(s);
    }
    
    public static final boolean isValidQName(final String s, final String s2, final boolean b) {
        if (s2 == null) {
            return false;
        }
        boolean b2;
        if (!b) {
            b2 = ((s == null || XMLChar.isValidNCName(s)) && XMLChar.isValidNCName(s2));
        }
        else {
            b2 = ((s == null || XML11Char.isXML11ValidNCName(s)) && XML11Char.isXML11ValidNCName(s2));
        }
        return b2;
    }
    
    protected boolean isKidOK(final Node node, final Node node2) {
        if (this.allowGrammarAccess && node.getNodeType() == 10) {
            return node2.getNodeType() == 1;
        }
        return 0x0 != (CoreDocumentImpl.kidOK[node.getNodeType()] & 1 << node2.getNodeType());
    }
    
    protected void changed() {
        ++this.changes;
    }
    
    protected int changes() {
        return this.changes;
    }
    
    NodeListCache getNodeListCache(final ParentNode fOwner) {
        if (this.fFreeNLCache == null) {
            return new NodeListCache(fOwner);
        }
        final NodeListCache fFreeNLCache = this.fFreeNLCache;
        this.fFreeNLCache = this.fFreeNLCache.next;
        fFreeNLCache.fChild = null;
        fFreeNLCache.fChildIndex = -1;
        fFreeNLCache.fLength = -1;
        if (fFreeNLCache.fOwner != null) {
            fFreeNLCache.fOwner.fNodeListCache = null;
        }
        fFreeNLCache.fOwner = fOwner;
        return fFreeNLCache;
    }
    
    void freeNodeListCache(final NodeListCache fFreeNLCache) {
        fFreeNLCache.next = this.fFreeNLCache;
        this.fFreeNLCache = fFreeNLCache;
    }
    
    public Object setUserData(final Node node, final String s, final Object o, final UserDataHandler userDataHandler) {
        if (o == null) {
            if (this.userData != null) {
                final Hashtable hashtable = this.userData.get(node);
                if (hashtable != null) {
                    final Object remove = hashtable.remove(s);
                    if (remove != null) {
                        return ((UserDataRecord)remove).fData;
                    }
                }
            }
            return null;
        }
        Hashtable hashtable2;
        if (this.userData == null) {
            this.userData = new WeakHashMap();
            hashtable2 = new Hashtable();
            this.userData.put(node, hashtable2);
        }
        else {
            hashtable2 = this.userData.get(node);
            if (hashtable2 == null) {
                hashtable2 = new Hashtable();
                this.userData.put(node, hashtable2);
            }
        }
        final UserDataRecord put = hashtable2.put(s, new UserDataRecord(o, userDataHandler));
        if (put != null) {
            return put.fData;
        }
        return null;
    }
    
    public Object getUserData(final Node node, final String s) {
        if (this.userData == null) {
            return null;
        }
        final Hashtable hashtable = this.userData.get(node);
        if (hashtable == null) {
            return null;
        }
        final Object value = hashtable.get(s);
        if (value != null) {
            return ((UserDataRecord)value).fData;
        }
        return null;
    }
    
    protected Hashtable getUserDataRecord(final Node node) {
        if (this.userData == null) {
            return null;
        }
        final Hashtable hashtable = this.userData.get(node);
        if (hashtable == null) {
            return null;
        }
        return hashtable;
    }
    
    Hashtable removeUserDataTable(final Node node) {
        if (this.userData == null) {
            return null;
        }
        return this.userData.get(node);
    }
    
    void setUserDataTable(final Node node, final Hashtable hashtable) {
        if (this.userData == null) {
            this.userData = new WeakHashMap();
        }
        if (hashtable != null) {
            this.userData.put(node, hashtable);
        }
    }
    
    protected void callUserDataHandlers(final Node node, final Node node2, final short n) {
        if (this.userData == null) {
            return;
        }
        if (node instanceof NodeImpl) {
            final Hashtable userDataRecord = ((NodeImpl)node).getUserDataRecord();
            if (userDataRecord == null || userDataRecord.isEmpty()) {
                return;
            }
            this.callUserDataHandlers(node, node2, n, userDataRecord);
        }
    }
    
    void callUserDataHandlers(final Node node, final Node node2, final short n, final Hashtable hashtable) {
        if (hashtable == null || hashtable.isEmpty()) {
            return;
        }
        final Iterator iterator = hashtable.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            final String s = (String)entry.getKey();
            final UserDataRecord userDataRecord = (UserDataRecord)entry.getValue();
            if (userDataRecord.fHandler != null) {
                userDataRecord.fHandler.handle(n, s, userDataRecord.fData, node, node2);
            }
        }
    }
    
    protected final void checkNamespaceWF(final String s, final int n, final int n2) {
        if (!this.errorChecking) {
            return;
        }
        if (n == 0 || n == s.length() - 1 || n2 != n) {
            throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
        }
    }
    
    protected final void checkDOMNSErr(final String s, final String s2) {
        if (this.errorChecking) {
            if (s2 == null) {
                throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
            }
            if (s.equals("xml") && !s2.equals(NamespaceContext.XML_URI)) {
                throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
            }
            if ((s.equals("xmlns") && !s2.equals(NamespaceContext.XMLNS_URI)) || (!s.equals("xmlns") && s2.equals(NamespaceContext.XMLNS_URI))) {
                throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
            }
        }
    }
    
    protected final void checkQName(final String s, final String s2) {
        if (!this.errorChecking) {
            return;
        }
        boolean b;
        if (!this.xml11Version) {
            b = ((s == null || XMLChar.isValidNCName(s)) && XMLChar.isValidNCName(s2));
        }
        else {
            b = ((s == null || XML11Char.isXML11ValidNCName(s)) && XML11Char.isXML11ValidNCName(s2));
        }
        if (!b) {
            throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
        }
    }
    
    boolean isXML11Version() {
        return this.xml11Version;
    }
    
    boolean isNormalizeDocRequired() {
        return true;
    }
    
    boolean isXMLVersionChanged() {
        return this.xmlVersionChanged;
    }
    
    protected void setUserData(final NodeImpl nodeImpl, final Object o) {
        this.setUserData(nodeImpl, "XERCES1DOMUSERDATA", o, null);
    }
    
    protected Object getUserData(final NodeImpl nodeImpl) {
        return this.getUserData(nodeImpl, "XERCES1DOMUSERDATA");
    }
    
    protected void addEventListener(final NodeImpl nodeImpl, final String s, final EventListener eventListener, final boolean b) {
    }
    
    protected void removeEventListener(final NodeImpl nodeImpl, final String s, final EventListener eventListener, final boolean b) {
    }
    
    protected void copyEventListeners(final NodeImpl nodeImpl, final NodeImpl nodeImpl2) {
    }
    
    protected boolean dispatchEvent(final NodeImpl nodeImpl, final Event event) {
        return false;
    }
    
    void replacedText(final CharacterDataImpl characterDataImpl) {
    }
    
    void deletedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
    }
    
    void insertedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
    }
    
    void modifyingCharacterData(final NodeImpl nodeImpl, final boolean b) {
    }
    
    void modifiedCharacterData(final NodeImpl nodeImpl, final String s, final String s2, final boolean b) {
    }
    
    void insertingNode(final NodeImpl nodeImpl, final boolean b) {
    }
    
    void insertedNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
    }
    
    void removingNode(final NodeImpl nodeImpl, final NodeImpl nodeImpl2, final boolean b) {
    }
    
    void removedNode(final NodeImpl nodeImpl, final boolean b) {
    }
    
    void replacingNode(final NodeImpl nodeImpl) {
    }
    
    void replacedNode(final NodeImpl nodeImpl) {
    }
    
    void replacingData(final NodeImpl nodeImpl) {
    }
    
    void replacedCharacterData(final NodeImpl nodeImpl, final String s, final String s2) {
    }
    
    void modifiedAttrValue(final AttrImpl attrImpl, final String s) {
    }
    
    void setAttrNode(final AttrImpl attrImpl, final AttrImpl attrImpl2) {
    }
    
    void removedAttrNode(final AttrImpl attrImpl, final NodeImpl nodeImpl, final String s) {
    }
    
    void renamedAttrNode(final Attr attr, final Attr attr2) {
    }
    
    void renamedElement(final Element element, final Element element2) {
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.userData != null) {
            this.userData = new WeakHashMap(this.userData);
        }
        if (this.nodeTable != null) {
            this.nodeTable = new WeakHashMap(this.nodeTable);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Map userData = this.userData;
        final Map nodeTable = this.nodeTable;
        try {
            if (userData != null) {
                this.userData = new Hashtable(userData);
            }
            if (nodeTable != null) {
                this.nodeTable = new Hashtable(nodeTable);
            }
            objectOutputStream.defaultWriteObject();
        }
        finally {
            this.userData = userData;
            this.nodeTable = nodeTable;
        }
    }
    
    static {
        (kidOK = new int[13])[9] = 1410;
        final int[] kidOK2 = CoreDocumentImpl.kidOK;
        final int n = 11;
        final int[] kidOK3 = CoreDocumentImpl.kidOK;
        final int n2 = 6;
        final int[] kidOK4 = CoreDocumentImpl.kidOK;
        final int n3 = 5;
        final int[] kidOK5 = CoreDocumentImpl.kidOK;
        final int n4 = 1;
        final int n5 = 442;
        kidOK4[n3] = (kidOK5[n4] = n5);
        kidOK2[n] = (kidOK3[n2] = n5);
        CoreDocumentImpl.kidOK[2] = 40;
        final int[] kidOK6 = CoreDocumentImpl.kidOK;
        final int n6 = 10;
        final int[] kidOK7 = CoreDocumentImpl.kidOK;
        final int n7 = 7;
        final int[] kidOK8 = CoreDocumentImpl.kidOK;
        final int n8 = 8;
        final int[] kidOK9 = CoreDocumentImpl.kidOK;
        final int n9 = 3;
        final int[] kidOK10 = CoreDocumentImpl.kidOK;
        final int n10 = 4;
        final int[] kidOK11 = CoreDocumentImpl.kidOK;
        final int n11 = 12;
        final int n12 = 0;
        kidOK10[n10] = (kidOK11[n11] = n12);
        kidOK8[n8] = (kidOK9[n9] = n12);
        kidOK6[n6] = (kidOK7[n7] = n12);
    }
}
