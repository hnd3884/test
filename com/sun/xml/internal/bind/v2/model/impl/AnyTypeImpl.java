package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.core.NonElement;

class AnyTypeImpl<T, C> implements NonElement<T, C>
{
    private final T type;
    private final Navigator<T, C, ?, ?> nav;
    
    public AnyTypeImpl(final Navigator<T, C, ?, ?> nav) {
        this.type = nav.ref(Object.class);
        this.nav = nav;
    }
    
    @Override
    public QName getTypeName() {
        return AnyTypeImpl.ANYTYPE_NAME;
    }
    
    @Override
    public T getType() {
        return this.type;
    }
    
    @Override
    public Locatable getUpstream() {
        return null;
    }
    
    @Override
    public boolean isSimpleType() {
        return false;
    }
    
    @Override
    public Location getLocation() {
        return this.nav.getClassLocation(this.nav.asDecl(Object.class));
    }
    
    @Override
    @Deprecated
    public final boolean canBeReferencedByIDREF() {
        return true;
    }
}
