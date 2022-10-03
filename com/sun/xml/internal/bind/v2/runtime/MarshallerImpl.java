package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.PropertyException;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.C14nXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.IndentingUTF8XmlOutput;
import com.sun.xml.internal.bind.marshaller.XMLWriter;
import com.sun.xml.internal.bind.marshaller.DataWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import com.sun.xml.internal.bind.marshaller.DumbEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NioEscapeHandler;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import javax.xml.validation.ValidatorHandler;
import com.sun.xml.internal.bind.v2.runtime.output.ForkXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ErrorHandler;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import org.w3c.dom.Node;
import java.io.IOException;
import javax.xml.bind.MarshalException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Result;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLEventWriterOutput;
import javax.xml.stream.XMLEventWriter;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import java.io.Closeable;
import java.io.Flushable;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.AbstractMarshallerImpl;

public final class MarshallerImpl extends AbstractMarshallerImpl implements ValidationEventHandler
{
    private String indent;
    private NamespacePrefixMapper prefixMapper;
    private CharacterEscapeHandler escapeHandler;
    private String header;
    final JAXBContextImpl context;
    protected final XMLSerializer serializer;
    private Schema schema;
    private Marshaller.Listener externalListener;
    private boolean c14nSupport;
    private Flushable toBeFlushed;
    private Closeable toBeClosed;
    protected static final String INDENT_STRING = "com.sun.xml.internal.bind.indentString";
    protected static final String PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";
    protected static final String ENCODING_HANDLER = "com.sun.xml.internal.bind.characterEscapeHandler";
    protected static final String ENCODING_HANDLER2 = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";
    protected static final String XMLDECLARATION = "com.sun.xml.internal.bind.xmlDeclaration";
    protected static final String XML_HEADERS = "com.sun.xml.internal.bind.xmlHeaders";
    protected static final String C14N = "com.sun.xml.internal.bind.c14n";
    protected static final String OBJECT_IDENTITY_CYCLE_DETECTION = "com.sun.xml.internal.bind.objectIdentitityCycleDetection";
    
    public MarshallerImpl(final JAXBContextImpl c, final AssociationMap assoc) {
        this.indent = "    ";
        this.prefixMapper = null;
        this.escapeHandler = null;
        this.header = null;
        this.externalListener = null;
        this.context = c;
        this.serializer = new XMLSerializer(this);
        this.c14nSupport = this.context.c14nSupport;
        try {
            this.setEventHandler(this);
        }
        catch (final JAXBException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public JAXBContextImpl getContext() {
        return this.context;
    }
    
    public void marshal(final Object obj, final OutputStream out, final NamespaceContext inscopeNamespace) throws JAXBException {
        this.write(obj, this.createWriter(out), new StAXPostInitAction(inscopeNamespace, this.serializer));
    }
    
    @Override
    public void marshal(final Object obj, final XMLStreamWriter writer) throws JAXBException {
        this.write(obj, XMLStreamWriterOutput.create(writer, this.context, this.escapeHandler), new StAXPostInitAction(writer, this.serializer));
    }
    
    @Override
    public void marshal(final Object obj, final XMLEventWriter writer) throws JAXBException {
        this.write(obj, new XMLEventWriterOutput(writer), new StAXPostInitAction(writer, this.serializer));
    }
    
    public void marshal(final Object obj, final XmlOutput output) throws JAXBException {
        this.write(obj, output, null);
    }
    
    final XmlOutput createXmlOutput(final Result result) throws JAXBException {
        if (result instanceof SAXResult) {
            return new SAXOutput(((SAXResult)result).getHandler());
        }
        if (!(result instanceof DOMResult)) {
            if (result instanceof StreamResult) {
                final StreamResult sr = (StreamResult)result;
                if (sr.getWriter() != null) {
                    return this.createWriter(sr.getWriter());
                }
                if (sr.getOutputStream() != null) {
                    return this.createWriter(sr.getOutputStream());
                }
                if (sr.getSystemId() != null) {
                    String fileURL = sr.getSystemId();
                    try {
                        fileURL = new URI(fileURL).getPath();
                    }
                    catch (final URISyntaxException ex) {}
                    try {
                        final FileOutputStream fos = new FileOutputStream(fileURL);
                        assert this.toBeClosed == null;
                        this.toBeClosed = fos;
                        return this.createWriter(fos);
                    }
                    catch (final IOException e) {
                        throw new MarshalException(e);
                    }
                }
            }
            throw new MarshalException(Messages.UNSUPPORTED_RESULT.format(new Object[0]));
        }
        final Node node = ((DOMResult)result).getNode();
        if (node == null) {
            final Document doc = JAXBContextImpl.createDom(this.getContext().disableSecurityProcessing);
            ((DOMResult)result).setNode(doc);
            return new SAXOutput(new SAX2DOMEx(doc));
        }
        return new SAXOutput(new SAX2DOMEx(node));
    }
    
    final Runnable createPostInitAction(final Result result) {
        if (result instanceof DOMResult) {
            final Node node = ((DOMResult)result).getNode();
            return new DomPostInitAction(node, this.serializer);
        }
        return null;
    }
    
    @Override
    public void marshal(final Object target, final Result result) throws JAXBException {
        this.write(target, this.createXmlOutput(result), this.createPostInitAction(result));
    }
    
    protected final <T> void write(final Name rootTagName, final JaxBeanInfo<T> bi, final T obj, final XmlOutput out, final Runnable postInitAction) throws JAXBException {
        try {
            try {
                this.prewrite(out, true, postInitAction);
                this.serializer.startElement(rootTagName, null);
                if (bi.jaxbType == Void.class || bi.jaxbType == Void.TYPE) {
                    this.serializer.endNamespaceDecls(null);
                    this.serializer.endAttributes();
                }
                else if (obj == null) {
                    this.serializer.writeXsiNilTrue();
                }
                else {
                    this.serializer.childAsXsiType(obj, "root", bi, false);
                }
                this.serializer.endElement();
                this.postwrite();
            }
            catch (final SAXException e) {
                throw new MarshalException(e);
            }
            catch (final IOException e2) {
                throw new MarshalException(e2);
            }
            catch (final XMLStreamException e3) {
                throw new MarshalException(e3);
            }
            finally {
                this.serializer.close();
            }
        }
        finally {
            this.cleanUp();
        }
    }
    
    private void write(final Object obj, XmlOutput out, final Runnable postInitAction) throws JAXBException {
        try {
            if (obj == null) {
                throw new IllegalArgumentException(Messages.NOT_MARSHALLABLE.format(new Object[0]));
            }
            Label_0090: {
                if (this.schema == null) {
                    break Label_0090;
                }
                final ValidatorHandler validator = this.schema.newValidatorHandler();
                validator.setErrorHandler(new FatalAdapter(this.serializer));
                final XMLFilterImpl f = new XMLFilterImpl() {
                    @Override
                    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
                        super.startPrefixMapping(prefix.intern(), uri.intern());
                    }
                };
                f.setContentHandler(validator);
                out = new ForkXmlOutput(new SAXOutput(f) {
                    @Override
                    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws SAXException, IOException, XMLStreamException {
                        super.startDocument(serializer, false, nsUriIndex2prefixIndex, nsContext);
                    }
                    
                    @Override
                    public void endDocument(final boolean fragment) throws SAXException, IOException, XMLStreamException {
                        super.endDocument(false);
                    }
                }, out);
                try {
                    this.prewrite(out, this.isFragment(), postInitAction);
                    this.serializer.childAsRoot(obj);
                    this.postwrite();
                }
                catch (final SAXException e) {
                    throw new MarshalException(e);
                }
                catch (final IOException e2) {
                    throw new MarshalException(e2);
                }
                catch (final XMLStreamException e3) {
                    throw new MarshalException(e3);
                }
                finally {
                    this.serializer.close();
                }
            }
        }
        finally {
            this.cleanUp();
        }
    }
    
    private void cleanUp() {
        if (this.toBeFlushed != null) {
            try {
                this.toBeFlushed.flush();
            }
            catch (final IOException ex) {}
        }
        if (this.toBeClosed != null) {
            try {
                this.toBeClosed.close();
            }
            catch (final IOException ex2) {}
        }
        this.toBeFlushed = null;
        this.toBeClosed = null;
    }
    
    private void prewrite(final XmlOutput out, final boolean fragment, final Runnable postInitAction) throws IOException, SAXException, XMLStreamException {
        this.serializer.startDocument(out, fragment, this.getSchemaLocation(), this.getNoNSSchemaLocation());
        if (postInitAction != null) {
            postInitAction.run();
        }
        if (this.prefixMapper != null) {
            final String[] decls = this.prefixMapper.getContextualNamespaceDecls();
            if (decls != null) {
                for (int i = 0; i < decls.length; i += 2) {
                    final String prefix = decls[i];
                    final String nsUri = decls[i + 1];
                    if (nsUri != null && prefix != null) {
                        this.serializer.addInscopeBinding(nsUri, prefix);
                    }
                }
            }
        }
        this.serializer.setPrefixMapper(this.prefixMapper);
    }
    
    private void postwrite() throws IOException, SAXException, XMLStreamException {
        this.serializer.endDocument();
        this.serializer.reconcileID();
    }
    
    CharacterEscapeHandler getEscapeHandler() {
        return this.escapeHandler;
    }
    
    protected CharacterEscapeHandler createEscapeHandler(final String encoding) {
        if (this.escapeHandler != null) {
            return this.escapeHandler;
        }
        if (encoding.startsWith("UTF")) {
            return MinimumEscapeHandler.theInstance;
        }
        try {
            return new NioEscapeHandler(this.getJavaEncoding(encoding));
        }
        catch (final Throwable e) {
            return DumbEscapeHandler.theInstance;
        }
    }
    
    public XmlOutput createWriter(Writer w, final String encoding) {
        if (!(w instanceof BufferedWriter)) {
            w = new BufferedWriter(w);
        }
        assert this.toBeFlushed == null;
        this.toBeFlushed = w;
        final CharacterEscapeHandler ceh = this.createEscapeHandler(encoding);
        XMLWriter xw;
        if (this.isFormattedOutput()) {
            final DataWriter d = new DataWriter(w, encoding, ceh);
            d.setIndentStep(this.indent);
            xw = d;
        }
        else {
            xw = new XMLWriter(w, encoding, ceh);
        }
        xw.setXmlDecl(!this.isFragment());
        xw.setHeader(this.header);
        return new SAXOutput(xw);
    }
    
    public XmlOutput createWriter(final Writer w) {
        return this.createWriter(w, this.getEncoding());
    }
    
    public XmlOutput createWriter(final OutputStream os) throws JAXBException {
        return this.createWriter(os, this.getEncoding());
    }
    
    public XmlOutput createWriter(final OutputStream os, final String encoding) throws JAXBException {
        if (encoding.equals("UTF-8")) {
            final Encoded[] table = this.context.getUTF8NameTable();
            UTF8XmlOutput out;
            if (this.isFormattedOutput()) {
                out = new IndentingUTF8XmlOutput(os, this.indent, table, this.escapeHandler);
            }
            else if (this.c14nSupport) {
                out = new C14nXmlOutput(os, table, this.context.c14nSupport, this.escapeHandler);
            }
            else {
                out = new UTF8XmlOutput(os, table, this.escapeHandler);
            }
            if (this.header != null) {
                out.setHeader(this.header);
            }
            return out;
        }
        try {
            return this.createWriter(new OutputStreamWriter(os, this.getJavaEncoding(encoding)), encoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new MarshalException(Messages.UNSUPPORTED_ENCODING.format(encoding), e);
        }
    }
    
    @Override
    public Object getProperty(final String name) throws PropertyException {
        if ("com.sun.xml.internal.bind.indentString".equals(name)) {
            return this.indent;
        }
        if ("com.sun.xml.internal.bind.characterEscapeHandler".equals(name) || "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(name)) {
            return this.escapeHandler;
        }
        if ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(name)) {
            return this.prefixMapper;
        }
        if ("com.sun.xml.internal.bind.xmlDeclaration".equals(name)) {
            return !this.isFragment();
        }
        if ("com.sun.xml.internal.bind.xmlHeaders".equals(name)) {
            return this.header;
        }
        if ("com.sun.xml.internal.bind.c14n".equals(name)) {
            return this.c14nSupport;
        }
        if ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(name)) {
            return this.serializer.getObjectIdentityCycleDetection();
        }
        return super.getProperty(name);
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws PropertyException {
        if ("com.sun.xml.internal.bind.indentString".equals(name)) {
            this.checkString(name, value);
            this.indent = (String)value;
            return;
        }
        if ("com.sun.xml.internal.bind.characterEscapeHandler".equals(name) || "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(name)) {
            if (!(value instanceof CharacterEscapeHandler)) {
                throw new PropertyException(Messages.MUST_BE_X.format(name, CharacterEscapeHandler.class.getName(), value.getClass().getName()));
            }
            this.escapeHandler = (CharacterEscapeHandler)value;
        }
        else if ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(name)) {
            if (!(value instanceof NamespacePrefixMapper)) {
                throw new PropertyException(Messages.MUST_BE_X.format(name, NamespacePrefixMapper.class.getName(), value.getClass().getName()));
            }
            this.prefixMapper = (NamespacePrefixMapper)value;
        }
        else {
            if ("com.sun.xml.internal.bind.xmlDeclaration".equals(name)) {
                this.checkBoolean(name, value);
                super.setProperty("jaxb.fragment", !(boolean)value);
                return;
            }
            if ("com.sun.xml.internal.bind.xmlHeaders".equals(name)) {
                this.checkString(name, value);
                this.header = (String)value;
                return;
            }
            if ("com.sun.xml.internal.bind.c14n".equals(name)) {
                this.checkBoolean(name, value);
                this.c14nSupport = (boolean)value;
                return;
            }
            if ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(name)) {
                this.checkBoolean(name, value);
                this.serializer.setObjectIdentityCycleDetection((boolean)value);
                return;
            }
            super.setProperty(name, value);
        }
    }
    
    private void checkBoolean(final String name, final Object value) throws PropertyException {
        if (!(value instanceof Boolean)) {
            throw new PropertyException(Messages.MUST_BE_X.format(name, Boolean.class.getName(), value.getClass().getName()));
        }
    }
    
    private void checkString(final String name, final Object value) throws PropertyException {
        if (!(value instanceof String)) {
            throw new PropertyException(Messages.MUST_BE_X.format(name, String.class.getName(), value.getClass().getName()));
        }
    }
    
    @Override
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.serializer.putAdapter(type, adapter);
    }
    
    @Override
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (this.serializer.containsAdapter(type)) {
            return this.serializer.getAdapter(type);
        }
        return null;
    }
    
    @Override
    public void setAttachmentMarshaller(final AttachmentMarshaller am) {
        this.serializer.attachmentMarshaller = am;
    }
    
    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return this.serializer.attachmentMarshaller;
    }
    
    @Override
    public Schema getSchema() {
        return this.schema;
    }
    
    @Override
    public void setSchema(final Schema s) {
        this.schema = s;
    }
    
    @Override
    public boolean handleEvent(final ValidationEvent event) {
        return false;
    }
    
    @Override
    public Marshaller.Listener getListener() {
        return this.externalListener;
    }
    
    @Override
    public void setListener(final Marshaller.Listener listener) {
        this.externalListener = listener;
    }
}
