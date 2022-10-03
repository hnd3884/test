package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;

class TypeRefImpl<TypeT, ClassDeclT> implements TypeRef<TypeT, ClassDeclT>
{
    private final QName elementName;
    private final TypeT type;
    protected final ElementPropertyInfoImpl<TypeT, ClassDeclT, ?, ?> owner;
    private NonElement<TypeT, ClassDeclT> ref;
    private final boolean isNillable;
    private String defaultValue;
    
    public TypeRefImpl(final ElementPropertyInfoImpl<TypeT, ClassDeclT, ?, ?> owner, final QName elementName, final TypeT type, final boolean isNillable, final String defaultValue) {
        this.owner = owner;
        this.elementName = elementName;
        this.type = type;
        this.isNillable = isNillable;
        this.defaultValue = defaultValue;
        assert owner != null;
        assert elementName != null;
        assert type != null;
    }
    
    @Override
    public NonElement<TypeT, ClassDeclT> getTarget() {
        if (this.ref == null) {
            this.calcRef();
        }
        return this.ref;
    }
    
    @Override
    public QName getTagName() {
        return this.elementName;
    }
    
    @Override
    public boolean isNillable() {
        return this.isNillable;
    }
    
    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    protected void link() {
        this.calcRef();
    }
    
    private void calcRef() {
        this.ref = this.owner.parent.builder.getTypeInfo(this.type, this.owner);
        assert this.ref != null;
    }
    
    @Override
    public PropertyInfo<TypeT, ClassDeclT> getSource() {
        return this.owner;
    }
}
