package com.sun.xml.internal.stream.writers;

import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import com.sun.org.apache.xerces.internal.xni.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.nio.charset.CharsetEncoder;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import java.util.Random;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamWriter;
import java.util.AbstractMap;

public final class XMLStreamWriterImpl extends AbstractMap implements XMLStreamWriter
{
    public static final String START_COMMENT = "<!--";
    public static final String END_COMMENT = "-->";
    public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
    public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
    public static final String DEFAULT_XML_VERSION = "1.0";
    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';
    public static final String START_CDATA = "<![CDATA[";
    public static final String END_CDATA = "]]>";
    public static final String CLOSE_EMPTY_ELEMENT = "/>";
    public static final String SPACE = " ";
    public static final String UTF_8 = "UTF-8";
    public static final String OUTPUTSTREAM_PROPERTY = "sjsxp-outputstream";
    boolean fEscapeCharacters;
    private boolean fIsRepairingNamespace;
    private Writer fWriter;
    private OutputStream fOutputStream;
    private ArrayList fAttributeCache;
    private ArrayList fNamespaceDecls;
    private NamespaceContextImpl fNamespaceContext;
    private NamespaceSupport fInternalNamespaceContext;
    private Random fPrefixGen;
    private PropertyManager fPropertyManager;
    private boolean fStartTagOpened;
    private boolean fReuse;
    private SymbolTable fSymbolTable;
    private ElementStack fElementStack;
    private final String DEFAULT_PREFIX;
    private final ReadOnlyIterator fReadOnlyIterator;
    private CharsetEncoder fEncoder;
    HashMap fAttrNamespace;
    
    public XMLStreamWriterImpl(final OutputStream outputStream, final PropertyManager props) throws IOException {
        this(new OutputStreamWriter(outputStream), props);
    }
    
    public XMLStreamWriterImpl(final OutputStream outputStream, final String encoding, final PropertyManager props) throws IOException {
        this(new StreamResult(outputStream), encoding, props);
    }
    
    public XMLStreamWriterImpl(final Writer writer, final PropertyManager props) throws IOException {
        this(new StreamResult(writer), null, props);
    }
    
    public XMLStreamWriterImpl(final StreamResult sr, final String encoding, final PropertyManager props) throws IOException {
        this.fEscapeCharacters = true;
        this.fIsRepairingNamespace = false;
        this.fOutputStream = null;
        this.fNamespaceContext = null;
        this.fInternalNamespaceContext = null;
        this.fPrefixGen = null;
        this.fPropertyManager = null;
        this.fStartTagOpened = false;
        this.fSymbolTable = new SymbolTable();
        this.fElementStack = new ElementStack();
        this.DEFAULT_PREFIX = this.fSymbolTable.addSymbol("");
        this.fReadOnlyIterator = new ReadOnlyIterator();
        this.fEncoder = null;
        this.fAttrNamespace = null;
        this.setOutput(sr, encoding);
        this.fPropertyManager = props;
        this.init();
    }
    
    private void init() {
        this.fReuse = false;
        this.fNamespaceDecls = new ArrayList();
        this.fPrefixGen = new Random();
        this.fAttributeCache = new ArrayList();
        (this.fInternalNamespaceContext = new NamespaceSupport()).reset();
        this.fNamespaceContext = new NamespaceContextImpl();
        this.fNamespaceContext.internalContext = this.fInternalNamespaceContext;
        Boolean ob = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
        this.fIsRepairingNamespace = ob;
        ob = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
        this.setEscapeCharacters(ob);
    }
    
    public void reset() {
        this.reset(false);
    }
    
    void reset(final boolean resetProperties) {
        if (!this.fReuse) {
            throw new IllegalStateException("close() Must be called before calling reset()");
        }
        this.fReuse = false;
        this.fNamespaceDecls.clear();
        this.fAttributeCache.clear();
        this.fElementStack.clear();
        this.fInternalNamespaceContext.reset();
        this.fStartTagOpened = false;
        this.fNamespaceContext.userContext = null;
        if (resetProperties) {
            Boolean ob = (Boolean)this.fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
            this.fIsRepairingNamespace = ob;
            ob = (Boolean)this.fPropertyManager.getProperty("escapeCharacters");
            this.setEscapeCharacters(ob);
        }
    }
    
    public void setOutput(final StreamResult sr, final String encoding) throws IOException {
        if (sr.getOutputStream() != null) {
            this.setOutputUsingStream(sr.getOutputStream(), encoding);
        }
        else if (sr.getWriter() != null) {
            this.setOutputUsingWriter(sr.getWriter());
        }
        else if (sr.getSystemId() != null) {
            this.setOutputUsingStream(new FileOutputStream(sr.getSystemId()), encoding);
        }
    }
    
    private void setOutputUsingWriter(final Writer writer) throws IOException {
        this.fWriter = writer;
        if (writer instanceof OutputStreamWriter) {
            final String charset = ((OutputStreamWriter)writer).getEncoding();
            if (charset != null && !charset.equalsIgnoreCase("utf-8")) {
                this.fEncoder = Charset.forName(charset).newEncoder();
            }
        }
    }
    
    private void setOutputUsingStream(final OutputStream os, String encoding) throws IOException {
        this.fOutputStream = os;
        if (encoding != null) {
            if (encoding.equalsIgnoreCase("utf-8")) {
                this.fWriter = new UTF8OutputStreamWriter(os);
            }
            else {
                this.fWriter = new XMLWriter(new OutputStreamWriter(os, encoding));
                this.fEncoder = Charset.forName(encoding).newEncoder();
            }
        }
        else {
            encoding = SecuritySupport.getSystemProperty("file.encoding");
            if (encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.fWriter = new UTF8OutputStreamWriter(os);
            }
            else {
                this.fWriter = new XMLWriter(new OutputStreamWriter(os));
            }
        }
    }
    
    public boolean canReuse() {
        return this.fReuse;
    }
    
    public void setEscapeCharacters(final boolean escape) {
        this.fEscapeCharacters = escape;
    }
    
    public boolean getEscapeCharacters() {
        return this.fEscapeCharacters;
    }
    
    @Override
    public void close() throws XMLStreamException {
        if (this.fWriter != null) {
            try {
                this.fWriter.flush();
            }
            catch (final IOException e) {
                throw new XMLStreamException(e);
            }
        }
        this.fWriter = null;
        this.fOutputStream = null;
        this.fNamespaceDecls.clear();
        this.fAttributeCache.clear();
        this.fElementStack.clear();
        this.fInternalNamespaceContext.reset();
        this.fReuse = true;
        this.fStartTagOpened = false;
        this.fNamespaceContext.userContext = null;
    }
    
    @Override
    public void flush() throws XMLStreamException {
        try {
            this.fWriter.flush();
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this.fNamespaceContext.getPrefix(uri);
    }
    
    @Override
    public Object getProperty(final String str) throws IllegalArgumentException {
        if (str == null) {
            throw new NullPointerException();
        }
        if (!this.fPropertyManager.containsProperty(str)) {
            throw new IllegalArgumentException("Property '" + str + "' is not supported");
        }
        return this.fPropertyManager.getProperty(str);
    }
    
    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        if (uri != null) {
            uri = this.fSymbolTable.addSymbol(uri);
        }
        if (this.fIsRepairingNamespace) {
            if (this.isDefaultNamespace(uri)) {
                return;
            }
            final QName qname = new QName();
            qname.setValues(this.DEFAULT_PREFIX, "xmlns", null, uri);
            this.fNamespaceDecls.add(qname);
        }
        else {
            this.fInternalNamespaceContext.declarePrefix(this.DEFAULT_PREFIX, uri);
        }
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this.fNamespaceContext.userContext = namespaceContext;
    }
    
    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new XMLStreamException("Prefix cannot be null");
        }
        if (uri == null) {
            throw new XMLStreamException("URI cannot be null");
        }
        prefix = this.fSymbolTable.addSymbol(prefix);
        uri = this.fSymbolTable.addSymbol(uri);
        if (!this.fIsRepairingNamespace) {
            this.fInternalNamespaceContext.declarePrefix(prefix, uri);
            return;
        }
        final String tmpURI = this.fInternalNamespaceContext.getURI(prefix);
        if (tmpURI != null && tmpURI == uri) {
            return;
        }
        if (this.checkUserNamespaceContext(prefix, uri)) {
            return;
        }
        final QName qname = new QName();
        qname.setValues(prefix, "xmlns", null, uri);
        this.fNamespaceDecls.add(qname);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        try {
            if (!this.fStartTagOpened) {
                throw new XMLStreamException("Attribute not associated with any element");
            }
            if (this.fIsRepairingNamespace) {
                final Attribute attr = new Attribute(value);
                attr.setValues(null, localName, null, null);
                this.fAttributeCache.add(attr);
                return;
            }
            this.fWriter.write(" ");
            this.fWriter.write(localName);
            this.fWriter.write("=\"");
            this.writeXMLContent(value, true, true);
            this.fWriter.write("\"");
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeAttribute(String namespaceURI, final String localName, final String value) throws XMLStreamException {
        try {
            if (!this.fStartTagOpened) {
                throw new XMLStreamException("Attribute not associated with any element");
            }
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            final String prefix = this.fInternalNamespaceContext.getPrefix(namespaceURI);
            if (!this.fIsRepairingNamespace) {
                if (prefix == null) {
                    throw new XMLStreamException("Prefix cannot be null");
                }
                this.writeAttributeWithPrefix(prefix, localName, value);
            }
            else {
                final Attribute attr = new Attribute(value);
                attr.setValues(null, localName, null, namespaceURI);
                this.fAttributeCache.add(attr);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void writeAttributeWithPrefix(final String prefix, final String localName, final String value) throws IOException {
        this.fWriter.write(" ");
        if (prefix != null && prefix != "") {
            this.fWriter.write(prefix);
            this.fWriter.write(":");
        }
        this.fWriter.write(localName);
        this.fWriter.write("=\"");
        this.writeXMLContent(value, true, true);
        this.fWriter.write("\"");
    }
    
    @Override
    public void writeAttribute(String prefix, String namespaceURI, final String localName, final String value) throws XMLStreamException {
        try {
            if (!this.fStartTagOpened) {
                throw new XMLStreamException("Attribute not associated with any element");
            }
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (localName == null) {
                throw new XMLStreamException("Local name cannot be null");
            }
            if (!this.fIsRepairingNamespace) {
                if (prefix == null || prefix.equals("")) {
                    if (!namespaceURI.equals("")) {
                        throw new XMLStreamException("prefix cannot be null or empty");
                    }
                    this.writeAttributeWithPrefix(null, localName, value);
                }
                else {
                    if (!prefix.equals("xml") || !namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
                        prefix = this.fSymbolTable.addSymbol(prefix);
                        namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
                        if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(prefix)) {
                            final String tmpURI = this.fInternalNamespaceContext.getURI(prefix);
                            if (tmpURI != null && tmpURI != namespaceURI) {
                                throw new XMLStreamException("Prefix " + prefix + " is already bound to " + tmpURI + ". Trying to rebind it to " + namespaceURI + " is an error.");
                            }
                        }
                        this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURI);
                    }
                    this.writeAttributeWithPrefix(prefix, localName, value);
                }
            }
            else {
                if (prefix != null) {
                    prefix = this.fSymbolTable.addSymbol(prefix);
                }
                namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
                final Attribute attr = new Attribute(value);
                attr.setValues(prefix, localName, null, namespaceURI);
                this.fAttributeCache.add(attr);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCData(final String cdata) throws XMLStreamException {
        try {
            if (cdata == null) {
                throw new XMLStreamException("cdata cannot be null");
            }
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.fWriter.write("<![CDATA[");
            this.fWriter.write(cdata);
            this.fWriter.write("]]>");
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCharacters(final String data) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.writeXMLContent(data);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeCharacters(final char[] data, final int start, final int len) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.writeXMLContent(data, start, len, this.fEscapeCharacters);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeComment(final String comment) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.fWriter.write("<!--");
            if (comment != null) {
                this.fWriter.write(comment);
            }
            this.fWriter.write("-->");
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.fWriter.write(dtd);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        String namespaceURINormalized = null;
        if (namespaceURI == null) {
            namespaceURINormalized = "";
        }
        else {
            namespaceURINormalized = namespaceURI;
        }
        try {
            if (!this.fStartTagOpened) {
                throw new IllegalStateException("Namespace Attribute not associated with any element");
            }
            if (this.fIsRepairingNamespace) {
                final QName qname = new QName();
                qname.setValues("", "xmlns", null, namespaceURINormalized);
                this.fNamespaceDecls.add(qname);
                return;
            }
            namespaceURINormalized = this.fSymbolTable.addSymbol(namespaceURINormalized);
            if (this.fInternalNamespaceContext.containsPrefixInCurrentContext("")) {
                final String tmp = this.fInternalNamespaceContext.getURI("");
                if (tmp != null && tmp != namespaceURINormalized) {
                    throw new XMLStreamException("xmlns has been already bound to " + tmp + ". Rebinding it to " + namespaceURINormalized + " is an error");
                }
            }
            this.fInternalNamespaceContext.declarePrefix("", namespaceURINormalized);
            this.writenamespace(null, namespaceURINormalized);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.openStartTag();
            this.fElementStack.push(null, localName, null, null, true);
            this.fInternalNamespaceContext.pushContext();
            if (!this.fIsRepairingNamespace) {
                this.fWriter.write(localName);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeEmptyElement(String namespaceURI, final String localName) throws XMLStreamException {
        if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
        }
        namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
        final String prefix = this.fNamespaceContext.getPrefix(namespaceURI);
        this.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(String prefix, final String localName, String namespaceURI) throws XMLStreamException {
        try {
            if (localName == null) {
                throw new XMLStreamException("Local Name cannot be null");
            }
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (prefix != null) {
                prefix = this.fSymbolTable.addSymbol(prefix);
            }
            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.openStartTag();
            this.fElementStack.push(prefix, localName, null, namespaceURI, true);
            this.fInternalNamespaceContext.pushContext();
            if (this.fIsRepairingNamespace) {
                return;
            }
            if (prefix == null) {
                throw new XMLStreamException("NamespaceURI " + namespaceURI + " has not been bound to any prefix");
            }
            if (prefix != null && prefix != "") {
                this.fWriter.write(prefix);
                this.fWriter.write(":");
            }
            this.fWriter.write(localName);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            ElementState elem = null;
            while (!this.fElementStack.empty()) {
                elem = this.fElementStack.pop();
                this.fInternalNamespaceContext.popContext();
                if (elem.isEmpty) {
                    continue;
                }
                this.fWriter.write("</");
                if (elem.prefix != null && !elem.prefix.equals("")) {
                    this.fWriter.write(elem.prefix);
                    this.fWriter.write(":");
                }
                this.fWriter.write(elem.localpart);
                this.fWriter.write(62);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
        catch (final ArrayIndexOutOfBoundsException e2) {
            throw new XMLStreamException("No more elements to write");
        }
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            final ElementState currentElement = this.fElementStack.pop();
            if (currentElement == null) {
                throw new XMLStreamException("No element was found to write");
            }
            if (currentElement.isEmpty) {
                return;
            }
            this.fWriter.write("</");
            if (currentElement.prefix != null && !currentElement.prefix.equals("")) {
                this.fWriter.write(currentElement.prefix);
                this.fWriter.write(":");
            }
            this.fWriter.write(currentElement.localpart);
            this.fWriter.write(62);
            this.fInternalNamespaceContext.popContext();
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
        catch (final ArrayIndexOutOfBoundsException e2) {
            throw new XMLStreamException("No element was found to write: " + e2.toString(), e2);
        }
    }
    
    @Override
    public void writeEntityRef(final String refName) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.fWriter.write(38);
            this.fWriter.write(refName);
            this.fWriter.write(59);
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeNamespace(String prefix, final String namespaceURI) throws XMLStreamException {
        String namespaceURINormalized = null;
        if (namespaceURI == null) {
            namespaceURINormalized = "";
        }
        else {
            namespaceURINormalized = namespaceURI;
        }
        try {
            QName qname = null;
            if (!this.fStartTagOpened) {
                throw new IllegalStateException("Invalid state: start tag is not opened at writeNamespace(" + prefix + ", " + namespaceURINormalized + ")");
            }
            if (prefix == null || prefix.equals("") || prefix.equals("xmlns")) {
                this.writeDefaultNamespace(namespaceURINormalized);
                return;
            }
            if (prefix.equals("xml") && namespaceURINormalized.equals("http://www.w3.org/XML/1998/namespace")) {
                return;
            }
            prefix = this.fSymbolTable.addSymbol(prefix);
            namespaceURINormalized = this.fSymbolTable.addSymbol(namespaceURINormalized);
            if (this.fIsRepairingNamespace) {
                final String tmpURI = this.fInternalNamespaceContext.getURI(prefix);
                if (tmpURI != null && tmpURI == namespaceURINormalized) {
                    return;
                }
                qname = new QName();
                qname.setValues(prefix, "xmlns", null, namespaceURINormalized);
                this.fNamespaceDecls.add(qname);
            }
            else {
                if (this.fInternalNamespaceContext.containsPrefixInCurrentContext(prefix)) {
                    final String tmp = this.fInternalNamespaceContext.getURI(prefix);
                    if (tmp != null && tmp != namespaceURINormalized) {
                        throw new XMLStreamException("prefix " + prefix + " has been already bound to " + tmp + ". Rebinding it to " + namespaceURINormalized + " is an error");
                    }
                }
                this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURINormalized);
                this.writenamespace(prefix, namespaceURINormalized);
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void writenamespace(final String prefix, final String namespaceURI) throws IOException {
        this.fWriter.write(" xmlns");
        if (prefix != null && prefix != "") {
            this.fWriter.write(":");
            this.fWriter.write(prefix);
        }
        this.fWriter.write("=\"");
        this.writeXMLContent(namespaceURI, true, true);
        this.fWriter.write("\"");
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            if (target != null) {
                this.fWriter.write("<?");
                this.fWriter.write(target);
                this.fWriter.write("?>");
                return;
            }
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
        throw new XMLStreamException("PI target cannot be null");
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        try {
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            if (target == null || data == null) {
                throw new XMLStreamException("PI target cannot be null");
            }
            this.fWriter.write("<?");
            this.fWriter.write(target);
            this.fWriter.write(" ");
            this.fWriter.write(data);
            this.fWriter.write("?>");
        }
        catch (final IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        try {
            this.fWriter.write("<?xml version=\"1.0\" ?>");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        try {
            if (version == null || version.equals("")) {
                this.writeStartDocument();
                return;
            }
            this.fWriter.write("<?xml version=\"");
            this.fWriter.write(version);
            this.fWriter.write("\"");
            this.fWriter.write("?>");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        try {
            if (encoding == null && version == null) {
                this.writeStartDocument();
                return;
            }
            if (encoding == null) {
                this.writeStartDocument(version);
                return;
            }
            String streamEncoding = null;
            if (this.fWriter instanceof OutputStreamWriter) {
                streamEncoding = ((OutputStreamWriter)this.fWriter).getEncoding();
            }
            else if (this.fWriter instanceof UTF8OutputStreamWriter) {
                streamEncoding = ((UTF8OutputStreamWriter)this.fWriter).getEncoding();
            }
            else if (this.fWriter instanceof XMLWriter) {
                streamEncoding = ((OutputStreamWriter)((XMLWriter)this.fWriter).getWriter()).getEncoding();
            }
            if (streamEncoding != null && !streamEncoding.equalsIgnoreCase(encoding)) {
                boolean foundAlias = false;
                final Set aliases = Charset.forName(encoding).aliases();
                for (Iterator it = aliases.iterator(); !foundAlias && it.hasNext(); foundAlias = true) {
                    if (streamEncoding.equalsIgnoreCase(it.next())) {}
                }
                if (!foundAlias) {
                    throw new XMLStreamException("Underlying stream encoding '" + streamEncoding + "' and input paramter for writeStartDocument() method '" + encoding + "' do not match.");
                }
            }
            this.fWriter.write("<?xml version=\"");
            if (version == null || version.equals("")) {
                this.fWriter.write("1.0");
            }
            else {
                this.fWriter.write(version);
            }
            if (!encoding.equals("")) {
                this.fWriter.write("\" encoding=\"");
                this.fWriter.write(encoding);
            }
            this.fWriter.write("\"?>");
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        try {
            if (localName == null) {
                throw new XMLStreamException("Local Name cannot be null");
            }
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.openStartTag();
            this.fElementStack.push(null, localName, null, null, false);
            this.fInternalNamespaceContext.pushContext();
            if (this.fIsRepairingNamespace) {
                return;
            }
            this.fWriter.write(localName);
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeStartElement(String namespaceURI, final String localName) throws XMLStreamException {
        if (localName == null) {
            throw new XMLStreamException("Local Name cannot be null");
        }
        if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
        }
        namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
        String prefix = null;
        if (!this.fIsRepairingNamespace) {
            prefix = this.fNamespaceContext.getPrefix(namespaceURI);
            if (prefix != null) {
                prefix = this.fSymbolTable.addSymbol(prefix);
            }
        }
        this.writeStartElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeStartElement(String prefix, final String localName, String namespaceURI) throws XMLStreamException {
        try {
            if (localName == null) {
                throw new XMLStreamException("Local Name cannot be null");
            }
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (!this.fIsRepairingNamespace && prefix == null) {
                throw new XMLStreamException("Prefix cannot be null");
            }
            if (this.fStartTagOpened) {
                this.closeStartTag();
            }
            this.openStartTag();
            namespaceURI = this.fSymbolTable.addSymbol(namespaceURI);
            if (prefix != null) {
                prefix = this.fSymbolTable.addSymbol(prefix);
            }
            this.fElementStack.push(prefix, localName, null, namespaceURI, false);
            this.fInternalNamespaceContext.pushContext();
            final String tmpPrefix = this.fNamespaceContext.getPrefix(namespaceURI);
            if (prefix != null && (tmpPrefix == null || !prefix.equals(tmpPrefix))) {
                this.fInternalNamespaceContext.declarePrefix(prefix, namespaceURI);
            }
            if (this.fIsRepairingNamespace) {
                if (prefix == null || (tmpPrefix != null && prefix.equals(tmpPrefix))) {
                    return;
                }
                final QName qname = new QName();
                qname.setValues(prefix, "xmlns", null, namespaceURI);
                this.fNamespaceDecls.add(qname);
            }
            else {
                if (prefix != null && prefix != "") {
                    this.fWriter.write(prefix);
                    this.fWriter.write(":");
                }
                this.fWriter.write(localName);
            }
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    private void writeCharRef(final int codePoint) throws IOException {
        this.fWriter.write("&#x");
        this.fWriter.write(Integer.toHexString(codePoint));
        this.fWriter.write(59);
    }
    
    private void writeXMLContent(final char[] content, final int start, final int length, final boolean escapeChars) throws IOException {
        if (!escapeChars) {
            this.fWriter.write(content, start, length);
            return;
        }
        int startWritePos = start;
        final int end = start + length;
        for (int index = start; index < end; ++index) {
            final char ch = content[index];
            if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
                this.fWriter.write(content, startWritePos, index - startWritePos);
                if (index != end - 1 && Character.isSurrogatePair(ch, content[index + 1])) {
                    this.writeCharRef(Character.toCodePoint(ch, content[index + 1]));
                    ++index;
                }
                else {
                    this.writeCharRef(ch);
                }
                startWritePos = index + 1;
            }
            else {
                switch (ch) {
                    case '<': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&lt;");
                        startWritePos = index + 1;
                        break;
                    }
                    case '&': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&amp;");
                        startWritePos = index + 1;
                        break;
                    }
                    case '>': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&gt;");
                        startWritePos = index + 1;
                        break;
                    }
                }
            }
        }
        this.fWriter.write(content, startWritePos, end - startWritePos);
    }
    
    private void writeXMLContent(final String content) throws IOException {
        if (content != null && content.length() > 0) {
            this.writeXMLContent(content, this.fEscapeCharacters, false);
        }
    }
    
    private void writeXMLContent(final String content, final boolean escapeChars, final boolean escapeDoubleQuotes) throws IOException {
        if (!escapeChars) {
            this.fWriter.write(content);
            return;
        }
        int startWritePos = 0;
        final int end = content.length();
        for (int index = 0; index < end; ++index) {
            final char ch = content.charAt(index);
            if (this.fEncoder != null && !this.fEncoder.canEncode(ch)) {
                this.fWriter.write(content, startWritePos, index - startWritePos);
                if (index != end - 1 && Character.isSurrogatePair(ch, content.charAt(index + 1))) {
                    this.writeCharRef(Character.toCodePoint(ch, content.charAt(index + 1)));
                    ++index;
                }
                else {
                    this.writeCharRef(ch);
                }
                startWritePos = index + 1;
            }
            else {
                switch (ch) {
                    case '<': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&lt;");
                        startWritePos = index + 1;
                        break;
                    }
                    case '&': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&amp;");
                        startWritePos = index + 1;
                        break;
                    }
                    case '>': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        this.fWriter.write("&gt;");
                        startWritePos = index + 1;
                        break;
                    }
                    case '\"': {
                        this.fWriter.write(content, startWritePos, index - startWritePos);
                        if (escapeDoubleQuotes) {
                            this.fWriter.write("&quot;");
                        }
                        else {
                            this.fWriter.write(34);
                        }
                        startWritePos = index + 1;
                        break;
                    }
                }
            }
        }
        this.fWriter.write(content, startWritePos, end - startWritePos);
    }
    
    private void closeStartTag() throws XMLStreamException {
        try {
            final ElementState currentElement = this.fElementStack.peek();
            if (this.fIsRepairingNamespace) {
                this.repair();
                this.correctPrefix(currentElement, 1);
                if (currentElement.prefix != null && currentElement.prefix != "") {
                    this.fWriter.write(currentElement.prefix);
                    this.fWriter.write(":");
                }
                this.fWriter.write(currentElement.localpart);
                final int len = this.fNamespaceDecls.size();
                QName qname = null;
                for (int i = 0; i < len; ++i) {
                    qname = this.fNamespaceDecls.get(i);
                    if (qname != null && this.fInternalNamespaceContext.declarePrefix(qname.prefix, qname.uri)) {
                        this.writenamespace(qname.prefix, qname.uri);
                    }
                }
                this.fNamespaceDecls.clear();
                Attribute attr = null;
                for (int j = 0; j < this.fAttributeCache.size(); ++j) {
                    attr = this.fAttributeCache.get(j);
                    if (attr.prefix != null && attr.uri != null && !attr.prefix.equals("") && !attr.uri.equals("")) {
                        String tmp = this.fInternalNamespaceContext.getPrefix(attr.uri);
                        if (tmp == null || tmp != attr.prefix) {
                            tmp = this.getAttrPrefix(attr.uri);
                            if (tmp == null) {
                                if (this.fInternalNamespaceContext.declarePrefix(attr.prefix, attr.uri)) {
                                    this.writenamespace(attr.prefix, attr.uri);
                                }
                            }
                            else {
                                this.writenamespace(attr.prefix, attr.uri);
                            }
                        }
                    }
                    this.writeAttributeWithPrefix(attr.prefix, attr.localpart, attr.value);
                }
                this.fAttrNamespace = null;
                this.fAttributeCache.clear();
            }
            if (currentElement.isEmpty) {
                this.fElementStack.pop();
                this.fInternalNamespaceContext.popContext();
                this.fWriter.write("/>");
            }
            else {
                this.fWriter.write(62);
            }
            this.fStartTagOpened = false;
        }
        catch (final IOException ex) {
            this.fStartTagOpened = false;
            throw new XMLStreamException(ex);
        }
    }
    
    private void openStartTag() throws IOException {
        this.fStartTagOpened = true;
        this.fWriter.write(60);
    }
    
    private void correctPrefix(final QName attr, final int type) {
        String tmpPrefix = null;
        String prefix = attr.prefix;
        String uri = attr.uri;
        boolean isSpecialCaseURI = false;
        if (prefix == null || prefix.equals("")) {
            if (uri == null) {
                return;
            }
            if (prefix == "" && uri == "") {
                return;
            }
            uri = this.fSymbolTable.addSymbol(uri);
            QName decl = null;
            for (int i = 0; i < this.fNamespaceDecls.size(); ++i) {
                decl = this.fNamespaceDecls.get(i);
                if (decl != null && decl.uri == attr.uri) {
                    attr.prefix = decl.prefix;
                    return;
                }
            }
            tmpPrefix = this.fNamespaceContext.getPrefix(uri);
            if (tmpPrefix == "") {
                if (type == 1) {
                    return;
                }
                if (type == 10) {
                    tmpPrefix = this.getAttrPrefix(uri);
                    isSpecialCaseURI = true;
                }
            }
            if (tmpPrefix == null) {
                final StringBuffer genPrefix = new StringBuffer("zdef");
                for (int j = 0; j < 1; ++j) {
                    genPrefix.append(this.fPrefixGen.nextInt());
                }
                prefix = genPrefix.toString();
                prefix = this.fSymbolTable.addSymbol(prefix);
            }
            else {
                prefix = this.fSymbolTable.addSymbol(tmpPrefix);
            }
            if (tmpPrefix == null) {
                if (isSpecialCaseURI) {
                    this.addAttrNamespace(prefix, uri);
                }
                else {
                    final QName qname = new QName();
                    qname.setValues(prefix, "xmlns", null, uri);
                    this.fNamespaceDecls.add(qname);
                    this.fInternalNamespaceContext.declarePrefix(this.fSymbolTable.addSymbol(prefix), uri);
                }
            }
        }
        attr.prefix = prefix;
    }
    
    private String getAttrPrefix(final String uri) {
        if (this.fAttrNamespace != null) {
            return this.fAttrNamespace.get(uri);
        }
        return null;
    }
    
    private void addAttrNamespace(final String prefix, final String uri) {
        if (this.fAttrNamespace == null) {
            this.fAttrNamespace = new HashMap();
        }
        this.fAttrNamespace.put(prefix, uri);
    }
    
    private boolean isDefaultNamespace(final String uri) {
        final String defaultNamespace = this.fInternalNamespaceContext.getURI(this.DEFAULT_PREFIX);
        return uri == defaultNamespace;
    }
    
    private boolean checkUserNamespaceContext(final String prefix, final String uri) {
        if (this.fNamespaceContext.userContext != null) {
            final String tmpURI = this.fNamespaceContext.userContext.getNamespaceURI(prefix);
            if (tmpURI != null && tmpURI.equals(uri)) {
                return true;
            }
        }
        return false;
    }
    
    protected void repair() {
        Attribute attr = null;
        Attribute attr2 = null;
        final ElementState currentElement = this.fElementStack.peek();
        this.removeDuplicateDecls();
        for (int i = 0; i < this.fAttributeCache.size(); ++i) {
            attr = this.fAttributeCache.get(i);
            if ((attr.prefix != null && !attr.prefix.equals("")) || (attr.uri != null && !attr.uri.equals(""))) {
                this.correctPrefix(currentElement, attr);
            }
        }
        if (!this.isDeclared(currentElement) && currentElement.prefix != null && currentElement.uri != null && !currentElement.prefix.equals("") && !currentElement.uri.equals("")) {
            this.fNamespaceDecls.add(currentElement);
        }
        for (int i = 0; i < this.fAttributeCache.size(); ++i) {
            attr = this.fAttributeCache.get(i);
            for (int j = i + 1; j < this.fAttributeCache.size(); ++j) {
                attr2 = this.fAttributeCache.get(j);
                if (!"".equals(attr.prefix) && !"".equals(attr2.prefix)) {
                    this.correctPrefix(attr, attr2);
                }
            }
        }
        this.repairNamespaceDecl(currentElement);
        int i;
        for (i = 0, i = 0; i < this.fAttributeCache.size(); ++i) {
            attr = this.fAttributeCache.get(i);
            if (attr.prefix != null && attr.prefix.equals("") && attr.uri != null && attr.uri.equals("")) {
                this.repairNamespaceDecl(attr);
            }
        }
        QName qname = null;
        for (i = 0; i < this.fNamespaceDecls.size(); ++i) {
            qname = this.fNamespaceDecls.get(i);
            if (qname != null) {
                this.fInternalNamespaceContext.declarePrefix(qname.prefix, qname.uri);
            }
        }
        for (i = 0; i < this.fAttributeCache.size(); ++i) {
            attr = this.fAttributeCache.get(i);
            this.correctPrefix(attr, 10);
        }
    }
    
    void correctPrefix(final QName attr1, final QName attr2) {
        String tmpPrefix = null;
        QName decl = null;
        final boolean done = false;
        this.checkForNull(attr1);
        this.checkForNull(attr2);
        if (attr1.prefix.equals(attr2.prefix) && !attr1.uri.equals(attr2.uri)) {
            tmpPrefix = this.fNamespaceContext.getPrefix(attr2.uri);
            if (tmpPrefix != null) {
                attr2.prefix = this.fSymbolTable.addSymbol(tmpPrefix);
            }
            else {
                decl = null;
                for (int n = 0; n < this.fNamespaceDecls.size(); ++n) {
                    decl = this.fNamespaceDecls.get(n);
                    if (decl != null && decl.uri == attr2.uri) {
                        attr2.prefix = decl.prefix;
                        return;
                    }
                }
                final StringBuffer genPrefix = new StringBuffer("zdef");
                for (int k = 0; k < 1; ++k) {
                    genPrefix.append(this.fPrefixGen.nextInt());
                }
                tmpPrefix = genPrefix.toString();
                tmpPrefix = this.fSymbolTable.addSymbol(tmpPrefix);
                attr2.prefix = tmpPrefix;
                final QName qname = new QName();
                qname.setValues(tmpPrefix, "xmlns", null, attr2.uri);
                this.fNamespaceDecls.add(qname);
            }
        }
    }
    
    void checkForNull(final QName attr) {
        if (attr.prefix == null) {
            attr.prefix = "";
        }
        if (attr.uri == null) {
            attr.uri = "";
        }
    }
    
    void removeDuplicateDecls() {
        for (int i = 0; i < this.fNamespaceDecls.size(); ++i) {
            final QName decl1 = this.fNamespaceDecls.get(i);
            if (decl1 != null) {
                for (int j = i + 1; j < this.fNamespaceDecls.size(); ++j) {
                    final QName decl2 = this.fNamespaceDecls.get(j);
                    if (decl2 != null && decl1.prefix.equals(decl2.prefix) && decl1.uri.equals(decl2.uri)) {
                        this.fNamespaceDecls.remove(j);
                    }
                }
            }
        }
    }
    
    void repairNamespaceDecl(final QName attr) {
        QName decl = null;
        for (int j = 0; j < this.fNamespaceDecls.size(); ++j) {
            decl = this.fNamespaceDecls.get(j);
            if (decl != null && attr.prefix != null && attr.prefix.equals(decl.prefix) && !attr.uri.equals(decl.uri)) {
                final String tmpURI = this.fNamespaceContext.getNamespaceURI(attr.prefix);
                if (tmpURI != null) {
                    if (tmpURI.equals(attr.uri)) {
                        this.fNamespaceDecls.set(j, null);
                    }
                    else {
                        decl.uri = attr.uri;
                    }
                }
            }
        }
    }
    
    boolean isDeclared(final QName attr) {
        QName decl = null;
        for (int n = 0; n < this.fNamespaceDecls.size(); ++n) {
            decl = this.fNamespaceDecls.get(n);
            if (attr.prefix != null && attr.prefix == decl.prefix && decl.uri == attr.uri) {
                return true;
            }
        }
        return attr.uri != null && this.fNamespaceContext.getPrefix(attr.uri) != null;
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return key.equals("sjsxp-outputstream");
    }
    
    @Override
    public Object get(final Object key) {
        if (key.equals("sjsxp-outputstream")) {
            return this.fOutputStream;
        }
        return null;
    }
    
    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
    }
    
    @Override
    public int hashCode() {
        return this.fElementStack.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    protected class ElementStack
    {
        protected ElementState[] fElements;
        protected short fDepth;
        
        public ElementStack() {
            this.fElements = new ElementState[10];
            for (int i = 0; i < this.fElements.length; ++i) {
                this.fElements[i] = new ElementState();
            }
        }
        
        public ElementState push(final ElementState element) {
            if (this.fDepth == this.fElements.length) {
                final ElementState[] array = new ElementState[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
                this.fElements = array;
                for (int i = this.fDepth; i < this.fElements.length; ++i) {
                    this.fElements[i] = new ElementState();
                }
            }
            this.fElements[this.fDepth].setValues(element);
            final ElementState[] fElements = this.fElements;
            final short fDepth = this.fDepth;
            this.fDepth = (short)(fDepth + 1);
            return fElements[fDepth];
        }
        
        public ElementState push(final String prefix, final String localpart, final String rawname, final String uri, final boolean isEmpty) {
            if (this.fDepth == this.fElements.length) {
                final ElementState[] array = new ElementState[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
                this.fElements = array;
                for (int i = this.fDepth; i < this.fElements.length; ++i) {
                    this.fElements[i] = new ElementState();
                }
            }
            this.fElements[this.fDepth].setValues(prefix, localpart, rawname, uri, isEmpty);
            final ElementState[] fElements = this.fElements;
            final short fDepth = this.fDepth;
            this.fDepth = (short)(fDepth + 1);
            return fElements[fDepth];
        }
        
        public ElementState pop() {
            final ElementState[] fElements = this.fElements;
            final short fDepth = (short)(this.fDepth - 1);
            this.fDepth = fDepth;
            return fElements[fDepth];
        }
        
        public void clear() {
            this.fDepth = 0;
        }
        
        public ElementState peek() {
            return this.fElements[this.fDepth - 1];
        }
        
        public boolean empty() {
            return this.fDepth <= 0;
        }
    }
    
    class ElementState extends QName
    {
        public boolean isEmpty;
        
        public ElementState() {
            this.isEmpty = false;
        }
        
        public ElementState(final String prefix, final String localpart, final String rawname, final String uri) {
            super(prefix, localpart, rawname, uri);
            this.isEmpty = false;
        }
        
        public void setValues(final String prefix, final String localpart, final String rawname, final String uri, final boolean isEmpty) {
            super.setValues(prefix, localpart, rawname, uri);
            this.isEmpty = isEmpty;
        }
    }
    
    class Attribute extends QName
    {
        String value;
        
        Attribute(final String value) {
            this.value = value;
        }
    }
    
    class NamespaceContextImpl implements NamespaceContext
    {
        NamespaceContext userContext;
        NamespaceSupport internalContext;
        
        NamespaceContextImpl() {
            this.userContext = null;
            this.internalContext = null;
        }
        
        @Override
        public String getNamespaceURI(String prefix) {
            String uri = null;
            if (prefix != null) {
                prefix = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(prefix);
            }
            if (this.internalContext != null) {
                uri = this.internalContext.getURI(prefix);
                if (uri != null) {
                    return uri;
                }
            }
            if (this.userContext != null) {
                uri = this.userContext.getNamespaceURI(prefix);
                return uri;
            }
            return null;
        }
        
        @Override
        public String getPrefix(String uri) {
            String prefix = null;
            if (uri != null) {
                uri = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(uri);
            }
            if (this.internalContext != null) {
                prefix = this.internalContext.getPrefix(uri);
                if (prefix != null) {
                    return prefix;
                }
            }
            if (this.userContext != null) {
                return this.userContext.getPrefix(uri);
            }
            return null;
        }
        
        @Override
        public Iterator getPrefixes(String uri) {
            Vector prefixes = null;
            Iterator itr = null;
            if (uri != null) {
                uri = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(uri);
            }
            if (this.userContext != null) {
                itr = this.userContext.getPrefixes(uri);
            }
            if (this.internalContext != null) {
                prefixes = this.internalContext.getPrefixes(uri);
            }
            if (prefixes == null && itr != null) {
                return itr;
            }
            if (prefixes != null && itr == null) {
                return new ReadOnlyIterator(prefixes.iterator());
            }
            if (prefixes != null && itr != null) {
                String ob = null;
                while (itr.hasNext()) {
                    ob = itr.next();
                    if (ob != null) {
                        ob = XMLStreamWriterImpl.this.fSymbolTable.addSymbol(ob);
                    }
                    if (!prefixes.contains(ob)) {
                        prefixes.add(ob);
                    }
                }
                return new ReadOnlyIterator(prefixes.iterator());
            }
            return XMLStreamWriterImpl.this.fReadOnlyIterator;
        }
    }
}
