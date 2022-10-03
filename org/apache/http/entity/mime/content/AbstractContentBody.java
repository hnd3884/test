package org.apache.http.entity.mime.content;

import java.nio.charset.Charset;
import org.apache.http.util.Args;
import org.apache.http.entity.ContentType;

public abstract class AbstractContentBody implements ContentBody
{
    private final ContentType contentType;
    
    public AbstractContentBody(final ContentType contentType) {
        Args.notNull((Object)contentType, "Content type");
        this.contentType = contentType;
    }
    
    @Deprecated
    public AbstractContentBody(final String mimeType) {
        this(ContentType.parse(mimeType));
    }
    
    public ContentType getContentType() {
        return this.contentType;
    }
    
    public String getMimeType() {
        return this.contentType.getMimeType();
    }
    
    public String getMediaType() {
        final String mimeType = this.contentType.getMimeType();
        final int i = mimeType.indexOf(47);
        if (i != -1) {
            return mimeType.substring(0, i);
        }
        return mimeType;
    }
    
    public String getSubType() {
        final String mimeType = this.contentType.getMimeType();
        final int i = mimeType.indexOf(47);
        if (i != -1) {
            return mimeType.substring(i + 1);
        }
        return null;
    }
    
    public String getCharset() {
        final Charset charset = this.contentType.getCharset();
        return (charset != null) ? charset.name() : null;
    }
}
