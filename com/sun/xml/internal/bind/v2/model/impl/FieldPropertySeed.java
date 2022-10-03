package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;

class FieldPropertySeed<TypeT, ClassDeclT, FieldT, MethodT> implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT>
{
    protected final FieldT field;
    private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
    
    FieldPropertySeed(final ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> classInfo, final FieldT field) {
        this.parent = classInfo;
        this.field = field;
    }
    
    @Override
    public <A extends Annotation> A readAnnotation(final Class<A> a) {
        return this.parent.reader().getFieldAnnotation(a, this.field, this);
    }
    
    @Override
    public boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
        return this.parent.reader().hasFieldAnnotation(annotationType, this.field);
    }
    
    @Override
    public String getName() {
        return this.parent.nav().getFieldName(this.field);
    }
    
    @Override
    public TypeT getRawType() {
        return this.parent.nav().getFieldType(this.field);
    }
    
    @Override
    public Locatable getUpstream() {
        return this.parent;
    }
    
    @Override
    public Location getLocation() {
        return this.parent.nav().getFieldLocation(this.field);
    }
}
