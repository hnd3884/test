package org.glassfish.hk2.utilities;

import java.util.Collection;
import org.glassfish.hk2.utilities.reflection.Pretty;
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

public class InjecteeImpl implements Injectee
{
    private Type requiredType;
    private Set<Annotation> qualifiers;
    private int position;
    private Class<?> pClass;
    private AnnotatedElement parent;
    private boolean isOptional;
    private boolean isSelf;
    private Unqualified unqualified;
    private ActiveDescriptor<?> injecteeDescriptor;
    
    public InjecteeImpl() {
        this.position = -1;
        this.isOptional = false;
        this.isSelf = false;
        this.unqualified = null;
    }
    
    public InjecteeImpl(final Type requiredType) {
        this.position = -1;
        this.isOptional = false;
        this.isSelf = false;
        this.unqualified = null;
        this.requiredType = requiredType;
    }
    
    public InjecteeImpl(final Injectee copyMe) {
        this.position = -1;
        this.isOptional = false;
        this.isSelf = false;
        this.unqualified = null;
        this.requiredType = copyMe.getRequiredType();
        this.position = copyMe.getPosition();
        this.parent = copyMe.getParent();
        this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)copyMe.getRequiredQualifiers());
        this.isOptional = copyMe.isOptional();
        this.isSelf = copyMe.isSelf();
        this.injecteeDescriptor = copyMe.getInjecteeDescriptor();
        if (this.parent == null) {
            this.pClass = null;
        }
        else if (this.parent instanceof Field) {
            this.pClass = ((Field)this.parent).getDeclaringClass();
        }
        else if (this.parent instanceof Constructor) {
            this.pClass = ((Constructor)this.parent).getDeclaringClass();
        }
        else if (this.parent instanceof Method) {
            this.pClass = ((Method)this.parent).getDeclaringClass();
        }
    }
    
    @Override
    public Type getRequiredType() {
        return this.requiredType;
    }
    
    public void setRequiredType(final Type requiredType) {
        this.requiredType = requiredType;
    }
    
    @Override
    public Set<Annotation> getRequiredQualifiers() {
        if (this.qualifiers == null) {
            return Collections.emptySet();
        }
        return this.qualifiers;
    }
    
    public void setRequiredQualifiers(final Set<Annotation> requiredQualifiers) {
        this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)requiredQualifiers);
    }
    
    @Override
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    @Override
    public Class<?> getInjecteeClass() {
        return this.pClass;
    }
    
    @Override
    public AnnotatedElement getParent() {
        return this.parent;
    }
    
    public void setParent(final AnnotatedElement parent) {
        this.parent = parent;
        if (parent instanceof Field) {
            this.pClass = ((Field)parent).getDeclaringClass();
        }
        else if (parent instanceof Constructor) {
            this.pClass = ((Constructor)parent).getDeclaringClass();
        }
        else if (parent instanceof Method) {
            this.pClass = ((Method)parent).getDeclaringClass();
        }
    }
    
    @Override
    public boolean isOptional() {
        return this.isOptional;
    }
    
    public void setOptional(final boolean optional) {
        this.isOptional = optional;
    }
    
    @Override
    public boolean isSelf() {
        return this.isSelf;
    }
    
    public void setSelf(final boolean self) {
        this.isSelf = self;
    }
    
    @Override
    public Unqualified getUnqualified() {
        return this.unqualified;
    }
    
    public void setUnqualified(final Unqualified unqualified) {
        this.unqualified = unqualified;
    }
    
    @Override
    public ActiveDescriptor<?> getInjecteeDescriptor() {
        return this.injecteeDescriptor;
    }
    
    public void setInjecteeDescriptor(final ActiveDescriptor<?> injecteeDescriptor) {
        this.injecteeDescriptor = injecteeDescriptor;
    }
    
    @Override
    public String toString() {
        return "InjecteeImpl(requiredType=" + Pretty.type(this.requiredType) + ",parent=" + Pretty.clazz((Class)this.pClass) + ",qualifiers=" + Pretty.collection((Collection)this.qualifiers) + ",position=" + this.position + ",optional=" + this.isOptional + ",self=" + this.isSelf + ",unqualified=" + this.unqualified + "," + System.identityHashCode(this) + ")";
    }
}
