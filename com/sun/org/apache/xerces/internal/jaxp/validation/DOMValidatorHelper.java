package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.Enumeration;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.w3c.dom.Entity;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;

final class DOMValidatorHelper implements ValidatorHelper, EntityState
{
    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_MASK = 1023;
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private XMLErrorReporter fErrorReporter;
    private NamespaceSupport fNamespaceContext;
    private DOMNamespaceContext fDOMNamespaceContext;
    private XMLSchemaValidator fSchemaValidator;
    private SymbolTable fSymbolTable;
    private ValidationManager fValidationManager;
    private XMLSchemaValidatorComponentManager fComponentManager;
    private final SimpleLocator fXMLLocator;
    private DOMDocumentHandler fDOMValidatorHandler;
    private final DOMResultAugmentor fDOMResultAugmentor;
    private final DOMResultBuilder fDOMResultBuilder;
    private NamedNodeMap fEntities;
    private char[] fCharBuffer;
    private Node fRoot;
    private Node fCurrentElement;
    final QName fElementQName;
    final QName fAttributeQName;
    final XMLAttributesImpl fAttributes;
    final XMLString fTempString;
    
    public DOMValidatorHelper(final XMLSchemaValidatorComponentManager componentManager) {
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
        this.fComponentManager = componentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fNamespaceContext = (NamespaceSupport)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
    }
    
    @Override
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
                catch (final XMLParseException e) {
                    throw Util.toSAXParseException(e);
                }
                catch (final XNIException e2) {
                    throw Util.toSAXException(e2);
                }
                finally {
                    this.fRoot = null;
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
    
    @Override
    public boolean isEntityDeclared(final String name) {
        return false;
    }
    
    @Override
    public boolean isEntityUnparsed(final String name) {
        if (this.fEntities != null) {
            final Entity entity = (Entity)this.fEntities.getNamedItem(name);
            if (entity != null) {
                return entity.getNotationName() != null;
            }
        }
        return false;
    }
    
    private void validate(Node node) {
        final Node top = node;
        while (node != null) {
            this.beginNode(node);
            Node next = node.getFirstChild();
            while (next == null) {
                this.finishNode(node);
                if (top == node) {
                    break;
                }
                next = node.getNextSibling();
                if (next != null) {
                    continue;
                }
                node = node.getParentNode();
                if (node == null || top == node) {
                    if (node != null) {
                        this.finishNode(node);
                    }
                    next = null;
                    break;
                }
            }
            node = next;
        }
    }
    
    private void beginNode(final Node node) {
        switch (node.getNodeType()) {
            case 1: {
                this.fCurrentElement = node;
                this.fNamespaceContext.pushContext();
                this.fillQName(this.fElementQName, node);
                this.processAttributes(node.getAttributes());
                this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
                break;
            }
            case 3: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.sendCharactersToValidator(node.getNodeValue());
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.characters((Text)node);
                    break;
                }
                this.sendCharactersToValidator(node.getNodeValue());
                break;
            }
            case 4: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.setIgnoringCharacters(true);
                    this.fSchemaValidator.startCDATA(null);
                    this.sendCharactersToValidator(node.getNodeValue());
                    this.fSchemaValidator.endCDATA(null);
                    this.fDOMValidatorHandler.setIgnoringCharacters(false);
                    this.fDOMValidatorHandler.cdata((CDATASection)node);
                    break;
                }
                this.fSchemaValidator.startCDATA(null);
                this.sendCharactersToValidator(node.getNodeValue());
                this.fSchemaValidator.endCDATA(null);
                break;
            }
            case 7: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.processingInstruction((ProcessingInstruction)node);
                    break;
                }
                break;
            }
            case 8: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.comment((Comment)node);
                    break;
                }
                break;
            }
            case 10: {
                if (this.fDOMValidatorHandler != null) {
                    this.fDOMValidatorHandler.doctypeDecl((DocumentType)node);
                    break;
                }
                break;
            }
        }
    }
    
    private void finishNode(final Node node) {
        if (node.getNodeType() == 1) {
            this.fCurrentElement = node;
            this.fillQName(this.fElementQName, node);
            this.fSchemaValidator.endElement(this.fElementQName, null);
            this.fNamespaceContext.popContext();
        }
    }
    
    private void setupEntityMap(final Document doc) {
        if (doc != null) {
            final DocumentType docType = doc.getDoctype();
            if (docType != null) {
                this.fEntities = docType.getEntities();
                return;
            }
        }
        this.fEntities = null;
    }
    
    private void setupDOMResultHandler(final DOMSource source, final DOMResult result) throws SAXException {
        if (result == null) {
            this.fDOMValidatorHandler = null;
            this.fSchemaValidator.setDocumentHandler(null);
            return;
        }
        final Node nodeResult = result.getNode();
        if (source.getNode() == nodeResult) {
            this.fDOMValidatorHandler = this.fDOMResultAugmentor;
            this.fDOMResultAugmentor.setDOMResult(result);
            this.fSchemaValidator.setDocumentHandler(this.fDOMResultAugmentor);
            return;
        }
        if (result.getNode() == null) {
            try {
                final DocumentBuilderFactory factory = JdkXmlUtils.getDOMFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
                final DocumentBuilder builder = factory.newDocumentBuilder();
                result.setNode(builder.newDocument());
            }
            catch (final ParserConfigurationException e) {
                throw new SAXException(e);
            }
        }
        this.fDOMValidatorHandler = this.fDOMResultBuilder;
        this.fDOMResultBuilder.setDOMResult(result);
        this.fSchemaValidator.setDocumentHandler(this.fDOMResultBuilder);
    }
    
    private void fillQName(final QName toFill, final Node node) {
        final String prefix = node.getPrefix();
        final String localName = node.getLocalName();
        final String rawName = node.getNodeName();
        final String namespace = node.getNamespaceURI();
        toFill.uri = ((namespace != null && namespace.length() > 0) ? this.fSymbolTable.addSymbol(namespace) : null);
        toFill.rawname = ((rawName != null) ? this.fSymbolTable.addSymbol(rawName) : XMLSymbols.EMPTY_STRING);
        if (localName == null) {
            final int k = rawName.indexOf(58);
            if (k > 0) {
                toFill.prefix = this.fSymbolTable.addSymbol(rawName.substring(0, k));
                toFill.localpart = this.fSymbolTable.addSymbol(rawName.substring(k + 1));
            }
            else {
                toFill.prefix = XMLSymbols.EMPTY_STRING;
                toFill.localpart = toFill.rawname;
            }
        }
        else {
            toFill.prefix = ((prefix != null) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
            toFill.localpart = ((localName != null) ? this.fSymbolTable.addSymbol(localName) : XMLSymbols.EMPTY_STRING);
        }
    }
    
    private void processAttributes(final NamedNodeMap attrMap) {
        final int attrCount = attrMap.getLength();
        this.fAttributes.removeAllAttributes();
        for (int i = 0; i < attrCount; ++i) {
            final Attr attr = (Attr)attrMap.item(i);
            String value = attr.getValue();
            if (value == null) {
                value = XMLSymbols.EMPTY_STRING;
            }
            this.fillQName(this.fAttributeQName, attr);
            this.fAttributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, value);
            this.fAttributes.setSpecified(i, attr.getSpecified());
            if (this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                    this.fNamespaceContext.declarePrefix(this.fAttributeQName.localpart, (value.length() != 0) ? this.fSymbolTable.addSymbol(value) : null);
                }
                else {
                    this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, (value.length() != 0) ? this.fSymbolTable.addSymbol(value) : null);
                }
            }
        }
    }
    
    private void sendCharactersToValidator(final String str) {
        if (str != null) {
            final int length = str.length();
            final int remainder = length & 0x3FF;
            if (remainder > 0) {
                str.getChars(0, remainder, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, remainder);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
            int i = remainder;
            while (i < length) {
                final int n = i;
                i += 1024;
                str.getChars(n, i, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                this.fSchemaValidator.characters(this.fTempString, null);
            }
        }
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
        
        @Override
        public void pushContext() {
            DOMValidatorHelper.this.fNamespaceContext.pushContext();
        }
        
        @Override
        public void popContext() {
            DOMValidatorHelper.this.fNamespaceContext.popContext();
        }
        
        @Override
        public boolean declarePrefix(final String prefix, final String uri) {
            return DOMValidatorHelper.this.fNamespaceContext.declarePrefix(prefix, uri);
        }
        
        @Override
        public String getURI(final String prefix) {
            String uri = DOMValidatorHelper.this.fNamespaceContext.getURI(prefix);
            if (uri == null) {
                if (!this.fDOMContextBuilt) {
                    this.fillNamespaceContext();
                    this.fDOMContextBuilt = true;
                }
                if (this.fNamespaceSize > 0 && !DOMValidatorHelper.this.fNamespaceContext.containsPrefix(prefix)) {
                    uri = this.getURI0(prefix);
                }
            }
            return uri;
        }
        
        @Override
        public String getPrefix(final String uri) {
            return DOMValidatorHelper.this.fNamespaceContext.getPrefix(uri);
        }
        
        @Override
        public int getDeclaredPrefixCount() {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixCount();
        }
        
        @Override
        public String getDeclaredPrefixAt(final int index) {
            return DOMValidatorHelper.this.fNamespaceContext.getDeclaredPrefixAt(index);
        }
        
        @Override
        public Enumeration getAllPrefixes() {
            return DOMValidatorHelper.this.fNamespaceContext.getAllPrefixes();
        }
        
        @Override
        public void reset() {
            this.fDOMContextBuilt = false;
            this.fNamespaceSize = 0;
        }
        
        private void fillNamespaceContext() {
            if (DOMValidatorHelper.this.fRoot != null) {
                for (Node currentNode = DOMValidatorHelper.this.fRoot.getParentNode(); currentNode != null; currentNode = currentNode.getParentNode()) {
                    if (1 == currentNode.getNodeType()) {
                        final NamedNodeMap attributes = currentNode.getAttributes();
                        for (int attrCount = attributes.getLength(), i = 0; i < attrCount; ++i) {
                            final Attr attr = (Attr)attributes.item(i);
                            String value = attr.getValue();
                            if (value == null) {
                                value = XMLSymbols.EMPTY_STRING;
                            }
                            DOMValidatorHelper.this.fillQName(DOMValidatorHelper.this.fAttributeQName, attr);
                            if (DOMValidatorHelper.this.fAttributeQName.uri == NamespaceContext.XMLNS_URI) {
                                if (DOMValidatorHelper.this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                                    this.declarePrefix0(DOMValidatorHelper.this.fAttributeQName.localpart, (value.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(value) : null);
                                }
                                else {
                                    this.declarePrefix0(XMLSymbols.EMPTY_STRING, (value.length() != 0) ? DOMValidatorHelper.this.fSymbolTable.addSymbol(value) : null);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private void declarePrefix0(final String prefix, final String uri) {
            if (this.fNamespaceSize == this.fNamespace.length) {
                final String[] namespacearray = new String[this.fNamespaceSize * 2];
                System.arraycopy(this.fNamespace, 0, namespacearray, 0, this.fNamespaceSize);
                this.fNamespace = namespacearray;
            }
            this.fNamespace[this.fNamespaceSize++] = prefix;
            this.fNamespace[this.fNamespaceSize++] = uri;
        }
        
        private String getURI0(final String prefix) {
            for (int i = 0; i < this.fNamespaceSize; i += 2) {
                if (this.fNamespace[i] == prefix) {
                    return this.fNamespace[i + 1];
                }
            }
            return null;
        }
    }
}
