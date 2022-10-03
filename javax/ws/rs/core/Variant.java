package javax.ws.rs.core;

import java.util.List;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.StringWriter;
import java.util.Locale;

public class Variant
{
    private Locale language;
    private MediaType mediaType;
    private String encoding;
    
    public Variant(final MediaType mediaType, final String language, final String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = ((language == null) ? null : new Locale(language));
        this.mediaType = mediaType;
    }
    
    public Variant(final MediaType mediaType, final String language, final String country, final String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = ((language == null) ? null : new Locale(language, country));
        this.mediaType = mediaType;
    }
    
    public Variant(final MediaType mediaType, final String language, final String country, final String languageVariant, final String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = ((language == null) ? null : new Locale(language, country, languageVariant));
        this.mediaType = mediaType;
    }
    
    public Variant(final MediaType mediaType, final Locale language, final String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = language;
        this.mediaType = mediaType;
    }
    
    public Locale getLanguage() {
        return this.language;
    }
    
    public String getLanguageString() {
        return (this.language == null) ? null : this.language.toString();
    }
    
    public MediaType getMediaType() {
        return this.mediaType;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public static VariantListBuilder mediaTypes(final MediaType... mediaTypes) {
        final VariantListBuilder b = VariantListBuilder.newInstance();
        b.mediaTypes(mediaTypes);
        return b;
    }
    
    public static VariantListBuilder languages(final Locale... languages) {
        final VariantListBuilder b = VariantListBuilder.newInstance();
        b.languages(languages);
        return b;
    }
    
    public static VariantListBuilder encodings(final String... encodings) {
        final VariantListBuilder b = VariantListBuilder.newInstance();
        b.encodings(encodings);
        return b;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + ((this.language != null) ? this.language.hashCode() : 0);
        hash = 29 * hash + ((this.mediaType != null) ? this.mediaType.hashCode() : 0);
        hash = 29 * hash + ((this.encoding != null) ? this.encoding.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Variant other = (Variant)obj;
        return (this.language == other.language || (this.language != null && this.language.equals(other.language))) && (this.mediaType == other.mediaType || (this.mediaType != null && this.mediaType.equals(other.mediaType))) && (this.encoding == other.encoding || (this.encoding != null && this.encoding.equals(other.encoding)));
    }
    
    @Override
    public String toString() {
        final StringWriter w = new StringWriter();
        w.append("Variant[mediaType=");
        w.append((this.mediaType == null) ? "null" : this.mediaType.toString());
        w.append(", language=");
        w.append((this.language == null) ? "null" : this.language.toString());
        w.append(", encoding=");
        w.append((this.encoding == null) ? "null" : this.encoding);
        w.append("]");
        return w.toString();
    }
    
    public abstract static class VariantListBuilder
    {
        protected VariantListBuilder() {
        }
        
        public static VariantListBuilder newInstance() {
            return RuntimeDelegate.getInstance().createVariantListBuilder();
        }
        
        public abstract List<Variant> build();
        
        public abstract VariantListBuilder add();
        
        public abstract VariantListBuilder languages(final Locale... p0);
        
        public abstract VariantListBuilder encodings(final String... p0);
        
        public abstract VariantListBuilder mediaTypes(final MediaType... p0);
    }
}
