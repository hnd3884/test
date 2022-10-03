package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.lang.annotation.Annotation;

public final class AnnotationBundleKey
{
    private static final Annotation[] NO_ANNOTATIONS;
    private final Annotation[] _annotations;
    private final Class<?> _type;
    private final boolean _annotationsCopied;
    private final int _hashCode;
    
    public AnnotationBundleKey(Annotation[] annotations, final Class<?> type) {
        this._type = type;
        final int typeHash = type.getName().hashCode();
        if (annotations == null || annotations.length == 0) {
            annotations = AnnotationBundleKey.NO_ANNOTATIONS;
            this._annotationsCopied = true;
            this._hashCode = typeHash;
        }
        else {
            this._annotationsCopied = false;
            this._hashCode = (calcHash(annotations) ^ typeHash);
        }
        this._annotations = annotations;
    }
    
    private AnnotationBundleKey(final Annotation[] annotations, final Class<?> type, final int hashCode) {
        this._annotations = annotations;
        this._annotationsCopied = true;
        this._type = type;
        this._hashCode = hashCode;
    }
    
    private static final int calcHash(final Annotation[] annotations) {
        int hash;
        for (int len = hash = annotations.length, i = 0; i < len; ++i) {
            hash = hash * 31 + annotations[i].hashCode();
        }
        return hash;
    }
    
    public AnnotationBundleKey immutableKey() {
        if (this._annotationsCopied) {
            return this;
        }
        final int len = this._annotations.length;
        final Annotation[] newAnnotations = new Annotation[len];
        System.arraycopy(this._annotations, 0, newAnnotations, 0, len);
        return new AnnotationBundleKey(newAnnotations, this._type, this._hashCode);
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public String toString() {
        return "[Annotations: " + this._annotations.length + ", type: " + this._type.getName() + ", hash 0x" + Integer.toHexString(this._hashCode) + ", copied: " + this._annotationsCopied + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final AnnotationBundleKey other = (AnnotationBundleKey)o;
        return other._hashCode == this._hashCode && other._type == this._type && this._equals(other._annotations);
    }
    
    private final boolean _equals(final Annotation[] otherAnn) {
        final int len = this._annotations.length;
        if (otherAnn.length != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (this._annotations[i] != otherAnn[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        NO_ANNOTATIONS = new Annotation[0];
    }
}
