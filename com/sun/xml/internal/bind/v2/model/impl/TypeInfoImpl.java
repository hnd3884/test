package com.sun.xml.internal.bind.v2.model.impl;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;

abstract class TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements TypeInfo<TypeT, ClassDeclT>, Locatable
{
    private final Locatable upstream;
    protected final TypeInfoSetImpl<TypeT, ClassDeclT, FieldT, MethodT> owner;
    protected ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> builder;
    
    protected TypeInfoImpl(final ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> builder, final Locatable upstream) {
        this.builder = builder;
        this.owner = builder.typeInfoSet;
        this.upstream = upstream;
    }
    
    @Override
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    void link() {
        this.builder = null;
    }
    
    protected final Navigator<TypeT, ClassDeclT, FieldT, MethodT> nav() {
        return this.owner.nav;
    }
    
    protected final AnnotationReader<TypeT, ClassDeclT, FieldT, MethodT> reader() {
        return this.owner.reader;
    }
    
    protected final QName parseElementName(final ClassDeclT clazz) {
        final XmlRootElement e = this.reader().getClassAnnotation(XmlRootElement.class, clazz, this);
        if (e == null) {
            return null;
        }
        String local = e.name();
        if (local.equals("##default")) {
            local = NameConverter.standard.toVariableName(this.nav().getClassShortName(clazz));
        }
        String nsUri = e.namespace();
        if (nsUri.equals("##default")) {
            final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, clazz, this);
            if (xs != null) {
                nsUri = xs.namespace();
            }
            else {
                nsUri = this.builder.defaultNsUri;
            }
        }
        return new QName(nsUri.intern(), local.intern());
    }
    
    protected final QName parseTypeName(final ClassDeclT clazz) {
        return this.parseTypeName(clazz, this.reader().getClassAnnotation(XmlType.class, clazz, this));
    }
    
    protected final QName parseTypeName(final ClassDeclT clazz, final XmlType t) {
        String nsUri = "##default";
        String local = "##default";
        if (t != null) {
            nsUri = t.namespace();
            local = t.name();
        }
        if (local.length() == 0) {
            return null;
        }
        if (local.equals("##default")) {
            local = NameConverter.standard.toVariableName(this.nav().getClassShortName(clazz));
        }
        if (nsUri.equals("##default")) {
            final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, clazz, this);
            if (xs != null) {
                nsUri = xs.namespace();
            }
            else {
                nsUri = this.builder.defaultNsUri;
            }
        }
        return new QName(nsUri.intern(), local.intern());
    }
}
