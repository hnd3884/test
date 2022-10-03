package org.apache.http.entity.mime;

import org.apache.http.HttpEntity;
import java.util.Collections;
import java.util.Collection;
import java.util.Random;
import org.apache.http.entity.mime.content.InputStreamBody;
import java.io.InputStream;
import org.apache.http.entity.mime.content.FileBody;
import java.io.File;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;
import org.apache.http.entity.mime.content.ContentBody;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.Charset;

public class MultipartEntityBuilder
{
    private static final char[] MULTIPART_CHARS;
    private static final String DEFAULT_SUBTYPE = "form-data";
    private String subType;
    private HttpMultipartMode mode;
    private String boundary;
    private Charset charset;
    private List<FormBodyPart> bodyParts;
    
    public static MultipartEntityBuilder create() {
        return new MultipartEntityBuilder();
    }
    
    MultipartEntityBuilder() {
        this.subType = "form-data";
        this.mode = HttpMultipartMode.STRICT;
        this.boundary = null;
        this.charset = null;
        this.bodyParts = null;
    }
    
    public MultipartEntityBuilder setMode(final HttpMultipartMode mode) {
        this.mode = mode;
        return this;
    }
    
    public MultipartEntityBuilder setLaxMode() {
        this.mode = HttpMultipartMode.BROWSER_COMPATIBLE;
        return this;
    }
    
    public MultipartEntityBuilder setStrictMode() {
        this.mode = HttpMultipartMode.STRICT;
        return this;
    }
    
    public MultipartEntityBuilder setBoundary(final String boundary) {
        this.boundary = boundary;
        return this;
    }
    
    public MultipartEntityBuilder setCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }
    
    MultipartEntityBuilder addPart(final FormBodyPart bodyPart) {
        if (bodyPart == null) {
            return this;
        }
        if (this.bodyParts == null) {
            this.bodyParts = new ArrayList<FormBodyPart>();
        }
        this.bodyParts.add(bodyPart);
        return this;
    }
    
    public MultipartEntityBuilder addPart(final String name, final ContentBody contentBody) {
        Args.notNull((Object)name, "Name");
        Args.notNull((Object)contentBody, "Content body");
        return this.addPart(new FormBodyPart(name, contentBody));
    }
    
    public MultipartEntityBuilder addTextBody(final String name, final String text, final ContentType contentType) {
        return this.addPart(name, new StringBody(text, contentType));
    }
    
    public MultipartEntityBuilder addTextBody(final String name, final String text) {
        return this.addTextBody(name, text, ContentType.DEFAULT_TEXT);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final byte[] b, final ContentType contentType, final String filename) {
        return this.addPart(name, new ByteArrayBody(b, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final byte[] b) {
        return this.addBinaryBody(name, b, ContentType.DEFAULT_BINARY, null);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final File file, final ContentType contentType, final String filename) {
        return this.addPart(name, new FileBody(file, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final File file) {
        return this.addBinaryBody(name, file, ContentType.DEFAULT_BINARY, null);
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final InputStream stream, final ContentType contentType, final String filename) {
        return this.addPart(name, new InputStreamBody(stream, contentType, filename));
    }
    
    public MultipartEntityBuilder addBinaryBody(final String name, final InputStream stream) {
        return this.addBinaryBody(name, stream, ContentType.DEFAULT_BINARY, null);
    }
    
    private String generateContentType(final String boundary, final Charset charset) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("multipart/form-data; boundary=");
        buffer.append(boundary);
        if (charset != null) {
            buffer.append("; charset=");
            buffer.append(charset.name());
        }
        return buffer.toString();
    }
    
    private String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        for (int count = rand.nextInt(11) + 30, i = 0; i < count; ++i) {
            buffer.append(MultipartEntityBuilder.MULTIPART_CHARS[rand.nextInt(MultipartEntityBuilder.MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
    
    MultipartFormEntity buildEntity() {
        final String st = (this.subType != null) ? this.subType : "form-data";
        final Charset cs = this.charset;
        final String b = (this.boundary != null) ? this.boundary : this.generateBoundary();
        final List<FormBodyPart> bps = (this.bodyParts != null) ? new ArrayList<FormBodyPart>(this.bodyParts) : Collections.emptyList();
        final HttpMultipartMode m = (this.mode != null) ? this.mode : HttpMultipartMode.STRICT;
        AbstractMultipartForm form = null;
        switch (m) {
            case BROWSER_COMPATIBLE: {
                form = new HttpBrowserCompatibleMultipart(st, cs, b, bps);
                break;
            }
            case RFC6532: {
                form = new HttpRFC6532Multipart(st, cs, b, bps);
                break;
            }
            default: {
                form = new HttpStrictMultipart(st, cs, b, bps);
                break;
            }
        }
        return new MultipartFormEntity(form, this.generateContentType(b, cs), form.getTotalLength());
    }
    
    public HttpEntity build() {
        return (HttpEntity)this.buildEntity();
    }
    
    static {
        MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }
}
