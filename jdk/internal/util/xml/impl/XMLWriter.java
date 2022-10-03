package jdk.internal.util.xml.impl;

import java.io.OutputStreamWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import jdk.internal.util.xml.XMLStreamException;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.nio.charset.CharsetEncoder;
import java.io.Writer;

public class XMLWriter
{
    private Writer _writer;
    private CharsetEncoder _encoder;
    
    public XMLWriter(final OutputStream outputStream, final String s, final Charset charset) throws XMLStreamException {
        this._encoder = null;
        this._encoder = charset.newEncoder();
        try {
            this._writer = this.getWriter(outputStream, s, charset);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    public boolean canEncode(final char c) {
        return this._encoder != null && this._encoder.canEncode(c);
    }
    
    public void write(final String s) throws XMLStreamException {
        try {
            this._writer.write(s.toCharArray());
        }
        catch (final IOException ex) {
            throw new XMLStreamException("I/O error", ex);
        }
    }
    
    public void write(final String s, final int n, final int n2) throws XMLStreamException {
        try {
            this._writer.write(s, n, n2);
        }
        catch (final IOException ex) {
            throw new XMLStreamException("I/O error", ex);
        }
    }
    
    public void write(final char[] array, final int n, final int n2) throws XMLStreamException {
        try {
            this._writer.write(array, n, n2);
        }
        catch (final IOException ex) {
            throw new XMLStreamException("I/O error", ex);
        }
    }
    
    void write(final int n) throws XMLStreamException {
        try {
            this._writer.write(n);
        }
        catch (final IOException ex) {
            throw new XMLStreamException("I/O error", ex);
        }
    }
    
    void flush() throws XMLStreamException {
        try {
            this._writer.flush();
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    void close() throws XMLStreamException {
        try {
            this._writer.close();
        }
        catch (final IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    private void nl() throws XMLStreamException {
        final String property = System.getProperty("line.separator");
        try {
            this._writer.write(property);
        }
        catch (final IOException ex) {
            throw new XMLStreamException("I/O error", ex);
        }
    }
    
    private Writer getWriter(final OutputStream outputStream, final String s, final Charset charset) throws XMLStreamException, UnsupportedEncodingException {
        if (charset != null) {
            return new OutputStreamWriter(new BufferedOutputStream(outputStream), charset);
        }
        return new OutputStreamWriter(new BufferedOutputStream(outputStream), s);
    }
}
