package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import java.lang.reflect.Type;

final class RuntimeTypeRefImpl extends TypeRefImpl<Type, Class> implements RuntimeTypeRef
{
    public RuntimeTypeRefImpl(final RuntimeElementPropertyInfoImpl elementPropertyInfo, final QName elementName, final Type type, final boolean isNillable, final String defaultValue) {
        super((ElementPropertyInfoImpl<Type, Object, ?, ?>)elementPropertyInfo, elementName, type, isNillable, defaultValue);
    }
    
    @Override
    public RuntimeNonElement getTarget() {
        return (RuntimeNonElement)super.getTarget();
    }
    
    @Override
    public Transducer getTransducer() {
        return RuntimeModelBuilder.createTransducer(this);
    }
    
    @Override
    public RuntimePropertyInfo getSource() {
        return (RuntimePropertyInfo)this.owner;
    }
}
