package org.apache.http.entity.mime.content;

import java.io.IOException;
import org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringBody extends AbstractContentBody
{
    private final byte[] content;
    
    @Deprecated
    public static StringBody create(final String text, final String mimeType, final Charset charset) throws IllegalArgumentException {
        try {
            return new StringBody(text, mimeType, charset);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }
    
    @Deprecated
    public static StringBody create(final String text, final Charset charset) throws IllegalArgumentException {
        return create(text, null, charset);
    }
    
    @Deprecated
    public static StringBody create(final String text) throws IllegalArgumentException {
        return create(text, null, null);
    }
    
    @Deprecated
    public StringBody(final String text, final String mimeType, final Charset charset) throws UnsupportedEncodingException {
        this(text, ContentType.create(mimeType, charset));
    }
    
    @Deprecated
    public StringBody(final String text, final Charset charset) throws UnsupportedEncodingException {
        this(text, "text/plain", charset);
    }
    
    @Deprecated
    public StringBody(final String text) throws UnsupportedEncodingException {
        this(text, "text/plain", Consts.ASCII);
    }
    
    public StringBody(final String text, final ContentType contentType) {
        super(contentType);
        final Charset charset = contentType.getCharset();
        final String csname = (charset != null) ? charset.name() : Consts.ASCII.name();
        try {
            this.content = text.getBytes(csname);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new UnsupportedCharsetException(csname);
        }
    }
    
    public Reader getReader() {
        final Charset charset = this.getContentType().getCharset();
        return new InputStreamReader(new ByteArrayInputStream(this.content), (charset != null) ? charset : Consts.ASCII);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull((Object)out, "Output stream");
        final InputStream in = new ByteArrayInputStream(this.content);
        final byte[] tmp = new byte[4096];
        int l;
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }
        out.flush();
    }
    
    public String getTransferEncoding() {
        return "8bit";
    }
    
    public long getContentLength() {
        return this.content.length;
    }
    
    public String getFilename() {
        return null;
    }
}
