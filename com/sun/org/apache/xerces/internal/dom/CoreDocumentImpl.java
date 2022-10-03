package com.sun.org.apache.xerces.internal.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.UserDataHandler;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Notation;
import org.w3c.dom.Entity;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.DOMImplementationLS;
import com.sun.org.apache.xerces.internal.util.URI;
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
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.Iterator;
import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.io.ObjectStreamField;
import org.w3c.dom.Node;
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
    private Map<Node, Map<String, UserDataRecord>> nodeUserData;
    protected Map<String, Node> identifiers;
    transient DOMNormalizer domNormalizer;
    transient DOMConfigurationImpl fConfiguration;
    transient Object fXPathEvaluator;
    private static final int[] kidOK;
    protected int changes;
    protected boolean allowGrammarAccess;
    protected boolean errorChecking;
    protected boolean ancestorChecking;
    protected boolean xmlVersionChanged;
    private int documentNumber;
    private int nodeCounter;
    private Map<Node, Integer> nodeTable;
    private boolean xml11Version;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public CoreDocumentImpl() {
        this(false);
    }
    
    public CoreDocumentImpl(final boolean grammarAccess) {
        super(null);
        this.domNormalizer = null;
        this.fConfiguration = null;
        this.fXPathEvaluator = null;
        this.changes = 0;
        this.errorChecking = true;
        this.ancestorChecking = true;
        this.xmlVersionChanged = false;
        this.documentNumber = 0;
        this.nodeCounter = 0;
        this.xml11Version = false;
        this.ownerDocument = this;
        this.allowGrammarAccess = grammarAccess;
        final String systemProp = SecuritySupport.getSystemProperty("http://java.sun.com/xml/dom/properties/ancestor-check");
        if (systemProp != null && systemProp.equalsIgnoreCase("false")) {
            this.ancestorChecking = false;
        }
    }
    
    public CoreDocumentImpl(final DocumentType doctype) {
        this(doctype, false);
    }
    
    public CoreDocumentImpl(final DocumentType doctype, final boolean grammarAccess) {
        this(grammarAccess);
        if (doctype != null) {
            DocumentTypeImpl doctypeImpl;
            try {
                doctypeImpl = (DocumentTypeImpl)doctype;
            }
            catch (final ClassCastException e) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException((short)4, msg);
            }
            (doctypeImpl.ownerDocument = this).appendChild(doctype);
        }
    }
    
    @Override
    public final Document getOwnerDocument() {
        return null;
    }
    
    @Override
    public short getNodeType() {
        return 9;
    }
    
    @Override
    public String getNodeName() {
        return "#document";
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final CoreDocumentImpl newdoc = new CoreDocumentImpl();
        this.callUserDataHandlers(this, newdoc, (short)1);
        this.cloneNode(newdoc, deep);
        return newdoc;
    }
    
    protected void cloneNode(final CoreDocumentImpl newdoc, final boolean deep) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (deep) {
            Map<Node, String> reversedIdentifiers = null;
            if (this.identifiers != null) {
                reversedIdentifiers = new HashMap<Node, String>(this.identifiers.size());
                for (final String elementId : this.identifiers.keySet()) {
                    reversedIdentifiers.put(this.identifiers.get(elementId), elementId);
                }
            }
            for (ChildNode kid = this.firstChild; kid != null; kid = kid.nextSibling) {
                newdoc.appendChild(newdoc.importNode(kid, true, true, reversedIdentifiers));
            }
        }
        newdoc.allowGrammarAccess = this.allowGrammarAccess;
        newdoc.errorChecking = this.errorChecking;
    }
    
    @Override
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        final int type = newChild.getNodeType();
        if (this.errorChecking && ((type == 1 && this.docElement != null) || (type == 10 && this.docType != null))) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
            throw new DOMException((short)3, msg);
        }
        if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)newChild).ownerDocument = this;
        }
        super.insertBefore(newChild, refChild);
        if (type == 1) {
            this.docElement = (ElementImpl)newChild;
        }
        else if (type == 10) {
            this.docType = (DocumentTypeImpl)newChild;
        }
        return newChild;
    }
    
    @Override
    public Node removeChild(final Node oldChild) throws DOMException {
        super.removeChild(oldChild);
        final int type = oldChild.getNodeType();
        if (type == 1) {
            this.docElement = null;
        }
        else if (type == 10) {
            this.docType = null;
        }
        return oldChild;
    }
    
    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)newChild).ownerDocument = this;
        }
        if (this.errorChecking && ((this.docType != null && oldChild.getNodeType() != 10 && newChild.getNodeType() == 10) || (this.docElement != null && oldChild.getNodeType() != 1 && newChild.getNodeType() == 1))) {
            throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
        }
        super.replaceChild(newChild, oldChild);
        final int type = oldChild.getNodeType();
        if (type == 1) {
            this.docElement = (ElementImpl)newChild;
        }
        else if (type == 10) {
            this.docType = (DocumentTypeImpl)newChild;
        }
        return oldChild;
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return null;
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        final boolean anyVersion = version == null || version.length() == 0;
        if (feature.equalsIgnoreCase("+XPath") && (anyVersion || version.equals("3.0"))) {
            if (this.fXPathEvaluator != null) {
                return this.fXPathEvaluator;
            }
            try {
                final Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
                final Constructor xpathClassConstr = xpathClass.getConstructor(Document.class);
                final Class[] interfaces = xpathClass.getInterfaces();
                for (int i = 0; i < interfaces.length; ++i) {
                    if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                        return this.fXPathEvaluator = xpathClassConstr.newInstance(this);
                    }
                }
                return null;
            }
            catch (final Exception e) {
                return null;
            }
        }
        return super.getFeature(feature, version);
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new AttrImpl(this, name);
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        return new CDATASectionImpl(this, data);
    }
    
    @Override
    public Comment createComment(final String data) {
        return new CommentImpl(this, data);
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(this);
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        if (this.errorChecking && !isXMLName(tagName, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new ElementImpl(this, tagName);
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new EntityReferenceImpl(this, name);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        if (this.errorChecking && !isXMLName(target, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new ProcessingInstructionImpl(this, target, data);
    }
    
    @Override
    public Text createTextNode(final String data) {
        return new TextImpl(this, data);
    }
    
    @Override
    public DocumentType getDoctype() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docType;
    }
    
    @Override
    public Element getDocumentElement() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docElement;
    }
    
    @Override
    public NodeList getElementsByTagName(final String tagname) {
        return new DeepNodeListImpl(this, tagname);
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return CoreDOMImplementationImpl.getDOMImplementation();
    }
    
    public void setErrorChecking(final boolean check) {
        this.errorChecking = check;
    }
    
    @Override
    public void setStrictErrorChecking(final boolean check) {
        this.errorChecking = check;
    }
    
    public boolean getErrorChecking() {
        return this.errorChecking;
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        return this.errorChecking;
    }
    
    @Override
    public String getInputEncoding() {
        return this.actualEncoding;
    }
    
    public void setInputEncoding(final String value) {
        this.actualEncoding = value;
    }
    
    public void setXmlEncoding(final String value) {
        this.encoding = value;
    }
    
    @Deprecated
    public void setEncoding(final String value) {
        this.setXmlEncoding(value);
    }
    
    @Override
    public String getXmlEncoding() {
        return this.encoding;
    }
    
    @Deprecated
    public String getEncoding() {
        return this.getXmlEncoding();
    }
    
    @Override
    public void setXmlVersion(final String value) {
        if (value.equals("1.0") || value.equals("1.1")) {
            if (!this.getXmlVersion().equals(value)) {
                this.xmlVersionChanged = true;
                this.isNormalized(false);
                this.version = value;
            }
            if (this.getXmlVersion().equals("1.1")) {
                this.xml11Version = true;
            }
            else {
                this.xml11Version = false;
            }
            return;
        }
        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, msg);
    }
    
    @Deprecated
    public void setVersion(final String value) {
        this.setXmlVersion(value);
    }
    
    @Override
    public String getXmlVersion() {
        return (this.version == null) ? "1.0" : this.version;
    }
    
    @Deprecated
    public String getVersion() {
        return this.getXmlVersion();
    }
    
    @Override
    public void setXmlStandalone(final boolean value) throws DOMException {
        this.standalone = value;
    }
    
    @Deprecated
    public void setStandalone(final boolean value) {
        this.setXmlStandalone(value);
    }
    
    @Override
    public boolean getXmlStandalone() {
        return this.standalone;
    }
    
    @Deprecated
    public boolean getStandalone() {
        return this.getXmlStandalone();
    }
    
    @Override
    public String getDocumentURI() {
        return this.fDocumentURI;
    }
    
    @Override
    public Node renameNode(final Node n, final String namespaceURI, final String name) throws DOMException {
        if (this.errorChecking && n.getOwnerDocument() != this && n != this) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException((short)4, msg);
        }
        switch (n.getNodeType()) {
            case 1: {
                ElementImpl el = (ElementImpl)n;
                if (el instanceof ElementNSImpl) {
                    ((ElementNSImpl)el).rename(namespaceURI, name);
                    this.callUserDataHandlers(el, null, (short)4);
                }
                else if (namespaceURI == null) {
                    if (this.errorChecking) {
                        final int colon1 = name.indexOf(58);
                        if (colon1 != -1) {
                            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                            throw new DOMException((short)14, msg2);
                        }
                        if (!isXMLName(name, this.xml11Version)) {
                            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                            throw new DOMException((short)5, msg2);
                        }
                    }
                    el.rename(name);
                    this.callUserDataHandlers(el, null, (short)4);
                }
                else {
                    final ElementNSImpl nel = new ElementNSImpl(this, namespaceURI, name);
                    this.copyEventListeners(el, nel);
                    final Map<String, UserDataRecord> data = this.removeUserDataTable(el);
                    final Node parent = el.getParentNode();
                    final Node nextSib = el.getNextSibling();
                    if (parent != null) {
                        parent.removeChild(el);
                    }
                    for (Node child = el.getFirstChild(); child != null; child = el.getFirstChild()) {
                        el.removeChild(child);
                        nel.appendChild(child);
                    }
                    nel.moveSpecifiedAttributes(el);
                    this.setUserDataTable(nel, data);
                    this.callUserDataHandlers(el, nel, (short)4);
                    if (parent != null) {
                        parent.insertBefore(nel, nextSib);
                    }
                    el = nel;
                }
                this.renamedElement((Element)n, el);
                return el;
            }
            case 2: {
                AttrImpl at = (AttrImpl)n;
                final Element el2 = at.getOwnerElement();
                if (el2 != null) {
                    el2.removeAttributeNode(at);
                }
                if (n instanceof AttrNSImpl) {
                    ((AttrNSImpl)at).rename(namespaceURI, name);
                    if (el2 != null) {
                        el2.setAttributeNodeNS(at);
                    }
                    this.callUserDataHandlers(at, null, (short)4);
                }
                else if (namespaceURI == null) {
                    at.rename(name);
                    if (el2 != null) {
                        el2.setAttributeNode(at);
                    }
                    this.callUserDataHandlers(at, null, (short)4);
                }
                else {
                    final AttrNSImpl nat = new AttrNSImpl(this, namespaceURI, name);
                    this.copyEventListeners(at, nat);
                    final Map<String, UserDataRecord> data2 = this.removeUserDataTable(at);
                    for (Node child2 = at.getFirstChild(); child2 != null; child2 = at.getFirstChild()) {
                        at.removeChild(child2);
                        nat.appendChild(child2);
                    }
                    this.setUserDataTable(nat, data2);
                    this.callUserDataHandlers(at, nat, (short)4);
                    if (el2 != null) {
                        el2.setAttributeNode(nat);
                    }
                    at = nat;
                }
                this.renamedAttrNode((Attr)n, at);
                return at;
            }
            default: {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException((short)9, msg);
            }
        }
    }
    
    @Override
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
    
    @Override
    public DOMConfiguration getDomConfig() {
        if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
        }
        return this.fConfiguration;
    }
    
    @Override
    public String getBaseURI() {
        if (this.fDocumentURI != null && this.fDocumentURI.length() != 0) {
            try {
                return new URI(this.fDocumentURI).toString();
            }
            catch (final URI.MalformedURIException e) {
                return null;
            }
        }
        return this.fDocumentURI;
    }
    
    @Override
    public void setDocumentURI(final String documentURI) {
        this.fDocumentURI = documentURI;
    }
    
    public boolean getAsync() {
        return false;
    }
    
    public void setAsync(final boolean async) {
        if (async) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
    }
    
    public void abort() {
    }
    
    public boolean load(final String uri) {
        return false;
    }
    
    public boolean loadXML(final String source) {
        return false;
    }
    
    public String saveXML(Node node) throws DOMException {
        if (this.errorChecking && node != null && this != node.getOwnerDocument()) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException((short)4, msg);
        }
        final DOMImplementationLS domImplLS = (DOMImplementationLS)DOMImplementationImpl.getDOMImplementation();
        final LSSerializer xmlWriter = domImplLS.createLSSerializer();
        if (node == null) {
            node = this;
        }
        return xmlWriter.writeToString(node);
    }
    
    void setMutationEvents(final boolean set) {
    }
    
    boolean getMutationEvents() {
        return false;
    }
    
    public DocumentType createDocumentType(final String qualifiedName, final String publicID, final String systemID) throws DOMException {
        return new DocumentTypeImpl(this, qualifiedName, publicID, systemID);
    }
    
    public Entity createEntity(final String name) throws DOMException {
        if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new EntityImpl(this, name);
    }
    
    public Notation createNotation(final String name) throws DOMException {
        if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new NotationImpl(this, name);
    }
    
    public ElementDefinitionImpl createElementDefinition(final String name) throws DOMException {
        if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
        }
        return new ElementDefinitionImpl(this, name);
    }
    
    @Override
    protected int getNodeNumber() {
        if (this.documentNumber == 0) {
            final CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.documentNumber = cd.assignDocumentNumber();
        }
        return this.documentNumber;
    }
    
    protected int getNodeNumber(final Node node) {
        int num;
        if (this.nodeTable == null) {
            this.nodeTable = new HashMap<Node, Integer>();
            final int nodeCounter = this.nodeCounter - 1;
            this.nodeCounter = nodeCounter;
            num = nodeCounter;
            this.nodeTable.put(node, new Integer(num));
        }
        else {
            final Integer n = this.nodeTable.get(node);
            if (n == null) {
                final int nodeCounter2 = this.nodeCounter - 1;
                this.nodeCounter = nodeCounter2;
                num = nodeCounter2;
                this.nodeTable.put(node, num);
            }
            else {
                num = n;
            }
        }
        return num;
    }
    
    @Override
    public Node importNode(final Node source, final boolean deep) throws DOMException {
        return this.importNode(source, deep, false, null);
    }
    
    private Node importNode(final Node source, boolean deep, final boolean cloningDoc, final Map<Node, String> reversedIdentifiers) throws DOMException {
        Node newnode = null;
        Map<String, UserDataRecord> userData = null;
        if (source instanceof NodeImpl) {
            userData = ((NodeImpl)source).getUserDataRecord();
        }
        final int type = source.getNodeType();
        switch (type) {
            case 1: {
                final boolean domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
                Element newElement;
                if (!domLevel20 || source.getLocalName() == null) {
                    newElement = this.createElement(source.getNodeName());
                }
                else {
                    newElement = this.createElementNS(source.getNamespaceURI(), source.getNodeName());
                }
                final NamedNodeMap sourceAttrs = source.getAttributes();
                if (sourceAttrs != null) {
                    for (int length = sourceAttrs.getLength(), index = 0; index < length; ++index) {
                        final Attr attr = (Attr)sourceAttrs.item(index);
                        if (attr.getSpecified() || cloningDoc) {
                            final Attr newAttr = (Attr)this.importNode(attr, true, cloningDoc, reversedIdentifiers);
                            if (!domLevel20 || attr.getLocalName() == null) {
                                newElement.setAttributeNode(newAttr);
                            }
                            else {
                                newElement.setAttributeNodeNS(newAttr);
                            }
                        }
                    }
                }
                if (reversedIdentifiers != null) {
                    final String elementId = reversedIdentifiers.get(source);
                    if (elementId != null) {
                        if (this.identifiers == null) {
                            this.identifiers = new HashMap<String, Node>();
                        }
                        this.identifiers.put(elementId, newElement);
                    }
                }
                newnode = newElement;
                break;
            }
            case 2: {
                if (source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0")) {
                    if (source.getLocalName() == null) {
                        newnode = this.createAttribute(source.getNodeName());
                    }
                    else {
                        newnode = this.createAttributeNS(source.getNamespaceURI(), source.getNodeName());
                    }
                }
                else {
                    newnode = this.createAttribute(source.getNodeName());
                }
                if (source instanceof AttrImpl) {
                    final AttrImpl attr2 = (AttrImpl)source;
                    if (attr2.hasStringValue()) {
                        final AttrImpl newattr = (AttrImpl)newnode;
                        newattr.setValue(attr2.getValue());
                        deep = false;
                    }
                    else {
                        deep = true;
                    }
                    break;
                }
                if (source.getFirstChild() == null) {
                    newnode.setNodeValue(source.getNodeValue());
                    deep = false;
                    break;
                }
                deep = true;
                break;
            }
            case 3: {
                newnode = this.createTextNode(source.getNodeValue());
                break;
            }
            case 4: {
                newnode = this.createCDATASection(source.getNodeValue());
                break;
            }
            case 5: {
                newnode = this.createEntityReference(source.getNodeName());
                deep = false;
                break;
            }
            case 6: {
                final Entity srcentity = (Entity)source;
                final EntityImpl newentity = (EntityImpl)this.createEntity(source.getNodeName());
                newentity.setPublicId(srcentity.getPublicId());
                newentity.setSystemId(srcentity.getSystemId());
                newentity.setNotationName(srcentity.getNotationName());
                newentity.isReadOnly(false);
                newnode = newentity;
                break;
            }
            case 7: {
                newnode = this.createProcessingInstruction(source.getNodeName(), source.getNodeValue());
                break;
            }
            case 8: {
                newnode = this.createComment(source.getNodeValue());
                break;
            }
            case 10: {
                if (!cloningDoc) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                    throw new DOMException((short)9, msg);
                }
                final DocumentType srcdoctype = (DocumentType)source;
                final DocumentTypeImpl newdoctype = (DocumentTypeImpl)this.createDocumentType(srcdoctype.getNodeName(), srcdoctype.getPublicId(), srcdoctype.getSystemId());
                NamedNodeMap smap = srcdoctype.getEntities();
                NamedNodeMap tmap = newdoctype.getEntities();
                if (smap != null) {
                    for (int i = 0; i < smap.getLength(); ++i) {
                        tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
                    }
                }
                smap = srcdoctype.getNotations();
                tmap = newdoctype.getNotations();
                if (smap != null) {
                    for (int i = 0; i < smap.getLength(); ++i) {
                        tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
                    }
                }
                newnode = newdoctype;
                break;
            }
            case 11: {
                newnode = this.createDocumentFragment();
                break;
            }
            case 12: {
                final Notation srcnotation = (Notation)source;
                final NotationImpl newnotation = (NotationImpl)this.createNotation(source.getNodeName());
                newnotation.setPublicId(srcnotation.getPublicId());
                newnotation.setSystemId(srcnotation.getSystemId());
                newnode = newnotation;
                break;
            }
            default: {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException((short)9, msg);
            }
        }
        if (userData != null) {
            this.callUserDataHandlers(source, newnode, (short)2, userData);
        }
        if (deep) {
            for (Node srckid = source.getFirstChild(); srckid != null; srckid = srckid.getNextSibling()) {
                newnode.appendChild(this.importNode(srckid, true, cloningDoc, reversedIdentifiers));
            }
        }
        if (newnode.getNodeType() == 6) {
            ((NodeImpl)newnode).setReadOnly(true, true);
        }
        return newnode;
    }
    
    @Override
    public Node adoptNode(final Node source) {
        NodeImpl node;
        try {
            node = (NodeImpl)source;
        }
        catch (final ClassCastException e) {
            return null;
        }
        if (source == null) {
            return null;
        }
        if (source.getOwnerDocument() != null) {
            final DOMImplementation thisImpl = this.getImplementation();
            final DOMImplementation otherImpl = source.getOwnerDocument().getImplementation();
            if (thisImpl != otherImpl) {
                if (thisImpl instanceof DOMImplementationImpl && otherImpl instanceof DeferredDOMImplementationImpl) {
                    this.undeferChildren(node);
                }
                else if (!(thisImpl instanceof DeferredDOMImplementationImpl) || !(otherImpl instanceof DOMImplementationImpl)) {
                    return null;
                }
            }
        }
        Map<String, UserDataRecord> userData = null;
        switch (node.getNodeType()) {
            case 2: {
                final AttrImpl attr = (AttrImpl)node;
                if (attr.getOwnerElement() != null) {
                    attr.getOwnerElement().removeAttributeNode(attr);
                }
                attr.isSpecified(true);
                userData = node.getUserDataRecord();
                attr.setOwnerDocument(this);
                if (userData != null) {
                    this.setUserDataTable(node, userData);
                    break;
                }
                break;
            }
            case 6:
            case 12: {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException((short)7, msg);
            }
            case 9:
            case 10: {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException((short)9, msg);
            }
            case 5: {
                userData = node.getUserDataRecord();
                final Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                Node child;
                while ((child = node.getFirstChild()) != null) {
                    node.removeChild(child);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    this.setUserDataTable(node, userData);
                }
                if (this.docType == null) {
                    break;
                }
                final NamedNodeMap entities = this.docType.getEntities();
                final Node entityNode = entities.getNamedItem(node.getNodeName());
                if (entityNode == null) {
                    break;
                }
                for (child = entityNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                    final Node childClone = child.cloneNode(true);
                    node.appendChild(childClone);
                }
                break;
            }
            case 1: {
                userData = node.getUserDataRecord();
                final Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    this.setUserDataTable(node, userData);
                }
                ((ElementImpl)node).reconcileDefaultAttributes();
                break;
            }
            default: {
                userData = node.getUserDataRecord();
                final Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
                if (userData != null) {
                    this.setUserDataTable(node, userData);
                    break;
                }
                break;
            }
        }
        if (userData != null) {
            this.callUserDataHandlers(source, null, (short)5, userData);
        }
        return node;
    }
    
    protected void undeferChildren(Node node) {
        final Node top = node;
        while (null != node) {
            if (((NodeImpl)node).needsSyncData()) {
                ((NodeImpl)node).synchronizeData();
            }
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                    this.undeferChildren(attributes.item(i));
                }
            }
            Node nextNode = null;
            nextNode = node.getFirstChild();
            while (null == nextNode) {
                if (top.equals(node)) {
                    break;
                }
                nextNode = node.getNextSibling();
                if (null != nextNode) {
                    continue;
                }
                node = node.getParentNode();
                if (null == node || top.equals(node)) {
                    nextNode = null;
                    break;
                }
            }
            node = nextNode;
        }
    }
    
    @Override
    public Element getElementById(final String elementId) {
        return this.getIdentifier(elementId);
    }
    
    protected final void clearIdentifiers() {
        if (this.identifiers != null) {
            this.identifiers.clear();
        }
    }
    
    public void putIdentifier(final String idName, final Element element) {
        if (element == null) {
            this.removeIdentifier(idName);
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            this.identifiers = new HashMap<String, Node>();
        }
        this.identifiers.put(idName, element);
    }
    
    public Element getIdentifier(final String idName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return null;
        }
        final Element elem = this.identifiers.get(idName);
        if (elem != null) {
            for (Node parent = elem.getParentNode(); parent != null; parent = parent.getParentNode()) {
                if (parent == this) {
                    return elem;
                }
            }
        }
        return null;
    }
    
    public void removeIdentifier(final String idName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return;
        }
        this.identifiers.remove(idName);
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName);
    }
    
    public Element createElementNS(final String namespaceURI, final String qualifiedName, final String localpart) throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName, localpart);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName);
    }
    
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName, final String localpart) throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName, localpart);
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        return new DeepNodeListImpl(this, namespaceURI, localName);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final CoreDocumentImpl newdoc = (CoreDocumentImpl)super.clone();
        newdoc.docType = null;
        newdoc.docElement = null;
        return newdoc;
    }
    
    public static final boolean isXMLName(final String s, final boolean xml11Version) {
        if (s == null) {
            return false;
        }
        if (!xml11Version) {
            return XMLChar.isValidName(s);
        }
        return XML11Char.isXML11ValidName(s);
    }
    
    public static final boolean isValidQName(final String prefix, final String local, final boolean xml11Version) {
        if (local == null) {
            return false;
        }
        boolean validNCName = false;
        if (!xml11Version) {
            validNCName = ((prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local));
        }
        else {
            validNCName = ((prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local));
        }
        return validNCName;
    }
    
    protected boolean isKidOK(final Node parent, final Node child) {
        if (this.allowGrammarAccess && parent.getNodeType() == 10) {
            return child.getNodeType() == 1;
        }
        return 0x0 != (CoreDocumentImpl.kidOK[parent.getNodeType()] & 1 << child.getNodeType());
    }
    
    @Override
    protected void changed() {
        ++this.changes;
    }
    
    @Override
    protected int changes() {
        return this.changes;
    }
    
    NodeListCache getNodeListCache(final ParentNode owner) {
        if (this.fFreeNLCache == null) {
            return new NodeListCache(owner);
        }
        final NodeListCache c = this.fFreeNLCache;
        this.fFreeNLCache = this.fFreeNLCache.next;
        c.fChild = null;
        c.fChildIndex = -1;
        c.fLength = -1;
        if (c.fOwner != null) {
            c.fOwner.fNodeListCache = null;
        }
        c.fOwner = owner;
        return c;
    }
    
    void freeNodeListCache(final NodeListCache c) {
        c.next = this.fFreeNLCache;
        this.fFreeNLCache = c;
    }
    
    public Object setUserData(final Node n, final String key, final Object data, final UserDataHandler handler) {
        if (data == null) {
            if (this.nodeUserData != null) {
                final Map<String, UserDataRecord> t = this.nodeUserData.get(n);
                if (t != null) {
                    final UserDataRecord r = t.remove(key);
                    if (r != null) {
                        return r.fData;
                    }
                }
            }
            return null;
        }
        Map<String, UserDataRecord> t;
        if (this.nodeUserData == null) {
            this.nodeUserData = new HashMap<Node, Map<String, UserDataRecord>>();
            t = new HashMap<String, UserDataRecord>();
            this.nodeUserData.put(n, t);
        }
        else {
            t = this.nodeUserData.get(n);
            if (t == null) {
                t = new HashMap<String, UserDataRecord>();
                this.nodeUserData.put(n, t);
            }
        }
        final UserDataRecord r = t.put(key, new UserDataRecord(data, handler));
        if (r != null) {
            return r.fData;
        }
        return null;
    }
    
    public Object getUserData(final Node n, final String key) {
        if (this.nodeUserData == null) {
            return null;
        }
        final Map<String, UserDataRecord> t = this.nodeUserData.get(n);
        if (t == null) {
            return null;
        }
        final UserDataRecord r = t.get(key);
        if (r != null) {
            return r.fData;
        }
        return null;
    }
    
    protected Map<String, UserDataRecord> getUserDataRecord(final Node n) {
        if (this.nodeUserData == null) {
            return null;
        }
        final Map<String, UserDataRecord> t = this.nodeUserData.get(n);
        if (t == null) {
            return null;
        }
        return t;
    }
    
    Map<String, UserDataRecord> removeUserDataTable(final Node n) {
        if (this.nodeUserData == null) {
            return null;
        }
        return this.nodeUserData.get(n);
    }
    
    void setUserDataTable(final Node n, final Map<String, UserDataRecord> data) {
        if (this.nodeUserData == null) {
            this.nodeUserData = new HashMap<Node, Map<String, UserDataRecord>>();
        }
        if (data != null) {
            this.nodeUserData.put(n, data);
        }
    }
    
    void callUserDataHandlers(final Node n, final Node c, final short operation) {
        if (this.nodeUserData == null) {
            return;
        }
        if (n instanceof NodeImpl) {
            final Map<String, UserDataRecord> t = ((NodeImpl)n).getUserDataRecord();
            if (t == null || t.isEmpty()) {
                return;
            }
            this.callUserDataHandlers(n, c, operation, t);
        }
    }
    
    void callUserDataHandlers(final Node n, final Node c, final short operation, final Map<String, UserDataRecord> userData) {
        if (userData == null || userData.isEmpty()) {
            return;
        }
        for (final String key : userData.keySet()) {
            final UserDataRecord r = userData.get(key);
            if (r.fHandler != null) {
                r.fHandler.handle(operation, key, r.fData, n, c);
            }
        }
    }
    
    protected final void checkNamespaceWF(final String qname, final int colon1, final int colon2) {
        if (!this.errorChecking) {
            return;
        }
        if (colon1 == 0 || colon1 == qname.length() - 1 || colon2 != colon1) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, msg);
        }
    }
    
    protected final void checkDOMNSErr(final String prefix, final String namespace) {
        if (this.errorChecking) {
            if (namespace == null) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException((short)14, msg);
            }
            if (prefix.equals("xml") && !namespace.equals(NamespaceContext.XML_URI)) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException((short)14, msg);
            }
            if ((prefix.equals("xmlns") && !namespace.equals(NamespaceContext.XMLNS_URI)) || (!prefix.equals("xmlns") && namespace.equals(NamespaceContext.XMLNS_URI))) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException((short)14, msg);
            }
        }
    }
    
    protected final void checkQName(final String prefix, final String local) {
        if (!this.errorChecking) {
            return;
        }
        boolean validNCName = false;
        if (!this.xml11Version) {
            validNCName = ((prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local));
        }
        else {
            validNCName = ((prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local));
        }
        if (!validNCName) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, msg);
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
    
    protected void setUserData(final NodeImpl n, final Object data) {
        this.setUserData(n, "XERCES1DOMUSERDATA", data, null);
    }
    
    protected Object getUserData(final NodeImpl n) {
        return this.getUserData(n, "XERCES1DOMUSERDATA");
    }
    
    protected void addEventListener(final NodeImpl node, final String type, final EventListener listener, final boolean useCapture) {
    }
    
    protected void removeEventListener(final NodeImpl node, final String type, final EventListener listener, final boolean useCapture) {
    }
    
    protected void copyEventListeners(final NodeImpl src, final NodeImpl tgt) {
    }
    
    protected boolean dispatchEvent(final NodeImpl node, final Event event) {
        return false;
    }
    
    void replacedText(final NodeImpl node) {
    }
    
    void deletedText(final NodeImpl node, final int offset, final int count) {
    }
    
    void insertedText(final NodeImpl node, final int offset, final int count) {
    }
    
    void modifyingCharacterData(final NodeImpl node, final boolean replace) {
    }
    
    void modifiedCharacterData(final NodeImpl node, final String oldvalue, final String value, final boolean replace) {
    }
    
    void insertingNode(final NodeImpl node, final boolean replace) {
    }
    
    void insertedNode(final NodeImpl node, final NodeImpl newInternal, final boolean replace) {
    }
    
    void removingNode(final NodeImpl node, final NodeImpl oldChild, final boolean replace) {
    }
    
    void removedNode(final NodeImpl node, final boolean replace) {
    }
    
    void replacingNode(final NodeImpl node) {
    }
    
    void replacedNode(final NodeImpl node) {
    }
    
    void replacingData(final NodeImpl node) {
    }
    
    void replacedCharacterData(final NodeImpl node, final String oldvalue, final String value) {
    }
    
    void modifiedAttrValue(final AttrImpl attr, final String oldvalue) {
    }
    
    void setAttrNode(final AttrImpl attr, final AttrImpl previous) {
    }
    
    void removedAttrNode(final AttrImpl attr, final NodeImpl oldOwner, final String name) {
    }
    
    void renamedAttrNode(final Attr oldAt, final Attr newAt) {
    }
    
    void renamedElement(final Element oldEl, final Element newEl) {
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        Hashtable<Node, Hashtable<String, UserDataRecord>> nud = null;
        if (this.nodeUserData != null) {
            nud = new Hashtable<Node, Hashtable<String, UserDataRecord>>();
            for (final Map.Entry<Node, Map<String, UserDataRecord>> e : this.nodeUserData.entrySet()) {
                nud.put(e.getKey(), new Hashtable<String, UserDataRecord>(e.getValue()));
            }
        }
        final Hashtable<String, Node> ids = (this.identifiers == null) ? null : new Hashtable<String, Node>(this.identifiers);
        final Hashtable<Node, Integer> nt = (this.nodeTable == null) ? null : new Hashtable<Node, Integer>(this.nodeTable);
        final ObjectOutputStream.PutField pf = out.putFields();
        pf.put("docType", this.docType);
        pf.put("docElement", this.docElement);
        pf.put("fFreeNLCache", this.fFreeNLCache);
        pf.put("encoding", this.encoding);
        pf.put("actualEncoding", this.actualEncoding);
        pf.put("version", this.version);
        pf.put("standalone", this.standalone);
        pf.put("fDocumentURI", this.fDocumentURI);
        pf.put("userData", nud);
        pf.put("identifiers", ids);
        pf.put("changes", this.changes);
        pf.put("allowGrammarAccess", this.allowGrammarAccess);
        pf.put("errorChecking", this.errorChecking);
        pf.put("ancestorChecking", this.ancestorChecking);
        pf.put("xmlVersionChanged", this.xmlVersionChanged);
        pf.put("documentNumber", this.documentNumber);
        pf.put("nodeCounter", this.nodeCounter);
        pf.put("nodeTable", nt);
        pf.put("xml11Version", this.xml11Version);
        out.writeFields();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField gf = in.readFields();
        this.docType = (DocumentTypeImpl)gf.get("docType", null);
        this.docElement = (ElementImpl)gf.get("docElement", null);
        this.fFreeNLCache = (NodeListCache)gf.get("fFreeNLCache", null);
        this.encoding = (String)gf.get("encoding", null);
        this.actualEncoding = (String)gf.get("actualEncoding", null);
        this.version = (String)gf.get("version", null);
        this.standalone = gf.get("standalone", false);
        this.fDocumentURI = (String)gf.get("fDocumentURI", null);
        final Hashtable<Node, Hashtable<String, UserDataRecord>> nud = (Hashtable<Node, Hashtable<String, UserDataRecord>>)gf.get("userData", null);
        final Hashtable<String, Node> ids = (Hashtable<String, Node>)gf.get("identifiers", null);
        this.changes = gf.get("changes", 0);
        this.allowGrammarAccess = gf.get("allowGrammarAccess", false);
        this.errorChecking = gf.get("errorChecking", true);
        this.ancestorChecking = gf.get("ancestorChecking", true);
        this.xmlVersionChanged = gf.get("xmlVersionChanged", false);
        this.documentNumber = gf.get("documentNumber", 0);
        this.nodeCounter = gf.get("nodeCounter", 0);
        final Hashtable<Node, Integer> nt = (Hashtable<Node, Integer>)gf.get("nodeTable", null);
        this.xml11Version = gf.get("xml11Version", false);
        if (nud != null) {
            this.nodeUserData = new HashMap<Node, Map<String, UserDataRecord>>();
            for (final Map.Entry<Node, Hashtable<String, UserDataRecord>> e : nud.entrySet()) {
                this.nodeUserData.put(e.getKey(), new HashMap<String, UserDataRecord>(e.getValue()));
            }
        }
        if (ids != null) {
            this.identifiers = new HashMap<String, Node>(ids);
        }
        if (nt != null) {
            this.nodeTable = new HashMap<Node, Integer>(nt);
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
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("docType", DocumentTypeImpl.class), new ObjectStreamField("docElement", ElementImpl.class), new ObjectStreamField("fFreeNLCache", NodeListCache.class), new ObjectStreamField("encoding", String.class), new ObjectStreamField("actualEncoding", String.class), new ObjectStreamField("version", String.class), new ObjectStreamField("standalone", Boolean.TYPE), new ObjectStreamField("fDocumentURI", String.class), new ObjectStreamField("userData", Hashtable.class), new ObjectStreamField("identifiers", Hashtable.class), new ObjectStreamField("changes", Integer.TYPE), new ObjectStreamField("allowGrammarAccess", Boolean.TYPE), new ObjectStreamField("errorChecking", Boolean.TYPE), new ObjectStreamField("ancestorChecking", Boolean.TYPE), new ObjectStreamField("xmlVersionChanged", Boolean.TYPE), new ObjectStreamField("documentNumber", Integer.TYPE), new ObjectStreamField("nodeCounter", Integer.TYPE), new ObjectStreamField("nodeTable", Hashtable.class), new ObjectStreamField("xml11Version", Boolean.TYPE) };
    }
}
