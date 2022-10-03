package org.apache.xml.serialize;

import org.w3c.dom.NamedNodeMap;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XML11Char;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import org.apache.xerces.dom.CoreDocumentImpl;
import java.lang.reflect.Method;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.xerces.impl.XMLEntityManager;
import org.w3c.dom.ls.LSOutput;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.NamespaceSupport;
import org.w3c.dom.ls.LSSerializerFilter;
import java.io.IOException;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.dom.DOMNormalizer;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import java.io.Writer;
import java.io.StringWriter;
import org.w3c.dom.Node;
import org.apache.xerces.dom.DOMStringListImpl;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMLocatorImpl;
import org.apache.xerces.dom.DOMErrorImpl;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSSerializer;

public class DOMSerializerImpl implements LSSerializer, DOMConfiguration
{
    private XMLSerializer serializer;
    private XML11Serializer xml11Serializer;
    private DOMStringList fRecognizedParameters;
    protected short features;
    protected static final short NAMESPACES = 1;
    protected static final short WELLFORMED = 2;
    protected static final short ENTITIES = 4;
    protected static final short CDATA = 8;
    protected static final short SPLITCDATA = 16;
    protected static final short COMMENTS = 32;
    protected static final short DISCARDDEFAULT = 64;
    protected static final short INFOSET = 128;
    protected static final short XMLDECL = 256;
    protected static final short NSDECL = 512;
    protected static final short DOM_ELEMENT_CONTENT_WHITESPACE = 1024;
    protected static final short PRETTY_PRINT = 2048;
    private DOMErrorHandler fErrorHandler;
    private final DOMErrorImpl fError;
    private final DOMLocatorImpl fLocator;
    
    public DOMSerializerImpl() {
        this.features = 0;
        this.fErrorHandler = null;
        this.fError = new DOMErrorImpl();
        this.fLocator = new DOMLocatorImpl();
        this.features |= 0x1;
        this.features |= 0x4;
        this.features |= 0x20;
        this.features |= 0x8;
        this.features |= 0x10;
        this.features |= 0x2;
        this.features |= 0x200;
        this.features |= 0x400;
        this.features |= 0x40;
        this.features |= 0x100;
        this.initSerializer(this.serializer = new XMLSerializer());
    }
    
    public DOMConfiguration getDomConfig() {
        return this;
    }
    
    public void setParameter(final String s, final Object o) throws DOMException {
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            if (s.equalsIgnoreCase("infoset")) {
                if (booleanValue) {
                    this.features &= 0xFFFFFFFB;
                    this.features &= 0xFFFFFFF7;
                    this.features |= 0x1;
                    this.features |= 0x200;
                    this.features |= 0x2;
                    this.features |= 0x20;
                }
            }
            else if (s.equalsIgnoreCase("xml-declaration")) {
                this.features = (short)(booleanValue ? (this.features | 0x100) : (this.features & 0xFFFFFEFF));
            }
            else if (s.equalsIgnoreCase("namespaces")) {
                this.features = (short)(booleanValue ? (this.features | 0x1) : (this.features & 0xFFFFFFFE));
                this.serializer.fNamespaces = booleanValue;
            }
            else if (s.equalsIgnoreCase("split-cdata-sections")) {
                this.features = (short)(booleanValue ? (this.features | 0x10) : (this.features & 0xFFFFFFEF));
            }
            else if (s.equalsIgnoreCase("discard-default-content")) {
                this.features = (short)(booleanValue ? (this.features | 0x40) : (this.features & 0xFFFFFFBF));
            }
            else if (s.equalsIgnoreCase("well-formed")) {
                this.features = (short)(booleanValue ? (this.features | 0x2) : (this.features & 0xFFFFFFFD));
            }
            else if (s.equalsIgnoreCase("entities")) {
                this.features = (short)(booleanValue ? (this.features | 0x4) : (this.features & 0xFFFFFFFB));
            }
            else if (s.equalsIgnoreCase("cdata-sections")) {
                this.features = (short)(booleanValue ? (this.features | 0x8) : (this.features & 0xFFFFFFF7));
            }
            else if (s.equalsIgnoreCase("comments")) {
                this.features = (short)(booleanValue ? (this.features | 0x20) : (this.features & 0xFFFFFFDF));
            }
            else if (s.equalsIgnoreCase("format-pretty-print")) {
                this.features = (short)(booleanValue ? (this.features | 0x800) : (this.features & 0xFFFFF7FF));
            }
            else if (s.equalsIgnoreCase("canonical-form") || s.equalsIgnoreCase("validate-if-schema") || s.equalsIgnoreCase("validate") || s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("datatype-normalization") || s.equalsIgnoreCase("normalize-characters")) {
                if (booleanValue) {
                    throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
                }
            }
            else if (s.equalsIgnoreCase("namespace-declarations")) {
                this.features = (short)(booleanValue ? (this.features | 0x200) : (this.features & 0xFFFFFDFF));
                this.serializer.fNamespacePrefixes = booleanValue;
            }
            else {
                if (!s.equalsIgnoreCase("element-content-whitespace") && !s.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                    throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { s }));
                }
                if (!booleanValue) {
                    throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
                }
            }
        }
        else if (s.equalsIgnoreCase("error-handler")) {
            if (o != null && !(o instanceof DOMErrorHandler)) {
                throw new DOMException((short)17, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { s }));
            }
            this.fErrorHandler = (DOMErrorHandler)o;
        }
        else {
            if (s.equalsIgnoreCase("resource-resolver") || s.equalsIgnoreCase("schema-location") || (s.equalsIgnoreCase("schema-type") && o != null)) {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
            }
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { s }));
        }
    }
    
    public boolean canSetParameter(final String s, final Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Boolean) {
            final boolean booleanValue = (boolean)o;
            if (s.equalsIgnoreCase("namespaces") || s.equalsIgnoreCase("split-cdata-sections") || s.equalsIgnoreCase("discard-default-content") || s.equalsIgnoreCase("xml-declaration") || s.equalsIgnoreCase("well-formed") || s.equalsIgnoreCase("infoset") || s.equalsIgnoreCase("entities") || s.equalsIgnoreCase("cdata-sections") || s.equalsIgnoreCase("comments") || s.equalsIgnoreCase("format-pretty-print") || s.equalsIgnoreCase("namespace-declarations")) {
                return true;
            }
            if (s.equalsIgnoreCase("canonical-form") || s.equalsIgnoreCase("validate-if-schema") || s.equalsIgnoreCase("validate") || s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("datatype-normalization") || s.equalsIgnoreCase("normalize-characters")) {
                return !booleanValue;
            }
            if (s.equalsIgnoreCase("element-content-whitespace") || s.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return booleanValue;
            }
        }
        else if ((s.equalsIgnoreCase("error-handler") && o == null) || o instanceof DOMErrorHandler) {
            return true;
        }
        return false;
    }
    
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final ArrayList list = new ArrayList();
            list.add("namespaces");
            list.add("split-cdata-sections");
            list.add("discard-default-content");
            list.add("xml-declaration");
            list.add("canonical-form");
            list.add("validate-if-schema");
            list.add("validate");
            list.add("check-character-normalization");
            list.add("datatype-normalization");
            list.add("format-pretty-print");
            list.add("normalize-characters");
            list.add("well-formed");
            list.add("infoset");
            list.add("namespace-declarations");
            list.add("element-content-whitespace");
            list.add("entities");
            list.add("cdata-sections");
            list.add("comments");
            list.add("ignore-unknown-character-denormalizations");
            list.add("error-handler");
            this.fRecognizedParameters = new DOMStringListImpl(list);
        }
        return this.fRecognizedParameters;
    }
    
    public Object getParameter(final String s) throws DOMException {
        if (s.equalsIgnoreCase("comments")) {
            return ((this.features & 0x20) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("namespaces")) {
            return ((this.features & 0x1) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("xml-declaration")) {
            return ((this.features & 0x100) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("cdata-sections")) {
            return ((this.features & 0x8) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("entities")) {
            return ((this.features & 0x4) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("split-cdata-sections")) {
            return ((this.features & 0x10) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("well-formed")) {
            return ((this.features & 0x2) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("namespace-declarations")) {
            return ((this.features & 0x200) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("element-content-whitespace") || s.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("discard-default-content")) {
            return ((this.features & 0x40) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("format-pretty-print")) {
            return ((this.features & 0x800) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("infoset")) {
            if ((this.features & 0x4) == 0x0 && (this.features & 0x8) == 0x0 && (this.features & 0x1) != 0x0 && (this.features & 0x200) != 0x0 && (this.features & 0x2) != 0x0 && (this.features & 0x20) != 0x0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        else {
            if (s.equalsIgnoreCase("normalize-characters") || s.equalsIgnoreCase("canonical-form") || s.equalsIgnoreCase("validate-if-schema") || s.equalsIgnoreCase("check-character-normalization") || s.equalsIgnoreCase("validate") || s.equalsIgnoreCase("validate-if-schema") || s.equalsIgnoreCase("datatype-normalization")) {
                return Boolean.FALSE;
            }
            if (s.equalsIgnoreCase("error-handler")) {
                return this.fErrorHandler;
            }
            if (s.equalsIgnoreCase("resource-resolver") || s.equalsIgnoreCase("schema-location") || s.equalsIgnoreCase("schema-type")) {
                throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { s }));
            }
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { s }));
        }
    }
    
    public String writeToString(final Node node) throws DOMException, LSException {
        final String getXmlVersion = this._getXmlVersion(node);
        XMLSerializer xmlSerializer;
        if (getXmlVersion != null && getXmlVersion.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            xmlSerializer = this.xml11Serializer;
        }
        else {
            xmlSerializer = this.serializer;
        }
        final StringWriter outputCharStream = new StringWriter();
        try {
            this.prepareForSerialization(xmlSerializer, node);
            xmlSerializer._format.setEncoding("UTF-16");
            xmlSerializer.setOutputCharStream(outputCharStream);
            if (node.getNodeType() == 9) {
                xmlSerializer.serialize((Document)node);
            }
            else if (node.getNodeType() == 11) {
                xmlSerializer.serialize((DocumentFragment)node);
            }
            else {
                if (node.getNodeType() != 1) {
                    final String formatMessage = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unable-to-serialize-node", null);
                    if (xmlSerializer.fDOMErrorHandler != null) {
                        final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                        domErrorImpl.fType = "unable-to-serialize-node";
                        domErrorImpl.fMessage = formatMessage;
                        domErrorImpl.fSeverity = 3;
                        xmlSerializer.fDOMErrorHandler.handleError(domErrorImpl);
                    }
                    throw new LSException((short)82, formatMessage);
                }
                xmlSerializer.serialize((Element)node);
            }
        }
        catch (final LSException ex) {
            throw ex;
        }
        catch (final RuntimeException ex2) {
            if (ex2 == DOMNormalizer.abort) {
                return null;
            }
            throw (LSException)DOMUtil.createLSException((short)82, ex2).fillInStackTrace();
        }
        catch (final IOException ex3) {
            throw new DOMException((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "STRING_TOO_LONG", new Object[] { ex3.getMessage() }));
        }
        finally {
            xmlSerializer.clearDocumentState();
        }
        return outputCharStream.toString();
    }
    
    public void setNewLine(final String lineSeparator) {
        this.serializer._format.setLineSeparator(lineSeparator);
    }
    
    public String getNewLine() {
        return this.serializer._format.getLineSeparator();
    }
    
    public LSSerializerFilter getFilter() {
        return this.serializer.fDOMFilter;
    }
    
    public void setFilter(final LSSerializerFilter fdomFilter) {
        this.serializer.fDOMFilter = fdomFilter;
    }
    
    private void initSerializer(final XMLSerializer xmlSerializer) {
        xmlSerializer.fNSBinder = new NamespaceSupport();
        xmlSerializer.fLocalNSBinder = new NamespaceSupport();
        xmlSerializer.fSymbolTable = new SymbolTable();
    }
    
    private void copySettings(final XMLSerializer xmlSerializer, final XMLSerializer xmlSerializer2) {
        xmlSerializer2.fDOMErrorHandler = this.fErrorHandler;
        xmlSerializer2._format.setEncoding(xmlSerializer._format.getEncoding());
        xmlSerializer2._format.setLineSeparator(xmlSerializer._format.getLineSeparator());
        xmlSerializer2.fDOMFilter = xmlSerializer.fDOMFilter;
    }
    
    public boolean write(final Node node, final LSOutput lsOutput) throws LSException {
        if (node == null) {
            return false;
        }
        final String getXmlVersion = this._getXmlVersion(node);
        XMLSerializer xmlSerializer;
        if (getXmlVersion != null && getXmlVersion.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            xmlSerializer = this.xml11Serializer;
        }
        else {
            xmlSerializer = this.serializer;
        }
        Label_0124: {
            String encoding;
            if ((encoding = lsOutput.getEncoding()) != null) {
                break Label_0124;
            }
            encoding = this._getInputEncoding(node);
            if (encoding != null) {
                break Label_0124;
            }
            encoding = this._getXmlEncoding(node);
            if (encoding != null) {
                break Label_0124;
            }
            encoding = "UTF-8";
            try {
                this.prepareForSerialization(xmlSerializer, node);
                xmlSerializer._format.setEncoding(encoding);
                final OutputStream byteStream = lsOutput.getByteStream();
                final Writer characterStream = lsOutput.getCharacterStream();
                final String systemId = lsOutput.getSystemId();
                if (characterStream == null) {
                    if (byteStream == null) {
                        if (systemId == null) {
                            final String formatMessage = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "no-output-specified", null);
                            if (xmlSerializer.fDOMErrorHandler != null) {
                                final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                                domErrorImpl.fType = "no-output-specified";
                                domErrorImpl.fMessage = formatMessage;
                                domErrorImpl.fSeverity = 3;
                                xmlSerializer.fDOMErrorHandler.handleError(domErrorImpl);
                            }
                            throw new LSException((short)82, formatMessage);
                        }
                        xmlSerializer.setOutputByteStream(XMLEntityManager.createOutputStream(systemId));
                    }
                    else {
                        xmlSerializer.setOutputByteStream(byteStream);
                    }
                }
                else {
                    xmlSerializer.setOutputCharStream(characterStream);
                }
                if (node.getNodeType() == 9) {
                    xmlSerializer.serialize((Document)node);
                }
                else if (node.getNodeType() == 11) {
                    xmlSerializer.serialize((DocumentFragment)node);
                }
                else {
                    if (node.getNodeType() != 1) {
                        return false;
                    }
                    xmlSerializer.serialize((Element)node);
                }
            }
            catch (final UnsupportedEncodingException fException) {
                if (xmlSerializer.fDOMErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl2 = new DOMErrorImpl();
                    domErrorImpl2.fException = fException;
                    domErrorImpl2.fType = "unsupported-encoding";
                    domErrorImpl2.fMessage = fException.getMessage();
                    domErrorImpl2.fSeverity = 3;
                    xmlSerializer.fDOMErrorHandler.handleError(domErrorImpl2);
                }
                throw new LSException((short)82, DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unsupported-encoding", null));
            }
            catch (final LSException ex) {
                throw ex;
            }
            catch (final RuntimeException ex2) {
                if (ex2 == DOMNormalizer.abort) {
                    return false;
                }
                throw (LSException)DOMUtil.createLSException((short)82, ex2).fillInStackTrace();
            }
            catch (final Exception fException2) {
                if (xmlSerializer.fDOMErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl3 = new DOMErrorImpl();
                    domErrorImpl3.fException = fException2;
                    domErrorImpl3.fMessage = fException2.getMessage();
                    domErrorImpl3.fSeverity = 2;
                    xmlSerializer.fDOMErrorHandler.handleError(domErrorImpl3);
                }
                throw (LSException)DOMUtil.createLSException((short)82, fException2).fillInStackTrace();
            }
            finally {
                xmlSerializer.clearDocumentState();
            }
        }
        return true;
    }
    
    public boolean writeToURI(final Node node, final String s) throws LSException {
        if (node == null) {
            return false;
        }
        final String getXmlVersion = this._getXmlVersion(node);
        XMLSerializer xmlSerializer;
        if (getXmlVersion != null && getXmlVersion.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            xmlSerializer = this.xml11Serializer;
        }
        else {
            xmlSerializer = this.serializer;
        }
        String encoding = this._getInputEncoding(node);
        Label_0109: {
            if (encoding != null) {
                break Label_0109;
            }
            encoding = this._getXmlEncoding(node);
            if (encoding != null) {
                break Label_0109;
            }
            encoding = "UTF-8";
            try {
                this.prepareForSerialization(xmlSerializer, node);
                xmlSerializer._format.setEncoding(encoding);
                xmlSerializer.setOutputByteStream(XMLEntityManager.createOutputStream(s));
                if (node.getNodeType() == 9) {
                    xmlSerializer.serialize((Document)node);
                }
                else if (node.getNodeType() == 11) {
                    xmlSerializer.serialize((DocumentFragment)node);
                }
                else {
                    if (node.getNodeType() != 1) {
                        return false;
                    }
                    xmlSerializer.serialize((Element)node);
                }
            }
            catch (final LSException ex) {
                throw ex;
            }
            catch (final RuntimeException ex2) {
                if (ex2 == DOMNormalizer.abort) {
                    return false;
                }
                throw (LSException)DOMUtil.createLSException((short)82, ex2).fillInStackTrace();
            }
            catch (final Exception fException) {
                if (xmlSerializer.fDOMErrorHandler != null) {
                    final DOMErrorImpl domErrorImpl = new DOMErrorImpl();
                    domErrorImpl.fException = fException;
                    domErrorImpl.fMessage = fException.getMessage();
                    domErrorImpl.fSeverity = 2;
                    xmlSerializer.fDOMErrorHandler.handleError(domErrorImpl);
                }
                throw (LSException)DOMUtil.createLSException((short)82, fException).fillInStackTrace();
            }
            finally {
                xmlSerializer.clearDocumentState();
            }
        }
        return true;
    }
    
    private void prepareForSerialization(final XMLSerializer xmlSerializer, Node parentNode) {
        xmlSerializer.reset();
        xmlSerializer.features = this.features;
        xmlSerializer.fDOMErrorHandler = this.fErrorHandler;
        xmlSerializer.fNamespaces = ((this.features & 0x1) != 0x0);
        xmlSerializer.fNamespacePrefixes = ((this.features & 0x200) != 0x0);
        xmlSerializer._format.setIndenting((this.features & 0x800) != 0x0);
        xmlSerializer._format.setOmitComments((this.features & 0x20) == 0x0);
        xmlSerializer._format.setOmitXMLDeclaration((this.features & 0x100) == 0x0);
        if ((this.features & 0x2) != 0x0) {
            final Node node = parentNode;
            boolean booleanValue = true;
            final Document document = (Document)((parentNode.getNodeType() == 9) ? parentNode : parentNode.getOwnerDocument());
            try {
                final Method method = document.getClass().getMethod("isXMLVersionChanged()", (Class<?>[])new Class[0]);
                if (method != null) {
                    booleanValue = (boolean)method.invoke(document, (Object[])null);
                }
            }
            catch (final Exception ex) {}
            if (parentNode.getFirstChild() != null) {
                while (parentNode != null) {
                    this.verify(parentNode, booleanValue, false);
                    Node node2;
                    for (node2 = parentNode.getFirstChild(); node2 == null; node2 = parentNode.getNextSibling()) {
                        node2 = parentNode.getNextSibling();
                        if (node2 == null) {
                            parentNode = parentNode.getParentNode();
                            if (node == parentNode) {
                                node2 = null;
                                break;
                            }
                        }
                    }
                    parentNode = node2;
                }
            }
            else {
                this.verify(parentNode, booleanValue, false);
            }
        }
    }
    
    private void verify(final Node fRelatedNode, final boolean b, final boolean b2) {
        final short nodeType = fRelatedNode.getNodeType();
        this.fLocator.fRelatedNode = fRelatedNode;
        switch (nodeType) {
            case 9: {}
            case 1: {
                if (b) {
                    boolean b3;
                    if ((this.features & 0x1) != 0x0) {
                        b3 = CoreDocumentImpl.isValidQName(fRelatedNode.getPrefix(), fRelatedNode.getLocalName(), b2);
                    }
                    else {
                        b3 = CoreDocumentImpl.isXMLName(fRelatedNode.getNodeName(), b2);
                    }
                    if (!b3 && this.fErrorHandler != null) {
                        DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", fRelatedNode.getNodeName() }), (short)3, "wf-invalid-character-in-node-name");
                    }
                }
                final NamedNodeMap namedNodeMap = fRelatedNode.hasAttributes() ? fRelatedNode.getAttributes() : null;
                if (namedNodeMap != null) {
                    for (int i = 0; i < namedNodeMap.getLength(); ++i) {
                        final Attr fRelatedNode2 = (Attr)namedNodeMap.item(i);
                        this.fLocator.fRelatedNode = fRelatedNode2;
                        DOMNormalizer.isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, namedNodeMap, fRelatedNode2, fRelatedNode2.getValue(), b2);
                        if (b && !CoreDocumentImpl.isXMLName(fRelatedNode2.getNodeName(), b2)) {
                            DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", fRelatedNode.getNodeName() }), (short)3, "wf-invalid-character-in-node-name");
                        }
                    }
                    break;
                }
                break;
            }
            case 8: {
                if ((this.features & 0x20) != 0x0) {
                    DOMNormalizer.isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)fRelatedNode).getData(), b2);
                    break;
                }
                break;
            }
            case 5: {
                if (b && (this.features & 0x4) != 0x0) {
                    CoreDocumentImpl.isXMLName(fRelatedNode.getNodeName(), b2);
                    break;
                }
                break;
            }
            case 4: {
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, fRelatedNode.getNodeValue(), b2);
                break;
            }
            case 3: {
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, fRelatedNode.getNodeValue(), b2);
                break;
            }
            case 7: {
                final ProcessingInstruction processingInstruction = (ProcessingInstruction)fRelatedNode;
                final String target = processingInstruction.getTarget();
                if (b) {
                    boolean b4;
                    if (b2) {
                        b4 = XML11Char.isXML11ValidName(target);
                    }
                    else {
                        b4 = XMLChar.isValidName(target);
                    }
                    if (!b4) {
                        DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", fRelatedNode.getNodeName() }), (short)3, "wf-invalid-character-in-node-name");
                    }
                }
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, processingInstruction.getData(), b2);
                break;
            }
        }
        this.fLocator.fRelatedNode = null;
    }
    
    private String _getXmlVersion(final Node node) {
        final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        if (document != null && DocumentMethods.fgDocumentMethodsAvailable) {
            try {
                return (String)DocumentMethods.fgDocumentGetXmlVersionMethod.invoke(document, (Object[])null);
            }
            catch (final VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (final ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (final Throwable t) {}
        }
        return null;
    }
    
    private String _getInputEncoding(final Node node) {
        final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        if (document != null && DocumentMethods.fgDocumentMethodsAvailable) {
            try {
                return (String)DocumentMethods.fgDocumentGetInputEncodingMethod.invoke(document, (Object[])null);
            }
            catch (final VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (final ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (final Throwable t) {}
        }
        return null;
    }
    
    private String _getXmlEncoding(final Node node) {
        final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        if (document != null && DocumentMethods.fgDocumentMethodsAvailable) {
            try {
                return (String)DocumentMethods.fgDocumentGetXmlEncodingMethod.invoke(document, (Object[])null);
            }
            catch (final VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (final ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (final Throwable t) {}
        }
        return null;
    }
    
    static class DocumentMethods
    {
        private static Method fgDocumentGetXmlVersionMethod;
        private static Method fgDocumentGetInputEncodingMethod;
        private static Method fgDocumentGetXmlEncodingMethod;
        private static boolean fgDocumentMethodsAvailable;
        
        private DocumentMethods() {
        }
        
        static {
            DocumentMethods.fgDocumentGetXmlVersionMethod = null;
            DocumentMethods.fgDocumentGetInputEncodingMethod = null;
            DocumentMethods.fgDocumentGetXmlEncodingMethod = null;
            DocumentMethods.fgDocumentMethodsAvailable = false;
            try {
                DocumentMethods.fgDocumentGetXmlVersionMethod = Document.class.getMethod("getXmlVersion", (Class[])new Class[0]);
                DocumentMethods.fgDocumentGetInputEncodingMethod = Document.class.getMethod("getInputEncoding", (Class[])new Class[0]);
                DocumentMethods.fgDocumentGetXmlEncodingMethod = Document.class.getMethod("getXmlEncoding", (Class[])new Class[0]);
                DocumentMethods.fgDocumentMethodsAvailable = true;
            }
            catch (final Exception ex) {
                DocumentMethods.fgDocumentGetXmlVersionMethod = null;
                DocumentMethods.fgDocumentGetInputEncodingMethod = null;
                DocumentMethods.fgDocumentGetXmlEncodingMethod = null;
                DocumentMethods.fgDocumentMethodsAvailable = false;
            }
        }
    }
}
