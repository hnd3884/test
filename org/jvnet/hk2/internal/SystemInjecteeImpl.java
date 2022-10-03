package org.jvnet.hk2.internal;

import java.util.Collection;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Unqualified;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.Injectee;

public class SystemInjecteeImpl implements Injectee
{
    private final Type requiredType;
    private final Set<Annotation> qualifiers;
    private final int position;
    private final Class<?> pClass;
    private final AnnotatedElement parent;
    private final boolean isOptional;
    private final boolean isSelf;
    private final Unqualified unqualified;
    private ActiveDescriptor<?> injecteeDescriptor;
    private final Object parentIdentifier;
    
    SystemInjecteeImpl(final Type requiredType, final Set<Annotation> qualifiers, final int position, final AnnotatedElement parent, final boolean isOptional, final boolean isSelf, final Unqualified unqualified, final ActiveDescriptor<?> injecteeDescriptor) {
        this.requiredType = requiredType;
        this.position = position;
        this.parent = parent;
        this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)qualifiers);
        this.isOptional = isOptional;
        this.isSelf = isSelf;
        this.unqualified = unqualified;
        this.injecteeDescriptor = injecteeDescriptor;
        if (parent instanceof Field) {
            this.pClass = ((Field)parent).getDeclaringClass();
            this.parentIdentifier = ((Field)parent).getName();
        }
        else if (parent instanceof Constructor) {
            this.pClass = ((Constructor)parent).getDeclaringClass();
            this.parentIdentifier = this.pClass;
        }
        else {
            this.pClass = ((Method)parent).getDeclaringClass();
            this.parentIdentifier = ReflectionHelper.createMethodWrapper((Method)parent);
        }
    }
    
    public Type getRequiredType() {
        if (this.requiredType instanceof TypeVariable && this.injecteeDescriptor != null && this.injecteeDescriptor.getImplementationType() != null && this.injecteeDescriptor.getImplementationType() instanceof ParameterizedType) {
            final TypeVariable<?> tv = (TypeVariable<?>)this.requiredType;
            final ParameterizedType pt = (ParameterizedType)this.injecteeDescriptor.getImplementationType();
            final Type translatedRequiredType = ReflectionHelper.resolveKnownType((TypeVariable)tv, pt, (Class)this.pClass);
            if (translatedRequiredType != null) {
                return translatedRequiredType;
            }
        }
        return this.requiredType;
    }
    
    public Set<Annotation> getRequiredQualifiers() {
        return this.qualifiers;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public Class<?> getInjecteeClass() {
        return this.pClass;
    }
    
    public AnnotatedElement getParent() {
        return this.parent;
    }
    
    public boolean isOptional() {
        return this.isOptional;
    }
    
    public boolean isSelf() {
        return this.isSelf;
    }
    
    public Unqualified getUnqualified() {
        return this.unqualified;
    }
    
    public ActiveDescriptor<?> getInjecteeDescriptor() {
        return this.injecteeDescriptor;
    }
    
    void resetInjecteeDescriptor(final ActiveDescriptor<?> injecteeDescriptor) {
        this.injecteeDescriptor = injecteeDescriptor;
    }
    
    @Override
    public int hashCode() {
        return this.position ^ this.parentIdentifier.hashCode() ^ this.pClass.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SystemInjecteeImpl)) {
            return false;
        }
        final SystemInjecteeImpl other = (SystemInjecteeImpl)o;
        return this.position == other.getPosition() && this.pClass.equals(other.getInjecteeClass()) && this.parentIdentifier.equals(other.parentIdentifier);
    }
    
    @Override
    public String toString() {
        return "SystemInjecteeImpl(requiredType=" + Pretty.type(this.requiredType) + ",parent=" + Pretty.clazz((Class)this.pClass) + ",qualifiers=" + Pretty.collection((Collection)this.qualifiers) + ",position=" + this.position + ",optional=" + this.isOptional + ",self=" + this.isSelf + ",unqualified=" + this.unqualified + "," + System.identityHashCode(this) + ")";
    }
}
