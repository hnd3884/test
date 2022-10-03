package org.apache.xerces.dom;

import org.apache.xerces.util.AugmentationsImpl;
import java.util.Vector;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.DOMError;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Entity;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentType;
import java.io.IOException;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.w3c.dom.CDATASection;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XML11Char;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;
import org.apache.xerces.xni.XMLAttributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.xni.XMLString;
import org.w3c.dom.Node;
import java.util.ArrayList;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.DOMErrorHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLDocumentHandler;

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
    private final QName fAttrQName;
    final XMLString fNormalizedValue;
    public static final RuntimeException abort;
    public static final XMLString EMPTY_STRING;
    private boolean fAllWhitespace;
    
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
        this.fAllWhitespace = false;
    }
    
    protected void normalizeDocument(final CoreDocumentImpl fDocument, final DOMConfigurationImpl fConfiguration) {
        this.fDocument = fDocument;
        this.fConfiguration = fConfiguration;
        this.fAllWhitespace = false;
        this.fNamespaceValidation = false;
        final String xmlVersion = this.fDocument.getXmlVersion();
        String s = null;
        String[] array = null;
        this.fSymbolTable = (SymbolTable)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fNamespaceContext.reset();
        this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, null);
        if ((this.fConfiguration.features & 0x40) != 0x0) {
            final String s2 = (String)this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            if (s2 != null && s2.equals(Constants.NS_XMLSCHEMA)) {
                s = "http://www.w3.org/2001/XMLSchema";
                this.fValidationHandler = CoreDOMImplementationImpl.singleton.getValidator(s, xmlVersion);
                this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema", true);
                this.fConfiguration.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
                this.fNamespaceValidation = true;
                this.fPSVI = ((this.fConfiguration.features & 0x80) != 0x0);
            }
            else {
                s = "http://www.w3.org/TR/REC-xml";
                if (s2 != null) {
                    array = (String[])this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
                }
                this.fConfiguration.setDTDValidatorFactory(xmlVersion);
                this.fValidationHandler = CoreDOMImplementationImpl.singleton.getValidator(s, xmlVersion);
                this.fPSVI = false;
            }
            this.fConfiguration.setFeature("http://xml.org/sax/features/validation", true);
            this.fDocument.clearIdentifiers();
            if (this.fValidationHandler != null) {
                ((XMLComponent)this.fValidationHandler).reset(this.fConfiguration);
            }
        }
        else {
            this.fValidationHandler = null;
        }
        this.fErrorHandler = (DOMErrorHandler)this.fConfiguration.getParameter("error-handler");
        if (this.fValidationHandler != null) {
            this.fValidationHandler.setDocumentHandler(this);
            this.fValidationHandler.startDocument(new SimpleLocator(this.fDocument.fDocumentURI, this.fDocument.fDocumentURI, -1, -1), this.fDocument.encoding, this.fNamespaceContext, null);
            this.fValidationHandler.xmlDecl(this.fDocument.getXmlVersion(), this.fDocument.getXmlEncoding(), this.fDocument.getXmlStandalone() ? "yes" : "no", null);
        }
        try {
            if (s == "http://www.w3.org/TR/REC-xml") {
                this.processDTD(xmlVersion, (array != null) ? array[0] : null);
            }
            Node nextSibling;
            for (Node firstChild = this.fDocument.getFirstChild(); firstChild != null; firstChild = nextSibling) {
                nextSibling = firstChild.getNextSibling();
                final Node normalizeNode = this.normalizeNode(firstChild);
                if (normalizeNode != null) {
                    nextSibling = normalizeNode;
                }
            }
            if (this.fValidationHandler != null) {
                this.fValidationHandler.endDocument(null);
                this.fValidationHandler.setDocumentHandler(null);
                CoreDOMImplementationImpl.singleton.releaseValidator(s, xmlVersion, this.fValidationHandler);
                this.fValidationHandler = null;
            }
        }
        catch (final RuntimeException ex) {
            if (this.fValidationHandler != null) {
                this.fValidationHandler.setDocumentHandler(null);
                CoreDOMImplementationImpl.singleton.releaseValidator(s, xmlVersion, this.fValidationHandler);
                this.fValidationHandler = null;
            }
            if (ex == DOMNormalizer.abort) {
                return;
            }
            throw ex;
        }
    }
    
    protected Node normalizeNode(Node replaceChild) {
        final short nodeType = replaceChild.getNodeType();
        this.fLocator.fRelatedNode = replaceChild;
        switch (nodeType) {
            case 1: {
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0 && this.fDocument.isXMLVersionChanged()) {
                    boolean b;
                    if (this.fNamespaceValidation) {
                        b = CoreDocumentImpl.isValidQName(replaceChild.getPrefix(), replaceChild.getLocalName(), this.fDocument.isXML11Version());
                    }
                    else {
                        b = CoreDocumentImpl.isXMLName(replaceChild.getNodeName(), this.fDocument.isXML11Version());
                    }
                    if (!b) {
                        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", replaceChild.getNodeName() }), (short)2, "wf-invalid-character-in-node-name");
                    }
                }
                this.fNamespaceContext.pushContext();
                this.fLocalNSBinder.reset();
                final ElementImpl elementImpl = (ElementImpl)replaceChild;
                if (elementImpl.needsSyncChildren()) {
                    elementImpl.synchronizeChildren();
                }
                AttributeMap attributeMap = elementImpl.hasAttributes() ? ((AttributeMap)elementImpl.getAttributes()) : null;
                if ((this.fConfiguration.features & 0x1) != 0x0) {
                    this.namespaceFixUp(elementImpl, attributeMap);
                    if ((this.fConfiguration.features & 0x200) == 0x0) {
                        if (attributeMap == null) {
                            attributeMap = (elementImpl.hasAttributes() ? ((AttributeMap)elementImpl.getAttributes()) : null);
                        }
                        if (attributeMap != null) {
                            for (int i = 0; i < attributeMap.getLength(); ++i) {
                                final Attr attr = (Attr)attributeMap.getItem(i);
                                if (XMLSymbols.PREFIX_XMLNS.equals(attr.getPrefix()) || XMLSymbols.PREFIX_XMLNS.equals(attr.getName())) {
                                    elementImpl.removeAttributeNode(attr);
                                    --i;
                                }
                            }
                        }
                    }
                }
                else if (attributeMap != null) {
                    for (int j = 0; j < attributeMap.getLength(); ++j) {
                        final Attr attr2 = (Attr)attributeMap.item(j);
                        attr2.normalize();
                        if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                            isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributeMap, attr2, attr2.getValue(), this.fDocument.isXML11Version());
                            if (this.fDocument.isXMLVersionChanged()) {
                                boolean b2;
                                if (this.fNamespaceValidation) {
                                    b2 = CoreDocumentImpl.isValidQName(replaceChild.getPrefix(), replaceChild.getLocalName(), this.fDocument.isXML11Version());
                                }
                                else {
                                    b2 = CoreDocumentImpl.isXMLName(replaceChild.getNodeName(), this.fDocument.isXML11Version());
                                }
                                if (!b2) {
                                    reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", replaceChild.getNodeName() }), (short)2, "wf-invalid-character-in-node-name");
                                }
                            }
                        }
                    }
                }
                if (this.fValidationHandler != null) {
                    this.fAttrProxy.setAttributes(attributeMap, this.fDocument, elementImpl);
                    this.updateQName(elementImpl, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = replaceChild;
                    this.fCurrentNode = replaceChild;
                    this.fValidationHandler.startElement(this.fQName, this.fAttrProxy, null);
                }
                Node nextSibling;
                for (Node firstChild = elementImpl.getFirstChild(); firstChild != null; firstChild = nextSibling) {
                    nextSibling = firstChild.getNextSibling();
                    final Node normalizeNode = this.normalizeNode(firstChild);
                    if (normalizeNode != null) {
                        nextSibling = normalizeNode;
                    }
                }
                if (this.fValidationHandler != null) {
                    this.updateQName(elementImpl, this.fQName);
                    this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = replaceChild;
                    this.fCurrentNode = replaceChild;
                    this.fValidationHandler.endElement(this.fQName, null);
                }
                this.fNamespaceContext.popContext();
                break;
            }
            case 8: {
                if ((this.fConfiguration.features & 0x20) == 0x0) {
                    final Node previousSibling = replaceChild.getPreviousSibling();
                    final Node parentNode = replaceChild.getParentNode();
                    parentNode.removeChild(replaceChild);
                    if (previousSibling == null || previousSibling.getNodeType() != 3) {
                        break;
                    }
                    final Node nextSibling2 = previousSibling.getNextSibling();
                    if (nextSibling2 != null && nextSibling2.getNodeType() == 3) {
                        ((TextImpl)nextSibling2).insertData(0, previousSibling.getNodeValue());
                        parentNode.removeChild(previousSibling);
                        return nextSibling2;
                    }
                    break;
                }
                else {
                    if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                        isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)replaceChild).getData(), this.fDocument.isXML11Version());
                    }
                    if (this.fValidationHandler != null) {
                        this.fValidationHandler.comment(DOMNormalizer.EMPTY_STRING, null);
                        break;
                    }
                    break;
                }
                break;
            }
            case 5: {
                if ((this.fConfiguration.features & 0x4) == 0x0) {
                    final Node previousSibling2 = replaceChild.getPreviousSibling();
                    final Node parentNode2 = replaceChild.getParentNode();
                    ((EntityReferenceImpl)replaceChild).setReadOnly(false, true);
                    this.expandEntityRef(parentNode2, replaceChild);
                    parentNode2.removeChild(replaceChild);
                    final Node node = (previousSibling2 != null) ? previousSibling2.getNextSibling() : parentNode2.getFirstChild();
                    if (previousSibling2 != null && node != null && previousSibling2.getNodeType() == 3 && node.getNodeType() == 3) {
                        return previousSibling2;
                    }
                    return node;
                }
                else {
                    if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0 && this.fDocument.isXMLVersionChanged()) {
                        CoreDocumentImpl.isXMLName(replaceChild.getNodeName(), this.fDocument.isXML11Version());
                        break;
                    }
                    break;
                }
                break;
            }
            case 4: {
                if ((this.fConfiguration.features & 0x8) == 0x0) {
                    final Node previousSibling3 = replaceChild.getPreviousSibling();
                    if (previousSibling3 != null && previousSibling3.getNodeType() == 3) {
                        ((Text)previousSibling3).appendData(replaceChild.getNodeValue());
                        replaceChild.getParentNode().removeChild(replaceChild);
                        return previousSibling3;
                    }
                    final Text textNode = this.fDocument.createTextNode(replaceChild.getNodeValue());
                    replaceChild = replaceChild.getParentNode().replaceChild(textNode, replaceChild);
                    return textNode;
                }
                else {
                    if (this.fValidationHandler != null) {
                        this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = replaceChild;
                        this.fCurrentNode = replaceChild;
                        this.fValidationHandler.startCDATA(null);
                        this.fValidationHandler.characterData(replaceChild.getNodeValue(), null);
                        this.fValidationHandler.endCDATA(null);
                    }
                    String s = replaceChild.getNodeValue();
                    if ((this.fConfiguration.features & 0x10) != 0x0) {
                        final Node parentNode3 = replaceChild.getParentNode();
                        if (this.fDocument.errorChecking) {
                            isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, replaceChild.getNodeValue(), this.fDocument.isXML11Version());
                        }
                        int index;
                        while ((index = s.indexOf("]]>")) >= 0) {
                            replaceChild.setNodeValue(s.substring(0, index + 2));
                            s = s.substring(index + 2);
                            final Node fRelatedNode = replaceChild;
                            final CDATASection cdataSection = this.fDocument.createCDATASection(s);
                            parentNode3.insertBefore(cdataSection, replaceChild.getNextSibling());
                            replaceChild = cdataSection;
                            this.fLocator.fRelatedNode = fRelatedNode;
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "cdata-sections-splitted", null), (short)1, "cdata-sections-splitted");
                        }
                        break;
                    }
                    if (this.fDocument.errorChecking) {
                        isCDataWF(this.fErrorHandler, this.fError, this.fLocator, s, this.fDocument.isXML11Version());
                        break;
                    }
                    break;
                }
                break;
            }
            case 3: {
                final Node nextSibling3 = replaceChild.getNextSibling();
                if (nextSibling3 != null && nextSibling3.getNodeType() == 3) {
                    ((Text)replaceChild).appendData(nextSibling3.getNodeValue());
                    replaceChild.getParentNode().removeChild(nextSibling3);
                    return replaceChild;
                }
                if (replaceChild.getNodeValue().length() == 0) {
                    replaceChild.getParentNode().removeChild(replaceChild);
                    break;
                }
                final short n = (short)((nextSibling3 != null) ? nextSibling3.getNodeType() : -1);
                if (n != -1 && (((this.fConfiguration.features & 0x4) == 0x0 && n == 6) || ((this.fConfiguration.features & 0x20) == 0x0 && n == 8) || ((this.fConfiguration.features & 0x8) == 0x0 && n == 4))) {
                    break;
                }
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                    isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, replaceChild.getNodeValue(), this.fDocument.isXML11Version());
                }
                if (this.fValidationHandler == null) {
                    break;
                }
                this.fConfiguration.fErrorHandlerWrapper.fCurrentNode = replaceChild;
                this.fCurrentNode = replaceChild;
                this.fValidationHandler.characterData(replaceChild.getNodeValue(), null);
                if (this.fNamespaceValidation) {
                    break;
                }
                if (this.fAllWhitespace) {
                    this.fAllWhitespace = false;
                    ((TextImpl)replaceChild).setIgnorableWhitespace(true);
                    break;
                }
                ((TextImpl)replaceChild).setIgnorableWhitespace(false);
                break;
            }
            case 7: {
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                    final ProcessingInstruction processingInstruction = (ProcessingInstruction)replaceChild;
                    final String target = processingInstruction.getTarget();
                    boolean b3;
                    if (this.fDocument.isXML11Version()) {
                        b3 = XML11Char.isXML11ValidName(target);
                    }
                    else {
                        b3 = XMLChar.isValidName(target);
                    }
                    if (!b3) {
                        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", replaceChild.getNodeName() }), (short)2, "wf-invalid-character-in-node-name");
                    }
                    isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, processingInstruction.getData(), this.fDocument.isXML11Version());
                }
                if (this.fValidationHandler != null) {
                    this.fValidationHandler.processingInstruction(((ProcessingInstruction)replaceChild).getTarget(), DOMNormalizer.EMPTY_STRING, null);
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    private void processDTD(final String s, final String s2) {
        String publicId = null;
        String systemId = s2;
        final String documentURI = this.fDocument.getDocumentURI();
        String internalSubset = null;
        final DocumentType doctype = this.fDocument.getDoctype();
        String s3;
        if (doctype != null) {
            s3 = doctype.getName();
            publicId = doctype.getPublicId();
            if (systemId == null || systemId.length() == 0) {
                systemId = doctype.getSystemId();
            }
            internalSubset = doctype.getInternalSubset();
        }
        else {
            final Element documentElement = this.fDocument.getDocumentElement();
            if (documentElement == null) {
                return;
            }
            s3 = documentElement.getNodeName();
            if (systemId == null || systemId.length() == 0) {
                return;
            }
        }
        XMLDTDLoader dtdLoader = null;
        try {
            this.fValidationHandler.doctypeDecl(s3, publicId, systemId, null);
            dtdLoader = CoreDOMImplementationImpl.singleton.getDTDLoader(s);
            dtdLoader.setFeature("http://xml.org/sax/features/validation", true);
            dtdLoader.setEntityResolver(this.fConfiguration.getEntityResolver());
            dtdLoader.setErrorHandler(this.fConfiguration.getErrorHandler());
            dtdLoader.loadGrammarWithContext((XMLDTDValidator)this.fValidationHandler, s3, publicId, systemId, documentURI, internalSubset);
        }
        catch (final IOException ex) {}
        finally {
            if (dtdLoader != null) {
                CoreDOMImplementationImpl.singleton.releaseDTDLoader(s, dtdLoader);
            }
        }
    }
    
    protected final void expandEntityRef(final Node node, final Node node2) {
        Node nextSibling;
        for (Node firstChild = node2.getFirstChild(); firstChild != null; firstChild = nextSibling) {
            nextSibling = firstChild.getNextSibling();
            node.insertBefore(firstChild, node2);
        }
    }
    
    protected final void namespaceFixUp(final ElementImpl elementImpl, final AttributeMap attributeMap) {
        if (attributeMap != null) {
            for (int i = 0; i < attributeMap.getLength(); ++i) {
                final Attr fRelatedNode = (Attr)attributeMap.getItem(i);
                final String namespaceURI = fRelatedNode.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(NamespaceContext.XMLNS_URI)) {
                    String s = fRelatedNode.getNodeValue();
                    if (s == null) {
                        s = XMLSymbols.EMPTY_STRING;
                    }
                    if (this.fDocument.errorChecking && s.equals(NamespaceContext.XMLNS_URI)) {
                        this.fLocator.fRelatedNode = fRelatedNode;
                        reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null), (short)2, "CantBindXMLNS");
                    }
                    else {
                        final String prefix = fRelatedNode.getPrefix();
                        final String s2 = (prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix);
                        final String addSymbol = this.fSymbolTable.addSymbol(fRelatedNode.getLocalName());
                        if (s2 == XMLSymbols.PREFIX_XMLNS) {
                            final String addSymbol2 = this.fSymbolTable.addSymbol(s);
                            if (addSymbol2.length() != 0) {
                                this.fNamespaceContext.declarePrefix(addSymbol, addSymbol2);
                            }
                        }
                        else {
                            final String addSymbol3 = this.fSymbolTable.addSymbol(s);
                            this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, (addSymbol3.length() != 0) ? addSymbol3 : null);
                        }
                    }
                }
            }
        }
        final String namespaceURI2 = elementImpl.getNamespaceURI();
        final String prefix2 = elementImpl.getPrefix();
        if (namespaceURI2 != null) {
            final String addSymbol4 = this.fSymbolTable.addSymbol(namespaceURI2);
            final String s3 = (prefix2 == null || prefix2.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix2);
            if (this.fNamespaceContext.getURI(s3) != addSymbol4) {
                this.addNamespaceDecl(s3, addSymbol4, elementImpl);
                this.fLocalNSBinder.declarePrefix(s3, addSymbol4);
                this.fNamespaceContext.declarePrefix(s3, addSymbol4);
            }
        }
        else if (elementImpl.getLocalName() == null) {
            if (this.fNamespaceValidation) {
                reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { elementImpl.getNodeName() }), (short)3, "NullLocalElementName");
            }
            else {
                reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { elementImpl.getNodeName() }), (short)2, "NullLocalElementName");
            }
        }
        else {
            final String uri = this.fNamespaceContext.getURI(XMLSymbols.EMPTY_STRING);
            if (uri != null && uri.length() > 0) {
                this.addNamespaceDecl(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING, elementImpl);
                this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, null);
                this.fNamespaceContext.declarePrefix(XMLSymbols.EMPTY_STRING, null);
            }
        }
        if (attributeMap != null) {
            attributeMap.cloneMap(this.fAttributeList);
            for (int j = 0; j < this.fAttributeList.size(); ++j) {
                final Attr fRelatedNode2 = this.fAttributeList.get(j);
                (this.fLocator.fRelatedNode = fRelatedNode2).normalize();
                String s4 = fRelatedNode2.getValue();
                final String namespaceURI3 = fRelatedNode2.getNamespaceURI();
                if (s4 == null) {
                    s4 = XMLSymbols.EMPTY_STRING;
                }
                if (this.fDocument.errorChecking && (this.fConfiguration.features & 0x100) != 0x0) {
                    isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributeMap, fRelatedNode2, s4, this.fDocument.isXML11Version());
                    if (this.fDocument.isXMLVersionChanged()) {
                        boolean b;
                        if (this.fNamespaceValidation) {
                            b = CoreDocumentImpl.isValidQName(fRelatedNode2.getPrefix(), fRelatedNode2.getLocalName(), this.fDocument.isXML11Version());
                        }
                        else {
                            b = CoreDocumentImpl.isXMLName(fRelatedNode2.getNodeName(), this.fDocument.isXML11Version());
                        }
                        if (!b) {
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", fRelatedNode2.getNodeName() }), (short)2, "wf-invalid-character-in-node-name");
                        }
                    }
                }
                if (namespaceURI3 != null) {
                    final String prefix3 = fRelatedNode2.getPrefix();
                    String prefix4 = (prefix3 == null || prefix3.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix3);
                    this.fSymbolTable.addSymbol(fRelatedNode2.getLocalName());
                    if (namespaceURI3 == null || !namespaceURI3.equals(NamespaceContext.XMLNS_URI)) {
                        ((AttrImpl)fRelatedNode2).setIdAttribute(false);
                        final String addSymbol5 = this.fSymbolTable.addSymbol(namespaceURI3);
                        final String uri2 = this.fNamespaceContext.getURI(prefix4);
                        if (prefix4 == XMLSymbols.EMPTY_STRING || uri2 != addSymbol5) {
                            final String prefix5 = this.fNamespaceContext.getPrefix(addSymbol5);
                            if (prefix5 != null && prefix5 != XMLSymbols.EMPTY_STRING) {
                                prefix4 = prefix5;
                            }
                            else {
                                if (prefix4 == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(prefix4) != null) {
                                    int n;
                                    for (n = 1, prefix4 = this.fSymbolTable.addSymbol("NS" + n++); this.fLocalNSBinder.getURI(prefix4) != null; prefix4 = this.fSymbolTable.addSymbol("NS" + n++)) {}
                                }
                                this.addNamespaceDecl(prefix4, addSymbol5, elementImpl);
                                this.fLocalNSBinder.declarePrefix(prefix4, this.fSymbolTable.addSymbol(s4));
                                this.fNamespaceContext.declarePrefix(prefix4, addSymbol5);
                            }
                            fRelatedNode2.setPrefix(prefix4);
                        }
                    }
                }
                else {
                    ((AttrImpl)fRelatedNode2).setIdAttribute(false);
                    if (fRelatedNode2.getLocalName() == null) {
                        if (this.fNamespaceValidation) {
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { fRelatedNode2.getNodeName() }), (short)3, "NullLocalAttrName");
                        }
                        else {
                            reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { fRelatedNode2.getNodeName() }), (short)2, "NullLocalAttrName");
                        }
                    }
                }
            }
        }
    }
    
    protected final void addNamespaceDecl(final String s, final String s2, final ElementImpl elementImpl) {
        if (s == XMLSymbols.EMPTY_STRING) {
            elementImpl.setAttributeNS(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS, s2);
        }
        else {
            elementImpl.setAttributeNS(NamespaceContext.XMLNS_URI, "xmlns:" + s, s2);
        }
    }
    
    public static final void isCDataWF(final DOMErrorHandler domErrorHandler, final DOMErrorImpl domErrorImpl, final DOMLocatorImpl domLocatorImpl, final String s, final boolean b) {
        if (s == null || s.length() == 0) {
            return;
        }
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        if (b) {
            int i = 0;
            while (i < length) {
                final char c = charArray[i++];
                if (XML11Char.isXML11Invalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < length) {
                        final char c2 = charArray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) }), (short)2, "wf-invalid-character");
                }
                else {
                    if (c != ']') {
                        continue;
                    }
                    int n = i;
                    if (n >= length || charArray[n] != ']') {
                        continue;
                    }
                    while (++n < length && charArray[n] == ']') {}
                    if (n >= length || charArray[n] != '>') {
                        continue;
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null), (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int j = 0;
            while (j < length) {
                final char c3 = charArray[j++];
                if (XMLChar.isInvalid(c3)) {
                    if (XMLChar.isHighSurrogate(c3) && j < length) {
                        final char c4 = charArray[j++];
                        if (XMLChar.isLowSurrogate(c4) && XMLChar.isSupplemental(XMLChar.supplemental(c3, c4))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect", new Object[] { Integer.toString(c3, 16) }), (short)2, "wf-invalid-character");
                }
                else {
                    if (c3 != ']') {
                        continue;
                    }
                    int n2 = j;
                    if (n2 >= length || charArray[n2] != ']') {
                        continue;
                    }
                    while (++n2 < length && charArray[n2] == ']') {}
                    if (n2 >= length || charArray[n2] != '>') {
                        continue;
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent", null), (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isXMLCharWF(final DOMErrorHandler domErrorHandler, final DOMErrorImpl domErrorImpl, final DOMLocatorImpl domLocatorImpl, final String s, final boolean b) {
        if (s == null || s.length() == 0) {
            return;
        }
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        if (b) {
            int i = 0;
            while (i < length) {
                if (XML11Char.isXML11Invalid(charArray[i++])) {
                    final char c = charArray[i - 1];
                    if (XMLChar.isHighSurrogate(c) && i < length) {
                        final char c2 = charArray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(charArray[i - 1], 16) }), (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int j = 0;
            while (j < length) {
                if (XMLChar.isInvalid(charArray[j++])) {
                    final char c3 = charArray[j - 1];
                    if (XMLChar.isHighSurrogate(c3) && j < length) {
                        final char c4 = charArray[j++];
                        if (XMLChar.isLowSurrogate(c4) && XMLChar.isSupplemental(XMLChar.supplemental(c3, c4))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidXMLCharInDOM", new Object[] { Integer.toString(charArray[j - 1], 16) }), (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isCommentWF(final DOMErrorHandler domErrorHandler, final DOMErrorImpl domErrorImpl, final DOMLocatorImpl domLocatorImpl, final String s, final boolean b) {
        if (s == null || s.length() == 0) {
            return;
        }
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        if (b) {
            int i = 0;
            while (i < length) {
                final char c = charArray[i++];
                if (XML11Char.isXML11Invalid(c)) {
                    if (XMLChar.isHighSurrogate(c) && i < length) {
                        final char c2 = charArray[i++];
                        if (XMLChar.isLowSurrogate(c2) && XMLChar.isSupplemental(XMLChar.supplemental(c, c2))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(charArray[i - 1], 16) }), (short)2, "wf-invalid-character");
                }
                else {
                    if (c != '-' || i >= length || charArray[i] != '-') {
                        continue;
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null), (short)2, "wf-invalid-character");
                }
            }
        }
        else {
            int j = 0;
            while (j < length) {
                final char c3 = charArray[j++];
                if (XMLChar.isInvalid(c3)) {
                    if (XMLChar.isHighSurrogate(c3) && j < length) {
                        final char c4 = charArray[j++];
                        if (XMLChar.isLowSurrogate(c4) && XMLChar.isSupplemental(XMLChar.supplemental(c3, c4))) {
                            continue;
                        }
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInComment", new Object[] { Integer.toString(charArray[j - 1], 16) }), (short)2, "wf-invalid-character");
                }
                else {
                    if (c3 != '-' || j >= length || charArray[j] != '-') {
                        continue;
                    }
                    reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "DashDashInComment", null), (short)2, "wf-invalid-character");
                }
            }
        }
    }
    
    public static final void isAttrValueWF(final DOMErrorHandler domErrorHandler, final DOMErrorImpl domErrorImpl, final DOMLocatorImpl domLocatorImpl, final NamedNodeMap namedNodeMap, final Attr attr, final String s, final boolean b) {
        if (attr instanceof AttrImpl && ((AttrImpl)attr).hasStringValue()) {
            isXMLCharWF(domErrorHandler, domErrorImpl, domLocatorImpl, s, b);
        }
        else {
            final NodeList childNodes = attr.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node item = childNodes.item(i);
                if (item.getNodeType() == 5) {
                    final Document ownerDocument = attr.getOwnerDocument();
                    Entity entity = null;
                    if (ownerDocument != null) {
                        final DocumentType doctype = ownerDocument.getDoctype();
                        if (doctype != null) {
                            entity = (Entity)doctype.getEntities().getNamedItemNS("*", item.getNodeName());
                        }
                    }
                    if (entity == null) {
                        reportDOMError(domErrorHandler, domErrorImpl, domLocatorImpl, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UndeclaredEntRefInAttrValue", new Object[] { attr.getNodeName() }), (short)2, "UndeclaredEntRefInAttrValue");
                    }
                }
                else {
                    isXMLCharWF(domErrorHandler, domErrorImpl, domLocatorImpl, item.getNodeValue(), b);
                }
            }
        }
    }
    
    public static final void reportDOMError(final DOMErrorHandler domErrorHandler, final DOMErrorImpl domErrorImpl, final DOMLocatorImpl fLocator, final String fMessage, final short fSeverity, final String fType) {
        if (domErrorHandler != null) {
            domErrorImpl.reset();
            domErrorImpl.fMessage = fMessage;
            domErrorImpl.fSeverity = fSeverity;
            domErrorImpl.fLocator = fLocator;
            domErrorImpl.fType = fType;
            domErrorImpl.fRelatedData = fLocator.fRelatedNode;
            if (!domErrorHandler.handleError(domErrorImpl)) {
                throw DOMNormalizer.abort;
            }
        }
        if (fSeverity == 3) {
            throw DOMNormalizer.abort;
        }
    }
    
    protected final void updateQName(final Node node, final QName qName) {
        final String prefix = node.getPrefix();
        final String namespaceURI = node.getNamespaceURI();
        final String localName = node.getLocalName();
        qName.prefix = ((prefix != null && prefix.length() != 0) ? this.fSymbolTable.addSymbol(prefix) : null);
        qName.localpart = ((localName != null) ? this.fSymbolTable.addSymbol(localName) : null);
        qName.rawname = this.fSymbolTable.addSymbol(node.getNodeName());
        qName.uri = ((namespaceURI != null) ? this.fSymbolTable.addSymbol(namespaceURI) : null);
    }
    
    final String normalizeAttributeValue(String string, final Attr attr) {
        if (!attr.getSpecified()) {
            return string;
        }
        final int length = string.length();
        if (this.fNormalizedValue.ch.length < length) {
            this.fNormalizedValue.ch = new char[length];
        }
        this.fNormalizedValue.length = 0;
        boolean b = false;
        for (int i = 0; i < length; ++i) {
            final char char1 = string.charAt(i);
            if (char1 == '\t' || char1 == '\n') {
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
                b = true;
            }
            else if (char1 == '\r') {
                b = true;
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = ' ';
                final int n = i + 1;
                if (n < length && string.charAt(n) == '\n') {
                    i = n;
                }
            }
            else {
                this.fNormalizedValue.ch[this.fNormalizedValue.length++] = char1;
            }
        }
        if (b) {
            string = this.fNormalizedValue.toString();
            attr.setValue(string);
        }
        return string;
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        final Element element = (Element)this.fCurrentNode;
        for (int length = xmlAttributes.getLength(), i = 0; i < length; ++i) {
            xmlAttributes.getName(i, this.fAttrQName);
            Attr attr = element.getAttributeNodeNS(this.fAttrQName.uri, this.fAttrQName.localpart);
            if (attr == null) {
                attr = element.getAttributeNode(this.fAttrQName.rawname);
            }
            final AttributePSVI psvi = (AttributePSVI)xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
            if (psvi != null) {
                XSTypeDefinition type = psvi.getMemberTypeDefinition();
                boolean b = false;
                if (type != null) {
                    b = ((XSSimpleType)type).isIDType();
                }
                else {
                    type = psvi.getTypeDefinition();
                    if (type != null) {
                        b = ((XSSimpleType)type).isIDType();
                    }
                }
                if (b) {
                    ((ElementImpl)element).setIdAttributeNode(attr, true);
                }
                if (this.fPSVI) {
                    ((PSVIAttrNSImpl)attr).setPSVI(psvi);
                }
                ((AttrImpl)attr).setType(type);
                if ((this.fConfiguration.features & 0x2) != 0x0) {
                    final String schemaNormalizedValue = psvi.getSchemaNormalizedValue();
                    if (schemaNormalizedValue != null) {
                        final boolean specified = attr.getSpecified();
                        attr.setValue(schemaNormalizedValue);
                        if (!specified) {
                            ((AttrImpl)attr).setSpecified(specified);
                        }
                    }
                }
            }
            else {
                Object type2 = null;
                if (Boolean.TRUE.equals(xmlAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"))) {
                    type2 = xmlAttributes.getType(i);
                    if ("ID".equals(type2)) {
                        ((ElementImpl)element).setIdAttributeNode(attr, true);
                    }
                }
                ((AttrImpl)attr).setType(type2);
            }
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.fAllWhitespace = true;
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (augmentations != null) {
            final ElementPSVI psvi = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI");
            if (psvi != null) {
                final ElementImpl elementImpl = (ElementImpl)this.fCurrentNode;
                if (this.fPSVI) {
                    ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(psvi);
                }
                if (elementImpl instanceof ElementNSImpl) {
                    XSTypeDefinition type = psvi.getMemberTypeDefinition();
                    if (type == null) {
                        type = psvi.getTypeDefinition();
                    }
                    ((ElementNSImpl)elementImpl).setType(type);
                }
                final String schemaNormalizedValue = psvi.getSchemaNormalizedValue();
                if ((this.fConfiguration.features & 0x2) != 0x0) {
                    if (schemaNormalizedValue != null) {
                        elementImpl.setTextContent(schemaNormalizedValue);
                    }
                }
                else if (elementImpl.getTextContent().length() == 0 && schemaNormalizedValue != null) {
                    elementImpl.setTextContent(schemaNormalizedValue);
                }
                return;
            }
        }
        if (this.fCurrentNode instanceof ElementNSImpl) {
            ((ElementNSImpl)this.fCurrentNode).setType(null);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
    }
    
    public void setDocumentSource(final XMLDocumentSource xmlDocumentSource) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    static {
        abort = new RuntimeException() {
            private static final long serialVersionUID = 5361322877988412432L;
            
            public Throwable fillInStackTrace() {
                return this;
            }
        };
        EMPTY_STRING = new XMLString();
    }
    
    protected final class XMLAttributesProxy implements XMLAttributes
    {
        protected AttributeMap fAttributes;
        protected CoreDocumentImpl fDocument;
        protected ElementImpl fElement;
        protected final Vector fDTDTypes;
        protected final Vector fAugmentations;
        
        protected XMLAttributesProxy() {
            this.fDTDTypes = new Vector(5);
            this.fAugmentations = new Vector(5);
        }
        
        public void setAttributes(final AttributeMap fAttributes, final CoreDocumentImpl fDocument, final ElementImpl fElement) {
            this.fDocument = fDocument;
            this.fAttributes = fAttributes;
            this.fElement = fElement;
            if (fAttributes != null) {
                final int length = fAttributes.getLength();
                this.fDTDTypes.setSize(length);
                this.fAugmentations.setSize(length);
                for (int i = 0; i < length; ++i) {
                    this.fAugmentations.setElementAt(new AugmentationsImpl(), i);
                }
            }
            else {
                this.fDTDTypes.setSize(0);
                this.fAugmentations.setSize(0);
            }
        }
        
        public int addAttribute(final QName qName, final String s, final String nodeValue) {
            int n = this.fElement.getXercesAttribute(qName.uri, qName.localpart);
            if (n < 0) {
                final AttrImpl xercesAttributeNode = (AttrImpl)((CoreDocumentImpl)this.fElement.getOwnerDocument()).createAttributeNS(qName.uri, qName.rawname, qName.localpart);
                xercesAttributeNode.setNodeValue(nodeValue);
                n = this.fElement.setXercesAttributeNode(xercesAttributeNode);
                this.fDTDTypes.insertElementAt(s, n);
                this.fAugmentations.insertElementAt(new AugmentationsImpl(), n);
                xercesAttributeNode.setSpecified(false);
            }
            return n;
        }
        
        public void removeAllAttributes() {
        }
        
        public void removeAttributeAt(final int n) {
        }
        
        public int getLength() {
            return (this.fAttributes != null) ? this.fAttributes.getLength() : 0;
        }
        
        public int getIndex(final String s) {
            return -1;
        }
        
        public int getIndex(final String s, final String s2) {
            return -1;
        }
        
        public void setName(final int n, final QName qName) {
        }
        
        public void getName(final int n, final QName qName) {
            if (this.fAttributes != null) {
                DOMNormalizer.this.updateQName((Node)this.fAttributes.getItem(n), qName);
            }
        }
        
        public String getPrefix(final int n) {
            if (this.fAttributes != null) {
                final String prefix = ((Node)this.fAttributes.getItem(n)).getPrefix();
                return (prefix != null && prefix.length() != 0) ? DOMNormalizer.this.fSymbolTable.addSymbol(prefix) : null;
            }
            return null;
        }
        
        public String getURI(final int n) {
            if (this.fAttributes != null) {
                final String namespaceURI = ((Node)this.fAttributes.getItem(n)).getNamespaceURI();
                return (namespaceURI != null) ? DOMNormalizer.this.fSymbolTable.addSymbol(namespaceURI) : null;
            }
            return null;
        }
        
        public String getLocalName(final int n) {
            if (this.fAttributes != null) {
                final String localName = ((Node)this.fAttributes.getItem(n)).getLocalName();
                return (localName != null) ? DOMNormalizer.this.fSymbolTable.addSymbol(localName) : null;
            }
            return null;
        }
        
        public String getQName(final int n) {
            if (this.fAttributes != null) {
                return DOMNormalizer.this.fSymbolTable.addSymbol(((Node)this.fAttributes.getItem(n)).getNodeName());
            }
            return null;
        }
        
        public void setType(final int n, final String s) {
            this.fDTDTypes.setElementAt(s, n);
        }
        
        public String getType(final int n) {
            final String s = this.fDTDTypes.elementAt(n);
            return (s != null) ? this.getReportableType(s) : "CDATA";
        }
        
        public String getType(final String s) {
            return "CDATA";
        }
        
        public String getType(final String s, final String s2) {
            return "CDATA";
        }
        
        private String getReportableType(final String s) {
            if (s.charAt(0) == '(') {
                return "NMTOKEN";
            }
            return s;
        }
        
        public void setValue(final int n, final String value) {
            if (this.fAttributes != null) {
                final AttrImpl attrImpl = (AttrImpl)this.fAttributes.getItem(n);
                final boolean specified = attrImpl.getSpecified();
                attrImpl.setValue(value);
                attrImpl.setSpecified(specified);
            }
        }
        
        public String getValue(final int n) {
            return (this.fAttributes != null) ? this.fAttributes.item(n).getNodeValue() : "";
        }
        
        public String getValue(final String s) {
            return null;
        }
        
        public String getValue(final String s, final String s2) {
            if (this.fAttributes != null) {
                final Node namedItemNS = this.fAttributes.getNamedItemNS(s, s2);
                return (namedItemNS != null) ? namedItemNS.getNodeValue() : null;
            }
            return null;
        }
        
        public void setNonNormalizedValue(final int n, final String s) {
        }
        
        public String getNonNormalizedValue(final int n) {
            return null;
        }
        
        public void setSpecified(final int n, final boolean specified) {
            ((AttrImpl)this.fAttributes.getItem(n)).setSpecified(specified);
        }
        
        public boolean isSpecified(final int n) {
            return ((Attr)this.fAttributes.getItem(n)).getSpecified();
        }
        
        public Augmentations getAugmentations(final int n) {
            return this.fAugmentations.elementAt(n);
        }
        
        public Augmentations getAugmentations(final String s, final String s2) {
            return null;
        }
        
        public Augmentations getAugmentations(final String s) {
            return null;
        }
        
        public void setAugmentations(final int n, final Augmentations augmentations) {
            this.fAugmentations.setElementAt(augmentations, n);
        }
    }
}
