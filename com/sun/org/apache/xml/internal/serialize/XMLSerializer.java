package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMError;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.AttributeList;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import org.xml.sax.Attributes;
import java.io.OutputStream;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class XMLSerializer extends BaseMarkupSerializer
{
    protected static final boolean DEBUG = false;
    protected NamespaceSupport fNSBinder;
    protected NamespaceSupport fLocalNSBinder;
    protected SymbolTable fSymbolTable;
    protected static final String PREFIX = "NS";
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes;
    private boolean fPreserveSpace;
    
    public XMLSerializer() {
        super(new OutputFormat("xml", null, false));
        this.fNamespaces = false;
        this.fNamespacePrefixes = true;
    }
    
    public XMLSerializer(final OutputFormat format) {
        super((format != null) ? format : new OutputFormat("xml", null, false));
        this.fNamespaces = false;
        this.fNamespacePrefixes = true;
        this._format.setMethod("xml");
    }
    
    public XMLSerializer(final Writer writer, final OutputFormat format) {
        super((format != null) ? format : new OutputFormat("xml", null, false));
        this.fNamespaces = false;
        this.fNamespacePrefixes = true;
        this._format.setMethod("xml");
        this.setOutputCharStream(writer);
    }
    
    public XMLSerializer(final OutputStream output, final OutputFormat format) {
        super((format != null) ? format : new OutputFormat("xml", null, false));
        this.fNamespaces = false;
        this.fNamespacePrefixes = true;
        this._format.setMethod("xml");
        this.setOutputByteStream(output);
    }
    
    @Override
    public void setOutputFormat(final OutputFormat format) {
        super.setOutputFormat((format != null) ? format : new OutputFormat("xml", null, false));
    }
    
    public void setNamespaces(final boolean namespaces) {
        this.fNamespaces = namespaces;
        if (this.fNSBinder == null) {
            this.fNSBinder = new NamespaceSupport();
            this.fLocalNSBinder = new NamespaceSupport();
            this.fSymbolTable = new SymbolTable();
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, String rawName, Attributes attrs) throws SAXException {
        boolean addNSAttr = false;
        try {
            if (this._printer == null) {
                final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
                throw new IllegalStateException(msg);
            }
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument((localName == null || localName.length() == 0) ? rawName : localName);
                }
            }
            else {
                if (state.empty) {
                    this._printer.printText('>');
                }
                if (state.inCData) {
                    this._printer.printText("]]>");
                    state.inCData = false;
                }
                if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
                    this._printer.breakLine();
                }
            }
            boolean preserveSpace = state.preserveSpace;
            attrs = this.extractNamespaces(attrs);
            if (rawName == null || rawName.length() == 0) {
                if (localName == null) {
                    final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoName", null);
                    throw new SAXException(msg);
                }
                if (namespaceURI != null && !namespaceURI.equals("")) {
                    final String prefix = this.getPrefix(namespaceURI);
                    if (prefix != null && prefix.length() > 0) {
                        rawName = prefix + ":" + localName;
                    }
                    else {
                        rawName = localName;
                    }
                }
                else {
                    rawName = localName;
                }
                addNSAttr = true;
            }
            this._printer.printText('<');
            this._printer.printText(rawName);
            this._printer.indent();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this._printer.printSpace();
                    String name = attrs.getQName(i);
                    if (name != null && name.length() == 0) {
                        name = attrs.getLocalName(i);
                        final String attrURI = attrs.getURI(i);
                        if (attrURI != null && attrURI.length() != 0 && (namespaceURI == null || namespaceURI.length() == 0 || !attrURI.equals(namespaceURI))) {
                            final String prefix = this.getPrefix(attrURI);
                            if (prefix != null && prefix.length() > 0) {
                                name = prefix + ":" + name;
                            }
                        }
                    }
                    String value = attrs.getValue(i);
                    if (value == null) {
                        value = "";
                    }
                    this._printer.printText(name);
                    this._printer.printText("=\"");
                    this.printEscaped(value);
                    this._printer.printText('\"');
                    if (name.equals("xml:space")) {
                        preserveSpace = (value.equals("preserve") || this._format.getPreserveSpace());
                    }
                }
            }
            if (this._prefixes != null) {
                for (final Map.Entry<String, String> entry : this._prefixes.entrySet()) {
                    this._printer.printSpace();
                    final String value = entry.getKey();
                    final String name = entry.getValue();
                    if (name.length() == 0) {
                        this._printer.printText("xmlns=\"");
                        this.printEscaped(value);
                        this._printer.printText('\"');
                    }
                    else {
                        this._printer.printText("xmlns:");
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('\"');
                    }
                }
            }
            state = this.enterElementState(namespaceURI, localName, rawName, preserveSpace);
            String name = (localName == null || localName.length() == 0) ? rawName : (namespaceURI + "^" + localName);
            state.doCData = this._format.isCDataElement(name);
            state.unescaped = this._format.isNonEscapingElement(name);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String rawName) throws SAXException {
        try {
            this.endElementIO(namespaceURI, localName, rawName);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    public void endElementIO(final String namespaceURI, final String localName, final String rawName) throws IOException {
        this._printer.unindent();
        ElementState state = this.getElementState();
        if (state.empty) {
            this._printer.printText("/>");
        }
        else {
            if (state.inCData) {
                this._printer.printText("]]>");
            }
            if (this._indenting && !state.preserveSpace && (state.afterElement || state.afterComment)) {
                this._printer.breakLine();
            }
            this._printer.printText("</");
            this._printer.printText(state.rawName);
            this._printer.printText('>');
        }
        state = this.leaveElementState();
        state.afterElement = true;
        state.afterComment = false;
        state.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }
    
    @Override
    public void startElement(final String tagName, final AttributeList attrs) throws SAXException {
        try {
            if (this._printer == null) {
                final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
                throw new IllegalStateException(msg);
            }
            ElementState state = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument(tagName);
                }
            }
            else {
                if (state.empty) {
                    this._printer.printText('>');
                }
                if (state.inCData) {
                    this._printer.printText("]]>");
                    state.inCData = false;
                }
                if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
                    this._printer.breakLine();
                }
            }
            boolean preserveSpace = state.preserveSpace;
            this._printer.printText('<');
            this._printer.printText(tagName);
            this._printer.indent();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    this._printer.printSpace();
                    final String name = attrs.getName(i);
                    final String value = attrs.getValue(i);
                    if (value != null) {
                        this._printer.printText(name);
                        this._printer.printText("=\"");
                        this.printEscaped(value);
                        this._printer.printText('\"');
                    }
                    if (name.equals("xml:space")) {
                        preserveSpace = (value.equals("preserve") || this._format.getPreserveSpace());
                    }
                }
            }
            state = this.enterElementState(null, null, tagName, preserveSpace);
            state.doCData = this._format.isCDataElement(tagName);
            state.unescaped = this._format.isNonEscapingElement(tagName);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void endElement(final String tagName) throws SAXException {
        this.endElement(null, null, tagName);
    }
    
    protected void startDocument(final String rootTagName) throws IOException {
        final String dtd = this._printer.leaveDTD();
        if (!this._started) {
            if (!this._format.getOmitXMLDeclaration()) {
                final StringBuffer buffer = new StringBuffer("<?xml version=\"");
                if (this._format.getVersion() != null) {
                    buffer.append(this._format.getVersion());
                }
                else {
                    buffer.append("1.0");
                }
                buffer.append('\"');
                final String format_encoding = this._format.getEncoding();
                if (format_encoding != null) {
                    buffer.append(" encoding=\"");
                    buffer.append(format_encoding);
                    buffer.append('\"');
                }
                if (this._format.getStandalone() && this._docTypeSystemId == null && this._docTypePublicId == null) {
                    buffer.append(" standalone=\"yes\"");
                }
                buffer.append("?>");
                this._printer.printText(buffer);
                this._printer.breakLine();
            }
            if (!this._format.getOmitDocumentType()) {
                if (this._docTypeSystemId != null) {
                    this._printer.printText("<!DOCTYPE ");
                    this._printer.printText(rootTagName);
                    if (this._docTypePublicId != null) {
                        this._printer.printText(" PUBLIC ");
                        this.printDoctypeURL(this._docTypePublicId);
                        if (this._indenting) {
                            this._printer.breakLine();
                            for (int i = 0; i < 18 + rootTagName.length(); ++i) {
                                this._printer.printText(" ");
                            }
                        }
                        else {
                            this._printer.printText(" ");
                        }
                        this.printDoctypeURL(this._docTypeSystemId);
                    }
                    else {
                        this._printer.printText(" SYSTEM ");
                        this.printDoctypeURL(this._docTypeSystemId);
                    }
                    if (dtd != null && dtd.length() > 0) {
                        this._printer.printText(" [");
                        this.printText(dtd, true, true);
                        this._printer.printText(']');
                    }
                    this._printer.printText(">");
                    this._printer.breakLine();
                }
                else if (dtd != null && dtd.length() > 0) {
                    this._printer.printText("<!DOCTYPE ");
                    this._printer.printText(rootTagName);
                    this._printer.printText(" [");
                    this.printText(dtd, true, true);
                    this._printer.printText("]>");
                    this._printer.breakLine();
                }
            }
        }
        this._started = true;
        this.serializePreRoot();
    }
    
    @Override
    protected void serializeElement(final Element elem) throws IOException {
        if (this.fNamespaces) {
            this.fLocalNSBinder.reset();
            this.fNSBinder.pushContext();
        }
        final String tagName = elem.getTagName();
        ElementState state = this.getElementState();
        if (this.isDocumentState()) {
            if (!this._started) {
                this.startDocument(tagName);
            }
        }
        else {
            if (state.empty) {
                this._printer.printText('>');
            }
            if (state.inCData) {
                this._printer.printText("]]>");
                state.inCData = false;
            }
            if (this._indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment)) {
                this._printer.breakLine();
            }
        }
        this.fPreserveSpace = state.preserveSpace;
        int length = 0;
        NamedNodeMap attrMap = null;
        if (elem.hasAttributes()) {
            attrMap = elem.getAttributes();
            length = attrMap.getLength();
        }
        if (!this.fNamespaces) {
            this._printer.printText('<');
            this._printer.printText(tagName);
            this._printer.indent();
            for (int i = 0; i < length; ++i) {
                final Attr attr = (Attr)attrMap.item(i);
                final String name = attr.getName();
                String value = attr.getValue();
                if (value == null) {
                    value = "";
                }
                this.printAttribute(name, value, attr.getSpecified(), attr);
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                final Attr attr = (Attr)attrMap.item(i);
                final String uri = attr.getNamespaceURI();
                if (uri != null && uri.equals(NamespaceContext.XMLNS_URI)) {
                    String value = attr.getNodeValue();
                    if (value == null) {
                        value = XMLSymbols.EMPTY_STRING;
                    }
                    if (value.equals(NamespaceContext.XMLNS_URI)) {
                        if (this.fDOMErrorHandler != null) {
                            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null);
                            this.modifyDOMError(msg, (short)2, null, attr);
                            final boolean continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                            if (!continueProcess) {
                                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                            }
                        }
                    }
                    else {
                        String prefix = attr.getPrefix();
                        prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                        final String localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                        if (prefix == XMLSymbols.PREFIX_XMLNS) {
                            value = this.fSymbolTable.addSymbol(value);
                            if (value.length() != 0) {
                                this.fNSBinder.declarePrefix(localpart, value);
                            }
                        }
                        else {
                            value = this.fSymbolTable.addSymbol(value);
                            this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, value);
                        }
                    }
                }
            }
            String uri = elem.getNamespaceURI();
            String prefix = elem.getPrefix();
            if (uri != null && prefix != null && uri.length() == 0 && prefix.length() != 0) {
                prefix = null;
                this._printer.printText('<');
                this._printer.printText(elem.getLocalName());
                this._printer.indent();
            }
            else {
                this._printer.printText('<');
                this._printer.printText(tagName);
                this._printer.indent();
            }
            if (uri != null) {
                uri = this.fSymbolTable.addSymbol(uri);
                prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                if (this.fNSBinder.getURI(prefix) != uri) {
                    if (this.fNamespacePrefixes) {
                        this.printNamespaceAttr(prefix, uri);
                    }
                    this.fLocalNSBinder.declarePrefix(prefix, uri);
                    this.fNSBinder.declarePrefix(prefix, uri);
                }
            }
            else if (elem.getLocalName() == null) {
                if (this.fDOMErrorHandler != null) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[] { elem.getNodeName() });
                    this.modifyDOMError(msg, (short)2, null, elem);
                    final boolean continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                    if (!continueProcess) {
                        throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                    }
                }
            }
            else {
                uri = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                if (uri != null && uri.length() > 0) {
                    if (this.fNamespacePrefixes) {
                        this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                    }
                    this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                    this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                }
            }
            for (int i = 0; i < length; ++i) {
                final Attr attr = (Attr)attrMap.item(i);
                String value = attr.getValue();
                String name = attr.getNodeName();
                uri = attr.getNamespaceURI();
                if (uri != null && uri.length() == 0) {
                    uri = null;
                    name = attr.getLocalName();
                }
                if (value == null) {
                    value = XMLSymbols.EMPTY_STRING;
                }
                if (uri != null) {
                    prefix = attr.getPrefix();
                    prefix = ((prefix == null) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                    String localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                    if (uri != null && uri.equals(NamespaceContext.XMLNS_URI)) {
                        prefix = attr.getPrefix();
                        prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                        localpart = this.fSymbolTable.addSymbol(attr.getLocalName());
                        if (prefix == XMLSymbols.PREFIX_XMLNS) {
                            final String localUri = this.fLocalNSBinder.getURI(localpart);
                            value = this.fSymbolTable.addSymbol(value);
                            if (value.length() != 0 && localUri == null) {
                                if (this.fNamespacePrefixes) {
                                    this.printNamespaceAttr(localpart, value);
                                }
                                this.fLocalNSBinder.declarePrefix(localpart, value);
                            }
                        }
                        else {
                            uri = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                            final String localUri = this.fLocalNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                            value = this.fSymbolTable.addSymbol(value);
                            if (localUri == null && this.fNamespacePrefixes) {
                                this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, value);
                            }
                        }
                    }
                    else {
                        uri = this.fSymbolTable.addSymbol(uri);
                        final String declaredURI = this.fNSBinder.getURI(prefix);
                        if (prefix == XMLSymbols.EMPTY_STRING || declaredURI != uri) {
                            name = attr.getNodeName();
                            final String declaredPrefix = this.fNSBinder.getPrefix(uri);
                            if (declaredPrefix != null && declaredPrefix != XMLSymbols.EMPTY_STRING) {
                                prefix = declaredPrefix;
                                name = prefix + ":" + localpart;
                            }
                            else {
                                if (prefix == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(prefix) != null) {
                                    int counter;
                                    for (counter = 1, prefix = this.fSymbolTable.addSymbol("NS" + counter++); this.fLocalNSBinder.getURI(prefix) != null; prefix = this.fSymbolTable.addSymbol("NS" + counter++)) {}
                                    name = prefix + ":" + localpart;
                                }
                                if (this.fNamespacePrefixes) {
                                    this.printNamespaceAttr(prefix, uri);
                                }
                                value = this.fSymbolTable.addSymbol(value);
                                this.fLocalNSBinder.declarePrefix(prefix, value);
                                this.fNSBinder.declarePrefix(prefix, uri);
                            }
                        }
                        this.printAttribute(name, (value == null) ? XMLSymbols.EMPTY_STRING : value, attr.getSpecified(), attr);
                    }
                }
                else if (attr.getLocalName() == null) {
                    if (this.fDOMErrorHandler != null) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[] { attr.getNodeName() });
                        this.modifyDOMError(msg, (short)2, null, attr);
                        final boolean continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                        if (!continueProcess) {
                            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                        }
                    }
                    this.printAttribute(name, value, attr.getSpecified(), attr);
                }
                else {
                    this.printAttribute(name, value, attr.getSpecified(), attr);
                }
            }
        }
        if (elem.hasChildNodes()) {
            state = this.enterElementState(null, null, tagName, this.fPreserveSpace);
            state.doCData = this._format.isCDataElement(tagName);
            state.unescaped = this._format.isNonEscapingElement(tagName);
            for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                this.serializeNode(child);
            }
            if (this.fNamespaces) {
                this.fNSBinder.popContext();
            }
            this.endElementIO(null, null, tagName);
        }
        else {
            if (this.fNamespaces) {
                this.fNSBinder.popContext();
            }
            this._printer.unindent();
            this._printer.printText("/>");
            state.afterElement = true;
            state.afterComment = false;
            state.empty = false;
            if (this.isDocumentState()) {
                this._printer.flush();
            }
        }
    }
    
    private void printNamespaceAttr(final String prefix, final String uri) throws IOException {
        this._printer.printSpace();
        if (prefix == XMLSymbols.EMPTY_STRING) {
            this._printer.printText(XMLSymbols.PREFIX_XMLNS);
        }
        else {
            this._printer.printText("xmlns:" + prefix);
        }
        this._printer.printText("=\"");
        this.printEscaped(uri);
        this._printer.printText('\"');
    }
    
    private void printAttribute(final String name, final String value, final boolean isSpecified, final Attr attr) throws IOException {
        if (isSpecified || (this.features & 0x40) == 0x0) {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x2) != 0x0) {
                final short code = this.fDOMFilter.acceptNode(attr);
                switch (code) {
                    case 2:
                    case 3: {
                        return;
                    }
                }
            }
            this._printer.printSpace();
            this._printer.printText(name);
            this._printer.printText("=\"");
            this.printEscaped(value);
            this._printer.printText('\"');
        }
        if (name.equals("xml:space")) {
            if (value.equals("preserve")) {
                this.fPreserveSpace = true;
            }
            else {
                this.fPreserveSpace = this._format.getPreserveSpace();
            }
        }
    }
    
    @Override
    protected String getEntityRef(final int ch) {
        switch (ch) {
            case 60: {
                return "lt";
            }
            case 62: {
                return "gt";
            }
            case 34: {
                return "quot";
            }
            case 39: {
                return "apos";
            }
            case 38: {
                return "amp";
            }
            default: {
                return null;
            }
        }
    }
    
    private Attributes extractNamespaces(final Attributes attrs) throws SAXException {
        if (attrs == null) {
            return null;
        }
        final int length = attrs.getLength();
        final AttributesImpl attrsOnly = new AttributesImpl(attrs);
        for (int i = length - 1; i >= 0; --i) {
            final String rawName = attrsOnly.getQName(i);
            if (rawName.startsWith("xmlns")) {
                if (rawName.length() == 5) {
                    this.startPrefixMapping("", attrs.getValue(i));
                    attrsOnly.removeAttribute(i);
                }
                else if (rawName.charAt(5) == ':') {
                    this.startPrefixMapping(rawName.substring(6), attrs.getValue(i));
                    attrsOnly.removeAttribute(i);
                }
            }
        }
        return attrsOnly;
    }
    
    @Override
    protected void printEscaped(final String source) throws IOException {
        for (int length = source.length(), i = 0; i < length; ++i) {
            final int ch = source.charAt(i);
            if (!XMLChar.isValid(ch)) {
                if (++i < length) {
                    this.surrogates(ch, source.charAt(i));
                }
                else {
                    this.fatalError("The character '" + (char)ch + "' is an invalid XML character");
                }
            }
            else if (ch == 10 || ch == 13 || ch == 9) {
                this.printHex(ch);
            }
            else if (ch == 60) {
                this._printer.printText("&lt;");
            }
            else if (ch == 38) {
                this._printer.printText("&amp;");
            }
            else if (ch == 34) {
                this._printer.printText("&quot;");
            }
            else if (ch >= 32 && this._encodingInfo.isPrintable((char)ch)) {
                this._printer.printText((char)ch);
            }
            else {
                this.printHex(ch);
            }
        }
    }
    
    protected void printXMLChar(final int ch) throws IOException {
        if (ch == 13) {
            this.printHex(ch);
        }
        else if (ch == 60) {
            this._printer.printText("&lt;");
        }
        else if (ch == 38) {
            this._printer.printText("&amp;");
        }
        else if (ch == 62) {
            this._printer.printText("&gt;");
        }
        else if (ch == 10 || ch == 9 || (ch >= 32 && this._encodingInfo.isPrintable((char)ch))) {
            this._printer.printText((char)ch);
        }
        else {
            this.printHex(ch);
        }
    }
    
    @Override
    protected void printText(final String text, final boolean preserveSpace, final boolean unescaped) throws IOException {
        final int length = text.length();
        if (preserveSpace) {
            for (int index = 0; index < length; ++index) {
                final char ch = text.charAt(index);
                if (!XMLChar.isValid(ch)) {
                    if (++index < length) {
                        this.surrogates(ch, text.charAt(index));
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
        else {
            for (int index = 0; index < length; ++index) {
                final char ch = text.charAt(index);
                if (!XMLChar.isValid(ch)) {
                    if (++index < length) {
                        this.surrogates(ch, text.charAt(index));
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
    }
    
    @Override
    protected void printText(final char[] chars, int start, int length, final boolean preserveSpace, final boolean unescaped) throws IOException {
        if (preserveSpace) {
            while (length-- > 0) {
                final char ch = chars[start++];
                if (!XMLChar.isValid(ch)) {
                    if (length-- > 0) {
                        this.surrogates(ch, chars[start++]);
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
        else {
            while (length-- > 0) {
                final char ch = chars[start++];
                if (!XMLChar.isValid(ch)) {
                    if (length-- > 0) {
                        this.surrogates(ch, chars[start++]);
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
    }
    
    @Override
    protected void checkUnboundNamespacePrefixedNode(final Node node) throws IOException {
        if (this.fNamespaces) {
            Node next;
            for (Node child = node.getFirstChild(); child != null; child = next) {
                next = child.getNextSibling();
                String prefix = child.getPrefix();
                prefix = ((prefix == null || prefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(prefix));
                if (this.fNSBinder.getURI(prefix) == null && prefix != null) {
                    this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + child.getNodeName() + "' with an undeclared prefix '" + prefix + "'.");
                }
                if (child.getNodeType() == 1) {
                    final NamedNodeMap attrs = child.getAttributes();
                    for (int i = 0; i < attrs.getLength(); ++i) {
                        String attrPrefix = attrs.item(i).getPrefix();
                        attrPrefix = ((attrPrefix == null || attrPrefix.length() == 0) ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(attrPrefix));
                        if (this.fNSBinder.getURI(attrPrefix) == null && attrPrefix != null) {
                            this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + child.getNodeName() + "' with an attribute '" + attrs.item(i).getNodeName() + "' an undeclared prefix '" + attrPrefix + "'.");
                        }
                    }
                }
                if (child.hasChildNodes()) {
                    this.checkUnboundNamespacePrefixedNode(child);
                }
            }
        }
    }
    
    @Override
    public boolean reset() {
        super.reset();
        if (this.fNSBinder != null) {
            this.fNSBinder.reset();
            this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
        }
        return true;
    }
}
