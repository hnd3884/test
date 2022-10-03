package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import java.lang.reflect.Type;

final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type, Class> implements RuntimeNonElement
{
    static final RuntimeNonElement theInstance;
    
    private RuntimeAnyTypeImpl() {
        super(Utils.REFLECTION_NAVIGATOR);
    }
    
    @Override
    public <V> Transducer<V> getTransducer() {
        return null;
    }
    
    static {
        theInstance = new RuntimeAnyTypeImpl();
    }
}
