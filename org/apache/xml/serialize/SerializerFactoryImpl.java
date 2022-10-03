package org.apache.xml.serialize;

import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xerces.dom.DOMMessageFormatter;

final class SerializerFactoryImpl extends SerializerFactory
{
    private String _method;
    
    SerializerFactoryImpl(final String method) {
        this._method = method;
        if (!this._method.equals("xml") && !this._method.equals("html") && !this._method.equals("xhtml") && !this._method.equals("text")) {
            throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { method }));
        }
    }
    
    public Serializer makeSerializer(final OutputFormat outputFormat) {
        final Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputFormat(outputFormat);
        return serializer;
    }
    
    public Serializer makeSerializer(final Writer outputCharStream, final OutputFormat outputFormat) {
        final Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputCharStream(outputCharStream);
        return serializer;
    }
    
    public Serializer makeSerializer(final OutputStream outputByteStream, final OutputFormat outputFormat) throws UnsupportedEncodingException {
        final Serializer serializer = this.getSerializer(outputFormat);
        serializer.setOutputByteStream(outputByteStream);
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
        throw new IllegalStateException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "MethodNotSupported", new Object[] { this._method }));
    }
    
    protected String getSupportedMethod() {
        return this._method;
    }
}
