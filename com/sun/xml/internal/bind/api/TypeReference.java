package com.sun.xml.internal.bind.api;

import java.util.Arrays;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public final class TypeReference
{
    public final QName tagName;
    public final Type type;
    public final Annotation[] annotations;
    
    public TypeReference(final QName tagName, final Type type, final Annotation... annotations) {
        if (tagName == null || type == null || annotations == null) {
            String nullArgs = "";
            if (tagName == null) {
                nullArgs = "tagName";
            }
            if (type == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", type" : "type");
            }
            if (annotations == null) {
                nullArgs += ((nullArgs.length() > 0) ? ", annotations" : "annotations");
            }
            Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs);
            throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs));
        }
        this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
        this.type = type;
        this.annotations = annotations;
    }
    
    public <A extends Annotation> A get(final Class<A> annotationType) {
        for (final Annotation a : this.annotations) {
            if (a.annotationType() == annotationType) {
                return annotationType.cast(a);
            }
        }
        return null;
    }
    
    public TypeReference toItemType() {
        final Type base = Utils.REFLECTION_NAVIGATOR.getBaseClass(this.type, Collection.class);
        if (base == null) {
            return this;
        }
        return new TypeReference(this.tagName, Utils.REFLECTION_NAVIGATOR.getTypeArgument(base, 0), new Annotation[0]);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TypeReference that = (TypeReference)o;
        return Arrays.equals(this.annotations, that.annotations) && this.tagName.equals(that.tagName) && this.type.equals(that.type);
    }
    
    @Override
    public int hashCode() {
        int result = this.tagName.hashCode();
        result = 31 * result + this.type.hashCode();
        result = 31 * result + Arrays.hashCode(this.annotations);
        return result;
    }
}
