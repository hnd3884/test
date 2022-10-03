package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import java.io.Writer;
import java.io.StringWriter;
import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import java.io.OutputStream;

public class UTF8XmlOutput extends XmlOutputAbstractImpl
{
    protected final OutputStream out;
    private Encoded[] prefixes;
    private int prefixCount;
    private final Encoded[] localNames;
    private final Encoded textBuffer;
    protected final byte[] octetBuffer;
    protected int octetBufferIndex;
    protected boolean closeStartTagPending;
    private String header;
    private CharacterEscapeHandler escapeHandler;
    private final byte[] XMLNS_EQUALS;
    private final byte[] XMLNS_COLON;
    private final byte[] EQUALS;
    private final byte[] CLOSE_TAG;
    private final byte[] EMPTY_TAG;
    private final byte[] XML_DECL;
    private static final byte[] _XMLNS_EQUALS;
    private static final byte[] _XMLNS_COLON;
    private static final byte[] _EQUALS;
    private static final byte[] _CLOSE_TAG;
    private static final byte[] _EMPTY_TAG;
    private static final byte[] _XML_DECL;
    private static final byte[] EMPTY_BYTE_ARRAY;
    
    public UTF8XmlOutput(final OutputStream out, final Encoded[] localNames, final CharacterEscapeHandler escapeHandler) {
        this.prefixes = new Encoded[8];
        this.textBuffer = new Encoded();
        this.octetBuffer = new byte[1024];
        this.closeStartTagPending = false;
        this.escapeHandler = null;
        this.XMLNS_EQUALS = UTF8XmlOutput._XMLNS_EQUALS.clone();
        this.XMLNS_COLON = UTF8XmlOutput._XMLNS_COLON.clone();
        this.EQUALS = UTF8XmlOutput._EQUALS.clone();
        this.CLOSE_TAG = UTF8XmlOutput._CLOSE_TAG.clone();
        this.EMPTY_TAG = UTF8XmlOutput._EMPTY_TAG.clone();
        this.XML_DECL = UTF8XmlOutput._XML_DECL.clone();
        this.out = out;
        this.localNames = localNames;
        for (int i = 0; i < this.prefixes.length; ++i) {
            this.prefixes[i] = new Encoded();
        }
        this.escapeHandler = escapeHandler;
    }
    
    public void setHeader(final String header) {
        this.header = header;
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        this.octetBufferIndex = 0;
        if (!fragment) {
            this.write(this.XML_DECL);
        }
        if (this.header != null) {
            this.textBuffer.set(this.header);
            this.textBuffer.write(this);
        }
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.flushBuffer();
        super.endDocument(fragment);
    }
    
    protected final void closeStartTag() throws IOException {
        if (this.closeStartTagPending) {
            this.write(62);
            this.closeStartTagPending = false;
        }
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException {
        this.closeStartTag();
        final int base = this.pushNsDecls();
        this.write(60);
        this.writeName(prefix, localName);
        this.writeNsDecls(base);
    }
    
    @Override
    public void beginStartTag(final Name name) throws IOException {
        this.closeStartTag();
        final int base = this.pushNsDecls();
        this.write(60);
        this.writeName(name);
        this.writeNsDecls(base);
    }
    
    private int pushNsDecls() {
        final int total = this.nsContext.count();
        final NamespaceContextImpl.Element ns = this.nsContext.getCurrent();
        if (total > this.prefixes.length) {
            final int m = Math.max(total, this.prefixes.length * 2);
            final Encoded[] buf = new Encoded[m];
            System.arraycopy(this.prefixes, 0, buf, 0, this.prefixes.length);
            for (int i = this.prefixes.length; i < buf.length; ++i) {
                buf[i] = new Encoded();
            }
            this.prefixes = buf;
        }
        final int base = Math.min(this.prefixCount, ns.getBase());
        final int size = this.nsContext.count();
        for (int i = base; i < size; ++i) {
            final String p = this.nsContext.getPrefix(i);
            final Encoded e = this.prefixes[i];
            if (p.length() == 0) {
                e.buf = UTF8XmlOutput.EMPTY_BYTE_ARRAY;
                e.len = 0;
            }
            else {
                e.set(p);
                e.append(':');
            }
        }
        this.prefixCount = size;
        return base;
    }
    
    protected void writeNsDecls(final int base) throws IOException {
        final NamespaceContextImpl.Element ns = this.nsContext.getCurrent();
        for (int size = this.nsContext.count(), i = ns.getBase(); i < size; ++i) {
            this.writeNsDecl(i);
        }
    }
    
    protected final void writeNsDecl(final int prefixIndex) throws IOException {
        final String p = this.nsContext.getPrefix(prefixIndex);
        if (p.length() == 0) {
            if (this.nsContext.getCurrent().isRootElement() && this.nsContext.getNamespaceURI(prefixIndex).length() == 0) {
                return;
            }
            this.write(this.XMLNS_EQUALS);
        }
        else {
            final Encoded e = this.prefixes[prefixIndex];
            this.write(this.XMLNS_COLON);
            this.write(e.buf, 0, e.len - 1);
            this.write(this.EQUALS);
        }
        this.doText(this.nsContext.getNamespaceURI(prefixIndex), true);
        this.write(34);
    }
    
    private void writePrefix(final int prefix) throws IOException {
        this.prefixes[prefix].write(this);
    }
    
    private void writeName(final Name name) throws IOException {
        this.writePrefix(this.nsUriIndex2prefixIndex[name.nsUriIndex]);
        this.localNames[name.localNameIndex].write(this);
    }
    
    private void writeName(final int prefix, final String localName) throws IOException {
        this.writePrefix(prefix);
        this.textBuffer.set(localName);
        this.textBuffer.write(this);
    }
    
    @Override
    public void attribute(final Name name, final String value) throws IOException {
        this.write(32);
        if (name.nsUriIndex == -1) {
            this.localNames[name.localNameIndex].write(this);
        }
        else {
            this.writeName(name);
        }
        this.write(this.EQUALS);
        this.doText(value, true);
        this.write(34);
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException {
        this.write(32);
        if (prefix == -1) {
            this.textBuffer.set(localName);
            this.textBuffer.write(this);
        }
        else {
            this.writeName(prefix, localName);
        }
        this.write(this.EQUALS);
        this.doText(value, true);
        this.write(34);
    }
    
    @Override
    public void endStartTag() throws IOException {
        this.closeStartTagPending = true;
    }
    
    @Override
    public void endTag(final Name name) throws IOException {
        if (this.closeStartTagPending) {
            this.write(this.EMPTY_TAG);
            this.closeStartTagPending = false;
        }
        else {
            this.write(this.CLOSE_TAG);
            this.writeName(name);
            this.write(62);
        }
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException {
        if (this.closeStartTagPending) {
            this.write(this.EMPTY_TAG);
            this.closeStartTagPending = false;
        }
        else {
            this.write(this.CLOSE_TAG);
            this.writeName(prefix, localName);
            this.write(62);
        }
    }
    
    @Override
    public void text(final String value, final boolean needSP) throws IOException {
        this.closeStartTag();
        if (needSP) {
            this.write(32);
        }
        this.doText(value, false);
    }
    
    @Override
    public void text(final Pcdata value, final boolean needSP) throws IOException {
        this.closeStartTag();
        if (needSP) {
            this.write(32);
        }
        value.writeTo(this);
    }
    
    private void doText(final String value, final boolean isAttribute) throws IOException {
        if (this.escapeHandler != null) {
            final StringWriter sw = new StringWriter();
            this.escapeHandler.escape(value.toCharArray(), 0, value.length(), isAttribute, sw);
            this.textBuffer.set(sw.toString());
        }
        else {
            this.textBuffer.setEscape(value, isAttribute);
        }
        this.textBuffer.write(this);
    }
    
    public final void text(int value) throws IOException {
        this.closeStartTag();
        final boolean minus = value < 0;
        this.textBuffer.ensureSize(11);
        final byte[] buf = this.textBuffer.buf;
        int idx = 11;
        do {
            int r = value % 10;
            if (r < 0) {
                r = -r;
            }
            buf[--idx] = (byte)(0x30 | r);
            value /= 10;
        } while (value != 0);
        if (minus) {
            buf[--idx] = 45;
        }
        this.write(buf, idx, 11 - idx);
    }
    
    public void text(final byte[] data, int dataLen) throws IOException {
        this.closeStartTag();
        int start = 0;
        while (dataLen > 0) {
            final int batchSize = Math.min((this.octetBuffer.length - this.octetBufferIndex) / 4 * 3, dataLen);
            this.octetBufferIndex = DatatypeConverterImpl._printBase64Binary(data, start, batchSize, this.octetBuffer, this.octetBufferIndex);
            if (batchSize < dataLen) {
                this.flushBuffer();
            }
            start += batchSize;
            dataLen -= batchSize;
        }
    }
    
    public final void write(final int i) throws IOException {
        if (this.octetBufferIndex < this.octetBuffer.length) {
            this.octetBuffer[this.octetBufferIndex++] = (byte)i;
        }
        else {
            this.out.write(this.octetBuffer);
            this.octetBufferIndex = 1;
            this.octetBuffer[0] = (byte)i;
        }
    }
    
    protected final void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    protected final void write(final byte[] b, final int start, final int length) throws IOException {
        if (this.octetBufferIndex + length < this.octetBuffer.length) {
            System.arraycopy(b, start, this.octetBuffer, this.octetBufferIndex, length);
            this.octetBufferIndex += length;
        }
        else {
            this.out.write(this.octetBuffer, 0, this.octetBufferIndex);
            this.out.write(b, start, length);
            this.octetBufferIndex = 0;
        }
    }
    
    protected final void flushBuffer() throws IOException {
        this.out.write(this.octetBuffer, 0, this.octetBufferIndex);
        this.octetBufferIndex = 0;
    }
    
    static byte[] toBytes(final String s) {
        final byte[] buf = new byte[s.length()];
        for (int i = s.length() - 1; i >= 0; --i) {
            buf[i] = (byte)s.charAt(i);
        }
        return buf;
    }
    
    static {
        _XMLNS_EQUALS = toBytes(" xmlns=\"");
        _XMLNS_COLON = toBytes(" xmlns:");
        _EQUALS = toBytes("=\"");
        _CLOSE_TAG = toBytes("</");
        _EMPTY_TAG = toBytes("/>");
        _XML_DECL = toBytes("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        EMPTY_BYTE_ARRAY = new byte[0];
    }
}
