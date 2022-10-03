package com.sun.org.apache.xml.internal.serialize;

import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;

final class SerializerFactoryImpl extends SerializerFactory
{
    private String _method;
    
    SerializerFactoryImpl(final String method) {
        this._method = method;
        if (!this._method.equals("xml") && !this._method.equals("html") && !this._method.equals("xhtml") && !this._method.equals("text")) {
            final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { method });
            throw new IllegalArgumentException(msg);
        }
    }
    
    @Override
    public Serializer makeSerializer(final OutputFormat format) {
        final Serializer serializer = this.getSerializer(format);
        serializer.setOutputFormat(format);
        return serializer;
    }
    
    @Override
    public Serializer makeSerializer(final Writer writer, final OutputFormat format) {
        final Serializer serializer = this.getSerializer(format);
        serializer.setOutputCharStream(writer);
        return serializer;
    }
    
    @Override
    public Serializer makeSerializer(final OutputStream output, final OutputFormat format) throws UnsupportedEncodingException {
        final Serializer serializer = this.getSerializer(format);
        serializer.setOutputByteStream(output);
        return serializer;
    }
    
    private Serializer getSerializer(final OutputFormat format) {
        if (this._method.equals("xml")) {
            return new XMLSerializer(format);
        }
        if (this._method.equals("html")) {
            return new HTMLSerializer(format);
        }
        if (this._method.equals("xhtml")) {
            return new XHTMLSerializer(format);
        }
        if (this._method.equals("text")) {
            return new TextSerializer();
        }
        final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { this._method });
        throw new IllegalStateException(msg);
    }
    
    @Override
    protected String getSupportedMethod() {
        return this._method;
    }
}
