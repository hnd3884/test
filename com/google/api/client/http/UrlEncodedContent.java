package com.google.api.client.http;

import com.google.api.client.util.FieldInfo;
import java.util.HashMap;
import com.google.api.client.util.Preconditions;
import java.io.IOException;
import java.util.Iterator;
import com.google.api.client.util.Types;
import com.google.api.client.util.escape.CharEscapers;
import java.util.Map;
import com.google.api.client.util.Data;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;

public class UrlEncodedContent extends AbstractHttpContent
{
    private Object data;
    private boolean uriPathEncodingFlag;
    
    public UrlEncodedContent(final Object data) {
        super(UrlEncodedParser.MEDIA_TYPE);
        this.setData(data);
        this.uriPathEncodingFlag = false;
    }
    
    public UrlEncodedContent(final Object data, final boolean useUriPathEncoding) {
        super(UrlEncodedParser.MEDIA_TYPE);
        this.setData(data);
        this.uriPathEncodingFlag = useUriPathEncoding;
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        final Writer writer = new BufferedWriter(new OutputStreamWriter(out, this.getCharset()));
        boolean first = true;
        for (final Map.Entry<String, Object> nameValueEntry : Data.mapOf(this.data).entrySet()) {
            final Object value = nameValueEntry.getValue();
            if (value != null) {
                final String name = CharEscapers.escapeUri(nameValueEntry.getKey());
                final Class<?> valueClass = value.getClass();
                if (value instanceof Iterable || valueClass.isArray()) {
                    for (final Object repeatedValue : Types.iterableOf(value)) {
                        first = appendParam(first, writer, name, repeatedValue, this.uriPathEncodingFlag);
                    }
                }
                else {
                    first = appendParam(first, writer, name, value, this.uriPathEncodingFlag);
                }
            }
        }
        writer.flush();
    }
    
    @Override
    public UrlEncodedContent setMediaType(final HttpMediaType mediaType) {
        super.setMediaType(mediaType);
        return this;
    }
    
    public final Object getData() {
        return this.data;
    }
    
    public UrlEncodedContent setData(final Object data) {
        this.data = Preconditions.checkNotNull(data);
        return this;
    }
    
    public static UrlEncodedContent getContent(final HttpRequest request) {
        final HttpContent content = request.getContent();
        if (content != null) {
            return (UrlEncodedContent)content;
        }
        final UrlEncodedContent result = new UrlEncodedContent(new HashMap());
        request.setContent(result);
        return result;
    }
    
    private static boolean appendParam(boolean first, final Writer writer, final String name, final Object value, final boolean uriPathEncodingFlag) throws IOException {
        if (value == null || Data.isNull(value)) {
            return first;
        }
        if (first) {
            first = false;
        }
        else {
            writer.write("&");
        }
        writer.write(name);
        String stringValue = (value instanceof Enum) ? FieldInfo.of((Enum<?>)value).getName() : value.toString();
        if (uriPathEncodingFlag) {
            stringValue = CharEscapers.escapeUriPath(stringValue);
        }
        else {
            stringValue = CharEscapers.escapeUri(stringValue);
        }
        if (stringValue.length() != 0) {
            writer.write("=");
            writer.write(stringValue);
        }
        return first;
    }
}
