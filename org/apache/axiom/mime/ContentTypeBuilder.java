package org.apache.axiom.mime;

import java.util.Locale;
import java.text.ParseException;
import java.util.Map;
import java.util.LinkedHashMap;

public final class ContentTypeBuilder
{
    private MediaType mediaType;
    private final LinkedHashMap<String, String> parameters;
    
    public ContentTypeBuilder(final MediaType mediaType) {
        this.parameters = new LinkedHashMap<String, String>();
        this.mediaType = mediaType;
    }
    
    public ContentTypeBuilder(final ContentType type) {
        this(type.getMediaType());
        type.getParameters(this.parameters);
    }
    
    public ContentTypeBuilder(final String type) throws ParseException {
        this(new ContentType(type));
    }
    
    public MediaType getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    public String getParameter(final String name) {
        return this.parameters.get(name.toLowerCase(Locale.ENGLISH));
    }
    
    public void setParameter(final String name, final String value) {
        this.parameters.put(name.toLowerCase(Locale.ENGLISH), value);
    }
    
    public void clearParameters() {
        this.parameters.clear();
    }
    
    public ContentType build() {
        return new ContentType(this.mediaType, this.parameters);
    }
    
    @Override
    public String toString() {
        return this.build().toString();
    }
}
