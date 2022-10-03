package com.sun.xml.internal.bind.v2.model.impl;

import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import javax.xml.namespace.QName;
import java.util.Collections;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import com.sun.istack.internal.FinalArrayList;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlList;
import java.util.AbstractList;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;

class ElementPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ElementPropertyInfo<TypeT, ClassDeclT>
{
    private List<TypeRefImpl<TypeT, ClassDeclT>> types;
    private final List<TypeInfo<TypeT, ClassDeclT>> ref;
    private Boolean isRequired;
    private final boolean isValueList;
    
    ElementPropertyInfoImpl(final ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent, final PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> propertySeed) {
        super(parent, propertySeed);
        this.ref = new AbstractList<TypeInfo<TypeT, ClassDeclT>>() {
            @Override
            public TypeInfo<TypeT, ClassDeclT> get(final int index) {
                return ((TypeRefImpl)ElementPropertyInfoImpl.this.getTypes().get(index)).getTarget();
            }
            
            @Override
            public int size() {
                return ElementPropertyInfoImpl.this.getTypes().size();
            }
        };
        this.isValueList = this.seed.hasAnnotation(XmlList.class);
    }
    
    @Override
    public List<? extends TypeRefImpl<TypeT, ClassDeclT>> getTypes() {
        if (this.types == null) {
            this.types = new FinalArrayList<TypeRefImpl<TypeT, ClassDeclT>>();
            XmlElement[] ann = null;
            final XmlElement xe = this.seed.readAnnotation(XmlElement.class);
            final XmlElements xes = this.seed.readAnnotation(XmlElements.class);
            if (xe != null && xes != null) {
                this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(this.nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), xe.annotationType().getName(), xes.annotationType().getName()), xe, xes));
            }
            this.isRequired = true;
            if (xe != null) {
                ann = new XmlElement[] { xe };
            }
            else if (xes != null) {
                ann = xes.value();
            }
            if (ann == null) {
                final TypeT t = this.getIndividualType();
                if (!this.nav().isPrimitive(t) || this.isCollection()) {
                    this.isRequired = false;
                }
                this.types.add(this.createTypeRef(this.calcXmlName((XmlElement)null), t, this.isCollection(), null));
            }
            else {
                for (final XmlElement item : ann) {
                    final QName name = this.calcXmlName(item);
                    TypeT type = this.reader().getClassValue(item, "type");
                    if (this.nav().isSameType(type, this.nav().ref(XmlElement.DEFAULT.class))) {
                        type = this.getIndividualType();
                    }
                    if ((!this.nav().isPrimitive(type) || this.isCollection()) && !item.required()) {
                        this.isRequired = false;
                    }
                    this.types.add(this.createTypeRef(name, type, item.nillable(), this.getDefaultValue(item.defaultValue())));
                }
            }
            this.types = Collections.unmodifiableList((List<? extends TypeRefImpl<TypeT, ClassDeclT>>)this.types);
            assert !this.types.contains(null);
        }
        return this.types;
    }
    
    private String getDefaultValue(final String value) {
        if (value.equals("\u0000")) {
            return null;
        }
        return value;
    }
    
    protected TypeRefImpl<TypeT, ClassDeclT> createTypeRef(final QName name, final TypeT type, final boolean isNillable, final String defaultValue) {
        return new TypeRefImpl<TypeT, ClassDeclT>(this, name, type, isNillable, defaultValue);
    }
    
    @Override
    public boolean isValueList() {
        return this.isValueList;
    }
    
    @Override
    public boolean isRequired() {
        if (this.isRequired == null) {
            this.getTypes();
        }
        return this.isRequired;
    }
    
    @Override
    public List<? extends TypeInfo<TypeT, ClassDeclT>> ref() {
        return this.ref;
    }
    
    @Override
    public final PropertyKind kind() {
        return PropertyKind.ELEMENT;
    }
    
    @Override
    protected void link() {
        super.link();
        for (final TypeRefImpl<TypeT, ClassDeclT> ref : this.getTypes()) {
            ref.link();
        }
        if (this.isValueList()) {
            if (this.id() != ID.IDREF) {
                for (final TypeRefImpl<TypeT, ClassDeclT> ref : this.types) {
                    if (!ref.getTarget().isSimpleType()) {
                        this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_NEEDS_SIMPLETYPE.format(this.nav().getTypeName(ref.getTarget().getType())), this));
                        break;
                    }
                }
            }
            if (!this.isCollection()) {
                this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(new Object[0]), this));
            }
        }
    }
}
