package org.apache.xerces.jaxp.validation;

import java.util.Enumeration;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Attr;
import org.apache.xerces.util.XMLSymbols;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.apache.xerces.xni.XMLAttributes;
import org.w3c.dom.Entity;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.Document;
import org.apache.xerces.xni.XMLLocator;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.validation.EntityState;

final class DOMValidatorHelper implements ValidatorHelper, EntityState
{
    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_MASK = 1023;
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final XMLErrorReporter fErrorReporter;
    private final NamespaceSupport fNamespaceContext;
    private final DOMNamespaceContext fDOMNamespaceContext;
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final SimpleLocator fXMLLocator;
    private DOMDocumentHandler fDOMValidatorHandler;
    private final DOMResultAugmentor fDOMResultAugmentor;
    private final DOMResultBuilder fDOMResultBuilder;
    private NamedNodeMap fEntities;
    private final char[] fCharBuffer;
    private Node fRoot;
    private Node fCurrentElement;
    final QName fElementQName;
    final QName fAttributeQName;
    final XMLAttributesImpl fAttributes;
    final XMLString fTempString;
    private final boolean fIsXSD11;
    
    public DOMValidatorHelper(final XMLSchemaValidatorComponentManager fComponentManager) {
        this.fDOMNamespaceContext = new DOMNamespaceContext();
        this.fXMLLocator = new SimpleLocator(null, null, -1, -1, -1);
        this.fDOMResultAugmentor = new DOMResultAugmentor(this);
        this.fDOMResultBuilder = new DOMResultBuilder();
        this.fEntities = null;
        this.fCharBuffer = new char[1024];
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fTempString = new XMLString();
        this.fComponentManager = fComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fNamespaceContext = (NamespaceSupport)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
        this.fIsXSD11 = Constants.W3C_XML_SCHEMA11_NS_URI.equals(this.fComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/version"));
    }
    
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result instanceof DOMResult || result == null) {
            final DOMSource domSource = (DOMSource)source;
            final DOMResult domResult = (DOMResult)result;
            final Node node = domSource.getNode();
            if ((this.fRoot = node) != null) {
                this.fComponentManager.reset();
                this.fValidationManager.setEntityState(this);
                this.fDOMNamespaceContext.reset();
                final String systemId = domSource.getSystemId();
                this.fXMLLocator.setLiteralSystemId(systemId);
                this.fXMLLocator.setExpandedSystemId(systemId);
                this.fErrorReporter.setDocumentLocator(this.fXMLLocator);
                try {
                    this.setupEntityMap((node.getNodeType() == 9) ? ((Document)node) : node.getOwnerDocument());
                    this.setupDOMResultHandler(domSource, domResult);
                    this.fSchemaValidator.startDocument(this.fXMLLocator, null, this.fDOMNamespaceContext, null);
                    this.validate(node);
                    this.fSchemaValidator.endDocument(null);
                }
                catch (final XMLParseException ex) {
                    throw Util.toSAXParseException(ex);
                }
                catch (final XNIException ex2) {
                    throw Util.toSAXException(ex2);
                }
                finally {
                    this.fRoot = null;
                    this.fCurrentElement = null;
                    this.fEntities = null;
                    if (this.fDOMValidatorHandler != null) {
                        this.fDOMValidatorHandler.setDOMResult(null);
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    public boolean isEntityDeclared(final String s) {
        return false;
    }
    
    public boolean isEntityUnparsed(final String s) {
        if (this.fEntities != null) {
            final Entity entity = (Entity)this.fEntities.getNamedItem(s);
            if (entity != null) {
                return entity.getNotationName() != null;
            }
        }
        return false;
    }
    
    private void validate(Node parentNode) {
        final Node node = parentNode;
        final boolean useIsSameNode = this.useIsSameNode(node);
        while (parentNode != null) {
            this.beginNode(parentNode);
            Node node2 = parentNode.getFirstChild();
            while (node2 == null) {
                this.finishNode(parentNode);
                if (node == parentNode) {
                    break;
                }
                node2 = parentNode.getNextSibling();
                if (node2 == null) {
                    parentNode = parentNode.getParentNode();
                    if (parentNode != null) {
                        if (useIsSameNode) {
                            if (!node.isSameNode(parentNode)) {
                                continue;
                            }
                        }
                        else if (node != parentNode) {
                            continue;
                        }
                    }
                    if (parentNode != null) {
                        this.finishNode(parentNode);
                    }
                    node2 = null;
                    break;
                }
            }
            parentNode = node2;
        }
    }
    
    private void beginNode(final Node fCurrentElement) {
        switch (fCurrentElement.getNodeType()) {
            case 1: {
                this.fCurrentElement = fCurrentElement;
                this.fNamespaceContext.pushContext();
                this.fillQName(this.fElementQName, fCurrentElement);
                this.processAttributes(fCurrentElement.getAttributes());
                this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
                break;
            }
            case 3: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.sendCharactersToValidator(fCurrentElement.getNodeValue());
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.characters((Text)fCurrentElement);
                    break;
                }
                this.sendCharactersToValidator(fCurrentElement.getNodeValue());
                break;
            }
            case 4: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.fSchemaValidator.startCDATA(null);
                    this.sendCharactersToValidator(fCurrentElement.getNodeValue());
                    this.fSchemaValidator.endCDATA(null);
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.cdata((CDATASection)fCurrentElement);
                    break;
                }
                this.fSchemaValidator.startCDATA(null);
                this.sendCharactersToValidator(fCurrentElement.getNodeValue());
                this.fSchemaValidator.endCDATA(null);
                break;
            }
            case 7: {
                if (this.fIsXSD11) {
                    this.fillXMLString(this.fTempString, fCurrentElement.getNodeValue());
                    this.fSchemaValidator.processingInstruction(fCurrentElement.getNodeName(), this.fTempString, null);
                }
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.processingInstruction((ProcessingInstruction)fCurrentElement);
                    break;
                }
                break;
            }
            case 8: {
                if (this.fIsXSD11) {
                    this.fillXMLString(this.fTempString, fCurrentElement.getNodeValue());
                    this.fSchemaValidator.comment(this.fTempString, null);
                }
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.comment((Comment)fCurrentElement);
                    break;
                }
                break;
            }
            case 10: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.doctypeDecl((DocumentType)fCurrentElement);
                    break;
                }
                break;
            }
        }
    }
    
    private void finishNode(final Node fCurrentElement) {
        if (fCurrentElement.getNodeType() == 1) {
            this.fCurrentElement = fCurrentElement;
            this.fillQName(this.fElementQName, fCurrentElement);
            this.fSchemaValidator.endElement(this.fElementQName, null);
            this.fNamespaceContext.popContext();
        }
    }
    
    private void setupEntityMap(final Document document) {
        if (document != null) {
            final DocumentType doctype = document.getDoctype();
            if (doctype != null) {
                this.fEntities = doctype.getEntities();
                return;
            }
        }
        this.fEntities = null;
    }
    
    private void setupDOMResultHandler(final DOMSource domSource, final DOMResult domResult) throws SAXException {
        if (domResult == null) {
            this.fDOMValidatorHandler = null;
            this.fSchemaValidator.setDocumentHandler(null);
            return;
        }
        if (domSource.getNode() == domResult.getNode()) {
            this.fDOMValidatorHandler = this.fDOMResultAugmentor;
            this.fDOMResultAugmentor.setDOMResult(domResult);
            this.fSchemaValidator.setDocumentHandler(this.fDOMResultAugmentor);
            return;
        }
        if (domResult.getNode() == null) {
            try {
                final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
                instance.setNamespaceAware(true);
                domResult.setNode(instance.newDocumentBuilder().newDocument());
            }
            catch (final ParserConfigurationException ex) {
                throw new SAXException(ex);
            }
        }
        this.fDOMValidatorHandler = this.fDOMResultBuilder;
        this.fDOMResultBuilder.setDOMResult(domResult);
        this.fSchemaValidator.setDocumentHandler(this.fDOMResultBuilder);
    }
    
    private void fillQName(final QName qName, final Node node) {
        final String prefix = node.getPrefix();
        final String localName = node.getLocalName();
        final String nodeName = node.getNodeName();
        final String namespaceURI = node.getNamespaceURI();
        qName.prefix = ((prefix != null) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
        qName.localpart = ((localName != null) ? this.fSymbolTable.addSymbol(localName) : XMLSymbols.EMPTY_STRING);
        qName.rawname = ((nodeName != null) ? this.fSymbolTable.addSymbol(nodeName) : XMLSymbols.EMPTY_STRING);
        qName.uri = ((namespaceURI != null && namespaceURI.length() > 0) ? this.fSymbolTable.addSymbol(namespaceURI) : null);
    }
    
    private void processAttributes(final NamedNodeMap namedNodeMap) {
        final int length = namedNodeMap.getLength();
        this.fAttributes.removeAllAttributes();
        for (int i = 0; i < length; ++i) {
            final Attr attr = (Attr)namedNodeMap.item(i);
            String s = attr.getValue();
            if (s == null) {
                s = XMLSymbols.EMPTY_STRING;
            }
            this.fillQName(this.fAttributeQName, attr);
            this.fAttributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, s);
            this.fAttributes.setSpecified(i, attr.getSpecified());
            if (this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                    this.fNamespaceContext.declarePrefix(this.fAttributeQName.localpart, (s.length() != 0) ? this.fSymbolTable.addSymbol(s) : null);
                }
                else {
                    this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, (s.length() != 0) ? this.fSymbolTable.addSymbol(s) : null);
                }
            }
        }
    }
    
    private void sendCharactersToValidator(final String s) {
        if (s != null) {
            final int length = s.length();
            final int n = length & 0x3FF;
            if (n > 0) {
                s.getChars(0, n, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, n);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
            int i = n;
            while (i < length) {
                s.getChars(i, i += 1024, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
        }
    }
    
    private void fillXMLString(final XMLString xmlString, final String s) {
        final int length = s.length();
        char[] array;
        if (length <= this.fCharBuffer.length) {
            s.getChars(0, length, this.fCharBuffer, 0);
            array = this.fCharBuffer;
        }
        else {
            array = s.toCharArray();
        }
        xmlString.setValues(array, 0, length);
    }
    
    private boolean useIsSameNode(final Node node) {
        if (node instanceof NodeImpl) {
            return false;
        }
        final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        return document != null && document.getImplementation().hasFeature("Core", "3.0");
    }
    
    Node getCurrentElement() {
        return this.fCurrentElement;
    }
    
    final class DOMNamespaceContext implements NamespaceContext
    {
        protected String[] fNamespace;
        protected int fNamespaceSize;
        protected boolean fDOMContextBuilt;
        
        DOMNamespaceContext() {
            this.fNamespace = new String[32];
            this.fNamespaceSize = 0;
            this.fDOMContextBuilt = false;
        }
        
        public void pushContext() {
            DOMValidatorHelper.this.fNamespaceContext.pushContext();
        }
        
        public void popContext() {
            DOMValidatorHelper.this.fNamespaceContext.popContext();
        }
        
        public boolean declarePrefix(final String s, final String s2) {
            return DOMValidatorHelper.this.fNamespaceContext.declarePrefix(s, s2);
        }
        
        public boolean deletePrefix(final String s) {
            return DOMValidatorHelper.this.fNamespaceContext.deletePrefix(s);
        }
        
        public String getURI(final String s) {
            String s2 = DOMValidatorHelper.this.fNamespaceContext.getURI(s);
            if (s2 == null) {
                if (!this.fDOMContextBuilt) {
                    this.fillNamespaceContext();
                    this.fDOMContextBuilt = true;
                }
                if (this.fNamespaceSize > 0 && !DOMValidatorHelper.this.fNamespaceContext.containsPrefix(s)) {
                    s2 = this.getURI0(s);
                }
            }
            return s2;
        }
        
        public String getPrefix(final String s) {
            return DOMValidatorHelper.this.fNamespaceContext.getPrefix(s);
        }
        
        public int getDeclaredPrefixCount() {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixCount();
        }
        
        public String getDeclaredPrefixAt(final int n) {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixAt(n);
        }
        
        public Enumeration getAllPrefixes() {
            return DOMValidatorHelper.this.fNamespaceContext.getAllPrefixes();
        }
        
        public void reset() {
            this.fDOMContextBuilt = false;
            this.fNamespaceSize = 0;
        }
        
        private void fillNamespaceContext() {
            if (DOMValidatorHelper.this.fRoot != null) {
                for (Node node = DOMValidatorHelper.this.fRoot.getParentNode(); node != null; node = node.getParentNode()) {
                    if (1 == node.getNodeType()) {
                        final NamedNodeMap attributes = node.getAttributes();
                        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                            final Attr attr = (Attr)attributes.item(i);
                            String s = attr.getValue();
                            if (s == null) {
                                s = XMLSymbols.EMPTY_STRING;
                            }
                            DOMValidatorHelper.this.fillQName(DOMValidatorHelper.this.fAttributeQName, attr);
                            if (DOMValidatorHelper.this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                                if (DOMValidatorHelper.this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                                    this.declarePrefix0(DOMValidatorHelper.this.fAttributeQName.localpart, (s.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(s) : null);
                                }
                                else {
                                    this.declarePrefix0(XMLSymbols.EMPTY_STRING, (s.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(s) : null);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private void declarePrefix0(final String s, final String s2) {
            if (this.fNamespaceSize == this.fNamespace.length) {
                final String[] fNamespace = new String[this.fNamespaceSize * 2];
                System.arraycopy(this.fNamespace, 0, fNamespace, 0, this.fNamespaceSize);
                this.fNamespace = fNamespace;
            }
            this.fNamespace[this.fNamespaceSize++] = s;
            this.fNamespace[this.fNamespaceSize++] = s2;
        }
        
        private String getURI0(final String s) {
            for (int i = 0; i < this.fNamespaceSize; i += 2) {
                if (this.fNamespace[i] == s) {
                    return this.fNamespace[i + 1];
                }
            }
            return null;
        }
    }
}
