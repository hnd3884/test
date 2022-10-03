package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;

abstract class LeafInfoImpl<TypeT, ClassDeclT> implements LeafInfo<TypeT, ClassDeclT>, Location
{
    private final TypeT type;
    private final QName typeName;
    
    protected LeafInfoImpl(final TypeT type, final QName typeName) {
        assert type != null;
        this.type = type;
        this.typeName = typeName;
    }
    
    @Override
    public TypeT getType() {
        return this.type;
    }
    
    @Override
    @Deprecated
    public final boolean canBeReferencedByIDREF() {
        return false;
    }
    
    @Override
    public QName getTypeName() {
        return this.typeName;
    }
    
    @Override
    public Locatable getUpstream() {
        return null;
    }
    
    @Override
    public Location getLocation() {
        return this;
    }
    
    @Override
    public boolean isSimpleType() {
        return true;
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
}
