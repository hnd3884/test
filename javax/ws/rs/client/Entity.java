package javax.ws.rs.client;

import java.util.Arrays;
import java.util.Locale;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.lang.annotation.Annotation;

public final class Entity<T>
{
    private static final Annotation[] EMPTY_ANNOTATIONS;
    private final T entity;
    private final Variant variant;
    private final Annotation[] annotations;
    
    public static <T> Entity<T> entity(final T entity, final MediaType mediaType) {
        return new Entity<T>(entity, mediaType);
    }
    
    public static <T> Entity<T> entity(final T entity, final MediaType mediaType, final Annotation[] annotations) {
        return new Entity<T>(entity, mediaType, annotations);
    }
    
    public static <T> Entity<T> entity(final T entity, final String mediaType) {
        return new Entity<T>(entity, MediaType.valueOf(mediaType));
    }
    
    public static <T> Entity<T> entity(final T entity, final Variant variant) {
        return new Entity<T>(entity, variant);
    }
    
    public static <T> Entity<T> entity(final T entity, final Variant variant, final Annotation[] annotations) {
        return new Entity<T>(entity, variant, annotations);
    }
    
    public static <T> Entity<T> text(final T entity) {
        return new Entity<T>(entity, MediaType.TEXT_PLAIN_TYPE);
    }
    
    public static <T> Entity<T> xml(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XML_TYPE);
    }
    
    public static <T> Entity<T> json(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_JSON_TYPE);
    }
    
    public static <T> Entity<T> html(final T entity) {
        return new Entity<T>(entity, MediaType.TEXT_HTML_TYPE);
    }
    
    public static <T> Entity<T> xhtml(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XHTML_XML_TYPE);
    }
    
    public static Entity<Form> form(final Form form) {
        return new Entity<Form>(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }
    
    public static Entity<Form> form(final MultivaluedMap<String, String> formData) {
        return new Entity<Form>(new Form(formData), MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }
    
    private Entity(final T entity, final MediaType mediaType) {
        this(entity, new Variant(mediaType, (Locale)null, null), null);
    }
    
    private Entity(final T entity, final Variant variant) {
        this(entity, variant, null);
    }
    
    private Entity(final T entity, final MediaType mediaType, final Annotation[] annotations) {
        this(entity, new Variant(mediaType, (Locale)null, null), annotations);
    }
    
    private Entity(final T entity, final Variant variant, final Annotation[] annotations) {
        this.entity = entity;
        this.variant = variant;
        this.annotations = ((annotations == null) ? Entity.EMPTY_ANNOTATIONS : annotations);
    }
    
    public Variant getVariant() {
        return this.variant;
    }
    
    public MediaType getMediaType() {
        return this.variant.getMediaType();
    }
    
    public String getEncoding() {
        return this.variant.getEncoding();
    }
    
    public Locale getLanguage() {
        return this.variant.getLanguage();
    }
    
    public T getEntity() {
        return this.entity;
    }
    
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entity)) {
            return false;
        }
        final Entity entity1 = (Entity)o;
        if (!Arrays.equals(this.annotations, entity1.annotations)) {
            return false;
        }
        Label_0070: {
            if (this.entity != null) {
                if (this.entity.equals(entity1.entity)) {
                    break Label_0070;
                }
            }
            else if (entity1.entity == null) {
                break Label_0070;
            }
            return false;
        }
        if (this.variant != null) {
            if (this.variant.equals(entity1.variant)) {
                return true;
            }
        }
        else if (entity1.variant == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.entity != null) ? this.entity.hashCode() : 0;
        result = 31 * result + ((this.variant != null) ? this.variant.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.annotations);
        return result;
    }
    
    @Override
    public String toString() {
        return "Entity{entity=" + this.entity + ", variant=" + this.variant + ", annotations=" + Arrays.toString(this.annotations) + '}';
    }
    
    static {
        EMPTY_ANNOTATIONS = new Annotation[0];
    }
}
