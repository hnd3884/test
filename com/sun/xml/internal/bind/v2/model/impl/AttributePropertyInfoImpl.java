package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;

class AttributePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends SingleTypePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements AttributePropertyInfo<TypeT, ClassDeclT>
{
    private final QName xmlName;
    private final boolean isRequired;
    
    AttributePropertyInfoImpl(final ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent, final PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> seed) {
        super(parent, seed);
        final XmlAttribute att = seed.readAnnotation(XmlAttribute.class);
        assert att != null;
        if (att.required()) {
            this.isRequired = true;
        }
        else {
            this.isRequired = this.nav().isPrimitive(this.getIndividualType());
        }
        this.xmlName = this.calcXmlName(att);
    }
    
    private QName calcXmlName(final XmlAttribute att) {
        String uri = att.namespace();
        String local = att.name();
        if (local.equals("##default")) {
            local = NameConverter.standard.toVariableName(this.getName());
        }
        if (uri.equals("##default")) {
            final XmlSchema xs = this.reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
            if (xs != null) {
                switch (xs.attributeFormDefault()) {
                    case QUALIFIED: {
                        uri = this.parent.getTypeName().getNamespaceURI();
                        if (uri.length() == 0) {
                            uri = this.parent.builder.defaultNsUri;
                            break;
                        }
                        break;
                    }
                    case UNQUALIFIED:
                    case UNSET: {
                        uri = "";
                        break;
                    }
                }
            }
            else {
                uri = "";
            }
        }
        return new QName(uri.intern(), local.intern());
    }
    
    @Override
    public boolean isRequired() {
        return this.isRequired;
    }
    
    @Override
    public final QName getXmlName() {
        return this.xmlName;
    }
    
    @Override
    public final PropertyKind kind() {
        return PropertyKind.ATTRIBUTE;
    }
}
