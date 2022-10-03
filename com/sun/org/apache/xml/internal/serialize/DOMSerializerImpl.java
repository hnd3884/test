package com.sun.org.apache.xml.internal.serialize;

import java.util.StringTokenizer;
import org.w3c.dom.NamedNodeMap;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.dom.DOMNormalizer;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import java.net.URLConnection;
import java.io.OutputStream;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.io.FileOutputStream;
import java.net.URL;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import org.w3c.dom.ls.LSOutput;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import org.w3c.dom.ls.LSSerializerFilter;
import java.lang.reflect.Method;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.dom.AbortException;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.DOMError;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import java.io.Writer;
import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import java.util.Vector;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
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
    protected static final short FORMAT_PRETTY_PRINT = 2048;
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
    
    @Override
    public DOMConfiguration getDomConfig() {
        return this;
    }
    
    @Override
    public void setParameter(final String name, final Object value) throws DOMException {
        if (value instanceof Boolean) {
            final boolean state = (boolean)value;
            if (name.equalsIgnoreCase("infoset")) {
                if (state) {
                    this.features &= 0xFFFFFFFB;
                    this.features &= 0xFFFFFFF7;
                    this.features |= 0x1;
                    this.features |= 0x200;
                    this.features |= 0x2;
                    this.features |= 0x20;
                }
            }
            else if (name.equalsIgnoreCase("xml-declaration")) {
                this.features = (short)(state ? (this.features | 0x100) : (this.features & 0xFFFFFEFF));
            }
            else if (name.equalsIgnoreCase("namespaces")) {
                this.features = (short)(state ? (this.features | 0x1) : (this.features & 0xFFFFFFFE));
                this.serializer.fNamespaces = state;
            }
            else if (name.equalsIgnoreCase("split-cdata-sections")) {
                this.features = (short)(state ? (this.features | 0x10) : (this.features & 0xFFFFFFEF));
            }
            else if (name.equalsIgnoreCase("discard-default-content")) {
                this.features = (short)(state ? (this.features | 0x40) : (this.features & 0xFFFFFFBF));
            }
            else if (name.equalsIgnoreCase("well-formed")) {
                this.features = (short)(state ? (this.features | 0x2) : (this.features & 0xFFFFFFFD));
            }
            else if (name.equalsIgnoreCase("entities")) {
                this.features = (short)(state ? (this.features | 0x4) : (this.features & 0xFFFFFFFB));
            }
            else if (name.equalsIgnoreCase("cdata-sections")) {
                this.features = (short)(state ? (this.features | 0x8) : (this.features & 0xFFFFFFF7));
            }
            else if (name.equalsIgnoreCase("comments")) {
                this.features = (short)(state ? (this.features | 0x20) : (this.features & 0xFFFFFFDF));
            }
            else if (name.equalsIgnoreCase("format-pretty-print")) {
                this.features = (short)(state ? (this.features | 0x800) : (this.features & 0xFFFFF7FF));
            }
            else if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization")) {
                if (state) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
            }
            else if (name.equalsIgnoreCase("namespace-declarations")) {
                this.features = (short)(state ? (this.features | 0x200) : (this.features & 0xFFFFFDFF));
                this.serializer.fNamespacePrefixes = state;
            }
            else {
                if (!name.equalsIgnoreCase("element-content-whitespace") && !name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
                if (!state) {
                    final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                    throw new DOMException((short)9, msg);
                }
            }
        }
        else if (name.equalsIgnoreCase("error-handler")) {
            if (value != null && !(value instanceof DOMErrorHandler)) {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { name });
                throw new DOMException((short)17, msg2);
            }
            this.fErrorHandler = (DOMErrorHandler)value;
        }
        else {
            if (name.equalsIgnoreCase("resource-resolver") || name.equalsIgnoreCase("schema-location") || name.equalsIgnoreCase("schema-type") || (name.equalsIgnoreCase("normalize-characters") && value != null)) {
                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                throw new DOMException((short)9, msg2);
            }
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
            throw new DOMException((short)8, msg2);
        }
    }
    
    @Override
    public boolean canSetParameter(final String name, final Object state) {
        if (state == null) {
            return true;
        }
        if (state instanceof Boolean) {
            final boolean value = (boolean)state;
            if (name.equalsIgnoreCase("namespaces") || name.equalsIgnoreCase("split-cdata-sections") || name.equalsIgnoreCase("discard-default-content") || name.equalsIgnoreCase("xml-declaration") || name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("infoset") || name.equalsIgnoreCase("entities") || name.equalsIgnoreCase("cdata-sections") || name.equalsIgnoreCase("comments") || name.equalsIgnoreCase("namespace-declarations") || name.equalsIgnoreCase("format-pretty-print")) {
                return true;
            }
            if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization")) {
                return !value;
            }
            if (name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return value;
            }
        }
        else if ((name.equalsIgnoreCase("error-handler") && state == null) || state instanceof DOMErrorHandler) {
            return true;
        }
        return false;
    }
    
    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            final Vector parameters = new Vector();
            parameters.add("namespaces");
            parameters.add("split-cdata-sections");
            parameters.add("discard-default-content");
            parameters.add("xml-declaration");
            parameters.add("canonical-form");
            parameters.add("validate-if-schema");
            parameters.add("validate");
            parameters.add("check-character-normalization");
            parameters.add("datatype-normalization");
            parameters.add("format-pretty-print");
            parameters.add("well-formed");
            parameters.add("infoset");
            parameters.add("namespace-declarations");
            parameters.add("element-content-whitespace");
            parameters.add("entities");
            parameters.add("cdata-sections");
            parameters.add("comments");
            parameters.add("ignore-unknown-character-denormalizations");
            parameters.add("error-handler");
            this.fRecognizedParameters = new DOMStringListImpl(parameters);
        }
        return this.fRecognizedParameters;
    }
    
    @Override
    public Object getParameter(final String name) throws DOMException {
        if (name.equalsIgnoreCase("normalize-characters")) {
            return null;
        }
        if (name.equalsIgnoreCase("comments")) {
            return ((this.features & 0x20) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespaces")) {
            return ((this.features & 0x1) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("xml-declaration")) {
            return ((this.features & 0x100) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("cdata-sections")) {
            return ((this.features & 0x8) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("entities")) {
            return ((this.features & 0x4) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("split-cdata-sections")) {
            return ((this.features & 0x10) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("well-formed")) {
            return ((this.features & 0x2) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespace-declarations")) {
            return ((this.features & 0x200) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("format-pretty-print")) {
            return ((this.features & 0x800) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
            return Boolean.TRUE;
        }
        if (name.equalsIgnoreCase("discard-default-content")) {
            return ((this.features & 0x40) != 0x0) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("infoset")) {
            if ((this.features & 0x4) == 0x0 && (this.features & 0x8) == 0x0 && (this.features & 0x1) != 0x0 && (this.features & 0x200) != 0x0 && (this.features & 0x2) != 0x0 && (this.features & 0x20) != 0x0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        else {
            if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("datatype-normalization")) {
                return Boolean.FALSE;
            }
            if (name.equalsIgnoreCase("error-handler")) {
                return this.fErrorHandler;
            }
            if (name.equalsIgnoreCase("resource-resolver") || name.equalsIgnoreCase("schema-location") || name.equalsIgnoreCase("schema-type")) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { name });
                throw new DOMException((short)9, msg);
            }
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { name });
            throw new DOMException((short)8, msg);
        }
    }
    
    @Override
    public String writeToString(final Node wnode) throws DOMException, LSException {
        final Document doc = (Document)((wnode.getNodeType() == 9) ? wnode : wnode.getOwnerDocument());
        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        try {
            getVersion = doc.getClass().getMethod("getXmlVersion", (Class<?>[])new Class[0]);
            if (getVersion != null) {
                ver = (String)getVersion.invoke(doc, (Object[])null);
            }
        }
        catch (final Exception ex) {}
        if (ver != null && ver.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            ser = this.xml11Serializer;
        }
        else {
            ser = this.serializer;
        }
        final StringWriter destination = new StringWriter();
        try {
            this.prepareForSerialization(ser, wnode);
            ser._format.setEncoding("UTF-16");
            ser.setOutputCharStream(destination);
            if (wnode.getNodeType() == 9) {
                ser.serialize((Document)wnode);
            }
            else if (wnode.getNodeType() == 11) {
                ser.serialize((DocumentFragment)wnode);
            }
            else if (wnode.getNodeType() == 1) {
                ser.serialize((Element)wnode);
            }
            else {
                if (wnode.getNodeType() != 3 && wnode.getNodeType() != 8 && wnode.getNodeType() != 5 && wnode.getNodeType() != 4 && wnode.getNodeType() != 7) {
                    final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unable-to-serialize-node", null);
                    if (ser.fDOMErrorHandler != null) {
                        final DOMErrorImpl error = new DOMErrorImpl();
                        error.fType = "unable-to-serialize-node";
                        error.fMessage = msg;
                        error.fSeverity = 3;
                        ser.fDOMErrorHandler.handleError(error);
                    }
                    throw new LSException((short)82, msg);
                }
                ser.serialize(wnode);
            }
        }
        catch (final LSException lse) {
            throw lse;
        }
        catch (final AbortException e) {
            return null;
        }
        catch (final RuntimeException e2) {
            throw (LSException)new LSException((short)82, e2.toString()).initCause(e2);
        }
        catch (final IOException ioe) {
            final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "STRING_TOO_LONG", new Object[] { ioe.getMessage() });
            throw (DOMException)new DOMException((short)2, msg2).initCause(ioe);
        }
        return destination.toString();
    }
    
    @Override
    public void setNewLine(final String newLine) {
        this.serializer._format.setLineSeparator(newLine);
    }
    
    @Override
    public String getNewLine() {
        return this.serializer._format.getLineSeparator();
    }
    
    @Override
    public LSSerializerFilter getFilter() {
        return this.serializer.fDOMFilter;
    }
    
    @Override
    public void setFilter(final LSSerializerFilter filter) {
        this.serializer.fDOMFilter = filter;
    }
    
    private void initSerializer(final XMLSerializer ser) {
        ser.fNSBinder = new NamespaceSupport();
        ser.fLocalNSBinder = new NamespaceSupport();
        ser.fSymbolTable = new SymbolTable();
    }
    
    private void copySettings(final XMLSerializer src, final XMLSerializer dest) {
        dest.fDOMErrorHandler = this.fErrorHandler;
        dest._format.setEncoding(src._format.getEncoding());
        dest._format.setLineSeparator(src._format.getLineSeparator());
        dest.fDOMFilter = src.fDOMFilter;
    }
    
    @Override
    public boolean write(final Node node, final LSOutput destination) throws LSException {
        if (node == null) {
            return false;
        }
        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        final Document fDocument = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        try {
            getVersion = fDocument.getClass().getMethod("getXmlVersion", (Class<?>[])new Class[0]);
            if (getVersion != null) {
                ver = (String)getVersion.invoke(fDocument, (Object[])null);
            }
        }
        catch (final Exception ex) {}
        if (ver != null && ver.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            ser = this.xml11Serializer;
        }
        else {
            ser = this.serializer;
        }
        String encoding = null;
        if ((encoding = destination.getEncoding()) == null) {
            try {
                final Method getEncoding = fDocument.getClass().getMethod("getInputEncoding", (Class<?>[])new Class[0]);
                if (getEncoding != null) {
                    encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
                }
            }
            catch (final Exception ex2) {}
            if (encoding == null) {
                try {
                    final Method getEncoding = fDocument.getClass().getMethod("getXmlEncoding", (Class<?>[])new Class[0]);
                    if (getEncoding != null) {
                        encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
                    }
                }
                catch (final Exception ex3) {}
                if (encoding == null) {
                    encoding = "UTF-8";
                }
            }
        }
        try {
            this.prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);
            final OutputStream outputStream = destination.getByteStream();
            final Writer writer = destination.getCharacterStream();
            final String uri = destination.getSystemId();
            if (writer == null) {
                if (outputStream == null) {
                    if (uri == null) {
                        final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "no-output-specified", null);
                        if (ser.fDOMErrorHandler != null) {
                            final DOMErrorImpl error = new DOMErrorImpl();
                            error.fType = "no-output-specified";
                            error.fMessage = msg;
                            error.fSeverity = 3;
                            ser.fDOMErrorHandler.handleError(error);
                        }
                        throw new LSException((short)82, msg);
                    }
                    final String expanded = XMLEntityManager.expandSystemId(uri, null, true);
                    final URL url = new URL((expanded != null) ? expanded : uri);
                    OutputStream out = null;
                    final String protocol = url.getProtocol();
                    final String host = url.getHost();
                    if (protocol.equals("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
                        out = new FileOutputStream(this.getPathWithoutEscapes(url.getFile()));
                    }
                    else {
                        final URLConnection urlCon = url.openConnection();
                        urlCon.setDoInput(false);
                        urlCon.setDoOutput(true);
                        urlCon.setUseCaches(false);
                        if (urlCon instanceof HttpURLConnection) {
                            final HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                            httpCon.setRequestMethod("PUT");
                        }
                        out = urlCon.getOutputStream();
                    }
                    ser.setOutputByteStream(out);
                }
                else {
                    ser.setOutputByteStream(outputStream);
                }
            }
            else {
                ser.setOutputCharStream(writer);
            }
            if (node.getNodeType() == 9) {
                ser.serialize((Document)node);
            }
            else if (node.getNodeType() == 11) {
                ser.serialize((DocumentFragment)node);
            }
            else if (node.getNodeType() == 1) {
                ser.serialize((Element)node);
            }
            else {
                if (node.getNodeType() != 3 && node.getNodeType() != 8 && node.getNodeType() != 5 && node.getNodeType() != 4 && node.getNodeType() != 7) {
                    return false;
                }
                ser.serialize(node);
            }
        }
        catch (final UnsupportedEncodingException ue) {
            if (ser.fDOMErrorHandler != null) {
                final DOMErrorImpl error2 = new DOMErrorImpl();
                error2.fException = ue;
                error2.fType = "unsupported-encoding";
                error2.fMessage = ue.getMessage();
                error2.fSeverity = 3;
                ser.fDOMErrorHandler.handleError(error2);
            }
            throw new LSException((short)82, DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "unsupported-encoding", null));
        }
        catch (final LSException lse) {
            throw lse;
        }
        catch (final AbortException e) {
            return false;
        }
        catch (final RuntimeException e2) {
            throw (LSException)DOMUtil.createLSException((short)82, e2).fillInStackTrace();
        }
        catch (final Exception e3) {
            if (ser.fDOMErrorHandler != null) {
                final DOMErrorImpl error2 = new DOMErrorImpl();
                error2.fException = e3;
                error2.fMessage = e3.getMessage();
                error2.fSeverity = 2;
                ser.fDOMErrorHandler.handleError(error2);
            }
            throw (LSException)DOMUtil.createLSException((short)82, e3).fillInStackTrace();
        }
        return true;
    }
    
    @Override
    public boolean writeToURI(final Node node, final String URI) throws LSException {
        if (node == null) {
            return false;
        }
        Method getXmlVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        String encoding = null;
        final Document fDocument = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        try {
            getXmlVersion = fDocument.getClass().getMethod("getXmlVersion", (Class<?>[])new Class[0]);
            if (getXmlVersion != null) {
                ver = (String)getXmlVersion.invoke(fDocument, (Object[])null);
            }
        }
        catch (final Exception ex) {}
        if (ver != null && ver.equals("1.1")) {
            if (this.xml11Serializer == null) {
                this.initSerializer(this.xml11Serializer = new XML11Serializer());
            }
            this.copySettings(this.serializer, this.xml11Serializer);
            ser = this.xml11Serializer;
        }
        else {
            ser = this.serializer;
        }
        try {
            final Method getEncoding = fDocument.getClass().getMethod("getInputEncoding", (Class<?>[])new Class[0]);
            if (getEncoding != null) {
                encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
            }
        }
        catch (final Exception ex2) {}
        if (encoding == null) {
            try {
                final Method getEncoding = fDocument.getClass().getMethod("getXmlEncoding", (Class<?>[])new Class[0]);
                if (getEncoding != null) {
                    encoding = (String)getEncoding.invoke(fDocument, (Object[])null);
                }
            }
            catch (final Exception ex3) {}
            if (encoding == null) {
                encoding = "UTF-8";
            }
        }
        try {
            this.prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);
            final String expanded = XMLEntityManager.expandSystemId(URI, null, true);
            final URL url = new URL((expanded != null) ? expanded : URI);
            OutputStream out = null;
            final String protocol = url.getProtocol();
            final String host = url.getHost();
            if (protocol.equals("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
                out = new FileOutputStream(this.getPathWithoutEscapes(url.getFile()));
            }
            else {
                final URLConnection urlCon = url.openConnection();
                urlCon.setDoInput(false);
                urlCon.setDoOutput(true);
                urlCon.setUseCaches(false);
                if (urlCon instanceof HttpURLConnection) {
                    final HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                    httpCon.setRequestMethod("PUT");
                }
                out = urlCon.getOutputStream();
            }
            ser.setOutputByteStream(out);
            if (node.getNodeType() == 9) {
                ser.serialize((Document)node);
            }
            else if (node.getNodeType() == 11) {
                ser.serialize((DocumentFragment)node);
            }
            else if (node.getNodeType() == 1) {
                ser.serialize((Element)node);
            }
            else {
                if (node.getNodeType() != 3 && node.getNodeType() != 8 && node.getNodeType() != 5 && node.getNodeType() != 4 && node.getNodeType() != 7) {
                    return false;
                }
                ser.serialize(node);
            }
        }
        catch (final LSException lse) {
            throw lse;
        }
        catch (final AbortException e) {
            return false;
        }
        catch (final RuntimeException e2) {
            throw (LSException)DOMUtil.createLSException((short)82, e2).fillInStackTrace();
        }
        catch (final Exception e3) {
            if (ser.fDOMErrorHandler != null) {
                final DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e3;
                error.fMessage = e3.getMessage();
                error.fSeverity = 2;
                ser.fDOMErrorHandler.handleError(error);
            }
            throw (LSException)DOMUtil.createLSException((short)82, e3).fillInStackTrace();
        }
        return true;
    }
    
    private void prepareForSerialization(final XMLSerializer ser, Node node) {
        ser.reset();
        ser.features = this.features;
        ser.fDOMErrorHandler = this.fErrorHandler;
        ser.fNamespaces = ((this.features & 0x1) != 0x0);
        ser.fNamespacePrefixes = ((this.features & 0x200) != 0x0);
        ser._format.setOmitComments((this.features & 0x20) == 0x0);
        ser._format.setOmitXMLDeclaration((this.features & 0x100) == 0x0);
        ser._format.setIndenting((this.features & 0x800) != 0x0);
        if ((this.features & 0x2) != 0x0) {
            final Node root = node;
            boolean verifyNames = true;
            final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
            try {
                final Method versionChanged = document.getClass().getMethod("isXMLVersionChanged()", (Class<?>[])new Class[0]);
                if (versionChanged != null) {
                    verifyNames = (boolean)versionChanged.invoke(document, (Object[])null);
                }
            }
            catch (final Exception ex) {}
            if (node.getFirstChild() != null) {
                while (node != null) {
                    this.verify(node, verifyNames, false);
                    Node next;
                    for (next = node.getFirstChild(); next == null; next = node.getNextSibling()) {
                        next = node.getNextSibling();
                        if (next == null) {
                            node = node.getParentNode();
                            if (root == node) {
                                next = null;
                                break;
                            }
                        }
                    }
                    node = next;
                }
            }
            else {
                this.verify(node, verifyNames, false);
            }
        }
    }
    
    private void verify(final Node node, final boolean verifyNames, final boolean xml11Version) {
        final int type = node.getNodeType();
        this.fLocator.fRelatedNode = node;
        switch (type) {
            case 9: {}
            case 1: {
                if (verifyNames) {
                    boolean wellformed;
                    if ((this.features & 0x1) != 0x0) {
                        wellformed = CoreDocumentImpl.isValidQName(node.getPrefix(), node.getLocalName(), xml11Version);
                    }
                    else {
                        wellformed = CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version);
                    }
                    if (!wellformed && !wellformed && this.fErrorHandler != null) {
                        final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
                        DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg, (short)3, "wf-invalid-character-in-node-name");
                    }
                }
                final NamedNodeMap attributes = node.hasAttributes() ? node.getAttributes() : null;
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        this.fLocator.fRelatedNode = attr;
                        DOMNormalizer.isAttrValueWF(this.fErrorHandler, this.fError, this.fLocator, attributes, attr, attr.getValue(), xml11Version);
                        if (verifyNames) {
                            final boolean wellformed = CoreDocumentImpl.isXMLName(attr.getNodeName(), xml11Version);
                            if (!wellformed) {
                                final String msg2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Attr", node.getNodeName() });
                                DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg2, (short)3, "wf-invalid-character-in-node-name");
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 8: {
                if ((this.features & 0x20) != 0x0) {
                    DOMNormalizer.isCommentWF(this.fErrorHandler, this.fError, this.fLocator, ((Comment)node).getData(), xml11Version);
                    break;
                }
                break;
            }
            case 5: {
                if (verifyNames && (this.features & 0x4) != 0x0) {
                    CoreDocumentImpl.isXMLName(node.getNodeName(), xml11Version);
                    break;
                }
                break;
            }
            case 4: {
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
                break;
            }
            case 3: {
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, node.getNodeValue(), xml11Version);
                break;
            }
            case 7: {
                final ProcessingInstruction pinode = (ProcessingInstruction)node;
                final String target = pinode.getTarget();
                if (verifyNames) {
                    boolean wellformed;
                    if (xml11Version) {
                        wellformed = XML11Char.isXML11ValidName(target);
                    }
                    else {
                        wellformed = XMLChar.isValidName(target);
                    }
                    if (!wellformed) {
                        final String msg3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "wf-invalid-character-in-node-name", new Object[] { "Element", node.getNodeName() });
                        DOMNormalizer.reportDOMError(this.fErrorHandler, this.fError, this.fLocator, msg3, (short)3, "wf-invalid-character-in-node-name");
                    }
                }
                DOMNormalizer.isXMLCharWF(this.fErrorHandler, this.fError, this.fLocator, pinode.getData(), xml11Version);
                break;
            }
        }
    }
    
    private String getPathWithoutEscapes(final String origPath) {
        if (origPath != null && origPath.length() != 0 && origPath.indexOf(37) != -1) {
            final StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
            final StringBuffer result = new StringBuffer(origPath.length());
            final int size = tokenizer.countTokens();
            result.append(tokenizer.nextToken());
            for (int i = 1; i < size; ++i) {
                final String token = tokenizer.nextToken();
                result.append((char)(int)Integer.valueOf(token.substring(0, 2), 16));
                result.append(token.substring(2));
            }
            return result.toString();
        }
        return origPath;
    }
}
