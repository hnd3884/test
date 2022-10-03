package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterOutput extends XmlOutputAbstractImpl
{
    private final XMLStreamWriter out;
    private final CharacterEscapeHandler escapeHandler;
    private final XmlStreamOutWriterAdapter writerWrapper;
    protected final char[] buf;
    private static final Class FI_STAX_WRITER_CLASS;
    private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR;
    private static final Class STAXEX_WRITER_CLASS;
    private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR;
    
    public static XmlOutput create(final XMLStreamWriter out, final JAXBContextImpl context, final CharacterEscapeHandler escapeHandler) {
        final Class writerClass = out.getClass();
        if (writerClass == XMLStreamWriterOutput.FI_STAX_WRITER_CLASS) {
            try {
                return (XmlOutput)XMLStreamWriterOutput.FI_OUTPUT_CTOR.newInstance(out, context);
            }
            catch (final Exception ex) {}
        }
        if (XMLStreamWriterOutput.STAXEX_WRITER_CLASS != null && XMLStreamWriterOutput.STAXEX_WRITER_CLASS.isAssignableFrom(writerClass)) {
            try {
                return (XmlOutput)XMLStreamWriterOutput.STAXEX_OUTPUT_CTOR.newInstance(out);
            }
            catch (final Exception ex2) {}
        }
        final CharacterEscapeHandler xmlStreamEscapeHandler = (escapeHandler != null) ? escapeHandler : NoEscapeHandler.theInstance;
        return new XMLStreamWriterOutput(out, xmlStreamEscapeHandler);
    }
    
    protected XMLStreamWriterOutput(final XMLStreamWriter out, final CharacterEscapeHandler escapeHandler) {
        this.buf = new char[256];
        this.out = out;
        this.escapeHandler = escapeHandler;
        this.writerWrapper = new XmlStreamOutWriterAdapter(out);
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        if (!fragment) {
            this.out.writeStartDocument();
        }
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        if (!fragment) {
            this.out.writeEndDocument();
            this.out.flush();
        }
        super.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException, XMLStreamException {
        this.out.writeStartElement(this.nsContext.getPrefix(prefix), localName, this.nsContext.getNamespaceURI(prefix));
        final NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
        if (nse.count() > 0) {
            for (int i = nse.count() - 1; i >= 0; --i) {
                final String uri = nse.getNsUri(i);
                if (uri.length() != 0 || nse.getBase() != 1) {
                    this.out.writeNamespace(nse.getPrefix(i), uri);
                }
            }
        }
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException, XMLStreamException {
        if (prefix == -1) {
            this.out.writeAttribute(localName, value);
        }
        else {
            this.out.writeAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName, value);
        }
    }
    
    @Override
    public void endStartTag() throws IOException, SAXException {
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException, SAXException, XMLStreamException {
        this.out.writeEndElement();
    }
    
    @Override
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        this.escapeHandler.escape(value.toCharArray(), 0, value.length(), false, this.writerWrapper);
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        final int len = value.length();
        if (len < this.buf.length) {
            value.writeTo(this.buf, 0);
            this.out.writeCharacters(this.buf, 0, len);
        }
        else {
            this.out.writeCharacters(value.toString());
        }
    }
    
    private static Class initFIStAXWriterClass() {
        try {
            final Class<?> llfisw = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter");
            final Class<?> sds = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer");
            if (llfisw.isAssignableFrom(sds)) {
                return sds;
            }
            return null;
        }
        catch (final Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends XmlOutput> initFastInfosetOutputClass() {
        try {
            if (XMLStreamWriterOutput.FI_STAX_WRITER_CLASS == null) {
                return null;
            }
            final Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.FastInfosetStreamWriterOutput");
            return c.getConstructor(XMLStreamWriterOutput.FI_STAX_WRITER_CLASS, JAXBContextImpl.class);
        }
        catch (final Throwable e) {
            return null;
        }
    }
    
    private static Class initStAXExWriterClass() {
        try {
            return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx");
        }
        catch (final Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends XmlOutput> initStAXExOutputClass() {
        try {
            final Class c = Class.forName("com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput");
            return c.getConstructor(XMLStreamWriterOutput.STAXEX_WRITER_CLASS);
        }
        catch (final Throwable e) {
            return null;
        }
    }
    
    static {
        FI_STAX_WRITER_CLASS = initFIStAXWriterClass();
        FI_OUTPUT_CTOR = initFastInfosetOutputClass();
        STAXEX_WRITER_CLASS = initStAXExWriterClass();
        STAXEX_OUTPUT_CTOR = initStAXExOutputClass();
    }
    
    private static final class XmlStreamOutWriterAdapter extends Writer
    {
        private final XMLStreamWriter writer;
        
        private XmlStreamOutWriterAdapter(final XMLStreamWriter writer) {
            this.writer = writer;
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            try {
                this.writer.writeCharacters(cbuf, off, len);
            }
            catch (final XMLStreamException e) {
                throw new IOException("Error writing XML stream", e);
            }
        }
        
        public void writeEntityRef(final String entityReference) throws XMLStreamException {
            this.writer.writeEntityRef(entityReference);
        }
        
        @Override
        public void flush() throws IOException {
            try {
                this.writer.flush();
            }
            catch (final XMLStreamException e) {
                throw new IOException("Error flushing XML stream", e);
            }
        }
        
        @Override
        public void close() throws IOException {
            try {
                this.writer.close();
            }
            catch (final XMLStreamException e) {
                throw new IOException("Error closing XML stream", e);
            }
        }
    }
}
