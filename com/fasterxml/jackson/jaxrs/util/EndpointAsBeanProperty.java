package com.fasterxml.jackson.jaxrs.util;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.BeanProperty;

public class EndpointAsBeanProperty extends BeanProperty.Std
{
    private static final long serialVersionUID = 1L;
    public static final PropertyName ENDPOINT_NAME;
    private static final AnnotationMap NO_ANNOTATIONS;
    protected transient Annotation[] _rawAnnotations;
    public AnnotationMap _annotations;
    
    public EndpointAsBeanProperty(final PropertyName name, final JavaType type, final Annotation[] annotations) {
        super(name, type, (PropertyName)null, (AnnotatedMember)null, PropertyMetadata.STD_OPTIONAL);
        this._rawAnnotations = annotations;
        this._annotations = null;
    }
    
    protected EndpointAsBeanProperty(final EndpointAsBeanProperty base, final JavaType newType) {
        super((BeanProperty.Std)base, newType);
        this._rawAnnotations = base._rawAnnotations;
        this._annotations = base._annotations;
    }
    
    public BeanProperty.Std withType(final JavaType type) {
        if (this._type == type) {
            return this;
        }
        return new BeanProperty.Std(this._name, type, this._wrapperName, this._member, this._metadata);
    }
    
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return (A)this.annotations().get((Class)acls);
    }
    
    protected AnnotationMap annotations() {
        AnnotationMap am = this._annotations;
        if (am == null) {
            final Annotation[] raw = this._rawAnnotations;
            if (raw == null || raw.length == 0) {
                am = EndpointAsBeanProperty.NO_ANNOTATIONS;
            }
            else {
                am = new AnnotationMap();
                for (final Annotation a : raw) {
                    am.add(a);
                }
            }
            this._annotations = am;
        }
        return am;
    }
    
    static {
        ENDPOINT_NAME = new PropertyName("JAX-RS/endpoint");
        NO_ANNOTATIONS = new AnnotationMap();
    }
}
