package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;

public class ArrayInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ArrayInfo<TypeT, ClassDeclT>, Location
{
    private final NonElement<TypeT, ClassDeclT> itemType;
    private final QName typeName;
    private final TypeT arrayType;
    
    public ArrayInfoImpl(final ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> builder, final Locatable upstream, final TypeT arrayType) {
        super(builder, upstream);
        this.arrayType = arrayType;
        final TypeT componentType = this.nav().getComponentType(arrayType);
        this.itemType = builder.getTypeInfo(componentType, this);
        QName n = this.itemType.getTypeName();
        if (n == null) {
            builder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(this.nav().getTypeName(componentType)), this));
            n = new QName("#dummy");
        }
        this.typeName = ArrayInfoUtil.calcArrayTypeName(n);
    }
    
    @Override
    public NonElement<TypeT, ClassDeclT> getItemType() {
        return this.itemType;
    }
    
    @Override
    public QName getTypeName() {
        return this.typeName;
    }
    
    @Override
    public boolean isSimpleType() {
        return false;
    }
    
    @Override
    public TypeT getType() {
        return this.arrayType;
    }
    
    @Override
    @Deprecated
    public final boolean canBeReferencedByIDREF() {
        return false;
    }
    
    @Override
    public Location getLocation() {
        return this;
    }
    
    @Override
    public String toString() {
        return this.nav().getTypeName(this.arrayType);
    }
}
