package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import org.w3c.dom.Element;
import org.w3c.dom.DOMError;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Entity;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import java.io.Reader;
import java.io.StringReader;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.parsers.XMLGrammarPreparser;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import org.w3c.dom.Node;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMErrorHandler;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public class DOMNormalizer implements XMLDocumentHandler
{
    protected static final boolean DEBUG_ND = false;
    protected static final boolean DEBUG = false;
    protected static final boolean DEBUG_EVENTS = false;
    protected static final String PREFIX = "NS";
    protected DOMConfigurationImpl fConfiguration;
    protected CoreDocumentImpl fDocument;
    protected final XMLAttributesProxy fAttrProxy;
    protected final QName fQName;
    protected RevalidationHandler fValidationHandler;
    protected SymbolTable fSymbolTable;
    protected DOMErrorHandler fErrorHandler;
    private final DOMErrorImpl fError;
    protected boolean fNamespaceValidation;
    protected boolean fPSVI;
    protected final NamespaceContext fNamespaceContext;
    protected final NamespaceContext fLocalNSBinder;
    protected final ArrayList fAttributeList;
    protected final DOMLocatorImpl fLocator;
    protected Node fCurrentNode;
    private QName fAttrQName;
    final XMLString fNormalizedValue;
    private XMLDTDValidator fDTDValidator;
    private boolean allWhitespace;
    
    public DOMNormalizer() {
        this.fConfiguration = null;
        this.fDocument = null;
        this.fAttrProxy = new XMLAttributesProxy();
        this.fQName = new QName();
        this.fError = new DOMErrorImpl();
        this.fNamespaceValidation = false;
        this.fPSVI = false;
        this.fNamespaceContext = new NamespaceSupport();
        this.fLocalNSBinder = new NamespaceSupport();
        this.fAttributeList = new ArrayList(5);
        this.fLocator = new DOMLocatorImpl();
        this.fCurrentNode = null;
        this.fAttrQName = new QName();
        this.fNormalizedValue = new XMLString(new char[16], 0, 0);
        this.allWhitespace = false;
    }
    
    protected void normalizeDocument(final CoreDocumentImpl document, final DOMConfigurationImpl config) {
        this.fDocument = document;
        this.fConfiguration = config;
        this.fSymbolTable = (SymbolTable)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fNamespaceContext.reset();
        this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
        if ((this.fConfiguration.features & 0x40) != 0x0) {
            final String schemaLang = (String)this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            if (schemaLang != null && schemaLang.equals(Constants.NS_XMLSCHEMA)) {
                this.fValidationHandler = CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/2001/XMLSchema");
                this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
                this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
                this.fNamespaceValidation = true;
                this.fPSVI = ((this.fConfiguration.features & 0x80) != 0x0);
            }
            this.fConfiguration.setFeature("http://xml.org/sax/features/validation", true);
            this.fDocument.clearIdentifiers();
            if (this.fValidationHandler != null) {
                ((XMLComponent)this.fValidationHandler).reset(this.fConfiguration);
            }
        }
        this.fErrorHandler = (DOMErrorHandler)this.fConfiguration.getParameter("error-handler");
        if (this.fValidationHandler != null) {
            this.fValidationHandler.setDocumentHandler(this);
            this.fValidationHandler.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, null);
        }
        try {
            Node next;
            for (Node kid = this.fDocument.getFirstChild(); kid != null; kid = next) {
                next = kid.getNextSibling();
                kid = this.normalizeNode(kid);
                if (kid != null) {
                    next = kid;
                }
            }
            if (this.fValidationHandler != null) {
                this.fValidationHandler.endDocument(null);
                CoreDOMImplementationImpl.singleton.releaseValidator("http://www.w3.org/2001/XMLSchema", this.fValidationHandler);
                this.fValidationHandler = null;
            }
        }
        catch (final AbortException e) {}
        catch (final RuntimeException e2) {
            throw e2;
        }
    }
    
    protected Node normalizeNode(Node node) {
        final int type = node.getNodeType();
        this.fLocator.fRelatedNode = node;
        switch (type) {
            case 10: {
                final DocumentTypeImpl docType = (DocumentTypeImpl)node;
                (this.fDTDValidator = (XMLDTDValidator)CoreDOMImplementationImpl.singleton.getValidator("http://www.w3.org/TR/REC-xml")).setDocumentHandler(this);
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.createGrammarPool(docType));
                this.fDTDValidator.reset(this.fConfiguration);
                this.fDTDValidator.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, null);
                this.fDTDValidator.doctypeDecl(docType.getName(), docType.getPublicId(), docType.getSystemId(), null);
                break;
            }
            case 1: {
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0 && this.fDocument.isXMLVersionChanged()) {
                    boolean wellformed;
                    if (this.fNamespaceValidation) {
                        wellformed = CoreDocumentImpl.isValidQName(node.getPrefix(), node.getLocalName(), this.fDocument.isXML11Version());
                    }
                    else {
                        wellformed = CoreDocumentImpl.isXMLName(node.getNodeName(), this.fDocument.isXML11Version());
                    }
                    if (!wellformed) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
                        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "wf-invalid-character-in-node-name");
                    }
                }
                this.fNamespaceContext.pushContext();
                this.fLocalNSBinder.reset();
                final ElementImpl elem = (ElementImpl)node;
                if (elem.needsSyncChildren()) {
                    elem.synchronizeChildren();
                }
                final AttributeMap attributes = elem.hasAttributes() ? ((AttributeMap)elem.getAttributes()) : null;
                if ((this.fConfiguration.features & 0x1) != 0x0) {
                    this.namespaceFixUp(elem, attributes);
                    if ((this.fConfiguration.features & 0x200) == 0x0 && attributes != null) {
                        for (int i = 0; i < attributes.getLength(); ++i) {
                            final Attr att = (Attr)attributes.getItem(i);
                            if (XMLSymbols.PREFIX_XMLNS.equals(att.getPrefix()) || XMLSymbols.PREFIX_XMLNS.equals(att.getName())) {
                                elem.removeAttributeNode(att);
                                --i;
                            }
                        }
                    }
                }
                else if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        attr.normalize();
                        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                            isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, attr, attr.getValue(), this.fDocument.isXML11Version());
                            if (this.fDocument.isXMLVersionChanged()) {
                                final boolean wellformed = CoreDocumentImpl.isXMLName(node.getNodeName(), this.fDocument.isXML11Version());
                                if (!wellformed) {
                                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", node.getNodeName() });
                                    reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg2, (short)2, "wf-invalid-character-in-node-name");
                                }
                            }
                        }
                    }
                }
                if (this.fValidationHandler != null) {
                    this.fAttrProxy.setAttributes(attributes, this.fDocument, elem);
                    this.updateQName(elem, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                    this.fCurrentNode = node;
                    this.fValidationHandler.startElement(this.fQName, this.fAttrProxy, null);
                }
                if (this.fDTDValidator != null) {
                    this.fAttrProxy.setAttributes(attributes, this.fDocument, elem);
                    this.updateQName(elem, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                    this.fCurrentNode = node;
                    this.fDTDValidator.startElement(this.fQName, this.fAttrProxy, null);
                }
                Node next;
                for (Node kid = elem.getFirstChild(); kid != null; kid = next) {
                    next = kid.getNextSibling();
                    kid = this.normalizeNode(kid);
                    if (kid != null) {
                        next = kid;
                    }
                }
                if (this.fValidationHandler != null) {
                    this.updateQName(elem, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                    this.fCurrentNode = node;
                    this.fValidationHandler.endElement(this.fQName, null);
                }
                if (this.fDTDValidator != null) {
                    this.updateQName(elem, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                    this.fCurrentNode = node;
                    this.fDTDValidator.endElement(this.fQName, null);
                }
                this.fNamespaceContext.popContext();
                break;
            }
            case 8: {
                if ((this.fConfiguration.features & 0x20) == 0x0) {
                    final Node prevSibling = node.getPreviousSibling();
                    final Node parent = node.getParentNode();
                    parent.removeChild(node);
                    if (prevSibling != null && prevSibling.getNodeType() == 3) {
                        final Node nextSibling = prevSibling.getNextSibling();
                        if (nextSibling != null && nextSibling.getNodeType() == 3) {
                            ((TextImpl)nextSibling).insertData(0, prevSibling.getNodeValue());
                            parent.removeChild(prevSibling);
                            return nextSibling;
                        }
                    }
                    break;
                }
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                    final String commentdata = ((Comment)node).getData();
                    isCommentWF(this.fErrorHandler, this.fError, this.fLocator, commentdata, this.fDocument.isXML11Version());
                    break;
                }
                break;
            }
            case 5: {
                if ((this.fConfiguration.features & 0x4) == 0x0) {
                    final Node prevSibling = node.getPreviousSibling();
                    final Node parent = node.getParentNode();
                    ((EntityReferenceImpl)node).setReadOnly(false, true);
                    this.expandEntityRef(parent, node);
                    parent.removeChild(node);
                    final Node next2 = (prevSibling != null) ? prevSibling.getNextSibling() : parent.getFirstChild();
                    if (prevSibling != null && next2 != null && prevSibling.getNodeType() == 3 && next2.getNodeType() == 3) {
                        return prevSibling;
                    }
                    return next2;
                }
                else {
                    if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0 && this.fDocument.isXMLVersionChanged()) {
                        CoreDocumentImpl.isXMLName(node.getNodeName(), this.fDocument.isXML11Version());
                        break;
                    }
                    break;
                }
                break;
            }
            case 4: {
                if ((this.fConfiguration.features & 0x8) == 0x0) {
                    final Node prevSibling = node.getPreviousSibling();
                    if (prevSibling != null && prevSibling.getNodeType() == 3) {
                        ((Text)prevSibling).appendData(node.getNodeValue());
                        node.getParentNode().removeChild(node);
                        return prevSibling;
                    }
                    final Text text = this.fDocument.createTextNode(node.getNodeValue());
                    final Node parent2 = node.getParentNode();
                    node = parent2.replaceChild(text, node);
                    return text;
                }
                else {
                    if (this.fValidationHandler != null) {
                        this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                        this.fCurrentNode = node;
                        this.fValidationHandler.startCDATA(null);
                        this.fValidationHandler.characterData(node.getNodeValue(), null);
                        this.fValidationHandler.endCDATA(null);
                    }
                    if (this.fDTDValidator != null) {
                        this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                        this.fCurrentNode = node;
                        this.fDTDValidator.startCDATA(null);
                        this.fDTDValidator.characterData(node.getNodeValue(), null);
                        this.fDTDValidator.endCDATA(null);
                    }
                    String value = node.getNodeValue();
                    if ((this.fConfiguration.features & 0x10) != 0x0) {
                        final Node parent2 = node.getParentNode();
                        if (this.fDocument.errorChecking) {
                            isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), this.fDocument.isXML11Version());
                        }
                        int index;
                        while ((index = value.indexOf("]]>")) >= 0) {
                            node.setNodeValue(value.substring(0, index + 2));
                            value = value.substring(index + 2);
                            final Node firstSplitNode = node;
                            final Node newChild = this.fDocument.createCDATASection(value);
                            parent2.insertBefore(newChild, node.getNextSibling());
                            node = newChild;
                            this.fLocator.fRelatedNode = firstSplitNode;
                            final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "cdata-sections-splitted", null);
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg3, (short)1, "cdata-sections-splitted");
                        }
                        break;
                    }
                    if (this.fDocument.errorChecking) {
                        isCDataWF(this.fErrorHandler, this.fError, this.fLocator, value, this.fDocument.isXML11Version());
                        break;
                    }
                    break;
                }
                break;
            }
            case 3: {
                final Node next3 = node.getNextSibling();
                if (next3 != null && next3.getNodeType() == 3) {
                    ((Text)node).appendData(next3.getNodeValue());
                    node.getParentNode().removeChild(next3);
                    return node;
                }
                if (node.getNodeValue().length() == 0) {
                    node.getParentNode().removeChild(node);
                    break;
                }
                final short nextType = (short)((next3 != null) ? next3.getNodeType() : -1);
                if (nextType == -1 || (((this.fConfiguration.features & 0x4) != 0x0 || nextType != 6) && ((this.fConfiguration.features & 0x20) != 0x0 || nextType != 8) && ((this.fConfiguration.features & 0x8) != 0x0 || nextType != 4))) {
                    if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                        isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), this.fDocument.isXML11Version());
                    }
                    if (this.fValidationHandler != null) {
                        this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                        this.fCurrentNode = node;
                        this.fValidationHandler.characterData(node.getNodeValue(), null);
                    }
                    if (this.fDTDValidator != null) {
                        this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = node;
                        this.fCurrentNode = node;
                        this.fDTDValidator.characterData(node.getNodeValue(), null);
                        if (this.allWhitespace) {
                            this.allWhitespace = false;
                            ((TextImpl)node).setIgnorableWhitespace(true);
                        }
                    }
                }
                break;
            }
            case 7: {
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                    final ProcessingInstruction pinode = (ProcessingInstruction)node;
                    final String target = pinode.getTarget();
                    boolean wellformed;
                    if (this.fDocument.isXML11Version()) {
                        wellformed = XML11Char.isXML11ValidName(target);
                    }
                    else {
                        wellformed = XMLChar.isValidName(target);
                    }
                    if (!wellformed) {
                        final String msg4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
                        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg4, (short)2, "wf-invalid-character-in-node-name");
                    }
                    isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, pinode.getData(), this.fDocument.isXML11Version());
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    private XMLGrammarPool createGrammarPool(final DocumentTypeImpl docType) {
        final XMLGrammarPoolImpl pool = new XMLGrammarPoolImpl();
        final XMLGrammarPreparser preParser = new XMLGrammarPreparser(this.fSymbolTable);
        preParser.registerPreparser("http://www.w3.org/TR/REC-xml", null);
        preParser.setFeature("http://apache.org/xml/features/namespaces", true);
        preParser.setFeature("http://apache.org/xml/features/validation", true);
        preParser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", pool);
        final String internalSubset = docType.getInternalSubset();
        final XMLInputSource is = new XMLInputSource(docType.getPublicId(), docType.getSystemId(), null);
        if (internalSubset != null) {
            is.setCharacterStream(new StringReader(internalSubset));
        }
        try {
            DTDGrammar g = (DTDGrammar)preParser.preparseGrammar("http://www.w3.org/TR/REC-xml", is);
            ((XMLDTDDescription)g.getGrammarDescription()).setRootName(docType.getName());
            is.setCharacterStream(null);
            g = (DTDGrammar)preParser.preparseGrammar("http://www.w3.org/TR/REC-xml", is);
            ((XMLDTDDescription)g.getGrammarDescription()).setRootName(docType.getName());
        }
        catch (final XNIException ex) {}
        catch (final IOException ex2) {}
        return pool;
    }
    
    protected final void expandEntityRef(final Node parent, final Node reference) {
        Node next;
        for (Node kid = reference.getFirstChild(); kid != null; kid = next) {
            next = kid.getNextSibling();
            parent.insertBefore(kid, reference);
        }
    }
    
    protected final void namespaceFixUp(final ElementImpl element, final AttributeMap attributes) {
        if (attributes != null) {
            for (int k = 0; k < attributes.getLength(); ++k) {
                final Attr attr = (Attr)attributes.getItem(k);
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0 && this.fDocument.isXMLVersionChanged()) {
                    this.fDocument.checkQName(attr.getPrefix(), attr.getLocalName());
                }
                final String uri = attr.getNamespaceURI();
                if (uri != null && uri.equals(NamespaceContext.XMLNS_URI)) {
                    if ((this.fConfiguration.features & 0x200) != 0x0) {
                        String value = attr.getNodeValue();
                        if (value == null) {
                            value = XMLSymbols.EMPTY_STRING;
                        }
                        if (this.fDocument.errorChecking && value.equals(NamespaceContext.XMLNS_URI)) {
                            this.fLocator.fRelatedNode = attr;
                            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null);
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "CantBindXMLNS");
                        }
                        else {
                            String prefix = attr.getPrefix();
                            prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                            final String localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                            if (prefix == XMLSymbols.PREFIX_XMLNS) {
                                value = this.fSymbolTable.addSymbol(value);
                                if (value.length() != 0) {
                                    this.fNamespaceContext.declarePrefix(localpart, value);
                                }
                            }
                            else {
                                value = this.fSymbolTable.addSymbol(value);
                                this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, value);
                            }
                        }
                    }
                }
            }
        }
        String uri = element.getNamespaceURI();
        String prefix = element.getPrefix();
        if ((this.fConfiguration.features & 0x200) == 0x0) {
            uri = null;
        }
        else if (uri != null) {
            uri = this.fSymbolTable.addSymbol(uri);
            prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
            if (this.fNamespaceContext.getURI(prefix) != uri) {
                this.addNamespaceDecl(prefix, uri, element);
                this.fLocalNSBinder.declarePrefix(prefix, uri);
                this.fNamespaceContext.declarePrefix(prefix, uri);
            }
        }
        else if (element.getLocalName() == null) {
            if (this.fNamespaceValidation) {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { element.getNodeName() });
                reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg2, (short)3, "NullLocalElementName");
            }
            else {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { element.getNodeName() });
                reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg2, (short)2, "NullLocalElementName");
            }
        }
        else {
            uri = this.fNamespaceContext.getURI(XMLSymbols.EMPTY_STRING);
            if (uri != null && uri.length() > 0) {
                this.addNamespaceDecl(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING, element);
                this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
            }
        }
        if (attributes != null) {
            attributes.cloneMap(this.fAttributeList);
            for (int i = 0; i < this.fAttributeList.size(); ++i) {
                final Attr attr = this.fAttributeList.get(i);
                (this.fLocator.fRelatedNode = attr).normalize();
                String value = attr.getValue();
                String name = attr.getNodeName();
                uri = attr.getNamespaceURI();
                if (value == null) {
                    value = XMLSymbols.EMPTY_STRING;
                }
                if (uri != null) {
                    prefix = attr.getPrefix();
                    prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                    this.fSymbolTable.addSymbol(attr.getLocalName());
                    if (uri == null || !uri.equals(NamespaceContext.XMLNS_URI)) {
                        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                            isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, attr, attr.getValue(), this.fDocument.isXML11Version());
                            if (this.fDocument.isXMLVersionChanged()) {
                                final boolean wellformed = CoreDocumentImpl.isXMLName(attr.getNodeName(), this.fDocument.isXML11Version());
                                if (!wellformed) {
                                    final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attribute", attr.getNodeName() });
                                    reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg3, (short)2, "wf-invalid-character-in-node-name");
                                }
                            }
                        }
                        ((AttrImpl)attr).setIdAttribute(false);
                        uri = this.fSymbolTable.addSymbol(uri);
                        final String declaredURI = this.fNamespaceContext.getURI(prefix);
                        if (prefix == XMLSymbols.EMPTY_STRING || declaredURI != uri) {
                            name = attr.getNodeName();
                            final String declaredPrefix = this.fNamespaceContext.getPrefix(uri);
                            if (declaredPrefix != null && declaredPrefix != XMLSymbols.EMPTY_STRING) {
                                prefix = declaredPrefix;
                            }
                            else {
                                if (prefix == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(prefix) != null) {
                                    int counter;
                                    for (counter = 1, prefix = this.fSymbolTable.addSymbol("NS" + counter++); this.fLocalNSBinder.getURI(prefix) != null; prefix = this.fSymbolTable.addSymbol("NS" + counter++)) {}
                                }
                                this.addNamespaceDecl(prefix, uri, element);
                                value = this.fSymbolTable.addSymbol(value);
                                this.fLocalNSBinder.declarePrefix(prefix, value);
                                this.fNamespaceContext.declarePrefix(prefix, uri);
                            }
                            attr.setPrefix(prefix);
                        }
                    }
                }
                else {
                    ((AttrImpl)attr).setIdAttribute(false);
                    if (attr.getLocalName() == null) {
                        if (this.fNamespaceValidation) {
                            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "NullLocalAttrName");
                        }
                        else {
                            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)2, "NullLocalAttrName");
                        }
                    }
                }
            }
        }
    }
    
    protected final void addNamespaceDecl(final String prefix, final String uri, final ElementImpl element) {
        if (prefix == XMLSymbols.EMPTY_STRING) {
            element.setAttributeNS(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS, uri);
        }
        else {
            element.setAttributeNS(NamespaceContext.XMLNS_URI, "xmlns:" + prefix, uri);
        }
    }
    
    public static final void isCDataWF(final DOMErrorHandler errorHandler, final DOMErrorImpl error, final DOMLocatorImpl locator, final String datavalue, final boolean isXML11Version) {
        if (datavalue == null || datavalue.length() == 0) {
            return;
        }
        final char[] dataarray = datavalue.toCharArray();
        final int datalength = dataarray.length;
        if (isXML11Version) {
            int i = 0;
            while (i < datalength) {
                final char c = dataarray[i++];
                if (XML11Char.isXML11Invalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < datalength) {
                        final char c2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
                else {
                    if (c != ']') {
                        continue;
                    }
                    int count = i;
                    if (count >= datalength || dataarray[count] != ']') {
                        continue;
                    }
                    while (++count < datalength && dataarray[count] == ']') {}
                    if (count >= datalength || dataarray[count] != '>') {
                        continue;
                    }
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null);
                    reportDOMError(errorHandler, error, locator, msg2, (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int i = 0;
            while (i < datalength) {
                final char c = dataarray[i++];
                if (XMLChar.isInvalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < datalength) {
                        final char c2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
                else {
                    if (c != ']') {
                        continue;
                    }
                    int count = i;
                    if (count >= datalength || dataarray[count] != ']') {
                        continue;
                    }
                    while (++count < datalength && dataarray[count] == ']') {}
                    if (count >= datalength || dataarray[count] != '>') {
                        continue;
                    }
                    final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null);
                    reportDOMError(errorHandler, error, locator, msg2, (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isXMLCharWF(final DOMErrorHandler errorHandler, final DOMErrorImpl error, final DOMLocatorImpl locator, final String datavalue, final boolean isXML11Version) {
        if (datavalue == null || datavalue.length() == 0) {
            return;
        }
        final char[] dataarray = datavalue.toCharArray();
        final int datalength = dataarray.length;
        if (isXML11Version) {
            int i = 0;
            while (i < datalength) {
                if (XML11Char.isXML11Invalid(dataarray[i++])) {
                    final char ch = dataarray[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        final char ch2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(ch2) && XMLChar.isSupplemental(XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(dataarray[i - 1], 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int i = 0;
            while (i < datalength) {
                if (XMLChar.isInvalid(dataarray[i++])) {
                    final char ch = dataarray[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        final char ch2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(ch2) && XMLChar.isSupplemental(XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(dataarray[i - 1], 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isCommentWF(final DOMErrorHandler errorHandler, final DOMErrorImpl error, final DOMLocatorImpl locator, final String datavalue, final boolean isXML11Version) {
        if (datavalue == null || datavalue.length() == 0) {
            return;
        }
        final char[] dataarray = datavalue.toCharArray();
        final int datalength = dataarray.length;
        if (isXML11Version) {
            int i = 0;
            while (i < datalength) {
                final char c = dataarray[i++];
                if (XML11Char.isXML11Invalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < datalength) {
                        final char c2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(dataarray[i - 1], 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
                else {
                    if (c != '-' || i >= datalength || dataarray[i] != '-') {
                        continue;
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null);
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int i = 0;
            while (i < datalength) {
                final char c = dataarray[i++];
                if (XMLChar.isInvalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < datalength) {
                        final char c2 = dataarray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(dataarray[i - 1], 16) });
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
                else {
                    if (c != '-' || i >= datalength || dataarray[i] != '-') {
                        continue;
                    }
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null);
                    reportDOMError(errorHandler, error, locator, msg, (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isAttrValueWF(final DOMErrorHandler errorHandler, final DOMErrorImpl error, final DOMLocatorImpl locator, final NamedNodeMap attributes, final Attr a, final String value, final boolean xml11Version) {
        if (a instanceof AttrImpl && ((AttrImpl)a).hasStringValue()) {
            isXMLCharWF(errorHandler, error, locator, value, xml11Version);
        }
        else {
            final NodeList children = a.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                if (child.getNodeType() == 5) {
                    final Document owner = a.getOwnerDocument();
                    Entity ent = null;
                    if (owner != null) {
                        final DocumentType docType = owner.getDoctype();
                        if (docType != null) {
                            final NamedNodeMap entities = docType.getEntities();
                            ent = (Entity)entities.getNamedItemNS("*", child.getNodeName());
                        }
                    }
                    if (ent == null) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UndeclaredEntRefInAttrValue", new Object[] { a.getNodeName() });
                        reportDOMError(errorHandler, error, locator, msg, (short)2, "UndeclaredEntRefInAttrValue");
                    }
                }
                else {
                    isXMLCharWF(errorHandler, error, locator, child.getNodeValue(), xml11Version);
                }
            }
        }
    }
    
    public static final void reportDOMError(final DOMErrorHandler errorHandler, final DOMErrorImpl error, final DOMLocatorImpl locator, final String message, final short severity, final String type) {
        if (errorHandler != null) {
            error.reset();
            error.fMessage = message;
            error.fSeverity = severity;
            error.fLocator = locator;
            error.fType = type;
            error.fRelatedData = locator.fRelatedNode;
            if (!errorHandler.handleError(error)) {
                throw new AbortException();
            }
        }
        if (severity == 3) {
            throw new AbortException();
        }
    }
    
    protected final void updateQName(final Node node, final QName qname) {
        final String prefix = node.getPrefix();
        final String namespace = node.getNamespaceURI();
        final String localName = node.getLocalName();
        qname.prefix = ((prefix != null && prefix.length() != 0) ? this.fSymbolTable.addSymbol(prefix) : null);
        qname.localpart = ((localName != null) ? this.fSymbolTable.addSymbol(localName) : null);
        qname.rawname = this.fSymbolTable.addSymbol(node.getNodeName());
        qname.uri = ((namespace != null) ? this.fSymbolTable.addSymbol(namespace) : null);
    }
    
    final String normalizeAttributeValue(String value, final Attr attr) {
        if (!attr.getSpecified()) {
            return value;
        }
        final int end = value.length();
        if (this.fNormalizedValue.ch.length < end) {
            this.fNormalizedValue.ch = new char[end];
        }
        this.fNormalizedValue.length = 0;
        boolean normalized = false;
        for (int i = 0; i < end; ++i) {
            final char c = value.charAt(i);
            if (c == '\t' || c == '\n') {
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
                normalized = true;
            }
            else if (c == '\r') {
                normalized = true;
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
                final int next = i + 1;
                if (next < end && value.charAt(next) == '\n') {
                    i = next;
                }
            }
            else {
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = c;
            }
        }
        if (normalized) {
            value = this.fNormalizedValue.toString();
            attr.setValue(value);
        }
        return value;
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        final Element currentElement = (Element)this.fCurrentNode;
        for (int attrCount = attributes.getLength(), i = 0; i < attrCount; ++i) {
            attributes.getName(i, this.fAttrQName);
            Attr attr = null;
            attr = currentElement.getAttributeNodeNS(this.fAttrQName.uri, this.fAttrQName.localpart);
            final AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            if (attrPSVI != null) {
                XSTypeDefinition decl = attrPSVI.getMemberTypeDefinition();
                boolean id = false;
                if (decl != null) {
                    id = ((XSSimpleType)decl).isIDType();
                }
                else {
                    decl = attrPSVI.getTypeDefinition();
                    if (decl != null) {
                        id = ((XSSimpleType)decl).isIDType();
                    }
                }
                if (id) {
                    ((ElementImpl)currentElement).setIdAttributeNode(attr, true);
                }
                if (this.fPSVI) {
                    ((PSVIAttrNSImpl)attr).setPSVI(attrPSVI);
                }
                if ((this.fConfiguration.features & 0x2) != 0x0) {
                    final boolean specified = attr.getSpecified();
                    attr.setValue(attrPSVI.getSchemaNormalizedValue());
                    if (!specified) {
                        ((AttrImpl)attr).setSpecified(specified);
                    }
                }
            }
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.allWhitespace = true;
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (augs != null) {
            final ElementPSVI elementPSVI = (ElementPSVI)augs.getItem("ELEMENT_PSVI");
            if (elementPSVI != null) {
                final ElementImpl elementNode = (ElementImpl)this.fCurrentNode;
                if (this.fPSVI) {
                    ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
                }
                final String normalizedValue = elementPSVI.getSchemaNormalizedValue();
                if ((this.fConfiguration.features & 0x2) != 0x0) {
                    if (normalizedValue != null) {
                        elementNode.setTextContent(normalizedValue);
                    }
                }
                else {
                    final String text = elementNode.getTextContent();
                    if (text.length() == 0 && normalizedValue != null) {
                        elementNode.setTextContent(normalizedValue);
                    }
                }
            }
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    protected final class XMLAttributesProxy implements XMLAttributes
    {
        protected AttributeMap fAttributes;
        protected CoreDocumentImpl fDocument;
        protected ElementImpl fElement;
        protected final Vector fAugmentations;
        
        protected XMLAttributesProxy() {
            this.fAugmentations = new Vector(5);
        }
        
        public void setAttributes(final AttributeMap attributes, final CoreDocumentImpl doc, final ElementImpl elem) {
            this.fDocument = doc;
            this.fAttributes = attributes;
            this.fElement = elem;
            if (attributes != null) {
                final int length = attributes.getLength();
                this.fAugmentations.setSize(length);
                for (int i = 0; i < length; ++i) {
                    this.fAugmentations.setElementAt(new AugmentationsImpl(), i);
                }
            }
            else {
                this.fAugmentations.setSize(0);
            }
        }
        
        @Override
        public int addAttribute(final QName qname, final String attrType, final String attrValue) {
            int index = this.fElement.getXercesAttribute(qname.uri, qname.localpart);
            if (index < 0) {
                final AttrImpl attr = (AttrImpl)((CoreDocumentImpl)this.fElement.getOwnerDocument()).createAttributeNS(qname.uri, qname.rawname, qname.localpart);
                attr.setNodeValue(attrValue);
                index = this.fElement.setXercesAttributeNode(attr);
                this.fAugmentations.insertElementAt(new AugmentationsImpl(), index);
                attr.setSpecified(false);
            }
            return index;
        }
        
        @Override
        public void removeAllAttributes() {
        }
        
        @Override
        public void removeAttributeAt(final int attrIndex) {
        }
        
        @Override
        public int getLength() {
            return (this.fAttributes != null) ? this.fAttributes.getLength() : 0;
        }
        
        @Override
        public int getIndex(final String qName) {
            return -1;
        }
        
        @Override
        public int getIndex(final String uri, final String localPart) {
            return -1;
        }
        
        @Override
        public void setName(final int attrIndex, final QName attrName) {
        }
        
        @Override
        public void getName(final int attrIndex, final QName attrName) {
            if (this.fAttributes != null) {
                DOMNormalizer.this.updateQName((Node)this.fAttributes.getItem(attrIndex), attrName);
            }
        }
        
        @Override
        public String getPrefix(final int index) {
            return null;
        }
        
        @Override
        public String getURI(final int index) {
            return null;
        }
        
        @Override
        public String getLocalName(final int index) {
            return null;
        }
        
        @Override
        public String getQName(final int index) {
            return null;
        }
        
        @Override
        public QName getQualifiedName(final int index) {
            return null;
        }
        
        @Override
        public void setType(final int attrIndex, final String attrType) {
        }
        
        @Override
        public String getType(final int index) {
            return "CDATA";
        }
        
        @Override
        public String getType(final String qName) {
            return "CDATA";
        }
        
        @Override
        public String getType(final String uri, final String localName) {
            return "CDATA";
        }
        
        @Override
        public void setValue(final int attrIndex, final String attrValue) {
            if (this.fAttributes != null) {
                final AttrImpl attr = (AttrImpl)this.fAttributes.getItem(attrIndex);
                final boolean specified = attr.getSpecified();
                attr.setValue(attrValue);
                attr.setSpecified(specified);
            }
        }
        
        @Override
        public void setValue(final int attrIndex, final String attrValue, final XMLString value) {
            this.setValue(attrIndex, value.toString());
        }
        
        @Override
        public String getValue(final int index) {
            return (this.fAttributes != null) ? this.fAttributes.item(index).getNodeValue() : "";
        }
        
        @Override
        public String getValue(final String qName) {
            return null;
        }
        
        @Override
        public String getValue(final String uri, final String localName) {
            if (this.fAttributes != null) {
                final Node node = this.fAttributes.getNamedItemNS(uri, localName);
                return (node != null) ? node.getNodeValue() : null;
            }
            return null;
        }
        
        @Override
        public void setNonNormalizedValue(final int attrIndex, final String attrValue) {
        }
        
        @Override
        public String getNonNormalizedValue(final int attrIndex) {
            return null;
        }
        
        @Override
        public void setSpecified(final int attrIndex, final boolean specified) {
            final AttrImpl attr = (AttrImpl)this.fAttributes.getItem(attrIndex);
            attr.setSpecified(specified);
        }
        
        @Override
        public boolean isSpecified(final int attrIndex) {
            return ((Attr)this.fAttributes.getItem(attrIndex)).getSpecified();
        }
        
        @Override
        public Augmentations getAugmentations(final int attributeIndex) {
            return this.fAugmentations.elementAt(attributeIndex);
        }
        
        @Override
        public Augmentations getAugmentations(final String uri, final String localPart) {
            return null;
        }
        
        @Override
        public Augmentations getAugmentations(final String qName) {
            return null;
        }
        
        @Override
        public void setAugmentations(final int attrIndex, final Augmentations augs) {
            this.fAugmentations.setElementAt(augs, attrIndex);
        }
    }
}
